package azari.amirhossein.filmora.ui.detail.people

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
import azari.amirhossein.filmora.adapter.ProfileAdapter
import azari.amirhossein.filmora.databinding.FragmentPeopleDetailBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.viewmodel.PeopleDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PeopleDetailFragment : Fragment() {

    // Binding
    private var _binding: FragmentPeopleDetailBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: PeopleDetailsViewModel by viewModels()

    @Inject
    lateinit var profileAdapter: ProfileAdapter

    // Base URL for loading images
    private val baseUrl = Constants.Network.IMAGE_BASE_URL
    private lateinit var originalScaleType: ImageView.ScaleType

    // Arguments passed to the fragment
    private lateinit var args: PeopleDetailFragmentArgs
    private var id: Int = -1

    private val biographyMaxLines = Constants.Defaults.BIOGRAPHY_MAX_LINES
    private var isBiographyExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPeopleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = PeopleDetailFragmentArgs.fromBundle(requireArguments())
        id = args.id

        setActionBarTitle("")

        viewModel.getPeopleDetails(id)
        originalScaleType = binding.imgPoster.scaleType

        binding.biographyContainer.setupOverviewExpansion(
            txtOverview = binding.txtBiography,
            imgExpand = binding.imgExpand,
            overviewMaxLines = biographyMaxLines,
            isOverviewExpanded = false
        ) { isExpanded ->
            isBiographyExpanded = isExpanded
        }
        observeViewModel()
        setupRecyclerViews()

        binding.btnMovies.setOnClickListener {
            viewModel.peopleDetails.value.data?.movieCredits?.let { credits ->
                findNavController().navigate(
                    PeopleDetailFragmentDirections.actionPeopleDetailFragmentToCreditsFragment(
                        movieCredits = credits,
                        tvCredits = null,
                        isMovie = true
                    )
                )
            }
        }

        binding.btnTvShows.setOnClickListener {
            viewModel.peopleDetails.value.data?.tvCredits?.let { credits ->
                findNavController().navigate(
                    PeopleDetailFragmentDirections.actionPeopleDetailFragmentToCreditsFragment(
                        movieCredits = null,
                        tvCredits = credits,
                        isMovie = false
                    )
                )
            }
        }


    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.peopleDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Loading -> {
                            showLoading()
                        }
                        is NetworkRequest.Success -> {
                            result.data?.let { item ->
                                showSuccess()
                                bindUI(item)
                                if (item.images?.profiles.isNullOrEmpty()) {
                                    binding.cvProfile.visibility = View.GONE
                                } else {
                                    profileAdapter.differ.submitList(item.images?.profiles)
                                }
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

    private fun setupRecyclerViews() {
        binding.rvProfile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = profileAdapter
        }
    }

    private fun bindUI(personDetails: ResponsePeopleDetails) {
        binding.apply {
            val posterFullPath = if (personDetails.profilePath.isNullOrEmpty()) {
                null
            } else {
                baseUrl + Constants.ImageSize.ORIGINAL + personDetails.profilePath
            }
            imgPoster.loadImageWithoutShimmer(
                posterFullPath,
                R.drawable.image_slash_medium,
                R.drawable.image_medium,
                originalScaleType,
                true
            )

            txtName.text = personDetails.name ?: Constants.Defaults.NOT_AVAILABLE
            setActionBarTitle(personDetails.name)
            txtBiography.text =
                if (!personDetails.biography.isNullOrEmpty()) personDetails.biography
                else Constants.Defaults.NO_BIOGRAPHY
            handleOverviewExpansion()

            txtGenderValue.text = when (personDetails.gender) {
                1 -> Constants.Gender.FEMALE
                2 -> Constants.Gender.MALE
                3 -> Constants.Gender.NON_BINARY
                else -> Constants.Gender.NOT_SPECIFIED
            }

            val age = calculateAge(personDetails.birthday)
            txtAgeValue.text = if (age != -1) age.toString() else Constants.Defaults.AGE_UNKNOWN

            txtKnownForValue.text =
                personDetails.knownForDepartment ?: Constants.Gender.NOT_SPECIFIED
            txtBirthdayValue.text =
                personDetails.birthday?.toFormattedDate() ?: Constants.Defaults.NO_BIRTHDAY_AVAILABLE

            txtDeathdayValue.text = personDetails.deathday?.toFormattedDate() ?: run {
                txtDeathdayValue.visibility = View.GONE
                txtDeathday.visibility = View.GONE
                ""
            }

            txtPlaceOfBirthValue.text =
                personDetails.placeOfBirth ?: Constants.Defaults.PLACE_OF_BIRTH_NOT_AVAILABLE

            val movieDepartmentsMap = mutableMapOf<String, Int>().apply {
                val actingCount = personDetails.movieCredits?.cast?.size ?: 0
                if (actingCount > 0) {
                    put(Constants.Departments.ACTING, actingCount)
                }
                personDetails.movieCredits?.crew?.forEach { crew ->
                    crew?.department?.let { department ->
                        put(department, getOrDefault(department, 0) + 1)
                    }
                }
            }
            if (movieDepartmentsMap.isEmpty()) {
                llMovies.visibility = View.GONE
                knownForSection.weightSum = 1f
            }

            val tvDepartmentsMap = mutableMapOf<String, Int>().apply {
                val actingCount = personDetails.tvCredits?.cast?.size ?: 0
                if (actingCount > 0) {
                    put(Constants.Departments.ACTING, actingCount)
                }
                personDetails.tvCredits?.crew?.forEach { crew ->
                    crew?.department?.let { department ->
                        put(department, getOrDefault(department, 0) + 1)
                    }
                }
            }
            if (tvDepartmentsMap.isEmpty()) {
                llTVShows.visibility = View.GONE
                knownForSection.weightSum = 1f
            }

            if (movieDepartmentsMap.size > tvDepartmentsMap.size) {
                tvTvDepartment.minLines = movieDepartmentsMap.size
            } else {
                tvMovieDepartment.minLines = tvDepartmentsMap.size
            }

            tvMovieDepartment.text = buildDepartmentsText(movieDepartmentsMap)
            tvTvDepartment.text = buildDepartmentsText(tvDepartmentsMap)
        }
    }

    private fun buildDepartmentsText(map: Map<String, Int>): String {
        return map.entries.joinToString("\n") { "${it.key}: ${it.value}" }
    }

    private fun calculateAge(birthday: String?): Int {
        if (birthday.isNullOrEmpty()) return -1

        val dateFormat = SimpleDateFormat(Constants.Formats.DATE_YYYY_MM_DD, Locale.getDefault())
        val birthDate = dateFormat.parse(birthday) ?: return -1

        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance().apply { time = birthDate }

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun handleOverviewExpansion() {
        binding.txtBiography.maxLines = biographyMaxLines
        binding.txtBiography.ellipsize = TextUtils.TruncateAt.END

        binding.txtBiography.post {
            val isEllipsized = binding.txtBiography.layout?.let { layout ->
                layout.getEllipsisCount(layout.lineCount - 1) > 0
            } ?: false

            binding.imgExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
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

    private fun showErrorSnackbar(root: View, message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).apply {
            customize(R.color.error, R.color.white, Gravity.TOP)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}