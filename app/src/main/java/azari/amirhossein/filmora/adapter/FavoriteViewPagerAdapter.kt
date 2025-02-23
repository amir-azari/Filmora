package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.profile.favorite.FavoriteMovieFragment
import azari.amirhossein.filmora.ui.profile.favorite.FavoriteTvShowFragment

class FavoriteViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteMovieFragment()
            1 -> FavoriteTvShowFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        return when (position) {
            0 -> 500L
            1 -> 501L
            else -> position.toLong()
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId == 500L || itemId == 501L
    }
}

