package com.shashrwat.vault.features.settings.actors

import android.net.Uri
import com.shashrwat.vault.core.mvi.tea.Actor
import com.shashrwat.vault.features.common.data.files.StorageBackupPreferences
import com.shashrwat.vault.features.common.domain.DateTimeFormatter
import com.shashrwat.vault.features.settings.SettingsCommand
import com.shashrwat.vault.features.settings.SettingsCommand.FetchData
import com.shashrwat.vault.features.settings.SettingsCommand.FetchStorageBackupInfo
import com.shashrwat.vault.features.settings.SettingsEvent
import com.shashrwat.vault.features.settings.SettingsEvent.ReceivedStorageBackupEnabled
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest

class GetStorageBackupEnabledActor(
  private val preferences: StorageBackupPreferences,
  private val dateTimeFormatter: DateTimeFormatter
) : Actor<SettingsCommand, SettingsEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<SettingsCommand>): Flow<SettingsEvent> {
    return commands.filter { it is FetchData || it is FetchStorageBackupInfo }
        .mapLatest {
          val (enabled, backupFolder) = preferences.getStorageBackupEnabledPair()
          val timestamp = preferences.getLatestBackupTimestamp()
          val latestBackupDate = if (timestamp != 0L) {
            dateTimeFormatter.formatReadable(timestamp)
          } else {
            null
          }
          ReceivedStorageBackupEnabled(enabled, backupFolder?.let(Uri::parse), latestBackupDate)
        }
  }
}