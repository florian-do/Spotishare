package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.api.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepo {
    val api : SearchService = App.retrofit.create(SearchService::class.java)

    fun search(q : String) : LiveData<SearchResponse> {
        var data : MutableLiveData<SearchResponse>? = MutableLiveData()

        api.search(q, 10).enqueue(object : Callback<SearchResponse> {
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                data = null
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                data?.value = response.body()
            }
        })

        return data!!
    }
}