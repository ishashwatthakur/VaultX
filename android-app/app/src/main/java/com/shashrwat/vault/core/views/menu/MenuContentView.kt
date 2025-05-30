package com.shashrwat.vault.core.views.menu

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.shashrwat.vault.R
import com.shashrwat.vault.core.extensions.Paint
import com.shashrwat.vault.features.common.Durations
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Dimens
import com.shashrwat.vault.viewbuilding.TextSizes
import viewdsl.AccelerateDecelerateInterpolator
import viewdsl.cancelIfRunning
import viewdsl.children
import viewdsl.contains
import viewdsl.doOnEnd
import viewdsl.dp
import viewdsl.exactly
import viewdsl.isOrientationPortrait
import viewdsl.layoutLeftTop
import kotlin.math.abs
import kotlin.math.hypot

class MenuContentView(context: Context) : ViewGroup(context) {
  
  private val crossBaseSize = Dimens.CircleButtonSize
  private val crossOpenedSize = 48.dp
  private val crossBasePadding = 16.dp
  private val crossOpenedPadding = 12.dp
  private val itemSize = 60.dp
  private val itemsHorizontalPadding = 12.dp
  private val itemsVerticalPadding = 8.dp
  private val pTop = 20.dp
  private val pStart = 20.dp
  private val pEnd = 12.dp
  private val pBottom = 12.dp
  private val textSize = TextSizes.H5
  private val backgroundPaint = Paint(Colors.Dialog)
  private var latestY = 0f
  private var latestX = 0f
  private var wasDownEventInOpenCloseView = false
  private var maxItemWidth = 0
  private var maxItemHeight = 0
  private var animCoefficient = 0f
  private val path = Path()
  
  private val coefficientAnimator = ValueAnimator().apply {
    duration = Durations.MenuOpening
    interpolator = AccelerateDecelerateInterpolator
    addUpdateListener {
      animCoefficient = it.animatedValue as Float
      onAnimating(animCoefficient)
      invalidate()
    }
  }
  
  private val openCloseView get() = getChildAt(0) as AnimatableCircleIconView
  private val firstMenuItem get() = getChildAt(1) as MenuItemView
  private val secondMenuItem get() = getChildAt(2) as MenuItemView
  private val thirdMenuItem get() = getChildAt(3) as MenuItemView
  private val fourthMenuItem get() = getChildAt(4) as MenuItemView
  
  var opened = false
    private set
  
  var onMenuOpenClick: () -> Unit = {}
  var onMenuCloseClick: () -> Unit = {}
  var onAnimating: (fraction: Float) -> Unit = {}
  
  var onMenuOpened: () -> Unit = {}
  var onMenuClosed: () -> Unit = {}
  
  init {
    val iconSize = (crossBaseSize * 0.65f).toInt()
    addView(
      AnimatableCircleIconView(
        context = context,
        size = iconSize,
        circleColor = Colors.Accent,
        drawableRes1 = R.drawable.avd_menu_to_cross,
        drawableRes2 = R.drawable.avd_cross_to_menu,
        iconColor = Colors.Icon
      )
    )
  }
  
  fun openMenu(animate: Boolean = true) {
    if (opened) return
    opened = true
    openCloseView.switchToFirstDrawable(animate)
    if (animate) {
      coefficientAnimator.cancelIfRunning()
      coefficientAnimator.setFloatValues(animCoefficient, 1f)
      coefficientAnimator.doOnEnd(onMenuOpened)
      coefficientAnimator.start()
    } else {
      animCoefficient = 1f
      onMenuOpened()
      invalidate()
    }
  }
  
  fun closeMenu(animate: Boolean = true) {
    if (!opened) return
    opened = false
    openCloseView.switchToSecondDrawable(animate)
    if (animate) {
      coefficientAnimator.cancelIfRunning()
      coefficientAnimator.setFloatValues(animCoefficient, 0f)
      coefficientAnimator.doOnEnd(onMenuClosed)
      coefficientAnimator.start()
    } else {
      animCoefficient = 0f
      onMenuClosed()
      invalidate()
    }
  }
  
  fun addItems(vararg items: MenuItemModel) {
    assert(items.size == 4)
    val menuItemView: (MenuItemModel) -> MenuItemView = { menuItem ->
      MenuItemView(
        context = context,
        iconRes = menuItem.iconRes,
        text = context.getString(menuItem.titleRes),
        textSize = textSize,
        circleSize = itemSize,
      ).apply { setOnClickListener { menuItem.onClick() } }
    }
    addView(menuItemView(items[0]))
    addView(menuItemView(items[1]))
    addView(menuItemView(items[2]))
    addView(menuItemView(items[3]))
  }
  
