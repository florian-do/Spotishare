package do_f.com.spotishare.api.model

data class MyPlaylistsResponse(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)

data class Item(
    val album: Album,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String,
    val track_number: Int,
    val type: String,
    val uri: String,

    //MyPlaylists
    val collaborative: Boolean,
    val images: List<Image>,
    val owner: Owner,
    val primary_color: Any,
    val `public`: Boolean,
    val snapshot_id: String,
    val tracks: Tracks
)

data class Image(
    val height: Int,
    val url: String,
    val width: Int
)

data class ExternalUrls(
    val spotify: String
)

data class Owner(
    val display_name: String,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)