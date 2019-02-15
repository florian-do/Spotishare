package do_f.com.spotishare.base

import android.support.v4.app.Fragment
import android.util.Log
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

    abstract fun refreshSpotifyAppRemote()
}