package azari.amirhossein.filmora.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import azari.amirhossein.filmora.adapter.SectionedCastAndCrewAdapter
import azari.amirhossein.filmora.databinding.FragmentCastAndCrewBinding
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CastAndCrewFragment : Fragment() {
    //Binding
    private var _binding : FragmentCastAndCrewBinding? = null
    private val binding get() = _binding!!

    private val args: CastAndCrewFragmentArgs by navArgs()

    //Inject
    @Inject
    lateinit var castAndCrewAdapter: SectionedCastAndCrewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCastAndCrewBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        processCredits()


    }
    private fun setupRecyclerView() {
        val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()
        binding.rvCredits.layoutManager = flexboxLayoutManager
        binding.rvCredits.adapter = castAndCrewAdapter
    }



    private fun setupClickListeners() {
        castAndCrewAdapter.setOnItemClickListener { item ->
            when (item) {
                is ResponseCredit.Cast -> {
                    navigateToPeopleDetail(item.id)
                }
                is ResponseCredit.Crew -> {
                    navigateToPeopleDetail(item.id)
                }
            }
        }
    }
    private fun processCredits() {
        binding.apply {
            if (args.castAndCrew.cast.isNullOrEmpty() && args.castAndCrew.crew.isNullOrEmpty()) {
                progressBar.visibility = View.GONE
                rvCredits.visibility = View.GONE
                return
            }

            (rvCredits.layoutManager as? GridLayoutManager)?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (castAndCrewAdapter.getItemViewType(position)) {
                        SectionedCastAndCrewAdapter.VIEW_TYPE_HEADER,
                        SectionedCastAndCrewAdapter.VIEW_TYPE_JOB_HEADER -> 3
                        else -> 1
                    }
                }
            }

            progressBar.visibility = View.GONE
            castAndCrewAdapter.submitList(args.castAndCrew)
        }
    }


    private fun navigateToPeopleDetail(id: Int) {
        val action = CastAndCrewFragmentDirections.actionToPeopleDetailFragment(id)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}