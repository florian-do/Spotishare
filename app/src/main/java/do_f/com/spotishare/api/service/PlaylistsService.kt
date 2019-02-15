package do_f.com.spotishare.api.service

import do_f.com.spotishare.api.model.MyPlaylistsResponse
import retrofit2.Call
import retrofit2.http.GET

interface PlaylistsService {

    @GET("me/playlists")
    fun getMyPlaylists() : Call<MyPlaylistsResponse>
}