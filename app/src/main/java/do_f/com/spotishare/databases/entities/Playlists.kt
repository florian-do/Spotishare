package do_f.com.spotishare.databases.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import do_f.com.spotishare.api.model.Image

@Entity
data class Playlists(
    @PrimaryKey
    var id: String,
    var collaborative: Boolean,
    var href: String,
    var images: List<Image>,
    var name: String,
    var public: Boolean,
    var snapshot_id: String,
    var type: String,
    var uri: String
) {
    constructor() : this("", false, "", emptyList()
        , "", false, "", "", "")
}