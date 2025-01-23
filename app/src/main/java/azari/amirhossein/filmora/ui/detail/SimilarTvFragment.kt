package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.SimilarTvAdapter
import azari.amirhossein.filmora.databinding.FragmentSimilarBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieSimilar
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimilarTvFragment : Fragment(), MediaUpdateable<ResponseTvSimilar> {
    private var _binding: FragmentSimilarBinding? = null
    private val binding get() = _binding!!

    private val similarTvAdapter by lazy { SimilarTvAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        similarTvAdapter.setOnItemClickListener(clickTv)
    }

    private fun setupRecyclerView() {
        binding.rvSimilar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarTvAdapter
        }
    }
    //Click media
    private val clickTv = { tv: ResponseTvSimilar.Result ->
        val action = HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }
    override fun updateMediaData(data: ResponseTvSimilar?) {
        similarTvAdapter.differ.submitList(data?.results)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}