package do_f.com.spotishare.dialogfragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import do_f.com.spotishare.App

import do_f.com.spotishare.R
import do_f.com.spotishare.base.BDialogFragment
import kotlinx.android.synthetic.main.fragment_room.*

class RoomFragment : BDialogFragment() {

    companion object {
        const val TAG = "RoomFragment"
        fun newInstance() = RoomFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pin.requestFocus()
        Handler().postDelayed({
            val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }, 250)

        submit.setOnClickListener {
            if (pin.text.length == 4) {
                App.firebaseDb.child(pin.text.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.childrenCount > 0) {
                                val intent : Intent = Intent()
                                intent.putExtra("roomcode", pin.text.toString())
                                targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
                                dismiss()
                            } else {
                                Snackbar.make(view!!, "There is no room associate with this code", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            } else {
                Snackbar.make(view!!, "Invalid code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getStatusBarColor(): Int = R.color.green_spotify
}