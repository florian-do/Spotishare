package do_f.com.spotishare.api.service

import do_f.com.spotishare.api.model.SingleAlbumReponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumsService {
    @GET("albums/{id}/tracks")
    fun getAlbumById(@Path("id") id: String) : Call<SingleAlbumReponse>
}