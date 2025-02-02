package azari.amirhossein.filmora.ui.home

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MoviesAdapter
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.databinding.FragmentMayLikeMoviesBinding
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MayLikeMoviesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MayLikeMoviesFragment : Fragment() {
    // Binding
    private var _binding: FragmentMayLikeMoviesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapterMovie: MoviesAdapter

    private val viewModel: MayLikeMoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMayLikeMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle("Movies you may like")
        adapterMovie.setOnItemClickListener(clickMovie)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMovies.layoutManager = gridLayoutManager

        val concatAdapter = adapterMovie.withLoadStateFooter(
            footer = DataLoadStateAdapter { adapterMovie.retry() }
        )
        binding.rvMovies.adapter = concatAdapter

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    DataLoadStateAdapter.VIEW_TYPE_LOAD_STATE -> 3
                    else -> 1
                }
            }
        }
        // Collecting the movies
        lifecycleScope.launch {
            viewModel.movies.collect { state ->
                when (state) {
                    is NetworkRequest.Loading -> {
                        showLoading()
                    }
                    is NetworkRequest.Success -> {
                        showSuccess()
                        state.data?.let { adapterMovie.submitData(it) }
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
    //Click media
    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action = MayLikeMoviesFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
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
        binding.rvMovies.visibility = View.GONE
        binding.internetLay.visibility  =View.GONE


    }

    private fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.rvMovies.visibility = View.VISIBLE


    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.rvMovies.visibility = View.GONE

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

