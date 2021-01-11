package com.vhiefa.robotvision;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.IntSummaryStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG ="MainActivity";
    Mat mRgba,mRgba2, mGray, mCanny, mHsv;
    private static final int PERMISSION_REQUEST_CODE = 200;
    NeuralNetworkModel neuralNetworkModel;

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
            super.onManagerConnected(status);
        }
    };

    JavaCameraView javaCameraView;

    ArrayList<Blob> blobs = new ArrayList<Blob>();
    ArrayList<CharSegment> charSegments = new ArrayList<CharSegment>();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

       // textView = (TextView) findViewById(R.id.resultTxt);

        List<Double[]> weightW = getWeight("weightW");
        List<Double[]> weightV = getWeight("weightV");
        Log.d("CSV", weightW.get(0)[0]+"");

        neuralNetworkModel = new NeuralNetworkModel(112,75,26, weightW, weightV);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        requestPermission();
        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

    }

    @Override
    protected  void onPause(){
        super.onPause();
        if (javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (OpenCVLoader.initDebug()){
            Log.i(TAG, "successfully loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else {
            Log.i(TAG, "Not success");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,this, mLoaderCallback);
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba=new Mat(height, width, CvType.CV_8UC4); //The first 3 in this are R, G, B, and the last is Alpha, which is a value between 0 and 1 representing transparency.
        mRgba2=new Mat(height, width, CvType.CV_8UC4);
        mHsv=new Mat(height, width, CvType.CV_8UC4);
        mGray=new Mat(height, width, CvType.CV_8UC1);
        mCanny=new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Mat resizeimage = new Mat();
        blobs.clear();
        Imgproc.resize( mRgba, resizeimage, new Size(mRgba.width()/10,mRgba.height()/10) );
        Imgproc.cvtColor(resizeimage, mHsv, Imgproc.COLOR_RGB2HSV);
        trackGreen(mHsv, mHsv.height(), mHsv.width());

        for (Blob b: blobs) {
            if (b.size()>500) {
                String resultText="";
                b.showRetangle(mRgba);
                charSegments.clear();
                trackSegment(b);
                String resultChar = "";
                for (CharSegment s : charSegments){
                    if (s.size()>100 || s.isSpace()==true) {
                        if (s.isSpace()==false) {
                            s.showRetangle(mRgba);
                            Rect rectCrop = s.getRectangle();
                            Mat segment = new Mat(mRgba, rectCrop);
                            try {
                                int[] feature = neuralNetworkModel.preprocessing(segment, 8, 14);
                                resultChar = String.valueOf(neuralNetworkModel.predict(feature));
                                
                            } catch (Exception e) {
                                Log.d("result2", e.getMessage() + "");
                            }
                        } else {
                            resultChar = " ";
                           // Log.d("space2", "masuk");
                        }
                        resultText = resultText + resultChar;
                    }
                }
                textView = (TextView) ((Activity) this).findViewById(R.id.resultTxt);
                Log.d("resultText", resultText);
                textView.setText(resultText);
            }
        }
        return mRgba;
    }


    private void trackGreen(Mat img, int height, int width){
        int row,col;
        for (row=0; row<height;row++){
            for (col=0; col<width;col++){

                double[] hsv = img.get(row,col);
                if (isGreen(hsv[0], hsv[1], hsv[2])){
                    boolean found =false;
                    for (Blob b : blobs){
                        if (b.isNear(col, row)){
                            b.add(col,row);
                            found = true;
                            break;
                        }
                    }
                    if (!found) { //true
                        Blob b = new Blob(col, row);
                        blobs.add(b);
                    }
                }
            }
        }
    }
    private boolean isGreen(double h, double s, double v){
        if (h>=50 &&h <=70 && s>=50 && s<=255 && v>=50 && v<=255){
            return true;
        } else {
            return  false;
        }
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void trackSegment(Blob b){
        int x=0, y=0, x2=0, y2=0;
        int g[] = b.histogramHor(mRgba);
        float[] g2 = normalize(g);
        int middle_length = g2.length/2;
        //int i = 0;
        int i_mid =middle_length;
        while (i_mid>0){
            if (g2[i_mid]<0.06){
                y=i_mid;
                break;
            }
            i_mid--;
        }

        while (middle_length<g2.length){
            if (g2[middle_length]<0.06){ //white
                y2=middle_length;
                break;
            }
            middle_length++;
        }

        int f[] =b.histogram(mRgba);
        float[] f2 = normalize(f);
        int m=0;
        boolean character_found = false;
     //   boolean space_found = false;
        int pixelcounter = 0;
        int Charwidth= 999;
        while (m<f2.length){

            if (f2[m]>0.02 && f2[m]<0.5 && character_found==false){ //white
                x=m;
                character_found = true;
             //   space_found =false;
            }

            if (f2[m]<=0.02&&character_found==true){ //black
                x2=m;
                //addsegment
                CharSegment segment = new CharSegment(x, y , x2, y2, b, false);
                charSegments.add(segment);
                Log.d("posisi", "char");
                Charwidth = x2-x;
                character_found = false;
             //   space_found = true;
             //   pixelcounter++;
            }

            if (f2[m]<=0.02){// && space_found==true){ //black
                //  space_found = true;
                pixelcounter++;
            }

        //    Log.d("space", "pixelcounter"+pixelcounter);
            if (pixelcounter>=Charwidth*2) {
                CharSegment segment = new CharSegment(0, 0, 0, 0, b, true);
                charSegments.add(segment);
                Log.d("posisi", "space");
                pixelcounter=0;
             //   space_found=false;
                Log.d("space", "masuk");
            }

            m++;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private float[] normalize(int[] data){
        IntSummaryStatistics stat = Arrays.stream(data).summaryStatistics();
        float max = stat.getMax();
        float min = stat.getMin();
        float[] newdata = new float[data.length];
        int i;
        for (i=0;i<data.length;i++){
            newdata[i] = (data[i]-min)/(max-min);
        }
        return newdata;
    }


    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public List<Double[]>  getWeight(String weightName){
        String param;
        if (weightName.equals("weightW")){
            param = "Z";
        } else {
            param = "X";
        }
        Double[] content = null;
        List<Double[]> weightdata = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(weightName+".json"));
            JSONArray m_jArry = obj.getJSONArray(weightName);
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                content = new Double[jo_inside.length()];
                content[0] = jo_inside.getDouble("1");
                for (int j=1;j<jo_inside.length();j++) {
                    content[j] = jo_inside.getDouble(param+j);
                }
                weightdata.add(content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weightdata;
    }
}
