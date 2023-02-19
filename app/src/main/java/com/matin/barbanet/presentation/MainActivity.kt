package com.matin.barbanet.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.matin.barbanet.BuildConfig
import com.matin.barbanet.R
import com.matin.barbanet.domain.location.LocationService
import com.matin.barbanet.presentation.components.InternetClosedDialog
import com.matin.barbanet.presentation.components.LoadingScreen
import com.matin.barbanet.presentation.components.UpdateDialog
import com.matin.barbanet.presentation.components.WebViewInterface
import com.matin.barbanet.presentation.viewModel.DriverViewModel
import com.matin.barbanet.utiles.Constants
import com.matin.barbanet.utiles.isNetworkAvailable
import com.matin.common.ui.showPermissionDialog
import com.matin.common.utiles.CommonSharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: DriverViewModel by viewModels()
    private lateinit var prefEditor: Editor
    private lateinit var userSharedPref: CommonSharedPref
    private val backgroundLocationPermissionRequestCode = 1002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ), 0
        )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_DENIED
        ) {
            checkBackgroundLocation()
        }
        initFirebase()
        userSharedPref = CommonSharedPref(this@MainActivity)
        prefEditor =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)!!.edit()
        setContent {
//            Intent(applicationContext, LocationService::class.java).apply {
//                action = LocationService.ACTION_START
//                startService(this)
//            }
            checkLocationEnabledDialog()
            if (!isNetworkAvailable(this@MainActivity)) {
                InternetClosedDialog(R.drawable.no_internet,
                    title = stringResource(id = R.string.network_error),
                    desc = stringResource(id = R.string.network_error_body),
                    onDismiss = { this@MainActivity.recreate() })
            } else {
                val appFeature = viewModel.featureState.value
                val updateUrl = viewModel.versionState.value

                if (appFeature.isLoading)
                    LoadingScreen(loading = true)

                if (updateUrl.appVersion != null) {
                    if (updateUrl.error != "") {
                        InternetClosedDialog(R.drawable.error_404,
                            title = stringResource(id = R.string.service_error),
                            desc = stringResource(id = R.string.service_error_body),
                            onDismiss = { this@MainActivity.recreate() })
                    }
                    viewModel.getWebView(application.packageName)
                    val webUrl = viewModel.webViewState.value
                    if (webUrl.webView != null) {
                        println(" the url is : ${webUrl.webView.url}")
                        WebViewPage(
                            url = webUrl.webView?.url!! + Constants.WEB_VIEW_ATTACHMENT,
                            this@MainActivity,
                            viewModel
                        )
                    }
                    if (!appFeature.isLoading) {
                        if (BuildConfig.VERSION_CODE < appFeature.appFeatures[0].code!!) {
                            UpdateDialog(
                                updateUrl.appVersion,
                                appFeatures = appFeature.appFeatures,
                                this@MainActivity
                            )
                        }
                    }
                }
            }
        }
        val currentOrder = viewModel.currentOrder.value
        if (!currentOrder.isLoading) {
            if (userSharedPref.token != null && currentOrder.currentOrder != null)
                prefEditor.putInt("currentOrderId", currentOrder.currentOrder?.shipmentId!!)
                    .commit()
            else
                prefEditor.putInt("currentOrderId", -1).commit()
        }
    }

    private fun checkBackgroundLocation() {
        val message =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getString(
                    R.string.alert_background_permission_dialog,
                    packageManager.backgroundPermissionOptionLabel.toString()
                )
            } else {
                R.string.simple_alert_backgrounf_permission_dialog
            }
        println(" the message is $message")
        this@MainActivity.showPermissionDialog(
            message as String,
            { getString(R.string.background_location_permission) },
            backgroundLocationPermissionRequestCode,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            null
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ), 0
        )
    }

    private fun subscribeToFirebaseTopic(topic: String) {

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var message = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun initFirebase() {
        subscribeToFirebaseTopic("general")
    }
    private fun checkLocationEnabledDialog() {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            askEnableLocation()
        }
    }
    private fun askEnableLocation() {
        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("اخطار")
        alertDialog.setMessage("مکان نما خاموش است. روشنش کنیم؟")
        alertDialog.setPositiveButton(
            "بله"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this@MainActivity.startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "نه خیر"
        ) { dialog, which ->  }
        alertDialog.show()
    }
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewPage(
    url: String,
    context: MainActivity,
    viewModel: DriverViewModel
) {
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebViewInterface(context, viewModel), "app")
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true

            overScrollMode = WebView.OVER_SCROLL_NEVER

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // cancel loading
                }
            }

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}