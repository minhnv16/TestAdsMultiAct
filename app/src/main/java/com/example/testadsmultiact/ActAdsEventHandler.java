package com.example.testadsmultiact;

public interface ActAdsEventHandler {

    void InitAds();
    void stopAds();
    default void setAdsByConnectionStatus(boolean bIsShowAds){
        if(bIsShowAds){
            InitAds();
        }
        else{
            stopAds();
        }
    }
}
