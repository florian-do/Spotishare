package do_f.com.spotishare.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText

class MyEditText : EditText {

    companion object {
        private const val TAG = "MyEditText"
    }

    var mListener : OnKeyImeListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (mListener != null)
            mListener?.onKeyIme(keyCode, event)
        return super.onKeyPreIme(keyCode, event)
    }

    fun setKeyBackListener(listener : OnKeyImeListener) {
        mListener = listener
    }

    interface OnKeyImeListener {
        fun onKeyIme(keyCode: Int, event: KeyEvent?)
    }
}