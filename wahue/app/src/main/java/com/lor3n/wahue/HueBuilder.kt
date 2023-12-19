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

        startImage = Bitmap.createScaledBitmap(startImage, startImage.width/3, startImage.height/3, true);


        val palette = Palette.from(startImage).generate()
        val dominantColorsBitmaps = mutableListOf<Bitmap>()

        val border: Int = 100

        // Extract 5 dominant colors if available
        val swatches = palette.swatches
        val numColorsToExtract = 5.coerceAtMost(swatches.size)
        for (i in 0 until numColorsToExtract) {
            val color = swatches[i].rgb

            var hueWidth: Int = (startImage.width)/numColorsToExtract
            val colBitmap = Bitmap.createBitmap(hueWidth, startImage.height/3 , Bitmap.Config.ARGB_8888)
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


        var borderedHueBitmap = Bitmap.createBitmap(startImage.width+border, startImage.height/3+border/2, hueBitmap.config)
        val canvasHue = Canvas(borderedHueBitmap)
        canvasHue.drawColor(Color.WHITE)
        canvasHue.drawBitmap(hueBitmap, (border/2).toFloat(), 0f, null)


        var borderedPhotoBitmap = Bitmap.createBitmap(startImage.width+border, startImage.height+border, startImage.config)
        val canvas = Canvas(borderedPhotoBitmap)

        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(startImage, (border/2).toFloat(), (border/2).toFloat(), null)

        borderedPhotoBitmap = appendDownBitmaps(borderedPhotoBitmap, borderedHueBitmap)

        hueImage = borderedPhotoBitmap
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