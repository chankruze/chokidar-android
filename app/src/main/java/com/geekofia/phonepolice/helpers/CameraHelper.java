package com.geekofia.phonepolice.helpers;

import android.content.Context;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraHelper {
    private static final String TAG = "CameraManager";
    private final Context mContext;
    private ImageCapture imageCapture;
    private OnPictureTakenListener mListener;
    private final LifecycleOwner mLifecycleOwner;

    public interface OnPictureTakenListener {
        void onPictureTaken(String filePath);
    }

    public CameraHelper(Context context, LifecycleOwner lifecycleOwner) {
        this.mContext = context;
        this.mLifecycleOwner = lifecycleOwner;
    }

    public void setOnPictureTakenListener(OnPictureTakenListener listener) {
        mListener = listener;
    }

    public void startCamera(int lensFacing, Runnable onCameraStarted) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(mContext);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build();

                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

                preview.setSurfaceProvider(null);
                cameraProvider.unbindAll();

                if (mLifecycleOwner != null && mLifecycleOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    cameraProvider.bindToLifecycle(
                            mLifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                    );
                    // callback
                    onCameraStarted.run();
                } else {
                    Log.e(TAG, "LifecycleOwner is not active");
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    public void captureRearCamera() {
        // Capture photo from the rear camera first
        startCamera(CameraSelector.LENS_FACING_BACK, this::savePhoto);
    }

    public void captureFrontCamera() {
        // Capture photo from the rear camera first
        startCamera(CameraSelector.LENS_FACING_FRONT, this::savePhoto);
    }

    public void savePhoto() {
        if (imageCapture == null) {
            Log.e(TAG, "ImageCapture is null");
            return;
        }

        File file = new File(mContext.getExternalFilesDir(null), new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputOptions, getExecutor(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "Image saved to " + file.getAbsolutePath());
                if (mListener != null) {
                    mListener.onPictureTaken(file.getAbsolutePath());
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Image capture failed: " + exception.getMessage());
            }
        });
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(mContext);
    }
}
