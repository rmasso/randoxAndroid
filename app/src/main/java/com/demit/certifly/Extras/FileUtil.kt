package com.demit.certifly.Extras

import android.content.ContentValues
import android.content.Context
import android.os.Build
import java.io.File
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.FileOutputStream
import java.lang.Exception

object FileUtil {
    fun saveFile(context: Context, filename: String, pdfBytes: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Test for android q and above
            with(context.contentResolver) {
                try {
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.pdf")
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOCUMENTS}${File.separator}${Constants.DEFAULT_APP_FOLDER}"
                    )
                    val pdfUri = insert(
                        MediaStore.Files.getContentUri("external"),
                        contentValues
                    )
                    pdfUri?.let {
                        val outputStream = openOutputStream(it)
                        outputStream?.write(pdfBytes)
                        outputStream?.close()
                        Toast.makeText(
                            context,
                            "Certificate Successfully saved",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    } ?: kotlin.run {
                        Toast.makeText(context, "Unable to save Certificate", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(context, "Unable to save Certificate", Toast.LENGTH_SHORT)
                        .show()
                    ex.printStackTrace()
                }
            }

        } else {
            val folder = getORCreateNewFolder()
            folder?.let {
                var fileOutputStream: FileOutputStream?
                try {
                    val pdfFile = File(folder, "$filename.pdf")
                    if (pdfFile.exists()) {
                        Toast.makeText(
                            context,
                            "Can't save Certificate because it already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    pdfFile.createNewFile()
                    fileOutputStream = FileOutputStream(pdfFile)
                    fileOutputStream.write(pdfBytes)
                    fileOutputStream.close()
                    Toast.makeText(context, "Certificate Successfully saved", Toast.LENGTH_SHORT)
                        .show()
                } catch (ex: Exception) {
                    Toast.makeText(context, "Unable to save Certificate", Toast.LENGTH_SHORT).show()
                    ex.printStackTrace()
                }
            } ?: kotlin.run {
                Toast.makeText(context, "Unable to save Certificate", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getORCreateNewFolder(): File? {

        var dir = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/" + Constants.DEFAULT_APP_FOLDER
        )

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                return null
            }
        }
        return dir
    }
}