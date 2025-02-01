package azari.amirhossein.filmora.ui.detail

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
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.adapter.SimilarMovieAdapter
import azari.amirhossein.filmora.databinding.FragmentSimilarMovieBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimilarMovieFragment : Fragment() {
    private var _binding: FragmentSimilarMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels({ requireParentFragment() })
    private val similarMovieAdapter by lazy { SimilarMovieAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSimilarMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeSimilarMovies()
        similarMovieAdapter.setOnItemClickListener(clickMovie)
    }

    private fun observeSimilarMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieSimilar.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            similarMovieAdapter.differ.submitList(result.data?.results)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvSimilar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarMovieAdapter
        }
    }

    private val clickMovie = { movie: ResponseMovieSimilar.Result ->
        val action = SimilarMovieFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




