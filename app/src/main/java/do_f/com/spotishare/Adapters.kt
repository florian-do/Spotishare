package do_f.com.spotishare

import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE


@BindingMethods(
    BindingMethod(
        type = View::class,
        attribute = "android:visibility",
        method = "setVisibility"
    )
)
class Adapters {

    companion object {

        @JvmStatic
        @BindingAdapter("android:visibility")
        fun View.setVisibility(b : Boolean) {
            when(b) {
                true -> visibility = VISIBLE
                false -> visibility = GONE
            }
        }
    }
}