package com.visionairtel.drivetest.domain.use_case

import android.os.Environment
import com.visionairtel.drivetest.domain.modal.NetworkDataItems
import com.visionairtel.drivetest.util.Util.showLog
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SaveCsvFileUseCase @Inject constructor() {


    suspend operator fun invoke(networkDataList: List<NetworkDataItems>): Boolean {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

        val hms = SimpleDateFormat("dd_MM_HH_mm_ss").format(Date(System.currentTimeMillis()))
        val filePath = "$hms _map.csv"

        try {
            val file = File(path,filePath)
            val writer = FileWriter(file)

            // Write the header
            writer.append(NetworkDataItems.csvHeader)

            // Write the data rows
            for (networkData in networkDataList) {
                writer.append(networkData.toString())
            }

            writer.close()
            showLog(filePath, "successfully created")
            println("CSV file '$filePath' successfully created.")
            return true
        } catch (e: IOException) {
            println("Failed to create CSV file: ${e.message}")
            e.printStackTrace()
            showLog(e.message!!, "sFailed to create CSV file")
            return false
        }
    }

}