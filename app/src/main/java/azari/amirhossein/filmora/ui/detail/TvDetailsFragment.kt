package azari.amirhossein.filmora.ui.detail

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
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.CreditAdapter
import azari.amirhossein.filmora.adapter.GenresAdapter
import azari.amirhossein.filmora.adapter.SeasonsAdapter
import azari.amirhossein.filmora.adapter.SimilarTvRecommendationsPagerAdapter
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentTvDetailsBinding
import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.detail.DetailMediaItem
import azari.amirhossein.filmora.models.detail.ResponseCredit
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
    lateinit var creditAdapter: CreditAdapter

    private lateinit var pagerAdapter: SimilarTvRecommendationsPagerAdapter

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
        setupViewPager()

        // Extract fragment arguments
        args = MovieDetailFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        viewModel.getMediaDetails(mediaId, Constants.MediaType.TV)

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
    private fun setupViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        // Initialize pagerAdapter property
        pagerAdapter = SimilarTvRecommendationsPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.similar_tv_show)

                1 -> tab.text = getString(R.string.recommendations)

            }
        }.attach()


    }
    private fun setupSimilarAndRecommendations(mediaItem: DetailMediaItem) {
        val hasSimilar = !mediaItem.tvSimilar?.results.isNullOrEmpty()
        val hasRecommendations = !mediaItem.tvRecommendations?.results.isNullOrEmpty()

        if (!hasRecommendations && !hasSimilar) {
            binding.cvSimilarRecommendations.visibility = View.GONE
            return
        }

        binding.cvSimilarRecommendations.visibility = View.VISIBLE

        if (hasSimilar xor hasRecommendations) {
            binding.tabLayout.visibility = View.GONE
            binding.txtHeader.visibility = View.VISIBLE
            binding.txtHeader.text = if (hasSimilar) {
                getString(R.string.similar_tv_show)
            } else {
                getString(R.string.recommendations)
            }
        } else {
            binding.tabLayout.visibility = View.VISIBLE
            binding.txtHeader.visibility = View.GONE
        }

        if (hasSimilar) {
            (pagerAdapter.getFragment(0) as? SimilarTvFragment)?.updateMediaData(mediaItem.tvSimilar)
        }

        if (hasRecommendations) {
            (pagerAdapter.getFragment(1) as? RecommendationsTvFragment)?.updateMediaData(mediaItem.tvRecommendations)
        }

        if (hasSimilar xor hasRecommendations) {
            binding.viewPager.setCurrentItem(if (hasSimilar) 0 else 1, false)
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
            adapter = creditAdapter
            setHasFixedSize(true)
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
                                    bindUI(mediaItem)
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
        binding.txtOverview.maxLines = overviewMaxLines
        binding.txtOverview.ellipsize = TextUtils.TruncateAt.END

        binding.txtOverview.post {
            val isEllipsized = binding.txtOverview.layout?.let { layout ->
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            } ?: false

            binding.imgExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
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

    private fun bindUI(data: DetailMediaItem) {
        data.tv?.let {  bindUiDetail(it) }
        data.credits?.let { bindUiCast(it) }
        data.language?.let { bindUiLanguage(it) }
    }

    private fun bindUiCast(data : ResponseCredit){
        data.cast?.let { cast ->
            if (cast.isNotEmpty()){
                binding.cvCastAndCrew.visibility = View.VISIBLE
            }
            creditAdapter.submitList(
                cast.filterNotNull()
            )
        }
    }
    private fun bindUiLanguage(data: ResponseLanguage){
        val originalLanguage = binding.txtLanguageValue.text.toString()
        originalLanguage.getFullLanguageName(data)
            .also { binding.txtLanguageValue.text = it }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
