package do_f.com.spotishare.base

import android.support.v4.app.Fragment
import android.view.View
import com.spotify.android.appremote.api.SpotifyAppRemote
import do_f.com.spotishare.MainActivity

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

    abstract fun refreshSpotifyAppRemote()
}