package com.matin.barbanet.network.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matin.barbanet.R
import com.matin.barbanet.presentation.MainActivity
import com.matin.common.network.notification.SendFcmTokenOutput
import com.matin.common.utiles.CommonSharedPref
import java.io.IOException
import java.net.URL


class FirebaseNotificationService : FirebaseMessagingService() {
    private lateinit var userSharedPref: CommonSharedPref
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notification: RemoteMessage.Notification = remoteMessage.notification!!
        val data: Map<String, String> = remoteMessage.data
        println(" the data + ${data.keys}")
        if (notification != null) {
            println(" the notification is ; " + notification.body)
            showNotification(notification)
        }
    }

//    override fun handleIntent(intent: Intent?) {
//        println(" i am here ##### ")
//        super.handleIntent(intent)
//    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        userSharedPref = CommonSharedPref(this)
        println("Refreshed token: $p0")
        userSharedPref.tokenFcm = p0
        val fcmTokenOutput = SendFcmTokenOutput()
        fcmTokenOutput.setFcmToken(p0)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun showNotification(notification: RemoteMessage.Notification) {
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(notification.title)
                .setLargeIcon(icon)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.brb_notification_icon)

        if (notification.imageUrl != null && !notification.imageUrl.toString().isEmpty()) {
            val url: URL
            try {
                url = URL(notification.imageUrl.toString())
                val bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bigPicture)
                        .setSummaryText(notification.body)
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "description"
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}