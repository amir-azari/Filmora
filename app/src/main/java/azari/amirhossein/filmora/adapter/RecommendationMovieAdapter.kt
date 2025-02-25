package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemSimilarRemommendationBinding
import azari.amirhossein.filmora.models.detail.movie.ResponseMovieDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.setClickAnimation

class RecommendationMovieAdapter :
    RecyclerView.Adapter<RecommendationMovieAdapter.ViewHolder>() {

    private var onItemClickListener: ((ResponseMovieDetails.Recommendations.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseMovieDetails.Recommendations.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemSimilarRemommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: ResponseMovieDetails.Recommendations.Result) {
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
            binding.root.setClickAnimation {
                onItemClickListener?.let { it(item) }
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSimilarRemommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseMovieDetails.Recommendations.Result>() {
        override fun areItemsTheSame(oldItem: ResponseMovieDetails.Recommendations.Result, newItem: ResponseMovieDetails.Recommendations.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseMovieDetails.Recommendations.Result, newItem: ResponseMovieDetails.Recommendations.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}