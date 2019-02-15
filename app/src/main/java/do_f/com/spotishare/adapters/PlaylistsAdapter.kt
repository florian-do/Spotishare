package do_f.com.spotishare.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import do_f.com.spotishare.R
import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.databinding.AdapterPlaylistBinding

class PlaylistsAdapter(val context: Context) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    companion object {
        val TAG = "PlaylistsAdapter"
    }

    var items : List<Item> = emptyList()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterPlaylistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_playlist,
            p0,
            false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        items.get(p1).apply {
            holder.binding.artistName.text = name
            if (images.isNotEmpty()) {
                Glide.with(context)
                    .load(images[0].url)
                    .into(holder.binding.playlistCover)
            }
        }
    }

    class ViewHolder(val binding: AdapterPlaylistBinding) : RecyclerView.ViewHolder(binding.root)
}