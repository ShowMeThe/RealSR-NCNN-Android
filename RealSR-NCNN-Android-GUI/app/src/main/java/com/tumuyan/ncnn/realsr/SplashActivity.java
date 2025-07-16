package com.tumuyan.ncnn.realsr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tumuyan.ncnn.realsr.databinding.ActivityMainBinding;
import com.tumuyan.ncnn.realsr.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, rootView.getPaddingTop(), systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        WorkJobPool.submit(new Runnable() {
            @Override
            public void run() {
                runCopyJob();
            }
        });
    }

    private void runCopyJob(){
        AssetsCopyer.releaseAssetsToCache(this, WorkJobRepository.CHILD_REALSR_DIR_NAME,
                getCacheDir(),
                false,
                new AssetsCopyer.AssetsFileCopyListener() {
                    @Override
                    public void onStart(int max) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progress.setMax(max);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onProgress(int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progress.setProgress(progress,true);
                            }
                        });
                    }

                    @Override
                    public void onEnd() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progress.setProgress( binding.progress.getMax(),true);
                                starGoToMain();
                            }
                        });
                    }
                });
    }

    private void starGoToMain() {
        startActivity(new Intent(this,MainActivity.class));
    }

}