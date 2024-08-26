package com.geekofia.phonepolice.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.adapters.IntruderAdapter;
import com.geekofia.phonepolice.databinding.ActivityIntruderGalleryBinding;
import com.geekofia.phonepolice.models.Intruder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private void loadImages() {
        File dir = new File(getExternalFilesDir(null).getAbsolutePath());
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
