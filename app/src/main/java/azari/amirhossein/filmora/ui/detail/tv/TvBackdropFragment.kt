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
import azari.amirhossein.filmora.adapter.BackdropAdapter
import azari.amirhossein.filmora.databinding.FragmentTvBackdropBinding
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.TvDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TvBackdropFragment : Fragment() {
    //Binding
    private var _binding: FragmentTvBackdropBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val backdropAdapter by lazy { BackdropAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTvBackdropBinding.inflate(inflater, container, false)
        return binding.root


    }
    private fun observeBackdrops() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            binding.btnAllBackdrops.visibility =View.GONE
                            if (result.data?.images?.backdrops?.size!! > 10){
                                binding.btnAllBackdrops.visibility =View.VISIBLE
                            }
                            backdropAdapter.submitList(result.data.images.backdrops)
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
        observeBackdrops()

    }

    private fun navTo(data : ResponseImage){
        binding.btnAllBackdrops.setClickAnimation {
            val action = TvBackdropFragmentDirections.actionToMediaGalleryFragment(media = data, type = Constants.MediaGalleryType.BACKDROP , video = null)
            findNavController().navigate(
                action
            )
        }
    }
    private fun setupRecyclerView() {
        binding.rvBackdrop.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = backdropAdapter
            setHasFixedSize(false)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}