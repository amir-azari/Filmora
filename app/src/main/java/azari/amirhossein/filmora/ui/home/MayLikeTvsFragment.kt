package azari.amirhossein.filmora.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.adapter.TvsAdapter
import azari.amirhossein.filmora.databinding.FragmentMayLikeTvsBinding
import azari.amirhossein.filmora.ui.movies.MovieSectionFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.MayLikeTvsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MayLikeTvsFragment : Fragment() {
    // Binding
    private var _binding: FragmentMayLikeTvsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapterTv:TvsAdapter

    private val viewModel: MayLikeTvsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMayLikeTvsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle("Series you may like")
        adapterTv.setOnItemClickListener(clickTv)


        val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()

        binding.rvTvs.layoutManager = flexboxLayoutManager

        val concatAdapter = adapterTv.withLoadStateFooter(
            footer = DataLoadStateAdapter { adapterTv.retry() }
        )

        binding.rvTvs.adapter = concatAdapter

        // Collecting the Tvs
        viewLifecycleOwner.lifecycleScope.launch  {
            viewModel.tvs.collect { state ->
                when (state) {
                    is NetworkRequest.Loading -> {
                        adapterTv.addLoadStateListener { loadState ->
                            binding.progressBar.visibility =
                                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

                            binding.internetLay.visibility =
                                if (loadState.source.refresh is LoadState.Error) View.VISIBLE else View.GONE
                        }
                    }
                    is NetworkRequest.Success -> {
                        showSuccess()
                        state.data?.let { adapterTv.submitData(it) }
                    }
                    is NetworkRequest.Error -> {
                        showError()
                        if (state.message == Constants.Message.NO_INTERNET_CONNECTION) {
                            binding.internetLay.visibility = View.VISIBLE
                        }
                        showErrorSnackbar(binding.root, state.message.toString())
                    }
                }
            }
        }
    }
    //Click media
    private val clickTv = { tvId: Int ->
        val action = MovieSectionFragmentDirections.actionToTvDetail(Constants.MediaType.TV, tvId)
        findNavController().navigate(action)
    }

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTvs.visibility = View.GONE
        binding.internetLay.visibility  = View.GONE


    }

    private fun showSuccess() {
        binding.internetLay.visibility  = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.rvTvs.visibility = View.VISIBLE


    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.rvTvs.visibility = View.GONE

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
