package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.detail.RecommendationsTvFragment
import azari.amirhossein.filmora.ui.detail.SimilarTvFragment

class SimilarTvRecommendationsPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    companion object {
        private const val SIMILAR_TV_TAB_ID = 300L
        private const val RECOMMENDATIONS_TV_TAB_ID = 301L
    }

    private val fragments = listOf(
        SimilarTvFragment(),
        RecommendationsTvFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments.getOrNull(position)
            ?: throw IllegalArgumentException("Invalid position $position")
    }

    fun getFragment(position: Int): Fragment {
        return fragments.getOrNull(position)
            ?: throw IllegalArgumentException("Invalid position $position")
    }

    override fun getItemId(position: Int): Long {
        return when (position) {
            0 -> SIMILAR_TV_TAB_ID
            1 -> RECOMMENDATIONS_TV_TAB_ID
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return when (itemId) {
            SIMILAR_TV_TAB_ID -> true
            RECOMMENDATIONS_TV_TAB_ID -> true
            else -> false
        }
    }
}
