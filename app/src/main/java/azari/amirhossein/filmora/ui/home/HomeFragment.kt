package azari.amirhossein.filmora.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MayLikeMovieAdapter
import azari.amirhossein.filmora.adapter.MayLikeTvAdapter
import azari.amirhossein.filmora.adapter.TrendingAllAdapter
import azari.amirhossein.filmora.databinding.FragmentHomeBinding
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.HomeViewModel
import coil3.load
import coil3.request.crossfade
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: HomeViewModel by activityViewModels()

    // Adapters
    @Inject
    lateinit var mayLikeMovieAdapter: MayLikeMovieAdapter

    @Inject
    lateinit var mayLikeTvAdapter: MayLikeTvAdapter

    @Inject
    lateinit var trendingAdapter: TrendingAllAdapter

    // SharedRecycledViewPool for horizontal RecyclerViews
    private val sharedViewPool = RecyclerView.RecycledViewPool()

    // Below-fold views (inflated lazily via ViewStub)
    private var belowFoldInflated = false
    private var rvMovies: RecyclerView? = null
    private var rvTvs: RecyclerView? = null
    private var imgMoviePoster: ImageView? = null
    private var imgTvPoster: ImageView? = null

    // Cache URLs in case they arrive before ViewStub is inflated
    private var pendingMoviePosterUrl: String? = null
    private var pendingTvPosterUrl: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTrendingRecyclerView()
        observeViewModel()
        trendingAdapter.setOnItemClickListener(clickTrending)
        // Inflate below-fold sections lazily after a short delay
        inflateBelowFold()
    }

    private fun setupTrendingRecyclerView() {
        binding.rvTrending.apply {
            adapter = trendingAdapter
            setAlpha(true)
            setInfinite(true)
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

        rvMovies = root.findViewById(R.id.rv_movies)
        rvTvs = root.findViewById(R.id.rv_tvs)
        imgMoviePoster = root.findViewById(R.id.img_movie_poster)
        imgTvPoster = root.findViewById(R.id.img_tv_poster)

            rvMovies?.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mayLikeMovieAdapter
                setRecycledViewPool(sharedViewPool)
                setHasFixedSize(true)
            }

        rvTvs?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mayLikeTvAdapter
            setRecycledViewPool(sharedViewPool)
            setHasFixedSize(true)
        }
        mayLikeMovieAdapter.setOnItemClickListener(clickMovie)
        mayLikeTvAdapter.setOnItemClickListener(clickTv)
        root.findViewById<LinearLayout>(R.id.layoutSeeAllMovies)?.setClickAnimation {
            findNavController().navigate(R.id.actionHomeToMayLikeMovies)
        }
        root.findViewById<LinearLayout>(R.id.layoutSeeAllTVSeries)?.setClickAnimation {
            findNavController().navigate(R.id.actionHomeToMayLikeTvs)
        }
        // Load cached URLs if any arrived during inflation delay
        pendingMoviePosterUrl?.let { loadImage(it, imgMoviePoster) }
        pendingTvPosterUrl?.let { loadImage(it, imgTvPoster) }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.homePageData.collect { state ->
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
                                        mayLikeMovieAdapter.differ.submitList(data.recommendedMovies.data?.results)
                                        mayLikeTvAdapter.differ.submitList(data.recommendedTvs.data?.results)
                                        data.tvGenres.data?.genres?.let { genres ->
                                            mayLikeTvAdapter.submitGenres(genres)
                                        }
                                        data.movieGenres.data?.genres?.let { genres ->
                                            mayLikeMovieAdapter.submitGenres(genres)
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
                // Observing random movie poster
                launch {
                    viewModel.randomMoviePoster.collect { url ->
                        pendingMoviePosterUrl = url
                        imgMoviePoster?.let { loadImage(url, it) }
                    }
                }
                // Observing random Tv poster
                launch {
                    viewModel.randomTvPoster.collect { url ->
                        pendingTvPosterUrl = url
                        imgTvPoster?.let { loadImage(url, it) }
                    }
                }
            }
        }
    }

    private fun loadImage(url: String?, imageView: ImageView?) {
        imageView?.load(url) {
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

    //Click media
    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action = HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
        findNavController().navigate(action)
    }
    private val clickTv = { tv: ResponseTvsList.Result ->
        val action = HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV, tv.id)
        findNavController().navigate(action)
    }
    private val clickTrending = { trending: ResponseTrendingList.Result ->
        if (trending.mediaType == Constants.MediaType.MOVIE) {
            val action =
                HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, trending.id)
            findNavController().navigate(action)
        }
        if (trending.mediaType == Constants.MediaType.TV) {
            val action =
                HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV, trending.id)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvMovies = null
        rvTvs = null
        imgMoviePoster = null
        imgTvPoster = null
        belowFoldInflated = false
        _binding = null
    }
}