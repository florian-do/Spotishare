package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.MyPlaylistsResponse
import do_f.com.spotishare.api.service.Playlists
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistsRepository {

    companion object {
        val TAG = "PlaylistsRepository"
    }

    val api : Playlists

    init {
        api = App.retrofit.create(Playlists::class.java)
    }

    fun getPlaylists() : LiveData<MyPlaylistsResponse> {
        var data : MutableLiveData<MyPlaylistsResponse>? = MutableLiveData()

        api.getMyPlaylists().enqueue(object : Callback<MyPlaylistsResponse> {
            override fun onFailure(call: Call<MyPlaylistsResponse>, t: Throwable) {
                Log.e(TAG, "error : ", t)
                data = null
            }

            override fun onResponse(call: Call<MyPlaylistsResponse>, response: Response<MyPlaylistsResponse>) {
                Log.d(TAG, "success ${response.code()}")
                data?.value = response.body()
            }
        })

        return data!!
    }
}