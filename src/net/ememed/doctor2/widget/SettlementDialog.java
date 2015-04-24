package net.ememed.doctor2.widget;

import net.ememed.doctor2.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class SettlementDialog extends Dialog {

	TextView close_bnt;
	TextView content;

	public SettlementDialog(Context context) {
		super(context);
	}

	public SettlementDialog(Context context, int theme) {
		super(context, theme);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialof_settlement);
		close_bnt = (TextView) findViewById(R.id.close_bnt);
		close_bnt.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		content = (TextView) findViewById(R.id.content);
	}

	public void setText(String source) {
		content.setText(Html.fromHtml(source));
	}
	
	public void setParams(int width) {

		Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(  Gravity.CENTER);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        lp.width = width*4/5; // 宽度
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
	}
}
