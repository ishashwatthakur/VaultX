package com.shashrwat.vault.features.settings

import com.shashrwat.vault.core.mvi.tea.TeaStore
import com.shashrwat.vault.core.mvi.tea.TeaStoreImpl
import com.shashrwat.vault.features.common.di.CoreComponent
import com.shashrwat.vault.features.settings.actors.ChangeEnableImagesLoadingActor
import com.shashrwat.vault.features.settings.actors.ChangeShowUsernamesActor
import com.shashrwat.vault.features.settings.actors.ClearImagesCacheActor
import com.shashrwat.vault.features.settings.actors.DisableBiometricsActor
import com.shashrwat.vault.features.settings.actors.EnableBiometricsActor
import com.shashrwat.vault.features.settings.actors.GetBiometricsAvailableActor
import com.shashrwat.vault.features.settings.actors.GetBiometricsEnabledActor
import com.shashrwat.vault.features.settings.actors.GetImagesLoadingEnabledActor
import com.shashrwat.vault.features.settings.actors.GetShowUsernamesActor
import com.shashrwat.vault.features.settings.actors.GetStorageBackupEnabledActor
import com.shashrwat.vault.features.settings.actors.SettingsRouterActor
import com.shashrwat.vault.features.settings.actors.StorageBackupActor

fun SettingsStore(
  coreComponent: CoreComponent,
): TeaStore<SettingsState, SettingsUiEvent, SettingsNews> {
  return TeaStoreImpl(
    actors = listOf(
      GetStorageBackupEnabledActor(
        coreComponent.storageBackupPreferences,
        coreComponent.dateTimeFormatter
      ),
      GetShowUsernamesActor(coreComponent.showUsernamesInteractor),
      GetBiometricsAvailableActor(coreComponent.biometricsAvailabilityProvider),
      GetBiometricsEnabledActor(coreComponent.biometricsEnabledProvider),
      GetImagesLoadingEnabledActor(coreComponent.imagesLoadingEnabledInteractor),
      ChangeShowUsernamesActor(coreComponent.showUsernamesInteractor),
      EnableBiometricsActor(
        coreComponent.masterPasswordProvider,
        coreComponent.biometricsAllowedManager,
        coreComponent.biometricsStorage
      ),
      DisableBiometricsActor(coreComponent.biometricsStorage),
      StorageBackupActor(
        coreComponent.storageBackupPreferences,
        coreComponent.masterPasswordProvider,
        coreComponent.observableCachedDatabaseStorage,
        coreComponent.storageBackupInteractor
      ),
      ClearImagesCacheActor(
        coreComponent.imagesCache,
        coreComponent.imagesNamesLoader,
        coreComponent.reloadImagesObserver
      ),
      ChangeEnableImagesLoadingActor(coreComponent.imagesLoadingEnabledInteractor),
      SettingsRouterActor(coreComponent.router),
    ),
    reducer = SettingsReducer(),
    initialState = SettingsState()
  )
}

