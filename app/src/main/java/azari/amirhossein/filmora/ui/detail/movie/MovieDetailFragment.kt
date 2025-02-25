package azari.amirhossein.filmora.ui.detail.movie

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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
import azari.amirhossein.filmora.adapter.MovieGalleryPagerAdapter
import azari.amirhossein.filmora.adapter.SimilarMovieRecommendationsPagerAdapter
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentMovieDetailBinding
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.detail.SpokenLanguage
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.observeLoginStatus
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toCompanyNames
import azari.amirhossein.filmora.utils.toCountryNames
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.utils.toFormattedRuntime
import azari.amirhossein.filmora.utils.toFormattedVoteAverage
import azari.amirhossein.filmora.utils.toFormattedWithUnits
import azari.amirhossein.filmora.utils.toSpokenLanguagesText
import azari.amirhossein.filmora.viewmodel.MovieDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {
    //Binding
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var genresAdapter: GenresAdapter

    @Inject
    lateinit var castAndCrewAdapter: CastAndCrewAdapter

    private lateinit var similarAndRecommendationsPagerAdapter : SimilarMovieRecommendationsPagerAdapter
    private var movieGalleryPagerAdapter: MovieGalleryPagerAdapter? = null

    // State variables for overview expansion and configuration
    private var isOverviewExpanded = false
    private val overviewMaxLines = Constants.Defaults.OVERVIEW_MAX_LINES
    private val overviewReviewsMaxLines = 3

    private val viewModel: MovieDetailViewModel by viewModels()

    // Arguments passed to the fragment
    private lateinit var args: MovieDetailFragmentArgs
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
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagersSimilarAndRecommendations()

        castAndCrewAdapter.setOnItemClickListener(clickCast)

        // Extract fragment arguments
        args = MovieDetailFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        viewModel.getMovieDetails(mediaId)
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
        similarAndRecommendationsViewPager.adapter = SimilarMovieRecommendationsPagerAdapter(this)
        similarAndRecommendationsViewPager.isUserInputEnabled = false
        similarAndRecommendationsViewPager.offscreenPageLimit = 1
        TabLayoutMediator(
            similarAndRecommendationsTabLayout,
            similarAndRecommendationsViewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.similar_movies)

                1 -> tab.text = getString(R.string.recommendations)

            }
        }.attach()

    }

    private fun setupSimilarAndRecommendations(mediaItem: ResponseMovieDetails) {
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

    private fun setupVisual(mediaItem: ResponseMovieDetails) {
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
        if (hasVideos) visibleTabs.add(0)
        if (hasBackdrops) visibleTabs.add(1)
        if (hasPosters) visibleTabs.add(2)

        if (visibleTabs.size == 1) {
            tabLayout.visibility = View.GONE
            binding.txtHeaderVisualContent.visibility = View.VISIBLE

            when (visibleTabs[0]) {
                0 -> binding.txtHeaderVisualContent.text = getString(R.string.videos)
                1 -> binding.txtHeaderVisualContent.text = getString(R.string.backdrops)
                2 -> binding.txtHeaderVisualContent.text = getString(R.string.posters)
            }

            viewPager.setCurrentItem(0, true)
        } else {
            tabLayout.visibility = View.VISIBLE
            binding.txtHeaderVisualContent.visibility = View.GONE
        }

        movieGalleryPagerAdapter = MovieGalleryPagerAdapter(this, visibleTabs)
        viewPager.adapter = movieGalleryPagerAdapter

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

    private fun setupRecyclerViews() {
        // Set up the RecyclerView for displaying genres
        binding.rvGenre.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = genresAdapter
            isNestedScrollingEnabled = false

        }

        binding.rvCastAndCrew.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAndCrewAdapter
        }

    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetails.collect { result ->
                    result.let { it ->
                        when (it) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                result.data?.let { movieDetail ->
                                    showSuccess()
                                    bindUI(movieDetail)
                                    setupSimilarAndRecommendations(movieDetail)
                                    setupVisual(movieDetail)
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

    private fun bindUI(data: ResponseMovieDetails) {
        bindUiDetail(data)
        bindUiCast(data.credits)
        bindUiLanguage(data.spokenLanguages, data.originalLanguage)
        bindUiReview(data.reviews!!)
    }

    private fun bindUiReview(data: ResponseReviews) {

        binding.layoutSeeAllReviews.setClickAnimation {
            findNavController().navigate(
                MovieDetailFragmentDirections.actionMovieDetailFragmentToReviewsFragment(data)
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
                tvReviewDate.text = getString(R.string.written_on_date, createdAt ?: "N/A")

                val rating = data.results?.get(0)?.authorDetails?.rating?.toString()
                tvReviewRating.text = getString(R.string.review_rating, rating ?: "N/A")

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



    private fun bindUiDetail(data: ResponseMovieDetails) {
        binding.apply {
            data.apply {
                // Movie name and overview
                txtTitle.text = title ?: Constants.Defaults.NOT_APPLICABLE
                setActionBarTitle(title)
                txtOverview.text = overview ?: Constants.Defaults.OVERVIEW
                handleOverviewExpansion()

                // Vote average
                txtVoteCount.text = voteCount?.toString() ?: Constants.Defaults.VOTE_COUNT
                txtVoteAverage.text = voteAverage?.toFormattedVoteAverage()
                    ?: Constants.Defaults.VOTE_AVERAGE.toString()
                ratingBar.rating = voteAverage?.div(2)?.toFloat() ?: 0f

                genresAdapter.differ.submitList(genres)

                // Load poster
                val posterFullPath = if (posterPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + posterPath
                }
                imgPoster.loadImageWithoutShimmer(
                    posterFullPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true
                )


                // Load backdrop
                val backdropFullPath = if (backdropPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + backdropPath
                }
                imgBackdrop.loadImageWithoutShimmer(
                    backdropFullPath,
                    R.drawable.image_slash_large,
                    R.drawable.image_large,
                    originalScaleType,
                    true
                )


                txtStatusValue.text = releaseDate.let { it?.toFormattedDate() }
                txtLanguageValue.text = originalLanguage
                txtBudgetValue.text = if (budget == 0) "-" else budget.toFormattedWithUnits()
                txtRevenueValue.text = if (revenue == 0) "-" else revenue.toFormattedWithUnits()

                // Tagline
                if (tagline.isNullOrEmpty()) {
                    txtTag.visibility = View.GONE
                } else {
                    txtTag.visibility = View.VISIBLE
                    txtTag.text = tagline
                }
                txtDurationValue.text = runtime.toFormattedRuntime()
                txtSpokenLanguagesValue.text = spokenLanguages.toSpokenLanguagesText()

                // Production companies and countries
                txtProductionCountriesValue.text = productionCountries.toCountryNames()
                txtProductionCompaniesValue.text = productionCompanies?.toCompanyNames()

                // Collection
                if (belongsToCollection?.id != null) {
                    binding.cvCollection.visibility = View.VISIBLE
                }

                val collectionPosterFullPath =
                    if (belongsToCollection?.posterPath.isNullOrEmpty()) {
                        null
                    } else {
                        baseUrl + Constants.ImageSize.ORIGINAL + belongsToCollection?.posterPath
                    }

                imgCollection.loadImageWithoutShimmer(
                    collectionPosterFullPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true
                )
                tvCollectionName.text =
                    getString(R.string.part_of_collection, belongsToCollection?.name)

                binding.apply {
                    btnSeeCollection.setClickAnimation {
                        val action = data.belongsToCollection?.id?.let {
                            MovieDetailFragmentDirections.actionMovieDetailFragmentToCollectionFragment(
                                it
                            )
                        }
                        action?.let { findNavController().navigate(it) }
                    }
                }

            }
        }

    }


    private fun bindUiCast(credits: ResponseCredit) {
        binding.layoutSeeAllCastAndCrew.setClickAnimation {
            credits.let {
                findNavController().navigate(
                    MovieDetailFragmentDirections.actionToCastAndCrewFragment(it)
                )
            }
        }

        credits.cast?.let { cast ->
            if (cast.isNotEmpty()) {
                binding.cvCastAndCrew.visibility = View.VISIBLE
            }
            castAndCrewAdapter.submitList(
                cast.filterNotNull()
            )
        }
    }

    private val clickCast = { cast : ResponseCredit.Cast ->
        val action = MovieDetailFragmentDirections.actionToPeopleDetailFragment(cast.id)
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
        _binding = null
        movieGalleryPagerAdapter = null

    }
}
