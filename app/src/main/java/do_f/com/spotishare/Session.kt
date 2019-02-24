package do_f.com.spotishare

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

class Session(c : Context) {

    private val ROOM_CODE = "sp_room_code"
    private val TYPE = "sp_type"
    private val sp : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)

    fun initSession(type: SESSIONTYPE, roomCode: String) {
        sp.edit().putString(ROOM_CODE, roomCode).apply()
        sp.edit().putString(TYPE, type.name).apply()
    }

    fun getDeviceType() : SESSIONTYPE {
        return getType(sp.getString(TYPE, "")!!)
    }

    fun getRoomCode() : String {
        return sp.getString(ROOM_CODE, "")!!
    }

    fun isConnected() : Boolean {
        Log.d("SESSION", "${sp.getString(TYPE, "")!!.isNotEmpty()} | ${getRoomCode().isNotEmpty()} | ${(sp.getString(TYPE, "")!!.isNotEmpty()) && (getRoomCode().isNotEmpty())}")
        return (sp.getString(TYPE, "")!!.isNotEmpty()) && (getRoomCode().isNotEmpty())
    }

    fun clear() {
        sp.edit().putString(ROOM_CODE, "").apply()
        sp.edit().putString(TYPE, "").apply()
    }

    fun getType(type : String) : SESSIONTYPE {
        return when (type) {
            SESSIONTYPE.MASTER.name -> SESSIONTYPE.MASTER
            SESSIONTYPE.SLAVE.name -> SESSIONTYPE.SLAVE
            else -> SESSIONTYPE.MASTER
        }
    }
}