package com.shashrwat.vault.core.views.drawables

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.shashrwat.vault.core.extensions.Paint
import com.shashrwat.vault.core.extensions.TextPaint
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Fonts

class LetterInCircleDrawable(
  private var letter: String,
  textColor: Int = Colors.AccentHeavy,
  backgroundColor: Int = Colors.WhiteCircle
) : BaseDrawable() {
  
  private val circlePaint = Paint(color = backgroundColor)
  private val textPaint = TextPaint(
    color = textColor,
    font = Fonts.SegoeUiBold,
    textAlign = Paint.Align.LEFT
  )
  private var textRect = Rect()
  
  init {
    require(letter.length == 1)
  }
  
  fun setLetter(letter: String) {
    require(letter.length == 1)
    this.letter = letter
    invalidateSelf()
  }
  
  override fun draw(canvas: Canvas) {
    val hw = bounds.width() / 2f
    val hh = bounds.height() / 2f
    textPaint.textSize = minOf(hw, hh)
    textPaint.getTextBounds(letter, 0, letter.length, textRect)
    canvas.drawCircle(hw, hh, minOf(hw, hh), circlePaint)
    val x = hw - textRect.width() / 2f - textRect.left
    val y = hh + textRect.height() / 2f - textRect.bottom
    canvas.drawText(letter, x, y, textPaint)
  }
}
