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
import azari.amirhossein.filmora.adapter.BackdropAdapter
import azari.amirhossein.filmora.databinding.FragmentBackdropBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BackdropFragment : Fragment() {
    //Binding
    private var _binding: FragmentBackdropBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels({ requireParentFragment() })
    private val backdropAdapter by lazy { BackdropAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBackdropBinding.inflate(inflater, container, false)
        return binding.root


    }
    private fun observeBackdrops() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            binding.btnAllBackdrops.visibility =View.GONE
                            if (result.data?.backdrops?.size!! > 10){
                                binding.btnAllBackdrops.visibility =View.VISIBLE
                            }
                            backdropAdapter.submitList(result.data.backdrops)
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