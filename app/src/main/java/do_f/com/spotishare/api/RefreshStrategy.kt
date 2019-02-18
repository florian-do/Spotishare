package do_f.com.spotishare.api

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import do_f.com.spotishare.api.repository.PlaylistsRepo
import java.util.*

class RefreshStrategy(c: Context) {

    private val map: MutableMap<Class<*>, Long>
    private val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)

    companion object {
        private const val TAG = "RefreshStrategy"
    }

    init {
        map = hashMapOf()
        map[SpotifyClient::class.java] = 60
        map[PlaylistsRepo::class.java] = 60
    }

    fun shouldRefresh(className : Class<*>) : Boolean {
        val time = sp.getLong(className.simpleName, 0)
        val expire = Date(time + (map[className]!! * 60000))
        val now = Date()
        return expire.before(now)
    }

    fun refresh(className : Class<*>) {
        sp.edit()
            .putLong(className.simpleName, Date().time)
            .apply()
    }
}