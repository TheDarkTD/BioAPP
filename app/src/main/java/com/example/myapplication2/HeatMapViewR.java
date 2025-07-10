package com.example.myapplication2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
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

        // Desenha a imagem do pé
        canvas.drawBitmap(footBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);

        // Desenha os pontos suavizados com degradê multicolorido
        for (SensorRegionR r : regions) {
            float cx = r.x * getWidth();
            float cy = r.y * getHeight();
            float radius = r.radius * getWidth();

            // Degradê de várias cores: azul -> ciano -> verde -> amarelo -> vermelho -> transparente
            int[] colors = new int[]{
                    Color.BLUE,
                    Color.CYAN,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.RED,
                    Color.TRANSPARENT
            };
            float[] stops = new float[]{ 0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f };

            RadialGradient gradient = new RadialGradient(
                    cx, cy, radius,
                    colors,
                    stops,
                    Shader.TileMode.CLAMP
            );

            paint.setShader(gradient);
            paint.setAlpha(180);
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setShader(null);
        }
    }

    // Opcional: método para gerar cores dinâmicas via HSV
    private int getHeatColor(float p) {
        float frac = Math.min(1f, Math.max(0f, p / 100f));
        float hue = (1f - frac) * 240f; // 240° (azul) a 0° (vermelho)
        return Color.HSVToColor(new float[]{ hue, 1f, 1f });
    }

    public static class SensorRegionR {
        public float x, y, pressure, radius;
        public SensorRegionR(float x, float y, float p, float rad) {
            this.x = x;
            this.y = y;
            this.pressure = p;
            this.radius = rad;
        }
    }
}
