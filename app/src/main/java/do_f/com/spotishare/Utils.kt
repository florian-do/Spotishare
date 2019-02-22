package do_f.com.spotishare

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.graphics.Palette
import java.util.*
import kotlin.random.Random

class Utils {
    companion object {

        // https://gist.github.com/KKorvin/219555d4d3ee1828d7b0e808aad82930
        fun getDominantColor(bitmap: Bitmap): Int {
            val swatchesTemp = Palette.from(bitmap).generate().swatches
            val swatches = ArrayList(swatchesTemp)
            Collections.sort(swatches, object : Comparator<Palette.Swatch> {
                override fun compare(swatch1: Palette.Swatch, swatch2: Palette.Swatch): Int {
                    return swatch2.population - swatch1.population
                }
            })
            return if (swatches.size > 0) swatches[0].rgb else 0
        }

        fun manipulateColor(color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = Math.round(Color.red(color) * factor)
            val g = Math.round(Color.green(color) * factor)
            val b = Math.round(Color.blue(color) * factor)
            return Color.argb(
                a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255)
            )
        }

        fun generateRoomNumber() : String {
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
            return (1..4).map { i -> Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }
    }
}