package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCelebrityCircleBinding
import azari.amirhossein.filmora.databinding.ItemCelebrityRectangleBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.models.detail.ResponseCredit.Cast
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.HeightPayload
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import javax.inject.Inject

class TrendingCelebrityAdapter @Inject constructor() :
    RecyclerView.Adapter<TrendingCelebrityAdapter.ViewHolder>() {

    private var maxItemHeight = 0
    private var isHeightCalculated = false

    private var onItemClickListener: ((ResponseTrendingCelebrity.Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (ResponseTrendingCelebrity.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemCelebrityCircleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.ivProfile.scaleType

        fun bind(item: ResponseTrendingCelebrity.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.profilePath
                }

                ivProfile.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false,
                    imgShimmerContainer
                )

                // Only calculate max height once
                if (!isHeightCalculated) {
                    root.post {
                        val height = root.height
                        if (height > maxItemHeight) {
                            maxItemHeight = height
                            isHeightCalculated = true // Mark height as calculated
                            // Apply height to all items
                            differ.currentList.forEachIndexed { index, _ ->
                                notifyItemChanged(index, HeightPayload(maxItemHeight))
                            }
                        }
                    }
                }

                // Click
                root.setOnClickListener {
                    onItemClickListener?.let { it(item) }
                }

                tvCharacterName.text = item.name
                tvCharacterDescription.text = item.knownForDepartment
            }
        }

        fun setFixedHeight(maxItemHeight: Int) {
            if (maxItemHeight > 0) {
                binding.root.layoutParams.height = maxItemHeight
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCelebrityCircleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            val payload = payloads[0] as? HeightPayload
            payload?.let {
                holder.setFixedHeight(it.height)
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        if (maxItemHeight > 0) {
            holder.setFixedHeight(maxItemHeight)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseTrendingCelebrity.Result>() {
        override fun areItemsTheSame(oldItem: ResponseTrendingCelebrity.Result, newItem: ResponseTrendingCelebrity.Result) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ResponseTrendingCelebrity.Result, newItem: ResponseTrendingCelebrity.Result) = oldItem == newItem
        override fun getChangePayload(oldItem: ResponseTrendingCelebrity.Result, newItem: ResponseTrendingCelebrity.Result): Any? {
            return if (maxItemHeight > 0) HeightPayload(maxItemHeight) else null
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}

