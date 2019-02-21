package do_f.com.spotishare.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.navigation.Navigation

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import do_f.com.spotishare.R
import do_f.com.spotishare.Utils
import do_f.com.spotishare.adapters.SearchAdapter
import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.base.BFragment
import do_f.com.spotishare.databases.entities.Type
import do_f.com.spotishare.databinding.FragmentSearchBinding
import do_f.com.spotishare.view.MyEditText
import do_f.com.spotishare.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BFragment() {

    private var listener: OnFragmentInteractionListener? = null

    private val constraint1 = ConstraintSet()
    private val constraint2 = ConstraintSet()
    private var set = false
    private var items : MutableList<Item> = mutableListOf()

    private lateinit var searchViewModel : SearchViewModel
    private lateinit var adapter : SearchAdapter
    private lateinit var  binding : FragmentSearchBinding
    private val mHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "SearchFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        constraint1.clone(root)
        constraint2.clone(context, R.layout.fragment_search_alt)

        doAnimation()

        search_content.setOnClickListener {
            if (!set && items.isEmpty())
                doAnimation()
        }

        search_arrow.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        adapter = SearchAdapter(Glide.with(this.context!!), listener = { it: Item, view: View ->
            when (it.type) {
                Type.PLAYLIST.type -> {
                    val arg = Bundle()
                    arg.putString(SongsFragment.ARG_ID, it.id)
                    arg.putString(SongsFragment.ARG_TYPE, it.type)
                    arg.putString(SongsFragment.ARG_URL, it.images[0].url)
                    arg.putString(SongsFragment.ARG_NAME, it.name)
                    Navigation.findNavController(view).navigate(R.id.songsFragment, arg)
                }

                Type.ALBUM.type -> {
                    val arg = Bundle()
                    arg.putString(SongsFragment.ARG_ID, it.id)
                    arg.putString(SongsFragment.ARG_TYPE, it.type)
                    arg.putString(SongsFragment.ARG_URL, it.images[0].url)
                    arg.putString(SongsFragment.ARG_NAME, it.name)
                    Navigation.findNavController(view).navigate(R.id.songsFragment, arg)
                }
            }
        })

        rvFeed.layoutManager = LinearLayoutManager(context)
        rvFeed.setHasFixedSize(true)
        rvFeed.isNestedScrollingEnabled = false
        rvFeed.adapter = adapter

        search_field.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, action: Int, p2: KeyEvent?): Boolean {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    callApi()
                    return true
                }
                return false
            }
        })

        search_field.setKeyBackListener(object : MyEditText.OnKeyImeListener {
            override fun onKeyIme(keyCode: Int, event: KeyEvent?) {
                if (set && event?.action == KeyEvent.ACTION_DOWN && search_field.text.isEmpty()) {
                    doAnimation()
                }
            }
        })
    }

    private fun callApi() {
        hideKeyboard()
        binding.setLoading(true)
        binding.isContent = true
        searchViewModel.search(search_field.text.toString())
            .observe(this, Observer {
                if (it != null) {
                    items.clear()
                    buildSearchQuery(it)
                    Log.d(TAG, "observe ${items.size}")
                    binding.isContent = items.isNotEmpty()
                    binding.setLoading(false)
                    adapter.refreshData(items)
                }
        })
    }

    private fun buildSearchQuery(data : SearchResponse) {
        if (data.artists.total > 0) {
            items.add(data.artists.items[0])
            changeBackground(data.artists.items[0].images[0].url)
        }

        data.albums.let {
            if (it.items.isNotEmpty()) {
                if (it.items.size > 1)
                    for (i in 0..1) { items.add(it.items[i]) }
                else
                    items.add(it.items[0])
            }
        }

        data.playlists.let {
            if (it.items.isNotEmpty()) {
                if (it.items.size > 1)
                    for (i in 0..1) { items.add(it.items[i]) }
                else
                    items.add(it.items[0])
            }
        }

        if (data.tracks.total > 0)
            items.addAll(data.tracks.items)
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
                    return false
                }

            }).submit()
    }

    private fun setBackground(bitmap: Bitmap) {
        val domColor = Utils.manipulateColor(Utils.getDominantColor(bitmap), 0.6F)
        val backgroundColor = resources.getColor(R.color.background)
        val colors = intArrayOf(domColor, backgroundColor, backgroundColor)

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TL_BR, colors
        )

        val transition = TransitionDrawable(arrayOf(rvFeed.background, gd))
        transition.isCrossFadeEnabled = true
        rvFeed.background = transition
        transition.startTransition(1000)
    }

    private fun doAnimation() {
        Log.d(TAG, "doAnimation: ${set}")
        when (set) {
            true -> {
                updateConstraints(R.layout.fragment_search)
                search_label.alpha = 1F
                search_arrow.alpha = 0F
                search_field.visibility = GONE
                search_content.background = resources.getDrawable(R.drawable.drawable_background_first_search_bar)
            }
            // Extend
            false -> {
                updateConstraints(R.layout.fragment_search_alt)
                search_label.alpha = 0F
                search_arrow.alpha = 1F
                search_field.visibility = VISIBLE
                search_content.background = resources.getDrawable(R.drawable.drawable_background_search_bar_alt)
                search_field.requestFocus()
                val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }

        set = !set
    }

    fun updateConstraints(@LayoutRes id: Int) {
        val newConstraintSet = ConstraintSet()
        newConstraintSet.clone(context, id)
        newConstraintSet.applyTo(root)
        val transition = ChangeBounds()
        transition.interpolator = OvershootInterpolator()
        TransitionManager.beginDelayedTransition(root, transition)
    }

    private fun hideKeyboard() {
        activity?.let {
            val imm : InputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        if (set) set = false
        if (search_field.text.isNotEmpty())
            callApi()
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

    override fun refreshSpotifyAppRemote() {

    }
}
