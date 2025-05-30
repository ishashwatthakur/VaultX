package com.shashrwat.vault.features.master_password.actors

import com.shashrwat.vault.core.mvi.tea.Actor
import com.shashrwat.vault.features.master_password.MasterPasswordCommand
import com.shashrwat.vault.features.master_password.MasterPasswordCommand.CreateNewMasterPassword
import com.shashrwat.vault.features.master_password.MasterPasswordEvent
import com.shashrwat.vault.features.master_password.MasterPasswordEvent.FinishedMasterPasswordSaving
import domain.DatabaseInitializer
import domain.MasterPasswordHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class CreateNewMasterPasswordActor(
  private val databaseInitializer: DatabaseInitializer
) : Actor<MasterPasswordCommand, MasterPasswordEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<MasterPasswordCommand>): Flow<MasterPasswordEvent> {
    return commands.filterIsInstance<CreateNewMasterPassword>()
        .mapLatest { command ->
          databaseInitializer.initializeDatabase(command.password)
          MasterPasswordHolder.setMasterPassword(command.password)
          return@mapLatest FinishedMasterPasswordSaving
        }
  }
}
