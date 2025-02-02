package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.databinding.ItemLoadStateBinding

class DataLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<DataLoadStateAdapter.LoadStateViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOAD_STATE = 1
    }

    class LoadStateViewHolder(private val binding: ItemLoadStateBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    binding.progressBar.apply {
                        visibility = View.VISIBLE
                        alpha = 0f
                        animate().alpha(1f).setDuration(300).start()
                    }
                    binding.btnRetry.animate().alpha(0f).setDuration(300)
                        .withEndAction { binding.btnRetry.visibility = View.GONE }
                        .start()
                    binding.txtErrorMessage.animate().alpha(0f).setDuration(300)
                        .withEndAction { binding.txtErrorMessage.visibility = View.GONE }
                        .start()
                }
                is LoadState.Error -> {
                    binding.progressBar.animate().alpha(0f).setDuration(300)
                        .withEndAction { binding.progressBar.visibility = View.GONE }
                        .start()
                    binding.btnRetry.apply {
                        visibility = View.VISIBLE
                        alpha = 0f
                        animate().alpha(1f).setDuration(300).start()
                    }
                    binding.txtErrorMessage.apply {
                        visibility = View.VISIBLE
                        text = loadState.error.localizedMessage
                        alpha = 0f
                        animate().alpha(1f).setDuration(300).start()
                    }
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRetry.visibility = View.GONE
                    binding.txtErrorMessage.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

}
