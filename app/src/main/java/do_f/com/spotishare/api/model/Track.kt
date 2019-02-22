package do_f.com.spotishare.api.model

data class Track(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int,
    val album: Album,
    val artists: List<Artist>,
    val name: String,
    val uri: String,
    val explicit: Boolean
)