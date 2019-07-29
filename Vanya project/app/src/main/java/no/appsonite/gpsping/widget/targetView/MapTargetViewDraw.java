package no.appsonite.gpsping.widget.targetView;

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

import no.appsonite.gpsping.R;

/**
 * Created by taras on 11/14/17.
 */

public class MapTargetViewDraw extends AnimatedTextureView {

    private Bitmap targetBitmap;
    private Paint bitmapPaint = new Paint();
    private Paint linePaint = new Paint();
    private Path line = new Path();
    private Point startPoint = new Point(0, 0);
    private int markerSize = 0;

    public MapTargetViewDraw(Context context) {
        this(context, null);
    }

    public MapTargetViewDraw(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapTargetViewDraw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    protected void onTextureDraw(Canvas canvas) {
        canvas.drawBitmap(targetBitmap, getWidth() / 2 - targetBitmap.getWidth() / 2, getHeight() / 2 - targetBitmap.getHeight() / 2, bitmapPaint);
        line.reset();
        line.moveTo(getWidth() / 2, getHeight() / 2);
        line.lineTo(startPoint.x, startPoint.y);
        line.close();
        canvas.drawPath(line, linePaint);
    }

    public void setLineStart(Point point) {
        this.startPoint = point;
        this.startPoint.y -= markerSize / 2;
        invalidate();
    }
}
