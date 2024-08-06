package com.visionairtel.drivetest.domain.modal

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

data class MarkerItems(
    val networkType: String,
    val latLng: LatLng,
    val icon: BitmapDescriptor,
)