package azari.amirhossein.filmora.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.detail.RecommendationsTvFragment
import azari.amirhossein.filmora.ui.detail.SimilarTvFragment

class SimilarTvRecommendationsPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    private val fragments = listOf<Fragment>(
        SimilarTvFragment(),
        RecommendationsTvFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getFragment(position: Int): Fragment = fragments[position]
}