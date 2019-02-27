package do_f.com.spotishare.model

import android.graphics.Bitmap
import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

data class Queue(
    var uri: String = "",
    var song: String = "",
    var artist: String = "",
    var explicit: Boolean = false,
    @Exclude
    var itemViewType: Int = 0,
    @Exclude
    var bmp: Bitmap? = null,
    @Exclude
    var title: String = "",
    @Exclude
    var selection: Boolean = false,
    @Exclude
    var key: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uri" to uri,
            "song" to song,
            "artist" to artist,
            "explicit" to explicit
        )
    }
}