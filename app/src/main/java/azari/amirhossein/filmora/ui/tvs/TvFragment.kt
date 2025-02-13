package azari.amirhossein.filmora.ui.tvs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MayLikeTvAdapter
import azari.amirhossein.filmora.adapter.TopRatedTvAdapter
import azari.amirhossein.filmora.adapter.TrendingTvAdapter
import azari.amirhossein.filmora.databinding.FragmentTvBinding
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.tv.ResponseTrendingTv
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.TvViewModel
import coil3.load
import coil3.request.crossfade
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TvFragment : Fragment() {
    //Binding
    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: TvViewModel by viewModels()

    @Inject
    lateinit var trendingAdapter : TrendingTvAdapter

    @Inject
    lateinit var popularAdapter : MayLikeTvAdapter

    @Inject
    lateinit var topRatedAdapter : TopRatedTvAdapter

    @Inject
    lateinit var airingTodayAdapter : MayLikeTvAdapter

    @Inject
    lateinit var onTheAirAdapter : MayLikeTvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTvBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()

        trendingAdapter.setOnItemClickListener(clickTrending)
        popularAdapter.setOnItemClickListener(clickTv)
        airingTodayAdapter.setOnItemClickListener(clickTv)
        topRatedAdapter.setOnItemClickListener(clickTv)
        onTheAirAdapter.setOnItemClickListener(clickTv)

        binding.layoutSeeAllTrending.setClickAnimation {
            findNavController().navigate(
                R.id.actionToTvSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TRENDING_TV)
                }
            )
        }

        binding.layoutSeeAllPopular.setClickAnimation {
            findNavController().navigate(
                R.id.actionToTvSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.POPULAR_TV)
                }
            )
        }

        binding.layoutSeeAllAiringToday.setClickAnimation {
            findNavController().navigate(
                R.id.actionToTvSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.AIRING_TODAY)
                }
            )
        }

        binding.layoutSeeAllTopRated.setClickAnimation {
            findNavController().navigate(
                R.id.actionToTvSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TOP_RATED_TV)
                }
            )
        }

        binding.layoutSeeAllOnTheAir.setClickAnimation {
            findNavController().navigate(
                R.id.actionToTvSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.ON_THE_AIR)
                }
            )
        }
    }
    // Setup recyclerView
    private fun setupRecyclerViews() {
        binding.apply {
            rvTrending.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = trendingAdapter
                setHasFixedSize(true)
            }
            rvPopular.apply {
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = popularAdapter
                setHasFixedSize(true)
            }

            rvTopRated.apply {
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
                adapter = topRatedAdapter
                setHasFixedSize(true)
            }
            rvAiringToday.apply {
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = airingTodayAdapter
                setHasFixedSize(true)
            }
            rvOnTheAir.apply {
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = onTheAirAdapter
                setHasFixedSize(true)
            }

        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvPageData.collect { state ->
                    binding.mainContentContainer.visibility = View.GONE
                    if (state != null) {
                        when (state) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                showSuccess()
                                state.data?.let { data ->
                                    // Update adapters with the new data
                                    trendingAdapter.differ.submitList(data.trending.data?.results)
                                    popularAdapter.differ.submitList(data.popular.data?.results)
                                    topRatedAdapter.differ.submitList(data.topRated.data?.results)
                                    airingTodayAdapter.differ.submitList(data.airingToday.data?.results)
                                    onTheAirAdapter.differ.submitList(data.onTheAir.data?.results)


                                    data.tvGenres.data?.genres?.let { genres ->
                                        trendingAdapter.submitGenres(genres)
                                        popularAdapter.submitGenres(genres)
                                        topRatedAdapter.submitGenres(genres)
                                        airingTodayAdapter.submitGenres(genres)
                                        onTheAirAdapter.submitGenres(genres)

                                    }
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

        // Observing random movie
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.randomMoviePoster.collect { url ->
                    loadImage(url, binding.imgMoviePoster)
                }

            }
        }

    }

    private fun loadImage(url: String?, imageView: ImageView) {
        imageView.load(url) {
            crossfade(true)
            crossfade(400)
        }
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
        binding.internetLay.visibility  =View.GONE


    }

    private fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.VISIBLE


    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.GONE

    }
    //Click media
    private val clickTrending = { tv: ResponseTrendingTv.Result ->
        val action = TvFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }

    private val clickTv = { tv: ResponseTvsList.Result ->
        val action = TvFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}