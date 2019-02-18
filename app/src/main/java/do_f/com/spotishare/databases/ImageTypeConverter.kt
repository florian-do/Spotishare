package do_f.com.spotishare.databases

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import do_f.com.spotishare.api.model.Image

class ImageTypeConverter {

    @TypeConverter
    fun stringToImage(data: String?): List<Image> {
        val gson = Gson()
        if (data == null)
            return emptyList()
        val listType = object : TypeToken<List<Image>>() {

        }.type
        return gson.fromJson<List<Image>>(data, listType)
    }

    @TypeConverter
    fun imageToString(someObjects: List<Image>): String {
        val gson = Gson()
        return gson.toJson(someObjects)
    }
}