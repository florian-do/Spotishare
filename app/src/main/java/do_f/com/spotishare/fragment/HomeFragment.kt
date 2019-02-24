package do_f.com.spotishare.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import do_f.com.spotishare.*

import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.databinding.FragmentHomeBinding
import do_f.com.spotishare.dialogfragment.RoomFragment

import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BFragment() {

    private var TAG = "HomeFragment"
    private var mListener: OnFragmentInteractionListener? = null
    private val REQUEST_CODE = 1337

    private lateinit var mBinding : FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home,
            container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        master.setOnClickListener {
            App.firebaseDb.child(App.roomCode).setValue("{size:1}")
            initSession(SESSIONTYPE.MASTER, Utils.generateRoomNumber())
        }

        slave.setOnClickListener {
            val f : RoomFragment = RoomFragment.newInstance()
            f.setTargetFragment(this, REQUEST_CODE)
            f.show(fragmentManager, null)
        }

        cover.setOnClickListener {
            App.firebaseDb.child("NZ29").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    Log.d(TAG, "${p0.children.first().key}")
                    App.firebaseDb.child("NZ29").child(p0.children.first().key!!).removeValue()
                    p0.children.forEach {
                        Log.d(TAG, "${it.key}")
                    }
                }

                override fun onCancelled(p0: DatabaseError) { }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            initSession(SESSIONTYPE.SLAVE, data?.getStringExtra("roomcode")!!)
        }
    }

    private fun initSession(type : SESSIONTYPE, roomCode : String) {
        App.session.initSession(type, roomCode)
        mListener?.initQueue()
        findNavController(this).navigate(R.id.discoverFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build())
    }

    fun setImage(bitmap: Bitmap) {
        cover.setImageBitmap(bitmap)
    }

    override fun refreshSpotifyAppRemote() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun initQueue()
    }
}