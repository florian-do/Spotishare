package do_f.com.spotishare.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import do_f.com.spotishare.api.repository.AlbumsRepo
import do_f.com.spotishare.api.repository.PlaylistsRepo
import do_f.com.spotishare.databases.entities.Album
import do_f.com.spotishare.databases.entities.Playlist

class SongsViewModel : ViewModel() {
    val url : ObservableField<String> = ObservableField()
    val name : ObservableField<String> = ObservableField()

    val repo : PlaylistsRepo
    val repoAlbum : AlbumsRepo

    init {
        repo = PlaylistsRepo()
        repoAlbum = AlbumsRepo()
    }

    fun getPlaylistById(id : String) : LiveData<Playlist> = repo.getPlaylistById(id)
    fun refreshPlaylistById(id: String) = repo.refreshById(id)

    fun getAlbumById(id : String) : LiveData<Album> = repoAlbum.getAlbumById(id)
    fun refreshAlbumById(id : String, albumName : String) = repoAlbum.refreshById(id, albumName)
}