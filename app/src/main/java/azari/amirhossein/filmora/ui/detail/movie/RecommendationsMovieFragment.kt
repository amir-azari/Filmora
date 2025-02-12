package azari.amirhossein.filmora.ui.detail.movie

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
import azari.amirhossein.filmora.adapter.RecommendationMovieAdapter
import azari.amirhossein.filmora.databinding.FragmentRecommendationsMovieBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.MediaDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendationsMovieFragment : Fragment() {
    private var _binding: FragmentRecommendationsMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaDetailsViewModel by viewModels({ requireParentFragment() })
    private val recommendationsMovieAdapter by lazy { RecommendationMovieAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecommendationsMovieBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeRecommendationMovies()
        recommendationsMovieAdapter.setOnItemClickListener(clickMovie)

    }

    private fun observeRecommendationMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieRecommendations.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            recommendationsMovieAdapter.differ.submitList(result.data?.results)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsMovieAdapter
        }
    }

    private val clickMovie = { movie: ResponseMovieRecommendations.Result ->
        val action = RecommendationsMovieFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE, movie.id)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}