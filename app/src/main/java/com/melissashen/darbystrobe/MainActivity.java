package com.melissashen.darbystrobe;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private int progress = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CameraManager cam = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        final String[] cameraList;
        SeekBar seekBar=(SeekBar)findViewById(R.id.SeekFreq);
        final TextView progressText=(TextView)findViewById(R.id.progressText);
        progressText.setText("Light on for: " + progress + "ms");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = (progressValue+10);
                progressText.setText("Light on for: " + progress + "ms");

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressText.setText("Light on for: " + progress + "ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progressText.setText("Light on for: " + progress + "ms");
            }
        });
        try {
            cameraList = cam.getCameraIdList();
            final String cameraID = cameraList[0];
            final ToggleButton toggler = (ToggleButton) findViewById(R.id.toggleButton);
            final Handler strobeHandler = new Handler();
            final Runnable offRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        cam.setTorchMode(cameraID, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            };
            final Runnable runner = new Runnable() {
                @Override
                public void run() {
                    try {
                        cam.setTorchMode(cameraID, true);
                        strobeHandler.postDelayed(offRunner, progress);
                        strobeHandler.postDelayed(this, progress * 2);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            };
            strobeHandler.removeCallbacks(offRunner);
            toggler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    strobeHandler.removeCallbacks(runner);
                    if (!toggler.isChecked()) {
                        try {
                            cam.setTorchMode(cameraID, false);
                            strobeHandler.removeCallbacksAndMessages(runner);
                            strobeHandler.removeCallbacksAndMessages(offRunner);
                            cam.setTorchMode(cameraID,false);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        strobeHandler.post(runner);
                    }
                }
            });
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}