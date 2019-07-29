package no.appsonite.gpsping.widget.targetView;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.view.TextureView;

/**
 * Created by taras on 11/14/17.
 */

public abstract class RenderingThread extends Thread {
    protected final TextureView surface;
    protected volatile boolean running = true;

    public RenderingThread(TextureView surface) {
        this.surface = surface;
    }

    void stopRendering() throws InterruptedException {
        interrupt();
        running = false;
        join();
    }

    protected boolean autoClear() {
        return true;
    }

    @Override
    public void run() {
        while (running && !Thread.interrupted()) {
            final Canvas canvas = surface.lockCanvas(null);
            if (autoClear()) {
                canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            }
            draw(canvas);
            surface.unlockCanvasAndPost(canvas);
        }
    }

    protected abstract void draw(Canvas canvas);
}