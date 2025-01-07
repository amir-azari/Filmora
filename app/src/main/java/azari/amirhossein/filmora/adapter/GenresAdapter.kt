package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.databinding.ItemGenreBinding
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import javax.inject.Inject

class GenresAdapter @Inject constructor() :
    RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseGenresList.Genre) {
            binding.apply {
                txtGenre.text = item.name
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(differ.currentList[position])

    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseGenresList.Genre>() {
        override fun areItemsTheSame(oldItem: ResponseGenresList.Genre, newItem: ResponseGenresList.Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseGenresList.Genre, newItem: ResponseGenresList.Genre): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}