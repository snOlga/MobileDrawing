package com.example.mobiledrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ColorPicker extends View {
    private Paint paint = new Paint();
    private Shader colorAndWhite;
    private Shader black;
    private final float[] color = {1, 1, 1};
    private Bitmap currentBitmap;
    private DrawView workZone;


    public ColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint = new Paint();
        colorAndWhite = new LinearGradient(0, 0, 0, this.getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);

        int rgb = Color.HSVToColor(color);
        black = new LinearGradient(0, 0, this.getMeasuredWidth(), 0, 0xffffffff, rgb, TileMode.CLAMP);

        ComposeShader shader = new ComposeShader(colorAndWhite, black, PorterDuff.Mode.MULTIPLY);
        paint.setShader(shader);

        canvas.drawRect(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight(), paint);
    }

    void setHue(float hue) {
        color[0] = hue;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.setDrawingCacheEnabled(true);
        currentBitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        int pickedColor = currentBitmap.getPixel((int) event.getX(), (int) event.getY());
        StaticTool.color = pickedColor;

        return false;
    }
}