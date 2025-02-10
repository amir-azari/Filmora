package azari.amirhossein.filmora.ui.movies

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MoviesAdapter
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.databinding.FragmentMovieSectionBinding
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MovieSectionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieSectionFragment : Fragment() {
    // Binding
    private var _binding: FragmentMovieSectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapterMovie: MoviesAdapter

    private val viewModel: MovieSectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieSectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionType = arguments?.getString(Constants.SectionType.SECTION_TYPE) ?: return
        when(sectionType){
            Constants.SectionType.NOW_PLAYING -> setActionBarTitle(getString(R.string.playing_in_theatres))
            else -> setActionBarTitle(sectionType)
        }

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovies(sectionType).collect { state ->
                when (state) {
                    is NetworkRequest.Loading -> {
                        adapterMovie.addLoadStateListener { loadState ->
                            binding.progressBar.visibility =
                                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

                            binding.internetLay.visibility =
                                if (loadState.source.refresh is LoadState.Error) View.VISIBLE else View.GONE
                        }
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

    // Click media
    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action = MovieSectionFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
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
        binding.internetLay.visibility = View.GONE
    }

    private fun showSuccess() {
        binding.internetLay.visibility = View.GONE
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