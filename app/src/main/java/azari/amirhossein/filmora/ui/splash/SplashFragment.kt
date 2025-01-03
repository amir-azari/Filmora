package azari.amirhossein.filmora.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentSplashBinding
import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences
import azari.amirhossein.filmora.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    //Binding
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    //ViewModel
    private val viewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //InitViews
        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    delay(2500)

                    try {
                        val sessionId = viewModel.getSessionId()

                        if (sessionId.isNullOrEmpty()) {
                            findNavController().navigate(R.id.splashToLogin)
                        } else {
                            val moviePrefs = sessionManager.getMoviePreferences().firstOrNull()
                            val tvPrefs = sessionManager.getTvPreferences().firstOrNull()

                            Log.d("Preferences", "Movie preferences: ${moviePrefs?.isEmpty()}")
                            Log.d("Preferences", "TV preferences: ${tvPrefs?.isEmpty()}")

                            when {
                                moviePrefs == null || moviePrefs.isEmpty() -> {
                                    findNavController().navigate(R.id.actionSplashToMoviePreferences)
                                }
                                tvPrefs == null || tvPrefs.isEmpty() -> {
                                    findNavController().navigate(R.id.actionSplashToMoviePreferences)
                                }
                                else -> {
                                    findNavController().navigate(R.id.actionSplashToHome)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SplashFragment", "Navigation error: ${e.message}")
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
            }
        }
    }
    private fun TvAndMoviePreferences.isEmpty(): Boolean {
        return selectedIds.isEmpty() &&
                favoriteGenres.isEmpty() &&
                dislikedGenres.isEmpty() &&
                selectedKeywords.isEmpty() &&
                selectedGenres.isEmpty()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}