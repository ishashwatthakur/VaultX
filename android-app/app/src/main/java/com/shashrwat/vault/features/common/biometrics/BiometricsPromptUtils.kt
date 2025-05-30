/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shashrwat.vault.features.common.biometrics

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import navigation.BaseFragmentScreen
import javax.crypto.Cipher

object BiometricsPromptUtils {
  
  fun createBiometricPrompt(
    screen: BaseFragmentScreen,
    onSuccess: (Cipher) -> Unit,
    onCancelled: () -> Unit
  ): BiometricPrompt {
    val activity = screen.requireActivity() as AppCompatActivity
    
    val executor = ContextCompat.getMainExecutor(activity)
    
    val callback = object : BiometricPrompt.AuthenticationCallback() {
      
      override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errCode, errString)
        onCancelled()
      }
      
      override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        onCancelled()
      }
      
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        result.cryptoObject?.cipher?.let(onSuccess)
      }
    }
    return BiometricPrompt(activity, executor, callback)
  }
  
  fun createPromptInfo(
    title: CharSequence,
    negativeText: CharSequence,
  ): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder().apply {
      setTitle(title)
      setNegativeButtonText(negativeText)
      setConfirmationRequired(false)
    }.build()
  }
}
