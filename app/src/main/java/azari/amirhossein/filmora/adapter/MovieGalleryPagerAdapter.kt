package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.detail.movie.MovieBackdropFragment
import azari.amirhossein.filmora.ui.detail.movie.MoviePosterFragment
import azari.amirhossein.filmora.ui.detail.movie.MovieVideoFragment


class MovieGalleryPagerAdapter(
    fragment: Fragment,
    private val visibleTabs: List<Int>,
) : FragmentStateAdapter(fragment) {

    companion object {
        private const val VIDEO_TAB_ID = 100L
        private const val BACKDROP_TAB_ID = 101L
        private const val POSTER_TAB_ID = 102L
    }

    override fun getItemCount(): Int = visibleTabs.size


    override fun createFragment(position: Int): Fragment {
        return when (visibleTabs[position]) {
            0 -> MovieVideoFragment()
            1 -> MovieBackdropFragment()
            2 -> MoviePosterFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemId(position: Int): Long {
        // Use unique IDs for each tab type
        return when (visibleTabs[position]) {
            0 -> VIDEO_TAB_ID
            1 -> BACKDROP_TAB_ID
            2 -> POSTER_TAB_ID
            else -> position.toLong()
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return when (itemId) {
            VIDEO_TAB_ID -> visibleTabs.contains(0)
            BACKDROP_TAB_ID -> visibleTabs.contains(1)
            POSTER_TAB_ID -> visibleTabs.contains(2)
            else -> false
        }
    }

}
