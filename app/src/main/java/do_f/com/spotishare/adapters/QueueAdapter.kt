package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import do_f.com.spotishare.App
import do_f.com.spotishare.R
import do_f.com.spotishare.databinding.AdapterQueueBinding
import do_f.com.spotishare.model.Queue
import java.util.*

class QueueAdapter : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    var items : MutableList<Queue> = mutableListOf()
    val TAG = "QueueAdapter"

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterQueueBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_queue,
            p0, false
        )

        return ViewHolder(binding)
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

    fun onItemMove(from: Int, to: Int) {
        Collections.swap(items, from, to)
        notifyItemMoved(from, to)

        val qFrom : Queue = items[from]
        val qTo : Queue = items[to]
        val childUpdates = HashMap<String, Any>()
        val keyFrom = qFrom.key
        val keyTo = qTo.key
        val roomCode = App.roomCode

        Log.d(TAG, "$from : $roomCode/$keyFrom -> ${qTo.song}")
        Log.d(TAG, "$to : $roomCode/$keyTo -> ${qFrom.song}")

        //@TODO gerer l'update de queue avec une variable de position stocker en bdd
        childUpdates["$roomCode/$keyFrom"] = qTo
        childUpdates["$roomCode/$keyTo"] = qFrom
        App.firebaseDb.updateChildren(childUpdates)
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
            holder.binding.songName.text = it.song
            holder.binding.songArtistAlbum.text = it.artist
            holder.binding.explicit = it.explicit
        }
    }

    class ViewHolder(val binding : AdapterQueueBinding) : RecyclerView.ViewHolder(binding.root)
}