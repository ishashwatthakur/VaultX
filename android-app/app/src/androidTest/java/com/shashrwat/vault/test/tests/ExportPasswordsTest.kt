package com.shashrwat.vault.test.tests

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import com.shashrwat.vault.features.common.AppConstants.CONTENT_TYPE_UNKNOWN
import com.shashrwat.vault.features.common.di.CoreComponentHolder
import com.shashrwat.vault.test.core.base.VaultTestCase
import com.shashrwat.vault.test.core.data.Databases
import com.shashrwat.vault.test.core.di.StubExtraDependenciesFactory
import com.shashrwat.vault.test.core.di.stubs.StubActivityResultWrapper
import com.shashrwat.vault.test.core.di.stubs.StubPasswordsFileExporter
import com.shashrwat.vault.test.core.ext.hasTextColorInt
import com.shashrwat.vault.test.core.ext.launchActivityWithDatabase
import com.shashrwat.vault.test.core.rule.VaultAutotestRule
import com.shashrwat.vault.test.screens.KLoginScreen
import com.shashrwat.vault.test.screens.KMainListScreen
import com.shashrwat.vault.viewbuilding.Colors
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test

class ExportPasswordsTest : VaultTestCase() {
  
  @get:Rule
  val rule = VaultAutotestRule()
  
  private val stubPasswordsFileExporter = StubPasswordsFileExporter()
  
  @Test
  fun testExportingPasswords() = init {
    CoreComponentHolder.initialize(
      application = ApplicationProvider.getApplicationContext(),
      factory = StubExtraDependenciesFactory(
        activityResultWrapper = StubActivityResultWrapper(
          stubSelectPasswordsFileUri = "content://myfolder/passwords.kdbx"
        ),
        passwordsFileExporter = stubPasswordsFileExporter,
      )
    )
    rule.launchActivityWithDatabase(Databases.TwoPasswords)
  }.run {
    KLoginScreen {
      editTextEnterPassword.replaceText("qwetu1233")
      buttonContinue.click()
      KMainListScreen {
        menu {
          open()
          exportPasswordsMenuItem.click()
        }
        infoDialog {
          title.hasText("Done")
          message.hasText("Exported passwords successfully!")
          action1.hasText("OK")
          action2.hasText("SHARE FILE")
          action2.hasTextColorInt(Colors.AccentLight)
          action2.click()
        }
        flakySafely {
          intended(
            allOf(
              hasAction(Intent.ACTION_CHOOSER),
              hasExtra(Intent.EXTRA_TITLE, "Share"),
              hasExtra(
                `is`(Intent.EXTRA_INTENT),
                allOf(
                  hasAction(Intent.ACTION_SEND),
                  hasType(CONTENT_TYPE_UNKNOWN),
                  hasExtra(Intent.EXTRA_STREAM, stubPasswordsFileExporter.exportingUri)
                )
              )
            )
          )
        }
      }
    }
  }
}
