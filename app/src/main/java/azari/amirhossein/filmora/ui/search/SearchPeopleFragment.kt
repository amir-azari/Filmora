package azari.amirhossein.filmora.ui.search

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.flatMap
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.DataLoadStateAdapter
import azari.amirhossein.filmora.adapter.PeopleAdapter
import azari.amirhossein.filmora.databinding.FragmentSearchPeopleBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.ui.people.PeopleSectionFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SearchPeopleFragment : Fragment() {
    private var _binding: FragmentSearchPeopleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var peopleAdapter: PeopleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peopleAdapter.setOnItemClickListener(click)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvPeople.layoutManager = gridLayoutManager

        val concatAdapter = peopleAdapter.withLoadStateFooter(
            footer = DataLoadStateAdapter { peopleAdapter.retry() }
        )
        binding.rvPeople.adapter = concatAdapter
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    DataLoadStateAdapter.VIEW_TYPE_LOAD_STATE -> 3
                    else -> 1
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchPeopleResults
                    .map { pagingData ->
                        pagingData.map<ResponsePopularCelebrity.Result, ResponsePeopleType> { movieResult ->
                            ResponsePeopleType.Popular(movieResult)
                        }
                    }
                    .collectLatest { mappedMovies ->
                        peopleAdapter.submitData(mappedMovies)
                    }
            }
        }
    }
    private val click = { id : Int ->
        val action = PeopleSectionFragmentDirections.actionToPeopleDetailFragment(id)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

