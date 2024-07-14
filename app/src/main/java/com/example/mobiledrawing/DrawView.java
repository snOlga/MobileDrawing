package com.example.mobiledrawing;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.util.Pair;
import android.widget.Button;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DrawView extends View {
    private Path path = new Path();
    private float strokeWidth = StaticTool.brushSize;
    private ArrayList<Stroke> strokes = new ArrayList<>();
    private float strokeBefore = StaticTool.brushSize;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        strokeWidth = StaticTool.brushSize * (event.getPressure());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                strokes.add(initStroke(x, y, true));
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                if (strokeBefore != strokeWidth)
                    strokes.add(initStroke(x, y, false));
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        invalidate();
        strokeBefore = strokeWidth;

        return false;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.brush);
        }
    }

    private Stroke initStroke(float x, float y, boolean isNew) {
        StaticTool.brush = new Paint();
        StaticTool.brush.setColor(StaticTool.color);
        StaticTool.brush.setStrokeJoin(Paint.Join.ROUND);
        StaticTool.brush.setStyle(Paint.Style.STROKE);
        StaticTool.brush.setStrokeWidth(strokeWidth);
        StaticTool.brush.setStrokeCap(Paint.Cap.ROUND);

        path = new Path();

        path.moveTo(x, y);
        return (new Stroke(isNew, path, StaticTool.brush));
    }

    public void revertStroke() {
        int journalSize = strokes.size();
        if (journalSize == 0)
            return;

        while (strokes.get(journalSize - 1).isNew == false) {
            strokes.remove(journalSize - 1);
            journalSize--;
        }
        strokes.remove(journalSize - 1);

        invalidate();
    }

    public ArrayList<Stroke> getStrokes() {
        return strokes;
    }
}
