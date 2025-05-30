package com.shashrwat.vault.test.screens

import android.widget.SeekBar
import android.widget.TextView
import com.shashrwat.vault.R
import com.shashrwat.vault.core.views.PasswordStrengthMeterWithText
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen.Companion.ButtonGeneratePassword
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen.Companion.EditTextPassword
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen.Companion.TextPasswordLength
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen.Companion.Title
import com.shashrwat.vault.test.core.base.BaseScreen
import com.shashrwat.vault.test.core.ext.withClassNameTag
import com.shashrwat.vault.test.core.views.checkmark.KCheckmarkAndTextView
import com.shashrwat.vault.test.core.views.password_strength_meter.KPasswordStrengthMeter
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.progress.KSeekBar
import io.github.kakaocup.kakao.text.KTextView

object KCreatingPasswordScreen : BaseScreen<KCreatingPasswordScreen>() {
  
  override val viewClass = CreatingPasswordScreen::class.java
  
  val iconCross = KImageView {
    isDisplayed()
    withDrawable(R.drawable.ic_cross)
  }
  val title = KTextView { withId(Title) }
  val editTextPassword = KEditText { withId(EditTextPassword) }
  val textPasswordStrength = KTextView {
    withParent { isInstanceOf(PasswordStrengthMeterWithText::class.java) }
    isInstanceOf(TextView::class.java)
  }
  val passwordStrengthMeter = KPasswordStrengthMeter()
  val textPasswordLength = KTextView { withId(TextPasswordLength) }
  val passwordLengthSpinner = KSeekBar { withClassNameTag<SeekBar>() }
  val checkmarkUppercaseSymbols = KCheckmarkAndTextView { withId(R.string.text_uppercase_symbols) }
  val checkmarkNumbers = KCheckmarkAndTextView { withId(R.string.text_numbers) }
  val buttonGeneratePassword = KTextView { withId(ButtonGeneratePassword) }
  val buttonSavePassword = KTextView { withText("Save") }
  val checkmarkSpecialSymbols = KCheckmarkAndTextView { withId(R.string.text_special_symbols) }
}
