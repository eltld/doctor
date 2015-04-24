package net.ememed.doctor2.finace;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.ShareEnd;

public class FinaceWeb2Activity extends BasicActivity implements OnRefreshListener {
	protected static final int MSG_404 = 1;
	private WebView wb;
	private ProgressBar progressBar;
	private TextView tvText;
	private Button btn;
	private String firstUrl = "http://www.ememed.net:8004/normal/html5/apply_cash_message";
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_webview);
	}
	
	public void onEvent(ShareEnd event) {
		destroyDialog();
	}
	
	@Override
	protected void setupView() {
		wb = (WebView) findViewById(R.id.wv_oauth_normal);
		
		WebSettings set = wb.getSettings();
		set.setUserAgentString(set.getUserAgentString()+" ememeddoctor/"+MyApplication.getAppVersion());
		set.setJavaScriptEnabled(true);
		
		progressBar = (ProgressBar) findViewById(R.id.webview_progress_normal);
		tvText = (TextView) findViewById(R.id.top_title);
		tvText.setText("关于提现");
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Utils.startActivity(FinaceWeb2Activity.this, FinaceBaseActivity.class);
				startActivity(new Intent(FinaceWeb2Activity.this, BankcardListActivity.class));
			}
		});
		wb.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				// If the PullToRefreshAttacher is refreshing, make it as
				// complete
				
			}

			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Logger.dout(TAG + " onReceivedError ");
				view.stopLoading();
				view.clearView();
				Message msg = handler.obtainMessage();// 发送通知，加入线程
				msg.what = MSG_404;// 通知加载自定义404页面
				handler.sendMessage(msg);// 通知发送！
			}
		});

		// 处理网页中的一些对话框信息和加载进度（提示对话框，带选择的对话框，带输入的对话框）,未完成
		wb.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				// Log.d(TAG, "onJsAlert");
				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
				// Log.d(TAG, "onJsConfirm");
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
				// Log.d(TAG, "onJsPrompt");
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setProgress(newProgress);
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
				}
			}
		});
		loadUrl(firstUrl);
	}
	
	private void loadUrl(String url) {
		if (NetWorkUtils.detect(FinaceWeb2Activity.this)) {
			wb.loadUrl(url);
		} else {
			progressBar.setVisibility(View.GONE);
			Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		wb.setVisibility(View.VISIBLE);
		wb.reload();
	}
	
	@Override
	protected void onResult(Message msg) {
		destroyDialog();
		super.onResult(msg);
		if (msg.what == MSG_404) {
			wb.setVisibility(View.GONE);
		}
	}
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		}
	}
}
