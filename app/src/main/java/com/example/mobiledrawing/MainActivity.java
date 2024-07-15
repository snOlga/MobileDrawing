package com.example.mobiledrawing;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.ViewGroup.LayoutParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SeekBar colorSeekBar = (SeekBar) this.findViewById(R.id.colorChange);
        ColorPicker colorPicker = (ColorPicker) this.findViewById(R.id.gradient);
        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float color = ((float) progress / 100 * 400);
                colorPicker.setHue(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar brushSizeSeekBar = (SeekBar) this.findViewById(R.id.brushSize);
        brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                StaticTool.brushSize = progress * 10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        LayoutParams params = new LayoutParams(width, height);
        this.findViewById(R.id.drawView).setLayoutParams(params);
        this.findViewById(R.id.drawView).setForegroundGravity(Gravity.CENTER);
    }

    public void revertStroke(View v) {
        ((DrawView) this.findViewById(R.id.drawView)).revertStroke();
    }

    public void saveDrawing(View view) {
        DrawView workZone = (DrawView) this.findViewById(R.id.drawView);
        ArrayList<Stroke> strokes = workZone.getStrokes();
        Bitmap bitmap = Bitmap.createBitmap(workZone.getMeasuredWidth(), workZone.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.brush);
        }

        File imageFile = new File(getApplicationContext().getFilesDir(), "image.png");

        try {
            imageFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEraser(View v) {
        StaticTool.setBrushID(0);
    }

    public void setBrush(View v) {
        StaticTool.setBrushID(1);
    }
}