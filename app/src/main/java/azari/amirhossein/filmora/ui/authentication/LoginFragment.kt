package azari.amirhossein.filmora.ui.authentication

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentLoginBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.customize
import azari.amirhossein.filmora.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    //Binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Set up text watchers
            etUsername.doAfterTextChanged { validateUsername(it.toString()) }
            etPassword.doAfterTextChanged { validatePassword(it.toString()) }

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

            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && password.isNotEmpty()) {
                    if (validateUsername(username) && validatePassword(password)) {
                        viewModel.authenticateUser(username, password)
                    }
                }else {
                    // Show an error if any field is empty
                    Snackbar.make(root,R.string.fillRequiredFields , Snackbar.LENGTH_SHORT).customize(
                        R.color.error,
                        R.color.white,
                        Gravity.TOP,
                    ).show()
                }



            }

            viewModel.authResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is NetworkRequest.Loading -> {
                        root.transitionToEnd()
                    }

                    is NetworkRequest.Success -> {
                        root.transitionToStart()

                        val sessionId = result.data
                        if (!sessionId.isNullOrEmpty()) {
                            //TODO Navigate to Home Fragment
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Session ID is empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is NetworkRequest.Error -> {
                        root.transitionToStart()
                        Snackbar.make(root,result.message.toString() , Snackbar.LENGTH_SHORT).customize(
                            R.color.error,
                            R.color.white,
                            Gravity.TOP,
                        ).show()

                    }
                }
            }

        }


    }
    private fun validateUsername(username: String): Boolean {
        return if (!username.contains(" ")) {
            binding.tilUsername.isErrorEnabled = false
            true
        } else {
            binding.tilUsername.error = if (username.isEmpty()) {
                getString(R.string.usernameEmpty)
            } else {
                getString(R.string.usernameContainsSpace)
            }
            false
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isNotEmpty()) {
            if (password.length >= 4) {
                binding.tilPassword.isErrorEnabled = false
                true
            } else {
                binding.tilPassword.error = getString(R.string.passwordTooShort)
                false
            }
        } else {
            binding.tilPassword.isErrorEnabled = false
            false
        }
    }
    private fun handleEditorAction(actionId: Int, nextField: View): Boolean {
        return when (actionId) {
            EditorInfo.IME_ACTION_NEXT -> {
                nextField.requestFocus()
                true
            }

            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}