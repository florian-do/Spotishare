package do_f.com.spotishare.databases.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import do_f.com.spotishare.databases.entities.Row

class RowTypeConverter {

    @TypeConverter
    fun stringToRow(data: String?): List<Row> {
        val gson = Gson()
        if (data == null)
            return emptyList()
        val listType = object : TypeToken<List<Row>>(){}.type
        return gson.fromJson<List<Row>>(data, listType)
    }

    @TypeConverter
    fun rowToString(someObjects: List<Row>): String {
        val gson = Gson()
        return gson.toJson(someObjects)
    }
}