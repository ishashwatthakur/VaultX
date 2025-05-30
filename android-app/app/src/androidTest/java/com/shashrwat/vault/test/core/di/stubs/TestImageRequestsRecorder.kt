package com.shashrwat.vault.test.core.di.stubs

import com.shashrwat.vault.features.common.domain.ImageRequestsRecorder

class TestImageRequestsRecorder : ImageRequestsRecorder {
  
  private val records = HashMap<Int, String>()
  
  override fun recordUrlRequest(imageId: Int, url: String) {
    records[imageId] = url
  }
  
  fun getRequestForImage(imageId: Int): String? {
    return records[imageId]
  }
}
