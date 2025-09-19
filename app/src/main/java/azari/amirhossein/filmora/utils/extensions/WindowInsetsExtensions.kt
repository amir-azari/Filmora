package azari.amirhossein.filmora.utils.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


fun View.applyTopInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(top = insets.top)
        windowInsets
    }
}

fun View.applyBottomInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(bottom = insets.bottom)
        windowInsets
    }
}

fun View.applyHorizontalInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        view.updatePadding(
            left = insets.left,
            right = insets.right
        )
        windowInsets
    }
}

fun View.applySystemBarInsets(
    applyTop: Boolean = false,
    applyBottom: Boolean = false,
    applyLeft: Boolean = false,
    applyRight: Boolean = false,
    consume: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        view.updatePadding(
            top = if (applyTop) insets.top else view.paddingTop,
            bottom = if (applyBottom) insets.bottom else view.paddingBottom,
            left = if (applyLeft) insets.left else view.paddingLeft,
            right = if (applyRight) insets.right else view.paddingRight
        )

        if (consume) WindowInsetsCompat.CONSUMED else windowInsets
    }
}
