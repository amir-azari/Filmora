package azari.amirhossein.filmora.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.flatMap
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.MayLikeMovieAdapter
import azari.amirhossein.filmora.adapter.MayLikeTvAdapter
import azari.amirhossein.filmora.adapter.MoviesAdapter
import azari.amirhossein.filmora.adapter.PeopleAdapter
import azari.amirhossein.filmora.adapter.PopularCelebrityAdapter
import azari.amirhossein.filmora.adapter.SearchMoviePreferencesAdapter
import azari.amirhossein.filmora.adapter.SearchTvPreferencesAdapter
import azari.amirhossein.filmora.adapter.SimilarMovieAdapter
import azari.amirhossein.filmora.adapter.SimilarTvAdapter
import azari.amirhossein.filmora.adapter.TvsAdapter
import azari.amirhossein.filmora.databinding.FragmentSearchAllBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.models.search.SearchResultPageData
import azari.amirhossein.filmora.models.tv.ResponseTvType
import azari.amirhossein.filmora.ui.home.HomeFragmentDirections
import azari.amirhossein.filmora.ui.movies.MovieSectionFragmentDirections
import azari.amirhossein.filmora.ui.people.PeopleSectionFragmentDirections
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchAllFragment : Fragment() {
    private var _binding: FragmentSearchAllBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var moviesAdapter: MayLikeMovieAdapter

    @Inject
    lateinit var tvShowsAdapter: MayLikeTvAdapter

    @Inject
    lateinit var celebritiesAdapter: PopularCelebrityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        moviesAdapter.setOnItemClickListener(clickMovie)
        celebritiesAdapter.setOnItemClickListener(clickPeople)
        tvShowsAdapter.setOnItemClickListener(clickTv)
        binding.layoutSeeAllMovies.setOnClickListener { navigateToTab(1) }
        binding.layoutSeeAllTvShows.setOnClickListener { navigateToTab(2) }
        binding.layoutSeeAllPeoples.setOnClickListener { navigateToTab(3) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchAllResults.collectLatest { result ->
                result?.let {
                    when (result) {
                        is NetworkRequest.Loading -> {
                        }
                        is NetworkRequest.Success -> {
                            showSuccess()
                            result.data?.let { updateUI(it) }
                        }
                        is NetworkRequest.Error -> {
                        }
                    }
                }

            }
        }
    }

    private fun updateUI(data: SearchResultPageData) {
        moviesAdapter.differ.submitList(data.movies?.results?.take(10))
        tvShowsAdapter.differ.submitList(data.tvShows?.results?.take(10))
        celebritiesAdapter.differ.submitList(data.people?.results?.take(10))
    }
    private fun navigateToTab(index: Int) {
        val viewPager = requireParentFragment().view?.findViewById<ViewPager2>(R.id.viewPager)
        viewPager?.currentItem = index
    }
    private val clickMovie = { movie: ResponseMoviesList.Result ->
        val action = SearchFragmentDirections.actionToMovieDetail(Constants.MediaType.MOVIE,movie.id)
        findNavController().navigate(action)

    }
    private val clickPeople = { id : Int ->
        val action = SearchFragmentDirections.actionToPeopleDetailFragment(id)
        findNavController().navigate(action)
    }
    private val clickTv = { tv: ResponseTvsList.Result ->
        val action = SearchFragmentDirections.actionToTvDetail(Constants.MediaType.TV,tv.id)
        findNavController().navigate(action)

    }

    private fun setupRecyclerViews() {
        setupRecyclerView(binding.rvMovies, moviesAdapter)
        setupRecyclerView(binding.rvTvShows, tvShowsAdapter)
        setupRecyclerView(binding.rvPeoples, celebritiesAdapter)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            isNestedScrollingEnabled = false

            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val viewPager =
                        requireParentFragment().view?.findViewById<ViewPager2>(R.id.viewPager)
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewPager?.isUserInputEnabled = false
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            viewPager?.isUserInputEnabled = true
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }
    private fun showSuccess() {
        binding.mainContentContainer.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
