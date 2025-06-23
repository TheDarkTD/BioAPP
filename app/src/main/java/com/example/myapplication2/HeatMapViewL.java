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

public class HeatMapViewL extends View {

    private Bitmap footBitmap;
    private List<SensorRegionL> regions = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HeatMapViewL(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        footBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.footleftt);
    }


    public void setRegions(List<SensorRegionL> regs) {
        regions = regs;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Desenha a imagem do pé
        canvas.drawBitmap(footBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);

        // Desenha os pontos suavizados
        for (SensorRegionL r : regions) {
            float centerX = r.x * getWidth();
            float centerY = r.y * getHeight();
            float radius = r.radius * getWidth();

            // Define cor do centro baseado na pressão
            int centerColor = getHeatColor(r.pressure);
            int edgeColor = Color.TRANSPARENT;

            RadialGradient gradient = new RadialGradient(
                    centerX, centerY, radius,
                    centerColor, edgeColor,
                    Shader.TileMode.CLAMP
            );

            paint.setShader(gradient);
            paint.setAlpha(180);
            canvas.drawCircle(centerX, centerY, radius, paint);
            paint.setShader(null); // limpa para próximo ponto
        }
    }

    private int getHeatColor(float p) {
        // gradiente de azul (baixo) a vermelho (alto)
        float frac = Math.min(1f, Math.max(0f, p / 100f));
        int r = (int)(frac * 255);
        int b = 255 - r;
        return Color.rgb(r, 0, b);
    }

    public static class SensorRegionL {
        public float x, y, pressure, radius;
        public SensorRegionL(float x, float y, float p, float rad) {
            this.x = x; this.y = y; this.pressure = p; this.radius = rad;
        }
    }
}
