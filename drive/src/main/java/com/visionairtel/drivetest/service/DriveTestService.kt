package com.visionairtel.drivetest.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import javax.inject.Inject

class DriveTestService @Inject constructor(

) : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

}