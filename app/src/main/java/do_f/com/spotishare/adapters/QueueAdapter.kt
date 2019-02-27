package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import do_f.com.spotishare.App

import do_f.com.spotishare.R
import do_f.com.spotishare.databinding.AdapterQueueBinding
import do_f.com.spotishare.databinding.AdapterQueueNowBinding
import do_f.com.spotishare.databinding.AdapterQueueTitleBinding
import do_f.com.spotishare.model.Queue
import java.util.*

class QueueAdapter(val glide: RequestManager) : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    var items : MutableList<Queue> = mutableListOf()
    val TAG = "QueueAdapter"
    private var callback : ((isSelected: Boolean) -> Unit)? = null

    companion object {
        const val TYPE_QUEUE = 0
        const val TYPE_TITLE = 1
        const val TYPE_NOW = 2
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        Log.d(TAG, "ItemViewType = $p1")

        when (p1) {
            TYPE_QUEUE -> {
                val binding : AdapterQueueBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(p0.context), R.layout.adapter_queue, p0, false)
                return QueueViewHolder(binding)
            }
            TYPE_TITLE ->  {
                val binding : AdapterQueueTitleBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(p0.context), R.layout.adapter_queue_title, p0, false)
                return TitleViewHolder(binding)
            }
            TYPE_NOW ->  {
                val binding : AdapterQueueNowBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(p0.context), R.layout.adapter_queue_now, p0, false)
                return NowViewHolder(binding)
            }
            else -> {
                val binding : AdapterQueueBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(p0.context), R.layout.adapter_queue, p0, false)
                return QueueViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].itemViewType
    }

    override fun getItemCount(): Int = items.size

    fun initData(_items : MutableList<Queue> ) {
        items = _items
        notifyDataSetChanged()
    }

    fun addItem(data : Queue) {
        items.add(data)
        notifyItemInserted(items.size)
    }

    fun updateNowPlaying(data: Queue) {
        items[1] = data
        notifyItemChanged(1)
    }

    fun removeItem(key: String) {
        var i = 0
        items.forEach {
            if (it.key == key) {
                items.remove(it)
                notifyItemRemoved(i)
                return
            }
            i++
        }
    }

    fun removeSelectItem() {
        var i = 0
        val iterator = items.iterator()      // it will return iterator
        while (iterator.hasNext()) {
            val name = iterator.next()
            if (name.selection) {
                App.firebaseDb.child(App.roomCode).child(name.key).removeValue()
                iterator.remove()
            }
            i++
        }
        notifyDataSetChanged()
    }

    fun onItemMove(from: Int, to: Int) {
        Collections.swap(items, from, to)
        notifyItemMoved(from, to)
    }

    fun onReleaseItemMove(from: Int, to: Int) {
        Log.d(TAG, "onReleaseItemMove: $from -> $to")
//        val qFrom : Queue = items[from]
//        val qTo : Queue = items[to]
//        val childUpdates = HashMap<String, Any>()
//        val keyFrom = qFrom.key
//        val keyTo = qTo.key
//        val roomCode = App.roomCode
//
//        Log.d(TAG, "$from : $roomCode/$keyFrom -> ${qTo.song}")
//        Log.d(TAG, "$to : $roomCode/$keyTo -> ${qFrom.song}")
//
//        //@TODO gerer l'update de queue avec une variable de position stocker en bdd
//        childUpdates["$roomCode/$keyFrom"] = qTo
//        childUpdates["$roomCode/$keyTo"] = qFrom
//        App.firebaseDb.updateChildren(childUpdates)
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        items[p1].let {
            when (it.itemViewType) {
                TYPE_QUEUE -> bindQueue(holder as QueueViewHolder, it)
                TYPE_TITLE -> bindTitle(holder as TitleViewHolder, it)
                TYPE_NOW -> bindNow(holder as NowViewHolder, it)
            }
        }
    }

    fun bindQueue(holder: QueueViewHolder, it: Queue) {
        holder.binding.selection.setOnCheckedChangeListener(null)
        holder.binding.songName.text = it.song
        holder.binding.songArtistAlbum.text = it.artist
        holder.binding.explicit = it.explicit
        holder.binding.selection.isChecked = it.selection
        holder.binding.selection.setOnCheckedChangeListener { _, b ->
            if (holder.adapterPosition < items.size) {
                items[holder.adapterPosition].selection = b
                callback?.invoke(b)
            }
        }
    }

    fun bindNow(holder: NowViewHolder, it: Queue) {
        glide.load(it.bmp).into(holder.binding.cover)
        holder.binding.songName.text = it.song
        holder.binding.songArtistAlbum.text = it.artist
        holder.binding.explicit = it.explicit
    }

    fun bindTitle(holder: TitleViewHolder, it: Queue) {
        holder.binding.title.text = it.title
    }

    fun setCheckedListener(_call : (s: Boolean) -> Unit) {
        callback = _call
    }

    open class ViewHolder(val itemView : View) : RecyclerView.ViewHolder(itemView)
    class NowViewHolder(val binding : AdapterQueueNowBinding) : ViewHolder(binding.root)
    class QueueViewHolder(val binding : AdapterQueueBinding) : ViewHolder(binding.root)
    class TitleViewHolder(val binding : AdapterQueueTitleBinding) : ViewHolder(binding.root)
}