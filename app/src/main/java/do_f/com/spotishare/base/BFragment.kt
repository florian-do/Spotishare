package do_f.com.spotishare.base

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.View
import com.spotify.android.appremote.api.SpotifyAppRemote
import do_f.com.spotishare.MainActivity
import do_f.com.spotishare.R

abstract class BFragment : Fragment() {

    companion object {
        val TAG = "BFragment"
    }

    fun initSpotifyAppRemote() {
        refreshSpotifyAppRemote()
    }

    fun getSpotifyAppRemote() : SpotifyAppRemote {
        return (activity as MainActivity).getSpotifyAppRemote()!!
    }

    fun getMainActivity() : MainActivity? = (activity as MainActivity)

    fun dataSuccessfullyLoad(v: View) {
        v.animate().alpha(1F).setDuration(400L).setStartDelay(100L).start()
    }

    fun showDialog(s: String) {
        val b : AlertDialog.Builder = AlertDialog.Builder(context!!)
        b.setMessage(s)
        b.setPositiveButton(R.string.yes, null)
        b.show()
    }

    abstract fun refreshSpotifyAppRemote()
}