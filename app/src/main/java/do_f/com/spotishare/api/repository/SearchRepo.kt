package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.api.service.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepo {
    val api : SearchService = App.retrofit.create(SearchService::class.java)

    companion object {
        private const val TAG = "SearchRepo"
    }

    fun search(q : String) : LiveData<SearchResponse> {
        var data : MutableLiveData<SearchResponse>? = MutableLiveData()

        api.search(q, 15).enqueue(object : Callback<SearchResponse> {
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.d(TAG, "error")
                data = null
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d(TAG, "${response.code()}")
                data?.value = response.body()
            }
        })

        return data!!
    }
}