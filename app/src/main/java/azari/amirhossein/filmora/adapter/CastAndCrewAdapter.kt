package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCastAndCrewBinding
import azari.amirhossein.filmora.models.detail.ResponseCredit.Cast
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.HeightPayload
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import javax.inject.Inject

class CastAndCrewAdapter @Inject constructor() :
    RecyclerView.Adapter<CastAndCrewAdapter.ViewHolder>() {

    private var maxItemHeight = 0
    private var isHeightCalculated = false

    private var onItemClickListener: ((Cast) -> Unit)? = null

    fun setOnItemClickListener(listener: (Cast) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemCastAndCrewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.ivProfile.scaleType

        fun bind(item: Cast) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.profilePath
                }

                ivProfile.loadImageWithoutShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false
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
                tvCharacterDescription.text = item.character
            }
        }

        fun setFixedHeight(maxItemHeight: Int) {
            if (maxItemHeight > 0) {
                binding.root.layoutParams.height = maxItemHeight
            }
        }
    }

    fun submitList(list: List<Cast>) {
        val limitedList = if (list.size > 10) list.subList(0, 10) else list
        differ.submitList(limitedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCastAndCrewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private val diffCallback = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Cast, newItem: Cast) = oldItem == newItem
        override fun getChangePayload(oldItem: Cast, newItem: Cast): Any? {
            return if (maxItemHeight > 0) HeightPayload(maxItemHeight) else null
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}

