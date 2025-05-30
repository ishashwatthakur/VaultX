package com.shashrwat.vault.test.core.views.checkmark

import androidx.test.espresso.assertion.ViewAssertions
import com.shashrwat.vault.core.views.Checkmark
import com.shashrwat.vault.test.core.base.baseMatcher
import io.github.kakaocup.kakao.common.builders.ViewBuilder
import io.github.kakaocup.kakao.common.views.KBaseView

class KCheckmark(builder: ViewBuilder.() -> Unit) : KBaseView<KCheckmark>(builder) {
  
  fun isChecked() {
    view.check(
      ViewAssertions.matches(
        baseMatcher(
          descriptionText = "is checked",
          matcher = Checkmark::isChecked
        )
      )
    )
  }
  
  fun isNotChecked() {
    view.check(
      ViewAssertions.matches(
        baseMatcher<Checkmark>(
          descriptionText = "is not checked",
          matcher = { !it.isChecked }
        )
      )
    )
  }
}
