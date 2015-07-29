package com.miaotu.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.miaotu.R;

public class TogetherCustomerTourActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvLeft, tvTitle;
    private WebView webView;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_together_customer_tour);
        initView();
        bindView();
        initData();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        webView = (WebView) findViewById(R.id.webview);
    }

    private void bindView() {
        tvLeft.setOnClickListener(this);
    }

    private void initData() {
        tvTitle.setText("约游秒旅团");
        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "native");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl("http://m.miaotu.com/AppTest/line?token="+readPreference("token"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                if(webView.canGoBack()){
                    webView.goBack();
                }else {
                    finish();
                }
                break;
        }
    }

    public final class JSInterface {
        //JavaScript脚本代码可以调用的函数onClick()处理
        @android.webkit.JavascriptInterface
        public void showDetail(final String aid){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(TogetherCustomerTourActivity.this,
                            CustomTourDetailActivity.class);
                    intent.putExtra("id", aid);
                    startActivityForResult(intent, 1001);
                }
            });
        }
        @android.webkit.JavascriptInterface
        public void setTitle(final String title) {
            mHandler.post(new Runnable() {

                public void run() {

                    // Code in here
                    tvTitle.setText(title);

                }

            });
        }
        @android.webkit.JavascriptInterface
        public void showTip(final String text) {
            mHandler.post(new Runnable() {

                public void run() {

                    // Code in here
                    showToastMsg(text);

                }

            });
        }

        @android.webkit.JavascriptInterface
        public void backRefresh() {
            mHandler.post(new Runnable() {

                public void run() {

                    webView.reload();
                    if (webView.canGoBack()){
                        webView.goBack();
                    }else {
                        finish();
                    }
                }

            });
        }
    }

}
