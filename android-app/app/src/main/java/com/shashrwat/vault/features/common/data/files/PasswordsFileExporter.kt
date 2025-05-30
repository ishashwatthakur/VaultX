package com.shashrwat.vault.features.common.data.files

import android.content.Context
import android.net.Uri
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.encode
import com.shashrwat.vault.core.DispatchersFacade
import kotlinx.coroutines.withContext

interface PasswordsFileExporter {
  
  suspend fun writeData(exportingUri: Uri, database: KeePassDatabase)
}

class RealPasswordsFileExporter(
  private val context: Context,
  private val dispatchersFacade: DispatchersFacade
) : PasswordsFileExporter {
  
  override suspend fun writeData(exportingUri: Uri, database: KeePassDatabase) {
    withContext(dispatchersFacade.IO) {
      val outputStream = context.contentResolver.openOutputStream(exportingUri, "wr")
      database.encode(requireNotNull(outputStream))
    }
  }
}
