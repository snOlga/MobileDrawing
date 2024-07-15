package com.example.mobiledrawing;

import android.graphics.BlendMode;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class StaticTool {
    public static Paint brush = new Paint();
    public static int color = 0;
    public static float brushSize = 1;

    //TODO: do it with enum
    private static int brushID = 1;

    public static void setBrushID(int brushID) {
        StaticTool.brushID = brushID;
    }

    //TODO: do it using properties files!!
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void initBrushStyle() {
        switch (brushID) {
            case 0:
                StaticTool.brush.setStrokeJoin(Paint.Join.ROUND);
                StaticTool.brush.setStyle(Paint.Style.STROKE);
                StaticTool.brush.setStrokeCap(Paint.Cap.ROUND);
                StaticTool.brush.setBlendMode(BlendMode.CLEAR);
                break;
            case 1:
                StaticTool.brush.setStrokeJoin(Paint.Join.ROUND);
                StaticTool.brush.setStyle(Paint.Style.STROKE);
                StaticTool.brush.setStrokeCap(Paint.Cap.ROUND);
                break;
        }
    }
}
