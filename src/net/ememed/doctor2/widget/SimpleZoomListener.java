package net.ememed.doctor2.widget;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 
 * @author chen
 * 
 */
public class SimpleZoomListener implements View.OnTouchListener {

	// private RelativeLayout topBarView;
	private Animation inAnimation;
	private Animation outAnimation;

	public enum ControlType {
		PAN, ZOOM
	}

	private ZoomState mState;

	private float mX;
	private float mY;
	private float mGap;

	public void setZoomState(ZoomState state) {
		mState = state;
		// topBarView = (RelativeLayout) ((BasicActivity)
		// AppContext.getContext()).findViewById(R.id.qa_bar);
		inAnimation = AnimationUtils.loadAnimation(MyApplication.getContext(),
				R.anim.alaph_bar_in);
		outAnimation = AnimationUtils.loadAnimation(MyApplication.getContext(),
				R.anim.alaph_bar_out);
	}

	@SuppressWarnings("deprecation")
	public boolean onTouch(View v, MotionEvent event) {
		try {

			final int action = event.getAction();
			int pointCount = event.getPointerCount();
			if (pointCount == 1) {
				final float x = event.getX();
				final float y = event.getY();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mX = x;
					mY = y;
					// try {
					// if (topBarView.getVisibility() == View.VISIBLE) {
					// topBarView.startAnimation(outAnimation);
					// topBarView.setVisibility(View.GONE);
					// } else {
					// topBarView.startAnimation(inAnimation);
					// topBarView.setVisibility(View.VISIBLE);
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					break;
				case MotionEvent.ACTION_MOVE: {
					final float dx = (x - mX) / v.getWidth();
					final float dy = (y - mY) / v.getHeight();
					mState.setPanX(mState.getPanX() - dx);
					mState.setPanY(mState.getPanY() - dy);
					mState.notifyObservers();
					mX = x;
					mY = y;
					break;
				}
				}
			}
			if (pointCount == 2) {
				final float x0 = event.getX(event.getPointerId(0));
				final float y0 = event.getY(event.getPointerId(0));

				final float x1 = event.getX(event.getPointerId(1));
				final float y1 = event.getY(event.getPointerId(1));

				final float gap = getGap(x0, x1, y0, y1);
				switch (action) {
				case MotionEvent.ACTION_POINTER_2_DOWN:
				case MotionEvent.ACTION_POINTER_1_DOWN:
					mGap = gap;
					break;
				case MotionEvent.ACTION_POINTER_1_UP:
					mX = x1;
					mY = y1;
					break;
				case MotionEvent.ACTION_POINTER_2_UP:
					mX = x0;
					mY = y0;
					break;
				case MotionEvent.ACTION_MOVE:
					final float dgap = (gap - mGap) / mGap;

					if (mState.getZoom() < 0.5) {
						mState.setZoom(0.5f);
					} else if (mState.getZoom() > 100f) {
						mState.setZoom(98f);
					} else {
						mState.setZoom(mState.getZoom()
								* (float) Math.pow(2, dgap));
					}

					mState.notifyObservers();
					mGap = gap;
					break;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private float getGap(float x0, float x1, float y0, float y1) {
		return (float) Math.pow(
				Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2), 0.5);
	}
}
