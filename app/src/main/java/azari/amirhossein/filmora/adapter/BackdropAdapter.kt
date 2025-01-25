package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemBackdropBinding
import azari.amirhossein.filmora.databinding.ItemPosterBinding
import azari.amirhossein.filmora.databinding.ItemSimilarRemommendationBinding
import azari.amirhossein.filmora.models.detail.ResponseCredit.Cast
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer

class BackdropAdapter  :
    RecyclerView.Adapter<BackdropAdapter.ViewHolder>() {

    private var onItemClickListener: ((ResponseImage.Backdrop) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseImage.Backdrop) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemBackdropBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgBackdrop.scaleType

        fun bind(item: ResponseImage.Backdrop) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.filePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.filePath
                }

                imgBackdrop.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_large,
                    R.drawable.image_large,
                    originalScaleType,
                    true,
                    imgShimmerContainer)



            }
            // Click
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBackdropBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    fun submitList(list: List<ResponseImage.Backdrop>) {
        val limitedList = if (list.size > 10) list.subList(0, 10) else list
        differ.submitList(limitedList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in differ.currentList.indices) {
            holder.bind(differ.currentList[position])
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseImage.Backdrop>() {
        override fun areItemsTheSame(oldItem: ResponseImage.Backdrop, newItem: ResponseImage.Backdrop): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: ResponseImage.Backdrop, newItem: ResponseImage.Backdrop): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}