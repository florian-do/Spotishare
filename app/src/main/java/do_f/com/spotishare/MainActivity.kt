package do_f.com.spotishare

import android.content.*
import android.support.v7.app.AppCompatActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.*
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import androidx.navigation.findNavController
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
import do_f.com.spotishare.dialogfragment.QueueFragment
import do_f.com.spotishare.fragment.HomeFragment
import do_f.com.spotishare.fragment.DiscoverFragment
import do_f.com.spotishare.model.Queue
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track
import do_f.com.spotishare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener {

    companion object {
        private val TAG = "MainActivity";
        val CLIENT_ID = "3b3789933bb949b4946a8a34fd29fcbf";
        val REQUEST_CODE = 1337
        val REDIRECT_URI = "http://lapusheen.chat/callback"
        val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        val FCM_INTENT_FILTER = "fcm_service_intent_filter"
        var isSpotifyInstalled : Boolean = false
    }

    private lateinit var binding : ActivityMainBinding
    private var mCountDownTimer : CountDownTimer? = null
    private var mSpotifyAppRemote : SpotifyAppRemote? = null
    private val mHandler = Handler(Looper.getMainLooper())

    private var currentAlbumImage : Bitmap? = null
    private var currentTrack : Track? = null
    private var currentPosition : Long = 0L
    private var currentPauseState : Boolean? = null

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {

        }
    }
    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) { }
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.childrenCount == 0L) {
                App.session.clear()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        window.statusBarColor = Color.argb(0, 0, 0, 0)
//        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.statusBarColor)

        if (App.session.isConnected()) {
            checkRoomExist()
        } else {
            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w(TAG, "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }

//            mFCMToken = it.result?.token!!
        }

        queue.setOnClickListener {
            val f : QueueFragment = QueueFragment.newInstance()
            f.show(supportFragmentManager, null)
        }

        play.setOnClickListener {
            binding.isPaused?.let {isPaused ->
                if (isPaused) {
                    mSpotifyAppRemote?.playerApi?.resume()
                } else {
                    mSpotifyAppRemote?.playerApi?.pause()
                }
                binding.isPaused = !binding.isPaused!!
            }
        }
    }

    private fun checkRoomExist() {
        val database = App.firebaseDb.child(App.roomCode)
        database.addListenerForSingleValueEvent(valueEventListener)
    }

    override fun updateUiAfterLogin() {
        binding.isConnected = App.session.isConnected()
    }

    var tmp = false

    private fun addToTheQueue() {
        App.firebaseDb.child(App.roomCode).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    p0.children.firstOrNull()
                    val next : Queue? = p0.children.firstOrNull()?.getValue(Queue::class.java)
                    next?.let {
                        mSpotifyAppRemote?.playerApi?.queue(it.uri)
                        App.firebaseDb.child(App.roomCode).child(it.key).removeValue()
                        Log.d(TAG, "NEXT WILL BE :${it.key} -> ${it.artist} • ${it.song}")
                    }
                } catch (e : DatabaseException) { }
            }
        })
    }

    private fun launchTrackProgression(position : Long, songDuration : Long) {
        if (mCountDownTimer != null) {
            Log.d(TAG, "launchTrackProgression: cancel")
            mCountDownTimer!!.cancel()
            tmp = false
        }

        mCountDownTimer = object : CountDownTimer(songDuration - position, 500) {
            override fun onFinish() {
                Log.d(TAG, "onFinish")
            }

            override fun onTick(p0: Long) {
                currentPosition = p0
                Log.d(TAG, "cdt: $p0 / $tmp")

                if (p0 < 5000) {
                    if (mSpotifyAppRemote != null && App.session.isConnected() && !tmp) {
                        Log.d(TAG, "ADD TO THE QUEUE")
                        addToTheQueue()
                        tmp = true
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    song_progression.setProgress(songDuration.toInt() - p0.toInt(), false)
                } else {
                    song_progression.progress = songDuration.toInt() - p0.toInt()
                }
            }
        }
        
        mCountDownTimer!!.start()
    }

    private fun initPlayer(spotifyAppRemote: SpotifyAppRemote, it: PlayerState) {
        spotifyAppRemote.apply {
            currentSong.text = it.track.name+" • "+it.track.artist.name

            mHandler.post {
                song_progression.max = it.track.duration.toInt()
                song_progression.progress = it.playbackPosition.toInt()
            }

            Log.d(TAG, "play : ${isPlaying(it.isPaused)}")
            Log.d(TAG, "seekTO: ${it.playbackPosition} -> ${it.track.duration} - $currentPosition = ${it.track.duration - currentPosition}")
            Log.d(TAG, "seekTO: ${it.playbackPosition} -> $currentPosition")
            Log.d(TAG, "seekTO : ${isSeekTo(it.playbackPosition, currentPosition)}")
            Log.d(TAG, "restart : ${isRestartingSong(it.playbackPosition, it.track)}")
            Log.d(TAG, "Prev Or Next : ${isPrevOrNext(it.playbackPosition, it.track)}")

            if (isPlaying(it.isPaused)
                || (isSeekTo(it.playbackPosition, (it.track.duration - currentPosition)) && !it.isPaused)
                || isPrevOrNext(it.playbackPosition, it.track)) {
                launchTrackProgression(it.playbackPosition, it.track.duration)
            } else if (it.isPaused) {
                Log.d(TAG, "pause: true")
                mCountDownTimer?.cancel()
            }
        }
    }

    private fun spotifyAppRemoteConnect() {
        mSpotifyAppRemote?.apply {
            motionStateApi.subscribeToMotionState().setEventCallback {
                Log.d(TAG, "motionStateApi: ${it.state}")
            }

            playerApi.subscribeToPlayerContext().setEventCallback {
                Log.d(TAG, "${it.subtitle} | ${it.title} | ${it.type} | ${it.uri}")
            }

            playerApi.subscribeToPlayerState().setEventCallback {
                if (currentPauseState == null)
                    currentPauseState = !it.isPaused

                if (currentPosition == 0L)
                    currentPosition = it.playbackPosition

                binding.isPaused = it.isPaused

                if (isPrevOrNext(it.playbackPosition, it.track)) {
                    currentPauseState = !it.isPaused
                }

                initPlayer(this, it)
                updateAlbumCover(it.track)

                currentTrack = it.track
                currentPauseState = it.isPaused
                Log.d(TAG, " ${currentTrack?.album?.name} • ${currentTrack?.name}")
            }
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.childFragmentManager?.primaryNavigationFragment?.let {
            if (it is BFragment) {
                it.initSpotifyAppRemote()
            }
        }
    }

    private fun updateAlbumCover(track: Track) {
        if (!compareAlbumCover(currentTrack?.imageUri ?: ImageUri(""), track.imageUri)) {
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
        }
    }

    override fun onStart() {
        super.onStart()
        isSpotifyInstalled = SpotifyAppRemote.isSpotifyInstalled(this)
        if (isSpotifyInstalled) {
            if (mSpotifyAppRemote != null)
                SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            connectToSpotify()
        } else {
            installSpotifyDialog()
        }

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
                Handler().postDelayed({
                    connectToSpotify()
                }, 60 * 1000)
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
        Log.d(TAG, "onStop!")
        if (App.session.getDeviceType() == SESSIONTYPE.SLAVE) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            mSpotifyAppRemote = null
        }

        // unregister LocalBroadcast
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(mBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        if (App.session.getDeviceType() == SESSIONTYPE.MASTER) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            mSpotifyAppRemote = null
        }
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

        binding.isConnected = App.session.isConnected()
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

    private fun installSpotifyDialog() {
        val b : AlertDialog.Builder = AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
        b.setMessage(R.string.install_spotify)
        b.setPositiveButton(R.string.yes) { _, _ ->
            val appPackageName = "com.spotify.music"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }

        }
        b.setNegativeButton(R.string.no, null)
        b.show()
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

    private fun compareTrack(cTrack : Track?, new : Track) : Boolean {
        if (cTrack == null)
            return true
        return (cTrack.name == new.name && cTrack.album.name == new.album.name)
    }

    private fun compareAlbumCover(current : ImageUri?, new : ImageUri) : Boolean = (current?.raw == new.raw)

    private fun isPlaying(isPaused : Boolean) = ((currentPauseState != isPaused) && !isPaused)

    private fun isRestartingSong(pos : Long, track: Track) : Boolean = (pos < 100 && compareTrack(currentTrack, track))

    private fun isPrevOrNext(pos : Long, track: Track) : Boolean = (pos < 100 && !compareTrack(currentTrack, track))

    private fun isSeekTo(playbackPosition : Long, localPlaybackPosition : Long) : Boolean {
        Log.d(TAG, "isSeekTo: $playbackPosition - $localPlaybackPosition = ${playbackPosition - localPlaybackPosition}")
        if (playbackPosition > localPlaybackPosition)
            return ((playbackPosition - localPlaybackPosition) > 2000)
        else
            return ((localPlaybackPosition - playbackPosition) > 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == MainActivity.REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    App.mRefreshStrategy.refresh(SpotifyClient::class.java)
                    PreferenceManager
                        .getDefaultSharedPreferences(applicationContext)
                        .edit()
                        .putString(SpotifyClient.SP_TOKEN, response.accessToken)
                        .apply()

                    App.mSpotifyClient.mAccessToken = response.accessToken
                }

                AuthenticationResponse.Type.ERROR -> {
                    Log.d(TAG, ": AuthenticationResponse.Type.ERROR")
                }

                else -> { }
            }
        }
    }
}