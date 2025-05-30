package com.shashrwat.vault.test.core.rule

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.shashrwat.vault.MainActivity
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class VaultAutotestRule(
  private val autoLaunch: Boolean = false
) : TestRule {
  
  private val initializeCoreComponentRule = InitializeCoreComponentRule()
  private val disableAnimationsRule = DisableAnimationsRule()
  private val intentsRule = IntentsRule()
  private val deleteFilesRule = DeleteFilesRule()
  private val clearPreferencesRule = ClearPreferencesRule()
  private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
  
  private var scenario: ActivityScenario<MainActivity>? = null
  
  fun launchActivity() {
    scenario = ActivityScenario.launch(MainActivity::class.java)
  }
  
  fun finishActivity() {
    scenario?.close()
  }
  
  override fun apply(base: Statement, description: Description): Statement {
    var chain = RuleChain
        .outerRule(initializeCoreComponentRule)
        .around(intentsRule)
        .around(disableAnimationsRule)
        .around(deleteFilesRule)
        .around(clearPreferencesRule)
    if (autoLaunch) {
      chain = chain.around(activityScenarioRule)
    }
    return chain.apply(base, description)
  }
}
