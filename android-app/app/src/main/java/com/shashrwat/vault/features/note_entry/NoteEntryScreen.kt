package com.shashrwat.vault.features.note_entry

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.EditText
import com.shashrwat.vault.R
import com.shashrwat.vault.core.extensions.getDeleteMessageText
import com.shashrwat.vault.core.extensions.stringNullableArg
import com.shashrwat.vault.core.mvi.ext.subscribe
import com.shashrwat.vault.core.mvi.ext.viewModelStore
import com.shashrwat.vault.core.views.Snackbar.Companion.Snackbar
import com.shashrwat.vault.core.views.Snackbar.Companion.snackbar
import com.shashrwat.vault.core.views.Snackbar.Type.CHECKMARK
import com.shashrwat.vault.features.common.Durations
import com.shashrwat.vault.features.common.TextState
import com.shashrwat.vault.features.common.di.CoreComponentHolder.coreComponent
import com.shashrwat.vault.features.common.dialogs.InfoDialog.Companion.InfoDialog
import com.shashrwat.vault.features.common.dialogs.InfoDialog.Companion.infoDialog
import com.shashrwat.vault.features.common.dialogs.LoadingDialog
import com.shashrwat.vault.features.common.dialogs.loadingDialog
import com.shashrwat.vault.features.common.model.NoteItem
import com.shashrwat.vault.features.note_entry.NoteEntryNews.ShowNoteEntryCreated
import com.shashrwat.vault.features.note_entry.NoteEntryNews.ShowTextCopied
import com.shashrwat.vault.features.note_entry.NoteEntryNews.ShowTitleCopied
import com.shashrwat.vault.features.note_entry.NoteEntryState.ExistingEntry
import com.shashrwat.vault.features.note_entry.NoteEntryState.NewEntry
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnBackPressed
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnConfirmedDeleting
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnDeleteClicked
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnDialogHidden
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnFavoriteClicked
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnInit
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnSaveClicked
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnTextActionClicked
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnTextChanged
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnTitleActionClicked
import com.shashrwat.vault.features.note_entry.NoteEntryUiEvent.OnTitleChanged
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Dimens.CircleButtonSize
import com.shashrwat.vault.viewbuilding.Dimens.GradientDrawableHeight
import com.shashrwat.vault.viewbuilding.Dimens.IconPadding
import com.shashrwat.vault.viewbuilding.Dimens.MarginLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginMedium
import com.shashrwat.vault.viewbuilding.Dimens.MarginNormal
import com.shashrwat.vault.viewbuilding.Dimens.MarginSmall
import com.shashrwat.vault.viewbuilding.Styles
import com.shashrwat.vault.viewbuilding.Styles.AccentTextView
import com.shashrwat.vault.viewbuilding.Styles.BaseEditText
import com.shashrwat.vault.viewbuilding.Styles.BoldTextView
import com.shashrwat.vault.viewbuilding.TextSizes
import navigation.BaseFragmentScreen
import viewdsl.BaseTextWatcher
import viewdsl.Size.Companion.WrapContent
import viewdsl.Size.Companion.ZERO
import viewdsl.Size.IntSize
import viewdsl.circleRippleBackground
import viewdsl.constraints
import viewdsl.gone
import viewdsl.hideKeyboard
import viewdsl.id
import viewdsl.image
import viewdsl.imageTint
import viewdsl.isVisible
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.margins
import viewdsl.onClick
import viewdsl.onSubmit
import viewdsl.padding
import viewdsl.paddings
import viewdsl.setTextSilently
import viewdsl.showKeyboard
import viewdsl.text
import viewdsl.textColor
import viewdsl.textSize
import viewdsl.withViewBuilder

class NoteEntryScreen : BaseFragmentScreen() {
  
