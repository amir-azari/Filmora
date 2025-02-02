package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemSimilarRemommendationBinding
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import javax.inject.Inject

class MoviesAdapter @Inject constructor() :
    PagingDataAdapter<ResponseMoviesList.Result, MoviesAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var onItemClickListener: ((ResponseMoviesList.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseMoviesList.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemSimilarRemommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: ResponseMoviesList.Result?) {
            if (item == null) return

            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.posterPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.posterPath
                }

                imgPoster.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true,
                    imgShimmerContainer
                )

                txtTitle.text = item.title
                txtYear.text = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                txtRating.text = String.format("%.1f", item.voteAverage)
            }
            // Click
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSimilarRemommendationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResponseMoviesList.Result>() {
            override fun areItemsTheSame(oldItem: ResponseMoviesList.Result, newItem: ResponseMoviesList.Result): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ResponseMoviesList.Result, newItem: ResponseMoviesList.Result): Boolean {
                return oldItem == newItem
            }
        }
    }
}
