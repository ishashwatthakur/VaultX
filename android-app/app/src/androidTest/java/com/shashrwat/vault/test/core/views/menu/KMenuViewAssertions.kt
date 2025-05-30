package com.shashrwat.vault.test.core.views.menu

import androidx.test.espresso.assertion.ViewAssertions
import com.shashrwat.vault.core.views.menu.MenuContentView
import com.shashrwat.vault.test.core.base.baseMatcher
import io.github.kakaocup.kakao.common.assertions.BaseAssertions

interface KMenuViewAssertions : BaseAssertions {
  
  fun isOpened() {
    view.check(
      ViewAssertions.matches(
        baseMatcher<MenuContentView>(
          descriptionText = "checking that menu is opened",
          matcher = { view -> view.opened }
        )
      )
    )
  }
  
  fun isClosed() {
    view.check(
      ViewAssertions.matches(
        baseMatcher<MenuContentView>(
          descriptionText = "checking that menu is closed",
          matcher = { view -> !view.opened }
        )
      )
    )
  }
}
