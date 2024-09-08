package com.geekofia.chokidar.ui.auth;

import static com.geekofia.chokidar.utils.Utils.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.data.models.ApiResponse;
import com.geekofia.chokidar.data.models.LoginRequest;
import com.geekofia.chokidar.data.models.QRData;
import com.geekofia.chokidar.data.repositories.DeviceRepository;
import com.geekofia.chokidar.databinding.ActivityAuthBinding;
import com.geekofia.chokidar.network.ApiResponseCallback;
import com.geekofia.chokidar.ui.common.MainActivity;
import com.geekofia.chokidar.utils.SecurePrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AuthActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    PreviewView previewView;
    ImageAnalysis imageAnalysis;
    ProcessCameraProvider cameraProvider;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        previewView = binding.previewView;
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);

        binding.scanQrCodeButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                startCamera();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }

    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageAnalysis =
                new ImageAnalysis.Builder()
                        //.setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        //.setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE
                ).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(barcodeScannerOptions);

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageProxy -> {
            @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
            Image mediaImage = imageProxy.getImage();

            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                // Crop the image to the scanning area
                // Rect scanningRect = getScanningRect();

                Task<List<Barcode>> result = scanner.process(image);

                result
                        .addOnSuccessListener(this::processBarcodes)
                        .addOnFailureListener(e -> showToast(getApplicationContext(), "Could not detect barcode!"))
                        .addOnCompleteListener(task -> {
                            mediaImage.close();
                            imageProxy.close();
                        });
            }

        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }

    private Rect getScanningRect() {
        View scanningArea = binding.scanningArea;
        int[] location = new int[2];
        scanningArea.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int size = (int) (scanningArea.getWidth() * getResources().getDisplayMetrics().density);
        return new Rect(left, top, left + size, top + size);
    }


    private void processBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String rawValue = barcode.getRawValue();

            if (rawValue != null) {
                Gson gson = new Gson();
                // Convert the JSON string to a QRData object
                QRData qrData = gson.fromJson(rawValue, QRData.class);

                // Unbind and stop the camera
                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                }
                imageAnalysis.clearAnalyzer();
                authenticateDevice(qrData.getDeviceId(), qrData.getDevicePin());
            }
        }
    }

    private void authenticateDevice(String deviceId, String pin) {
        binding.cameraContainer.setVisibility(View.GONE);

        // Implement your backend authentication API call here
        DeviceRepository deviceRepository = new DeviceRepository();

        deviceRepository.authenticateDevice(
                new LoginRequest(deviceId, pin),
                new ApiResponseCallback<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        // On successful authentication, store the credentials and proceed to the home activity
                        SecurePrefs securePrefs = new SecurePrefs();
                        securePrefs.setPin(getApplicationContext(), pin);
                        securePrefs.setFirstLaunch(getApplicationContext(), false);
                        // Navigate to Home
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        binding.cameraContainer.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
        }
    }
}
