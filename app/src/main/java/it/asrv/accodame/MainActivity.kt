package it.asrv.accodame

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.asrv.accodame.Constants.Companion.ACTION_SEARCH
import it.asrv.accodame.Constants.Companion.EXTRA_SEARCH_QUERY
import it.asrv.accodame.utils.DLog

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity.javaClass.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DLog.i(TAG, "onNewIntent")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_favorites, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        DLog.i(TAG, "onNewIntent")
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                val broadcastIntent = Intent(ACTION_SEARCH)
                broadcastIntent.putExtra(EXTRA_SEARCH_QUERY, query)
                sendBroadcast(broadcastIntent)
            }
        }
    }
}
