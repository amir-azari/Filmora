package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.detail.RecommendationsMovieFragment
import azari.amirhossein.filmora.ui.detail.SimilarMovieFragment

class SimilarMovieRecommendationsPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    companion object {
        private const val SIMILAR_MOVIE_TAB_ID = 200L
        private const val RECOMMENDATIONS_MOVIE_TAB_ID = 201L
    }

    private val fragments = listOf(
        SimilarMovieFragment(),
        RecommendationsMovieFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    override fun getItemId(position: Int): Long {
        return when (position) {
            0 -> SIMILAR_MOVIE_TAB_ID
            1 -> RECOMMENDATIONS_MOVIE_TAB_ID
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return when (itemId) {
            SIMILAR_MOVIE_TAB_ID -> true
            RECOMMENDATIONS_MOVIE_TAB_ID -> true
            else -> false
        }
    }
}
