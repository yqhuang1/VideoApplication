package com.fmscreenrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.fmscreenrecord.utils.MResource;

public class UseHelpActivity extends Activity implements OnClickListener
{

	private WebView mWebview; 
	private LinearLayout btBack;
	private String HTML = "http://www.17sysj.com/lpds/help.html?from=singlemessage&isappinstalled=1";

	@Override
	protected void onCreate(Bundle savedInstanceState)

	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_use_help_activity"));
		
		btBack = (LinearLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "use_help_imageButton_back"));
		btBack.setOnClickListener(this);

		mWebview = (WebView) findViewById(MResource.getIdByName(getApplication(),
				"id", "use_help_webview"));
		 mWebview.getSettings().setJavaScriptEnabled(true); //设置能够执行Javascript脚本
		// 设置可以支持缩放 
		 mWebview.getSettings().setSupportZoom(true); 
		 // 设置出现缩放工具 
		 mWebview.getSettings().setBuiltInZoomControls(true);
		 //扩大比例的缩放
		 mWebview.getSettings().setUseWideViewPort(true);
		 mWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		 mWebview.getSettings().setLoadWithOverviewMode(true);
		 mWebview.loadUrl(HTML); //加载需要显示的网页     
		 
		 mWebview.setWebViewClient(new HelloWebViewClient ()); 		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mWebview.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mWebview.onResume();
	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == btBack)
		{
			finish();
		}
		
	}
	
	//Web视图 
    private class HelloWebViewClient extends WebViewClient 
    { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); // 使用当前WebView处理跳转
            return true; //true表示此事件在此处被处理，不需要再广播
        } 
    } 
	
	 @Override
	    //设置回退 
	    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法 
	    public boolean onKeyDown(int keyCode, KeyEvent event) { 
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) { 
	            mWebview.goBack(); //goBack()表示返回WebView的上一页面 
	            return true; 
	        } 
	        
	        return super.onKeyDown(keyCode, event); 
	}
}
