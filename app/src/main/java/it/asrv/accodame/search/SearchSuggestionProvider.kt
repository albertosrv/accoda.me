package it.asrv.accodame.search

import android.app.SearchManager
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.BaseColumns
import it.asrv.accodame.Configuration
import it.asrv.accodame.R


class SearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "it.asrv.accodame.search.SearchSuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        queryArgs: Bundle?,
        cancellationSignal: CancellationSignal?
    ): Cursor? {
        val queryArg: String? = queryArgs?.getStringArray("android:query-arg-sql-selection-args")?.get(0)
        return createCursorFromResult(Configuration.CITIES.toList(), queryArg)
    }

    private fun createCursorFromResult(cities: List<String>, queryArg: String?): Cursor? {
        val menuCols = arrayOf(
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA
        )
        val cursor = MatrixCursor(menuCols)
        var counter = 0
        for (city in cities) {
            if(queryArg == null || city.startsWith(queryArg, true))
                cursor.addRow(arrayOf<Any>(counter, R.drawable.ic_apartment_black_24dp, city, city))
            counter++
        }
        return cursor
    }
}