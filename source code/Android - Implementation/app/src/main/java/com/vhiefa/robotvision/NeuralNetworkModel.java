package com.vhiefa.robotvision;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;

import static java.lang.Math.exp;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

public class NeuralNetworkModel {


    int number_neuronX,number_neuronZ,number_neuronY;
    List<Double[]> weightW;
    List<Double[]> weightV;

    NeuralNetworkModel(int numberNeuronX, int numberNeuronZ, int numberNeuronY, List<Double[]> weight_W, List<Double[]> weight_V) { //constructor
        number_neuronX = numberNeuronX;
        number_neuronY = numberNeuronY;
        number_neuronZ = numberNeuronZ;
        weightW = weight_W;
        weightV =weight_V;
    }

    char predict(int[] datainput) {
        int i,j;
        double[] Z = new double[number_neuronZ];
        double[] Znet = new double[number_neuronZ];
        double[] Y = new double[number_neuronY];
        double[] Ynet = new double[number_neuronY];

        for (i = 0;i<number_neuronZ;i++){
            double sikmaV = 0;
            for (j = 1;j<number_neuronX + 1;j++) {
                sikmaV = sikmaV + datainput[j - 1] * weightV.get(i)[j];
            }
            Znet[i] = 1 * weightV.get(i)[1] + sikmaV;
            Z[i] = 1 / (1 + exp(-Znet[i]));
        }
        for (i = 0;i<number_neuronY;i++){
            double sikmaW = 0;
            for (j = 1;j<number_neuronZ + 1;j++) {
                sikmaW = sikmaW + Z[j - 1] * weightW.get(i)[j];
            }
            Ynet[i] = 1 * weightW.get(i)[1] + sikmaW;
            Y[i] = 1 / (1 + exp(-Ynet[i]));
        }
        char result = covertResult(Y);

        return result;
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    private char covertResult(double[] Y){
        char[] alphabetlist = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        int index = 0;
       // DoubleSummaryStatistics stat = Arrays.stream(Y).summaryStatistics();
        double maxValue = 0;//stat.getMax();
        for(int i = 0; i < Y.length; i++) {
            if(Y[i] > maxValue) {
                maxValue = Y[i];
                index = i;
            }
        }
        return alphabetlist[index];
    }

    public int[] preprocessing(Mat segment, int width, int height){
        Mat resized_img = new Mat();
        Mat img_bw = new Mat();
        Mat img_gray = new Mat();
        Imgproc.resize(segment, resized_img, new Size(width, height));
        Imgproc.cvtColor(resized_img, img_gray, Imgproc.COLOR_RGB2GRAY);
        threshold(img_gray, img_bw, 128.0, 255.0, THRESH_BINARY);
       // Log.d("result", "bw : "+img_bw+"");
       // Log.d("result", "bw2 : "+img_bw.get(0,0)[0]+"");
        int[] feature = new int[height*width];
        int i,j;
        int index=0;
        for (i=0;i<height;i++){
            for (j=0;j<width;j++){
                if (img_bw.get(i,j)[0]==0){
                    feature[index]=0;
                } else {
                    feature[index]=1;
                }
                index++;
            }
        }
        return feature;
    }

}
