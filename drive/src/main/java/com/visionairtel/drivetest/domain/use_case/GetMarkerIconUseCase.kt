package com.visionairtel.drivetest.domain.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.visionairtel.drivetest.R
import com.visionairtel.drivetest.util.Util
import com.visionairtel.drivetest.util.Util.NetworkTypeEnum.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetMarkerIconUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    private val blueMarker: BitmapDescriptor? = null
    private val redMarker: BitmapDescriptor? = null
    private val yellowMarker: BitmapDescriptor? = null
    private val purpleMarker: BitmapDescriptor? = null

    operator fun invoke(networkTypeEnum: Util.NetworkTypeEnum): BitmapDescriptor {
        val bitmapDescriptor = when (networkTypeEnum) {
            LTE -> redMarker
            WCDMA -> purpleMarker
            GSM -> blueMarker
            NS -> yellowMarker
        }
        if (bitmapDescriptor != null) return bitmapDescriptor

        val drawable = ContextCompat.getDrawable(context, R.drawable.google_map_marker)
        DrawableCompat.setTint(drawable!!, networkTypeEnum.color.toArgb())
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}