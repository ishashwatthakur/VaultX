package com.shashrwat.vault.features.master_password.actors

import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.modifiers.modifyCredentials
import com.shashrwat.vault.core.mvi.tea.Actor
import com.shashrwat.vault.features.common.Durations
import com.shashrwat.vault.features.common.data.database.ObservableCachedDatabaseStorage
import com.shashrwat.vault.features.common.domain.ChangeMasterPasswordObserver
import com.shashrwat.vault.features.common.domain.MasterPasswordProvider
import com.shashrwat.vault.features.common.domain.StorageBackupInteractor
import com.shashrwat.vault.features.master_password.MasterPasswordCommand
import com.shashrwat.vault.features.master_password.MasterPasswordCommand.ChangeExistingMasterPassword
import com.shashrwat.vault.features.master_password.MasterPasswordEvent
import com.shashrwat.vault.features.master_password.MasterPasswordEvent.FinishedMasterPasswordSaving
import domain.MasterPasswordHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class ChangeExistingMasterPasswordActor(
  private val masterPasswordProvider: MasterPasswordProvider,
  private val storage: ObservableCachedDatabaseStorage,
  private val backupInteractor: StorageBackupInteractor,
  private val changeMasterPasswordObserver: ChangeMasterPasswordObserver,
) : Actor<MasterPasswordCommand, MasterPasswordEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<MasterPasswordCommand>): Flow<MasterPasswordEvent> {
    return commands.filterIsInstance<ChangeExistingMasterPassword>()
        .mapLatest { command ->
          delay(Durations.StubDelay)
          val newMasterPassword = command.password
          val currentMasterPassword = masterPasswordProvider.provideMasterPassword()
          require(currentMasterPassword != newMasterPassword)
          val currentDatabase = storage.getDatabase(currentMasterPassword)
          val newDatabase = currentDatabase
              .modifyCredentials { Credentials.from(newMasterPassword.encryptedValueField) }
          storage.saveDatabase(newDatabase)
          backupInteractor.forceBackup(newDatabase)
          MasterPasswordHolder.setMasterPassword(newMasterPassword)
          changeMasterPasswordObserver.sendMasterPasswordChangedEvent()
          FinishedMasterPasswordSaving
        }
  }
}