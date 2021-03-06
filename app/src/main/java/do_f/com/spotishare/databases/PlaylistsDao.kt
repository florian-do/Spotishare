package do_f.com.spotishare.databases

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import do_f.com.spotishare.databases.entities.Playlist
import do_f.com.spotishare.databases.entities.Playlists

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun instert(items : List<Playlists>)

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): LiveData<List<Playlists>>

    @Query("SELECT COUNT(*) FROM playlists")
    fun count(): Int

    @Query("SELECT * FROM playlist WHERE id=:id")
    fun getPlaylistById(id: String) : LiveData<Playlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun instertById(item : Playlist)

    @Query("SELECT COUNT(*) FROM playlist WHERE id=:id")
    fun isPlaylistInDb(id: String) : Int
}