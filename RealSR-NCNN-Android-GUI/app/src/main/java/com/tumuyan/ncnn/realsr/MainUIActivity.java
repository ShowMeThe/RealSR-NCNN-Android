package com.tumuyan.ncnn.realsr;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tumuyan.ncnn.realsr.databinding.ActivityMainUiActivityBinding;

public class MainUIActivity extends AppCompatActivity {

    private ActivityMainUiActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainUiActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, rootView.getPaddingTop(), systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });


    }
}