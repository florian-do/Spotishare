package do_f.com.spotishare.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
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
import do_f.com.spotishare.R
import do_f.com.spotishare.view.MyEditText
import do_f.com.spotishare.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Callback

class SearchFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private val constraint1 = ConstraintSet()
    private val constraint2 = ConstraintSet()
    private var set = false
    private lateinit var searchViewModel : SearchViewModel

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "SearchFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        constraint1.clone(root)
        constraint2.clone(context, R.layout.fragment_search_alt)

        doAnimation()

        search_content.setOnClickListener {
            doAnimation()
        }

        search_arrow.setOnClickListener {
            fragmentManager?.popBackStack()
        }


        search_field.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, action: Int, p2: KeyEvent?): Boolean {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    searchViewModel.search(search_field.text.toString()).observe(this@SearchFragment, Observer {

                    })
                    return true
                }
                return false
            }
        })

        search_field.setKeyBackListener(object : MyEditText.OnKeyImeListener {
            override fun onKeyIme(keyCode: Int, event: KeyEvent?) {
                if (set && event?.action == KeyEvent.ACTION_DOWN) {
                    doAnimation()
                }
            }
        })
    }

    private fun doAnimation() {
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

    override fun onStop() {
        super.onStop()
        val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
