package do_f.com.spotishare.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import do_f.com.spotishare.R
import do_f.com.spotishare.Utils
import do_f.com.spotishare.adapters.SongAdapter
import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.databinding.FragmentSongsBinding
import do_f.com.spotishare.viewmodel.SongsViewModel
import kotlinx.android.synthetic.main.fragment_songs.*

class SongsFragment : BFragment() {

    lateinit var vm : SongsViewModel
    lateinit var binding : FragmentSongsBinding

    override fun refreshSpotifyAppRemote() {

    }

    private lateinit var id: String
    private var type: String? = null
    private lateinit var url: String
    private var name: String? = null
    private var adapter = SongAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(ARG_TYPE)
            id = it.getString(ARG_ID)
            url = it.getString(ARG_URL)
            name = it.getString(ARG_NAME)
            Log.d(TAG, "${id}")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_songs,
            container,
            false)
        vm = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        binding.vm = vm
        binding.main.alpha = 0F
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.name.set(name)
        changeBackground(url)

        rvFeed.adapter = adapter
        rvFeed.setHasFixedSize(true)
        rvFeed.isNestedScrollingEnabled = false
        rvFeed.layoutManager = LinearLayoutManager(context)

        vm.getPlaylistById(id).observe(this, Observer {
            it?.let { data ->
                Log.d(TAG, "${data.items.size}")
                adapter.setData(data.items)
            }
        })
    }

    private fun changeBackground(url : String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?,
                                          target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?,
                                             target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    setBackground(resource!!)
                    dataSuccessfullyLoad(main)
                    return false
                }

            }).into(image_header)
    }

    private fun setBackground(bitmap: Bitmap) {
        val domColor = Utils.manipulateColor(Utils.getDominantColor(bitmap), 0.6F)
        val backgroundColor = resources.getColor(R.color.background)
        val colors = intArrayOf(domColor, backgroundColor)

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, colors
        )


        val transition = TransitionDrawable(arrayOf(collapsing_toolbar_layout.background, gd))
        transition.isCrossFadeEnabled = true
        collapsing_toolbar_layout.background = transition
        transition.startTransition(400)
    }

    companion object {
        const val ARG_TYPE = "arg_type"
        const val ARG_ID = "arg_id"
        const val ARG_URL = "arg_url"
        const val ARG_NAME = "arg_name"
    }
}
