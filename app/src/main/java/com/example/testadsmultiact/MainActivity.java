package com.example.testadsmultiact;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.testadsmultiact.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getSimpleName();
    MainActivity mainActivity;
    // Used to load the 'testadsmultiact' library on application startup.
    static {
        System.loadLibrary("testadsmultiact");
    }

    private ActivityMainBinding binding;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClickListener.onClick");
        }
    };
    void GotoConnect(){
        Intent myIntent = new Intent(MainActivity.this, MainActivity2.class);
        myIntent.putExtra("key1", 123); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
    View.OnClickListener onBtnGotoConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GotoConnect();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        binding.button.setOnClickListener(onBtnGotoConnectClickListener);
    }

    /**
     * A native method that is implemented by the 'testadsmultiact' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}