package azari.amirhossein.filmora.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.databinding.FragmentProfileBinding
import azari.amirhossein.filmora.models.detail.RateRequest
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.viewmodel.SharedAccountViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    // Binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val accountViewModel: SharedAccountViewModel by viewModels()
    private var isGuestUser: Boolean = true

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
                isGuestUser = isGuest
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
            val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment(
                loginType = Constants.LoginType.PROFILE
            )
            findNavController().navigate(action)
        }

        binding.cvFavorite.setOnClickListener { handleNavigationOrShowDialog {
            val action = ProfileFragmentDirections.actionProfileFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }}

        binding.cvWatchlist.setOnClickListener { handleNavigationOrShowDialog {
            val action = ProfileFragmentDirections.actionProfileFragmentToWatchlistFragment()
            findNavController().navigate(action)
        }}

        binding.cvRatings.setOnClickListener { handleNavigationOrShowDialog {
            val action = ProfileFragmentDirections.actionProfileFragmentToRatedFragment()
            findNavController().navigate(action)
        }}
    }

    private fun handleNavigationOrShowDialog(navigateAction: () -> Unit) {
        if (isGuestUser) {
            showLoginDialog()
        } else {
            navigateAction()
        }
    }

    private fun showLoginDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_message, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)

        tvMessage.text = getString(R.string.dialog_login_required_message)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
