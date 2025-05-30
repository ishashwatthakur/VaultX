package com.shashrwat.vault.features.main_list.recycler

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shashrwat.vault.R
import com.shashrwat.vault.core.TypefaceSpan
import com.shashrwat.vault.core.views.MaterialProgressBar
import com.shashrwat.vault.features.common.domain.setIconForTitle
import com.shashrwat.vault.features.common.model.Empty
import com.shashrwat.vault.features.common.model.EmptySearch
import com.shashrwat.vault.features.common.model.EntryItem
import com.shashrwat.vault.features.common.model.Loading
import com.shashrwat.vault.features.common.model.PasswordItem
import com.shashrwat.vault.features.common.model.NoteItem
import com.shashrwat.vault.features.common.model.Title
import com.shashrwat.vault.features.common.model.Title.Type
import com.shashrwat.vault.recycler.BaseListAdapter
import com.shashrwat.vault.recycler.delegate
import com.shashrwat.vault.viewbuilding.Colors
import com.shashrwat.vault.viewbuilding.Dimens.GradientDrawableHeight
import com.shashrwat.vault.viewbuilding.Dimens.HorizontalMarginSmall
import com.shashrwat.vault.viewbuilding.Dimens.ImageNoEntriesSize
import com.shashrwat.vault.viewbuilding.Dimens.ImageSize
import com.shashrwat.vault.viewbuilding.Dimens.ImageSizeBig
import com.shashrwat.vault.viewbuilding.Dimens.MarginLarge
import com.shashrwat.vault.viewbuilding.Dimens.MarginNormal
import com.shashrwat.vault.viewbuilding.Dimens.MarginSmall
import com.shashrwat.vault.viewbuilding.Dimens.MarginTiny
import com.shashrwat.vault.viewbuilding.Dimens.ProgressBarSizeBig
import com.shashrwat.vault.viewbuilding.Fonts
import com.shashrwat.vault.viewbuilding.Styles.BaseTextView
import com.shashrwat.vault.viewbuilding.Styles.BoldTextView
import com.shashrwat.vault.viewbuilding.Styles.SecondaryTextView
import com.shashrwat.vault.viewbuilding.TextSizes
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.Size.Companion.ZERO
import viewdsl.Size.IntSize
import viewdsl.backgroundCircle
import viewdsl.constraints
import viewdsl.gone
import viewdsl.gravity
import viewdsl.id
import viewdsl.image
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.marginHorizontal
import viewdsl.margins
import viewdsl.onClick
import viewdsl.paddingHorizontal
import viewdsl.paddingVertical
import viewdsl.paddings
import viewdsl.rippleBackground
import viewdsl.text
import viewdsl.textSize
import viewdsl.viewAs
import viewdsl.visible

