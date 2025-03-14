package azari.amirhossein.filmora.ui.profile.rated

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.FavoriteViewPagerAdapter
import azari.amirhossein.filmora.adapter.RatedViewPagerAdapter
import azari.amirhossein.filmora.databinding.FragmentFavoriteBinding
import azari.amirhossein.filmora.databinding.FragmentRatedBinding
import azari.amirhossein.filmora.viewmodel.FavoriteViewModel
import azari.amirhossein.filmora.viewmodel.RatedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RatedFragment : Fragment() {

    private var _binding: FragmentRatedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RatedViewModel by viewModels()
    private lateinit var viewPagerAdapter: RatedViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        observeCurrentTab()
    }

    private fun setupViewPager() {
        viewPagerAdapter = RatedViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                page.alpha = 1 - kotlin.math.abs(position)
            }
            setCurrentItem(viewModel.currentTabPosition.value, false)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.movies)
                1 -> getString(R.string.tv_shows)
                else -> ""
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.updateTabPosition(position)
            }
        })
    }


    private fun observeCurrentTab() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentTabPosition.collectLatest { position ->
                binding.viewPager.currentItem = position
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}