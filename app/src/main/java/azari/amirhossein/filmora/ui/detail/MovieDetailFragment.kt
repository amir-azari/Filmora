package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.text.TextUtils
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
import azari.amirhossein.filmora.adapter.CreditAdapter
import azari.amirhossein.filmora.adapter.GenresAdapter
import azari.amirhossein.filmora.adapter.SeasonsAdapter
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentMovieDetailBinding
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.getFullLanguageName
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.observeLoginStatus
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toCompanyNames
import azari.amirhossein.filmora.utils.toCountryNames
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.utils.toFormattedRuntime
import azari.amirhossein.filmora.utils.toFormattedVoteAverage
import azari.amirhossein.filmora.utils.toFormattedWithUnits
import azari.amirhossein.filmora.utils.toSpokenLanguagesText
import azari.amirhossein.filmora.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
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
        // Extract fragment arguments
        args = MovieDetailFragmentArgs.fromBundle(requireArguments())
        mediaId = args.id
        mediaType = args.mediaType

        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
        viewModel.getMediaDetails(mediaId, Constants.MediaType.MOVIE)
        binding.cvTvSeasons.visibility = View.GONE

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

    private fun setupRecyclerViews() {
        // Set up the RecyclerView for displaying genres
        binding.rvGenre.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = genresAdapter
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
                                    mediaItem.movie?.let { data -> bindUI(data) }

                                    mediaItem.credits?.cast?.let { cast ->
                                        creditAdapter.submitList(
                                            cast.filterNotNull()
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
        binding.txtOverview.maxLines = overviewMaxLines
        binding.txtOverview.ellipsize = TextUtils.TruncateAt.END

        binding.txtOverview.post {
            val isEllipsized = binding.txtOverview.layout?.let { layout ->
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            } ?: false

            binding.imgExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun bindUI(data: ResponseMovieDetails) {
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
                originalLanguage.getFullLanguageName(viewModel.languages.value?.peekContent())
                    .also { txtLanguageValue.text = it }
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


            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