  fun isEventOutsideOfContent(event: MotionEvent): Boolean {
    val x = event.x - left
    val y = event.y - top
    val insideOfOpenCloseView =
        x >= openCloseView.left + openCloseView.translationX
            && y >= openCloseView.top + openCloseView.translationY
            && x <= openCloseView.right + openCloseView.translationX
            && y <= openCloseView.bottom + openCloseView.translationY
    val insideOfContentRectangle = opened && x > 0 && y > (crossOpenedSize + crossOpenedPadding)
    return !insideOfOpenCloseView && !insideOfContentRectangle
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    openCloseView.measure(exactly(crossBaseSize), exactly(crossBaseSize))
    children.forEach { child ->
      if (child is MenuItemView) {
        child.measure(widthMeasureSpec, heightMeasureSpec)
        maxItemWidth = maxOf(maxItemWidth, child.measuredWidth)
        maxItemHeight = maxOf(maxItemHeight, child.measuredHeight)
      }
    }
    if (isOrientationPortrait) {
      measurePortrait(widthMeasureSpec, heightMeasureSpec)
    } else {
      measureLandscape(widthMeasureSpec, heightMeasureSpec)
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val topOffset = (crossOpenedSize + crossOpenedPadding).toFloat()
    val width = w.toFloat()
    val height = h.toFloat()
    val curveOffset = minOf(width, height) / 5f
    with(path) {
      moveTo(width, topOffset)
      lineTo(curveOffset, topOffset)
      quadTo(0f, topOffset, 0f, curveOffset + topOffset)
      lineTo(0f, height)
      lineTo(width, height)
      close()
    }
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    if (isOrientationPortrait) {
      layoutPortrait()
    } else {
      layoutLandscape()
    }
    val left = width - crossBaseSize - crossBasePadding
    val top = height - crossBaseSize - crossBasePadding
    openCloseView.layoutLeftTop(left, top)
  }
  
  override fun dispatchDraw(canvas: Canvas) {
    val topOffset = crossOpenedSize + crossOpenedPadding
    val pathHeight = height - topOffset
    drawPath(canvas, pathHeight)
    forEachMenuItem {
      alpha = animCoefficient
      isClickable = alpha == 1f
      translationX = (1f - animCoefficient) * width / 3f
      translationY = (1f - animCoefficient) * pathHeight / 3f
    }
    val openCloseViewRange = height - crossBaseSize - crossBasePadding + (crossBaseSize - crossOpenedSize) / 2
    openCloseView.translationY = animCoefficient * -openCloseViewRange
    val endScale = crossOpenedSize.toFloat() / crossBaseSize.toFloat()
    val scale = 1f - animCoefficient * (1f - endScale)
    openCloseView.scaleX = scale
    openCloseView.scaleY = scale
    super.dispatchDraw(canvas)
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!opened && event !in openCloseView) {
      return false
    }
    when (event.action) {
      ACTION_DOWN -> {
        wasDownEventInOpenCloseView = event in openCloseView
        latestX = event.x
        latestY = event.y
        return true
      }
      
      ACTION_UP -> {
        val dx = abs(event.x - latestX)
        val dy = abs(event.y - latestY)
        val dst = hypot(dx, dy)
        val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        if (wasDownEventInOpenCloseView && dst < scaledTouchSlop) {
          if (opened) onMenuCloseClick() else onMenuOpenClick()
        }
      }
    }
    return super.onTouchEvent(event)
  }
  
  private fun measurePortrait(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = pStart + pEnd +
        maxItemWidth * 2 + itemsHorizontalPadding
    val height = pTop + pBottom +
        crossOpenedSize + crossOpenedPadding + maxItemHeight * 2 + itemsVerticalPadding
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(height, heightMeasureSpec),
    )
  }
  
  private fun measureLandscape(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = pStart + pEnd +
        maxItemWidth * 4 + itemsHorizontalPadding * 3
    val height = pTop + pBottom +
        crossOpenedSize + crossOpenedPadding + maxItemHeight
    setMeasuredDimension(
      resolveSize(width, widthMeasureSpec),
      resolveSize(height, heightMeasureSpec),
    )
  }
  
  private fun layoutPortrait() {
    val topOffset = crossOpenedSize + crossOpenedPadding
    firstMenuItem.layout(
      pStart,
      topOffset + pTop,
      pStart + maxItemWidth,
      topOffset + pTop + firstMenuItem.measuredHeight
    )
    secondMenuItem.layout(
      width - pEnd - maxItemWidth,
      topOffset + pTop,
      width - pEnd,
      topOffset + pTop + secondMenuItem.measuredHeight
    )
    thirdMenuItem.layout(
      pStart,
      height - pBottom - maxItemHeight,
      pStart + maxItemWidth,
      height - pBottom - maxItemHeight + thirdMenuItem.measuredHeight
    )
    fourthMenuItem.layout(
      width - pEnd - maxItemWidth,
      height - pBottom - maxItemHeight,
      width - pEnd,
      height - pBottom - maxItemHeight + fourthMenuItem.measuredHeight
    )
  }
  
  private fun layoutLandscape() {
    val topOffset = crossOpenedSize + crossOpenedPadding
    var left = pStart
    children.forEach { child ->
      if (child is MenuItemView) {
        child.layout(left, topOffset + pTop, left + maxItemWidth,
          topOffset + pTop + child.measuredHeight)
        left += maxItemWidth + itemsHorizontalPadding
      }
    }
  }
  
  private fun drawPath(canvas: Canvas, pathHeight: Int) {
    canvas.save()
    canvas.translate(
      (1f - animCoefficient) * width,
      (1f - animCoefficient) * pathHeight
    )
    canvas.drawPath(path, backgroundPaint)
    canvas.restore()
  }
  
  private fun forEachMenuItem(block: View.() -> Unit) {
    firstMenuItem.apply(block)
    secondMenuItem.apply(block)
    thirdMenuItem.apply(block)
    fourthMenuItem.apply(block)
  }
}
