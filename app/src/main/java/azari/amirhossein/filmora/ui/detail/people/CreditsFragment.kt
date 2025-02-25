package azari.amirhossein.filmora.ui.detail.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.adapter.SectionedCreditsAdapter
import azari.amirhossein.filmora.databinding.FragmentCreditsBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleDetails
import azari.amirhossein.filmora.models.detail.people.CreditItem
import azari.amirhossein.filmora.models.detail.people.DepartmentSection
import azari.amirhossein.filmora.models.detail.people.YearSection
import azari.amirhossein.filmora.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreditsFragment : Fragment() {

    private var _binding: FragmentCreditsBinding? = null
    private val binding get() = _binding!!

    private val args: CreditsFragmentArgs by navArgs()

    @Inject
    lateinit var creditsAdapter: SectionedCreditsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        processCredits()
    }

    private fun setupRecyclerView() {
        binding.rvCredits.apply {
            adapter = creditsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        creditsAdapter.setOnItemClickListener { creditItem ->
            if (args.isMovie) {
                navigateToMovieDetail(creditItem.id)
            } else {
                navigateToTvDetail(creditItem.id)
            }
        }
    }

    private fun processCredits() {
        val sections = if (args.isMovie) {
            processMovieCredits(args.movieCredits!!)
        } else {
            processTVCredits(args.tvCredits!!)
        }
        creditsAdapter.submitList(sections)
    }

    private fun processMovieCredits(credits: ResponsePeopleDetails.MovieCredits): List<DepartmentSection> {
        val departmentMap = mutableMapOf<String, MutableMap<String, MutableList<CreditItem>>>()

        // Process Cast
        credits.cast?.forEach { cast ->
            cast?.let {
                val year = it.releaseDate?.split("-")?.firstOrNull() ?: ""
                val creditItem = CreditItem(
                    id = it.id,
                    department = "Acting",
                    showDepartment = false,
                    year = year,
                    title = it.title ?: "",
                    role = it.character ?: ""
                )
                departmentMap
                    .getOrPut("Acting") { mutableMapOf() }
                    .getOrPut(year) { mutableListOf() }
                    .add(creditItem)
            }
        }

        // Process Crew
        credits.crew?.forEach { crew ->
            crew?.let {
                val department = it.department ?: return@let
                val year = it.releaseDate?.split("-")?.firstOrNull() ?: ""
                val creditItem = CreditItem(
                    id = it.id,
                    department = department,
                    showDepartment = false,
                    year = year,
                    title = it.title ?: "",
                    role = it.job ?: ""
                )
                departmentMap
                    .getOrPut(department) { mutableMapOf() }
                    .getOrPut(year) { mutableListOf() }
                    .add(creditItem)
            }
        }

        return departmentMap.map { (department, yearMap) ->
            DepartmentSection(
                name = department,
                yearSections = yearMap.map { (year, items) ->
                    YearSection(
                        year = year,
                        items = items.sortedBy { it.title }
                    )
                }.sortedByDescending { it.year }
            )
        }.sortedBy { it.name }
    }

    private fun processTVCredits(credits: ResponsePeopleDetails.TvCredits): List<DepartmentSection> {
        val departmentMap = mutableMapOf<String, MutableMap<String, MutableList<CreditItem>>>()

        // Process Cast
        credits.cast?.forEach { cast ->
            cast?.let {
                val year = it.firstAirDate?.split("-")?.firstOrNull() ?: ""
                val creditItem = CreditItem(
                    id = it.id,
                    department = "Acting",
                    showDepartment = false,
                    year = year,
                    title = it.name ?: "",
                    role = it.character ?: ""
                )
                departmentMap
                    .getOrPut("Acting") { mutableMapOf() }
                    .getOrPut(year) { mutableListOf() }
                    .add(creditItem)
            }
        }

        // Process Crew
        credits.crew?.forEach { crew ->
            crew?.let {
                val department = it.department ?: return@let
                val year = it.firstAirDate?.split("-")?.firstOrNull() ?: ""
                val creditItem = CreditItem(
                    id = it.id,
                    department = department,
                    showDepartment = false,
                    year = year,
                    title = it.name ?: "",
                    role = it.job ?: ""
                )
                departmentMap
                    .getOrPut(department) { mutableMapOf() }
                    .getOrPut(year) { mutableListOf() }
                    .add(creditItem)
            }
        }

        return departmentMap.map { (department, yearMap) ->
            DepartmentSection(
                name = department,
                yearSections = yearMap.map { (year, items) ->
                    YearSection(
                        year = year,
                        items = items.sortedBy { it.title }
                    )
                }.sortedByDescending { it.year }
            )
        }.sortedBy { it.name }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        val action = CreditsFragmentDirections.actionToMovieDetail(
            mediaType = Constants.MediaType.MOVIE,
            id = movieId
        )
        findNavController().navigate(action)
    }

    private fun navigateToTvDetail(tvId: Int) {
        val action = CreditsFragmentDirections.actionToTvDetail(
            mediaType = Constants.MediaType.TV,
            id = tvId
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}