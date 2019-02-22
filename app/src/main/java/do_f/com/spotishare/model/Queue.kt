package do_f.com.spotishare.model

import com.google.gson.annotations.SerializedName

data class Queue(
    @SerializedName("u")
    var uri: String = "",
    @SerializedName("s")
    var song: String = "",
    @SerializedName("a")
    var artist: String = "",
    @SerializedName("e")
    var explicit: Boolean = false
)