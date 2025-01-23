package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.adapter.SimilarMovieAdapter
import azari.amirhossein.filmora.adapter.SimilarTvAdapter
import azari.amirhossein.filmora.databinding.FragmentSimilarBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieRecommendations
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimilarMovieFragment : Fragment(), MediaUpdateable<ResponseMovieSimilar> {
    private var _binding: FragmentSimilarBinding? = null
    private val binding get() = _binding!!


    private val similarMovieAdapter by lazy { SimilarMovieAdapter() }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSimilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        similarMovieAdapter.setOnItemClickListener(clickMovie)

    }

    private fun setupRecyclerView() {
        binding.rvSimilar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarMovieAdapter
        }
    }

    override fun updateMediaData(data: ResponseMovieSimilar?) {
        similarMovieAdapter.differ.submitList(data?.results)

    }
    private val clickMovie = { movie: ResponseMovieSimilar.Result ->
        val action = HomeFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,movie.id)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




