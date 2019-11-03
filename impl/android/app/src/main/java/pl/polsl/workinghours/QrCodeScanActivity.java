package pl.polsl.workinghours;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.ui.qrcode.QrCodeModelFactory;
import pl.polsl.workinghours.ui.qrcode.QrCodeViewModel;
import rx.Observer;

public class QrCodeScanActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    QrCodeViewModel qrCodeViewModel;
    String currentCode = "NOTHING";
    int pla = 0;

    public static void startActivity(Activity currentActivity) {
        Intent myIntent = new Intent(currentActivity, QrCodeScanActivity.class);
        currentActivity.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);

        qrCodeViewModel =  ViewModelProviders.of(this, new QrCodeModelFactory(getApplication()))
                .get(QrCodeViewModel.class);


        //BACK ARROW

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //CAMMERA

        surfaceView = findViewById(R.id.camerapreview);
        textView = findViewById(R.id.cameraDescription);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640,480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes =detections.getDetectedItems();
                if (!qrCodes.valueAt(0).displayValue.equals(currentCode)) {
                    currentCode = qrCodes.valueAt(0).displayValue;
                    if (qrCodes.size() != 0) {
                        textView.post(() -> {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            textView.setText(qrCodes.valueAt(0).displayValue + "   " + pla++);
                            sendQrCode(qrCodes.valueAt(0).displayValue);

                        });
                    }
                }
            }

        });
    }

    public void sendQrCode(String code) {
        qrCodeViewModel.postQrCode(code,this).first().subscribe(new Observer<QrCode>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                // nie udało się trzeba się logować ręcznie
            }

            @Override
            public void onNext(QrCode qrCode) {

            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
