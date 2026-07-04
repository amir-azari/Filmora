package azari.amirhossein.filmora.utils

import azari.amirhossein.filmora.models.prefences.TvAndMoviePreferences

/**
 * Utility object برای ساخت پارامترهای Discover API.
 *
 * این کلاس منطق مشترک buildMediaParams را که قبلاً در
 * HomeRepository، MayLikeMoviesPagingSource، و MayLikeTvsPagingSource
 * تکرار می‌شد، در یک جا متمرکز می‌کند.
 */
object MediaParamsBuilder {

    /**
     * پارامترهای Discover API را بر اساس preferences کاربر می‌سازد.
     *
     * @param preferences تنظیمات فیلم یا سریال کاربر
     * @param page شماره صفحه برای پاگینیشن (اختیاری)
     * @return Map از پارامترهای آماده برای ارسال به API
     */
    fun build(preferences: TvAndMoviePreferences, page: Int? = null): Map<String, String> {
        return buildMap {
            val favoriteGenres = preferences.favoriteGenres.joinToString("|")
            val selectedGenres = preferences.selectedGenres
                .filterNot { it in preferences.dislikedGenres }
                .joinToString("|")

            // ترکیب ژانرهای انتخابی و مورد علاقه (حذف موارد تکراری و خالی)
            val combinedGenres = (selectedGenres.split("|") + favoriteGenres.split("|"))
                .filter { it.isNotBlank() }
                .toSet()
                .joinToString("|")

            if (combinedGenres.isNotEmpty()) {
                put(Constants.Discover.WITH_GENRES, combinedGenres)
            }

            val dislikedGenres = preferences.dislikedGenres.joinToString("|")
            if (dislikedGenres.isNotEmpty()) {
                put(Constants.Discover.WITHOUT_GENRES, dislikedGenres)
            }

            val keywords = preferences.selectedKeywords.joinToString("|")
            if (keywords.isNotEmpty()) {
                put(Constants.Discover.WITH_KEYWORDS, keywords)
            }

            page?.let { put("page", it.toString()) }
        }
    }
}
