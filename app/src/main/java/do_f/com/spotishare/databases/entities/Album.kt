package do_f.com.spotishare.databases.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey
    val id : String,
    val songs : List<Row>
)