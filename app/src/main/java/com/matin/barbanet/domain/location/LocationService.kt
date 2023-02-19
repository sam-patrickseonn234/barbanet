package com.matin.barbanet.domain.location

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.matin.barbanet.R
import com.matin.barbanet.domain.useCase.SendLocationUseCase
import com.matin.barbanet.utiles.Constants
import com.matin.common.dto.LocationRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var sendLocationUseCase: SendLocationUseCase
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start(intent.getLongExtra("interval", 15000L))
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    private fun start(interval: Long) {
        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("baarbaanet")
            .setContentText("آماده سرویس دهی")
            .setSmallIcon(R.drawable.brb_notification_icon)
            .setOngoing(true)
        startForeground(1, notification.build())
 //       val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val orderId = if (sharedPreferences.getInt("currentOrderId", -1) == -1) {
            null
        } else
            sharedPreferences.getInt("currentOrderId", -1)


        locationClient
            .getLastLocation(interval)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val locationRequest = LocationRequest(
                    accuracy = location.accuracy,
                    bearing = location.bearing,
                    dateTime = null,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    night = null,
                    orderId = orderId,
                    speed = location.speed,
                    weather = null
                )
                sendLocationUseCase(locationRequest).launchIn(scope)
//                val updateNotification =
//                    notification.setContentText(" آماده سرویس دهی : (${location.latitude} , ${location.longitude} )")
//                notificationManager.notify(1, updateNotification.build())
            }
            .launchIn(scope)

//        startForeground(1, notification.build())
    }

    @Suppress("DEPRECATION")
    private fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else
            stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }


}