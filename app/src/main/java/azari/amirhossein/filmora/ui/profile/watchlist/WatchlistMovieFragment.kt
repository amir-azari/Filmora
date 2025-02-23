package azari.amirhossein.filmora.ui.profile.watchlist

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.adapter.MoviesAdapter
import azari.amirhossein.filmora.databinding.FragmentWatchlistMovieBinding
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.ui.profile.favorite.FavoriteFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.WatchlistViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class WatchlistMovieFragment : Fragment() {

    private var _binding: FragmentWatchlistMovieBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var moviesAdapter: MoviesAdapter

    private val viewModel: WatchlistViewModel by  viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWatchlistMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMovies()

        moviesAdapter.setOnItemClickListener(clickMovie)


    }
    private fun setupRecyclerView() {
        val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()
        binding.rvMovies.layoutManager = flexboxLayoutManager

        val concatAdapter = moviesAdapter.withLoadStateFooter(
            footer = DataLoadStateAdapter { moviesAdapter.retry() }
        )
        binding.rvMovies.adapter = concatAdapter

        moviesAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            binding.rvMovies.isVisible = loadState.refresh is LoadState.NotLoading

            if (loadState.refresh is LoadState.NotLoading && moviesAdapter.itemCount == 0) {
                binding.txtNotFound.isVisible = true
                binding.txtNotFound.text = getString(R.string.no_data_found)
            } else {
                binding.txtNotFound.isVisible = false
            }
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies
                    .map { networkRequest ->
                        when (networkRequest) {
                            is NetworkRequest.Success -> {
                                networkRequest.data?.map { movieResult ->
                                    ResponseMovieType.Movies(movieResult)
                                } ?: PagingData.empty()
                            }
                            is NetworkRequest.Error -> {
                                showErrorSnackbar(binding.root, networkRequest.message.toString())
                                showErrorSnackbar(binding.root, networkRequest.message.toString())

                                if (networkRequest.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                    binding.txtNotFound.visibility = View.VISIBLE
                                    binding.txtNotFound.text = Constants.Message.NO_INTERNET_CONNECTION
                                }

                                PagingData.empty()
                            }

                            is NetworkRequest.Loading -> PagingData.empty<ResponseMovieType>()
                        }
                    }
                    .collectLatest { mappedMovies ->
                        moviesAdapter.submitData(mappedMovies as PagingData<ResponseMovieType>)
                    }
            }
        }
    }

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private val clickMovie = { movieId : Int ->
        val action = FavoriteFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movieId)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}