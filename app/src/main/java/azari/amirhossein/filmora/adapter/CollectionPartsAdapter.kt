package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCollectionBinding
import azari.amirhossein.filmora.models.detail.ResponseCollectionDetails
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.setClickAnimation
import javax.inject.Inject

class CollectionPartsAdapter @Inject constructor() : RecyclerView.Adapter<CollectionPartsAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: ResponseCollectionDetails.Part) {
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

                txtTitle.text = item.title ?: "N/A"
                val year = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                val voteAverage = item.voteAverage?.let { String.format("%.1f", it) } ?: "N/A"
                txtRating.text = voteAverage
                txtYear.text = year
            }
            binding.root.setClickAnimation {
                onItemClickListener?.let { it(item.id) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseCollectionDetails.Part>() {
        override fun areItemsTheSame(oldItem: ResponseCollectionDetails.Part, newItem: ResponseCollectionDetails.Part): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseCollectionDetails.Part, newItem: ResponseCollectionDetails.Part): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}