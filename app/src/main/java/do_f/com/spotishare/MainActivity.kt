package do_f.com.spotishare

import android.content.BroadcastReceiver
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.*
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId

import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.authentication.AuthenticationRequest
import do_f.com.spotishare.api.SpotifyClient
import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.fragment.HomeFragment
import do_f.com.spotishare.fragment.DiscoverFragment
import do_f.com.spotishare.model.Queue
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity";
        val CLIENT_ID = "***REMOVED***";
        val REQUEST_CODE = 1337
        val REDIRECT_URI = "***REMOVED***"
        val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        val FCM_INTENT_FILTER = "fcm_service_intent_filter"
        var queueSize : Long = 0
        var roomCode : String = ""
    }

    private var mCountDownTimer : CountDownTimer? = null
    private var mSpotifyAppRemote : SpotifyAppRemote? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private var mFCMToken = "";
    private var currentAlbumImage : Bitmap? = null


    private val mBroadcastReceiver : BroadcastReceiver = object  : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {

        }
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            // A new comment has been added, add it to the displayed list

            try {
                val comment: Queue? = dataSnapshot.getValue(Queue::class.java)
            } catch(e : DatabaseException)  {

            }

            // ...
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            val newComment: Queue? = dataSnapshot.getValue(Queue::class.java)
            val commentKey = dataSnapshot.key

            // ...
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            val commentKey = dataSnapshot.key

            // ...
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            val movedComment = dataSnapshot.getValue(Queue::class.java)
            val commentKey = dataSnapshot.key

            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException())
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            queueSize = p0.childrenCount
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = App.firebaseDb
        val tmp : MutableList<Queue> = mutableListOf()
        tmp.add(Queue("spotify:song:1Yfe3ONJlioHys7jwHdfVm", "Hier", "Lomepal", true))
        tmp.add(Queue("spotify:song:1Yfe3ONJlioHys7jwHdfVm", "Ojd", "Lomepal", true))
        tmp.add(Queue("spotify:song:1Yfe3ONJlioHys7jwHdfVm", "demain", "Lomepal", true))
        tmp.add(Queue("spotify:song:1Yfe3ONJlioHys7jwHdfVm", "Beonoj", "Lomepal", true))
        tmp.add(Queue("spotify:song:1Yfe3ONJlioHys7jwHdfVm", "Bonjour", "Lomepal", true))
        database.setValue(tmp)
//        database.addChildEventListener(childEventListener)
        database.addListenerForSingleValueEvent(valueEventListener)
        database.addValueEventListener(valueEventListener)
        // [END read_message]

