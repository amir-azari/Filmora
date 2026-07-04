package azari.amirhossein.filmora.ui.movies

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MayLikeMovieAdapter
import azari.amirhossein.filmora.adapter.TopRatedMovieAdapter
import azari.amirhossein.filmora.adapter.TrendingMovieAdapter
import azari.amirhossein.filmora.databinding.FragmentMovieBinding
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.MovieViewModel
import coil3.load
import coil3.request.crossfade
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieFragment : Fragment() {
    //Binding
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: MovieViewModel by activityViewModels()

    @Inject
    lateinit var trendingAdapter: TrendingMovieAdapter

    @Inject
    lateinit var popularAdapter: MayLikeMovieAdapter

    @Inject
    lateinit var nowPlayingAdapter: MayLikeMovieAdapter

    @Inject
    lateinit var topRatedAdapter: TopRatedMovieAdapter

    @Inject
    lateinit var upcomingAdapter: MayLikeMovieAdapter

    // Caching the root view to prevent layout inflation lag
    private var rootView: View? = null
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (rootView == null) {
            _binding = FragmentMovieBinding.inflate(inflater, container, false)
            rootView = binding.root
        } else {
            // Remove view from parent before returning it
            (rootView?.parent as? ViewGroup)?.removeView(rootView)
        }
        return rootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup only once if adapters are not set up yet
        if (binding.rvTrending.adapter == null) {
            setupRecyclerViews()
            observeViewModel()

            trendingAdapter.setOnItemClickListener(clickTrending)
            popularAdapter.setOnItemClickListener(clickMovie)
            nowPlayingAdapter.setOnItemClickListener(clickMovie)
            topRatedAdapter.setOnItemClickListener(clickMovie)
            upcomingAdapter.setOnItemClickListener(clickMovie)

            binding.layoutSeeAllTrending.setClickAnimation {
                findNavController().navigate(
                    R.id.actionToMovieSectionFragment,
                    Bundle().apply {
                        putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TRENDING_MOVIE)
                    }
                )
            }

            binding.layoutSeeAllPopular.setClickAnimation {
                findNavController().navigate(
                    R.id.actionToMovieSectionFragment,
                    Bundle().apply {
                        putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.POPULAR_MOVIE)
                    }
                )
            }

            binding.layoutSeeAllNowPlaying.setClickAnimation {
                findNavController().navigate(
                    R.id.actionToMovieSectionFragment,
                    Bundle().apply {
                        putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.NOW_PLAYING)
                    }
                )
            }

            binding.layoutSeeAllTopRated.setClickAnimation {
                findNavController().navigate(
                    R.id.actionToMovieSectionFragment,
                    Bundle().apply {
                        putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TOP_RATED_MOVIE)
                    }
                )
            }

            binding.layoutSeeAllUpcoming.setClickAnimation {
                findNavController().navigate(
                    R.id.actionToMovieSectionFragment,
                    Bundle().apply {
                        putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.UPCOMING)
                    }
                )
            }
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
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = popularAdapter
                setHasFixedSize(true)
            }
            rvNowPlaying.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = nowPlayingAdapter
                setHasFixedSize(true)
            }

            rvTopRated.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
                adapter = topRatedAdapter
                setHasFixedSize(true)
            }
            rvUpcoming.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = upcomingAdapter
                setHasFixedSize(true)
            }
        }
    }

    private var dataJob: kotlinx.coroutines.Job? = null

    private fun collectData() {
        dataJob?.cancel()
        dataJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moviePageData.collect { state ->
                if (state != null) {
                    when (state) {
                        is NetworkRequest.Loading -> {
                            showLoading()
                        }

                        is NetworkRequest.Success -> {
                            showSuccess()
                            isFirstLoad = false
                            state.data?.let { data ->
                                // Update adapters with the new data
                                trendingAdapter.differ.submitList(data.trending.data?.results)
                                popularAdapter.differ.submitList(data.popular.data?.results)
                                nowPlayingAdapter.differ.submitList(data.nowPlaying.data?.results)
                                topRatedAdapter.differ.submitList(data.topRated.data?.results)
                                upcomingAdapter.differ.submitList(data.upcoming.data?.results)

                                data.movieGenres.data?.genres?.let { genres ->
                                    trendingAdapter.submitGenres(genres)
                                    popularAdapter.submitGenres(genres)
                                    nowPlayingAdapter.submitGenres(genres)
                                    topRatedAdapter.submitGenres(genres)
                                    upcomingAdapter.submitGenres(genres)
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

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (!isHidden) {
                    collectData()
                    launch {
                        viewModel.randomMoviePoster.collect { url ->
                            loadImage(url, binding.imgMoviePoster)
                        }
                    }
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && _binding != null) {
            if (isFirstLoad) {
                viewLifecycleOwner.lifecycleScope.launch {
                    showLoading()
                    kotlinx.coroutines.delay(220)
                    collectData()
                }
            }
        }
    }


    //Click media
    private val clickTrending = { movie: ResponseTrendingMovie.Result ->
        val action =
            MovieFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
        findNavController().navigate(action)

    }

    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action =
            MovieFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
        findNavController().navigate(action)
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
        // Do NOT clear _binding here because we are caching rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rootView = null
    }
}
