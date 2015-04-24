package net.ememed.doctor2.finace;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;

public class FinaceWebActivity extends BasicActivity {
	private TextView top_title;
	private WebView wb;
	private WebSettings webSettings;
	private Button btn;
	private String firstUrl = "http://www.ememed.net:8004/normal/html5/apply_cash_message";
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_web);
	}

	@Override
	protected void getData() {
		super.getData();
		loading(null);
		wb.loadUrl(firstUrl);	
		destroyDialog();
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Utils.startActivity(FinaceWebActivity.this, FinaceBaseActivity.class);
			}
		});
	}

	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.finace_about_cash));
		wb = (WebView) findViewById(R.id.wv_oauth);
		btn = (Button) findViewById(R.id.btn);
		webSettings = wb.getSettings();
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		//自带缩放
		webSettings.setSupportZoom(true);  
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);		
		webSettings.setUserAgentString(webSettings.getUserAgentString()+" ememeddoctor/"+MyApplication.getAppVersion());
	}
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		}
	}
	
	

}
