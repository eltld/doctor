package net.ememed.doctor2.activity;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.Problem;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProblemInfoActivity extends BasicActivity{
	private ProgressBar mProgressBar;
	private WebView wvContent;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.problem_info);
	}
	@Override
	protected void setupView() {
		TextView tvTitle = (TextView) findViewById(R.id.top_title);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
		
		wvContent = (WebView) findViewById(R.id.tv_new_content);
		wvContent.setWebChromeClient(new MyWebChromeClient());

		WebSettings set = wvContent.getSettings();
		
		set.setUserAgentString(set.getUserAgentString()+" ememeddoctor/"+MyApplication.getAppVersion());
		set.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		set.setJavaScriptEnabled(true);
		
		Problem problem = (Problem) getIntent().getSerializableExtra("problem");
		if(problem!=null){
			tvTitle.setText(problem.getTITLE());
			//wvContent.loadDataWithBaseURL("about:blank", problem.getFURL(), "text/html", "utf-8", null);
			wvContent.loadUrl(problem.getFURL());
		}
	}
	
	private class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setProgress(newProgress);
			if (newProgress == 100) {
				mProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

	}
	
	
	public void doClick(View view){
		if (view.getId() == R.id.btn_back) {
			finish();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
