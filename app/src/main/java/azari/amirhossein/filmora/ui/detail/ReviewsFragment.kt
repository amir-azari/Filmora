package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.CastAndCrewAdapter
import azari.amirhossein.filmora.adapter.ReviewAdapter
import azari.amirhossein.filmora.databinding.FragmentPosterBinding
import azari.amirhossein.filmora.databinding.FragmentReviewsBinding
import azari.amirhossein.filmora.ui.detail.people.CreditsFragmentArgs
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReviewsFragment : Fragment() {
    //Binding
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private val args: ReviewsFragmentArgs by navArgs()

    @Inject
    lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReviewsBinding.inflate(inflater,container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        reviewAdapter.differ.submitList(args.review.results)

    }
    private fun setupRecyclerView() {
        val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL , false)
        binding.rvReviews.adapter = reviewAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}