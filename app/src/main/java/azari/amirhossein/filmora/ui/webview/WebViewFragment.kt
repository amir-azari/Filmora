package azari.amirhossein.filmora.ui.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.unit.Constraints
import androidx.fragment.app.Fragment
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.FragmentWebViewBinding
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.customize
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    // Binding
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    // Other
    private lateinit var args: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Extract URL
        args = arguments?.getString(Constants.BundleKey.URL_BUNDLE_KEY).toString()
        binding.apply {
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressBar.visibility = View.VISIBLE

                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)

                    val statusCode = errorResponse?.statusCode
                    val errorMessages = Constants.WebView.getHttpErrorMessages()
                    val message = errorMessages[statusCode] ?: "Error: Code $statusCode"

                    showErrorSnackbar(root,message)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: android.webkit.WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    progressBar.visibility = View.GONE
                    val errorMessage = error?.description?.toString() ?: "An unknown error occurred"

                    showErrorSnackbar(root,errorMessage)

                }
            }
            // Load URL
            webView.loadUrl(args)

            // Handle Back Button in webView
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }


    }

    fun showErrorSnackbar(root: View, errorMessage: String) {
        Snackbar.make(root, errorMessage, Snackbar.LENGTH_SHORT).apply {
            customize(
                R.color.error, R.color.white, marginBottom = 16
            )
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}