package no.appsonite.gpsping.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import no.appsonite.gpsping.R;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 28.01.2016
 */
public class MapTargetView extends View {
    private Bitmap targetBitmap;
    private Paint bitmapPaint = new Paint();
    private Paint linePaint = new Paint();
    private Path line = new Path();
    private Point startPoint = new Point(0, 0);
    private boolean hideLine = false;
    private int markerSize = 0;


    public MapTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        targetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_target);
        bitmapPaint.setAntiAlias(true);
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        linePaint.setStrokeWidth(8);
        markerSize = BitmapFactory.decodeResource(getResources(), R.drawable.ic_ellipse).getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(targetBitmap, getWidth() / 2 - targetBitmap.getWidth() / 2, getHeight() / 2 - targetBitmap.getHeight() / 2, bitmapPaint);
        line.reset();
        line.moveTo(getWidth() / 2, getHeight() / 2);
        line.lineTo(startPoint.x, startPoint.y);
        line.close();
        if (!hideLine) {
            canvas.drawPath(line, linePaint);
        }
    }

    public void setLineStart(Point point) {
        this.startPoint = point;
        this.startPoint.y -= markerSize / 2;
        invalidate();
    }

    public void hideLine() {
        hideLine = true;
        invalidate();
    }

    public void showLine() {
        hideLine = false;
        init();
    }
}
