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
                            "Certificate Successfully saved to",
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
            val folder = getORCreateNewFolder(context)
            folder?.let {
                var fileOutputStream: FileOutputStream?
                try {
                    val pdfFile = File(folder, "$filename.pdf")
                    pdfFile.createNewFile()
                    fileOutputStream = FileOutputStream(pdfFile)
                    fileOutputStream.write(pdfBytes)
                    fileOutputStream.close()
                    Toast.makeText(context, "Certificate Successfully saved\n Location: ${pdfFile.absolutePath}", Toast.LENGTH_SHORT)
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

    private fun getORCreateNewFolder(context: Context): File? {

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS+ "/" + Constants.DEFAULT_APP_FOLDER)

        // Make sure the path directory exists.
        dir?.let {
            if (!it.exists()) {
                val success = it.mkdirs()
                if (!success) {
                    return null
                }
            }
        } ?: run {
            return null
        }
        return dir
    }
}