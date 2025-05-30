package com.shashrwat.vault.core.extensions

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Fonts

val TEMP_RECT = Rect()

fun Any.Paint(
  color: Int
) = android.graphics.Paint(Paint.ANTI_ALIAS_FLAG).apply {
  this.color = color
}

fun Any.TextPaint(
  textSize: Float = 0f,
  color: Int = Colors.TextPrimary,
  textAlign: Paint.Align = Paint.Align.CENTER,
  font: Typeface = Fonts.SegoeUi
) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
  this.textSize = textSize
  this.color = color
  this.textAlign = textAlign
  this.typeface = font
}

/**
 * Using "Agy" text so that it can be measured properly, taking into account all possible heights
 * of text
 */
fun TextPaint.getTextHeight(text: String = "Agy"): Int {
  TEMP_RECT.setEmpty()
  getTextBounds(text, 0, text.length, TEMP_RECT)
  return TEMP_RECT.height()
}