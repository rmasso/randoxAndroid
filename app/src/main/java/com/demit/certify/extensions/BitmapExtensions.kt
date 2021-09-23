package com.demit.certify.extensions

import android.graphics.*
import android.util.Base64
import java.io.ByteArrayOutputStream


private const val MAX_FONT_SIZE = 96F

fun Bitmap.drawDetectionResult(
    bitmap: Bitmap,
    detectionResults: List<DetectionResult>
): Bitmap {
    val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)
    val pen = Paint()
    pen.textAlign = Paint.Align.LEFT

    detectionResults.forEach {
        // draw bounding box
        pen.color = Color.RED
        pen.strokeWidth = 8F
        pen.style = Paint.Style.STROKE
        val box = it.boundingBox
        canvas.drawRect(box, pen)


        val tagSize = Rect(0, 0, 0, 0)

        // calculate the right font size
        pen.style = Paint.Style.FILL_AND_STROKE
        pen.color = Color.YELLOW
        pen.strokeWidth = 2F

        pen.textSize = MAX_FONT_SIZE
        pen.getTextBounds(it.text, 0, it.text.length, tagSize)
        val fontSize: Float = pen.textSize * box.width() / tagSize.width()

        // adjust the font size so texts are inside the bounding box
        if (fontSize < pen.textSize) pen.textSize = fontSize

        var margin = (box.width() - tagSize.width()) / 2.0F
        if (margin < 0F) margin = 0F
        canvas.drawText(
            it.text, box.left + margin,
            box.top + tagSize.height().times(1F), pen
        )
    }
    return outputBitmap
}

fun Bitmap.cropImageInsideBoundingBox(original: Bitmap, boundingBox: RectF): Bitmap {
    val resultBitmap = Bitmap.createBitmap(
        boundingBox.width().toInt(),
        boundingBox.height().toInt(), Bitmap.Config.ARGB_8888
    );
    val cavas = Canvas(resultBitmap);

    // draw background
    val paint = Paint(Paint.FILTER_BITMAP_FLAG);
    paint.color = Color.WHITE;
    cavas.drawRect(//from  w w  w. ja v  a  2s. c  om
        RectF(0f, 0f, boundingBox.width(), boundingBox.height()),
        paint
    );

    val matrix = Matrix()
    matrix.postTranslate(-boundingBox.left, -boundingBox.top);

    cavas.drawBitmap(original, matrix, paint);

    if (!original.isRecycled) {
        original.recycle();
    }

    return resultBitmap
}

fun Bitmap.rotateImage(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        this, 0, 0, this.width, this.height,
        matrix, true
    )
}

const val OUTPUT_IMAGE_QUALITY = 70
//Extension function to
fun Bitmap.toBase64String(): String {
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG, OUTPUT_IMAGE_QUALITY, this)
        return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
    }
}


/**
 * DetectionResult
 *      A class to store the visualization info of a detected object.
 */
data class DetectionResult(val boundingBox: RectF, val text: String)

