package no.appsonite.gpsping.widget.targetView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by taras on 11/14/17.
 */

public abstract class AnimatedTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    RenderingThread renderingThread;

    public AnimatedTextureView(Context context) {
        this(context, null);
    }

    public AnimatedTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextureView();
    }

    private void initTextureView() {
        setSurfaceTextureListener(this);
        setOpaque(false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        renderingThread = new AnimationThread(this);
        renderingThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        try {
            renderingThread.stopRendering();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    protected boolean isAutoClear() {
        return true;
    }

    private class AnimationThread extends RenderingThread {
        public AnimationThread(AnimatedTextureView animatedTextureView) {
            super(animatedTextureView);
        }

        @Override
        protected boolean autoClear() {
            return isAutoClear();
        }

        @Override
        protected void draw(Canvas canvas) {
            onTextureDraw(canvas);
        }
    }

    protected abstract void onTextureDraw(Canvas canvas);
}
