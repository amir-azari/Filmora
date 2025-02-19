package azari.amirhossein.filmora.ui.detail.movie

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.CollectionPartsAdapter
import azari.amirhossein.filmora.databinding.FragmentCollectionBinding
import azari.amirhossein.filmora.models.detail.ResponseCollectionDetails
import azari.amirhossein.filmora.ui.people.PeopleSectionFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.viewmodel.CollectionDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    // Binding
    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var collectionAdapter :CollectionPartsAdapter
    // ViewModel
    private val viewModel: CollectionDetailViewModel by viewModels()

    // Arguments
    private val collectionId: Int by lazy {
        arguments?.getInt("collection_id") ?: 0
    }

    // Base URL for loading images
    val baseUrl = Constants.Network.IMAGE_BASE_URL
    private lateinit var originalScaleType: ImageView.ScaleType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle("")
        originalScaleType = binding.imgPoster.scaleType

        setupRecyclerView()
        setupObservers()

        viewModel.getCollectionDetails(collectionId)

        collectionAdapter.setOnItemClickListener(click)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectionDetails.collect { state ->
                    when (state) {
                        is NetworkRequest.Loading -> showLoading()

                        is NetworkRequest.Success -> {
                            showSuccess()
                            state.data?.let {
                                showContent(it)
                                collectionAdapter.differ.submitList(it.parts)

                            }
                        }
                        is NetworkRequest.Error -> {
                            showError()
                            if (state.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                // Show no internet UI
                                binding.internetLay.visibility = View.VISIBLE
                            }
                            showErrorSnackbar(binding.root, state.message.toString())
                        }
                    }
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

    private fun showLoading() {
        binding.apply {
            mainContentContainer.visibility = View.GONE
            internetLay.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun showSuccess() {
        binding.internetLay.visibility  =View.GONE
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.apply {
            progressBar.visibility = View.GONE
            mainContentContainer.visibility = View.GONE
            setActionBarTitle("")
        }
    }

    private fun showContent(data: ResponseCollectionDetails) {
        binding.apply {
            progressBar.visibility = View.GONE
            mainContentContainer.visibility = View.VISIBLE
            internetLay.visibility = View.GONE


            txtTitle.text = data.name
            setActionBarTitle(data.name)

            txtOverview.text = data.overview ?: getString(R.string.no_description)

            // Load poster
            val posterFullPath = if (data.posterPath.isNullOrEmpty()) {
                null
            } else {
                baseUrl + Constants.ImageSize.ORIGINAL + data.posterPath
            }
            imgPoster.loadImageWithoutShimmer(
                posterFullPath,
                R.drawable.image_slash_medium,
                R.drawable.image_medium,
                originalScaleType,
                true
            )


            // Load backdrop
            val backdropFullPath = if (data.backdropPath.isNullOrEmpty()) {
                null
            } else {
                baseUrl + Constants.ImageSize.ORIGINAL + data.backdropPath
            }
            imgBackdrop.loadImageWithoutShimmer(
                backdropFullPath,
                R.drawable.image_slash_large,
                R.drawable.image_large,
                originalScaleType,
                true
            )

        }
    }



    private fun setupRecyclerView() {
        binding.rvCollection.apply {
            layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically(): Boolean = false
            }
            adapter = collectionAdapter
        }
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private val click = { id : Int ->
        val action = CollectionFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,id)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
