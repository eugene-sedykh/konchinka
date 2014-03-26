package com.jjjackson.framework.impl;

import android.graphics.Bitmap;
import com.jjjackson.framework.Graphics.PixmapFormat;
import com.jjjackson.framework.Pixmap;

public class AndroidPixmap implements Pixmap {
    Bitmap bitmap;
    PixmapFormat pixmapFormat;

    public AndroidPixmap(Bitmap bitmap, PixmapFormat pixmapFormat) {
        this.bitmap = bitmap;
        this.pixmapFormat = pixmapFormat;
    }

    @Override
    public int getWidth() {
        return this.bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return this.bitmap.getHeight();
    }

    @Override
    public PixmapFormat getFormat() {
        return this.pixmapFormat;
    }

    @Override
    public void dispose() {
        this.bitmap.recycle();
    }
}
