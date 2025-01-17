package azari.amirhossein.filmora.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCreditBinding
import azari.amirhossein.filmora.models.detail.ResponseCredit.Cast
import azari.amirhossein.filmora.utils.Constants
import coil3.load
import coil3.request.crossfade
import javax.inject.Inject
import kotlin.math.log

class CastAdapter @Inject constructor() :
    RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    private var onItemClickListener: ((Cast) -> Unit)? = null
    fun setOnItemClickListener(listener: (Cast) -> Unit) {
        onItemClickListener = listener
    }
    inner class ViewHolder(private val binding: ItemCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cast) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL

                if (item.profilePath == null) {
                    ivProfile.visibility = View.VISIBLE
                    ivProfile.scaleType = ImageView.ScaleType.CENTER
                    ivProfile.setImageResource(R.drawable.user)
                } else {
                    val fullPosterPath = baseUrl + Constants.ImageSize.W500 + item.profilePath

                    ivProfile.load(fullPosterPath) {
                        crossfade(true)
                        crossfade(400)
                        ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP

                    }
                    // Click
                    binding.root.setOnClickListener {
                        onItemClickListener?.let { it(item) }
                    }
                }

                tvCharacterName.text = item.name
                tvCharacterDescription.text = item.character

            }
        }


    }

    fun submitList(list: List<Cast>) {
        val limitedList = if (list.size > 10) list.subList(0, 10) else list
        differ.submitList(limitedList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCreditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}