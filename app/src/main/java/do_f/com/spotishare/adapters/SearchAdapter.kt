package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import do_f.com.spotishare.R
import do_f.com.spotishare.api.model.Image
import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.databinding.AdapterSearchBinding

class SearchAdapter(val glide: RequestManager) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var items : List<Item> = emptyList()

    companion object {
        private const val TYPE_ARTIST = "artist"
        private const val TYPE_TRACK = "track"
        private const val TYPE_PLAYLIST = "playlist"
        private const val TAG = "SearchAdapter"
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val binding : AdapterSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_search, p0, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    fun refreshData(_items : List<Item>) {
        items = _items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        val data : Item = items[p1]

        when(data.type) {
            TYPE_ARTIST -> {
                if (data.images.isNotEmpty()) {
                    glide.load(getImageUrl(data.images).url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.binding.cover)
                }

                holder.binding.type.text = TYPE_ARTIST.capitalize()
                holder.binding.label.text = data.name
            }

            TYPE_PLAYLIST -> {
                if (data.images.isNotEmpty()) {
                    glide.load(data.images[0].url)
                        .into(holder.binding.cover)
                }

                holder.binding.type.text = TYPE_PLAYLIST.capitalize()
                holder.binding.label.text = data.name
            }

            TYPE_TRACK -> {
                if (data.album.images.isNotEmpty()) {
                    glide.load(getImageUrl(data.album.images).url)
                        .into(holder.binding.cover)
                }

                var artistsName = ""
                if (data.artists.size > 1) {
                    val tmp : MutableList<String> = mutableListOf()
                    data.artists.forEach {
                        tmp.add(it.name)
                    }

                    artistsName = tmp.joinToString(", ")

                } else {
                    artistsName = data.artists[0].name
                }

                holder.binding.type.text = holder.binding.type.resources
                    .getString(R.string.search_adapter_type_track, artistsName)
                holder.binding.label.text = data.name
            }
        }
    }

    private fun getImageUrl(images : List<Image>) : Image {
        for (it in images) {
            return if (it.height == 300) it else continue
        }

        return images[0]
    }

    class ViewHolder(val binding : AdapterSearchBinding) : RecyclerView.ViewHolder(binding.root)
}