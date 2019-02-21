package do_f.com.spotishare.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import do_f.com.spotishare.api.model.Item
import do_f.com.spotishare.api.model.SinglePlaylistResponse
import do_f.com.spotishare.api.repository.PlaylistsRepo

class SongsViewModel : ViewModel() {
    val url : ObservableField<String> = ObservableField()
    val name : ObservableField<String> = ObservableField()

    val repo : PlaylistsRepo

    init {
        repo = PlaylistsRepo()
    }

    fun getPlaylistById(id : String) : LiveData<SinglePlaylistResponse> {
        return repo.getPlaylistById(id)
    }
}