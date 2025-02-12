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
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.adapter.VideoAdapter
import azari.amirhossein.filmora.databinding.FragmentVideoBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.MediaDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoFragment : Fragment() {
    //Binding
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaDetailsViewModel by viewModels({ requireParentFragment() })
    private val videoAdapter by lazy { VideoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root


    }
    private fun observeVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videos.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            binding.btnAllVideos.visibility =View.GONE
                            if (result.data?.results?.size!! > 10){
                                binding.btnAllVideos.visibility =View.VISIBLE
                            }
                            videoAdapter.submitList(result.data.results)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
    private fun setupRecyclerView() {
        binding.rvVideo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = videoAdapter
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeVideos()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}