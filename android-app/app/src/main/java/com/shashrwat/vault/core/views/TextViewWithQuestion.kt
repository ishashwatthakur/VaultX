package com.shashrwat.vault.core.views

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.shashrwat.vault.R
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Dimens.MarginSmall
import com.shashrwat.vault.viewbuilding.Styles.BaseTextView
import viewdsl.Size.Companion.WrapContent
import viewdsl.id
import viewdsl.image
import viewdsl.invisible
import viewdsl.margins
import viewdsl.orientation
import viewdsl.textColor
import viewdsl.visible
import viewdsl.withViewBuilder

class TextWithQuestion(context: Context) : LinearLayout(context) {
  
  private val textView get() = getChildAt(0) as FixedHeightTextView
  private val imageView get() = getChildAt(1) as ImageView
  
  private var onClickListener = {}
  
  init {
    orientation(HORIZONTAL)
    withViewBuilder {
      child<FixedHeightTextView>(WrapContent, WrapContent, style = BaseTextView) {
        id(Text)
        textColor(Colors.TextError)
      }
      ImageView(WrapContent, WrapContent) {
        id(Image)
        image(R.drawable.ic_question, Colors.Error)
        margins(start = MarginSmall)
        invisible()
      }
    }
  }
  
  fun onClick(block: () -> Unit) {
    onClickListener = block
  }
  
  fun setText(@StringRes textRes: Int) {
    textView.setText(textRes)
  }
  
  fun clear() {
    textView.text = ""
    hideQuestion()
  }
  
  fun showQuestion() {
    imageView.visible()
    setOnClickListener { onClickListener() }
  }
  
  fun hideQuestion() {
    imageView.invisible()
    setOnClickListener(null)
  }
  
  companion object {
    
    val Text = View.generateViewId()
    val Image = View.generateViewId()
  }
}
