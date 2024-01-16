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

        var hueWithBorder: Bitmap = Bitmap.createBitmap(hueBitmap.width + 10 * 2, hueBitmap.height + 10 * 2, hueBitmap.config);
        val canvas: Canvas = Canvas(hueWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(hueBitmap, 10f, 10f, null);

        hueImage = hueWithBorder
    }

    private fun BuildHueVertical(): Bitmap{
        val palette = Palette.from(startImage).generate()
        val dominantColorsBitmaps = mutableListOf<Bitmap>()

        val border: Int = 100

        // Extract 5 dominant colors if available
        val swatches = palette.swatches
        val numColorsToExtract = 5.coerceAtMost(swatches.size)
        for (i in 0 until numColorsToExtract) {
            val color = swatches[i].rgb

            val colBitmap = Bitmap.createBitmap(200, startImage.height/5 , Bitmap.Config.ARGB_8888)
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
            hueBitmap = appendDownBitmaps(hueBitmap,bitmap)
        }

        var hueWithBorder: Bitmap = Bitmap.createBitmap(hueBitmap.width + 20, hueBitmap.height, hueBitmap.config);
        val canvas: Canvas = Canvas(hueWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(hueBitmap, 20f, 0f, null);

        return hueWithBorder
    }

    public fun BuildHueImage(): Bitmap{
        val hueBitmap = BuildHueVertical()
        var finalImage = appendRightBitmaps(startImage, hueBitmap)

        var finalWithBorder: Bitmap = Bitmap.createBitmap(finalImage.width + 20 * 2, finalImage.height + 20 * 2, finalImage.config);
        val canvas: Canvas = Canvas(finalWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(finalImage, 20f, 20f, null);

        return finalWithBorder
    }

    fun BuildHueList(): List<String>{
        val palette = Palette.from(startImage).generate()
        val dominantColors = mutableListOf<String>()
        val swatches = palette.swatches
        val numColorsToExtract = 5.coerceAtMost(swatches.size)
        for (i in 0 until numColorsToExtract) {
            val color = Integer.toHexString(swatches[i].rgb)
            dominantColors.add(color)
        }
        return dominantColors
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

        var resultWidth = firstBitmap.width
        var resultHeight = firstBitmap.height + secondBitmap.height

        val resultBitmap = Bitmap.createBitmap(resultWidth, resultHeight, firstBitmap.config)
        val canvas = Canvas(resultBitmap)

        canvas.drawBitmap(firstBitmap, 0f, 0f, null)
        canvas.drawBitmap(secondBitmap, 0f, firstBitmap.height.toFloat(), null)

        return resultBitmap
    }


}