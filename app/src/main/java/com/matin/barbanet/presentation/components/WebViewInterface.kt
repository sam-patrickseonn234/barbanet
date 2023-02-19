package com.matin.barbanet.presentation.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.matin.barbanet.R
import com.matin.barbanet.domain.location.LocationClient
import com.matin.barbanet.domain.location.LocationService
import com.matin.barbanet.presentation.viewModel.DriverViewModel
import com.matin.barbanet.utiles.Constants
import com.matin.common.network.notification.SendFcmTokenOutput
import com.matin.common.utiles.CommonSharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class WebViewInterface(
    private val context: Context,
    private val viewModel: DriverViewModel
) {
    private val userSharedPref = CommonSharedPref(context)
    private val tag = "Compose Activity"

    @Inject
    lateinit var locationClient: LocationClient
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    @JavascriptInterface
    fun login(
        accessToken: String,
        clientType: String,
        clientSerialNumber: String,
        clientVersion: String
    ) {
        userSharedPref.token = accessToken
        userSharedPref.cty = clientType
        userSharedPref.csn = clientSerialNumber
        userSharedPref.cvs = clientVersion

//                viewModel.requestCurrentOrder()
        Log.d(tag, "login")

        Log.d(tag, "firebase in startup")
        val fcmToken = SendFcmTokenOutput()
        fcmToken.setFcmToken(userSharedPref.tokenFcm)
        viewModel.getNotification(
            fcmToken
        )
    }

    @JavascriptInterface
    fun refreshToken(accessToken: String) {
        userSharedPref.token = accessToken

//                viewModel.requestCurrentOrder()

        Log.d(tag, "refreshToken")

        val fcmToken = SendFcmTokenOutput()
        fcmToken.setFcmToken(userSharedPref.tokenFcm)
        viewModel.getNotification(
            fcmToken
        )
    }

    @JavascriptInterface
    fun updateCurrentOrder() {
        viewModel.getCurrentOrder()

        Log.d(tag, "updateCurrentOrder")
    }

    @JavascriptInterface
    fun getCurrentLocation(): String? {
//        val currentLocation: Location? =
        val obj: JSONObject = JSONObject()
        locationClient
            .getLastLocation(Constants.LOCATION_UPDATE_INTERVAL).onEach {
//                    val obj: JSONObject = JSONObject()
                obj.put("latitude", it.latitude)
                obj.put("longitude", it.longitude)
                obj.put("speed", it.speed)
                obj.put("accuracy", it.accuracy)
                obj.put("bearing", it.bearing)
                Log.d(tag, obj.toString())
            }.launchIn(scope)
      //  println(" the json is : $obj")
        return obj.toString()
    }

    @JavascriptInterface
    fun startService(interval: Long, distance: Float) {
        Log.d(tag, "startService() called")
        Intent(context.applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            putExtra("interval", interval)
            context.startService(this)
            
        }
    }

    @JavascriptInterface
    fun endService() {
        Intent(context.applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context.stopService(this)
        }
        Log.d(tag, "endService")
    }

    @JavascriptInterface
    fun openMap(lat: Double, lng: Double) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng"))

        try {
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.choose_map_provider)
                )
            )
        } catch (e: ActivityNotFoundException) {
            // TODO: handle this situation
            Toast.makeText(
                context,
                "No Map Application Found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @JavascriptInterface
    fun openDialPad(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    @JavascriptInterface
    fun openExternalLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.choose_browser)
            )
        )
    }

    @JavascriptInterface
    fun openExternalLink(url: String, postParams: String) {
        val script = "javascript:" +
                "var to = '" + url + "';" +
                "var p = " + postParams + ";" +
                "var myForm = document.createElement('form');" +
                "myForm.method='post' ;" +
                "myForm.action = to;" +
                "for (var k in p) {" +
                "var myInput = document.createElement('input') ;" +
                "myInput.setAttribute('type', 'text');" +
                "myInput.setAttribute('name', k) ;" +
                "myInput.setAttribute('value', p[k]);" +
                "myForm.appendChild(myInput) ;" +
                "}" +
                "document.body.appendChild(myForm) ;" +
                "myForm.submit() ;" +
                "document.body.removeChild(myForm) ;"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(script))
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.choose_browser)
            )
        )
    }


}