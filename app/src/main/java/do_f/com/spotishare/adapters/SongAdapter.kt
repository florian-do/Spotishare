package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import do_f.com.spotishare.R
import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.databases.entities.Row
import do_f.com.spotishare.databinding.AdapterSongBinding

class SongAdapter : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private var items = emptyList<Row>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterSongBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_song,
            p0,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        items[p1].let {
            holder.binding.songName.text = it.song_name
            holder.binding.songArtistAlbum.text = holder.binding
                .songArtistAlbum
                .resources
                .getString(R.string.artist_album, it.artist_name, it.album_name)
        }
    }

    fun setData(_items : List<Row>) {
        items = _items
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : AdapterSongBinding) : RecyclerView.ViewHolder(binding.root)
}