//        FirebaseMessaging.getInstance().subscribeToTopic("weather")
//            .addOnCompleteListener { task ->
//                var msg = "OK"
//                if (!task.isSuccessful) {
//                    msg = "fail";
//                }
//
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w(TAG, "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }

            // Get new Instance ID token
            val token = it.result?.token

            // Log and toast
            mFCMToken = token!!
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == MainActivity.REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)

            when (response.type) {
                // Response was successful and contains auth token
                AuthenticationResponse.Type.TOKEN -> {
                    App.mRefreshStrategy.refresh(SpotifyClient::class.java)
                    PreferenceManager
                        .getDefaultSharedPreferences(applicationContext)
                        .edit()
                        .putString(SpotifyClient.SP_TOKEN, response.accessToken)
                        .apply()

                    App.mSpotifyClient.mAccessToken = response.accessToken

                    Log.d(TAG, " ${response.accessToken}")
                    Log.d(TAG, " ${response.code}")
                    Log.d(TAG, " ${response.expiresIn}")
                    Log.d(TAG, " ${response.state}")
                }

                // Auth flow returned an error
                AuthenticationResponse.Type.ERROR -> {
                    Log.d(TAG, ": AuthenticationResponse.Type.ERROR")
                }
            }
        }
    }

    private fun initPlayer(spotifyAppRemote: SpotifyAppRemote, it: PlayerState) {
        spotifyAppRemote.apply {
            currentSong.text = it.track.name+" • "+it.track.artist.name

            mHandler.post {
                song_progression.max = it.track.duration.toInt()
                song_progression.progress = it.playbackPosition.toInt()
            }

            if (!it.isPaused)
                launchTrackProgression(it.playbackPosition, it.track.duration)
        }
    }

    private fun launchTrackProgression(position : Long, songDuration : Long) {
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }

        mCountDownTimer = object : CountDownTimer(songDuration - position, 500) {
            override fun onFinish() {

            }

            override fun onTick(p0: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    song_progression.setProgress(songDuration.toInt() - p0.toInt(), false)
                } else {
                    song_progression.progress = songDuration.toInt() - p0.toInt()
                }
            }
        }
        mCountDownTimer!!.start();
    }

    private fun spotifyAppRemoteConnect() {
        mSpotifyAppRemote?.apply {
            playerApi.subscribeToPlayerState().setEventCallback {
                initPlayer(this, it)

                val track = it.track
                val bmp = mSpotifyAppRemote?.imagesApi?.getImage(track.imageUri)
                bmp?.setResultCallback { bmp ->
                    currentAlbumImage = bmp
                    val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    navHost?.let {
                        it.childFragmentManager.primaryNavigationFragment?.let { f ->
                            if (f is HomeFragment)
                                f.setImage(bmp)

                            if (f is DiscoverFragment)
                                f.updateBackground(bmp)
                        }
                    }
                }
                Log.d(TAG, " ${track.album.name}")
            }
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.childFragmentManager?.primaryNavigationFragment?.let {
            if (it is BFragment) {
                it.initSpotifyAppRemote()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connectToSpotify()

        // Register LocalBroadcast
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(mBroadcastReceiver, IntentFilter(FCM_INTENT_FILTER))
    }

    fun connectToSpotify() {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onFailure(p0: Throwable?) {
                Log.e(TAG, "error ", p0)
                connectToSpotify()
            }

            override fun onConnected(p0: SpotifyAppRemote?) {
                Log.d(TAG, "onConnected")
                mSpotifyAppRemote = p0
                spotifyAppRemoteConnect()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
        mSpotifyAppRemote = null

        // unregister LocalBroadcast
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(mBroadcastReceiver)
    }

    // @TODO add internet check
    override fun onResume() {
        super.onResume()
        if (App.mRefreshStrategy.shouldRefresh(SpotifyClient::class.java))
            refreshSpotifyToken()
        else {
            App.mSpotifyClient.mAccessToken = PreferenceManager
                .getDefaultSharedPreferences(applicationContext)
                .getString(SpotifyClient.SP_TOKEN, "")!!
            Log.d(TAG, "onResume: getToken from SP : ${App.mSpotifyClient.mAccessToken}")
        }
    }

    private fun refreshSpotifyToken() {
        Log.d(TAG, "Refresh Token")
        val builder = AuthenticationRequest.Builder(
            MainActivity.CLIENT_ID, AuthenticationResponse.Type.TOKEN,
            MainActivity.REDIRECT_URI
        )

        builder.setScopes(arrayOf("streaming", "user-read-currently-playing", "user-read-playback-state"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, MainActivity.REQUEST_CODE, request)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val window = window
        window.setFormat(PixelFormat.RGBA_8888)
    }

    fun getSpotifyAppRemote() : SpotifyAppRemote? {
        return mSpotifyAppRemote
    }

    fun getCurrentAlbumBitmap() : Bitmap? = currentAlbumImage

    private fun getAccessToken(callback : (String) -> Unit) {
        AsyncTask.execute {
            val googleCredential = GoogleCredential
                .fromStream(resources.openRawResource(R.raw.***REMOVED***))
                .createScoped(Arrays.asList(MESSAGING_SCOPE))
            googleCredential.refreshToken()
            callback.invoke(googleCredential.getAccessToken())
        }
    }
}