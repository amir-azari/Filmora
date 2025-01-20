package azari.amirhossein.filmora.ui.detail

import android.animation.ValueAnimator
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.CreditAdapter
import azari.amirhossein.filmora.adapter.GenresAdapter
import azari.amirhossein.filmora.adapter.SeasonsAdapter
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentTvDetailsBinding
import azari.amirhossein.filmora.models.detail.ResponseTvDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.getFullLanguageName
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.observeLoginStatus
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toCompanyNames
import azari.amirhossein.filmora.utils.toCountryNames
import azari.amirhossein.filmora.utils.toCreatorNames
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.utils.toFormattedVoteAverage
import azari.amirhossein.filmora.utils.toNetworkNames
import azari.amirhossein.filmora.utils.toSpokenLanguagesText
import azari.amirhossein.filmora.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TvDetailsFragment : Fragment() {
    //Binding
    private var _binding: FragmentTvDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var genresAdapter: GenresAdapter

    @Inject
    lateinit var seasonsAdapter: SeasonsAdapter

    @Inject
    lateinit var creditAdapter: CreditAdapter

    // State variables for overview expansion and configuration
    private var isOverviewExpanded = false
    private val overviewMaxLines = Constants.Defaults.OVERVIEW_MAX_LINES

    private val viewModel: DetailsViewModel by viewModels()

    // Arguments passed to the fragment
    private lateinit var args: MovieDetailFragmentArgs
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
        _binding = FragmentTvDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Extract fragment arguments
        args = MovieDetailFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        viewModel.getMediaDetails(mediaId, Constants.MediaType.TV)


        setupUI()
        binding.overviewContainer.setupOverviewExpansion(
            txtOverview = binding.txtOverview,
            imgExpand = binding.imgExpand,
            overviewMaxLines = overviewMaxLines,
            isOverviewExpanded = false
        ) { isExpanded ->

            isOverviewExpanded = isExpanded
        }

        observeViewModel()

        viewLifecycleOwner.observeLoginStatus(
            sessionManager = sessionManager,
            onGuestAction = {
                binding.cvMediaAction.visibility = View.GONE
            },
            onUserAction = {
                binding.cvMediaAction.visibility = View.VISIBLE
            }
        )

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
        binding.rvTvSeasons.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = seasonsAdapter
        }

        binding.rvCastAndCrew.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = creditAdapter
            setHasFixedSize(true)
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

                val initialHeight = txtOverview.height
                txtOverview.maxLines = if (isOverviewExpanded) Int.MAX_VALUE else overviewMaxLines
                txtOverview.measure(
                    View.MeasureSpec.makeMeasureSpec(txtOverview.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                val targetHeight = txtOverview.measuredHeight

                ValueAnimator.ofInt(initialHeight, targetHeight).apply {
                    duration = 400
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener { animator ->
                        val value = animator.animatedValue as Int
                        txtOverview.layoutParams.height = value
                        txtOverview.requestLayout()
                    }
                    start()
                }

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
                    result?.getContentIfNotHandled()?.let { it ->
                        when (it) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                it.data?.let { mediaItem ->
                                    showSuccess()
                                    mediaItem.tv?.let { data -> bindUI(data) }
                                    mediaItem.credits?.cast?.let { it1 ->
                                        creditAdapter.submitList(
                                            it1.filterNotNull()
                                        )
                                    }

                                }
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

    private fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.mainContentContainer.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.apply {
            progressBar.visibility = View.GONE
            mainContentContainer.visibility = View.GONE
            (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
        }
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

    private fun bindUI(data: ResponseTvDetails) {
        binding.apply {
            data.apply {

                // Tv show name and overview
                txtTitle.text = name ?: Constants.Defaults.NOT_APPLICABLE
                setActionBarTitle(name)
                txtOverview.text = overview ?: Constants.Defaults.OVERVIEW
                handleOverviewExpansion()

                // Vote details
                txtVoteCount.text = voteCount?.toString() ?: Constants.Defaults.VOTE_COUNT
                txtVoteAverage.text = voteAverage?.toFormattedVoteAverage()
                    ?: Constants.Defaults.VOTE_AVERAGE.toString()
                ratingBar.rating = voteAverage?.div(2)?.toFloat() ?: 0f

                genresAdapter.differ.submitList(genres)

                // Load poster
                imgPoster.loadImageWithoutShimmer(
                    posterPath,
                    baseUrl + Constants.ImageSize.ORIGINAL,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium
                )

                // Load backdrop
                imgBackdrop.loadImageWithoutShimmer(
                    backdropPath,
                    baseUrl + Constants.ImageSize.ORIGINAL,
                    R.drawable.image_slash_large,
                    R.drawable.image_large
                )
                // Seasons
                seasonsAdapter.differ.submitList(seasons)

                // Creators
                if (createdBy.isNullOrEmpty()) {
                    txtCreatorValue.visibility = View.GONE
                    txtCreator.visibility = View.GONE
                }
                txtCreatorValue.text = createdBy.toCreatorNames()


                // release
                if (firstAirDate.isNullOrEmpty()) {
                    txtReleaseValue.visibility = View.GONE
                    txtRelease.visibility = View.GONE
                }
                txtReleaseValue.text = getString(
                    R.string.release_value,
                    firstAirDate?.toFormattedDate() ?: Constants.Defaults.NOT_APPLICABLE,
                    status
                )

                // Networks
                txtNetworksValue.text = networks.toNetworkNames()

                originalLanguage.getFullLanguageName(viewModel.languages.value?.peekContent())
                    .also { txtLanguageValue.text = it }

                // Spoken Languages
                txtSpokenLanguagesValue.text = spokenLanguages.toSpokenLanguagesText()
                txtSeasonsAndEpisodesValue.text = getString(
                    R.string.seasons_and_episodes,
                    numberOfSeasons?.toString() ?: Constants.Defaults.NOT_APPLICABLE,
                    numberOfEpisodes?.toString() ?: Constants.Defaults.NOT_APPLICABLE
                )

                txtTypeValue.text = type

                // Tagline
                if (tagline.isNullOrEmpty()) {
                    txtTag.visibility = View.GONE
                } else {
                    txtTag.visibility = View.VISIBLE
                    txtTag.text = tagline
                }

                // Production companies and countries
                txtProductionCountriesValue.text = productionCountries.toCountryNames()
                txtProductionCompaniesValue.text = productionCompanies?.toCompanyNames()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
