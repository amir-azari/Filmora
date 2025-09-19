package azari.amirhossein.filmora.utils.extensions

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.models.ResponseError
import azari.amirhossein.filmora.utils.network.Failure
import azari.amirhossein.filmora.utils.network.Resource
import azari.amirhossein.filmora.utils.view.UiText
import coil3.load
import coil3.request.CachePolicy
import coil3.request.allowHardware
import coil3.request.allowRgb565
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import retrofit2.Response

fun <T> Response<T>.toResource(): Resource<T> {
    return try {
        if (isSuccessful) {
            body()?.let {
                if (it is Collection<*> && it.isEmpty()) {
                    Resource.Error(Failure.EmptyResponse)
                } else {
                    Resource.Success(it)
                }
            } ?: Resource.Error(Failure.UnknownError)
        } else {
            val errorBody = errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ResponseError::class.java)
            Resource.Error(Failure.CustomError(errorResponse.statusMessage ?: "Unknown Error"))
        }
    } catch (e: Exception) {
        Resource.Error(Failure.UnknownError)
    }
}


fun ImageView.loadImageWithShimmer(
    path: String?,
    fallbackRes: Int,
    errorRes: Int,
    originalScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
    hasStroke: Boolean = false,
    shimmerLayout: ShimmerFrameLayout,
    duration: Int = 300

) {
    if (!path.isNullOrBlank()) {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
        this.visibility = View.INVISIBLE
    }

    load(path) {
        crossfade(duration)
        error(errorRes)
        fallback(fallbackRes)
        diskCachePolicy(CachePolicy.ENABLED)
        memoryCachePolicy(CachePolicy.ENABLED)

        listener(
            onStart = { _ ->
                if (!path.isNullOrBlank()) {
                    shimmerLayout.startShimmer()
                    shimmerLayout.visibility = View.VISIBLE
                    this@loadImageWithShimmer.visibility = View.INVISIBLE
                }
            },
            onSuccess = { _, _ ->
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                this@loadImageWithShimmer.visibility = View.VISIBLE
                this@loadImageWithShimmer.scaleType = originalScaleType
            },
            onError = { _, _ ->
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                this@loadImageWithShimmer.visibility = View.VISIBLE

                if (hasStroke && this@loadImageWithShimmer is ShapeableImageView) {
                    this@loadImageWithShimmer.strokeWidth = 2.0f
                }

                this@loadImageWithShimmer.scaleType = ImageView.ScaleType.CENTER
            }
        )
    }
}

