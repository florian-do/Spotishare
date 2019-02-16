package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.MyPlaylistsResponse
import do_f.com.spotishare.api.service.PlaylistsService
import do_f.com.spotishare.databases.PlaylistsDao
import do_f.com.spotishare.databases.entities.Playlists
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistsRepo {

    companion object {
        val TAG = "PlaylistsRepo"
    }

    val api : PlaylistsService
    val dao : PlaylistsDao

    init {
        api = App.retrofit.create(PlaylistsService::class.java)
        dao = App.cacheDb.playlistsDao()
    }

    fun refresh() {
        AsyncTask.execute {
            Log.d(TAG, "dao == ${dao.count()} || refresh : ${App.mRefreshStrategy.shouldRefresh(this.javaClass)}")

            if (dao.count() == 0 || App.mRefreshStrategy.shouldRefresh(this.javaClass)) {
                val response : Response<MyPlaylistsResponse> = api.getMyPlaylists().execute()
                if (response.isSuccessful) {
                    Log.d(TAG, "success ${response.code()}")
                    dao.instert(response.body()?.items!!)

                    App.mRefreshStrategy.refresh(this.javaClass)
                } else {
                    Log.d(TAG, "Pas success ")
                }
            }
        }
    }

    fun getPlaylists() : LiveData<List<Playlists>> {
        return dao.getPlaylists()
    }
}