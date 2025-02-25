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
import azari.amirhossein.filmora.adapter.SimilarTvAdapter
import azari.amirhossein.filmora.databinding.FragmentSimilarTvBinding
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.TvDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimilarTvFragment : Fragment() {
    private var _binding: FragmentSimilarTvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })
    private val similarTvAdapter by lazy { SimilarTvAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimilarTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeSimilarTvs()
        similarTvAdapter.setOnItemClickListener(clickTv)
    }

    private fun observeSimilarTvs() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            similarTvAdapter.differ.submitList(result.data?.similar?.results)
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
            adapter = similarTvAdapter
        }
    }
    //Click media
    private val clickTv = { tv: ResponseTvDetails.Similar.Result ->
        val action = SimilarTvFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}