package azari.amirhossein.filmora.utils

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar

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


