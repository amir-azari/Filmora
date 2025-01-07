package azari.amirhossein.filmora.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.RecommendMovieAdapter
import azari.amirhossein.filmora.adapter.RecommendTvAdapter
import azari.amirhossein.filmora.adapter.TrendingAllAdapter
import azari.amirhossein.filmora.databinding.FragmentHomeBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.HomeViewModel
import coil3.load
import coil3.request.crossfade
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    //Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var recommendMovieAdapter: RecommendMovieAdapter

    @Inject
    lateinit var recommendTvAdapter: RecommendTvAdapter

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
        viewModel.combinedData()
        setupRecyclerViews()
        observeViewModel()
        lifecycleScope.launchWhenStarted {
            viewModel.isNetworkAvailable.collect { isAvailable ->
                if (!isAvailable) {
                    showErrorSnackbar(binding.root, Constants.Message.NO_INTERNET_CONNECTION)
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.apply {
            rvMovies.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = recommendMovieAdapter
            }

            rvTvs.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = recommendTvAdapter
            }

            binding.rvTrending.apply {
                adapter = trendingAdapter
                setAlpha(true)
                setInfinite(true)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.combineData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NetworkRequest.Loading -> {
                    showLoading()
                }

                is NetworkRequest.Success -> {
                    showSuccess()

                    state.data?.let {
                        trendingAdapter.differ.submitList(it.trendingMovies.data?.results)
                        recommendMovieAdapter.differ.submitList(it.recommendedMovies.data?.results)
                        recommendTvAdapter.differ.submitList(it.recommendedTvs.data?.results)
                        it.tvGenres.data?.genres?.let { genres ->
                            recommendTvAdapter.submitGenres(
                                genres
                            )
                        }
                        it.movieGenres.data?.genres?.let { genres ->
                            recommendMovieAdapter.submitGenres(
                                genres
                            )
                        }
                    }

                }

                is NetworkRequest.Error -> {
                    showError()
                    if (state.message == Constants.Message.NO_INTERNET_CONNECTION){
                        binding.internetLay.visibility  =View.VISIBLE
                    }
                    showErrorSnackbar(binding.root, state.message.toString())
                }
            }
        }
        viewModel.randomMoviePoster.observe(viewLifecycleOwner) { url ->
            loadImage(url, binding.imgMoviePoster)
        }

        viewModel.randomTvPoster.observe(viewLifecycleOwner) { url ->
            loadImage(url, binding.imgTvPoster)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}