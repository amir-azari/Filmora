package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.adapter.RecommendationMovieAdapter
import azari.amirhossein.filmora.adapter.RecommendationTvAdapter
import azari.amirhossein.filmora.databinding.FragmentRecommendationsBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsMovieFragment : Fragment(), MediaUpdateable<ResponseMovieRecommendations> {
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!


    private val recommendationsMovieAdapter by lazy { RecommendationMovieAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        recommendationsMovieAdapter.setOnItemClickListener(clickMovie)

    }

    private fun setupRecyclerView() {
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsMovieAdapter
        }
    }

    override fun updateMediaData(data: ResponseMovieRecommendations?) {
        recommendationsMovieAdapter.differ.submitList(data?.results)
    }
    private val clickMovie = { movie: ResponseMovieRecommendations.Result ->
        val action = HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,movie.id)
        findNavController().navigate(action)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}