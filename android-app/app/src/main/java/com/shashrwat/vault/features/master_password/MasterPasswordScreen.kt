package com.shashrwat.vault.features.master_password

import android.content.Context
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.ViewSwitcher
import com.shashrwat.vault.R
import com.shashrwat.vault.core.extensions.arg
import com.shashrwat.vault.core.mvi.ext.subscribe
import com.shashrwat.vault.core.mvi.ext.viewModelStore
import com.shashrwat.vault.core.views.EditTextPassword
import com.shashrwat.vault.core.views.FixedHeightTextView
import com.shashrwat.vault.core.views.PasswordStrengthMeter
import com.shashrwat.vault.core.views.TextWithQuestion
import com.shashrwat.vault.features.common.Durations
import com.shashrwat.vault.features.common.di.CoreComponentHolder.coreComponent
import com.shashrwat.vault.features.common.dialogs.InfoDialog.Companion.InfoDialog
import com.shashrwat.vault.features.common.dialogs.InfoDialog.Companion.infoDialog
import com.shashrwat.vault.features.common.dialogs.LoadingDialog
import com.shashrwat.vault.features.common.dialogs.PasswordStrengthDialog.Companion.PasswordStrengthDialog
import com.shashrwat.vault.features.common.dialogs.PasswordStrengthDialog.Companion.passwordStrengthDialog
import com.shashrwat.vault.features.common.dialogs.loadingDialog
import com.shashrwat.vault.features.master_password.MasterPasswordNews.FinishingSavingMasterPassword
import com.shashrwat.vault.features.master_password.MasterPasswordScreenMode.CHANGE_EXISTING
import com.shashrwat.vault.features.master_password.MasterPasswordScreenMode.CREATING_NEW
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnBackPressed
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnCancelChangePassword
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnConfirmChangePassword
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnContinueClicked
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnHidePasswordStrengthDialog
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnInitialPasswordTyping
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnProceedWithWeakPassword
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnRepeatPasswordTyping
import com.shashrwat.vault.features.master_password.MasterPasswordUiEvent.OnShowPasswordStrengthDialog
import com.shashrwat.vault.features.master_password.PasswordEnteringState.INITIAL
import com.shashrwat.vault.features.master_password.PasswordEnteringState.REPEATING
import com.shashrwat.vault.features.master_password.UiPasswordStatus.EMPTY
import com.shashrwat.vault.features.master_password.UiPasswordStatus.OK
import com.shashrwat.vault.features.master_password.UiPasswordStatus.PASSWORDS_DONT_MATCH
import com.shashrwat.vault.features.master_password.UiPasswordStatus.PASSWORD_SAME_AS_CURRENT
import com.shashrwat.vault.features.master_password.UiPasswordStatus.TOO_WEAK
import com.shashrwat.vault.viewbuilding.Dimens.MarginLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginMedium
import com.shashrwat.vault.viewbuilding.Dimens.MarginNormal
import com.shashrwat.vault.viewbuilding.Dimens.MarginSmall
import com.shashrwat.vault.viewbuilding.Dimens.MarginTiny
import com.shashrwat.vault.viewbuilding.Dimens.PasswordStrengthMeterHeight
import com.shashrwat.vault.viewbuilding.Styles.BoldTextView
import com.shashrwat.vault.viewbuilding.Styles.Button
import com.shashrwat.vault.viewbuilding.Styles.ImageBack
import com.shashrwat.vault.viewbuilding.TextSizes
import domain.PasswordStrength.MEDIUM
import domain.PasswordStrength.SECURE
import domain.PasswordStrength.STRONG
import domain.PasswordStrength.WEAK
import navigation.BaseFragmentScreen
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.Size.IntSize
import viewdsl.animateInvisible
import viewdsl.animateVisible
import viewdsl.classNameTag
import viewdsl.clearText
import viewdsl.hideKeyboard
import viewdsl.id
import viewdsl.invisible
import viewdsl.layoutGravity
import viewdsl.marginHorizontal
import viewdsl.margins
import viewdsl.onClick
import viewdsl.text
import viewdsl.textSize
import viewdsl.visible
import viewdsl.withViewBuilder

