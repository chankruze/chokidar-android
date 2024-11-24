package com.geekofia.chokidar.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.geekofia.chokidar.databinding.ActivityIntruderGalleryBinding;
import com.geekofia.chokidar.data.models.Intruder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntruderGalleryActivity extends AppCompatActivity {
    private IntruderAdapter adapter;
    private List<Intruder> intruderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntruderGalleryBinding binding = ActivityIntruderGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Update toolbar
        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle("Intruder Gallery");
        toolbar.setSubtitle("Captured unauthorized access attempts");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        intruderList = new ArrayList<>();
        adapter = new IntruderAdapter(this, intruderList);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        binding.recyclerView.setAdapter(adapter);

        loadImages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button click
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to previous
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadImages() {
        File dir = new File(Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath());
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        intruderList.add(new Intruder(file.getAbsolutePath(), "Intruder Alert"));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
