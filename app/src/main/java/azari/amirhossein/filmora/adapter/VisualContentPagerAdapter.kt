package azari.amirhossein.filmora.adapter

import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import azari.amirhossein.filmora.ui.detail.BackdropFragment
import azari.amirhossein.filmora.ui.detail.PosterFragment
import azari.amirhossein.filmora.ui.detail.VideoFragment

class VisualContentPagerAdapter(
    fragment: Fragment,
    private val visibleTabs: List<Int>
) : FragmentStateAdapter(fragment) {
    private val views = SparseArray<View>()

    private val fragments = listOf<Fragment>(
        VideoFragment(),
        BackdropFragment(),
        PosterFragment()
    )

    override fun getItemCount(): Int = visibleTabs.size

    override fun createFragment(position: Int): Fragment {
        return fragments[visibleTabs[position]]
    }

}
