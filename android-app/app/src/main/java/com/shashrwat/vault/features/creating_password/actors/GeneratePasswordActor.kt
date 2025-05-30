package com.shashrwat.vault.features.creating_password.actors

import com.shashrwat.vault.core.mvi.tea.Actor
import com.shashrwat.vault.features.creating_password.CreatingPasswordCommand
import com.shashrwat.vault.features.creating_password.CreatingPasswordCommand.GeneratePassword
import com.shashrwat.vault.features.creating_password.CreatingPasswordEvent
import com.shashrwat.vault.features.creating_password.CreatingPasswordEvent.GeneratedPassword
import domain.generator.PasswordGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class GeneratePasswordActor(
  private val passwordGenerator: PasswordGenerator
) : Actor<CreatingPasswordCommand, CreatingPasswordEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<CreatingPasswordCommand>): Flow<CreatingPasswordEvent> {
    return commands.filterIsInstance<GeneratePassword>()
        .mapLatest {
          val password = passwordGenerator.generatePassword(it.length, it.characteristics)
          GeneratedPassword(password)
        }
  }
}
