package com.shashrwat.vault.test.tests

import androidx.test.core.app.ApplicationProvider
import com.shashrwat.vault.R
import com.shashrwat.vault.core.views.drawables.LetterInCircleDrawable
import com.shashrwat.vault.features.common.di.CoreComponentHolder
import com.shashrwat.vault.features.initial.InitialScreen
import com.shashrwat.vault.features.main_list.MainListScreen
import com.shashrwat.vault.features.settings.SettingsScreen
import com.shashrwat.vault.test.core.data.Databases
import com.shashrwat.vault.test.core.di.StubExtraDependenciesFactory
import com.shashrwat.vault.test.core.di.stubs.TestImageRequestsRecorder
import com.shashrwat.vault.test.core.di.stubs.URL_IMAGE_GOOGLE
import com.shashrwat.vault.test.core.ext.currentScreenIs
import com.shashrwat.vault.test.core.ext.launchActivityWithDatabase
import com.shashrwat.vault.test.core.ext.wasImageRequestWithUrlPerformed
import com.shashrwat.vault.test.core.rule.VaultAutotestRule
import com.shashrwat.vault.test.screens.KInitialScreen
import com.shashrwat.vault.test.screens.KLoginScreen
import com.shashrwat.vault.test.screens.KMainListScreen
import com.shashrwat.vault.test.screens.KMainListScreen.PasswordItem
import com.shashrwat.vault.test.screens.KMasterPasswordScreen
import com.shashrwat.vault.test.screens.KSettingsScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import domain.PasswordStrength
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MasterPasswordTest : TestCase() {
  
  @get:Rule
  val rule = VaultAutotestRule()
  
  private val testImageRequestsRecorder = TestImageRequestsRecorder()
  
  @Before
  fun setup() {
    CoreComponentHolder.initialize(
      application = ApplicationProvider.getApplicationContext(),
      factory = StubExtraDependenciesFactory(
        imagesRequestsRecorder = testImageRequestsRecorder
      )
    )
  }
  
  @Test
  fun testCreatingNewMasterPassword() = init {
    rule.launchActivity()
  }.run {
    KInitialScreen {
      buttonCreateMasterPassword.click()
    }
    KMasterPasswordScreen {
      closeSoftKeyboard()
      pressBack()
    }
    
    currentScreenIs<InitialScreen>()
    
    KInitialScreen {
      buttonCreateMasterPassword.click()
    }
    
    KMasterPasswordScreen {
      titleFirst {
        isDisplayed()
        hasText("Create master password")
      }
      titleSecond.isNotDisplayed()
      buttonContinue.isCompletelyDisplayed()
      textPasswordStrength.isNotDisplayed()
      passwordStrengthMeter.isNotShowingStrength()
      editTextEnterPassword {
        isDisplayed()
        hasEmptyText()
        isPasswordHidden()
        hasHint("Enter password")
      }
      
      buttonContinue.click()
      
      textError {
        isDisplayed()
        hasText("Password is empty")
      }
      iconError.isNotDisplayed()
      textPasswordStrength.isNotDisplayed()
      passwordStrengthMeter.isNotShowingStrength()
      passwordStrengthDialog.isNotShown()
      
      editTextEnterPassword.typeText("aa")
      
      textError.isNotDisplayed()
      
      editTextEnterPassword.toggleVisibility()
      editTextEnterPassword.isPasswordVisible()
      
      buttonContinue.click()
      
      textError {
        isDisplayed()
        hasText("Password is too weak")
      }
      iconError.isDisplayed()
      textPasswordStrength.hasText("Weak")
      passwordStrengthMeter.hasPasswordStrength(PasswordStrength.WEAK)
      
      iconError.click()
      
      passwordStrengthDialog {
        isShown()
        title.isDisplayed()
        description.isDisplayed()
        textContinueWithWeakPassword.isDisplayed()
        textChangePassword.isDisplayed()
      }
      
      closeSoftKeyboard()
      pressBack()
      
      passwordStrengthDialog.isNotShown()
      
      iconError.click()
      
      passwordStrengthDialog.hide()
      
      passwordStrengthDialog.isNotShown()
      
      iconError.click()
      
      passwordStrengthDialog.proceedWithWeakPassword()
      
      textError.isNotDisplayed()
      iconError.isNotDisplayed()
      titleFirst.isNotDisplayed()
      titleSecond.isDisplayed()
      
      pressBack()
      
      titleFirst.isDisplayed()
      titleSecond.isNotDisplayed()
      
      editTextEnterPassword.replaceText("qwetu123")
      passwordStrengthMeter.hasPasswordStrength(PasswordStrength.MEDIUM)
      textPasswordStrength.hasText("Medium")
      
      editTextEnterPassword.typeText("/")
      passwordStrengthMeter.hasPasswordStrength(PasswordStrength.STRONG)
      textPasswordStrength.hasText("Strong")
      
      editTextEnterPassword.typeText("yd2")
      passwordStrengthMeter.hasPasswordStrength(PasswordStrength.SECURE)
      textPasswordStrength.hasText("Secure")
      
      buttonContinue.click()
      
      titleFirst.isNotDisplayed()
      titleSecond {
        hasText("Repeat password")
        isDisplayed()
      }
      
      editTextEnterPassword.isNotDisplayed()
      imageBack.isDisplayed()
      editTextRepeatPassword {
        isDisplayed()
        hasEmptyText()
        isPasswordHidden()
        hasHint("Repeat password")
      }
      
      editTextRepeatPassword.replaceText("ppp")
      buttonContinue.click()
      
      textError {
        isDisplayed()
        hasText("Passwords don't match")
      }
      iconError.isNotDisplayed()
      
      editTextRepeatPassword.typeText("o")
      
      textError.isNotDisplayed()
      
      imageBack.click()
      
      editTextRepeatPassword.isNotDisplayed()
      titleSecond.isNotDisplayed()
      titleFirst {
        hasText("Create master password")
        isDisplayed()
      }
      editTextEnterPassword {
        isDisplayed()
        isPasswordVisible()
        hasText("qwetu123/yd2")
      }
      
      buttonContinue.click()
      
      editTextRepeatPassword.replaceText("qwetu123/yd2")
      
      buttonContinue.click()
    }
    currentScreenIs<MainListScreen>()
  }
  
  @Test
  fun testChangeExistingMasterPassword() = init {
    rule.launchActivityWithDatabase(Databases.TwoPasswords)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      KMainListScreen {
        menu {
          open()
          settingsMenuItem.click()
        }
        KSettingsScreen {
          itemChangePassword.click()
          
          enterPasswordDialog {
            isDisplayed()
            title.hasText("Enter master password to continue")
            textError.hasEmptyText()
            
            editText.replaceText("abc")
            buttonContinue.click()
            
            textError.hasText("Password is incorrect")
            
            editText.replaceText("qwetu1233")
            
            textError.hasEmptyText()
            
            buttonContinue.click()
            
            KMasterPasswordScreen {
              titleFirst.hasText("Change master password")
              
              editTextEnterPassword.replaceText("qwetu1233")
              
              buttonContinue.click()
              
              textError {
                hasText("Password is the same as current one")
                isDisplayed()
              }
              iconError.isNotDisplayed()
              
              editTextEnterPassword.replaceText("abcd90-+321")
              
              buttonContinue.click()
              
              editTextRepeatPassword.replaceText("abcd90-+321")
              
              buttonContinue.click()
              
              confirmationDialog {
                isDisplayed()
                title.hasText("Confirmation")
                message.hasText(R.string.text_confirmation_message)
                action1.hasText("CANCEL")
                action2.hasText("CONTINUE")
              }
              
              closeSoftKeyboard()
              pressBack()
              
              confirmationDialog.isNotDisplayed()
              
              buttonContinue.click()
              
              confirmationDialog.isDisplayed()
              
              confirmationDialog.action1.click()
              
              confirmationDialog.isNotDisplayed()
              
              buttonContinue.click()
              
              confirmationDialog.action2.click()
              
              loadingDialog.isDisplayed()
            }
          }
          
          currentScreenIs<SettingsScreen>()
          
          snackbar.isDisplayedWithText("Master password successfully changed")
        }
      }
    }
    
    rule.finishActivity()
    
    rule.launchActivity()
    
    KLoginScreen {
      editTextEnterPassword.clearText()
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      
      textError.hasText("Password is incorrect")
      
      editTextEnterPassword.replaceText("abcd90-+321")
      buttonContinue.click()
      
      KMainListScreen {
        recycler {
          hasSize(3)
          childAt<PasswordItem>(1) {
            title.hasText("google")
            image.wasImageRequestWithUrlPerformed(URL_IMAGE_GOOGLE, testImageRequestsRecorder)
          }
          childAt<PasswordItem>(2) {
            title.hasText("test.com")
            image.hasDrawable(LetterInCircleDrawable("t"))
          }
        }
      }
    }
  }
}
