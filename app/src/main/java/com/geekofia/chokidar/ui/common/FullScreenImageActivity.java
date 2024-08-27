package com.geekofia.chokidar.ui.common;

import static com.geekofia.chokidar.utils.Utils.showToast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.databinding.ActivityFullScreenImageBinding;

public class FullScreenImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_PATH = "image_path";
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFullScreenImageBinding binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Update toolbar
        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .into(binding.imageViewFullScreen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_full_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_info) {
            showInfoDialog(imagePath);
            return true;
        } else if (itemId == R.id.action_share) {
            shareImage(imagePath);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showInfoDialog(String imagePath) {
        File imageFile = new File(imagePath);
        String fileName = imageFile.getName();
        long fileSize = imageFile.length() / 1024; // Size in KB
        String lastModified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date(imageFile.lastModified()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Image Details");

        String message = "File Name: " + fileName + "\n" +
                "File Path: " + imagePath + "\n" +
                "File Size: " + fileSize + " KB\n" +
                "Last Modified: " + lastModified;

        builder.setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void shareImage(String imagePath) {
        File imageFile = new File(imagePath);
        Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this person who tried to break into my phone!");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share Intruder Image"));
        } catch (Exception e) {
            showToast(this, "No app found to share image.");
        }
    }
}
