package azari.amirhossein.filmora.ui.detail.tv

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.CastAndCrewAdapter
import azari.amirhossein.filmora.adapter.GenresAdapter
import azari.amirhossein.filmora.adapter.SeasonsAdapter
import azari.amirhossein.filmora.adapter.SimilarTvRecommendationsPagerAdapter
import azari.amirhossein.filmora.adapter.TvGalleryPagerAdapter
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentTvDetailsBinding
import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails
import azari.amirhossein.filmora.models.detail.SpokenLanguage
import azari.amirhossein.filmora.ui.detail.AccountStatesUiState
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.observeLoginStatus
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toCompanyNames
import azari.amirhossein.filmora.utils.toCountryNames
import azari.amirhossein.filmora.utils.toCreatorNames
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.utils.toFormattedVoteAverage
import azari.amirhossein.filmora.utils.toNetworkNames
import azari.amirhossein.filmora.utils.toSpokenLanguagesText
import azari.amirhossein.filmora.viewmodel.TvDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TvDetailsFragment : Fragment() {

    // Binding and injected dependencies
    private var _binding: FragmentTvDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var genresAdapter: GenresAdapter

    @Inject
    lateinit var seasonsAdapter: SeasonsAdapter

    @Inject
    lateinit var castAndCrewAdapter: CastAndCrewAdapter

    // Adapter for similar and recommendations
    private var similarAndRecommendationsPagerAdapter: SimilarTvRecommendationsPagerAdapter? = null
    private var tvGalleryPagerAdapter: TvGalleryPagerAdapter? = null

    // State variables for overview expansion
    private var isOverviewExpanded = false
    private val overviewMaxLines = Constants.Defaults.OVERVIEW_MAX_LINES
    private val overviewReviewsMaxLines = 3

    private val viewModel: TvDetailViewModel by viewModels()

    // Fragment arguments and media info
    private lateinit var args: TvDetailsFragmentArgs
    private var mediaId: Int = -1
    private var mediaType: String = ""

    // Base URL for images and original image scale type
    private val baseUrl = Constants.Network.IMAGE_BASE_URL
    private lateinit var originalScaleType: ImageView.ScaleType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTvDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originalScaleType = binding.imgPoster.scaleType

        // Initialize data and UI
        initializeFragmentData()
        initializeUIComponents()
        setupObservers()
        restoreScrollPosition(savedInstanceState)
    }

    // Initialize fragment data
    private fun initializeFragmentData() {
        args = TvDetailsFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType
        setActionBarTitle("")
        viewModel.getTvAccountStates(mediaId)
        viewModel.getTvDetails(mediaId)
    }

    // Initialize views and UI settings
    private fun initializeUIComponents() {
        setupViewPagersSimilarAndRecommendations()
        setupOverviewExpansions()
        setupRecyclerViews()
        setupAccountActions()
        castAndCrewAdapter.setOnItemClickListener { cast ->
            findNavController().navigate(
                TvDetailsFragmentDirections.actionToPeopleDetailFragment(cast.id)
            )
        }
    }

    // Retrieve the scroll position if any
    private fun restoreScrollPosition(savedInstanceState: Bundle?) {
        savedInstanceState?.getInt("scroll_position")?.let { pos ->
            binding.nestedScrollView.post { binding.nestedScrollView.scrollTo(0, pos) }
        }
    }

    // Setting the development of texts (overview and review)
    private fun setupOverviewExpansions() {
        binding.overviewContainer.setupOverviewExpansion(
            txtOverview = binding.txtOverview,
            imgExpand = binding.imgExpand,
            overviewMaxLines = overviewMaxLines,
            isOverviewExpanded = false
        ) { isExpanded ->
            isOverviewExpanded = isExpanded
        }
        binding.reviewContainer.setupOverviewExpansion(
            txtOverview = binding.tvReviewContent,
            imgExpand = binding.imgReviewContentExpand,
            overviewMaxLines = overviewReviewsMaxLines,
            isOverviewExpanded = false
        ) { isExpanded ->
            isOverviewExpanded = isExpanded
        }
    }

    // Set ViewPager for similar and recommended videos
    private fun setupViewPagersSimilarAndRecommendations() {
        val viewPager = binding.vpSimilarRecommendation
        val tabLayout = binding.tlSimilarRecommendation

        similarAndRecommendationsPagerAdapter = SimilarTvRecommendationsPagerAdapter(this)
        viewPager.adapter = similarAndRecommendationsPagerAdapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.similar_tv_show)
                1 -> getString(R.string.recommendations)
                else -> ""
            }
        }.attach()
    }

    // Set up RecyclerView for genres and actors
    private fun setupRecyclerViews() {
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
            adapter = castAndCrewAdapter
            setHasFixedSize(true)
        }
    }

    // Set action buttons (Favorite, Watchlist, Rate)
    private fun setupAccountActions() {
        binding.apply {
            btnFavorite.setClickAnimation {
                val currentState =
                    (viewModel.accountStates.value as? AccountStatesUiState.Success)?.data?.favorite
                        ?: false
                viewModel.toggleFavorite(mediaId, !currentState)
            }
            btnWatchlist.setClickAnimation {
                val currentState =
                    (viewModel.accountStates.value as? AccountStatesUiState.Success)?.data?.watchlist
                        ?: false
                viewModel.toggleWatchlist(mediaId, !currentState)
            }
            btnRate.setClickAnimation { showRatingDialog() }
        }
    }

    // View ViewModel changes
    private fun setupObservers() {
        observeTvDetails()
        observeAccountStates()
        observeMediaActionStates()
        observeLoginStatus()
    }

    private fun observeTvDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Loading -> showLoading()
                        is NetworkRequest.Success -> {
                            result.data?.let { tvDetails ->
                                showSuccess()
                                bindUI(tvDetails)
                                setupMovieGallery(tvDetails)
                                setupSimilarAndRecommendations(tvDetails)
                            }
                        }
                        is NetworkRequest.Error -> {
                            showError()
                            if (result.message == Constants.Message.NO_INTERNET_CONNECTION) {
                                binding.internetLay.visibility = View.VISIBLE
                            }
                            showErrorSnackbar(binding.root, result.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun observeAccountStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            sessionManager.isGuest().collect { isGuest ->
                if (!isGuest) {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.accountStates.collect { state ->
                            when (state) {
                                is AccountStatesUiState.Loading -> setAccountActionsEnabled(false)
                                is AccountStatesUiState.Success -> {
                                    updateAccountActionsUI(state.data)
                                    setAccountActionsEnabled(true)
                                }
                                is AccountStatesUiState.Error -> {
                                    setAccountActionsEnabled(true)
                                    showErrorSnackbar(binding.root, state.message)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeMediaActionStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mediaActionState.collect { state ->
                    binding.apply {
                        btnFavorite.isEnabled = !state.isFavoriteLoading
                        btnWatchlist.isEnabled = !state.isWatchlistLoading
                        btnRate.isEnabled = !state.isRatingLoading
                    }
                }
            }
        }
    }

    private fun observeLoginStatus() {
        viewLifecycleOwner.observeLoginStatus(
            sessionManager = sessionManager,
            onGuestAction = { binding.cvMediaAction.visibility = View.GONE },
            onUserAction = { binding.cvMediaAction.visibility = View.VISIBLE }
        )
    }

    private fun updateAccountActionsUI(states: ResponseAccountStates) {
        binding.apply {
            btnFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.favorite) R.color.favorite else R.color.btn_icon
                )
            )
            btnWatchlist.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.watchlist) R.color.watchlist else R.color.btn_icon
                )
            )
            btnRate.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.rated != null) R.color.rate else R.color.btn_icon
                )
            )
        }
    }

    private fun setAccountActionsEnabled(enabled: Boolean) {
        binding.apply {
            btnFavorite.isEnabled = enabled
            btnWatchlist.isEnabled = enabled
            btnRate.isEnabled = enabled
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
        binding.internetLay.visibility = View.GONE
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

    // Helper function to generate the full image path
    private fun getImageFullPath(path: String?): String? {
        return if (path.isNullOrEmpty()) null else baseUrl + Constants.ImageSize.ORIGINAL + path
    }

    // Bind data to UI
    private fun bindUI(data: ResponseTvDetails) {
        bindUiDetail(data)
        bindUiCast(data.credits)
        bindUiLanguage(data.spokenLanguages, data.originalLanguage)
        bindUiReview(data.reviews!!)
    }

    private fun bindUiDetail(data: ResponseTvDetails) {
        binding.apply {
            txtTitle.text = data.name ?: Constants.Defaults.NOT_APPLICABLE
            setActionBarTitle(data.name)
            txtOverview.text = data.overview ?: Constants.Defaults.OVERVIEW
            handleOverviewExpansion()

            txtVoteCount.text = data.voteCount?.toString() ?: Constants.Defaults.VOTE_COUNT
            txtVoteAverage.text = data.voteAverage?.toFormattedVoteAverage()
                ?: Constants.Defaults.VOTE_AVERAGE.toString()
            ratingBar.rating = data.voteAverage?.div(2)?.toFloat() ?: 0f

            genresAdapter.differ.submitList(data.genres)

            imgPoster.loadImageWithoutShimmer(
                getImageFullPath(data.posterPath),
                R.drawable.image_slash_medium,
                R.drawable.image_medium,
                originalScaleType,
                true
            )

            imgBackdrop.loadImageWithoutShimmer(
                getImageFullPath(data.backdropPath),
                R.drawable.image_slash_large,
                R.drawable.image_large,
                originalScaleType,
                false
            )

            // Set the seasons
            if (data.seasons.isNullOrEmpty()) {
                cvTvSeasons.visibility = View.GONE
            } else {
                cvTvSeasons.visibility = View.VISIBLE
                seasonsAdapter.differ.submitList(data.seasons)
            }

            // Creators
            if (data.createdBy.isNullOrEmpty()) {
                txtCreatorValue.visibility = View.GONE
                txtCreator.visibility = View.GONE
            } else {
                txtCreatorValue.visibility = View.VISIBLE
                txtCreator.visibility = View.VISIBLE
                txtCreatorValue.text = data.createdBy.toCreatorNames()
            }

            // Release date and status
            if (data.firstAirDate.isNullOrEmpty()) {
                txtReleaseValue.visibility = View.GONE
                txtRelease.visibility = View.GONE
            } else {
                txtReleaseValue.visibility = View.VISIBLE
                txtRelease.visibility = View.VISIBLE
                txtReleaseValue.text = getString(
                    R.string.release_value,
                    data.firstAirDate.toFormattedDate(),
                    data.status
                )
            }

            // Networks
            txtNetworksValue.text = data.networks.toNetworkNames()

            txtLanguageValue.text = data.originalLanguage

            // Languages spoken and number of chapters/episodes
            txtSpokenLanguagesValue.text = data.spokenLanguages.toSpokenLanguagesText()
            txtSeasonsAndEpisodesValue.text = getString(
                R.string.seasons_and_episodes,
                data.numberOfSeasons?.toString() ?: Constants.Defaults.NOT_APPLICABLE,
                data.numberOfEpisodes?.toString() ?: Constants.Defaults.NOT_APPLICABLE
            )

            txtTypeValue.text = data.type

            // Tagline
            if (data.tagline.isNullOrEmpty()) {
                txtTag.visibility = View.GONE
            } else {
                txtTag.visibility = View.VISIBLE
                txtTag.text = data.tagline
            }

            txtProductionCountriesValue.text = data.productionCountries.toCountryNames()
            txtProductionCompaniesValue.text = data.productionCompanies?.toCompanyNames()
        }
    }

    private fun bindUiCast(data: ResponseCredit) {
        binding.layoutSeeAllCastAndCrew.setClickAnimation {
            findNavController().navigate(
                TvDetailsFragmentDirections.actionToCastAndCrewFragment(data)
            )
        }
        data.cast?.let { castList ->
            if (castList.isNotEmpty()) {
                binding.cvCastAndCrew.visibility = View.VISIBLE
            }
            castAndCrewAdapter.submitList(castList.filterNotNull())
        }
    }

    private fun bindUiReview(data: ResponseReviews) {
        binding.layoutSeeAllReviews.setClickAnimation {
            findNavController().navigate(
                TvDetailsFragmentDirections.actionTvDetailsFragmentToReviewsFragment(data)
            )
        }
        binding.apply {
            if (data.results.isNullOrEmpty()) {
                cvReview.visibility = View.GONE
            } else {
                cvReview.visibility = View.VISIBLE
                val review = data.results[0]
                val posterFullPath = getImageFullPath(review?.authorDetails?.avatarPath)
                ivProfileReviewAuthor.loadImageWithoutShimmer(
                    posterFullPath,
                    R.drawable.image_slash_small,
                    R.drawable.image_small,
                    originalScaleType,
                    false
                )
                tvReviewAuthor.text = review?.author
                tvReviewDate.text = getString(
                    R.string.written_on_date,
                    review?.createdAt?.toFormattedDate() ?: Constants.Defaults.NOT_APPLICABLE
                )
                tvReviewRating.text = getString(
                    R.string.review_rating,
                    review?.authorDetails?.rating?.toString() ?: Constants.Defaults.NOT_APPLICABLE
                )
                tvReviewContent.text = review?.content ?: ""
                handleOverviewReviewsExpansion()
                layoutSeeAllReviews.visibility = if (data.results.size > 1) View.VISIBLE else View.GONE
            }
        }
    }

    private fun bindUiLanguage(spokenLanguages: List<SpokenLanguage?>?, originalLanguage: String?) {
        val primaryLanguage = spokenLanguages?.filterNotNull()
            ?.find { it.iso6391 == originalLanguage }
            ?.englishName ?: Constants.Defaults.NOT_APPLICABLE
        binding.txtLanguageValue.text = primaryLanguage
    }

    // Control text expansion for overview and review
    private fun handleOverviewExpansion() {
        binding.txtOverview.maxLines = overviewMaxLines
        binding.txtOverview.ellipsize = TextUtils.TruncateAt.END
        binding.txtOverview.post {
            val isEllipsized = binding.txtOverview.layout?.let { layout ->
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            } ?: false
            binding.imgExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun handleOverviewReviewsExpansion() {
        binding.tvReviewContent.maxLines = overviewReviewsMaxLines
        binding.tvReviewContent.ellipsize = TextUtils.TruncateAt.END
        binding.tvReviewContent.post {
            val isEllipsized = binding.tvReviewContent.layout?.let { layout ->
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            } ?: false
            binding.imgReviewContentExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
        }
    }

    // Set similar and recommended sections
    private fun setupSimilarAndRecommendations(data: ResponseTvDetails) {
        val hasSimilar = !data.similar?.results.isNullOrEmpty()
        val hasRecommendations = !data.recommendations?.results.isNullOrEmpty()

        if (!hasSimilar && !hasRecommendations) {
            binding.cvSimilarRecommendations.visibility = View.GONE
            return
        }
        binding.cvSimilarRecommendations.visibility = View.VISIBLE

        if (hasSimilar xor hasRecommendations) {
            binding.tlSimilarRecommendation.visibility = View.GONE
            binding.txtHeaderSimilarRecommendation.visibility = View.VISIBLE
            binding.txtHeaderSimilarRecommendation.text = if (hasSimilar)
                getString(R.string.similar_tv_show) else getString(R.string.recommendations)
        } else {
            binding.tlSimilarRecommendation.visibility = View.VISIBLE
            binding.txtHeaderSimilarRecommendation.visibility = View.GONE
        }

        if (hasSimilar xor hasRecommendations) {
            binding.vpSimilarRecommendation.setCurrentItem(if (hasSimilar) 0 else 1, false)
        }
    }

    // Set up the video, backdrop, and poster sections
    private fun setupMovieGallery(data: ResponseTvDetails) {
        val hasVideos = !data.videos?.results.isNullOrEmpty()
        val hasBackdrops = !data.images?.backdrops.isNullOrEmpty()
        val hasPosters = !data.images?.posters.isNullOrEmpty()

        if (!hasVideos && !hasBackdrops && !hasPosters) {
            binding.cvVisualContent.visibility = View.GONE
            return
        }
        binding.cvVisualContent.visibility = View.VISIBLE

        val tabLayout = binding.tlVisualContent
        val viewPager = binding.vpVisualContent
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 1

        val visibleTabs = mutableListOf<Int>().apply {
            if (hasVideos) add(0)
            if (hasBackdrops) add(1)
            if (hasPosters) add(2)
        }

        if (visibleTabs.size == 1) {
            tabLayout.visibility = View.GONE
            binding.txtHeaderVisualContent.visibility = View.VISIBLE
            binding.txtHeaderVisualContent.text = when (visibleTabs[0]) {
                0 -> getString(R.string.videos)
                1 -> getString(R.string.backdrops)
                2 -> getString(R.string.posters)
                else -> ""
            }
            viewPager.setCurrentItem(0, false)
        } else {
            tabLayout.visibility = View.VISIBLE
            binding.txtHeaderVisualContent.visibility = View.GONE
        }

        tvGalleryPagerAdapter = TvGalleryPagerAdapter(this, visibleTabs)
        viewPager.adapter = tvGalleryPagerAdapter

        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.let { tab ->
                if (!visibleTabs.contains(i)) {
                    tabLayout.removeTab(tab)
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (visibleTabs[position]) {
                0 -> getString(R.string.videos)
                1 -> getString(R.string.backdrops)
                2 -> getString(R.string.posters)
                else -> ""
            }
        }.attach()
    }

    // Show the scoring dialog
    private fun showRatingDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rate, null)
        val ratingBar = dialogView.findViewById<com.willy.ratingbar.BaseRatingBar>(R.id.ratingBar)
        ratingBar.setMinimumStars(0.5f)
        val currentRating =
            (viewModel.accountStates.value as? AccountStatesUiState.Success)?.data?.rated?.value
        currentRating?.toFloat()?.let { ratingBar.rating = it }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Your Rating")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        viewModel.addRating(mediaId, ratingBar.rating)
                        dialog.dismiss()
                    } catch (e: Exception) {
                        showErrorSnackbar(binding.root, e.message ?: "Error submitting rating")
                    }
                }
            }
            .setNegativeButton("Clear") { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        viewModel.removeRating(mediaId)
                        dialog.dismiss()
                    } catch (e: Exception) {
                        showErrorSnackbar(binding.root, e.message ?: "Error removing rating")
                    }
                }
            }
            .create()
            .apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.successBtn))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.error))
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scroll_position", binding.nestedScrollView.scrollY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvGalleryPagerAdapter = null
        _binding = null
    }
}
