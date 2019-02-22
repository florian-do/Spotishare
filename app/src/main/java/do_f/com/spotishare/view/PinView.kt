package do_f.com.spotishare.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import do_f.com.spotishare.R
import android.graphics.RectF
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem

class PinView : EditText {

    companion object {
        const val TAG = "PinView"
    }

    var mSpace : Float = 24F
    var mLineSpacing : Float = 8F
    var mCharSize = 0
    var mNumChars = 4
    var color = 0

    private val itemLineRect = RectF()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context, attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes) { init(context, attrs) }

    fun init(c: Context?, attrs: AttributeSet?) {
        setBackgroundResource(0)
        color = resources.getColor(R.color.green_spotify)
        val multi = c?.resources?.displayMetrics?.density
        mSpace = multi?.times(mSpace) ?: 24F
        mLineSpacing = multi?.times(mLineSpacing) ?: 8F
        Log.d(TAG, "init: ${mSpace}")

        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean = false
            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean = false
            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean = false
            override fun onDestroyActionMode(p0: ActionMode?) { }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)
        val availableWidth = width - paddingStart - paddingEnd

        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1))
        } else {
            mCharSize = ((availableWidth - (mSpace * (mNumChars - 1))) / mNumChars).toInt()
        }

        var startX : Float = paddingStart.toFloat()
        val bottom : Float = (height - paddingBottom).toFloat()
        val paint : Paint = getPaint()
        paint.color = color

        //Text
        val textLength = text.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(text, 0, textLength, textWidths)

        for (i in 0 until mNumChars) {
            canvas?.drawLine(startX, bottom, (startX + mCharSize), bottom, paint)

            if (text.length > i) {
                val middle = startX + (mCharSize / 2)
                canvas?.drawText(text, i, i + 1,
                    middle - textWidths[0] / 2,
                    bottom - mLineSpacing,
                    paint)
            }

            if (mSpace < 0) {
                startX += mCharSize * 2
            } else {
                startX += mCharSize + mSpace
            }
        }
    }
}