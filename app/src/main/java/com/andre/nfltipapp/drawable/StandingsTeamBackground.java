package com.andre.nfltipapp.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class StandingsTeamBackground extends Drawable {

    private final int teamColor;

    public StandingsTeamBackground(int color) {
        this.teamColor = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        float delta = width / 2;

        Paint paint = new Paint();
        paint.setColor(teamColor);
        paint.setAlpha(255);

        Path p = new Path();
        p.moveTo(0,0);
        p.lineTo(delta + 35, 0);
        p.lineTo(delta - 75, getBounds().height());
        p.lineTo(0, getBounds().height());
        p.lineTo(0,0);
        canvas.drawPath(p, paint);

        paint.setAlpha(200);

        Path middle = new Path();
        middle.moveTo(delta + 35, 0);
        middle.lineTo(delta + 75, 0);
        middle.lineTo(delta - 35, getBounds().height());
        middle.lineTo(delta - 75, getBounds().height());
        middle.lineTo(delta + 35, 0);
        canvas.drawPath(middle, paint);

        paint.setAlpha(230);

        Path p1 = new Path();
        p1.moveTo(width,0);
        p1.lineTo(delta + 75, 0);
        p1.lineTo(delta - 35, getBounds().height());
        p1.lineTo(width, getBounds().height());
        p1.lineTo(width,0);
        canvas.drawPath(p1, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
