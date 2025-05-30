package com.shashrwat.vault.features.common.dialogs

import android.content.Context
import android.view.Gravity.CENTER
import android.view.Gravity.END
import android.view.View
import android.view.ViewGroup
import com.shashrwat.vault.R
import com.shashrwat.vault.core.views.SimpleDialog
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Dimens.CornerRadiusDefault
import com.shashrwat.vault.viewbuilding.Dimens.MarginExtraLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginNormal
import com.shashrwat.vault.viewbuilding.Dimens.MarginSmall
import com.shashrwat.vault.viewbuilding.Styles.BaseTextView
import com.shashrwat.vault.viewbuilding.Styles.BoldTextView
import com.shashrwat.vault.viewbuilding.Styles.ClickableTextView
import com.shashrwat.vault.viewbuilding.TextSizes
import navigation.BaseFragmentScreen
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.backgroundRoundRect
import viewdsl.classNameTag
import viewdsl.id
import viewdsl.layoutGravity
import viewdsl.marginHorizontal
import viewdsl.margins
import viewdsl.onClick
import viewdsl.text
import viewdsl.textColor
import viewdsl.textSize
import viewdsl.textView
import viewdsl.withViewBuilder

class PasswordStrengthDialog(context: Context) : SimpleDialog(context) {
  
  init {
    withViewBuilder {
      VerticalLayout(WrapContent, WrapContent) {
        layoutGravity(CENTER)
        marginHorizontal(MarginExtraLarge)
        backgroundRoundRect(CornerRadiusDefault, Colors.Dialog)
        TextView(WrapContent, WrapContent, style = BoldTextView) {
          margins(top = MarginNormal, start = MarginNormal)
          text(R.string.text_password_is_too_weak)
          textSize(TextSizes.H3)
        }
        TextView(WrapContent, WrapContent, style = BaseTextView) {
          margins(top = MarginLarge, start = MarginNormal, end = MarginNormal)
          textSize(TextSizes.H6)
          text(R.string.text_password_should_be_strong)
        }
        TextView(WrapContent, WrapContent, style = ClickableTextView()) {
          id(TextChangePassword)
          layoutGravity(END)
          margins(top = MarginLarge, start = MarginSmall, end = MarginSmall)
          text(R.string.text_change_password)
        }
        TextView(WrapContent, WrapContent, style = ClickableTextView(Colors.ErrorRipple)) {
          id(TextProceedWithWeakPassword)
          layoutGravity(END)
          textColor(Colors.TextError)
          margins(top = MarginSmall, bottom = MarginNormal, start = MarginSmall, end = MarginSmall)
          text(R.string.text_continue_with_weak_password)
        }
      }
    }
  }
  
  fun onChangePasswordClicked(block: () -> Unit) {
    textView(TextChangePassword).onClick(block)
  }
  
  fun onProceedClicked(block: () -> Unit) {
    textView(TextProceedWithWeakPassword).onClick(block)
  }
  
  companion object {
    
    private val TextChangePassword = View.generateViewId()
    private val TextProceedWithWeakPassword = View.generateViewId()
    
    val BaseFragmentScreen.passwordStrengthDialog get() = viewAs<PasswordStrengthDialog>()
    
    fun ViewGroup.PasswordStrengthDialog(
      block: PasswordStrengthDialog.() -> Unit = {}
    ) = withViewBuilder {
      child<PasswordStrengthDialog, ViewGroup.LayoutParams>(MatchParent, MatchParent, block) {
        classNameTag()
      }
    }
  }
}
