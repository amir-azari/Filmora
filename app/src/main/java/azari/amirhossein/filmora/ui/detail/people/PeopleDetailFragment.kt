package azari.amirhossein.filmora.ui.detail.people

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import azari.amirhossein.filmora.adapter.ProfileAdapter
import azari.amirhossein.filmora.databinding.FragmentPeopleDetailBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toFormattedDate
import azari.amirhossein.filmora.viewmodel.DetailsPeopleViewModel
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
    private val viewModel: DetailsPeopleViewModel by viewModels()

    @Inject
    lateinit var profileAdapter: ProfileAdapter

    // Base URL for loading images
    val baseUrl = Constants.Network.IMAGE_BASE_URL
    private lateinit var originalScaleType: ImageView.ScaleType

    // Arguments passed to the fragment
    private lateinit var args: PeopleDetailFragmentArgs
    private var id: Int = -1

    private val biographyMaxLines = 8
    private var isBiographyExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPeopleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = PeopleDetailFragmentArgs.fromBundle(requireArguments())
        id = args.id


        // Set an empty title initially
        setActionBarTitle("")

        // Fetch media details
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
    }

    private fun setActionBarTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.peopleDetails.collect { result ->
                    result.let {
                        when (it) {
                            is NetworkRequest.Loading -> {
                                showLoading()
                            }

                            is NetworkRequest.Success -> {
                                it.data?.let { item ->
                                    showSuccess()
                                    bindUI(item)
                                    if (item.images?.profiles.isNullOrEmpty()) {
                                        binding.cvProfile.visibility = View.GONE
                                    } else {
                                        profileAdapter.differ.submitList(
                                            item.images?.profiles
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

    private fun setupRecyclerViews() {
        binding.rvProfile.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = profileAdapter
        }

    }

    private fun bindUI(personDetails: ResponsePeopleDetails) {
        binding.apply {
            // Load poster
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
            txtName.text = personDetails.name ?: "Not available"
            setActionBarTitle(personDetails.name)
            txtBiography.text =
                if (!personDetails.biography.isNullOrEmpty()) personDetails.biography else "No biography available !!"
            handleOverviewExpansion()

            txtGenderValue.text = when (personDetails.gender) {
                1 -> "Female"
                2 -> "Male"
                3 -> "Non-binary"
                else -> "Not specified"
            }

            val age = calculateAge(personDetails.birthday)
            txtAgeValue.text = if (age != -1) age.toString() else "Age unknown"
            txtKnownForValue.text = personDetails.knownForDepartment ?: "Not specified"
            txtBirthdayValue.text =
                personDetails.birthday.let { it?.toFormattedDate() } ?: "No birthday available"

            txtDeathdayValue.text =
                personDetails.deathday?.let { it.toFormattedDate() } ?: run {
                    txtDeathdayValue.visibility = View.GONE
                    txtDeathday.visibility = View.GONE
                    ""
                }

            txtPlaceOfBirthValue.text =
                personDetails.placeOfBirth ?: "Place of birth not available"

            val movieDepartmentsMap = mutableMapOf<String, Int>().apply {
                val actingCount = personDetails.movieCredits?.cast?.size ?: 0
                if (actingCount > 0) {
                    put("Acting", actingCount)
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
            Log.d("TAG", movieDepartmentsMap.toString())

            val tvDepartmentsMap = mutableMapOf<String, Int>().apply {
                val actingCount = personDetails.tvCredits?.cast?.size ?: 0
                if (actingCount > 0) {
                    put("Acting", actingCount)
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

            if (movieDepartmentsMap.size > tvDepartmentsMap.size ){
                tvTvDepartment.minLines = movieDepartmentsMap.size

            }else{
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

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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