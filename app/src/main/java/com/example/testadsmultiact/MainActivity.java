package com.example.testadsmultiact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    /**
     * A native method that is implemented by the 'testadsmultiact' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}