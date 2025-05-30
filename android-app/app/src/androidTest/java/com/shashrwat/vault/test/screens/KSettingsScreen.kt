package com.shashrwat.vault.test.screens

import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.shashrwat.vault.R
import com.shashrwat.vault.core.views.SettingsItem.Companion.TextDescription
import com.shashrwat.vault.features.settings.SettingsScreen
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemChangePassword
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemImagesLoading
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemShowUsernames
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemStorageBackup
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemStorageBackupFolder
import com.shashrwat.vault.features.settings.SettingsScreen.Companion.ItemStorageBackupNow
import com.shashrwat.vault.test.core.base.BaseScreen
import com.shashrwat.vault.test.core.views.dialog.KEnterPasswordDialog
import com.shashrwat.vault.test.core.views.snackbar.KSnackbar
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.switch.KSwitch
import io.github.kakaocup.kakao.text.KTextView

object KSettingsScreen : BaseScreen<KSettingsScreen>() {
  
  override val viewClass = SettingsScreen::class.java
  
  val imageBack = KImageView { withDrawable(R.drawable.ic_back) }
  val itemChangePassword = KView { withId(ItemChangePassword) }
  val switchShowUsernames = KSwitch {
    withParent { withId(ItemShowUsernames) }
    isInstanceOf(SwitchCompat::class.java)
  }
  val switchStorageBackup = KSwitch {
    withParent { withId(ItemStorageBackup) }
    isInstanceOf(SwitchCompat::class.java)
  }
  val textBackupFolder = KTextView {
    withParent { withId(ItemStorageBackupFolder) }
    withId(TextDescription)
    isInstanceOf(TextView::class.java)
  }
  val itemStorageBackupNow = KView { withId(ItemStorageBackupNow) }
  val textLatestBackupDate = KTextView {
    withParent { withId(ItemStorageBackupNow) }
    withId(TextDescription)
    isInstanceOf(TextView::class.java)
  }
  val switchImagesLoading = KSwitch {
    withParent { withId(ItemImagesLoading) }
    isInstanceOf(SwitchCompat::class.java)
  }
  val enterPasswordDialog = KEnterPasswordDialog()
  val snackbar = KSnackbar()
}
