package com.shashrwat.vault.core

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Dispatchers for coroutines
 */
interface DispatchersFacade {
  
  val IO: CoroutineDispatcher
  val SingleThread: CoroutineDispatcher
  val Default: CoroutineDispatcher
  val Main: CoroutineDispatcher
}