  override fun buildLayout(context: Context) = context.withViewBuilder {
    RootFrameLayout {
      id(NoteScreenRoot)
      ScrollableConstraintLayout {
        paddings(bottom = MarginNormal + CircleButtonSize)
        ImageView(WrapContent, WrapContent, style = Styles.ImageBack) {
          id(ImageBack)
          margins(start = MarginSmall, top = StatusBarHeight + MarginMedium)
          onClick { store.tryDispatch(OnBackPressed) }
          constraints {
            topToTopOf(parent)
            startToStartOf(parent)
          }
        }
        TextView(WrapContent, WrapContent, style = BoldTextView) {
          id(MainTitle)
          text(R.string.text_new_note)
          textSize(TextSizes.H1)
          margins(start = MarginNormal)
          constraints {
            startToEndOf(ImageBack)
            topToTopOf(ImageBack)
          }
        }
        ImageView(WrapContent, WrapContent) {
          id(ImageDelete)
          padding(IconPadding)
          margins(start = MarginNormal, end = MarginNormal)
          image(R.drawable.ic_delete)
          imageTint(Colors.Error)
          circleRippleBackground(Colors.ErrorRipple)
          onClick { store.tryDispatch(OnDeleteClicked) }
          constraints {
            topToTopOf(MainTitle)
            endToEndOf(parent)
            bottomToBottomOf(MainTitle)
          }
        }
        ImageView(WrapContent, WrapContent) {
          id(ImageFavorite)
          image(R.drawable.ic_star_outline)
          imageTint(Colors.Favorite)
          padding(IconPadding)
          margins(end = MarginSmall)
          circleRippleBackground(Colors.FavoriteRipple)
          onClick { store.tryDispatch(OnFavoriteClicked) }
          constraints {
            topToTopOf(ImageDelete)
            endToStartOf(ImageDelete)
          }
        }
        TextView(WrapContent, WrapContent, style = AccentTextView) {
          id(Title)
          margins(start = MarginNormal, top = GradientDrawableHeight)
          text(R.string.text_title)
          constraints {
            topToTopOf(parent)
            startToStartOf(parent)
          }
        }
        EditText(ZERO, WrapContent, style = BaseEditText(hint = R.string.text_enter_title)) {
          id(EditTextTitle)
          margins(start = MarginNormal, end = MarginNormal)
          onSubmit { editText(EditTextText).requestFocus() }
          addTextChangedListener(titleTextWatcher)
          constraints {
            topToBottomOf(Title)
            startToStartOf(parent)
            endToStartOf(ImageTitleAction)
          }
        }
        ImageView(WrapContent, WrapContent) {
          id(ImageTitleAction)
          image(R.drawable.ic_copy)
          padding(IconPadding)
          margins(end = MarginNormal)
          circleRippleBackground(rippleColor = Colors.Ripple)
          onClick { store.tryDispatch(OnTitleActionClicked) }
          constraints {
            topToTopOf(EditTextTitle)
            bottomToBottomOf(EditTextTitle)
            endToEndOf(parent)
          }
        }
        TextView(WrapContent, WrapContent, style = AccentTextView) {
          id(TitleText)
          margins(start = MarginNormal, top = MarginLarge)
          text(R.string.text_text)
          constraints {
            topToBottomOf(EditTextTitle)
            startToStartOf(parent)
          }
        }
        EditText(ZERO, WrapContent, style = BaseEditText(hint = R.string.text_enter_text)) {
          id(EditTextText)
          isSingleLine = false
          margins(start = MarginNormal, end = MarginNormal)
          onSubmit { store.tryDispatch(OnSaveClicked) }
          addTextChangedListener(textTextWatcher)
          constraints {
            topToBottomOf(TitleText)
            startToStartOf(parent)
            endToStartOf(ImageTextAction)
          }
        }
        ImageView(WrapContent, WrapContent) {
          id(ImageTextAction)
          image(R.drawable.ic_copy)
          padding(IconPadding)
          margins(end = MarginNormal)
          circleRippleBackground(rippleColor = Colors.Ripple)
          onClick { store.tryDispatch(OnTextActionClicked) }
          constraints {
            topToTopOf(EditTextText)
            endToEndOf(parent)
          }
        }
      }
      ImageView(
        width = IntSize(CircleButtonSize),
        height = IntSize(CircleButtonSize),
        style = Styles.CircleCheckmarkButton
      ) {
        id(ButtonSave)
        gone()
        margin(MarginNormal)
        onClick { store.tryDispatch(OnSaveClicked) }
        layoutGravity(Gravity.BOTTOM or Gravity.END)
      }
      InfoDialog()
      LoadingDialog()
      Snackbar {
        layoutGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        margin(MarginNormal)
      }
    }
  }
  
  private val titleTextWatcher = BaseTextWatcher { store.tryDispatch(OnTitleChanged(it)) }
  private val textTextWatcher = BaseTextWatcher { store.tryDispatch(OnTextChanged(it)) }
  
  private val store by viewModelStore {
    NoteEntryStore(coreComponent, stringNullableArg(NoteItem::class.qualifiedName!!))
  }
  
  override fun onInit() {
    store.subscribe(this, ::render, ::handleNews)
    store.tryDispatch(OnInit)
  }
  
  override fun onAppearedOnScreen() {
    if (stringNullableArg(NoteItem::class.qualifiedName!!) == null) {
      requireView().postDelayed({
        viewAsNullable<EditText>(EditTextTitle)?.apply {
          requestFocus()
          requireContext().showKeyboard(this)
        }
      }, Durations.DelayOpenKeyboard)
    }
  }
  
