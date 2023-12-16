package com.lor3n.wahue;

import android.graphics.Bitmap;
/*
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

class HueBuilder {

    private Mat image;
    private Mat hueImage;

    public HueBuilder(Bitmap bitmap){
        image = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, image);
    }
    public void BuildHueImage(){
        int count_clusters = 5;
        Mat labels = new Mat();
        Mat centers = new Mat();
        Mat image32f = new Mat();

        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 10, 1.0);
        int flags = Core.KMEANS_RANDOM_CENTERS;

        image.convertTo(image32f, CvType.CV_32F, 1.0 / 255.0);

        Core.kmeans(image32f, count_clusters, labels, criteria, 10, flags, centers);
        hueImage = centers;
        System.out.println(centers);
    }

    public Bitmap GetHueImage(){
        Bitmap bitmap = Bitmap.createBitmap(hueImage.cols(), hueImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(hueImage, bitmap);
        return bitmap;
    }

}
 */