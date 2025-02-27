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
    lateinit var castAndCrewAdapter: CastAndCrewAdapter

    private lateinit var similarAndRecommendationsPagerAdapter: SimilarTvRecommendationsPagerAdapter
    private  var tvGalleryPagerAdapter: TvGalleryPagerAdapter? = null

    // State variables for overview expansion and configuration
    private var isOverviewExpanded = false
    private val overviewMaxLines = Constants.Defaults.OVERVIEW_MAX_LINES
    private val overviewReviewsMaxLines = 3

    private val viewModel: TvDetailViewModel by viewModels()

    // Arguments passed to the fragment
    private lateinit var args: TvDetailsFragmentArgs
    private var mediaId: Int = -1
    private var mediaType: String = ""

    // Base URL for loading images
    val baseUrl = Constants.Network.IMAGE_BASE_URL
    private lateinit var originalScaleType: ImageView.ScaleType

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
        setupViewPagersSimilarAndRecommendations()

        castAndCrewAdapter.setOnItemClickListener(clickCast)

        // Extract fragment arguments
        args = TvDetailsFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        viewModel.getTvAccountStates(mediaId)

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        viewModel.getTvDetails(mediaId)

        originalScaleType = binding.imgPoster.scaleType


        setupUI()
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

        setupAccountActions()
        observeAccountStates()
        observeMediaActionStates()

        savedInstanceState?.getInt("scroll_position")?.let { scrollPosition ->
            binding.nestedScrollView.post {
                binding.nestedScrollView.scrollTo(0, scrollPosition)
            }
        }

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scroll_position", binding.nestedScrollView.scrollY)
    }
    private fun setupUI() {
        setupRecyclerViews()
    }
    private fun setupViewPagersSimilarAndRecommendations() {
        val similarAndRecommendationsViewPager = binding.vpSimilarRecommendation
        val similarAndRecommendationsTabLayout = binding.tlSimilarRecommendation

        // Initialize pagerAdapter property
        similarAndRecommendationsPagerAdapter = SimilarTvRecommendationsPagerAdapter(this)
        similarAndRecommendationsViewPager.adapter = similarAndRecommendationsPagerAdapter
        similarAndRecommendationsViewPager.isUserInputEnabled = false

        TabLayoutMediator(similarAndRecommendationsTabLayout, similarAndRecommendationsViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.similar_tv_show)

                1 -> tab.text = getString(R.string.recommendations)

            }
        }.attach()

    }
    private fun setupVisual(mediaItem: ResponseTvDetails) {
        val hasVideos = !mediaItem.videos?.results.isNullOrEmpty()
        val hasBackdrops = !mediaItem.images?.backdrops.isNullOrEmpty()
        val hasPosters = !mediaItem.images?.posters.isNullOrEmpty()

        if (!hasVideos && !hasBackdrops && !hasPosters) {
            binding.cvVisualContent.visibility = View.GONE
            return
        }

        binding.cvVisualContent.visibility = View.VISIBLE

        val tabLayout = binding.tlVisualContent
        val viewPager = binding.vpVisualContent
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 1

        val visibleTabs = mutableListOf<Int>()

        if (hasVideos) {
            visibleTabs.add(0) // Videos tab
        }
        if (hasBackdrops) {
            visibleTabs.add(1) // Backdrops tab
        }
        if (hasPosters) {
            visibleTabs.add(2) // Posters tab
        }

        if (visibleTabs.size == 1) {
            tabLayout.visibility = View.GONE
            binding.txtHeaderVisualContent.visibility = View.VISIBLE

            when (visibleTabs[0]) {
                0 -> binding.txtHeaderVisualContent.text = getString(R.string.videos)
                1 -> binding.txtHeaderVisualContent.text = getString(R.string.backdrops)
                2 -> binding.txtHeaderVisualContent.text = getString(R.string.posters)
            }

            viewPager.setCurrentItem(0, false)
        } else {
            tabLayout.visibility = View.VISIBLE
            binding.txtHeaderVisualContent.visibility = View.GONE
        }

        tvGalleryPagerAdapter = TvGalleryPagerAdapter(this, visibleTabs)
        viewPager.adapter = tvGalleryPagerAdapter

        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            if (tab != null && !visibleTabs.contains(i)) {
                tabLayout.removeTab(tab)
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (visibleTabs[position]) {
                0 -> tab.text = getString(R.string.videos)
                1 -> tab.text = getString(R.string.backdrops)
                2 -> tab.text = getString(R.string.posters)
            }
        }.attach()

    }



    private fun setupSimilarAndRecommendations(mediaItem: ResponseTvDetails) {
        val hasSimilar = !mediaItem.similar?.results.isNullOrEmpty()
        val hasRecommendations = !mediaItem.recommendations?.results.isNullOrEmpty()

        if (!hasRecommendations && !hasSimilar) {
            binding.cvSimilarRecommendations.visibility = View.GONE
            return
        }

        binding.cvSimilarRecommendations.visibility = View.VISIBLE

        if (hasSimilar xor hasRecommendations) {
            binding.tlSimilarRecommendation.visibility = View.GONE
            binding.txtHeaderSimilarRecommendation.visibility = View.VISIBLE
            binding.txtHeaderSimilarRecommendation.text = if (hasSimilar) {
                getString(R.string.similar_tv_show)
            } else {
                getString(R.string.recommendations)
            }
        } else {
            binding.tlSimilarRecommendation.visibility = View.VISIBLE
            binding.txtHeaderSimilarRecommendation.visibility = View.GONE
        }

        if (hasSimilar xor hasRecommendations) {
            binding.vpSimilarRecommendation.setCurrentItem(if (hasSimilar) 0 else 1, false)
        }
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
            adapter = castAndCrewAdapter
            setHasFixedSize(true)
        }
    }

    private fun updateAccountActionsUI(states: ResponseAccountStates) {
        binding.apply {
            // Update favorite button
            btnFavorite.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.favorite == true) R.color.favorite else R.color.btn_icon
                )
            )

            // Update watchlist button
            btnWatchlist.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.watchlist == true) R.color.watchlist else R.color.btn_icon
                )
            )

            // Update rate button
            btnRate.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (states.rated != null) R.color.rate else R.color.btn_icon
                )
            )
        }
    }

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

            btnRate.setClickAnimation {
                showRatingDialog()
            }
        }
    }

    private fun setAccountActionsEnabled(enabled: Boolean) {
        binding.apply {
            btnFavorite.isEnabled = enabled
            btnWatchlist.isEnabled = enabled
            btnRate.isEnabled = enabled
        }
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvDetails.collect { result ->
                    result.let { it ->
                        when (it) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                it.data?.let { mediaItem ->
                                    showSuccess()
                                    bindUI(mediaItem)
                                    setupVisual(mediaItem)
                                    setupSimilarAndRecommendations(mediaItem)

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

    private fun observeAccountStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            sessionManager.isGuest().collect { isGuest ->
                if (!isGuest) {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.accountStates.collect { state ->
                            when (state) {
                                is AccountStatesUiState.Loading -> {
                                    setAccountActionsEnabled(false)
                                }

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    private fun bindUiReview(data: ResponseReviews) {
        binding.layoutSeeAllReviews.setClickAnimation {
            findNavController().navigate(
                TvDetailsFragmentDirections.actionTvDetailsFragmentToReviewsFragment(data)
            )
        }

        binding.apply {
            if (data.results?.isEmpty() == true) {
                cvReview.visibility = View.GONE
            } else {
                cvReview.visibility = View.VISIBLE

                val posterFullPath = if (data.results?.get(0)?.authorDetails?.avatarPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + data.results?.get(0)?.authorDetails?.avatarPath
                }

                ivProfileReviewAuthor.loadImageWithoutShimmer(
                    posterFullPath,
                    R.drawable.image_slash_small,
                    R.drawable.image_small,
                    originalScaleType,
                    false
                )
                tvReviewAuthor.text = data.results?.get(0)?.author
                val createdAt = data.results?.get(0)?.createdAt?.toFormattedDate()
                tvReviewDate.text = getString(R.string.written_on_date, createdAt ?: Constants.Defaults.NOT_APPLICABLE)

                val rating = data.results?.get(0)?.authorDetails?.rating?.toString()
                tvReviewRating.text = getString(R.string.review_rating, rating ?: Constants.Defaults.NOT_APPLICABLE)

                tvReviewContent.text = data.results?.get(0)?.content ?: ""
                handleOverviewReviewsExpansion()
                layoutSeeAllReviews.visibility = if (data.results?.size!! > 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    private fun bindUiDetail(data: ResponseTvDetails) {
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
                    baseUrl + Constants.ImageSize.ORIGINAL + posterPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true
                )


                // Load backdrop
                imgBackdrop.loadImageWithoutShimmer(
                    baseUrl + Constants.ImageSize.ORIGINAL + backdropPath,
                    R.drawable.image_slash_large,
                    R.drawable.image_large,
                    originalScaleType,
                    false
                )
                // Seasons
                if (seasons.isNullOrEmpty()){
                    cvTvSeasons.visibility = View.GONE
                }
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

                txtLanguageValue.text = originalLanguage


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

    private fun bindUI(data: ResponseTvDetails) {
        bindUiDetail(data)
        bindUiCast(data.credits)
        bindUiLanguage(data.spokenLanguages, data.originalLanguage)
        bindUiReview(data.reviews!!)

    }

    private fun bindUiCast(data : ResponseCredit){
        binding.layoutSeeAllCastAndCrew.setClickAnimation {
            findNavController().navigate(
                TvDetailsFragmentDirections.actionToCastAndCrewFragment(data)
            )
        }
        data.cast?.let { cast ->
            if (cast.isNotEmpty()){
                binding.cvCastAndCrew.visibility = View.VISIBLE
            }
            castAndCrewAdapter.submitList(
                cast.filterNotNull()
            )
        }
    }
    private fun showRatingDialog() {
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rate, null)

        // Initialize rating bar
        val ratingBar = dialogView.findViewById<com.willy.ratingbar.BaseRatingBar>(R.id.ratingBar)
        ratingBar.setMinimumStars(0.5f)

        // Set existing rating if available
        val currentRating =
            (viewModel.accountStates.value as? AccountStatesUiState.Success)?.data?.rated?.value
        currentRating?.toFloat()?.let {
            ratingBar.rating = it
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Your Rating")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val ratingValue = ratingBar.rating
                        viewModel.addRating(mediaId, ratingValue)
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
    private val clickCast = { cast : ResponseCredit.Cast ->
        val action = TvDetailsFragmentDirections.actionToPeopleDetailFragment(cast.id)
        findNavController().navigate(action)
    }

    private fun bindUiLanguage(spokenLanguages: List<SpokenLanguage?>?, originalLanguage: String?) {
        val primaryLanguage = spokenLanguages?.filterNotNull()
            ?.find { it.iso6391 == originalLanguage }
            ?.englishName ?: Constants.Defaults.NOT_APPLICABLE

        binding.txtLanguageValue.text = primaryLanguage
    }
    override fun onDestroyView() {
        super.onDestroyView()
        tvGalleryPagerAdapter = null
        _binding = null
    }
}
