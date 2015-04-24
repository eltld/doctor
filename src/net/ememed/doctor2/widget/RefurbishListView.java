package net.ememed.doctor2.widget;

import java.util.Date;

import net.ememed.doctor2.R;
import net.ememed.doctor2.fragment.adapter.MessageAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefurbishListView extends ListView implements OnScrollListener {

	private final static int RELEASE_To_REFRESH = 0;// �������̵�״ֵ̬
	private final static int PULL_To_REFRESH = 1; // ���������ص���ˢ�µ�״ֵ̬
	private final static int REFRESHING = 2;// ����ˢ�µ�״ֵ̬
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
	private final static int RATIO = 3;
	private LayoutInflater inflater;

	// ListViewͷ������ˢ�µĲ���
	private LinearLayout headerView;
	private TextView lvHeaderTipsTv;
	private TextView lvHeaderLastUpdatedTv;
	private ImageView lvHeaderArrowIv;
	private ProgressBar lvHeaderProgressBar;

	// ����ͷ������ˢ�µĲ��ֵĸ߶�
	private int headerContentHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int state;
	private boolean isBack;

	// ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
	private boolean isRecored;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public RefurbishListView(Context context) {
		super(context);
		init(context);
	}

	public RefurbishListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(
				android.R.color.transparent));
		inflater = LayoutInflater.from(context);
		headerView = (LinearLayout) inflater.inflate(R.layout.head, null);
		lvHeaderTipsTv = (TextView) headerView
				.findViewById(R.id.head_tipsTextView);
		lvHeaderLastUpdatedTv = (TextView) headerView
				.findViewById(R.id.head_lastUpdatedTextView);

		lvHeaderArrowIv = (ImageView) headerView
				.findViewById(R.id.head_arrowImageView);
		// ��������ˢ��ͼ�����С�߶ȺͿ��
		lvHeaderArrowIv.setMinimumWidth(70);
		lvHeaderArrowIv.setMinimumHeight(50);

		lvHeaderProgressBar = (ProgressBar) headerView
				.findViewById(R.id.head_progressBar);
		measureView(headerView);
		headerContentHeight = headerView.getMeasuredHeight();
		// �����ڱ߾࣬���þ��붥��Ϊһ�������������ֵĸ߶ȣ����ð�ͷ������
		headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
		// �ػ�һ��
		headerView.invalidate();
		// ������ˢ�µĲ��ּ���ListView�Ķ���
		addHeaderView(headerView, null, false);
		// ���ù��������¼�
		setOnScrollListener(this);

		// ������ת�����¼�
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// һ��ʼ��״̬��������ˢ�����״̬������ΪDONE
		state = DONE;
		// �Ƿ�����ˢ��
		isRefreshable = false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isRecored) {
					isRecored = true;
					startY = (int) ev.getY();// ��ָ����ʱ��¼��ǰλ��
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onLvRefresh();
					}
				}
				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored) {
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// ��֤������padding�Ĺ����У���ǰ��λ��һֱ����head������������б�����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���
					// ��������ȥˢ����
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
						if (((tempY - startY) / RATIO < headerContentHeight)// ���ɿ�ˢ��״̬ת�䵽����ˢ��״̬
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						// һ�����Ƶ�����
						else if (tempY - startY <= 0) {// ���ɿ�ˢ��״̬ת�䵽done״̬
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// ���������Խ���RELEASE_TO_REFRESH��״̬
						if ((tempY - startY) / RATIO >= headerContentHeight) {// ��done��������ˢ��״̬ת�䵽�ɿ�ˢ��
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						// ���Ƶ�����
						else if (tempY - startY <= 0) {// ��DOne��������ˢ��״̬ת�䵽done״̬
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// done״̬��
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// ����headView��size
					if (state == PULL_To_REFRESH) {
						headerView.setPadding(0, -1 * headerContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}
					// ����headView��paddingTop
					if (state == RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO
								- headerContentHeight, 0, 0);
					}

				}
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderTipsTv.setVisibility(View.VISIBLE);
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

			lvHeaderArrowIv.clearAnimation();// �������
			lvHeaderArrowIv.startAnimation(animation);// ��ʼ����Ч��

			lvHeaderTipsTv.setText("松开刷新");
			break;
		case PULL_To_REFRESH:
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderTipsTv.setVisibility(View.VISIBLE);
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������
			if (isBack) {
				isBack = false;
				lvHeaderArrowIv.clearAnimation();
				lvHeaderArrowIv.startAnimation(reverseAnimation);

				lvHeaderTipsTv.setText("下拉刷新");
			} else {
				lvHeaderTipsTv.setText("下拉刷新");
			}
			break;

		case REFRESHING:

			headerView.setPadding(0, 0, 0, 0);
			lvHeaderProgressBar.setVisibility(View.VISIBLE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setVisibility(View.GONE);
			lvHeaderTipsTv.setText("正在刷新...");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
			lvHeaderProgressBar.setVisibility(View.GONE);
			lvHeaderArrowIv.clearAnimation();
			lvHeaderArrowIv.setImageResource(R.drawable.arrow);
			lvHeaderTipsTv.setText("下拉刷新");
			lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			break;
		}
	}

	// �˷���ֱ���հ��������ϵ�һ������ˢ�µ�demo���˴��ǡ����ơ�headView��width�Լ�height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
				params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lvHeaderLastUpdatedTv.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	private void onLvRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void setAdapter(MessageAdapter adapter) {
		lvHeaderLastUpdatedTv.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

}
