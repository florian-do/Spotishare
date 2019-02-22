package do_f.com.spotishare.databases

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import do_f.com.spotishare.databases.entities.Album

@Dao
interface AlbumsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(album: Album)

    @Query("SELECT * FROM album WHERE id=:id")
    fun getAlbumById(id : String) : LiveData<Album>

    @Query("SELECT COUNT(*) FROM album WHERE id=:id")
    fun isAlbumInDb(id: String) : Int
}