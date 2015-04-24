package net.ememed.doctor2.widget;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import net.ememed.doctor2.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author chen
 * 
 */
public class ImageZoomView extends ImageView implements Observer {

	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Rect mRectSrc = new Rect();
	private final Rect mRectDst = new Rect();
	private float mAspectQuotient;

	private Bitmap mBitmap;
	private ZoomState mState;

	private static final String TAG = "PorterDuffView";
	/** 前景Bitmap高度为1像素。采用循环多次填充进度区域。 */
	public static final int FG_HEIGHT = 1;
	/** 下载进度前景色 */
	// public static final int FOREGROUND_COLOR = 0x77123456;
	public static final int FOREGROUND_COLOR = 0x77ff0000;
	/** 下载进度条的颜色。 */
	public static final int TEXT_COLOR = 0xff7fff00;
	/** 进度百分比字体大小。 */
	public static final int FONT_SIZE = 30;
	private Bitmap bitmapBg, bitmapFg;
	private Paint paint;
	/** 标识当前进度。 */
	private float progress;
	/** 标识进度图片的宽度与高度。 */
	private int width, height;
	/** 格式化输出百分比。 */
	private DecimalFormat decFormat;
	/** 进度百分比文本的锚定Y中心坐标值。 */
	private float txtBaseY;
	/** 标识是否使用PorterDuff模式重组界面。 */
	private boolean porterduffMode;
	/** 标识是否正在下载图片。 */
	private boolean loading;

	/** 生成一宽与背景图片等同高为1像素的Bitmap，。 */
	private static Bitmap createForegroundBitmap(int w) {
		Bitmap bm = Bitmap.createBitmap(w, FG_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(FOREGROUND_COLOR);
		c.drawRect(0, 0, w, FG_HEIGHT, p);
		return bm;
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.PorterDuffView);
			porterduffMode = typedArr.getBoolean(R.styleable.PorterDuffView_porterduffMode, false);
		}
		Drawable drawable = getDrawable();
		if (porterduffMode && drawable != null && drawable instanceof BitmapDrawable) {
			bitmapBg = ((BitmapDrawable) drawable).getBitmap();
			width = bitmapBg.getWidth();
			height = bitmapBg.getHeight();
			bitmapFg = createForegroundBitmap(width);
		} else {
			// 不符合要求，自动设置为false。
			porterduffMode = false;
		}

		paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
		paint.setTextSize(FONT_SIZE);

		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		// 注意观察本输出：
		// ascent:单个字符基线以上的推荐间距，为负数
//		Log.i(TAG, "ascent:" + fontMetrics.ascent//
//				// descent:单个字符基线以下的推荐间距，为正数
//				+ " descent:" + fontMetrics.descent //
//				// 单个字符基线以上的最大间距，为负数
//				+ " top:" + fontMetrics.top //
//				// 单个字符基线以下的最大间距，为正数
//				+ " bottom:" + fontMetrics.bottom//
//				// 文本行与行之间的推荐间距
//				+ " leading:" + fontMetrics.leading);
		// 在此处直接计算出来，避免了在onDraw()处的重复计算
		txtBaseY = (height - fontMetrics.bottom - fontMetrics.top) / 2;

