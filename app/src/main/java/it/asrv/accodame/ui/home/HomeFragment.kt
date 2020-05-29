package it.asrv.accodame.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import it.asrv.accodame.R
import it.asrv.accodame.ui.home.map.MapFragment
import it.asrv.accodame.utils.DLog
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    companion object {
        val TAG = HomeFragment.javaClass.name
    }

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        vHomeSearch.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        vHomeSearch.isIconifiedByDefault = false
        vHomeSearch.isSubmitButtonEnabled = true
        vHomeSearch.isQueryRefinementEnabled = true*/

        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        vHomePlaceSearch.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        vHomePlaceSearch.isIconifiedByDefault = false
        vHomePlaceSearch.isSubmitButtonEnabled = true
        vHomePlaceSearch.isQueryRefinementEnabled = true
        vHomePlaceSearch.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

        })

        vHomePlaceSearch.setOnSuggestionListener(object: android.widget.SearchView.OnSuggestionListener
        {
            override fun onSuggestionClick(position: Int): Boolean {
                DLog.i(TAG, "onSuggestionClick($position)")
                val cursorAdapter = vHomePlaceSearch.suggestionsAdapter
                val cursor = cursorAdapter.cursor
                cursor.moveToPosition(position)
                val query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                vHomePlaceSearch.setQuery(query, false)
                return false
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                DLog.i(TAG, "onSuggestionSelect($position)")
                return false
            }
        })

        vHomePager.adapter = HomePagerAdapter(this)
        //Disable swipe
        vHomePager.isUserInputEnabled = false

        TabLayoutMediator(vHomePagerTabs, vHomePager) { tab, position ->
            var title = ""
            when(position) {
                0 -> title = getString(R.string.home_tab_map)
                1 -> title = getString(R.string.home_tab_list)
            }
            tab.text = title
        }.attach()
    }

    public fun doFocusOnPlaceSearch() {
        vHomePlaceSearch.requestFocus()
    }

    class HomePagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            var fragment = Fragment()
            when(position) {
                0 -> fragment = MapFragment()
            }
            return fragment
        }
    }

}
