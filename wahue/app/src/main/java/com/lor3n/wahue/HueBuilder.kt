package com.lor3n.wahue

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import androidx.palette.graphics.Palette


class HueBuilder constructor(image: Bitmap)
{

    private var startImage: Bitmap = image
    private var hueImage: Bitmap? = null

    public fun BuildHue(){

        val palette = Palette.from(startImage).generate()
        // Retrieve the dominant colors from the Palette
        val dominantColors = mutableListOf<Int>()
        val dominantColorsBitmaps = mutableListOf<Bitmap>()

        // Extract 5 dominant colors if available
        val swatches = palette.swatches
        val numColorsToExtract = 5.coerceAtMost(swatches.size)
        for (i in 0 until numColorsToExtract) {
            val color = swatches[i].rgb

            val colBitmap = Bitmap.createBitmap(100, 300, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(colBitmap)
            canvas.drawColor(color)
            dominantColorsBitmaps.add(colBitmap)
            dominantColors.add(color)
        }


        var hueBitmap = dominantColorsBitmaps[0]
        for ((index, bitmap) in dominantColorsBitmaps.withIndex()) {
            if(index == 0){
                continue
            }
            hueBitmap = appendRightBitmaps(hueBitmap,bitmap)
        }

        /*// Print or use the dominant colors
        for ((index, color) in dominantColors.withIndex()) {
            println("Dominant color ${index + 1}: ${Integer.toHexString(color)}")
        }
         */

        //Crea una nuova immagine, appendi l'immagine e la hueBitmap

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

    private fun appendDownBitmaps(firstBitmap: Bitmap, secondBitmap: Bitmap): Bitmap {

        val resultWidth = firstBitmap.width.coerceAtLeast(secondBitmap.width)
        val resultHeight = firstBitmap.height + secondBitmap.height

        val resultBitmap = Bitmap.createBitmap(resultWidth, resultHeight, firstBitmap.config)
        val canvas = Canvas(resultBitmap)

        canvas.drawBitmap(firstBitmap, 0f, 0f, null)
        canvas.drawBitmap(secondBitmap, 0f, firstBitmap.height.toFloat(), null)

        return resultBitmap
    }

}