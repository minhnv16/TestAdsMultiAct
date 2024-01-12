package com.example.testadsmultiact;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity2 extends AppCompatActivity implements ActAdsEventHandler {
    static final String TAG = MainActivity.class.getSimpleName();
    AdView mAdmodBanner;
    public FrameLayout fr_layout_googleads;


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.bShowingAds = !MainActivity.bShowingAds;
            Log.d(TAG, "onClickListener.onClick, bShowingAds="+String.valueOf(MainActivity.bShowingAds));
            for( ActAdsEventHandler actAdsEventHandler:MainActivity.hashMap.values()){
                actAdsEventHandler.setAdsByConnectionStatus(MainActivity.bShowingAds);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Context context = getApplicationContext();
        Log.d(TAG, "context="+String.valueOf(context));

        Button btn = findViewById(R.id.button2);
        btn.setOnClickListener(onClickListener);
        fr_layout_googleads = (FrameLayout ) findViewById(R.id.frameLayout_googleads);
        setAdsByConnectionStatus(MainActivity.bShowingAds);
    }
    OnInitializationCompleteListener onInitializationCompleteListener = new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            Log.d(TAG, "onInitializationComplete: begin ");

        }
    };
    public void InitAds(){

        MobileAds.initialize(this, onInitializationCompleteListener);
        mAdmodBanner = new AdView(this);

        Log.d(TAG, "initAdsAdmob, "+"done new AdView");
        mAdmodBanner.setAdSize(AdSize.MEDIUM_RECTANGLE);

        //test ads
        String admob_app_banner = "ca-app-pub-3940256099942544/6300978111";


//        //real ads
//        String admob_app_banner = "ca-app-pub-2979798531795011/4426477935";

        Log.d(TAG, "initAdsAdmob, "+"admob_app_banner = "+ admob_app_banner);
        mAdmodBanner.setAdUnitId(admob_app_banner);

        Log.d(TAG, "initAdsAdmob, "+"done setAdUnitId");

        AdRequest adRequest = new AdRequest.Builder().build();
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

        ;    }

    public void stopAds(){
        if(mAdmodBanner!=null) {
            mAdmodBanner.destroy();
        }
        fr_layout_googleads.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        MainActivity.hashMap.put(this.getClass().getSimpleName(), this);
        super.onResume();
    }

}