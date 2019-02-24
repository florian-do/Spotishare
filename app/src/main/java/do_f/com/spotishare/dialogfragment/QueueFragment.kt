package do_f.com.spotishare.dialogfragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import do_f.com.spotishare.App

import do_f.com.spotishare.R
import do_f.com.spotishare.adapters.QueueAdapter
import do_f.com.spotishare.base.BDialogFragment
import do_f.com.spotishare.callback.MyItemTouchHelper
import do_f.com.spotishare.model.Queue
import kotlinx.android.synthetic.main.fragment_queue.*

class QueueFragment : BDialogFragment() {

    private val adapter : QueueAdapter = QueueAdapter()
    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}
        override fun onDataChange(p0: DataSnapshot) {
            val items : MutableList<Queue> = mutableListOf()
            Log.d(TAG, "onDataChange: ${p0.key}")
            p0.children.forEach {
                try {
                    Log.d(TAG, "onDataChange: ${it.key}")
                    it.getValue(Queue::class.java)?.let { data ->
                        items.add(data)
                    }
                } catch (e : DatabaseException) {
                    Log.e(TAG, "", e)
                }
            }
            adapter.initData(items)
        }
    }
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
        val database = App.firebaseDb.child(App.roomCode)

        database.addChildEventListener(childEventListener)
        rvFeed.setHasFixedSize(true)
        rvFeed.layoutManager = LinearLayoutManager(context!!)
        rvFeed.adapter = adapter

        val callback : ItemTouchHelper.Callback = MyItemTouchHelper(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvFeed)

        close.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "QueueFragment"
        fun newInstance() = QueueFragment()
    }

    override fun getDialogAnimation() : Int = R.style.QueueDialogAnimation
}
