package com.shashrwat.vault.core.mvi.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.shashrwat.vault.core.mvi.tea.TeaStore
import kotlinx.coroutines.launch

fun <State : Any, News : Any> TeaStore<State, *, News>.subscribe(
  lifecycleOwner: LifecycleOwner,
  stateCollector: ((State) -> Unit)?,
  newsCollector: ((News) -> Unit)? = null
) {
  val lifecycle = lifecycleOwner.lifecycle
  check(lifecycle.currentState == Lifecycle.State.INITIALIZED)
  with(lifecycleOwner.lifecycleScope) {
    if (stateCollector != null) {
      launch {
        state.flowWithLifecycle(lifecycle, STARTED).collect(stateCollector::invoke)
      }
    }
    if (newsCollector != null) {
      launch {
        news.flowWithLifecycle(lifecycle, RESUMED).collect(newsCollector::invoke)
      }
    }
  }
}
