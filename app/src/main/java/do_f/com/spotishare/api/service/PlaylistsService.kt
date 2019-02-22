package do_f.com.spotishare.api.service

import do_f.com.spotishare.api.model.MyPlaylistsResponse
import do_f.com.spotishare.api.model.SinglePlaylistResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistsService {

    @GET("me/playlists")
    fun getMyPlaylists() : Call<MyPlaylistsResponse>

    @GET("playlists/{playlist_id}/tracks")
    fun getPlaylistById(@Path("playlist_id") id : String, @Query("fields") field : String) : Call<SinglePlaylistResponse>

}