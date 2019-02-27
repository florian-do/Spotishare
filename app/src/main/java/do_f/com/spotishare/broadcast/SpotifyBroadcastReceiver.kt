package do_f.com.spotishare.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SpotifyBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SpotifyBroadcastRv"
        const val SPOTIFY_PACKAGE = "com.spotify.music"
        const val PLAYBACK_STATE_CHANGED = "$SPOTIFY_PACKAGE.playbackstatechanged"
        const val QUEUE_CHANGED = "$SPOTIFY_PACKAGE.queuechanged"
        const val METADATA_CHANGED = "$SPOTIFY_PACKAGE.metadatachanged"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {

        Log.d(TAG, "onReceive")
        p1?.let {
            when (it.action) {
                METADATA_CHANGED -> {
                    val trackId = it.getStringExtra("id")
                    val artistName = it.getStringExtra("artist")
                    val albumName = it.getStringExtra("album")
                    val trackName = it.getStringExtra("track")
                    val trackLengthInSec = it.getIntExtra("length", 0)
                    Log.d(TAG, "$trackId | $artistName | $albumName | $trackName | $trackLengthInSec")
                }
                PLAYBACK_STATE_CHANGED -> {
                    val playing = it.getBooleanExtra("playing", false)
                    val positionInMs = it.getIntExtra("playbackPosition", 0)

                    Log.d(TAG, "$playing | $positionInMs")
                    // Do something with extracted information
                }
                QUEUE_CHANGED -> Log.d(TAG, "queue change")
                // Sent only as a notification, your app may want to respond accordingly.
                else -> {

                }
            }
        }
    }
}