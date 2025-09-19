package azari.amirhossein.filmora.utils.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


object UIUtils {

    fun showBottomNavigation(view: View, duration: Long = 300) {
        if (view.visibility == View.VISIBLE) return

        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(null)
            .start()
    }

    fun hideBottomNavigation(view: View, duration: Long = 300) {
        if (view.visibility == View.GONE) return

        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    view.alpha = 1f
                }
            })
            .start()


    }

    fun showToolbar(view: View, duration: Long = 300) {
        if (view.visibility == View.VISIBLE) return

        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setListener(null)
            .start()
    }


    fun hideToolbar(view: View, duration: Long = 300) {
        if (view.visibility == View.GONE || view.visibility == View.INVISIBLE) return

        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    view.alpha = 1f
                }
            })
            .start()
    }

    fun fadeTransition(view: View, duration: Long = 200, action: () -> Unit) {
        view.animate()
            .alpha(0f)
            .setDuration(duration / 2)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    action()
                    view.animate()
                        .alpha(1f)
                        .setDuration(duration / 2)
                        .setListener(null)
                        .start()
                }
            })
            .start()
    }

    fun pulseAnimation(view: View) {
        val scaleUp = ValueAnimator.ofFloat(1f, 1.1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                view.scaleX = scale
                view.scaleY = scale
            }
        }
        scaleUp.start()
    }

    fun slideInFromRight(view: View, duration: Long = 400) {
        view.translationX = view.width.toFloat()
        view.alpha = 0f
        view.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
    }

    fun slideOutToLeft(view: View, duration: Long = 400, onEnd: () -> Unit = {}) {
        view.animate()
            .translationX(-view.width.toFloat())
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd()
                }
            })
            .start()
    }
}