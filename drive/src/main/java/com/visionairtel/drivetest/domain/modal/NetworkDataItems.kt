package com.visionairtel.drivetest.domain.modal

import com.google.android.gms.maps.model.LatLng
import com.visionairtel.drivetest.util.Util.NetworkTypeEnum


data class NetworkDataItems(
    var time: String = "",
    var latLong: LatLng = LatLng(0.0, 0.0),
    var networkType: String = "",
    var shortNetworkType: String = "",
    var mcc: String = "",
    var mnc: String = "",
    var pci: String = "",
    var earfcn: String = "",
    var uarfcn: String = "",
    var arfcn: String = "",
    var tac: String = "",
    var cellID: String = "",
    var cqi: String = "",
    var ta: String = "",
    var rssi: String = "",
    var rsrp: String = "",
    var rscp: String = "",
    var asu: String = "",
    var rssiDbm: String = "",
    var rsnnr: String = "",
    var signalQuality: String = "",
    var ecNo: String = "",
    var lacId: String = "",
    var bsic: String = "",
    var bitErrorRate: String = "",
    var networkTypeEnum: NetworkTypeEnum = NetworkTypeEnum.LTE,
) {

    fun toReadableString(): String {
        val signalQualityText =
            if (signalQuality.isEmpty()) "" else ", Signal Level is $signalQuality"
        val mccText = if (mcc.isEmpty()) "" else ", MCC is $mcc"
        val mncText = if (mnc.isEmpty()) "" else ", MNC is $mnc"
        val cellIDText = if (cellID.isEmpty()) "" else ", Cell Id is $cellID"
        val rssiText = if (rssi.isEmpty()) "" else ", RSSI is $rssi"
        val rssiDbmText = if (rssiDbm.isEmpty()) "" else ", RSSI DBM: ${rssiDbm}Dbm"

        return "Network type is $networkType at ${latLong.latitude}, ${latLong.longitude}" +
                "$signalQualityText$mccText$mncText$cellIDText$rssiText$rssiDbmText"
    }

    companion object {
        private const val header =
            "time,Lat,Lon,Networktype,networkproto,mcc,mnc,imsi,pci,earfcn,4G_ta ,4Q_CQI,4G_RSSI," +
                    "RSRP,sinr,TAC,enb,4G_cellid,RSRQ,psc,uarfcn,3G_CQI,3G_RSSI,RSCP,ecno,3G_lac," +
                    "NodeBId,3G_cellid,arfcn,2G_ta,rxlev,rxqual,2G_lac,siteid,2G_cellid,speed,distance,\n"

        const val csvHeader =
            "Time,Lat,Long,Network Type,MCC,MNC,PCI,EARFCN,UARFCN,ARFCN,TAC,Cell Id,CQI,TA,RSSI,RSRP,RSCP," +
                    "ASU,RSSI DBM,RSNNR,Signal Quality,Ec No,Lac Id,BSIC,Bit Error Rate\n"
    }

    override fun toString(): String {
        return "$time,${latLong.latitude},${latLong.longitude},$networkType,$mcc,$mnc," +
                "$pci,$earfcn,$uarfcn,$arfcn,$tac,$cellID,$cqi,$ta,$rssi,$rsrp,$rscp," +
                "$asu,$rssiDbm,$rsnnr,$signalQuality,$ecNo,$lacId,$bsic,$bitErrorRate\n"
    }
}

