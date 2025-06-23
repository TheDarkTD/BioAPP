package com.example.myapplication2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HeatMapViewR extends View {

    private Bitmap footBitmap;
    private List<SensorRegionR> regions = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public HeatMapViewR(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        footBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.footrightt);
    }

    public void setRegions(List<SensorRegionR> regs) {
        regions = regs;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // desenha o PNG ajustado ao tamanho da view
        canvas.drawBitmap(footBitmap, null, new Rect(0,0,getWidth(),getHeight()), null);

        // desenha o heatmap: círculos com cores baseadas na pressão
        for (SensorRegionR r : regions) {
            paint.setColor(getHeatColor(r.pressure));
            paint.setAlpha(180); // semitransparente
            canvas.drawCircle(r.x * getWidth(), r.y * getHeight(), r.radius * getWidth(), paint);
        }
    }

    private int getHeatColor(float p) {
        // gradiente de azul (baixo) a vermelho (alto)
        float frac = Math.min(1f, Math.max(0f, p / 100f));
        int r = (int)(frac * 255);
        int b = 255 - r;
        return Color.rgb(r, 0, b);
    }

    public static class SensorRegionR {
        public float x, y, pressure, radius;
        public SensorRegionR(float x, float y, float p, float rad) {
            this.x = x; this.y = y; this.pressure = p; this.radius = rad;
        }
    }
}
