package azari.amirhossein.filmora.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NetworkChecker @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val request: NetworkRequest,
) : ConnectivityManager.NetworkCallback() {

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> get() = _isNetworkAvailable


    fun startMonitoring(): StateFlow<Boolean> {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        _isNetworkAvailable.value = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        connectivityManager.registerNetworkCallback(request, this)
        return isNetworkAvailable
    }

    // Stop monitoring
    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        _isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        _isNetworkAvailable.value = false
    }
}


