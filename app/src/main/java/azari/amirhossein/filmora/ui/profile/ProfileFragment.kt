package azari.amirhossein.filmora.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentProfileBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.SharedAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    //Binding
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val accountViewModel: SharedAccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container , false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            sessionManager.isGuest().collect { isGuest ->
                if (isGuest) {
                    binding.tvUsername.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                } else {
                    binding.tvName.visibility = View.VISIBLE
                    binding.tvUsername.visibility = View.VISIBLE
                    binding.btnLogin.visibility = View.GONE

                    accountViewModel.accountDetails.collect { state ->
                        if (state is NetworkRequest.Success) {
                            state.data?.let { account ->
                                binding.tvName.text = account.name
                                binding.tvUsername.text = account.username
                            }
                        }
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment(loginType =  Constants.LoginType.PROFILE)
            findNavController().navigate(action)
        }

        binding.cvFavorite.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}