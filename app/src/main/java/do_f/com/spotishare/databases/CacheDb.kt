package do_f.com.spotishare.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import do_f.com.spotishare.databases.converters.RowTypeConverter
import do_f.com.spotishare.databases.entities.Playlist
import do_f.com.spotishare.databases.entities.Playlists

@Database(
    entities = [Playlists::class, Playlist::class],
    version = 5,
    exportSchema = false
)

@TypeConverters(ImageTypeConverter::class, RowTypeConverter::class)
abstract class CacheDb : RoomDatabase() {
    companion object {
        val DB_NAME = "cache.db"
    }
    abstract fun playlistsDao() : PlaylistsDao
}