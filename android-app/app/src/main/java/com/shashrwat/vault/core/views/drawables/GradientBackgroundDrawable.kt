package com.shashrwat.vault.core.views.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import com.shashrwat.vault.R
import com.shashrwat.vault.viewbuilding.Colors

class GradientBackgroundDrawable(context: Context) : BaseDrawable() {
  
  private val gradient = checkNotNull(context.getDrawable(R.drawable.bg_gradient))
  
  override fun onBoundsChange(bounds: Rect) {
    gradient.setBounds(0, 0, bounds.width(), gradient.intrinsicHeight)
  }
  
  override fun draw(canvas: Canvas) {
    canvas.drawColor(Colors.Background)
    gradient.draw(canvas)
  }
}