  private fun render(state: NoteEntryState) {
    view(ButtonSave).isVisible = state is NewEntry
    view(ImageTitleAction).isVisible = state is ExistingEntry
    view(ImageTextAction).isVisible = state is ExistingEntry
    view(ImageFavorite).isVisible = state is ExistingEntry
    view(ImageDelete).isVisible = state is ExistingEntry
    val resId = if (state is NewEntry) R.string.text_new_note else R.string.text_note
    textView(MainTitle).text(resId)
    when (state) {
      is NewEntry -> renderNewEntry(state)
      is ExistingEntry -> renderExistingEntry(state)
    }
  }
  
  private fun renderNewEntry(state: NewEntry) {
    imageView(ImageFavorite).gone()
    editText(EditTextTitle).setTextSilently(state.title, titleTextWatcher)
    editText(EditTextText).setTextSilently(state.text, textTextWatcher)
    if (state.showTitleIsEmptyError) {
      showTitleViewError()
    } else {
      showTitleViewDefault()
    }
  }
  
  private fun renderExistingEntry(state: ExistingEntry) {
    if (state.noteEntry?.isFavorite == true) {
      imageView(ImageFavorite).image(R.drawable.ic_star_filled)
    } else {
      imageView(ImageFavorite).image(R.drawable.ic_star_outline)
    }
    editText(EditTextTitle).setTextSilently(state.titleState.editedText, titleTextWatcher)
    editText(EditTextText).setTextSilently(state.textState.editedText, textTextWatcher)
    renderActionIcons(EditTextTitle, state.titleState, ImageTitleAction)
    renderActionIcons(EditTextText, state.textState, ImageTextAction)
    if (!state.isEditingSomething) {
      requireContext().hideKeyboard()
    }
    if (state.showTitleIsEmptyError) {
      showTitleViewError()
    } else {
      showTitleViewDefault()
    }
    if (state.showConfirmDeleteDialog) {
      infoDialog.showWithCancelAndProceedOption(
        titleRes = R.string.text_delete_note,
        message = getDeleteMessageText(state.titleState.initialText),
        onCancel = { store.tryDispatch(OnDialogHidden) },
        onProceed = { store.tryDispatch(OnConfirmedDeleting) }
      )
    } else {
      infoDialog.hide()
    }
    if (state.showLoadingDialog) {
      loadingDialog.show()
    } else {
      loadingDialog.hide()
    }
  }
  
  private fun renderActionIcons(
    editTextId: Int,
    textState: TextState,
    actionImageId: Int
  ) {
    val icon = if (textState.isEditingNow) R.drawable.ic_checmark else R.drawable.ic_copy
    imageView(actionImageId).image(icon)
    if (!textState.isEditingNow) {
      editText(editTextId).clearFocus()
    }
  }
  
  private fun handleNews(news: NoteEntryNews) {
    when (news) {
      ShowTitleCopied -> snackbar.show(CHECKMARK, R.string.text_title_copied)
      ShowTextCopied -> snackbar.show(CHECKMARK, R.string.text_text_copied)
      ShowNoteEntryCreated -> {
        snackbar.show(CHECKMARK, R.string.text_note_created)
        requireContext().hideKeyboard()
        editText(EditTextTitle).clearFocus()
        editText(EditTextText).clearFocus()
      }
    }
  }
  
  override fun onDisappearedFromScreen() {
    editText(EditTextTitle).clearFocus()
    editText(EditTextText).clearFocus()
    requireContext().hideKeyboard()
  }
  
  override fun handleBackPress(): Boolean {
    store.tryDispatch(OnBackPressed)
    return true
  }
  
  private fun showTitleViewDefault() {
    textView(Title).apply {
      textColor(Colors.Accent)
      text(R.string.text_title)
    }
  }
  
  private fun showTitleViewError() {
    textView(Title).apply {
      textColor(Colors.TextError)
      text(R.string.text_title_is_empty)
    }
  }
  
  companion object {
    
    val NoteScreenRoot = View.generateViewId()
    val ImageBack = View.generateViewId()
    val MainTitle = View.generateViewId()
    val ImageDelete = View.generateViewId()
    val ImageFavorite = View.generateViewId()
    val Title = View.generateViewId()
    val EditTextTitle = View.generateViewId()
    val ImageTitleAction = View.generateViewId()
    val TitleText = View.generateViewId()
    val EditTextText = View.generateViewId()
    val ImageTextAction = View.generateViewId()
    val ButtonSave = View.generateViewId()
  }
}
