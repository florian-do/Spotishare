package do_f.com.spotishare.api.model

import do_f.com.spotishare.databases.entities.Playlists

data class MyPlaylistsResponse(
    val href: String,
    val items: List<Playlists>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
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