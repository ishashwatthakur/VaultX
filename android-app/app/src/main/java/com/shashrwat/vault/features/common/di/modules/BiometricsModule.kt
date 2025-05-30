package com.shashrwat.vault.features.common.di.modules

import com.shashrwat.vault.BuildConfig
import com.shashrwat.vault.features.common.AppConfig
import com.shashrwat.vault.features.common.biometrics.BiometricsAllowedManager
import com.shashrwat.vault.features.common.biometrics.BiometricsAllowedManagerImpl
import com.shashrwat.vault.features.common.biometrics.BiometricsAvailabilityProvider
import com.shashrwat.vault.features.common.biometrics.BiometricsAvailabilityProviderImpl
import com.shashrwat.vault.features.common.biometrics.BiometricsCipherProvider
import com.shashrwat.vault.features.common.biometrics.BiometricsCipherProviderImpl
import com.shashrwat.vault.features.common.biometrics.BiometricsEnabledProvider
import com.shashrwat.vault.features.common.biometrics.BiometricsStorage
import com.shashrwat.vault.features.common.biometrics.BiometricsStorageImpl

interface BiometricsModule {
  val biometricsAvailabilityProvider: BiometricsAvailabilityProvider
  val biometricsEnabledProvider: BiometricsEnabledProvider
  val biometricsStorage: BiometricsStorage
  val biometricsCipherProvider: BiometricsCipherProvider
  val biometricsAllowedManager: BiometricsAllowedManager
}

class BiometricsModuleImpl(
  coreModule: CoreModule,
  preferencesModule: PreferencesModule
) : BiometricsModule {
  
  override val biometricsAvailabilityProvider =
      BiometricsAvailabilityProviderImpl(coreModule.application)
  
  private val biometricsStorageImpl =
      BiometricsStorageImpl(preferencesModule.biometricsDataPreferences)
  
  override val biometricsEnabledProvider = biometricsStorageImpl
  
  override val biometricsStorage = biometricsStorageImpl
  
  private val timeSinceLastPasswordEnterThreshold = if (BuildConfig.DEBUG) {
    AppConfig.Debug.MaxTimeSinceLastPasswordEnter
  } else {
    AppConfig.Release.MaxTimeSinceLastPasswordEnter
  }
  
  private val maxSuccessiveBiometricsEnters = if (BuildConfig.DEBUG) {
    AppConfig.Debug.MaxSuccessiveBiometricsEnters
  } else {
    AppConfig.Release.MaxSuccessiveBiometricsEnters
  }
  
  override val biometricsAllowedManager = BiometricsAllowedManagerImpl(
    coreModule.timestampProvider,
    preferencesModule.biometricsMetadataPreferences,
    timeSinceLastPasswordEnterThreshold,
    maxSuccessiveBiometricsEnters,
  )
  
  override val biometricsCipherProvider = BiometricsCipherProviderImpl()
}
