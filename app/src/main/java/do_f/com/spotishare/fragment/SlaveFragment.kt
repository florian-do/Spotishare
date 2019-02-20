package do_f.com.spotishare.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.coroutines.*

import do_f.com.spotishare.R
import kotlinx.android.synthetic.main.fragment_slave.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.support.v7.widget.GridLayoutManager
import androidx.navigation.Navigation
import do_f.com.spotishare.Utils
import do_f.com.spotishare.adapters.PlaylistsAdapter
import do_f.com.spotishare.api.repository.PlaylistsRepo
import do_f.com.spotishare.base.BFragment

class SlaveFragment : BFragment() {

    private var mCountDownTimer : CountDownTimer? = null
    private val mHandler = Handler(Looper.getMainLooper())

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: ")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slave, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPlaylistFeed()
        initSpotifyData()

        search_bar.setOnClickListener {
            mHandler.removeCallbacksAndMessages(null)
            Navigation.findNavController(it).navigate(R.id.searchFragment)
        }
    }

    private fun initPlaylistFeed() {
        val adapter = PlaylistsAdapter(context!!)
        val repo = PlaylistsRepo()

        rvPlaylist.adapter = adapter
        rvPlaylist.layoutManager = GridLayoutManager(
            context, 2, GridLayoutManager.VERTICAL, false)
        rvPlaylist.setHasFixedSize(true)
        rvPlaylist.isNestedScrollingEnabled = false

        repo.getPlaylists().observe(this, Observer {
            Log.d(TAG, "trigger")
            if (it == null) {
                repo.refresh()
            } else {
                it.apply {
                    dataSuccessfullyLoad()
                    adapter.items = this
                    adapter.notifyDataSetChanged()
                }

                repo.refresh()
            }
        })
    }

    fun dataSuccessfullyLoad() {
        main.animate().alpha(1F).setDuration(600L).start()
    }

    override fun refreshSpotifyAppRemote() {
        initSpotifyData()
    }

    private fun initSpotifyData() {
        // Change background color from album image
        getSpotifyAppRemote().apply {
            playerApi.subscribeToPlayerState().setEventCallback {
                imagesApi.getImage(it.track.imageUri).setResultCallback {
                    val domColor = Utils.manipulateColor(Utils.getDominantColor(it), 0.8F)
                    val backgroundColor = resources.getColor(R.color.background)
                    val colors = intArrayOf(domColor, backgroundColor, backgroundColor)

                    val gd = GradientDrawable(
                        GradientDrawable.Orientation.TL_BR, colors
                    )

                    mHandler.post {
                        if (main != null) {
                            val transition = TransitionDrawable(arrayOf(main.background, gd))
                            transition.isCrossFadeEnabled = true
                            main.background = transition
                            transition.startTransition(1000)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mCountDownTimer != null)
            mCountDownTimer?.cancel()
    }

    override fun onStop() {
        super.onStop()
        if (mCountDownTimer != null)
            mCountDownTimer!!.cancel()

        mHandler.removeCallbacksAndMessages(null)
    }

    fun main() = GlobalScope.async {

        val job = async(start = CoroutineStart.LAZY) {
            val result = withTimeoutOrNull(13000L) {
                repeat(100) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
                "Done" // will get cancelled before it produces this result
            }
            println("Result is $result")
        }

        Log.d(TAG, ": ${job.isActive}")
        job.start()

//        job.cancelAndJoin()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val TAG = "SlaveFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}