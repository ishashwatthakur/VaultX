package com.shashrwat.vault.features.common.di.modules

import android.app.Application
import com.shashrwat.vault.core.DefaultDispatchersFacade
import com.shashrwat.vault.core.DispatchersFacade
import com.shashrwat.vault.features.common.domain.Clipboard
import com.shashrwat.vault.features.common.domain.ClipboardImpl
import com.shashrwat.vault.features.common.domain.DateTimeFormatter
import com.shashrwat.vault.features.common.domain.DefaultTimestampProvider
import com.shashrwat.vault.features.common.domain.SimpleDateTimeFormatter
import com.shashrwat.vault.features.common.domain.TimestampProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

interface CoreModule {
  val application: Application
  val dispatchers: DispatchersFacade
  val globalIOScope: CoroutineScope
  val clipboard: Clipboard
  val timestampProvider: TimestampProvider
  val dateTimeFormatter: DateTimeFormatter
}

class CoreModuleImpl(override val application: Application) : CoreModule {
  override val dispatchers = DefaultDispatchersFacade
  override val globalIOScope = CoroutineScope(SupervisorJob() + dispatchers.IO)
  override val clipboard = ClipboardImpl(application)
  override val timestampProvider = DefaultTimestampProvider()
  override val dateTimeFormatter = SimpleDateTimeFormatter(timestampProvider)
}
