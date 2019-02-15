package do_f.com.spotishare.service

import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import android.content.Intent



class FCMService : FirebaseMessagingService() {

    private val TAG = "FCMService";
    private lateinit var mLocalBroadcastManager : LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


        val intent = Intent("MyData")
        intent.putExtra("phone", remoteMessage?.data?.get("DriverPhone"))
        mLocalBroadcastManager.sendBroadcast(intent)
        Log.d(TAG, "onMessageReceived ${remoteMessage?.data?.get("title")}")
        Log.d(TAG, ": ${remoteMessage?.notification?.body}")
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
        Log.d(TAG, "onMessageSent $p0")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "onDeletedMessages")
    }

    override fun onSendError(p0: String?, p1: Exception?) {
        super.onSendError(p0, p1)
        Log.e(TAG, "onSendError $p0", p1)
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken $p0")
    }
}
