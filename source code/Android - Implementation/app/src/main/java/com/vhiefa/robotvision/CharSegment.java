package com.vhiefa.robotvision;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.exp;

public class CharSegment {

    int x, y, x2, y2;
    boolean isSpaceParam;
    CharSegment(int x_, int y_, int x2_, int y2_, Blob b, boolean isSpace) { //constructor
        float minx = b.getMinx();
        float miny = b.getMiny();
        x = (int) (x_+minx);
        y = (int) (y_ +miny);
        x2 = (int) (x2_+minx);
        y2 = (int) (y2_ +miny);
        isSpaceParam = isSpace;
    }

    int size(){
        return (x2-x)*(y2-y);
    }

    String getCharacter(){
        String alphabet="";
        return alphabet;
    }

    void showRetangle(Mat img){
        Imgproc.rectangle(img, new Point(x, y), new Point(x2, y2), new Scalar(0, 255, 255, 255), 1);
    }

    boolean isSpace(){
        return isSpaceParam;
    }

    Rect getRectangle(){
        Rect rectCrop = new Rect(x, y, x2-x, y2-y);
        return rectCrop;
    }


}