class MasterPasswordScreen : BaseFragmentScreen() {
  
  override fun buildLayout(context: Context) = context.withViewBuilder {
    RootFrameLayout(MatchParent, MatchParent) {
      id(MasterPasswordScreenRoot)
      HorizontalLayout(MatchParent, WrapContent) {
        margins(top = MarginMedium + StatusBarHeight, start = MarginSmall)
        ImageView(WrapContent, WrapContent, style = ImageBack) {
          onClick { store.tryDispatch(OnBackPressed) }
        }
        child<TextSwitcher>(WrapContent, WrapContent) {
          id(TitleSwitcher)
          layoutGravity(CENTER_VERTICAL)
          TextView(WrapContent, WrapContent, style = BoldTextView) {
            id(TitleFirst)
            margins(start = MarginNormal)
            textSize(TextSizes.H1)
          }
          TextView(WrapContent, WrapContent, style = BoldTextView) {
            id(TitleSecond)
            margins(start = MarginNormal)
            textSize(TextSizes.H1)
          }
        }
      }
      VerticalLayout(MatchParent, WrapContent) {
        layoutGravity(CENTER)
        child<FixedHeightTextView>(WrapContent, WrapContent, style = BoldTextView) {
          id(TextPasswordStrength)
          margins(start = MarginNormal)
        }
        child<PasswordStrengthMeter>(MatchParent, IntSize(PasswordStrengthMeterHeight)) {
          classNameTag()
          margins(top = MarginNormal, start = MarginNormal,
            end = MarginNormal, bottom = MarginLarge)
        }
        child<ViewSwitcher>(MatchParent, WrapContent) {
          classNameTag()
          inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
          outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
          child<EditTextPassword>(MatchParent, WrapContent) {
            id(EditTextEnterPassword)
            marginHorizontal(MarginNormal - MarginTiny)
            setHint(R.string.text_enter_password)
            onTextChanged { text ->
              store.tryDispatch(OnInitialPasswordTyping(text))
            }
          }
          child<EditTextPassword>(MatchParent, WrapContent) {
            id(EditTextRepeatPassword)
            marginHorizontal(MarginNormal - MarginTiny)
            setHint(R.string.text_repeat_password)
            onTextChanged { text ->
              store.tryDispatch(OnRepeatPasswordTyping(text))
            }
          }
        }
        child<TextWithQuestion>(MatchParent, WrapContent) {
          classNameTag()
          margins(start = MarginNormal, end = MarginNormal, top = MarginSmall)
          onClick { store.tryDispatch(OnShowPasswordStrengthDialog) }
        }
      }
      TextView(MatchParent, WrapContent, style = Button()) {
        id(TextContinue)
        layoutGravity(Gravity.BOTTOM)
        text(R.string.text_continue)
        margins(start = MarginNormal, end = MarginNormal, bottom = MarginNormal)
        onClick { store.tryDispatch(OnContinueClicked) }
      }
      LoadingDialog()
      InfoDialog()
      PasswordStrengthDialog {
        onHide = { store.tryDispatch(OnHidePasswordStrengthDialog) }
        onChangePasswordClicked { store.tryDispatch(OnHidePasswordStrengthDialog) }
        onProceedClicked { store.tryDispatch(OnProceedWithWeakPassword) }
      }
    }
  }
  
  private var passwordEnteringState = INITIAL
  
  private val store by viewModelStore {
    MasterPasswordStore(coreComponent, arg(MasterPasswordScreenMode::class))
  }
  
  override fun onInit() {
    store.subscribe(this, ::render, ::handleNews)
  }
  
  override fun onAppearedOnScreen() {
    requireView().postDelayed({
      viewAsNullable<EditTextPassword>(EditTextEnterPassword)?.showKeyboard()
    }, Durations.DelayOpenKeyboard)
  }
  
