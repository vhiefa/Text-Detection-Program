package com.vhiefa.robotvision;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

public class Blob {
    float minx, miny, maxx, maxy;
    float distTreshold =25;

    Blob(float x, float y) { //constructor
        minx = x;
        miny = y;
        maxx = x;
        maxy = y;
    }

    float getMinx(){
        return minx*10;
    }

    float getMiny(){
        return miny*10;
    }

    float getMaxx(){
        return maxx;
    }

    float getMaxy(){
        return maxy;
    }

    float size(){
        return (maxx-minx)*(maxy-miny);
    }

    void showRetangle(Mat img){
      //  Mat img_bw = new Mat();
      //  Mat img_gray = new Mat();
        Imgproc.rectangle(img, new Point(minx*10, miny*10), new Point(((maxx+1)*10)-1, ((maxy+1)*10)-1), new Scalar(255, 255, 255, 255), 1);
        //Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
      //  threshold(img_gray, img_bw, 128.0, 255.0, THRESH_BINARY);
    }

    void add(float x, float y){
        minx = min(minx, x);
        miny = min(miny, y);
        maxx = max(maxx,x);
        maxy = max(maxy, y);

    }

    boolean isNear (float x, float y){
        float cx = (minx + maxx)/2;
        float cy = (miny +maxy)/2;
        float d = distSq(cx,cy, x,y);
        if (d<distTreshold*distTreshold) {
            return true;
        } else {
            return false;
        }
    }

    float distSq(float x1, float y1, float x2, float y2){
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
    }

    int[] histogram(Mat img){
        Mat img_bw = new Mat();
        Mat img_gray = new Mat();
        Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
        threshold(img_gray, img_bw, 128.0, 255.0, THRESH_BINARY);
        int x, y;
        int min_x = Math.round(minx*10);
        int min_y = Math.round(miny*10);
        int max_x = Math.round((maxx+1)*10-1);
        int max_y = Math.round((maxy+1)*10-1);
        int frequency[];
        frequency = new int[Math.round(max_x-min_x)];
        for  (x=min_x;x<max_x;x++){
            for (y=min_y;y<max_y;y++){
                double[] pixel = img_bw.get(y,x);
                if (pixel[0]!=0) { //not black
                    frequency[x-min_x] += 1;
                }
            }
        }
        return frequency;
    }


    int[] histogramHor(Mat img){
        Mat img_bw = new Mat();
        Mat img_gray = new Mat();
        Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
        threshold(img_gray, img_bw, 128.0, 255.0, THRESH_BINARY);
        int x, y;
        int min_x = Math.round(minx*10);
        int min_y = Math.round(miny*10);
        int max_x = Math.round((maxx+1)*10-1);
        int max_y = Math.round((maxy+1)*10-1);
        int frequency[];
        frequency = new int[Math.round(max_y-min_y)];
        for (y=min_y;y<max_y;y++){
            for  (x=min_x;x<max_x;x++){
                double[] pixel = img_bw.get(y,x);
                if (pixel[0]!=0) { //not black
                    frequency[y-min_y] += 1;
                }
            }
        }
        return frequency;
    }



    boolean isText(){
        return false;
    }
}
