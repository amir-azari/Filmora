package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.adapter.RecommendationTvAdapter
import azari.amirhossein.filmora.databinding.FragmentRecommendationsBinding
import azari.amirhossein.filmora.models.detail.ResponseTvRecommendations
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsTvFragment : Fragment(), MediaUpdateable<ResponseTvRecommendations> {
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    private val recommendationsTvAdapter by lazy { RecommendationTvAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        recommendationsTvAdapter.setOnItemClickListener(clickTv)

    }

    private fun setupRecyclerView() {
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsTvAdapter
        }
    }
    //Click media
    private val clickTv = { tv: ResponseTvRecommendations.Result ->
        val action = HomeFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }
    override fun updateMediaData(data: ResponseTvRecommendations?) {
        recommendationsTvAdapter.differ.submitList(data?.results)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}