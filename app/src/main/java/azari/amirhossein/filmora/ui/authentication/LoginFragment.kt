package azari.amirhossein.filmora.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import azari.amirhossein.filmora.databinding.FragmentLoginBinding
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.LoginViewModel
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
            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()

                viewModel.authenticateUser(username, password)

                viewModel.authResult.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is NetworkRequest.Loading -> {
                            progressbar.visibility = View.VISIBLE
                            btnLogin.visibility = View.INVISIBLE
                        }

                        is NetworkRequest.Success -> {

                            progressbar.visibility = View.GONE

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
                            progressbar.visibility = View.GONE
                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG)
                                .show()
                        }
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