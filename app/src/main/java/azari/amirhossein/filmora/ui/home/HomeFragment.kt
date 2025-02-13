package azari.amirhossein.filmora.ui.home

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
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: HomeViewModel by viewModels()

    // Adapters
    @Inject
    lateinit var mayLikeMovieAdapter: MayLikeMovieAdapter

    @Inject
    lateinit var mayLikeTvAdapter: MayLikeTvAdapter

    @Inject
    lateinit var trendingAdapter: TrendingAllAdapter

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
        setupRecyclerViews()
        observeViewModel()
        mayLikeMovieAdapter.setOnItemClickListener(clickMovie)
        mayLikeTvAdapter.setOnItemClickListener(clickTv)
        trendingAdapter.setOnItemClickListener(clickTrending)

        binding.layoutSeeAllMovies.setClickAnimation {
            findNavController().navigate(R.id.actionHomeToMayLikeMovies)
        }
        binding.layoutSeeAllTVSeries.setClickAnimation {
            findNavController().navigate(R.id.actionHomeToMayLikeTvs)
        }
    }

    // Setup recyclerView
    private fun setupRecyclerViews() {
        binding.apply {
            rvMovies.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mayLikeMovieAdapter
                setHasFixedSize(true)
            }

            rvTvs.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mayLikeTvAdapter
                setHasFixedSize(true)

            }

            binding.rvTrending.apply {
                adapter = trendingAdapter
                setAlpha(true)
                setInfinite(true)
                setHasFixedSize(true)

            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homePageData.collect { state ->
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
        }

        // Observing random movie
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.randomMoviePoster.collect { url ->
                    loadImage(url, binding.imgMoviePoster)
                }


            }
        }

        // Observing random Tv
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.randomTvPoster.collect { url ->
                    loadImage(url, binding.imgTvPoster)
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
    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action = HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,movie.id)
        findNavController().navigate(action)

    }

    private val clickTv = { tv: ResponseTvsList.Result ->
        val action = HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }

    private val clickTrending = { trending : ResponseTrendingList.Result ->
        if (trending.mediaType == Constants.MediaType.MOVIE){
            val action = HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,trending.id)
            findNavController().navigate(action)
        }
        if (trending.mediaType == Constants.MediaType.TV){
            val action = HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV,trending.id)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}