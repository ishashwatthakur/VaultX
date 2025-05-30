package com.shashrwat.vault.features.common.dialogs

import android.view.View
import android.view.ViewGroup
import com.shashrwat.vault.core.views.MaterialProgressBar
import com.shashrwat.vault.core.views.SimpleDialog
import com.shashrwat.vault.viewbuilding.Dimens.ProgressBarSizeBig
import navigation.BaseFragmentScreen
import viewdsl.Size.Companion.MatchParent
import viewdsl.addView
import viewdsl.id
import viewdsl.size
import viewdsl.tag
import viewdsl.withViewBuilder

const val DialogProgressBar = "DialogProgressBar"

val BaseFragmentScreen.loadingDialog get() = viewAs<SimpleDialog>(DialogProgressBar)

fun ViewGroup.LoadingDialog(block: SimpleDialog.() -> Unit = {}) = withViewBuilder {
  addView {
    SimpleDialog(context).apply {
      isCancellable = false
      size(MatchParent, MatchParent)
      id(View.generateViewId())
      tag(DialogProgressBar)
      child<MaterialProgressBar>(ProgressBarSizeBig, ProgressBarSizeBig)
      apply(block)
    }
  }
}
