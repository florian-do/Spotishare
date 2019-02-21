package do_f.com.spotishare.api.service

import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.api.model.MyPlaylistsResponse
import do_f.com.spotishare.api.model.SinglePlaylistResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaylistsService {

    @GET("me/playlists")
    fun getMyPlaylists() : Call<MyPlaylistsResponse>

    @GET("playlists/{playlist_id}/tracks?fields=items(track(name%2Curi%2Calbum.name%2Cartists(name)))")
    fun getPlaylistById(@Path("playlist_id") id : String) : Call<SinglePlaylistResponse>

}