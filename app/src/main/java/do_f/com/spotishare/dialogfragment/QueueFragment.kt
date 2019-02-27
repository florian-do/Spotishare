package do_f.com.spotishare.dialogfragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.spotify.protocol.types.Track
import do_f.com.spotishare.App
import do_f.com.spotishare.MainActivity

import do_f.com.spotishare.R
import do_f.com.spotishare.adapters.QueueAdapter
import do_f.com.spotishare.base.BDialogFragment
import do_f.com.spotishare.callback.MyItemTouchHelper
import do_f.com.spotishare.model.Queue
import kotlinx.android.synthetic.main.fragment_queue.*

class QueueFragment : BDialogFragment() {

    private var selectCount = 0
    private lateinit var adapter : QueueAdapter
    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Log.d(TAG, "onChildMoved: ${p0.key} | $p1")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            try {
                val data : Queue = p0.getValue(Queue::class.java)!!
                data.key = p0.key!!
                adapter.addItem(data)
            } catch (e : DatabaseException) {

            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            adapter.removeItem(p0.key!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_queue, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, ":${App.roomCode} ")
        adapter = QueueAdapter(Glide.with(this))
        initList()
        val database = App.firebaseDb.child(App.roomCode)
        database.addChildEventListener(childEventListener)
        rvFeed.setHasFixedSize(true)
        rvFeed.layoutManager = LinearLayoutManager(context!!)
        rvFeed.adapter = adapter
        adapter.setCheckedListener {
            when (it) {
                true -> selectCount++
                false -> selectCount--
            }

            if (selectCount > 0)
                selection_menu.visibility = View.VISIBLE
            else
                selection_menu.visibility = View.GONE
        }

        val callback : ItemTouchHelper.Callback = MyItemTouchHelper(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvFeed)

        close.setOnClickListener { dismiss() }
        remove_selection.setOnClickListener {
            adapter.removeSelectItem()
            selectCount = 0
            selection_menu.visibility = View.GONE
        }
    }

    private fun initList() {
        addTitle("Now Playing")
        val track : Track? = (activity as MainActivity).getCurrentTrack()
        val bmp = (activity as MainActivity).getCurrentAlbumBitmap()
        track?.let {
            val data = Queue()
            data.bmp = bmp
            data.itemViewType = QueueAdapter.TYPE_NOW
            data.song = it.name
            data.artist = it.artist.name
            adapter.addItem(data)
        }

        addTitle("Queue :")
    }

    private fun addTitle(title: String) {
        val nowPlaying = Queue()
        nowPlaying.title = title
        nowPlaying.itemViewType = QueueAdapter.TYPE_TITLE
        adapter.addItem(nowPlaying)
    }

    fun updateNowPlaying(bmp: Bitmap?, track: Track) {
        Log.d(TAG, "updateNowPlaying: ")
        val data = Queue()
        data.bmp = bmp
        data.itemViewType = QueueAdapter.TYPE_NOW
        data.song = track.name
        data.artist = track.artist.name
        adapter.updateNowPlaying(data)
    }

    companion object {
        const val TAG = "QueueFragment"
        fun newInstance() = QueueFragment()
    }

    override fun getDialogAnimation() : Int = R.style.QueueDialogAnimation
}