class MainListAdapter(
  private val onItemClick: (EntryItem) -> Unit,
  private val onImageLoadingFailed: () -> Unit
) : BaseListAdapter() {
  
  init {
    addDelegates(
      delegate<Title> {
        buildView {
          RootTextView(WrapContent, WrapContent, style = BoldTextView) {
            textSize(TextSizes.H4)
            margins(
              top = MarginLarge,
              bottom = MarginNormal,
              start = MarginNormal
            )
          }
        }
        onBind {
          (itemView as TextView).setText(when (item.type) {
            Type.FAVORITES -> R.string.text_favorites
            Type.PASSWORDS -> R.string.text_passwords
            Type.NOTES -> R.string.text_notes
          })
        }
      },
      delegate<PasswordItem> {
        buildView {
          RootConstraintLayout(MatchParent, WrapContent) {
            rippleBackground(Colors.Ripple)
            clipChildren = false
            paddingHorizontal(HorizontalMarginSmall)
            paddingVertical(MarginSmall)
            ImageView(ImageSizeBig, ImageSizeBig) {
              id(ItemPasswordEntryImage)
              margins(start = MarginNormal)
              constraints {
                topToTopOf(parent)
                startToStartOf(parent)
                bottomToBottomOf(parent)
              }
            }
            TextView(ZERO, WrapContent, style = BaseTextView) {
              id(ItemPasswordEntryTitle)
              setSingleLine()
              margins(start = MarginNormal, end = MarginNormal, bottom = MarginTiny / 2)
            }
            View(MatchParent, IntSize(1)) {
              id(ItemPasswordEntryVerticalGuideline)
            }
            TextView(ZERO, WrapContent, style = SecondaryTextView) {
              id(ItemPasswordEntrySubtitle)
              setSingleLine()
              margins(start = MarginNormal, end = MarginNormal, top = MarginTiny / 2)
              constraints {
                startToEndOf(ItemPasswordEntryImage)
                endToEndOf(parent)
                topToBottomOf(ItemPasswordEntryVerticalGuideline)
              }
            }
          }
        }
        onInitViewHolder {
          itemView.onClick { onItemClick(item) }
        }
        onBind {
          itemView.viewAs<ImageView>(ItemPasswordEntryImage).setIconForTitle(item.title,
            onImageLoadingFailed)
          itemView.viewAs<TextView>(ItemPasswordEntryTitle).text(item.title)
          if (item.username.isNotEmpty()) {
            itemView.viewAs<TextView>(ItemPasswordEntrySubtitle).visible()
            itemView.viewAs<TextView>(ItemPasswordEntrySubtitle).text(item.username)
            itemView.viewAs<TextView>(ItemPasswordEntryTitle).constraints {
              startToEndOf(ItemPasswordEntryImage)
              endToEndOf(parent)
              bottomToTopOf(ItemPasswordEntryVerticalGuideline)
            }
            itemView.viewAs<View>(ItemPasswordEntryVerticalGuideline).constraints {
              topToTopOf(parent)
              bottomToBottomOf(parent)
            }
          } else {
            itemView.viewAs<TextView>(ItemPasswordEntrySubtitle).gone()
            itemView.viewAs<TextView>(ItemPasswordEntryTitle).constraints {
              startToEndOf(ItemPasswordEntryImage)
              topToTopOf(parent)
              bottomToBottomOf(parent)
              endToEndOf(parent)
            }
            itemView.viewAs<View>(ItemPasswordEntryVerticalGuideline).constraints {
              bottomToBottomOf(parent)
            }
          }
        }
      },
      delegate<NoteItem> {
        buildView {
          RootHorizontalLayout(MatchParent, WrapContent) {
            rippleBackground(Colors.Ripple)
            paddingHorizontal(HorizontalMarginSmall)
            paddingVertical(MarginSmall)
            FrameLayout(IntSize(ImageSizeBig), IntSize(ImageSizeBig)) {
              layoutGravity(CENTER_VERTICAL)
              margins(start = MarginNormal, end = MarginNormal)
              View(MatchParent, MatchParent) {
                layoutGravity(CENTER)
                backgroundCircle(Colors.Accent)
              }
              ImageView(ImageSize, ImageSize) {
                layoutGravity(CENTER)
                image(R.drawable.ic_note)
              }
            }
            TextView(WrapContent, WrapContent, style = BaseTextView) {
              id(ItemNoteEntryTitle)
              maxLines = 2
              layoutGravity(CENTER)
            }
          }
        }
        onInitViewHolder {
          itemView.onClick { onItemClick(item) }
        }
        onBind {
          itemView.viewAs<TextView>(ItemNoteEntryTitle).text(item.title)
        }
      },
      delegate<Loading> {
        buildView {
          RootVerticalLayout {
            paddings(top = GradientDrawableHeight)
            gravity(CENTER)
            child<MaterialProgressBar>(ProgressBarSizeBig, ProgressBarSizeBig)
          }
        }
      },
      delegate<Empty> {
        buildView {
          RootVerticalLayout {
            marginHorizontal(MarginLarge)
            gravity(CENTER)
            ImageView(ImageNoEntriesSize, ImageNoEntriesSize) {
              image(R.drawable.ic_lists)
              margin(MarginNormal)
            }
            TextView(WrapContent, WrapContent, style = BoldTextView) {
              marginHorizontal(MarginNormal)
              textSize(TextSizes.H3)
              text(R.string.text_no_entries)
            }
            TextView(WrapContent, WrapContent, style = BaseTextView) {
              textSize(TextSizes.H4)
              margin(MarginNormal)
              gravity(CENTER)
              val spannableString = SpannableString(context.getString(R.string.text_click_plus))
              val index = spannableString.indexOf('+')
              spannableString.setSpan(
                TypefaceSpan(Fonts.SegoeUiBold), index, index + 1, 0)
              spannableString.setSpan(RelativeSizeSpan(1.3f), index, index + 1, 0)
              text(spannableString)
            }
          }
        }
      },
      delegate<EmptySearch> {
        buildView {
          RootTextView(MatchParent, MatchParent, style = BoldTextView) {
            gravity(CENTER)
            textSize(TextSizes.H3)
            text(R.string.text_no_matching_entries)
          }
        }
      }
    )
  }
  
  companion object {
    
    val ItemPasswordEntryImage = View.generateViewId()
    val ItemPasswordEntryTitle = View.generateViewId()
    val ItemPasswordEntryVerticalGuideline = View.generateViewId()
    val ItemPasswordEntrySubtitle = View.generateViewId()
    val ItemNoteEntryTitle = View.generateViewId()
  }
}
