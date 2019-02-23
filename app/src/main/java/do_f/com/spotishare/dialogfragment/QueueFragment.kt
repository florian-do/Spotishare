package do_f.com.spotishare.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import do_f.com.spotishare.R
import do_f.com.spotishare.base.BDialogFragment

class QueueFragment : BDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_queue, container, false)
    }

    companion object {
        fun newInstance() = QueueFragment()
    }

    override fun getDialogAnimation() : Int = R.style.QueueDialogAnimation
}
