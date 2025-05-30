package com.shashrwat.vault.core.extensions

import android.net.Uri
import android.os.Environment
import java.io.File
import java.net.URLDecoder


fun Uri.toReadablePath(): String {
  val uriString = toString()
  val internalStorageBasePath = if (File("/storage/emulated/0").exists()) {
    "/storage/emulated/0"
  } else {
    Environment.getExternalStorageDirectory().absolutePath.trimEnd('/')
  }
  val filePath = uriString.substring(uriString.lastIndexOf("/") + 1)
  return internalStorageBasePath + "/" + URLDecoder.decode(filePath, "UTF-8")
}
