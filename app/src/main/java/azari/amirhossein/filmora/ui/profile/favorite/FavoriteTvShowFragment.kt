package azari.amirhossein.filmora.ui.profile.favorite

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.adapter.TvsAdapter
import azari.amirhossein.filmora.databinding.FragmentFavoriteTvShowBinding
import azari.amirhossein.filmora.models.tv.ResponseTvType
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteTvShowFragment : Fragment() {

    private var _binding: FragmentFavoriteTvShowBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tvsAdapter: TvsAdapter

    private val viewModel: FavoriteViewModel by  viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteTvShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMovies()

        tvsAdapter.setOnItemClickListener(clickTv)


    }
    private fun setupRecyclerView() {
        val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()
        binding.rvTvShows.layoutManager = flexboxLayoutManager

        val concatAdapter = tvsAdapter.withLoadStateFooter(
            footer = DataLoadStateAdapter { tvsAdapter.retry() }
        )
        binding.rvTvShows.adapter = concatAdapter

        tvsAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            binding.rvTvShows.isVisible = loadState.refresh is LoadState.NotLoading

            if (loadState.refresh is LoadState.NotLoading && tvsAdapter.itemCount == 0) {
                binding.txtNotFound.isVisible = true
                binding.txtNotFound.text = getString(R.string.no_data_found)
            } else {
                binding.txtNotFound.isVisible = false
            }
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvs
                    .map { networkRequest ->
                        when (networkRequest) {
                            is NetworkRequest.Success -> {
                                networkRequest.data?.map { movieResult ->
                                    ResponseTvType.Tvs(movieResult)
                                } ?: PagingData.empty()
                            }
                            is NetworkRequest.Error -> {
                                showErrorSnackbar(binding.root, networkRequest.message.toString())
                                if (networkRequest.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                    binding.txtNotFound.visibility = View.VISIBLE
                                    binding.txtNotFound.text = Constants.Message.NO_INTERNET_CONNECTION
                                }
                                PagingData.empty()
                            }

                            is NetworkRequest.Loading -> PagingData.empty<ResponseTvType>()
                        }
                    }
                    .collectLatest { mappedMovies ->
                        tvsAdapter.submitData(mappedMovies as PagingData<ResponseTvType>)
                    }
            }
        }
    }
    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private val clickTv = { tvId: Int ->
        val action = FavoriteFragmentDirections.actionToTvDetail(Constants.MediaType.TV, tvId)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
