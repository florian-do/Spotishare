package do_f.com.spotishare.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.coroutines.*

import do_f.com.spotishare.R
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.support.v7.widget.GridLayoutManager
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import do_f.com.spotishare.MainActivity
import do_f.com.spotishare.Utils
import do_f.com.spotishare.adapters.PlaylistsAdapter
import do_f.com.spotishare.api.repository.PlaylistsRepo
import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.databases.entities.Playlists
import do_f.com.spotishare.databinding.FragmentDiscoverBinding

class DiscoverFragment : BFragment() {

    private val mHandler = Handler(Looper.getMainLooper())

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var gd : GradientDrawable? = null
    private var backgroundColor : Int = 0

    private lateinit var adapter : PlaylistsAdapter
    private lateinit var binding : FragmentDiscoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_discover,
            container,
            false
        )

        backgroundColor = resources.getColor(R.color.background)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPlaylistFeed()

        search_bar.setOnClickListener {
            mHandler.removeCallbacksAndMessages(null)
            Navigation.findNavController(it).navigate(R.id.searchFragment)
        }
    }

    private fun initPlaylistFeed() {
        binding.loading = true
        adapter = PlaylistsAdapter(Glide.with(this), listener = { it: Playlists, view: View ->
            val arg = Bundle()
            arg.putString(SongsFragment.ARG_ID, it.id)
            arg.putString(SongsFragment.ARG_TYPE, it.type)
            arg.putString(SongsFragment.ARG_URL, it.images[0].url)
            arg.putString(SongsFragment.ARG_NAME, it.name)
            Navigation.findNavController(view).navigate(R.id.songsFragment, arg)
        })

        val repo = PlaylistsRepo()

        rvPlaylist.adapter = adapter
        rvPlaylist.layoutManager = GridLayoutManager(
            context, 2, GridLayoutManager.VERTICAL, false)
        rvPlaylist.isNestedScrollingEnabled = false

        repo.getPlaylists().observe(this, Observer {
            if (it == null) {
                repo.refresh()
            } else {
                it.apply {
                    adapter.items = this
                    adapter.notifyDataSetChanged()
                    binding.loading = false
                    getMainActivity()?.getCurrentAlbumBitmap()?.let {
                        changeBackground(it)
                    }
                }

                repo.refresh()
            }
        })
    }

    override fun refreshSpotifyAppRemote() {

    }

    fun changeBackground(bitmap : Bitmap) {
        val domColor = Utils.manipulateColor(Utils.getDominantColor(bitmap), 0.8F)
        val colors = intArrayOf(domColor, backgroundColor, backgroundColor)

        gd = GradientDrawable(
            GradientDrawable.Orientation.TL_BR, colors
        )

        main.background = gd
    }

    fun updateBackground(bitmap : Bitmap) {
        AsyncTask.execute {
            val domColor = Utils.manipulateColor(Utils.getDominantColor(bitmap), 0.8F)
            val colors = intArrayOf(domColor, backgroundColor, backgroundColor)

            gd = GradientDrawable(
                GradientDrawable.Orientation.TL_BR, colors
            )

            mHandler.post {
                if (main != null) {
                    val transition = TransitionDrawable(arrayOf(main.background, gd))
                    transition.isCrossFadeEnabled = true
                    main.background = transition
                    transition.startTransition(500)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
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
        val TAG = "DiscoverFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}