		decFormat = new DecimalFormat("0.0%");
	}

	public ImageZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public void setZoomState(ZoomState state) {
		if (mState != null) {
			mState.deleteObserver(this);
		}
		mState = state;
		mState.addObserver(this);
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		if (porterduffMode && bitmapBg != null) {
			int tmpW = (getWidth() - width) / 2, tmpH = (getHeight() - height) / 2;
			// 画出背景图
			canvas.drawBitmap(bitmapBg, tmpW, tmpH, paint);
			// 设置PorterDuff模式
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
			// canvas.drawBitmap(bitmapFg, tmpW, tmpH - progress * height,
			// paint);
			int tH = height - (int) (progress * height);
			for (int i = 0; i < tH; i++) {
				canvas.drawBitmap(bitmapFg, tmpW, tmpH + i, paint);
			}

			// 立即取消xfermode
			paint.setXfermode(null);
			int oriColor = paint.getColor();
			paint.setColor(TEXT_COLOR);
			paint.setTextSize(FONT_SIZE);
			String tmp = decFormat.format(progress);
			float tmpWidth = paint.measureText(tmp);
			canvas.drawText(decFormat.format(progress), tmpW
					+ (width - tmpWidth) / 2, tmpH + txtBaseY, paint);
			// 恢复为初始值时的颜色
			paint.setColor(oriColor);
		} else {
			if (mBitmap != null && mState != null) {
				final int viewWidth = getWidth();
				final int viewHeight = getHeight();
				final int bitmapWidth = mBitmap.getWidth();
				final int bitmapHeight = mBitmap.getHeight();

				final float panX = mState.getPanX();
				final float panY = mState.getPanY();
				final float zoomX = mState.getZoomX(mAspectQuotient)
						* viewWidth / bitmapWidth;
				final float zoomY = mState.getZoomY(mAspectQuotient)
						* viewHeight / bitmapHeight;

				// Setup source and destination rectangles
				mRectSrc.left = (int) (panX * bitmapWidth - viewWidth
						/ (zoomX * 2));
				mRectSrc.top = (int) (panY * bitmapHeight - viewHeight
						/ (zoomY * 2));
				mRectSrc.right = (int) (mRectSrc.left + viewWidth / zoomX);
				mRectSrc.bottom = (int) (mRectSrc.top + viewHeight / zoomY);
				mRectDst.left = getLeft();
				mRectDst.top = getTop();
				mRectDst.right = getRight();
				mRectDst.bottom = getBottom();

				// Adjust source rectangle so that it fits within the source
				// image.
				if (mRectSrc.left < 0) {
					mRectDst.left += -mRectSrc.left * zoomX;
					mRectSrc.left = 0;
				}
				if (mRectSrc.right > bitmapWidth) {
					mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
					mRectSrc.right = bitmapWidth;
				}
				if (mRectSrc.top < 0) {
					mRectDst.top += -mRectSrc.top * zoomY;
					mRectSrc.top = 0;
				}
				if (mRectSrc.bottom > bitmapHeight) {
					mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
					mRectSrc.bottom = bitmapHeight;
				}

				canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
			}
		}
	}

	public void update(Observable observable, Object data) {
		invalidate();
	}

	private void calculateAspectQuotient() {
		if (mBitmap != null) {
			mAspectQuotient = (((float) mBitmap.getWidth()) / mBitmap
					.getHeight()) / (((float) getWidth()) / getHeight());
		}
	}

	public void setImage(Bitmap bitmap) {
		mBitmap = bitmap;
		calculateAspectQuotient();
		invalidate();
	}
	
	public void invalidateImage() {
		initThumbnail();
		invalidate();
		porterduffMode = false;
	}
	
	private void initThumbnail() {
		Drawable drawable = getDrawable();
		porterduffMode = true;
		if (porterduffMode && drawable != null && drawable instanceof BitmapDrawable) {
			bitmapBg = ((BitmapDrawable) drawable).getBitmap();
			width = bitmapBg.getWidth();
			height = bitmapBg.getHeight();
			bitmapFg = createForegroundBitmap(width);
		} else {
			// 不符合要求，自动设置为false。
			porterduffMode = false;
		}

		paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
		paint.setTextSize(FONT_SIZE);

		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		// 注意观察本输出：
		// ascent:单个字符基线以上的推荐间距，为负数
//		Log.i(TAG, "ascent:" + fontMetrics.ascent//
//				// descent:单个字符基线以下的推荐间距，为正数
//				+ " descent:" + fontMetrics.descent //
//				// 单个字符基线以上的最大间距，为负数
//				+ " top:" + fontMetrics.top //
//				// 单个字符基线以下的最大间距，为正数
//				+ " bottom:" + fontMetrics.bottom//
//				// 文本行与行之间的推荐间距
//				+ " leading:" + fontMetrics.leading);
		// 在此处直接计算出来，避免了在onDraw()处的重复计算
		txtBaseY = (height - fontMetrics.bottom - fontMetrics.top) / 2;
		decFormat = new DecimalFormat("0.0%");
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateAspectQuotient();
	}

	public void setProgress(float progress) {
		if (porterduffMode) {
			this.progress = progress;
			// 刷新自身。
			invalidate();
		}
	}

	public void setBitmap(Bitmap bg) {
		if (porterduffMode) {
			bitmapBg = bg;
			width = bitmapBg.getWidth();
			height = bitmapBg.getHeight();

			bitmapFg = createForegroundBitmap(width);

			Paint.FontMetrics fontMetrics = paint.getFontMetrics();
			txtBaseY = (height - fontMetrics.bottom - fontMetrics.top) / 2;

			setImageBitmap(bg);
			// 请求重新布局，将会再次调用onMeasure()
			// requestLayout();
		}
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public void setPorterDuffMode(boolean bool) {
		porterduffMode = bool;
	}

}
