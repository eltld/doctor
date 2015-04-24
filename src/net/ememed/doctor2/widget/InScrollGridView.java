package net.ememed.doctor2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import net.ememed.doctor2.R;

public class InScrollGridView extends GridView {
	public InScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InScrollGridView(Context context) {
		super(context);
	}

	public InScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
