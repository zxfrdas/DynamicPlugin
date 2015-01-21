package com.zt.plugin.brightness;

import iapp.eric.utils.base.Trace;
import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.android.tv.KKPictureManager;
import com.konka.dynamicplugin.plugin.BasePlugin;

public class PluginImpl extends BasePlugin {
	private LinearLayout mBrightnessLayout = null;
	private ImageView mBrightnessInc = null;
	private ImageView mBrightnessDec = null;
	private RoundProgressBar mBrightnessProgress = null;
	private ImageView mBrightnessImg = null;
	private ImageView mBrightnessIcon = null;
	private TextView mBrightnessValue = null;
	private TextView mBrightnessHint = null;

	private short miBacklightValue = 0;
	private Thread mSetBacklightByThread = null;
	private boolean mbSetBacklightByThreadDone = false;
	private KKPictureManager mPictureManager;

	@Override
	public View getPluginView() {
		Trace.Debug("#### getPluginView()");

		final LinearLayout layout = (LinearLayout) inflateRootView(R.layout.main);
		// 亮度调节
		mBrightnessLayout = (LinearLayout) findViewById(layout, R.id.brightness_layout);
		mBrightnessInc = (ImageView) findViewById(layout, R.id.brightness_inc);
		mBrightnessDec = (ImageView) findViewById(layout, R.id.brightness_dec);
		mBrightnessProgress = (RoundProgressBar) findViewById(layout, R.id.brightness_progress);
		mBrightnessImg = (ImageView) findViewById(layout, R.id.brightness_img);
		mBrightnessIcon = (ImageView) findViewById(layout, R.id.brightness_icon);
		mBrightnessValue = (TextView) findViewById(layout, R.id.brightness_value);
		mBrightnessHint = (TextView) findViewById(layout, R.id.brightness_hint);
		
		setLister();
		return layout;
	}

	@Override
	public void onCreate() {
		Trace.Debug("#### setContext()");
		mPictureManager = KKPictureManager.getInstance(getContext());
		miBacklightValue = mPictureManager.getBacklight();
	}

	@Override
	public void onAttach() {

	}

	@Override
	public void onDetach() {

	}

	private void setLister() {
		Trace.Debug("#### setLister()");
		mBrightnessLayout.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Trace.Debug("#### onKey():keyCode = " + keyCode
						+ "; event.getAction() = " + event.getAction());
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						backlightUp();
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
						backlightDown();
					}
				}
				return false;
			}
		});

		mBrightnessLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backlightUp();
			}
		});

		mBrightnessLayout.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean bHasFocus) {
				if (bHasFocus) {
					mBrightnessInc.setVisibility(View.VISIBLE);
					mBrightnessDec.setVisibility(View.VISIBLE);
					mBrightnessProgress.setVisibility(View.VISIBLE);
					mBrightnessImg.setVisibility(View.GONE);
					mBrightnessIcon.setVisibility(View.GONE);
					mBrightnessValue.setVisibility(View.VISIBLE);
					mBrightnessHint.setTextColor(getResources().getColor(
							R.color.control_hint_color_s));
					mBrightnessHint.setTextSize(18);
					miBacklightValue = mPictureManager.getBacklight();
					mBrightnessProgress.setProgress(miBacklightValue);
					mBrightnessValue.setText(miBacklightValue + "");

				} else {
					mBrightnessInc.setVisibility(View.GONE);
					mBrightnessDec.setVisibility(View.GONE);
					mBrightnessProgress.setVisibility(View.GONE);
					mBrightnessImg.setVisibility(View.VISIBLE);
					mBrightnessIcon.setVisibility(View.VISIBLE);
					mBrightnessValue.setVisibility(View.GONE);
					mBrightnessHint.setTextColor(getResources().getColor(
							R.color.control_hint_color_uns));
					mBrightnessHint.setTextSize(15);
				}
			}
		});
	}

	private void backlightUp() {
		if (miBacklightValue >= 100)
			return;
		miBacklightValue = (short) (miBacklightValue + 1);
		miBacklightValue = (miBacklightValue >= 100) ? 100 : miBacklightValue;

		mbSetBacklightByThreadDone = false;
		setBacklightByThread();
		mBrightnessProgress.setProgress(miBacklightValue);
		mBrightnessValue.setText(miBacklightValue + "");
	}

	private void backlightDown() {
		if (miBacklightValue <= 0)
			return;

		miBacklightValue = (short) (miBacklightValue - 1);
		miBacklightValue = (miBacklightValue <= 0) ? 0 : miBacklightValue;

		mbSetBacklightByThreadDone = false;
		setBacklightByThread();
		mBrightnessProgress.setProgress(miBacklightValue);
		mBrightnessValue.setText(miBacklightValue + "");
	}

	private void setBacklightByThread() {
		if (mSetBacklightByThread != null && mSetBacklightByThread.isAlive()) {
			return;
		} else {
			mSetBacklightByThread = new Thread() {
				@SuppressLint("NewApi")
				public void run() {

					while (mbSetBacklightByThreadDone == false) {

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mPictureManager.setBacklight(miBacklightValue);
						mbSetBacklightByThreadDone = true;
					}

				};
			};
			mSetBacklightByThread.start();
		}
	}

}
