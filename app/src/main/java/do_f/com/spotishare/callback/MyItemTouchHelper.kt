package do_f.com.spotishare.callback

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import do_f.com.spotishare.adapters.QueueAdapter

class MyItemTouchHelper(val adapter: QueueAdapter) : ItemTouchHelper.Callback() {

    var dragFrom = -1
    var dragTo = -1

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        if (dragFrom == -1)
            dragFrom = p1.adapterPosition
        dragTo = p2.adapterPosition

        adapter.onItemMove(p1.adapterPosition, p2.adapterPosition)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            adapter.onReleaseItemMove(dragFrom, dragTo)
        }
        dragFrom = -1
        dragTo = -1
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }
}