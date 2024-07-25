package com.example.mobiledrawing;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    Dialog createCanvasDialog;
    EditText xSize, ySize;
    Button confirmCreateCanvas;

    int workzoneWidth = 0;
    int workzoneHeight = 0;

    float toolbarHeight = 0;

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

        workzoneWidth = displayMetrics.widthPixels;
        workzoneHeight = displayMetrics.heightPixels;
        LayoutParams params = new LayoutParams(workzoneWidth, workzoneHeight);
        DrawView workzone = this.findViewById(R.id.drawView);
        workzone.setLayoutParams(params);

        createCanvasDialog = new Dialog(MainActivity.this);
        createCanvasDialog.setContentView(R.layout.create_canvas_dialog);
        createCanvasDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        createCanvasDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.create_canvas_bg));

        xSize = createCanvasDialog.findViewById(R.id.xSize);
        ySize = createCanvasDialog.findViewById(R.id.ySize);
        confirmCreateCanvas = createCanvasDialog.findViewById(R.id.confirmCreateCanvas);

        confirmCreateCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    workzoneHeight = Integer.parseInt(xSize.getText().toString());
                    workzoneWidth = Integer.parseInt(ySize.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    createCanvasDialog.dismiss();
                }

                if (workzoneHeight != 0 && workzoneWidth != 0) {
                    LayoutParams params = new LayoutParams(workzoneWidth, workzoneHeight);
                    workzone.setLayoutParams(params);
                    workzone.clear();
                    workzone.setX(((float) displayMetrics.widthPixels / 2) - (float) workzoneWidth / 2);
                    workzone.setY(((float) displayMetrics.heightPixels / 2) - (float) workzoneHeight / 2);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View closeToolbarButton = findViewById(R.id.closeButton);
        closeToolbarButton.setOnTouchListener(onTouchListenerShowButton);
        View openToolbarButton = findViewById(R.id.openButton);
        openToolbarButton.setOnTouchListener(onTouchListenerShowButton);

        findViewById(R.id.openButton).setVisibility(View.GONE);
    }

    public void revertStroke() {
        ((DrawView) this.findViewById(R.id.drawView)).revertStroke();
    }

    public void saveDrawing() {
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

            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "image", "coolimage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEraser() {
        StaticTool.setBrushID(0);
    }

    public void setBrush() {
        StaticTool.setBrushID(1);
    }

    public void createNewCanvas() {
        createCanvasDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.brushButton) {
            setBrush();
        } else if (id == R.id.eraserButton) {
            setEraser();
        } else if (id == R.id.saveButton) {
            saveDrawing();
        } else if (id == R.id.newDrawingButton) {
            createNewCanvas();
        } else if (id == R.id.revertStrokeButton) {
            revertStroke();
        }
        return true;
    }

    View.OnClickListener onClickListenerShowButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openToolbar(findViewById(R.id.openButton));
        }
    };

    View.OnClickListener onClickListenerCloseButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeToolbar(findViewById(R.id.closeButton));
        }
    };

    View.OnTouchListener onTouchListenerShowButton = new View.OnTouchListener() {
        int eventBefore = MotionEvent.ACTION_UP;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                findViewById(R.id.openButton).setX(motionEvent.getRawX() - (float) view.getMeasuredWidth() / 2);
                findViewById(R.id.closeButton).setX(motionEvent.getRawX() - (float) view.getMeasuredWidth() / 2);
                view.invalidate();
                view.setOnClickListener(null);
            } else if (eventBefore != MotionEvent.ACTION_MOVE) {
                findViewById(R.id.openButton).setOnClickListener(onClickListenerShowButton);
                findViewById(R.id.closeButton).setOnClickListener(onClickListenerCloseButton);
            }
            eventBefore = motionEvent.getAction();
            return false;
        }
    };

    public void openToolbar(View view) {
        findViewById(R.id.toolbar).animate().translationY(0);
        view.setVisibility(View.GONE);
        findViewById(R.id.closeButton).animate().translationY(0).alpha(1);
        view.setTranslationY(0);
        findViewById(R.id.closeButton).setVisibility(View.VISIBLE);
    }

    public void closeToolbar(View view) {
        float toolbarHeight = findViewById(R.id.toolbar).getHeight();
        findViewById(R.id.toolbar).animate().translationY(0 - toolbarHeight);
        view.setVisibility(View.GONE);
        findViewById(R.id.openButton).animate().translationY(0 - toolbarHeight).alpha(1);
        view.setTranslationY(0 - toolbarHeight);
        findViewById(R.id.openButton).setVisibility(View.VISIBLE);
    }
}