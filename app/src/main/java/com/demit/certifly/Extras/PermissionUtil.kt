package com.demit.certifly.Extras

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtil {

    const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    const val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    fun hasCameraPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    fun hasStoragePermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            STORAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    fun isMarshMallowOrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}