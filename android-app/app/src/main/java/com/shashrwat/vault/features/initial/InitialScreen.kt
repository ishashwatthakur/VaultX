package com.shashrwat.vault.features.initial

import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.View
import com.shashrwat.vault.R
import com.shashrwat.vault.features.common.AppConstants.MIME_TYPE_ALL
import com.shashrwat.vault.features.common.Screens.ImportPasswordsScreen
import com.shashrwat.vault.features.common.Screens.MasterPasswordScreen
import com.shashrwat.vault.features.common.di.CoreComponentHolder.coreComponent
import com.shashrwat.vault.features.master_password.MasterPasswordScreenMode.CREATING_NEW
import com.shashrwat.vault.viewbuilding.Dimens.ImageLogoSize
import com.shashrwat.vault.viewbuilding.Dimens.MarginExtraLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginNormal
import com.shashrwat.vault.viewbuilding.Styles.BaseTextView
import com.shashrwat.vault.viewbuilding.Styles.BoldTextView
import com.shashrwat.vault.viewbuilding.Styles.Button
import com.shashrwat.vault.viewbuilding.TextSizes
import navigation.BaseFragmentScreen
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.gravity
import viewdsl.id
import viewdsl.image
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.margins
import viewdsl.onClick
import viewdsl.rotate
import viewdsl.text
import viewdsl.textSize
import viewdsl.withViewBuilder

class InitialScreen : BaseFragmentScreen() {
  
  override fun buildLayout(context: Context) = context.withViewBuilder {
    RootFrameLayout {
      setBackgroundResource(R.drawable.bg_initial_screen)
      VerticalLayout(MatchParent, WrapContent) {
        layoutGravity(CENTER)
        gravity(CENTER)
        ImageView(ImageLogoSize, ImageLogoSize) {
          image(R.mipmap.ic_launcher)
          onClick { rotate() }
        }
        TextView(WrapContent, WrapContent, style = BoldTextView) {
          margins(top = MarginNormal)
          text(R.string.app_name)
          textSize(TextSizes.MainHeader)
        }
        TextView(WrapContent, WrapContent, style = BaseTextView) {
          margin(MarginExtraLarge)
          gravity(CENTER)
          text(R.string.text_welcome_description)
          textSize(TextSizes.H3)
        }
      }
      VerticalLayout(MatchParent, WrapContent) {
        margin(MarginNormal)
        layoutGravity(BOTTOM)
        TextView(MatchParent, WrapContent, style = Button()) {
          id(ButtonCreateMasterPassword)
          text(R.string.text_create_new_encrypted_vault)
          margins(bottom = MarginNormal)
          onClick {
            coreComponent.router.goForward(MasterPasswordScreen(CREATING_NEW))
          }
        }
        TextView(MatchParent, WrapContent, style = Button()) {
          id(ButtonImportPasswords)
          text(R.string.text_import_existing_vault)
          onClick { importFileLauncher.launch(MIME_TYPE_ALL) }
        }
      }
    }
  }
  
  private val importFileLauncher = coreComponent.activityResultWrapper
      .wrapSelectPasswordsFileLauncher(this@InitialScreen) { uri ->
        coreComponent.router.goForward(
          screenInfo = ImportPasswordsScreen(uri, askForConfirmation = false),
          animate = false
        )
      }
  
  companion object {
    
    val ButtonCreateMasterPassword = View.generateViewId()
    val ButtonImportPasswords = View.generateViewId()
  }
}
