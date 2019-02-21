package do_f.com.spotishare.api.service

import do_f.com.spotishare.api.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search?type=track%2Cartist%2Cplaylist%2Calbum")
    fun search(@Query("q") q : String, @Query("limit") limit : Int) : Call<SearchResponse>
}