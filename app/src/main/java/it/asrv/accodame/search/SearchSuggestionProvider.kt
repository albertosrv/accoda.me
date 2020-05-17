package it.asrv.accodame.search

import android.content.SearchRecentSuggestionsProvider

class SearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "it.asrv.accodame.search.SearchSuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}