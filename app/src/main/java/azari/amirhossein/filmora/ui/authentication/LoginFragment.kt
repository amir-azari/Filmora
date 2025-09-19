package azari.amirhossein.filmora.ui.authentication


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentLoginBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.view.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Shared Authentication ViewModel
    private val viewModel: AuthenticationViewModel by hiltNavGraphViewModels(R.id.auth_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout with binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()      // Setup UI interactions
        observeViewModel()    // Observe state and navigation events
    }

    // --- UI Listeners ---
    private fun setupListeners() {
        binding.apply {
            // Text change listeners
            etUsername.doOnTextChanged { text, _, _, _ ->
                validateUsername(text.toString())
                viewModel.onUsernameChanged(text.toString())
            }
            etPassword.doOnTextChanged { text, _, _, _ ->
                validatePassword(text.toString())
                viewModel.onPasswordChanged(text.toString())
            }

            // Editor actions
            etUsername.setOnEditorActionListener { _, actionId, _ ->
                handleEditorAction(actionId, etPassword)
            }
            etPassword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    btnLogin.performClick()
                    true
                } else {
                    false
                }
            }

            // Button clicks
            btnLogin.setOnClickListener {
                if (validateUsername(etUsername.text.toString()) && validatePassword(etPassword.text.toString())) {
                    viewModel.login()
                }
            }
            btnContinue.setOnClickListener { viewModel.continueAsGuest() }

            // Open external URLs
            tvForgotPassword.setOnClickListener {
                openUrlInBrowser(Constants.Web.RESET_PASSWORD_URL)
            }
            tvSignup.setOnClickListener {
                openUrlInBrowser(Constants.Web.SIGNUP_URL)
            }
        }
    }

    // --- Observe ViewModel State ---
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Show/hide progress bars
                    binding.loginProgressbar.isVisible = state.isLoading && state.currentAction == AuthAction.LOGIN
                    binding.continueProgressbar.isVisible = state.isLoading && state.currentAction == AuthAction.CONTINUE

                    // Enable/disable buttons
                    binding.btnLogin.isEnabled = !state.isLoading
                    binding.btnContinue.isEnabled = !state.isLoading

                    // Update button text during loading
                    binding.btnLogin.text = if (state.isLoading && state.currentAction == AuthAction.LOGIN) "" else getString(R.string.btn_login)
                    binding.btnContinue.text = if (state.isLoading && state.currentAction == AuthAction.CONTINUE) "" else getString(R.string.btn_continue)

                    // Show user messages
                    state.userMessage ?.getContentIfNotHandled()?.let { uiText ->
                        val message = when (uiText) {
                            is UiText.DynamicString -> uiText.value
                            is UiText.StringResource -> getString(uiText.id)
                        }
                        if (message.isNotEmpty()) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        }
                    }

                    // Handle navigation events
                    state.navigationEvent?.getContentIfNotHandled()?.let { destination ->
                        when (destination) {
                            is AuthNavigation.ToPreferences -> {
                                findNavController().navigate(R.id.action_loginFragment_to_movieSelection)
                            }
                            is AuthNavigation.ToHome -> {
                                findNavController().navigate(R.id.action_global_to_bottom_home)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Input Validation ---
    private fun validateUsername(username: String): Boolean {
        return if (username.isBlank()) {
            binding.tilUsername.error = getString(R.string.error_field_required)
            false
        } else if (username.contains(" ")) {
            binding.tilUsername.error = getString(R.string.error_contains_space)
            false
        } else {
            binding.tilUsername.isErrorEnabled = false
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isBlank()) {
            binding.tilPassword.error = getString(R.string.error_field_required)
            false
        } else if (password.length < 4) {
            binding.tilPassword.error = getString(R.string.error_password_short)
            false
        } else {
            binding.tilPassword.isErrorEnabled = false
            true
        }
    }

    // --- Keyboard Actions ---
    private fun handleEditorAction(actionId: Int, nextField: View): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_NEXT) {
            nextField.requestFocus()
            true
        } else {
            false
        }
    }

    // --- Open URLs in browser ---
    private fun openUrlInBrowser(url: String) {
        try {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
                .build()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()

            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))

        } catch (e: ActivityNotFoundException) {
            // Fallback if no browser
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(requireContext(), getString(R.string.error_no_browser_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}