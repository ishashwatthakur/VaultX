package com.shashrwat.vault.features.common.model

import com.shashrwat.vault.recycler.DifferentiableItem

sealed interface EntryItem : DifferentiableItem

data class PasswordItem(
  override val id: String,
  val title: String,
  val username: String,
  val hasActualTitle: Boolean
) : EntryItem

data class NoteItem(
  override val id: String,
  val title: String,
  val hasActualTitle: Boolean
) : EntryItem
