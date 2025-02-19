package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.search.SearchAllFragment
import azari.amirhossein.filmora.ui.search.SearchMoviesFragment
import azari.amirhossein.filmora.ui.search.SearchPeopleFragment
import azari.amirhossein.filmora.ui.search.SearchTvShowsFragment

class SearchPagerAdapter(
    fragment: Fragment,
    private val visibleTabs: List<Int>
    ) : FragmentStateAdapter(fragment) {
    companion object {
        private const val ALL_TAB_ID = 400L
        private const val MOVIE_TAB_ID = 401L
        private const val TV_TAB_ID = 402L
        private const val PEOPLE_TAB_ID = 403L
    }

    override fun getItemCount(): Int = visibleTabs.size

    override fun createFragment(position: Int): Fragment {
        return when (visibleTabs[position]) {
            0 -> SearchAllFragment()
            1 -> lazy { SearchMoviesFragment() }.value
            2 -> lazy { SearchTvShowsFragment() }.value
            3 -> lazy { SearchPeopleFragment() }.value
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        return when (visibleTabs[position]) {
            0 -> ALL_TAB_ID
            1 -> MOVIE_TAB_ID
            2 -> TV_TAB_ID
            3 -> PEOPLE_TAB_ID
            else -> position.toLong()
        }
    }
    override fun containsItem(itemId: Long): Boolean {
        return when (itemId) {
            ALL_TAB_ID -> visibleTabs.contains(0)
            MOVIE_TAB_ID -> visibleTabs.contains(1)
            TV_TAB_ID -> visibleTabs.contains(2)
            PEOPLE_TAB_ID -> visibleTabs.contains(3)
            else -> false
        }
    }
}