package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.MyPlaylistsResponse
import do_f.com.spotishare.api.model.SinglePlaylistResponse
import do_f.com.spotishare.api.service.PlaylistsService
import do_f.com.spotishare.databases.PlaylistsDao
import do_f.com.spotishare.databases.entities.Playlist
import do_f.com.spotishare.databases.entities.Playlists
import do_f.com.spotishare.databases.entities.Row
import retrofit2.Response
import com.google.gson.Gson
import java.net.URLEncoder

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
            if (dao.count() == 0 || App.mRefreshStrategy.shouldRefresh(Playlists::class.java)) {
                val response : Response<MyPlaylistsResponse> = api.getMyPlaylists().execute()
                if (response.isSuccessful) {
                    Log.d(TAG, "success ${response.code()}")

                    dao.instert(response.body()?.items!!)
                    App.mRefreshStrategy.refresh(Playlists::class.java)
                } else {
                    Log.d(TAG, "Pas success ")
                }
            }
        }
    }

    fun getPlaylists() : LiveData<List<Playlists>> {
        return dao.getPlaylists()
    }

    fun refreshById(id : String) {
        AsyncTask.execute {
            if (dao.isPlaylistInDb(id) == 0
                || App.mRefreshStrategy.shouldRefresh(Playlist::class.java)
                || App.mRefreshStrategy.shouldForceRefresh(Playlist::class.java)) {
                val field = URLEncoder.encode("items(id,track(name,uri,album.name,artists(name)))")
                val response : Response<SinglePlaylistResponse> = api.getPlaylistById(id, field).execute()
                Log.d(TAG, Gson().toJson(response))
                if (response.isSuccessful) {
                    Log.d(TAG, "success ${response.code()}")
                    val items : MutableList<Row> = mutableListOf()
                    response.body()?.items?.forEach {
                        items.add(Row(
                            it.track.album.name,
                            it.track.artists[0].name,
                            it.track.name,
                            it.track.uri,
                            it.track.explicit
                        ))
                    }

                    dao.instertById(Playlist(id, items))
                    App.mRefreshStrategy.refresh(Playlist::class.java)
                } else {
                    Log.d(TAG, "Pas success ")
                }
            }
        }
    }

    fun getPlaylistById(id : String) : LiveData<Playlist> = dao.getPlaylistById(id)
}