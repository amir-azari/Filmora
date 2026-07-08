package azari.amirhossein.filmora.ui.people

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.PopularCelebrityAdapter
import azari.amirhossein.filmora.adapter.TrendingCelebrityAdapter
import azari.amirhossein.filmora.databinding.FragmentPeopleBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.PeopleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PeopleFragment : Fragment() {
    //Binding
    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: PeopleViewModel by activityViewModels()

    @Inject
    lateinit var popular1Adapter : PopularCelebrityAdapter

    @Inject
    lateinit var popular2Adapter : PopularCelebrityAdapter

    @Inject
    lateinit var trendingAdapter : TrendingCelebrityAdapter

    // SharedRecycledViewPool for popular RecyclerViews using same item layout
    private val sharedViewPool = RecyclerView.RecycledViewPool()

    // Below-fold views (inflated lazily via ViewStub)
    private var belowFoldInflated = false
    private var rvTrending: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPopularRecyclerViews()
        observeViewModel()

        popular1Adapter.setOnItemClickListener(click)
        popular2Adapter.setOnItemClickListener(click)

        binding.layoutSeeAllPopular.setClickAnimation {
            findNavController().navigate(
                R.id.actionToPeopleSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.POPULAR_PEOPLE)
                }
            )
        }

        // Inflate below-fold sections lazily after a short delay
        inflateBelowFold()
    }

    private fun setupPopularRecyclerViews() {
        binding.apply {
            rvPopularOne.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = popular1Adapter
                setRecycledViewPool(sharedViewPool)
                setHasFixedSize(true)
            }

            rvPopularTwo.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = popular2Adapter
                setRecycledViewPool(sharedViewPool)
                setHasFixedSize(true)
            }
        }
    }

    private fun inflateBelowFold() {
        if (belowFoldInflated) return
        viewLifecycleOwner.lifecycleScope.launch {
            delay(300) // Wait for fragment enter animation to finish
            val viewStub = binding.viewStubBelowFold
            if (viewStub.parent != null) {
                viewStub.inflate()
                belowFoldInflated = true
                setupBelowFoldViews()
            }
        }
    }

    private fun setupBelowFoldViews() {
        val root = binding.root

        rvTrending = root.findViewById(R.id.rv_trending)

        rvTrending?.apply {
            layoutManager = GridLayoutManager(requireContext(), 4, GridLayoutManager.HORIZONTAL, false)
            adapter = trendingAdapter
            setHasFixedSize(true)
        }

        trendingAdapter.setOnItemClickListener(click)

        root.findViewById<LinearLayout>(R.id.layoutSeeAllTrending)?.setClickAnimation {
            findNavController().navigate(
                R.id.actionToPeopleSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TRENDING_PEOPLE)
                }
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.peoplePageData.collect { state ->
                    if (state != null) {
                        when (state) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                showSuccess()
                                state.data?.let { data ->
                                    // Update adapters with the new data
                                    data.popular.data?.results?.let { popular1Adapter.submitFirstTen(it) }
                                    data.popular.data?.results?.let { popular2Adapter.submitSecondTen(it) }
                                    trendingAdapter.differ.submitList(data.trending.data?.results)
                                }
                            }

                            is NetworkRequest.Error -> {
                                showError()
                                if (state.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                    binding.internetLay.visibility = View.VISIBLE
                                }
                                showErrorSnackbar(binding.root, state.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    private val click = { id: Int ->
        val action = PeopleFragmentDirections.actionToPeopleDetailFragment(id)
        findNavController().navigate(action)

    }
    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.mainContentContainer.visibility = View.GONE
        binding.internetLay.visibility = View.GONE
    }

    private fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear references to below-fold views
        rvTrending = null
        belowFoldInflated = false
        _binding = null
    }
}