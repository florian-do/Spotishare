package do_f.com.spotishare.model

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

data class Queue(
    var uri: String = "",
    var song: String = "",
    var artist: String = "",
    var explicit: Boolean = false,
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