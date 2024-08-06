package com.visionairtel.drivetest.domain.use_case

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(private val fusedLocationClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission")
    operator fun invoke(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // super.onLocationResult(locationResult)
                locationResult.locations.first().apply {
                    trySend(this)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }.flowOn(Dispatchers.IO)

}