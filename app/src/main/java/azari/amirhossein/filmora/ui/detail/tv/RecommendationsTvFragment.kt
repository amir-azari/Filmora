package azari.amirhossein.filmora.ui.detail.tv

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
import azari.amirhossein.filmora.adapter.RecommendationTvAdapter
import azari.amirhossein.filmora.databinding.FragmentRecommendationsTvBinding
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.TvDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendationsTvFragment : Fragment() {
    private var _binding: FragmentRecommendationsTvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })
    private val recommendationsTvAdapter by lazy { RecommendationTvAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeRecommendationTvs()
        recommendationsTvAdapter.setOnItemClickListener(clickTv)

    }
    private fun observeRecommendationTvs() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            recommendationsTvAdapter.differ.submitList(result.data?.recommendations?.results)
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
            adapter = recommendationsTvAdapter
        }
    }
    //Click media
    private val clickTv = { tv: ResponseTvDetails.Recommendations.Result ->
        val action = RecommendationsTvFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}