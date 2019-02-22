package do_f.com.spotishare.fragment

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import do_f.com.spotishare.App
import do_f.com.spotishare.MainActivity
import do_f.com.spotishare.R
import do_f.com.spotishare.Utils
import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.databinding.FragmentHomeBinding
import do_f.com.spotishare.dialogfragment.RoomFragment
import do_f.com.spotishare.model.Queue
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BFragment() {

    private var TAG = "HomeFragment"
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mBinding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        master.setOnClickListener {
            MainActivity.roomCode = Utils.generateRoomNumber()
            App.firebaseDb.child(MainActivity.roomCode).push().setValue("init")
            Navigation.findNavController(it).navigate(R.id.discoverFragment)

//            val arg = Bundle()
//            arg.putString(MasterFragment.ARG_PARAM1, "HEY HO HEY")
//            Navigation.findNavController(it).navigate(R.id.masterFragment, arg)
//            App.firebaseDb.child(Utils.generateRoomNumber()).setValue("create")
//            App.firebaseDb.child(Utils.generateRoomNumber()).push().setValue("push create")
        }

        slave.setOnClickListener {
            val f : RoomFragment = RoomFragment.newInstance()
            f.show(fragmentManager, null)
//            Navigation.findNavController(it).navigate(R.id.slaveFragment)
        }

        cover_placeholder.setOnClickListener {

            App.firebaseDb.child(Utils.generateRoomNumber()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, "error: ", p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {

                }

            })

            App.firebaseDb
                .child(MainActivity.queueSize.toString())
                .setValue(Queue("spotify:song:1Yfe3NJlioHys7jwHdfVm", "temp", "Lomepal", true))
        }
    }

    fun setImage(bitmap: Bitmap) {
        cover.setImageBitmap(bitmap)
    }

    override fun refreshSpotifyAppRemote() {

    }

    /**
     * Communication between activity and fragment
     */

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
