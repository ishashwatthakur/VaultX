package com.shashrwat.vault.test.tests

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.shashrwat.vault.R
import com.shashrwat.vault.core.views.drawables.LetterInCircleDrawable
import com.shashrwat.vault.features.common.di.CoreComponentHolder
import com.shashrwat.vault.features.creating_password.CreatingPasswordScreen
import com.shashrwat.vault.features.main_list.MainListScreen
import com.shashrwat.vault.features.password_entry.PasswordEntryScreen
import com.shashrwat.vault.test.core.base.VaultTestCase
import com.shashrwat.vault.test.core.data.Databases
import com.shashrwat.vault.test.core.di.StubExtraDependenciesFactory
import com.shashrwat.vault.test.core.di.stubs.TestImageRequestsRecorder
import com.shashrwat.vault.test.core.di.stubs.URL_IMAGE_GOOGLE
import com.shashrwat.vault.test.core.ext.currentScreenIs
import com.shashrwat.vault.test.core.ext.hasClipboardText
import com.shashrwat.vault.test.core.ext.hasNoDrawable
import com.shashrwat.vault.test.core.ext.hasPasswordWithAllCharacteristics
import com.shashrwat.vault.test.core.ext.hasPasswordWithCharacteristics
import com.shashrwat.vault.test.core.ext.hasPasswordWithNoCharacteristics
import com.shashrwat.vault.test.core.ext.hasTextColorInt
import com.shashrwat.vault.test.core.ext.hasTextLength
import com.shashrwat.vault.test.core.ext.launchActivityWithDatabase
import com.shashrwat.vault.test.core.ext.waitForSnackbarToHide
import com.shashrwat.vault.test.core.ext.wasImageRequestWithUrlPerformed
import com.shashrwat.vault.test.core.rule.VaultAutotestRule
import com.shashrwat.vault.test.screens.KCreatingPasswordScreen
import com.shashrwat.vault.test.screens.KLoginScreen
import com.shashrwat.vault.test.screens.KMainListScreen
import com.shashrwat.vault.test.screens.KMainListScreen.EmptyItem
import com.shashrwat.vault.test.screens.KMainListScreen.PasswordItem
import com.shashrwat.vault.test.screens.KMainListScreen.NoteItem
import com.shashrwat.vault.test.screens.KPasswordEntryScreen
import com.shashrwat.vault.viewbuilding.Colors
import domain.PasswordStrength.SECURE
import domain.PasswordStrength.WEAK
import domain.model.PasswordCharacteristic.NUMBERS
import domain.model.PasswordCharacteristic.SPECIAL_SYMBOLS
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test

class PasswordEntryTest : VaultTestCase() {
  
  @get:Rule
  val rule = VaultAutotestRule()
  
  private val testImageRequestsRecorder = TestImageRequestsRecorder()
  
