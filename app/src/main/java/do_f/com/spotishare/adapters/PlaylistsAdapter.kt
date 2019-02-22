package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import do_f.com.spotishare.R
import do_f.com.spotishare.databases.entities.Playlists
import do_f.com.spotishare.databinding.AdapterPlaylistBinding

class PlaylistsAdapter(val rm: RequestManager, val listener: (p : Playlists, v : View) -> Unit) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    companion object {
        val TAG = "PlaylistsAdapter"
    }

    var items : List<Playlists> = emptyList()

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
        items[p1].apply {

            holder.binding.root.setOnClickListener {
                listener.invoke(this, it)
            }
            holder.binding.artistName.text = name
            if (images.isNotEmpty()) {
                rm.load(images[0].url)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.binding.playlistCover)
            }
        }
    }

    class ViewHolder(val binding: AdapterPlaylistBinding) : RecyclerView.ViewHolder(binding.root)
}