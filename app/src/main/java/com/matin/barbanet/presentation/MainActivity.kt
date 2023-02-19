package com.matin.barbanet.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
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
import androidx.core.content.FileProvider
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: DriverViewModel by viewModels()
    private lateinit var prefEditor: Editor
    private lateinit var userSharedPref: CommonSharedPref
    private val backgroundLocationPermissionRequestCode = 1002
    val fileChooserRequestCode = 1000
    var filePathCallback: ValueCallback<Array<Uri>>? = null
    var cameraPhotoPath: String? = null
    val requestCheckSettings = 5000

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
                            viewModel,
                            cameraPhotoPath
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
        ) { dialog, which -> }
        alertDialog.show()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            fileChooserRequestCode -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && filePathCallback != null) {
                    var results: Array<Uri>? = null

                    // check that the response is a good one
                    if (resultCode == RESULT_OK) {
                        if (data == null || data.data == null) {
                            // if there is not data, then we may have taken a photo
                            if (cameraPhotoPath != null) {
                                results = arrayOf(Uri.parse(cameraPhotoPath))
                            }
                        } else {
                            val dataString = data.dataString
                            if (dataString != null) {
                                results = arrayOf(Uri.parse(dataString))
                            }
                        }
                    }
                    filePathCallback?.onReceiveValue(results)
                    filePathCallback = null
                }
            }
//            requestCheckSettings -> {
//                if (resultCode == RESULT_OK) {
//                    if (locationServiceIntent != null)
//                        startService(locationServiceIntent)
//                } else {
//                    Log.d(tag, "requestCheckSettings denied")
//                }
//            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewPage(
    url: String,
    context: MainActivity,
    viewModel: DriverViewModel,
    cameraPhotoPathIn : String?,

) {
    var cameraPhotoPath = cameraPhotoPathIn
    val tag = "MainActivity"
    val fileChooserRequestCode = 1000
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
            webChromeClient = object : WebChromeClient() {

                @SuppressLint("QueryPermissionsNeeded")
                override fun onShowFileChooser(
                    webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    context.filePathCallback?.onReceiveValue(null)
                    context.filePathCallback = filePathCallback
                    var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent!!.resolveActivity(context.packageManager) != null) {

                        // create the file where the photo should go
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                            val putExtra = takePictureIntent.putExtra("PhotoPath", cameraPhotoPath)
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            Log.d(tag, "Unable to create Image File", ex)
                        }

                        // continue only if the file was successfully created
                        if (photoFile != null) {
                            cameraPhotoPath = "file:" + photoFile.absolutePath
                            val photoURI: Uri = FileProvider.getUriForFile(
                                context,
                                BuildConfig.APPLICATION_ID + Constants.FILE_PROVIDER_SUFFIX,
                                photoFile
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        } else {
                            takePictureIntent = null
                        }
                    }

                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = "image/*"
                    val intentArray: Array<Intent?> =
                        takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(
                        Intent.EXTRA_TITLE,
                        context.getString(R.string.choose_folder)
                    )
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    context.startActivityForResult(chooserIntent, fileChooserRequestCode)
                    return true
                }

                private fun createImageFile(): File? {
                    return try {
                        // Create an image file name
                        val datePattern = "yyyy-MM-dd_HH-mm-ss"
                        val timeStamp: String =
                            SimpleDateFormat(datePattern, Locale.US).format(Date())
                        val storageDir: File? =
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        File.createTempFile(
                            "Baarbaanet_image_JPEG_at_${timeStamp}_", /* prefix */
                            ".jpg",
                            storageDir
                        )
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        ex.printStackTrace()
                        Log.e(tag, "captureFromCamera: ${ex.message}", ex)
                        null
                    }

                }

            }
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}