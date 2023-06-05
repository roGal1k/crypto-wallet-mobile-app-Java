//package com.example.cuttlewallet;
//
//import static android.hardware.biometrics.BiometricManager.Authenticators.*;
//
//import android.os.Bundle;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.hardware.biometrics.*;
//
//public class BioMetricActivity extends AppCompatActivity {
//
//    Button btn_fp,btn_fppin;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bio_metric);
//
//        //assign button reference from view
//        btn_fp = findViewById(R.id.btn_fp);
//        btn_fppin  = findViewById(R.id.btn_fppin);
//
//        //create new method to check whether support or not
//        checkBioMetricSupported();
//    }
//
//    void checkBioMetricSupported()
//    {
//        BiometricManager manager = new BiometricManager.from(this);
//        String info="";
//        switch (manager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG))
//        {
//            case BiometricManager.BIOMETRIC_SUCCESS:
//                info = "App can authenticate using biometrics.";
//                enableButton(true);
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                info = "No biometric features available on this device.";
//                enableButton(false);
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                info = "Biometric features are currently unavailable.";
//                enableButton(false);
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                info = "Need register at least one finger print";
//                enableButton(false,true);
//                break;
//            default:
//                info= "Unknown cause";
//                enableButton(false);
//        }
//
//        //set message to text view so we can see what happen with sensor device
//        TextView txinfo =  findViewById(R.id.tx_info);
//        txinfo.setText(info);
//    }
//}