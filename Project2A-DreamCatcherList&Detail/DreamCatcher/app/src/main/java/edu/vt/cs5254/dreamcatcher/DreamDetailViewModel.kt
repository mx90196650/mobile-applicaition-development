package edu.vt.cs5254.dreamcatcher

import androidx.lifecycle.ViewModel

class DreamDetailViewModel : ViewModel() {

    var dream: Dream

    init {
        dream = Dream(title = "My First Dream")
        dream.entries += listOf(
            DreamEntry(
                kind = DreamEntryKind.REFLECTION,
                text = "Reflection One",
                dreamId = dream.id
            ),
            DreamEntry(
                kind = DreamEntryKind.REFLECTION,
                text = "Reflection Two",
                dreamId = dream.id
            ),
            DreamEntry(
                kind = DreamEntryKind.DEFERRED,
                dreamId = dream.id
            )
        )
    }

    fun toggleFulfilledCheckedBox() {
        if (dream.isFulfilled) {
            dream.entries = dream.entries.filterNot { it.kind == DreamEntryKind.FULFILLED }
        } else {
            dream.entries += DreamEntry(
                kind = DreamEntryKind.FULFILLED,
                dreamId = dream.id
            )
        }
    }

    fun toggleDeferredCheckedBox() {
        if (dream.isDeferred) {
            dream.entries = dream.entries.filterNot { it.kind == DreamEntryKind.DEFERRED }
        } else {
            dream.entries += DreamEntry(
                kind = DreamEntryKind.DEFERRED,
                text = "Deferred",
                dreamId = dream.id
            )
        }
    }
}