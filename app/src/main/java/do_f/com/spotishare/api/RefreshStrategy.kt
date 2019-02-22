package do_f.com.spotishare.api

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import do_f.com.spotishare.databases.entities.Album
import do_f.com.spotishare.databases.entities.Playlist
import do_f.com.spotishare.databases.entities.Playlists
import java.util.*

class RefreshStrategy(c: Context) {

    private val map: MutableMap<Class<*>, Long> = hashMapOf()
    private val forceRefresh: MutableMap<Class<*>, Boolean> = hashMapOf()
    private val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)

    companion object {
        private const val TAG = "RefreshStrategy"
    }

    init {
        map[SpotifyClient::class.java] = 60
        map[Playlists::class.java] = 60
        map[Playlist::class.java] = 60
        map[Album::class.java] = 60

        forceRefresh[Album::class.java] = false
        forceRefresh[Playlist::class.java] = false
    }

    fun shouldRefresh(className : Class<*>) : Boolean {
        val time = sp.getLong(className.simpleName, 0)
        val expire = Date(time + (map[className]!! * 60000))
        val now = Date()
        return expire.before(now)
    }

    fun forceRefresh(className : Class<*>) {
        forceRefresh[className] = true
    }

    fun shouldForceRefresh(className : Class<*>) : Boolean = forceRefresh[className]!!

    fun refresh(className : Class<*>) {
        sp.edit()
            .putLong(className.simpleName, Date().time)
            .apply()
        forceRefresh[className] = false
    }
}