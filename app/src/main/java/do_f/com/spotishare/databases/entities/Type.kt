package do_f.com.spotishare.databases.entities

enum class Type(val type : String) {
    PLAYLIST("playlist"),
    TRACK("track"),
    ARTIST("artist"),
    ALBUM("album")
}