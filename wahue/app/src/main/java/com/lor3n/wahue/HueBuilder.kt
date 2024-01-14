package com.lor3n.wahue

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.palette.graphics.Palette


class HueBuilder constructor(image: Bitmap)
{

    private var startImage: Bitmap = image
    private var hueImage: Bitmap? = null

    public fun BuildHue(){

        val palette = Palette.from(startImage).generate()
        val dominantColorsBitmaps = mutableListOf<Bitmap>()

        val border: Int = 100

        // Extract 5 dominant colors if available
        val swatches = palette.swatches
        val numColorsToExtract = 5.coerceAtMost(swatches.size)
        for (i in 0 until numColorsToExtract) {
            val color = swatches[i].rgb

            val colBitmap = Bitmap.createBitmap(100, 150 , Bitmap.Config.ARGB_8888)
            val canvas = Canvas(colBitmap)
            canvas.drawColor(color)
            dominantColorsBitmaps.add(colBitmap)
        }

        //Building palette bitmap
        var hueBitmap = dominantColorsBitmaps[0]
        for ((index, bitmap) in dominantColorsBitmaps.withIndex()) {
            if(index == 0){
                continue
            }
            hueBitmap = appendRightBitmaps(hueBitmap,bitmap)
        }

        hueImage = hueBitmap
    }

    public fun getHueImage(): Bitmap?{
        return hueImage
    }

    private fun appendRightBitmaps(firstBitmap: Bitmap, secondBitmap: Bitmap): Bitmap {

        var resultWidth = firstBitmap.width + secondBitmap.width
        var resultHeight = firstBitmap.height

        val resultBitmap = Bitmap.createBitmap(resultWidth, resultHeight, firstBitmap.config)
        val canvas = Canvas(resultBitmap)

        canvas.drawBitmap(firstBitmap, 0f, 0f, null)
        canvas.drawBitmap(secondBitmap, firstBitmap.width.toFloat(), 0f, null)

        return resultBitmap
    }

}