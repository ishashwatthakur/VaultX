package com.shashrwat.vault.test.core.views.dialog

import android.widget.LinearLayout
import com.shashrwat.vault.R
import io.github.kakaocup.kakao.common.views.KBaseView

class KEntryTypeDialog(
  private val id: Int
) : KBaseView<KEntryTypeDialog>({ withId(id) }) {
  
  val passwordEntry = KEntryTypeItemView(id, R.drawable.ic_lock, "Password")
  val noteEntry = KEntryTypeItemView(id, R.drawable.ic_note, "Note")
  
  class KEntryTypeItemView(
    private val bottomSheetId: Int,
    private val imageRes: Int,
    private val text: String,
  ) : KBaseView<KEntryTypeItemView>({
    isInstanceOf(LinearLayout::class.java)
    isDescendantOfA { withId(bottomSheetId) }
    withDescendant { withDrawable(imageRes) }
    withDescendant { withText(text) }
  })
}
