package com.miaotu.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miaotu.R;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.util.LogUtil;
import com.miaotu.util.Util;
import com.pingplusplus.android.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OrderListActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvLeft,tvTitle;
    private WebView webView;
    private LinearLayout llWebview;
    private boolean isPay=false;
    private PopupWindow popupWindow;
    private View view, topBar;
    private TextView tvCancel,tvWxPay,tvAliPay;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        initView();
        bindView();
        initData();
    }

    private void initView(){
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        webView = (WebView) findViewById(R.id.webview);
        llWebview = (LinearLayout) findViewById(R.id.ll_webview);
        topBar = findViewById(R.id.top_bar);
    }

    private void bindView(){
        tvLeft.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("我的订单");
        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "native");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("http://m.miaotu.com/AppTest/order?token="+readPreference("token"));
    }

    /**
     * js调用java的接口
     * @author ying
     *
     */
    public final class JSInterface {
        @android.webkit.JavascriptInterface
        public void callPay(final String orderId){
            // 支付
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showPayWindow(orderId);
                }
            });
        }

        @android.webkit.JavascriptInterface
        public void contactMt(final String number){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent1 = new Intent();
                    intent1.setAction("android.intent.action.DIAL");
                    intent1.setData(Uri.parse("tel:"+number));
                    startActivity(intent1);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_left:
                if(isPay){
                    webView.loadUrl("http://m.miaotu.com/AppTest/order?token="+readPreference("token"));
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.clearHistory();
                        }
                    }, 1000);
                    isPay=false;
                }else if(webView .canGoBack()){
                    webView.goBack();
                }else {
                    finish();
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isPay){
            webView.loadUrl("http://m.miaotu.com/AppTest/order?token="+readPreference("token"));
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.clearHistory();
                }
            }, 1000);
            isPay=false;
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) &&   webView .canGoBack()&&!isPay) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //支付页面返回处理
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");

                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                if(result.equals("success")){
                    //支付成
                    OrderListActivity.this.showMyToast("付款完成！");
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.reload();
                        }
                    });
//                    webView.loadUrl("http://m.miaotu.com/App/joinRes/?uid=" + uid + "&nickname=" + nickname + "&headurl=" + headUrl + "&gid=" + groupId + "&groupname=" + groupName + "&remark=" + remark);
////                    webView.loadUrl("http://m.miaotu.com/App/joinRes");
//                    webView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            webView.clearHistory();
//                        }
//                    }, 1000);
                    isPay=true;
                }else{
                    OrderListActivity.this.showMyToast("付款未完成");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                OrderListActivity.this.showMyToast("付款未完成");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                OrderListActivity.this.showMyToast("An invalid Credential was submitted.");
            }
        }
    }

    private void showPayWindow(final String orderId){
        if (popupWindow == null){
            LayoutInflater inflater = LayoutInflater.from(this);
            view = inflater.inflate(R.layout.pop_pay_option, null);
            tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
            tvWxPay = (TextView) view.findViewById(R.id.tv_wxpay);
            tvAliPay = (TextView) view.findViewById(R.id.tv_alipay);
            popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing()) {
//                    changeBackground(1.0f);
                    popupWindow.dismiss();
                }
            }
        });
        tvAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderListActivity.this.showMyToast("支付宝支付");
                payOrder(orderId, "alipay");
                if (popupWindow.isShowing()) {
//                    changeBackground(1.0f);
                    popupWindow.dismiss();
                }
            }
        });
        tvWxPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderListActivity.this.showMyToast("微信支付");
                payOrder(orderId, "wx");
                if (popupWindow.isShowing()) {
//                    changeBackground(1.0f);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setOutsideTouchable(true);
        changeBackground(0.2f);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.background));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeBackground(1.0f);
            }
        });
        popupWindow.showAtLocation(webView, Gravity.BOTTOM, 0, 0);
    }

    /**
     *
     * @param orderId
     * @param mode alipay/wx
     */
    private void payOrder(final String orderId, final String mode){
        new BaseHttpAsyncTask<Void, Void, String>(OrderListActivity.this, true) {
            @Override
            protected void onCompleteTask(String result) {
                if(tvTitle==null){
                    return;
                }
                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jb;
                String err="";
                String data="";
                try {
                    jb = (JSONObject) jsonTokener.nextValue();
                    err = jb.getString("Err");
                    data = jb.getString("Items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(err.equals("0")){
                    LogUtil.d("data", data);
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                    intent.setComponent(componentName);
                    Log.d("data", data);
                    intent.putExtra(PaymentActivity.EXTRA_CHARGE,data);
                    startActivityForResult(intent, 1);
                }
            }

            @Override
            protected String run(Void... params) {
                LogUtil.d("orderid",orderId);
                return HttpRequestUtil.getInstance().payOrder(mode, orderId, readPreference("token"));
            }
        }.execute();
    }

    //改变背景透明度
    private void changeBackground(float value){
        topBar.setAlpha(value);
        webView.setAlpha(value);
    }
}
