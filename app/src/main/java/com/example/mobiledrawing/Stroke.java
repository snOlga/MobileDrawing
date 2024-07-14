package com.example.mobiledrawing;

import android.graphics.Paint;
import android.graphics.Path;

public class Stroke {
    public boolean isNew;
    public Path path;
    public Paint brush;

    public Stroke(boolean isNew, Path path, Paint brush) {
        this.isNew = isNew;
        this.path = path;
        this.brush = brush;
    }
}
