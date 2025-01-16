package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.GenresAdapter
import azari.amirhossein.filmora.databinding.FragmentDetailBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
import azari.amirhossein.filmora.models.home.HomeMediaItem
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.DetailsMovieViewModel
import coil3.load
import coil3.request.crossfade
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {
    //Binding
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var genresAdapter: GenresAdapter

    // State variables for overview expansion and configuration
    private var isOverviewExpanded = false
    private val overviewMaxLines = Constants.Defaults.OVERVIEW_MAX_LINES

    private val viewModel: DetailsMovieViewModel by viewModels()

    // Arguments passed to the fragment
    private lateinit var args: DetailFragmentArgs
    private var mediaId: Int = -1
    private var mediaType: String = ""

    // Base URL for loading images
    val baseUrl = Constants.Network.IMAGE_BASE_URL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Extract fragment arguments
        args = DetailFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        when (mediaType) {
            Constants.MediaType.MOVIE -> viewModel.getMediaDetails(mediaId, Constants.MediaType.MOVIE)
            Constants.MediaType.TV -> viewModel.getMediaDetails(mediaId, Constants.MediaType.TV)
        }

        setupUI()
        setupOverviewExpansion()
        observeViewModel()
    }

    private fun setupUI() {
        setupRecyclerViews()
    }
    private fun setupRecyclerViews() {
        // Set up the RecyclerView for displaying genres
        binding.rvGenre.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = genresAdapter
        }
    }
    private fun setupOverviewExpansion() {
        binding.apply {
            val clickListener = View.OnClickListener {
                isOverviewExpanded = !isOverviewExpanded

                TransitionManager.beginDelayedTransition(
                    overviewContainer,
                    AutoTransition()
                        .setDuration(400)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                )

                // Set max lines
                txtOverview.maxLines = if (isOverviewExpanded) Int.MAX_VALUE else overviewMaxLines

                // Rotate the arrow icon
                imgExpand.animate()
                    .rotation(if (isOverviewExpanded) 180f else 0f)
                    .setDuration(400)
                    .start()
            }

            // Set click listeners
            txtOverview.setOnClickListener(clickListener)
            imgExpand.setOnClickListener(clickListener)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mediaDetails.collect { result ->
                    result?.getContentIfNotHandled()?.let {
                        when (it) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }
                            is NetworkRequest.Success -> {
                                it.data?.let { mediaItem -> showSuccess(mediaItem) }
                            }
                            is NetworkRequest.Error -> {
                                showError()
                                if (it.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                    // Show no internet UI
                                    binding.internetLay.visibility = View.VISIBLE
                                }
                                showErrorSnackbar(binding.root, it.message.toString())
                            }
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
            progressBar.visibility = View.VISIBLE
            mainContentContainer.visibility = View.GONE
            internetLay.visibility = View.GONE
        }
    }

    private fun showSuccess(mediaItem: HomeMediaItem) {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.VISIBLE

        when {
            // Bind movie details
            mediaItem.movie != null -> bindMovieToUI(mediaItem.movie)
            // Bind TV show details
            mediaItem.tv != null -> bindTvToUI(mediaItem.tv)
        }
    }

    private fun showError() {
        binding.apply {
            progressBar.visibility = View.GONE
            mainContentContainer.visibility = View.GONE
            (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
        }
    }


    private fun bindMovieToUI(data: ResponseMovieDetails) {
        // Bind movie-specific details to the UI
        bindCommonUI(
            title = data.title,
            overview = data.overview,
            voteCount = data.voteCount,
            voteAverage = data.voteAverage,
            genres = data.genres,
            posterPath = data.posterPath,
            backdropPath = data.backdropPath
        )
    }

    private fun bindTvToUI(data: ResponseTvDetails) {
        // Bind TV-specific details to the UI
        bindCommonUI(
            title = data.name,
            overview = data.overview,
            voteCount = data.voteCount,
            voteAverage = data.voteAverage,
            genres = data.genres,
            posterPath = data.posterPath,
            backdropPath = data.backdropPath
        )
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun handleOverviewExpansion() {
        // Configure overview text to show/hide based on the line count
        binding.txtOverview.post {
            val lineCount = binding.txtOverview.lineCount
            if (lineCount > overviewMaxLines) {
                binding.txtOverview.maxLines = overviewMaxLines
                binding.imgExpand.visibility = View.VISIBLE
            } else {
                binding.imgExpand.visibility = View.INVISIBLE
            }
        }
    }

    private fun bindCommonUI(
        title: String?,
        overview: String?,
        voteCount: Int?,
        voteAverage: Double?,
        genres: List<ResponseGenresList.Genre?>?,
        posterPath: String?,
        backdropPath: String?
    ) {
        // Bind common UI elements for both movies and TV shows
        binding.apply {
            txtTitle.text = title ?: Constants.Defaults.TITLE
            setActionBarTitle(title)

            txtOverview.text = overview ?: Constants.Defaults.OVERVIEW
            handleOverviewExpansion()

            txtVoteCount.text = voteCount?.toString() ?: Constants.Defaults.VOTE_COUNT
            txtVoteAverage.text = String.format(Locale.getDefault(), "%.1f", voteAverage ?: Constants.Defaults.VOTE_AVERAGE)

            ratingBar.rating = voteAverage?.div(2)?.toFloat() ?: 0f

            genresAdapter.differ.submitList(genres)

            // Load poster
            val fullPosterPath = posterPath?.let { baseUrl + Constants.ImageSize.W500 + it } ?: ""
            imgPoster.load(fullPosterPath) {
                crossfade(true)
                crossfade(400)
                listener(
                    onError = { _, _ ->
                        imgPoster.setImageResource(R.drawable.image_slash_medium)
                        imgPoster.scaleType = ImageView.ScaleType.CENTER
                        imgPoster.strokeWidth = 2.0f
                    }
                )
            }

            // Load backdrop
            val fullBackdropPath = backdropPath?.let { baseUrl + Constants.ImageSize.ORIGINAL + it } ?: ""
            imgBackdrop.load(fullBackdropPath) {
                crossfade(true)
                crossfade(400)
                listener(
                    onError = { _, _ ->
                        imgBackdrop.setImageResource(R.drawable.image_slash_large)
                        imgBackdrop.scaleType = ImageView.ScaleType.CENTER
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
