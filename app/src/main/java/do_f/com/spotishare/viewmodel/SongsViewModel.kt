package do_f.com.spotishare.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import do_f.com.spotishare.api.repository.PlaylistsRepo
import do_f.com.spotishare.databases.entities.Playlist

class SongsViewModel : ViewModel() {
    val url : ObservableField<String> = ObservableField()
    val name : ObservableField<String> = ObservableField()

    val repo : PlaylistsRepo

    init {
        repo = PlaylistsRepo()
    }

    fun getPlaylistById(id : String) : LiveData<Playlist> = repo.getPlaylistById(id)
    fun refreshById(id: String) = repo.refreshById(id)
}