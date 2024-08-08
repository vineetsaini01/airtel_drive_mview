package com.visionairtel.drivetest.domain.use_case

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import android.telephony.TelephonyManager.*
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import com.visionairtel.drivetest.domain.modal.NetworkDataItems
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.*
import com.visionairtel.drivetest.util.Util
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetNetworkDataUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    private var telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    suspend operator fun invoke(latLng: LatLng?): NetworkDataItems? {

        return withContext(Dispatchers.IO) {
            if (latLng == null) return@withContext null
            val data = NetworkDataItems(
                latLong = latLng,
                time = getCurrentTime(),
                shortNetworkType = getShortNetworkType(),
            )

            telephonyManager.allCellInfo.forEach { cellInfo ->
                if (cellInfo.isRegistered) {
                    when (cellInfo) {
                        // Extract LTE-specific data
                        is CellInfoLte -> {
                            data.networkType = "LTE"
                            data.networkTypeEnum = Util.NetworkTypeEnum.LTE
                            cellInfo.cellIdentity.apply {
                                data.mcc = mccString ?: "Unknown"
                                data.mnc = mncString ?: "Unknown"
                                data.pci = pci.toString()
                                data.tac = tac.toString()
                                data.cellID = ci.toString()
                                data.earfcn = earfcn.toString()
                            }

                            cellInfo.cellSignalStrength.apply {
                                data.cqi = cqi.toString()
                                data.ta = timingAdvance.toString()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    data.rssi = rssi.toString()
                                }
                                data.rsrp = rsrp.toString()
                                data.asu = asuLevel.toString()
                                data.rssiDbm = dbm.toString()
                                data.signalQuality = level.toString()
                                data.rsnnr = rssnr.toString()
                            }

                        }
                        // Extract WCDMA-specific data
                        is CellInfoWcdma -> {
                            data.networkTypeEnum = Util.NetworkTypeEnum.WCDMA
                            data.networkType = "WCDMA"
                            cellInfo.cellIdentity.apply {
                                data.mcc = mccString ?: "Unknown"
                                data.mnc = mncString ?: "Unknown"
                                data.uarfcn = uarfcn.toString()
                                data.lacId = lac.toString()
                            }
                            cellInfo.cellSignalStrength.apply {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    data.ecNo = ecNo.toString()
                                }
                                data.asu = asuLevel.toString()
                                data.rscp = (asuLevel - 150).toString()
                                data.rssiDbm = dbm.toString()
                                data.signalQuality = level.toString()
                            }
                        }
                        // Extract GSM-specific data
                        is CellInfoGsm -> {
                            data.networkTypeEnum = Util.NetworkTypeEnum.GSM
                            data.networkType = "GSM"
                            cellInfo.cellIdentity.apply {
                                data.mcc = mccString ?: "Unknown"
                                data.mnc = mncString ?: "Unknown"
                                data.arfcn = arfcn.toString()
                                data.bsic = bsic.toString()
                                data.lacId = lac.toString()
                            }
                            cellInfo.cellSignalStrength.apply {
                                data.ta = timingAdvance.toString()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    data.rssi = rssi.toString()
                                }
                                data.ta = timingAdvance.toString()
                                data.asu = asuLevel.toString()
                                data.rssiDbm = dbm.toString()
                                data.signalQuality = level.toString()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    data.bitErrorRate = bitErrorRate.toString()
                                }
                            }
                        }
                        // Extract CDMA-specific data
                        is CellInfoCdma -> {
                            cellInfo.cellSignalStrength.apply {
                                data.networkType = "CDMA"
                                data.asu = asuLevel.toString()
                                data.rssiDbm = dbm.toString()
                            }
                        }
                    }
                }
            }
            data
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }

    @SuppressLint("MissingPermission")
    private fun getShortNetworkType(): String {
        return when (telephonyManager.networkType) {
            NETWORK_TYPE_IWLAN -> "WiFi"
            NETWORK_TYPE_1xRTT, NETWORK_TYPE_CDMA, NETWORK_TYPE_EDGE, NETWORK_TYPE_GPRS,
            NETWORK_TYPE_GSM, NETWORK_TYPE_IDEN,
            -> "2G"
            NETWORK_TYPE_EHRPD, NETWORK_TYPE_EVDO_0, NETWORK_TYPE_EVDO_A, NETWORK_TYPE_EVDO_B,
            NETWORK_TYPE_HSDPA, NETWORK_TYPE_HSPA, NETWORK_TYPE_HSPAP, NETWORK_TYPE_HSUPA,
            NETWORK_TYPE_TD_SCDMA, NETWORK_TYPE_UMTS,
            -> "3G"
            NETWORK_TYPE_LTE -> "4G"
            NETWORK_TYPE_NR -> "5G"
            NETWORK_TYPE_UNKNOWN -> "NS"
            else -> "NS"
        }
    }

}