  @Test
  fun testCreatingPasswordEntry() = init {
    rule.launchActivityWithDatabase(Databases.Empty)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
    }
    KMainListScreen {
      recycler {
        hasSize(1)
        firstChild<EmptyItem> {
          image.isDisplayed()
          title.isDisplayed()
          message.isDisplayed()
        }
      }
      
      menu {
        open()
        newEntryMenuItem.click()
      }
      
      entryTypeDialog.isDisplayed()
      
      entryTypeDialog.passwordEntry.click()
      
      KPasswordEntryScreen {
        imageBack.click()
      }
      
      currentScreenIs<MainListScreen>()
      
      recycler.isDisplayed()
      menu.isClosed()
      entryTypeDialog.isNotDisplayed()
      
      menu {
        open()
        newEntryMenuItem.click()
      }
      
      entryTypeDialog.passwordEntry.click()
      
      KPasswordEntryScreen {
        imageFavorite.isNotDisplayed()
        imageDelete.isNotDisplayed()
        imageTitle.hasNoDrawable()
        titleNewPassword {
          isDisplayed()
          hasText("New password")
        }
        titleTitle.hasText("Title")
        titleUsername.hasText("Username")
        titlePassword.hasText("Password")
        titleUrl.hasText("Url")
        titleNotes.hasText("Notes")
        buttonSave.isDisplayed()
        
        closeSoftKeyboard()
        buttonSave.click()
        
        titleTitle {
          hasTextColorInt(Colors.TextError)
          hasText("Title is empty")
        }
        
        editTextTitle.replaceText("test.com")
        
        titleTitle {
          hasTextColorInt(Colors.Accent)
          hasText("Title")
        }
        imageTitle.hasDrawable(LetterInCircleDrawable("t"))
        
        editTextUsername.replaceText("myusername")
        
        imageEditPassword.click()
        
        KCreatingPasswordScreen {
          iconCross.click()
        }
        
        currentScreenIs<PasswordEntryScreen>()
        
        imageEditPassword.click()
        
        KCreatingPasswordScreen {
          title {
            isDisplayed()
            hasText("Password")
          }
          editTextPassword {
            isDisplayed()
            hasTextLength(16) // default password length
            hasPasswordWithAllCharacteristics()
          }
          checkmarkUppercaseSymbols.isChecked()
          checkmarkNumbers.isChecked()
          checkmarkSpecialSymbols.isChecked()
          textPasswordLength.hasText("Length: 16")
          passwordLengthSpinner.hasProgress(8) // defaultPasswordLength - minPasswordLength
          buttonGeneratePassword {
            hasText("Generate password")
            isDisplayed()
          }
          buttonSavePassword.isDisplayed()
          
          passwordLengthSpinner.setProgress(13)
          
          buttonGeneratePassword.click()
          buttonGeneratePassword.click()
          buttonGeneratePassword.click()
          
          textPasswordLength.hasText("Length: 21")
          editTextPassword {
            hasTextLength(21)
            hasPasswordWithAllCharacteristics()
          }
          textPasswordStrength.hasText("Secure")
          passwordStrengthMeter.hasPasswordStrength(SECURE)
          
          checkmarkUppercaseSymbols.click()
          buttonGeneratePassword.click()
          
          checkmarkUppercaseSymbols.isNotChecked()
          checkmarkNumbers.isChecked()
          checkmarkSpecialSymbols.isChecked()
          editTextPassword {
            hasTextLength(21)
            hasPasswordWithCharacteristics(NUMBERS, SPECIAL_SYMBOLS)
          }
          
          checkmarkNumbers.click()
          buttonGeneratePassword.click()
          
          checkmarkUppercaseSymbols.isNotChecked()
          checkmarkNumbers.isNotChecked()
          checkmarkSpecialSymbols.isChecked()
          editTextPassword {
            hasTextLength(21)
            hasPasswordWithCharacteristics(SPECIAL_SYMBOLS)
          }
          
          checkmarkSpecialSymbols.click()
          buttonGeneratePassword.click()
          
          checkmarkUppercaseSymbols.isNotChecked()
          checkmarkNumbers.isNotChecked()
          checkmarkSpecialSymbols.isNotChecked()
          editTextPassword {
            hasTextLength(21)
            hasPasswordWithNoCharacteristics()
          }
          
          checkmarkUppercaseSymbols.click()
          checkmarkNumbers.click()
          checkmarkSpecialSymbols.click()
          buttonGeneratePassword.click()
          
          editTextPassword {
            hasTextLength(21)
            hasPasswordWithAllCharacteristics()
          }
          
          editTextPassword.replaceText("abcabcabcabcabc")
          
          checkmarkUppercaseSymbols.isNotChecked()
          checkmarkNumbers.isNotChecked()
          checkmarkUppercaseSymbols.isNotChecked()
          textPasswordStrength.hasText("Weak")
          passwordStrengthMeter.hasPasswordStrength(WEAK)
          
          buttonSavePassword.click()
        }
        
        editTextUrl.replaceText("example.com")
        
        buttonSave.click()
        
        imageFavorite.isDisplayed()
        imageDelete.isDisplayed()
        titleNewPassword.isNotDisplayed()
        
        imageTitle.hasDrawable(LetterInCircleDrawable("t"))
        
        editTextTitle.hasText("test.com")
        editTextUsername.hasText("myusername")
        textPassword.hasText(R.string.text_password_stub)
        editTextUrl.hasText("example.com")
        editTextNotes.hasEmptyText()
        
        imageEditPassword.click()
        
        currentScreenIs<CreatingPasswordScreen>()
        
        pressBack()
        
        currentScreenIs<PasswordEntryScreen>()
        
        pressBack()
      }
      
      currentScreenIs<MainListScreen>()
      
      recycler {
        isDisplayed()
        hasSize(2)
        childAt<PasswordItem>(1) {
          title.hasText("test.com")
          image.hasDrawable(LetterInCircleDrawable("t"))
        }
      }
    }
  }
  
  @Test
  fun testEditingPasswordEntry() = init {
    CoreComponentHolder.initialize(
      application = ApplicationProvider.getApplicationContext(),
      factory = StubExtraDependenciesFactory(
        imagesRequestsRecorder = testImageRequestsRecorder
      )
    )
    rule.launchActivityWithDatabase(Databases.TwoPasswordsAndNote)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      
      KMainListScreen {
        recycler.emptyChildAt(1) { click() }
        
        KPasswordEntryScreen {
          
          // -------- Testing general screen view --------
          
          imageBack.isDisplayed()
          imageTitle {
            isDisplayed()
            wasImageRequestWithUrlPerformed(URL_IMAGE_GOOGLE, testImageRequestsRecorder)
          }
          titleTitle.hasText("Title")
          editTextTitle.hasText("google")
          imageTitleAction.hasDrawable(R.drawable.ic_copy)
          titleUsername.hasText("Username")
          editTextUsername.hasText("me@gmail.com")
          imageUsernameAction.hasDrawable(R.drawable.ic_copy)
          titlePassword.hasText("Password")
          textPassword.hasText(R.string.text_password_stub)
          textPassword.hasTextColorInt(Colors.TextPrimary)
          titleUrl.hasText("Url")
          editTextUrl.hasEmptyText()
          imageUrlAction.hasDrawable(R.drawable.ic_copy)
          imageOpenUrl.isNotDisplayed()
          titleNotes.hasText("Notes")
          editTextNotes.hasEmptyText()
          imageNotesAction.hasDrawable(R.drawable.ic_copy)
          buttonSave.isNotDisplayed()
          snackbar.isNotDisplayed()
          
          imageBack.click()
          
          KMainListScreen {
            currentScreenIs<MainListScreen>()
            recycler.emptyChildAt(1) { click() }
          }
          
          // -------- Testing copying --------
          
          imageTitleAction.click()
          hasClipboardText("google")
          snackbar.isDisplayedWithText("Title successfully copied")
          
          waitForSnackbarToHide()
          
          imageUsernameAction.click()
          hasClipboardText("me@gmail.com")
          snackbar.isDisplayedWithText("Username successfully copied")
          
          waitForSnackbarToHide()
          
          imageCopyPassword.click()
          hasClipboardText("F/<1#E(J=\\51=k;")
          snackbar.isDisplayedWithText("Password successfully copied")
          
          waitForSnackbarToHide()
          
          imageUrlAction.click()
          hasClipboardText("")
          snackbar.isDisplayedWithText("Url successfully copied")
          
          waitForSnackbarToHide()
          
          imageNotesAction.click()
          hasClipboardText("")
          snackbar.isDisplayedWithText("Notes successfully copied")
          
          waitForSnackbarToHide()
          
          // -------- Testing title --------
          
          editTextTitle.typeText("a")
          
          imageTitleAction.hasDrawable(R.drawable.ic_checmark)
          editTextTitle.hasText("googlea")
          imageTitle.wasImageRequestWithUrlPerformed(URL_IMAGE_GOOGLE, testImageRequestsRecorder)
          
          imageBack.click()
          
          imageTitleAction.hasDrawable(R.drawable.ic_copy)
          editTextTitle.hasText("google")
          
          editTextTitle.clearText()
          imageTitleAction.click()
          
          titleTitle.hasText("Title is empty")
          titleTitle.hasTextColorInt(Colors.TextError)
          imageTitleAction.hasDrawable(R.drawable.ic_checmark)
          
          editTextTitle.replaceText("a")
          
          titleTitle.hasText("Title")
          titleTitle.hasTextColorInt(Colors.Accent)
          
          imageBack.click()
          
          editTextTitle.hasText("google")
          
          editTextTitle.replaceText("yabcd")
          imageTitleAction.click()
          
          imageTitle.hasDrawable(LetterInCircleDrawable("y"))
          editTextTitle.hasText("yabcd")
          
          imageTitleAction.click()
          
          hasClipboardText("yabcd")
          snackbar.isDisplayedWithText("Title successfully copied")
          
          imageBack.click()
          
          KMainListScreen {
            currentScreenIs<MainListScreen>()
            recycler {
              isDisplayed()
              hasSize(5)
              childAt<PasswordItem>(1) {
                title.hasText("test.com")
                image.hasDrawable(LetterInCircleDrawable("t"))
              }
              childAt<PasswordItem>(2) {
                title.hasText("yabcd")
                image.hasDrawable(LetterInCircleDrawable("y"))
              }
              childAt<NoteItem>(4) {
                title.hasText("my title")
              }
            }
            
            recycler.emptyChildAt(2) { click() }
          }
          
          // -------- Testing username --------
          
          editTextUsername.replaceText("qwerty")
          
          imageUsernameAction.hasDrawable(R.drawable.ic_checmark)
          
          closeSoftKeyboard()
          pressBack()
          
          imageUsernameAction.hasDrawable(R.drawable.ic_copy)
          editTextUsername.hasText("me@gmail.com")
          
          editTextUsername.clearText()
          imageUsernameAction.click()
          
          editTextUsername.hasEmptyText()
          imageUsernameAction.hasDrawable(R.drawable.ic_copy)
          
          editTextUsername.replaceText("kkk")
          
          imageUsernameAction.click()
          
          imageUsernameAction.hasDrawable(R.drawable.ic_copy)
          editTextUsername.hasText("kkk")
          
          imageUsernameAction.click()
          
          hasClipboardText("kkk")
          snackbar.isDisplayedWithText("Username successfully copied")
          
          // -------- Testing password --------
          
          imageEditPassword.click()
          
          KCreatingPasswordScreen {
            currentScreenIs<CreatingPasswordScreen>()
            editTextPassword.hasText("F/<1#E(J=\\51=k;")
            title.hasText("Edit password")
            
            pressBack()
          }
          
          currentScreenIs<PasswordEntryScreen>()
          
          imageEditPassword.click()
          
          KCreatingPasswordScreen {
            editTextPassword.clearText()
            iconCross.click()
          }
          
          currentScreenIs<PasswordEntryScreen>()
          
          imageEditPassword.click()
          
          KCreatingPasswordScreen {
            editTextPassword.replaceText("qwerty222;")
            
            buttonSavePassword.click()
          }
          
          currentScreenIs<PasswordEntryScreen>()
          
          imageCopyPassword.click()
          hasClipboardText("qwerty222;")
          
          imageEditPassword.click()
          
          KCreatingPasswordScreen {
            checkmarkUppercaseSymbols.isNotChecked()
            checkmarkNumbers.isChecked()
            checkmarkSpecialSymbols.isChecked()
            
            editTextPassword.clearText()
            
            buttonSavePassword.click()
          }
          
          textPassword.hasText("Generate password")
          textPassword.hasTextColorInt(Colors.TextSecondary)
          
          imageEditPassword.click()
          
          KCreatingPasswordScreen {
            checkmarkUppercaseSymbols.isNotChecked()
            checkmarkNumbers.isNotChecked()
            checkmarkSpecialSymbols.isNotChecked()
            
            buttonSavePassword.click()
          }
          
          // -------- Testing url --------
          
          editTextUrl.replaceText("a")
          
          imageOpenUrl.isDisplayed()
          
          editTextUrl.clearText()
          
          imageOpenUrl.isNotDisplayed()
          
          editTextUrl.replaceText("b")
          
          imageUrlAction.hasDrawable(R.drawable.ic_checmark)
          
          imageBack.click()
          
          imageUrlAction.hasDrawable(R.drawable.ic_copy)
          editTextUrl.hasEmptyText()
          
          editTextUrl.replaceText("example.com")
          
          imageUrlAction.click()
          
          editTextUrl.hasText("example.com")
          
          imageUrlAction.click()
          
          hasClipboardText("example.com")
          snackbar.isDisplayedWithText("Url successfully copied")
          
          waitForSnackbarToHide()
          
          // -------- Testing notes --------
          
          editTextNotes.replaceText("my notes")
          
          imageNotesAction.hasDrawable(R.drawable.ic_checmark)
          
          imageBack.click()
          
          imageNotesAction.hasDrawable(R.drawable.ic_copy)
          editTextNotes.hasEmptyText()
          
          editTextNotes.replaceText("my notes2")
          
          imageNotesAction.click()
          
          editTextNotes.hasText("my notes2")
          
          imageNotesAction.click()
          
          hasClipboardText("my notes2")
          snackbar.isDisplayedWithText("Notes successfully copied")
          
          waitForSnackbarToHide()
          
          // -------- Testing deleting --------
          
          imageDelete.click()
          
          confirmationDialog {
            isDisplayed()
            title.hasText("Delete password")
            message.hasText("Do you want to delete yabcd?")
            action1.hasText("CANCEL")
            action2.hasText("DELETE")
          }
          
          confirmationDialog.hide()
          
          confirmationDialog.isNotDisplayed()
          
          imageDelete.click()
          
          confirmationDialog.action2.click()
        }
        
        currentScreenIs<MainListScreen>()
      }
    }
  }
  
  @Test
  fun testUrlFieldWithOnlyDomain() = init {
    rule.launchActivityWithDatabase(Databases.TwoPasswordsAndNote)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      
      KMainListScreen {
        recycler.emptyChildAt(1) { click() }
        
        KPasswordEntryScreen {
          
          editTextUrl.replaceText("example.com")
          
          imageOpenUrl.click()
          
          flakySafely {
            intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://example.com")))
          }
        }
      }
    }
  }
  
  @Test
  fun testUrlFieldWithFullLink() = init {
    rule.launchActivityWithDatabase(Databases.TwoPasswordsAndNote)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      
      KMainListScreen {
        recycler.emptyChildAt(1) { click() }
        
        KPasswordEntryScreen {
          
          editTextUrl.replaceText("https://example.com")
          
          imageOpenUrl.click()
          
          flakySafely {
            intended(allOf(hasAction(Intent.ACTION_VIEW), hasData("https://example.com")))
          }
        }
      }
    }
  }
}
