package com.shashrwat.vault.features.creating_password.actors

import com.shashrwat.vault.core.mvi.tea.Actor
import com.shashrwat.vault.features.creating_password.CreatingPasswordCommand
import com.shashrwat.vault.features.creating_password.CreatingPasswordCommand.CheckPasswordStrength
import com.shashrwat.vault.features.creating_password.CreatingPasswordEvent
import com.shashrwat.vault.features.creating_password.CreatingPasswordEvent.PasswordStrengthChanged
import domain.PasswordChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

class CheckPasswordStrengthActor(
  private val passwordChecker: PasswordChecker
) : Actor<CreatingPasswordCommand, CreatingPasswordEvent> {
  
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun handle(commands: Flow<CreatingPasswordCommand>): Flow<CreatingPasswordEvent> {
    return commands.filterIsInstance<CheckPasswordStrength>()
        .mapLatest {
          PasswordStrengthChanged(passwordChecker.checkStrength(it.password))
        }
  }
}