  private fun render(state: MasterPasswordState) {
    when (state.mode) {
      CREATING_NEW -> textView(TitleFirst).text(R.string.text_create_master_password)
      CHANGE_EXISTING -> textView(TitleFirst).text(R.string.text_change_master_password)
    }
    if (passwordEnteringState != state.passwordEnteringState) {
      passwordEnteringState = state.passwordEnteringState
      when (passwordEnteringState) {
        INITIAL -> switchToEnterPasswordState(state.mode)
        REPEATING -> switchToRepeatPasswordState()
      }
    }
    if (state.showPasswordChangeConfirmationDialog) {
      infoDialog.showWithCancelAndProceedOption(
        titleRes = R.string.text_confirmation,
        message = getString(R.string.text_confirmation_message),
        proceedTextRes = R.string.text_continue_caps,
        onCancel = { store.tryDispatch(OnCancelChangePassword) },
        onProceed = { store.tryDispatch(OnConfirmChangePassword) }
      )
    } else {
      infoDialog.hide()
    }
    if (state.showPasswordTooWeakDialog) {
      passwordStrengthDialog.show()
    } else {
      passwordStrengthDialog.hide()
    }
    showPasswordStatus(state.passwordStatus)
    showPasswordStrength(state)
  }
  
  private fun handleNews(event: MasterPasswordNews) {
    if (event is FinishingSavingMasterPassword) {
      requireContext().hideKeyboard()
      loadingDialog.show()
    }
  }
  
  override fun handleBackPress(): Boolean {
    store.tryDispatch(OnBackPressed)
    return true
  }
  
  private fun showPasswordStatus(passwordStatus: UiPasswordStatus) {
    val text = when (passwordStatus) {
      OK -> R.string.text_empty
      EMPTY -> R.string.text_password_is_empty
      TOO_WEAK -> R.string.text_password_is_too_weak
      PASSWORDS_DONT_MATCH -> R.string.text_passwords_dont_match
      PASSWORD_SAME_AS_CURRENT -> R.string.text_password_is_the_same_as_current
    }
    if (passwordStatus == TOO_WEAK) {
      viewAs<TextWithQuestion>().showQuestion()
    } else {
      viewAs<TextWithQuestion>().hideQuestion()
    }
    viewAs<TextWithQuestion>().setText(text)
  }
  
  private fun showPasswordStrength(state: MasterPasswordState) {
    if (state.passwordEnteringState == REPEATING) {
      textView(TextPasswordStrength).clearText()
      return
    }
    viewAs<PasswordStrengthMeter>().setStrength(state.passwordStrength)
    val textResId = when (state.passwordStrength) {
      WEAK -> R.string.text_weak
      MEDIUM -> R.string.text_medium
      STRONG -> R.string.text_strong
      SECURE -> R.string.text_secure
      null -> R.string.text_empty
    }
    textView(TextPasswordStrength).text(textResId)
  }
  
  private fun switchToEnterPasswordState(mode: MasterPasswordScreenMode) {
    viewAs<PasswordStrengthMeter>().setStrength(null, animate = false)
    viewAs<PasswordStrengthMeter>().visible()
    val textRes = if (mode == CREATING_NEW)
      R.string.text_create_master_password else R.string.text_change_master_password
    viewAs<TextSwitcher>(TitleSwitcher).setText(getText(textRes))
    textView(TextPasswordStrength).animateVisible()
    viewAs<ViewSwitcher>().apply {
      inAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
      outAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_right)
      showPrevious()
    }
  }
  
  private fun switchToRepeatPasswordState() {
    viewAs<TextSwitcher>(TitleSwitcher).setText(getText(R.string.text_repeat_password))
    viewAs<EditTextPassword>(EditTextRepeatPassword).clearText()
    textView(TextPasswordStrength).animateInvisible()
    viewAs<PasswordStrengthMeter>().invisible()
    viewAs<ViewSwitcher>().apply {
      inAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
      outAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
      showNext()
    }
  }
  
  companion object {
    
    val MasterPasswordScreenRoot = View.generateViewId()
    val TitleSwitcher = View.generateViewId()
    val TitleFirst = View.generateViewId()
    val TitleSecond = View.generateViewId()
    val TextPasswordStrength = View.generateViewId()
    val TextContinue = View.generateViewId()
    val EditTextEnterPassword = View.generateViewId()
    val EditTextRepeatPassword = View.generateViewId()
  }
}
