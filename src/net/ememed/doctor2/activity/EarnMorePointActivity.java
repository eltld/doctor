package net.ememed.doctor2.activity;

import com.android.volley.Network;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.NetWorkUtils;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class EarnMorePointActivity extends BasicActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_earn_more_point);
		
		TextView top_title=(TextView) findViewById(R.id.top_title);
		top_title.setText("如何赚取更多薏米花");
		
		loadWeb();
		
	}
	
	private void loadWeb(){
		if(NetWorkUtils.detect(this)){
			loading(null);
			WebView webView=(WebView) findViewById(R.id.webview);
			webView.loadUrl("http://www.ememed.net:8004/normal/html5/points_rules");
			webView.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					view.loadUrl(url);
					return true;
				}
			});
			WebSettings settings = webView.getSettings();
			//缓存网页
			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
		}else{
			showToast("没有网络");
		}
		destroyDialog();
	}
	
	public void doClick(View v){
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
	
}
