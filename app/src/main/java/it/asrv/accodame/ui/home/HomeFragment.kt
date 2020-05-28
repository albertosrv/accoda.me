package it.asrv.accodame.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import it.asrv.accodame.R
import it.asrv.accodame.ui.home.map.MapFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var cityAdapter: CursorAdapter? = null

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



    class HomePagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            var fragment : Fragment = Fragment()
            when(position) {
                0 -> fragment = MapFragment()
            }
            return fragment
        }
    }

}
