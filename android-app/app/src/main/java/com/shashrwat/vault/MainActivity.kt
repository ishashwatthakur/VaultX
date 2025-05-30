package com.shashrwat.vault

import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shashrwat.vault.core.views.RootView
import com.shashrwat.vault.features.common.Screens
import com.shashrwat.vault.features.common.di.ActivityComponent
import com.shashrwat.vault.features.common.di.CoreComponentHolder.coreComponent
import viewdsl.Size.Companion.MatchParent
import viewdsl.ViewDslConfiguration
import viewdsl.id
import viewdsl.size
import viewdsl.withViewBuilder

class MainActivity : AppCompatActivity() {
  
  private val mainActivityLayout
    get() = withViewBuilder {
      RootView(context).apply {
        id(rootViewId)
        size(MatchParent, MatchParent)
        fitsSystemWindows = true
      }
    }
  
  private var _activityComponent: ActivityComponent? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ViewDslConfiguration.initializeResources(resources)
    @Suppress("DEPRECATION")
    window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
        or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    setContentView(mainActivityLayout)
    _activityComponent =
        ActivityComponent.create(coreComponent, rootViewId, this)
    coreComponent.imagesNamesLoaderNetworkNotifier.notifyAboutNetworkAvailability(lifecycleScope)
    figureOutScreenToGo()
  }
  
  private fun figureOutScreenToGo() {
    if (coreComponent.databaseFileSaver.doesDatabaseExist()) {
      coreComponent.router.switchToNewRoot(Screens.LoginScreen)
    } else {
      coreComponent.router.switchToNewRoot(Screens.InitialScreen)
    }
  }
  
  override fun onResume() {
    super.onResume()
    val navigator = checkNotNull(_activityComponent).navigator
    coreComponent.navigationController.getNavigatorHolder().setNavigator(navigator)
  }
  
  override fun onPause() {
    super.onPause()
    coreComponent.navigationController.getNavigatorHolder().removeNavigator()
  }
  
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    if (!checkNotNull(_activityComponent).navigator.handleGoBack()) {
      super.onBackPressed()
    }
  }
  
  override fun onDestroy() {
    super.onDestroy()
    _activityComponent = null
  }
  
  private companion object {
    
    val rootViewId = View.generateViewId()
  }
}
