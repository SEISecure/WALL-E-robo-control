
package com.atakmap.android.helloworld.widgets;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.atakmap.android.maps.MapView;
import com.atakmap.map.AtakMapView;
import com.atakmap.map.AtakMapView.OnMapViewResizedListener;

public class SeekBarControl_SEI implements OnMapViewResizedListener {

    protected  MapView _mapView;
    protected  Context _context;
    private  SeekBar seekBar;
    protected Subject subject;
    private HideControlAction _hideControlAction;
    protected long timeout;
    private int seekBarID;
    public boolean _showingControl;
    public int dataWeight;
    public boolean initialized;


    public SeekBarControl_SEI() {
        super();

        this._showingControl = false;
        this.dataWeight = 0;
        this.initialized = false;
    }

    public void Initialize(int ID) {
        this.initialized = true;

        _mapView = MapView.getMapView();
        _context = _mapView.getContext();
        this.seekBarID = ID;

        this.seekBar = ((Activity) _context).findViewById(ID);
        this.seekBar.setMax(10);
        this.seekBar.setProgress(0);
        this.timeout = 5000L;

        this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startHideTimer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopHideTimer();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (subject != null)
                    subject.setValue(progress,seekBarID);
            }
        });
    }

    protected void show(long timeout) {
        this.timeout = timeout;
        this.seekBar.setVisibility(View.VISIBLE);
        _mapView.addOnMapViewResizedListener(this);
        onMapViewResized(_mapView);
        resetHideTimer();
    }

    @Override
    public void onMapViewResized(AtakMapView view) {
        view.post(() -> {
            ViewGroup.LayoutParams lp = seekBar.getLayoutParams();
            lp.width = (int) (_mapView.getWidth() * 0.45f);
            seekBar.setLayoutParams(lp);
        });
    }

    protected void setSubject(Subject subject) {
        // notify previous subject that its control is being dismissed and clear
        // the reference
        if (this.subject != null) {
            this.subject.onControlDismissed(seekBarID);
            this.subject = null;
        }
        if (subject != null)
            seekBar.setProgress(subject.getValue(seekBarID));
        this.subject = subject;
    }

    protected void hideControl() {
        stopHideTimer();
        _mapView.removeOnMapViewResizedListener(this);
        this.seekBar.setVisibility(View.GONE);
        if (subject != null) {
            subject.onControlDismissed(seekBarID);
            subject = null;
        }
    }

    protected void stopHideTimer() {
        if (_hideControlAction != null) {
            _hideControlAction.cancel();
            _hideControlAction = null;
        }
    }

    protected void startHideTimer() {
        if (_hideControlAction == null) {
            _hideControlAction = new HideControlAction();
            _mapView.postDelayed(_hideControlAction, timeout);
        }
    }

    protected void resetHideTimer() {
        stopHideTimer();
        startHideTimer();
    }

    class HideControlAction implements Runnable {

        protected boolean _cancelled = false;

        @Override
        public void run() {
            if (!_cancelled) {
                _hideControlAction = null;
                hideControl();
            }
        }

        public void cancel() {
            _cancelled = true;
        }
    }

    /**************************************************************************/

    public synchronized void show(Subject subject, long timeout) {
        setSubject(subject);
        show(timeout);
        seekBar.setProgress(this.dataWeight);
    }

    public synchronized void dismiss() {
        hideControl();
    }

    /**************************************************************************/

    public interface Subject {
        /**
         * Returns the current value, <code>0</code> through <code>100</code>,
         * inclusive.
         */
        int getValue(int ID);

        /**
         * Sets the current value, <code>0</code> through <code>100</code>,
         * inclusive.
         */
        void setValue(int value, int ID);

        /**
         * Invoked when the control is dismissed or otherwise switches to a new
         * subject.
         */
        void onControlDismissed(int ID);
    }
}
