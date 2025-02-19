package azari.amirhossein.filmora.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.adapter.MoviesAdapter
import azari.amirhossein.filmora.databinding.FragmentSearchMoviesBinding
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.ui.movies.MovieSectionFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchMoviesFragment : Fragment() {
    private var _binding: FragmentSearchMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var moviesAdapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesAdapter.setOnItemClickListener(clickMovie)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMovies.layoutManager = gridLayoutManager

        val concatAdapter = moviesAdapter.withLoadStateFooter(
            footer = DataLoadStateAdapter { moviesAdapter.retry() }
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchMoviesResults
                    .map { pagingData ->
                        pagingData.map<ResponseMoviesList.Result, ResponseMovieType> { movieResult ->
                            ResponseMovieType.Movies(movieResult)
                        }
                    }
                    .collectLatest { mappedMovies ->
                        moviesAdapter.submitData(mappedMovies)
                    }
            }
        }

    }
    // Click media
    private val clickMovie = { movieId : Int ->
        val action = MovieSectionFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movieId)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
