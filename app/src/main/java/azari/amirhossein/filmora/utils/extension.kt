package azari.amirhossein.filmora.utils

import android.animation.ValueAnimator
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.text.TextStyle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.models.ResponseLanguage
import azari.amirhossein.filmora.models.detail.CreatedBy
import azari.amirhossein.filmora.models.detail.Network
import azari.amirhossein.filmora.models.detail.ProductionCompany
import azari.amirhossein.filmora.models.detail.ProductionCountry
import azari.amirhossein.filmora.models.detail.ResponseMovieDetails
import azari.amirhossein.filmora.models.detail.SpokenLanguage
import coil3.load
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

fun Snackbar.customize(
    backgroundColor: Int? = null,
    textColor: Int? = null,
    position: Int = Gravity.BOTTOM,
    textSize: Float? = 12f,
    marginTop: Int? = null,
    marginBottom: Int? = null,
    marginHorizontal: Int? = 14.dp(context),
): Snackbar {
    val context = view.context

    // Set background color
    backgroundColor?.let {
        this.view.backgroundTintList = ContextCompat.getColorStateList(this.view.context, it)
    }

    // Set text size
    textSize?.let {
        val textView =
            this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textSize = it
    }

    // Set text color
    textColor?.let {
        val textView =
            this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this.view.context, it))
    }

    // Set gravity
    val params = this.view.layoutParams as android.widget.FrameLayout.LayoutParams
    params.gravity = position or Gravity.CENTER_HORIZONTAL

    // Set margins for top and bottom
    if (marginTop != null) {
        params.topMargin = marginTop.dp(context)
    } else if (position == Gravity.TOP) {
        params.topMargin = 50.dp(context)
    }
    params.bottomMargin = marginBottom?.dp(context) ?: params.bottomMargin

    // Set horizontal margins
    marginHorizontal?.let {
        params.leftMargin = it.dp(context)
        params.rightMargin = it.dp(context)
    }
    params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    this.view.layoutParams = params

    // Set radius
    val drawable = MaterialShapeDrawable().apply {
        setShapeAppearanceModel(
            ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 26F)
                .build()
        )
    }
    this.view.background = drawable


    return this
}

fun Int.dp(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun String.toFormattedDate(): String {
    return if (this.isNullOrEmpty()) {
        Constants.Message.INVALID_DATE
    } else {
        runCatching {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(this)
            date?.let { outputFormat.format(it) } ?: Constants.Message.INVALID_DATE
        }.getOrElse { Constants.Message.INVALID_DATE }
    }
}

fun Int?.toFormattedWithUnits(): String {
    if (this == null) return Constants.Defaults.NOT_APPLICABLE

    return when {
        this >= 1_000_000_000 -> String.format(
            Locale.ENGLISH,
            "%.2f billion",
            this / 1_000_000_000.0
        )

        this >= 1_000_000 -> String.format(Locale.ENGLISH, "%.2f million", this / 1_000_000.0)
        this >= 1_000 -> String.format(Locale.ENGLISH, "%.2f thousand", this / 1_000.0)
        else -> this.toString()
    }
}

fun List<ProductionCountry?>?.toCountryNames(): String {
    return this?.filterNotNull()?.joinToString("\n") { it.name ?: "Unknown Country" } ?: Constants.Defaults.NOT_APPLICABLE
}

fun List<ProductionCompany?>?.toCompanyNames(): String {
    return this?.filterNotNull()?.joinToString("\n") { "${it.name} (${it.originCountry})" } ?: Constants.Defaults.NOT_APPLICABLE
}

fun List<CreatedBy?>?.toCreatorNames(): String {
    return this?.filterNotNull()?.joinToString("\n") { "${it.name}" } ?: Constants.Defaults.NOT_APPLICABLE
}

fun List<Network?>?.toNetworkNames(): String {
    return this?.filterNotNull()?.joinToString("\n") { "${it.name}" } ?: Constants.Defaults.NOT_APPLICABLE
}

fun String?.getFullLanguageName(languages: NetworkRequest<ResponseLanguage>?): String? {
    return if (languages is NetworkRequest.Success) {
        languages.data?.find { it.iso6391 == this }?.englishName
    } else {
        this
    }
}

fun Int?.toFormattedRuntime(): String {
    return if (this != null) {
        val hours = this / 60
        val minutes = this % 60
        if (hours > 0) {
            "$hours hour${if (hours > 1) "s" else ""} $minutes minute${if (minutes > 1) "s" else ""}"
        } else {
            "$minutes minute${if (minutes > 1) "s" else ""}"
        }
    } else {
        "N/A"
    }
}

fun Double.toFormattedVoteAverage(): String {
    return String.format(
        Locale.getDefault(),
        "%.1f",
        this
    )
}


fun List<SpokenLanguage?>?.toSpokenLanguagesText(): String {
    return this?.filterNotNull()?.joinToString("\n") { it.englishName ?: "Unknown Language" }
        ?: Constants.Defaults.NOT_APPLICABLE
}
fun LifecycleOwner.observeLoginStatus(
    sessionManager: SessionManager,
    onGuestAction: () -> Unit,
    onUserAction: () -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            sessionManager.isGuest().collect { isGuest ->
                if (isGuest) {
                    onGuestAction()
                } else {
                    onUserAction()
                }
            }
        }
    }
}

fun ImageView.loadImageWithoutShimmer(
    path: String?,
    baseUrl: String,
    placeholderRes: Int,
    errorRes: Int,
) {
    if (path.isNullOrEmpty()) {
        scaleType = ImageView.ScaleType.CENTER
        setImageResource(placeholderRes)
    } else {
        load(baseUrl + path) {
            crossfade(true)
            crossfade(400)
            listener(
                onError = { _, _ ->
                    setImageResource(errorRes)
                    scaleType = ImageView.ScaleType.CENTER
                }
            )
        }
    }
}
fun LinearLayout.setupOverviewExpansion(
    txtOverview: TextView,
    imgExpand: ImageView,
    overviewMaxLines: Int,
    isOverviewExpanded: Boolean,
    onExpansionChanged: (Boolean) -> Unit,
) {
    var isExpanded = isOverviewExpanded
    val clickListener = View.OnClickListener {
        isExpanded = !isExpanded
        onExpansionChanged(isExpanded)

        TransitionManager.beginDelayedTransition(
            this,
            AutoTransition()
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
        )

        val initialHeight = txtOverview.height
        txtOverview.maxLines = if (isExpanded) Int.MAX_VALUE else overviewMaxLines
        txtOverview.measure(
            View.MeasureSpec.makeMeasureSpec(txtOverview.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = txtOverview.measuredHeight

        ValueAnimator.ofInt(initialHeight, targetHeight).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                txtOverview.layoutParams.height = value
                txtOverview.requestLayout()
            }
            start()
        }

        imgExpand.animate()
            .rotation(if (isExpanded) 180f else 0f)
            .setDuration(400)
            .start()
    }

    txtOverview.setOnClickListener(clickListener)
    imgExpand.setOnClickListener(clickListener)
}


