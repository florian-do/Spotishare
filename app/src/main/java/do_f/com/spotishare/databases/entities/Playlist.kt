package do_f.com.spotishare.databases.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey
    val id : String,
    val songs : List<Row>
)

data class Row(
    val album_name: String,
    val artist_name: String,
    val song_name: String,
    val uri: String
)