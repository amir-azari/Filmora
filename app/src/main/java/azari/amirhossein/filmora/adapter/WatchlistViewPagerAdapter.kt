package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.profile.favorite.FavoriteMovieFragment
import azari.amirhossein.filmora.ui.profile.favorite.FavoriteTvShowFragment
import azari.amirhossein.filmora.ui.profile.watchlist.WatchlistMovieFragment
import azari.amirhossein.filmora.ui.profile.watchlist.WatchlistTvShowFragment

class WatchlistViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WatchlistMovieFragment()
            1 -> WatchlistTvShowFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        return when (position) {
            0 -> 600L
            1 -> 601L
            else -> position.toLong()
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId == 500L || itemId == 501L
    }
}

