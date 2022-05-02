package swu.xl.linkgame.LinkGame.Utils;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * 闪电连线效果
 */
public class CustomPaint {
    public Paint mLighnitngColorPaint = new Paint();// 细线
    public Paint mLighnitngColorPaintBold = new Paint();// 窄阴�?
    public Paint mLighnitngGlowPaint = new Paint();// 粗线
    public Paint mLighnitngGlowPaintBold = new Paint();// 宽阴�?

    public CustomPaint() {
        //抗锯齿
        this.mLighnitngColorPaint.setAntiAlias(false);
        //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        this.mLighnitngColorPaint.setDither(true);
        this.mLighnitngColorPaint.setColor(Color.argb(248, 255, 0, 0));
        this.mLighnitngColorPaint.setStyle(Paint.Style.STROKE);
        this.mLighnitngGlowPaint.set(this.mLighnitngColorPaint);
        this.mLighnitngGlowPaint.setColor(Color.parseColor("#6669FD"));
        this.mLighnitngGlowPaint.setAlpha(235);
        this.mLighnitngColorPaintBold.setAntiAlias(true);
        this.mLighnitngColorPaintBold.setDither(true);
        this.mLighnitngColorPaintBold.setColor(Color.argb(248, 255, 0, 0));
        this.mLighnitngColorPaintBold.setStyle(Paint.Style.STROKE);
        this.mLighnitngGlowPaintBold.set(this.mLighnitngColorPaintBold);
        this.mLighnitngGlowPaintBold.setColor(Color.argb(248, 255, 0, 0));
        this.mLighnitngGlowPaintBold.setAlpha(235);

    }

    // 画细线 阴影
    public void drawLightning(float x1, float y1, float x2, float y2,
                              int paramInt, Canvas paramCanvas) {
        Random localRandom = new Random();
        if (paramInt < localRandom.nextInt(7)) {
            return;
        }
        float x3 = 0, y3 = 0;
        return;

    }

    // 粗线 阴影
    public void drawLightningBold(float x1, float y1, float x2, float y2,
                                  int paramInt, Canvas paramCanvas) {
        Random localRandom = new Random();
        if (paramInt < localRandom.nextInt(7)) {
            paramCanvas.drawLine(x1, y1, x2, y2, mLighnitngColorPaintBold);
            paramCanvas.drawLine(x1, y1, x2, y2, mLighnitngColorPaint);
            paramCanvas.drawLine(x1, y1, x2, y2, mLighnitngGlowPaintBold);
            return;
        }

        float x3 = 0, y3 = 0;
        boolean x3Z = localRandom.nextBoolean();
        if (x3Z) {
            x3 = (float) ((x2 + x1) / 2.0F + ((localRandom.nextInt(8) - 0.5D) * paramInt));
        } else {
            x3 = (float) ((x2 + x1) / 2.0F - ((localRandom.nextInt(8) - 0.5D) * paramInt));
        }

        if (localRandom.nextBoolean()) {
            y3 = (float) ((y2 + y1) / 2.0F + ((localRandom.nextInt(5) - 0.5D) * paramInt));
        } else {
            y3 = (float) ((y2 + y1) / 2.0F - ((localRandom.nextInt(5) - 0.5D) * paramInt));
        }
        drawLightningBold(x1, y1, x3, y3, paramInt / 2, paramCanvas);
        drawLightningBold(x2, y2, x3, y3, paramInt / 2, paramCanvas);
        return;

    }
}
