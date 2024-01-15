package com.example.testadsmultiact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.testadsmultiact.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ActAdsEventHandler {

    static final String TAG = MainActivity.class.getSimpleName();
    static boolean bAdsInitialized = false;
    static boolean bShowingAds = true;
    static AdRequest adRequest = null;
    static HashMap<String, ActAdsEventHandler> hashMap = new HashMap<>();

    // Used to load the 'testadsmultiact' library on application startup.
    static {
        System.loadLibrary("testadsmultiact");
    }

    private ActivityMainBinding binding;
    Context context = null;
    AdView mAdmodBanner=null;
    public FrameLayout fr_layout_googleads;



    void GotoConnect(){
        Intent myIntent = new Intent(MainActivity.this, MainActivity2.class);
        myIntent.putExtra("key1", 123); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
    View.OnClickListener onBtnGotoConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClickListener.onClick");
            GotoConnect();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        binding.button.setOnClickListener(onBtnGotoConnectClickListener);
        fr_layout_googleads = (FrameLayout ) findViewById(R.id.frameLayout_googleads);

        context = getApplicationContext();
        Log.d(TAG, "context="+String.valueOf(context));
        setAdsByConnectionStatus(bShowingAds);

        int nOrientation = getResources().getConfiguration().orientation;
        InitLayout(nOrientation);

    }
    int convertPixelToDp(int px){
        final float scale = getResources().getDisplayMetrics().density;
        int dps = (int) (px / scale + 0.5f);
        return dps;
    }
    int convertDpToPixel(int dps){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    void AutoContentSize(){
        Log.d("onClickListener","onClick");
        int nOrientation = getResources().getConfiguration().orientation;

        View rootView = findViewById(R.id.mainAct);
        ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
        Log.d("onClickListener","layoutParamsRoot :"+
                ", width="+String.valueOf(layoutParamsRoot.width)+
                ", height="+String.valueOf(layoutParamsRoot.height)
        );


        View viewcontentAct = findViewById(R.id.contentAct);
        ViewGroup.LayoutParams layoutParamsContent = viewcontentAct.getLayoutParams();

        Log.d("onClickListener","layoutParamsContent :"+
                ", width DP="+String.valueOf(convertPixelToDp(layoutParamsContent.width))+
                ", height DP="+String.valueOf(convertPixelToDp(layoutParamsContent.height))
        );


        Log.d("onClickListener", "widthPixels="+String.valueOf(getResources().getDisplayMetrics().widthPixels));
        Log.d("onClickListener", "heightPixels="+String.valueOf(getResources().getDisplayMetrics().heightPixels));

        Log.d("onClickListener", "widthDps="+String.valueOf(convertPixelToDp(getResources().getDisplayMetrics().widthPixels)));
        Log.d("onClickListener", "heightDps="+String.valueOf(convertPixelToDp(getResources().getDisplayMetrics().heightPixels)));


        View viewcontentAds = findViewById(R.id.frameLayout_googleads);

        ViewGroup.LayoutParams layoutParamsAds = viewcontentAds.getLayoutParams();
        Log.d("onClickListener", "Ads width Px="+String.valueOf(layoutParamsAds.width));
        Log.d("onClickListener", "Ads height Px="+String.valueOf(layoutParamsAds.height));

        Log.d("onClickListener", "Ads widthDps="+String.valueOf(convertPixelToDp(layoutParamsAds.width)));
        Log.d("onClickListener", "Ads heightDps="+String.valueOf(convertPixelToDp(layoutParamsAds.height)));

        int nNewContentWidthPx = 0, nNewContentHeightPx = 0;



        if(nOrientation== Configuration.ORIENTATION_LANDSCAPE) {
            nNewContentWidthPx = getResources().getDisplayMetrics().widthPixels - layoutParamsAds.width;
            nNewContentHeightPx = getResources().getDisplayMetrics().heightPixels- convertDpToPixel(48);
        }
        else{
            nNewContentWidthPx = getResources().getDisplayMetrics().widthPixels;
            nNewContentHeightPx = getResources().getDisplayMetrics().heightPixels - layoutParamsAds.height - convertDpToPixel(80);
        }

        Log.d("onClickListener", "Content width Px="+String.valueOf(nNewContentWidthPx));
        Log.d("onClickListener", "Content height Px="+String.valueOf(nNewContentHeightPx));

        layoutParamsContent.width =nNewContentWidthPx;
        layoutParamsContent.height=nNewContentHeightPx;
        viewcontentAct.setLayoutParams(layoutParamsContent);
        viewcontentAct.requestLayout();
    }

    private void InitLayoutForAds(int nOrientation){
        ConstraintLayout constraintLayout = findViewById(R.id.mainAct);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        float fbias = 0.2f;

        if(nOrientation== Configuration.ORIENTATION_LANDSCAPE){
            Log.d("On Config Change","LANDSCAPE");

            //for ads
            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.TOP);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.TOP,R.id.mainAct,ConstraintSet.TOP,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.LEFT);
            //constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.LEFT,R.id.mainAct,ConstraintSet.LEFT,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.BOTTOM,R.id.mainAct,ConstraintSet.BOTTOM,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.RIGHT,R.id.mainAct,ConstraintSet.RIGHT,0);

            constraintSet.setVerticalBias(R.id.frameLayout_googleads, 0.5f);
            constraintSet.setHorizontalBias(R.id.frameLayout_googleads, 1.0f);



            //for content
            constraintSet.clear(R.id.contentAct, ConstraintSet.TOP);
            constraintSet.connect(R.id.contentAct,ConstraintSet.TOP,R.id.mainAct,ConstraintSet.TOP,0);

            constraintSet.clear(R.id.contentAct, ConstraintSet.LEFT);
            constraintSet.connect(R.id.contentAct,ConstraintSet.LEFT,R.id.mainAct,ConstraintSet.LEFT,0);



            constraintSet.clear(R.id.contentAct, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.contentAct,ConstraintSet.BOTTOM,R.id.mainAct,ConstraintSet.BOTTOM,0);

            constraintSet.clear(R.id.contentAct, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.contentAct,ConstraintSet.RIGHT,R.id.frameLayout_googleads,ConstraintSet.LEFT,0);

            constraintSet.setVerticalBias(R.id.contentAct, 0.5f);
            constraintSet.setHorizontalBias(R.id.contentAct, 0.0f);


        }
        else{
            Log.d("On Config Change","PORTRAIT");

            //for ads
            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.TOP);
            //constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.TOP,R.id.mainAct,ConstraintSet.TOP,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.LEFT);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.LEFT,R.id.mainAct,ConstraintSet.LEFT,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.BOTTOM,R.id.mainAct,ConstraintSet.BOTTOM,0);

            constraintSet.clear(R.id.frameLayout_googleads, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.RIGHT,R.id.mainAct,ConstraintSet.RIGHT,0);

            constraintSet.setVerticalBias(R.id.frameLayout_googleads, 1.0f);
            constraintSet.setHorizontalBias(R.id.frameLayout_googleads, 0.5f);





            //for content
            constraintSet.clear(R.id.contentAct, ConstraintSet.TOP);
            constraintSet.connect(R.id.contentAct,ConstraintSet.TOP,R.id.mainAct,ConstraintSet.TOP,0);

            constraintSet.clear(R.id.contentAct, ConstraintSet.LEFT);
            constraintSet.connect(R.id.contentAct,ConstraintSet.LEFT,R.id.mainAct,ConstraintSet.LEFT,0);



            constraintSet.clear(R.id.contentAct, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.contentAct,ConstraintSet.BOTTOM,R.id.frameLayout_googleads,ConstraintSet.TOP,0);

            constraintSet.clear(R.id.contentAct, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.contentAct,ConstraintSet.RIGHT,R.id.mainAct,ConstraintSet.LEFT,0);

            constraintSet.setVerticalBias(R.id.contentAct, 0.0f);
            constraintSet.setHorizontalBias(R.id.contentAct, 0.5f);



        }
        constraintSet.applyTo(constraintLayout);
        AutoContentSize();
    }



    private void InitLayout(int nOrientation) {
        //InitLayoutForButton(nOrientation);
        InitLayoutForAds(nOrientation);

    }

    OnInitializationCompleteListener onInitializationCompleteListener = new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            Log.d(TAG, "onInitializationComplete: begin ");

        }
    };
    public void InitAds(){

        if(!bAdsInitialized)
        {
            MobileAds.initialize(context, onInitializationCompleteListener);
            bAdsInitialized = true;
        }
        if(mAdmodBanner==null) {
            mAdmodBanner = new AdView(context);
        }
        Log.d(TAG, "initAdsAdmob, "+"done new AdView");
        mAdmodBanner.setAdSize(AdSize.MEDIUM_RECTANGLE);

        //test ads
        String admob_app_banner = "ca-app-pub-3940256099942544/6300978111";


//        //real ads
//        String admob_app_banner = "ca-app-pub-2979798531795011/4426477935";

        Log.d(TAG, "initAdsAdmob, "+"admob_app_banner = "+ admob_app_banner);
        mAdmodBanner.setAdUnitId(admob_app_banner);

        Log.d(TAG, "initAdsAdmob, "+"done setAdUnitId");

        if(adRequest==null) {
            adRequest = new AdRequest.Builder().build();
        }
        Log.d(TAG, "initAdsAdmob, "+"done adRequest = new AdRequest.Builder()");

        mAdmodBanner.loadAd(adRequest);
        Log.d(TAG, "initAdsAdmob, "+"done mAdmodBanner.loadAd");

        mAdmodBanner.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                Log.d(TAG,"initAdsAdmob, "+"ads loaded");
            }
        });

        fr_layout_googleads.addView(mAdmodBanner);
        fr_layout_googleads.setVisibility(View.VISIBLE);

    }

    public void stopAds(){
        if(mAdmodBanner!=null) {
            mAdmodBanner.destroy();
            mAdmodBanner=null;
        }
        fr_layout_googleads.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        //setAdsByConnectionStatus(MainActivity.bShowingAds);

        hashMap.put(this.getClass().getSimpleName(), this);
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        InitLayout(configuration.orientation);
    }

    /**
     * A native method that is implemented by the 'testadsmultiact' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}