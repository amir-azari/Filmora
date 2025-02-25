package azari.amirhossein.filmora.ui.detail.movie

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import azari.amirhossein.filmora.adapter.VideoAdapter
import azari.amirhossein.filmora.databinding.FragmentMovieVideoBinding
import azari.amirhossein.filmora.models.detail.ResponseVideo
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.viewmodel.MovieDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieVideoFragment : Fragment() {
    //Binding
    private var _binding: FragmentMovieVideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels({ requireParentFragment() })
    private val videoAdapter by lazy { VideoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMovieVideoBinding.inflate(inflater, container, false)
        return binding.root


    }
    private fun observeVideos() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetails.collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            binding.btnAllVideos.visibility = View.GONE
                            if (result.data?.videos?.results?.size!! > 10){
                                binding.btnAllVideos.visibility = View.VISIBLE
                            }
                            videoAdapter.submitList(result.data.videos.results)
                            Log.d("TAG", result.data.videos.toString())
                            navTo(result.data.videos)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
    private fun navTo(data : ResponseVideo){
        binding.btnAllVideos.setClickAnimation {
            val action = MovieVideoFragmentDirections.actionToMediaGalleryFragment(media = null, type = Constants.MediaGalleryType.VIDEO , video = data)
            findNavController().navigate(
                action
            )
        }
    }

    //click
    private val click = {videoId :String->
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))

        try {
            startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }
    private fun setupRecyclerView() {
        binding.rvVideo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = videoAdapter
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeVideos()

        videoAdapter.setOnItemClickListener(click)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}