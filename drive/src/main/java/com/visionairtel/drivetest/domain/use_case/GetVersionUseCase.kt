package com.visionairtel.drivetest.domain.use_case

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetVersionUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    operator fun invoke(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0"
        }
    }
}