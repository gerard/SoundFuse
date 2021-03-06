/*******************************************************************************

   Copyright: 2011 Android Aalto Community

   This file is part of SoundFused.

   SoundFused is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   SoundFused is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with SoundFused; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 ******************************************************************************/
package org.androidaalto.soundfused;

import org.androidaalto.soundfused.sequencer.Sequencer.OnBPMListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.view.View;

public class ProgressBarView extends View implements OnBPMListener {

    private static final int BAR_WIDTH = 25;

    private static final int PROGRESS_SIZE = 15;

    ShapeDrawable progressBar;

    ProgressBarView thisView = this;

    Handler progressHandler;

    // All the sizes are in pixels
    int totalWidth, barHeight, currentBarXPos, beatLength;

    private static int barColor = Color.YELLOW;

    private static final int BAR_TRANSPARENCY = 200;

    long updateDelay, beatTime;
    
    boolean visible;

    Runnable progressRunnable = new Runnable() {

        @Override
        public void run() {
            thisView.invalidate();
            moveBar();
        }
    };

    /**
     * @param context
     * @param width sequencer board total width
     * @param height sequencer board total height
     * @param beatLength beat length in pixels
     */
    public ProgressBarView(Context context, int width, int height, int beatLength, int bpm) {
        super(context);
        progressHandler = new Handler();

        barHeight = height;
        totalWidth = width;
        currentBarXPos = 0;
        visible = true;

        // How long it takes to go through a beat
        this.beatTime = (60 * 1000) / bpm;
        this.beatLength = beatLength;

        int nCallsToGoThroughABeat = beatLength / PROGRESS_SIZE;
        this.updateDelay = beatTime / nCallsToGoThroughABeat;

        progressBar = new ShapeDrawable(new RectShape());
        progressBar.getPaint().setColor(barColor);
        progressBar.setAlpha(BAR_TRANSPARENCY);
        moveBar();
    }

    private void moveBar() {
        synchronized (progressBar) {
            currentBarXPos = (currentBarXPos + PROGRESS_SIZE) % totalWidth;
            progressBar.setBounds(currentBarXPos - BAR_WIDTH, 0, currentBarXPos, barHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (progressBar) {
            if (visible) {
            	progressBar.draw(canvas);
            }
        }
    }

    @Override
    public void onBPM(int beatCount) {
        currentBarXPos = beatCount * beatLength;
        
        progressHandler.removeCallbacks(progressRunnable);
        for (int i = 0; i < beatTime; i += updateDelay) {
        	progressHandler.postDelayed(progressRunnable, i);
        }
    }
    
    public void toggle() {
    	visible = !visible;
    	progressHandler.removeCallbacks(progressRunnable);
    	thisView.invalidate();
    }
}
