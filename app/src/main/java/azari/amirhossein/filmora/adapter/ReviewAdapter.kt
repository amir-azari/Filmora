package azari.amirhossein.filmora.adapter

import android.content.Context
import android.provider.Settings.Global.getString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemMediaBinding
import azari.amirhossein.filmora.databinding.ItemReviewBinding
import azari.amirhossein.filmora.models.detail.ResponseReviews
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.setClickAnimation
import azari.amirhossein.filmora.utils.setupOverviewExpansion
import azari.amirhossein.filmora.utils.toFormattedDate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReviewAdapter @Inject constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.ivProfileReviewAuthor.scaleType
        private var isOverviewExpanded = false
        private val overviewReviewsMaxLines = 3

        fun bind(item: ResponseReviews.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.authorDetails?.avatarPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.authorDetails?.avatarPath
                }

                ivProfileReviewAuthor.loadImageWithoutShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_small,
                    R.drawable.image_small,
                    originalScaleType,
                    true
                )

                tvReviewAuthor.text = item.author
                val createdAt = item.createdAt?.toFormattedDate()
                tvReviewDate.text = context.getString(R.string.written_on_date, createdAt ?: "N/A")

                tvReviewContent.text = item.content

                tvReviewContent.maxLines = overviewReviewsMaxLines
                tvReviewContent.ellipsize = TextUtils.TruncateAt.END

                tvReviewContent.post {
                    val isEllipsized = tvReviewContent.layout?.let { layout ->
                        layout.getEllipsisCount(layout.lineCount - 1) > 0
                    } ?: false
                    imgReviewContentExpand.visibility = if (isEllipsized) View.VISIBLE else View.INVISIBLE
                }

                reviewContainer.setupOverviewExpansion(
                    txtOverview = tvReviewContent,
                    imgExpand = imgReviewContentExpand,
                    overviewMaxLines = overviewReviewsMaxLines,
                    isOverviewExpanded = isOverviewExpanded
                ) { isExpanded ->
                    isOverviewExpanded = isExpanded
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in differ.currentList.indices) {
            holder.bind(differ.currentList[position])
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseReviews.Result>() {
        override fun areItemsTheSame(oldItem: ResponseReviews.Result, newItem: ResponseReviews.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseReviews.Result, newItem: ResponseReviews.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}
