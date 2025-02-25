package azari.amirhossein.filmora.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.adapter.BackdropAdapter
import azari.amirhossein.filmora.adapter.PosterAdapter
import azari.amirhossein.filmora.adapter.VideoAdapter
import azari.amirhossein.filmora.databinding.FragmentMediaGalleryBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.VerticalSpaceItemDecoration
import azari.amirhossein.filmora.utils.createFlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaGalleryFragment : Fragment() {
    //Binding
    private var _binding : FragmentMediaGalleryBinding? = null
    private val binding get() = _binding!!

    private val args: MediaGalleryFragmentArgs by navArgs()

    private val backdropAdapter by lazy { BackdropAdapter(true) }
    private val posterAdapter by lazy { PosterAdapter(true) }
    private val videoAdapter by lazy { VideoAdapter(true) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMediaGalleryBinding.inflate(inflater , container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        backdropAdapter.differ.submitList(args.media?.backdrops)
        videoAdapter.differ.submitList(args.video?.results)
        posterAdapter.differ.submitList(args.media?.posters)

        videoAdapter.setOnItemClickListener(click)


    }
    private fun setupRecyclerView() {
        if (args.type == Constants.MediaGalleryType.BACKDROP){
            binding.rvMedia.apply {
                adapter = backdropAdapter
                layoutManager = LinearLayoutManager(context)
                val spaceInPx = resources.getDimensionPixelSize(R.dimen.spacing_8mdp)
                addItemDecoration(VerticalSpaceItemDecoration(spaceInPx))
            }
        }

        if (args.type == Constants.MediaGalleryType.POSTER) {

            binding.rvMedia.apply {
                val flexboxLayoutManager = requireContext().createFlexboxLayoutManager()
                adapter = posterAdapter
                layoutManager = flexboxLayoutManager

            }
        }
        if (args.type == Constants.MediaGalleryType.VIDEO) {
            binding.rvMedia.apply {
                adapter = videoAdapter
                layoutManager = LinearLayoutManager(context)
                val spaceInPx = resources.getDimensionPixelSize(R.dimen.spacing_8mdp)
                addItemDecoration(VerticalSpaceItemDecoration(spaceInPx))
            }
        }
    }

    private val click = {videoId :String->
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))

        try {
            startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}