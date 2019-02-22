package do_f.com.spotishare.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import do_f.com.spotishare.R
import do_f.com.spotishare.base.BDialogFragment

class RoomFragment : BDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoomFragment()
    }
}
