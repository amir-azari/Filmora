package azari.amirhossein.filmora.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {
    //Binding
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout with binding
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Collect destination after a 2-second delay
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(2000)

                val destination = viewModel.destination.first { it != null }

                // Navigate based on the destination type
                when (destination) {
                    is SplashNavigation.ToLogin ->
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)

                    is SplashNavigation.ToPreferences ->
                        findNavController().navigate(R.id.action_splashFragment_to_movieSelection)

                    is SplashNavigation.ToHome ->
                        findNavController().navigate(R.id.action_global_to_bottom_home)

                    else -> {

                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}