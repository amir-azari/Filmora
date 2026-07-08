package azari.amirhossein.filmora.ui.movies

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
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
import kotlinx.coroutines.delay
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

    // SharedRecycledViewPool for horizontal RecyclerViews using similar item layouts
    private val sharedViewPool = RecyclerView.RecycledViewPool()

    // Below-fold views (inflated lazily via ViewStub)
    private var belowFoldInflated = false
    private var rvPopular: RecyclerView? = null
    private var rvNowPlaying: RecyclerView? = null
    private var rvTopRated: RecyclerView? = null
    private var rvUpcoming: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTrendingRecyclerView()
        observeViewModel()

        trendingAdapter.setOnItemClickListener(clickTrending)

        binding.layoutSeeAllTrending.setClickAnimation {
            findNavController().navigate(
                R.id.actionToMovieSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TRENDING_MOVIE)
                }
            )
        }

        // Inflate below-fold sections lazily after a short delay
        inflateBelowFold()
    }

    private fun setupTrendingRecyclerView() {
        binding.rvTrending.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingAdapter
            setRecycledViewPool(sharedViewPool)
            setHasFixedSize(true)
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

        rvPopular = root.findViewById(R.id.rv_popular)
        rvNowPlaying = root.findViewById(R.id.rv_NowPlaying)
        rvTopRated = root.findViewById(R.id.rv_TopRated)
        rvUpcoming = root.findViewById(R.id.rv_upcoming)

        rvPopular?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
            setRecycledViewPool(sharedViewPool)
            setHasFixedSize(true)
        }
        rvNowPlaying?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = nowPlayingAdapter
            setRecycledViewPool(sharedViewPool)
            setHasFixedSize(true)
        }
        rvTopRated?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            adapter = topRatedAdapter
            setHasFixedSize(true)
        }
        rvUpcoming?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
            setRecycledViewPool(sharedViewPool)
            setHasFixedSize(true)
        }

        // Set click listeners for below-fold sections
        popularAdapter.setOnItemClickListener(clickMovie)
        nowPlayingAdapter.setOnItemClickListener(clickMovie)
        topRatedAdapter.setOnItemClickListener(clickMovie)
        upcomingAdapter.setOnItemClickListener(clickMovie)

        root.findViewById<LinearLayout>(R.id.layoutSeeAllPopular)?.setClickAnimation {
            findNavController().navigate(
                R.id.actionToMovieSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.POPULAR_MOVIE)
                }
            )
        }

        root.findViewById<LinearLayout>(R.id.layoutSeeAllNowPlaying)?.setClickAnimation {
            findNavController().navigate(
                R.id.actionToMovieSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.NOW_PLAYING)
                }
            )
        }

        root.findViewById<LinearLayout>(R.id.layoutSeeAllTopRated)?.setClickAnimation {
            findNavController().navigate(
                R.id.actionToMovieSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.TOP_RATED_MOVIE)
                }
            )
        }

        root.findViewById<LinearLayout>(R.id.layoutSeeAllUpcoming)?.setClickAnimation {
            findNavController().navigate(
                R.id.actionToMovieSectionFragment,
                Bundle().apply {
                    putString(Constants.SectionType.SECTION_TYPE, Constants.SectionType.UPCOMING)
                }
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.moviePageData.collect { state ->
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

                launch {
                    viewModel.randomMoviePoster.collect { url ->
                        loadImage(url, binding.imgMoviePoster)
                    }
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
        // Clear references to below-fold views
        rvPopular = null
        rvNowPlaying = null
        rvTopRated = null
        rvUpcoming = null
        belowFoldInflated = false
        _binding = null
    }
}
