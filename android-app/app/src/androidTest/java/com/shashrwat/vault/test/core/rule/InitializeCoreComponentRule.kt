package com.shashrwat.vault.test.core.rule

import androidx.test.core.app.ApplicationProvider
import com.shashrwat.vault.features.common.di.CoreComponentHolder
import com.shashrwat.vault.test.core.di.StubExtraDependenciesFactory
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InitializeCoreComponentRule : TestRule {
  
  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        CoreComponentHolder.initialize(
          ApplicationProvider.getApplicationContext(),
          StubExtraDependenciesFactory()
        )
        base.evaluate()
      }
    }
  }
}
