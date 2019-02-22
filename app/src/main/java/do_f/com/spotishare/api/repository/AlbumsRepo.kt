package do_f.com.spotishare.api.repository

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import do_f.com.spotishare.App
import do_f.com.spotishare.api.service.AlbumsService
import do_f.com.spotishare.databases.AlbumsDao
import do_f.com.spotishare.databases.entities.Album
import do_f.com.spotishare.databases.entities.Row

class AlbumsRepo {

    val api : AlbumsService = App.retrofit.create(AlbumsService::class.java)
    val dao : AlbumsDao = App.cacheDb.albumsDao()

    fun refreshById(id : String, albumName : String) {
        AsyncTask.execute {
            if (dao.isAlbumInDb(id) == 0
                || App.mRefreshStrategy.shouldRefresh(Album::class.java)
                || App.mRefreshStrategy.shouldForceRefresh(Album::class.java)) {
                val response = api.getAlbumById(id).execute()
                if (response.isSuccessful) {
                    val items : MutableList<Row> = mutableListOf()
                    response.body()?.items?.forEach {
                        items.add(Row(albumName, it.artists[0].name, it.name, it.uri, it.explicit))
                    }

                    dao.insert(Album(id, items))
                    App.mRefreshStrategy.refresh(Album::class.java)
                }
            }
        }
    }

    fun getAlbumById(id : String) : LiveData<Album> = dao.getAlbumById(id)
}