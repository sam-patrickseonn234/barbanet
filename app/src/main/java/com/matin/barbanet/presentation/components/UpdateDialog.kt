package com.matin.barbanet.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matin.barbanet.BuildConfig
import com.matin.barbanet.R
import com.matin.barbanet.domain.model.AppFeaturesResponse
import com.matin.barbanet.domain.model.UpdateAppResponse
import com.matin.barbanet.presentation.ui.theme.GreenUpdate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateDialog(
    appUpdate: UpdateAppResponse,
    appFeatures: List<AppFeaturesResponse>,
    context: Context
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        Image(
            painter = painterResource(id = R.drawable.brb_logo_text_box_png),
            contentDescription = "static logo picture",
            modifier = Modifier
                .padding(top = 32.dp, bottom = 16.dp)
                .fillMaxWidth()
                .height(34.dp),
            alignment = Alignment.Center
        )
        Text(
            text = "لیست تغییرات",
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    end = 32.dp,
                    start = 32.dp
                )
                .height(32.dp)
                .align(Alignment.CenterHorizontally),
            fontSize = 18.sp,
        )
        Divider()
        Spacer(modifier = Modifier.height(8.dp))
        val featureList = mutableListOf<String>()
        featureList.add("برای ادامه استفاده از اپلیکیشن، لازم است که نسخه جدید را نصب کنید")
        appFeatures.forEach { it.features?.let { it -> featureList.addAll(it) } }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(featureList) { feature ->
                Text(
                    text = feature,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                onClick = { onAccept(appUpdate, context) },
                content = {
                    Text(
                        text = "بروز رسانی",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(GreenUpdate)
            )

            TextButton(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                onClick = {},
                content = {
                    Text(
                        text = "بستن",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Color.Gray)
            )
        }
    }
}

fun onAccept(
    appUpdate: UpdateAppResponse,
    context: Context
) {
    val intent = Intent(Intent.ACTION_VIEW)
    var url = ""

    url = appUpdate.url!!

    val buildType = BuildConfig.MARKET

    appUpdate.markets.forEach { it ->
        if (buildType == it.market.name) {
            url = it.url
        }
    }

    intent.data = Uri.parse(url)
    val chooser = Intent.createChooser(intent, "به روز رسانی")
    context.startActivity(chooser)
}

//@Preview
//@Composable
//fun mainView() {
//    UpdateDialog()
//}