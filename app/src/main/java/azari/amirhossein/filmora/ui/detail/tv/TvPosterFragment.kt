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
import azari.amirhossein.filmora.adapter.PosterAdapter
import azari.amirhossein.filmora.databinding.FragmentTvPosterBinding
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.TvDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TvPosterFragment : Fragment(){
    //Binding
    private var _binding: FragmentTvPosterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })
    private val posterAdapter by lazy { PosterAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTvPosterBinding.inflate(inflater, container, false)
        return binding.root


    }
    private fun observePoster() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            binding.btnAllPosters.visibility =View.GONE
                            if (result.data?.images?.posters?.size!! > 10){
                                binding.btnAllPosters.visibility =View.VISIBLE
                            }
                            posterAdapter.submitList(result.data.images.posters)
                            navTo(result.data.images)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePoster()

    }
    private fun navTo(data : ResponseImage){
        binding.btnAllPosters.setClickAnimation {
            val action = TvPosterFragmentDirections.actionToMediaGalleryFragment(media = data, type = Constants.MediaGalleryType.POSTER , video = null)
            findNavController().navigate(
                action
            )
        }
    }
    private fun setupRecyclerView() {
        binding.rvPoster.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = posterAdapter
            setHasFixedSize(false)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}