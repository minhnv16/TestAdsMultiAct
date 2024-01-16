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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        fr_layout_googleads = (FrameLayout ) findViewById(R.id.frameLayout_googleads);

        context = getApplicationContext();
        Log.d(TAG, "context="+String.valueOf(context));
        setAdsByConnectionStatus(bShowingAds);

        int nOrientation = getResources().getConfiguration().orientation;
        InitLayout(nOrientation);

    }

    private void InitLayoutForAds(int nOrientation){
        ConstraintLayout constraintLayout = findViewById(R.id.mainAct);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        int nIdRoot = R.id.mainAct;
        int nIdAds = R.id.frameLayout_googleads;
        int nIdContent = R.id.contentAct;
        if(nOrientation== Configuration.ORIENTATION_LANDSCAPE){
            Log.d("On Config Change","LANDSCAPE");
            //for ads
            constraintSet.clear(nIdAds, ConstraintSet.TOP);
            constraintSet.connect(nIdAds,ConstraintSet.TOP,nIdRoot,ConstraintSet.TOP,0);

            constraintSet.clear(nIdAds, ConstraintSet.LEFT);
            //constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.LEFT,R.id.mainAct,ConstraintSet.LEFT,0);

            constraintSet.clear(nIdAds, ConstraintSet.BOTTOM);
            constraintSet.connect(nIdAds,ConstraintSet.BOTTOM,nIdRoot,ConstraintSet.BOTTOM,0);

            constraintSet.clear(nIdAds, ConstraintSet.RIGHT);
            constraintSet.connect(nIdAds,ConstraintSet.RIGHT,nIdRoot,ConstraintSet.RIGHT,0);

            constraintSet.setVerticalBias(nIdAds, 0.5f);
            constraintSet.setHorizontalBias(nIdAds, 1.0f);



            //for content
            constraintSet.clear(nIdContent, ConstraintSet.TOP);
            constraintSet.connect(nIdContent,ConstraintSet.TOP,nIdRoot,ConstraintSet.TOP,0);

            constraintSet.clear(nIdContent, ConstraintSet.LEFT);
            constraintSet.connect(nIdContent,ConstraintSet.LEFT,nIdRoot,ConstraintSet.LEFT,0);



            constraintSet.clear(nIdContent, ConstraintSet.BOTTOM);
            constraintSet.connect(nIdContent,ConstraintSet.BOTTOM,nIdRoot,ConstraintSet.BOTTOM,0);

            constraintSet.clear(nIdContent, ConstraintSet.RIGHT);
            constraintSet.connect(nIdContent,ConstraintSet.RIGHT,nIdAds,ConstraintSet.LEFT,0);

            constraintSet.setVerticalBias(nIdContent, 0.5f);
            constraintSet.setHorizontalBias(nIdContent, 0.0f);


        }
        else{
            Log.d("On Config Change","PORTRAIT");

            //for ads
            constraintSet.clear(nIdAds, ConstraintSet.TOP);
            //constraintSet.connect(R.id.frameLayout_googleads,ConstraintSet.TOP,R.id.mainAct,ConstraintSet.TOP,0);

            constraintSet.clear(nIdAds, ConstraintSet.LEFT);
            constraintSet.connect(nIdAds,ConstraintSet.LEFT,nIdRoot,ConstraintSet.LEFT,0);

            constraintSet.clear(nIdAds, ConstraintSet.BOTTOM);
            constraintSet.connect(nIdAds,ConstraintSet.BOTTOM,nIdRoot,ConstraintSet.BOTTOM,0);

            constraintSet.clear(nIdAds, ConstraintSet.RIGHT);
            constraintSet.connect(nIdAds,ConstraintSet.RIGHT,nIdRoot,ConstraintSet.RIGHT,0);

            constraintSet.setVerticalBias(nIdAds, 1.0f);
            constraintSet.setHorizontalBias(nIdAds, 0.5f);

            //for content
            constraintSet.clear(nIdContent, ConstraintSet.TOP);
            constraintSet.connect(nIdContent,ConstraintSet.TOP,nIdRoot,ConstraintSet.TOP,0);

            constraintSet.clear(nIdContent, ConstraintSet.LEFT);
            constraintSet.connect(nIdContent,ConstraintSet.LEFT,nIdRoot,ConstraintSet.LEFT,0);

            constraintSet.clear(nIdContent, ConstraintSet.BOTTOM);
            constraintSet.connect(nIdContent,ConstraintSet.BOTTOM,nIdAds,ConstraintSet.TOP,0);

            constraintSet.clear(nIdContent, ConstraintSet.RIGHT);
            constraintSet.connect(nIdContent,ConstraintSet.RIGHT,nIdRoot,ConstraintSet.LEFT,0);

            constraintSet.setVerticalBias(nIdContent, 0.0f);
            constraintSet.setHorizontalBias(nIdContent, 0.5f);
        }
        constraintSet.applyTo(constraintLayout);


        //begin adjust content width step
        View viewcontentAds = findViewById(R.id.frameLayout_googleads);
        View viewcontentAct = findViewById(R.id.contentAct);

        ViewGroup.LayoutParams layoutParamsAds = viewcontentAds.getLayoutParams();
        ViewGroup.LayoutParams layoutParamsContent = viewcontentAct.getLayoutParams();

        int nNewContentWidthPx = 0;
        if(nOrientation== Configuration.ORIENTATION_LANDSCAPE) {
            nNewContentWidthPx = getResources().getDisplayMetrics().widthPixels - layoutParamsAds.width;
        }
        else{
            nNewContentWidthPx = getResources().getDisplayMetrics().widthPixels;
        }
        layoutParamsContent.width = nNewContentWidthPx;
        //begin adjust content width step

    }



    private void InitLayout(int nOrientation) {
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