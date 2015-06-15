package com.miaotu.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.miaotu.R;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.CustomTour;
import com.miaotu.util.LogUtil;
import com.miaotu.util.MD5;
import com.pingplusplus.android.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class CustomTourDetailActivity extends BaseActivity {
private WebView webView;
    private TextView tvLeft,tvTitle;
    Handler mHandler = new Handler();
    private String orderId,uid,nickname,headUrl,groupId,groupName;
    private boolean isPay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_tour);
        webView = (WebView) findViewById(R.id.webview);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("线路详情");
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPay){
                    webView.loadUrl("http://m.miaotu.com/App/detail/?aid=" + getIntent().getStringExtra("id")+"&token="+readPreference("token"));
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.clearHistory();
                        }
                    }, 1000);
                    isPay=false;
                }else if(webView .canGoBack()){
                    webView.goBack();
                }
            }
        });
        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "native");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });


                webView.loadUrl("http://m.miaotu.com/App/detail/?aid=" + getIntent().getStringExtra("id")+"&token="+readPreference("token"));
    }
    /**
     * js调用java的接口
     * @author ying
     *
     */
    public final class JSInterface {
        //JavaScript脚本代码可以调用的函数onClick()处理
        @android.webkit.JavascriptInterface
        public void click(String action) {
            showToastMsg("点击了"+action);
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
        public String getToken() {
            return readPreference("token");
        }
        @android.webkit.JavascriptInterface
        public String getLuckyMoney() {
            return readPreference("luckmoney");
        }
        @android.webkit.JavascriptInterface
        public void chat(String uid,String name,String headphoto) {
            //私聊
            if(uid.equals(readPreference("uid"))){
                showToastMsg("不能和自己聊天！");
                return ;
            }
            Intent chatIntent = new Intent(CustomTourDetailActivity.this, ChatsActivity.class);
            chatIntent.putExtra("chatType", ChatsActivity.CHATTYPE_SINGLE);
            chatIntent.putExtra("id", MD5.md5(uid));
            chatIntent.putExtra("uid", uid);
            chatIntent.putExtra("name", name);
            chatIntent.putExtra("headphoto", headphoto);
            startActivity(chatIntent);
        }
        @android.webkit.JavascriptInterface
        public void chat() {
            //私聊
            mHandler.post(new Runnable() {

                public void run() {

                    // Code in here
                    if (uid.equals(readPreference("uid"))) {
                        showToastMsg("不能和自己聊天！");
                        return;
                    }
                    Intent chatIntent = new Intent(CustomTourDetailActivity.this, ChatsActivity.class);
                    chatIntent.putExtra("chatType", ChatsActivity.CHATTYPE_SINGLE);
                    chatIntent.putExtra("id", MD5.md5(uid));
                    chatIntent.putExtra("uid", uid);
                    chatIntent.putExtra("name", nickname);
                    chatIntent.putExtra("headphoto", headUrl);
                    startActivity(chatIntent);
                }

            });

        }
        @android.webkit.JavascriptInterface
        public void groupChat(String groupId,String groupName) {
            //群聊

            Intent groupChatIntent = new Intent(CustomTourDetailActivity.this, ChatsActivity.class);
            groupChatIntent.putExtra("groupImId", groupId);
            groupChatIntent.putExtra("groupName", groupName);
            groupChatIntent.putExtra("chatType", 2);
            startActivity(groupChatIntent);
        }
        @android.webkit.JavascriptInterface
        public void groupChat() {
            //群聊
            mHandler.post(new Runnable() {

                public void run() {

                    // Code in here
                    Intent groupChatIntent = new Intent(CustomTourDetailActivity.this, ChatsActivity.class);
                    groupChatIntent.putExtra("groupImId", groupId);
                    groupChatIntent.putExtra("groupName", groupName);
                    groupChatIntent.putExtra("chatType", 2);
                    startActivity(groupChatIntent);
                }

            });

        }
        @android.webkit.JavascriptInterface
        public void like(boolean isLike) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });
            if(isLike){
                //喜欢成功
                setResult(1003);
            }else{
                //取消喜欢成功
               setResult(1004);
            }
        }
        @android.webkit.JavascriptInterface
        public void enterUser(String uid) {
            // 个人中心
            Intent userIntent = new Intent(CustomTourDetailActivity.this,PersonCenterActivity.class);
            userIntent.putExtra("uid", uid);
            startActivity(userIntent);
        }
        @android.webkit.JavascriptInterface
        public void pay(String orderId,String uid,String nickname,String headUrl,String groupId,String groupName) {
            // 支付
            CustomTourDetailActivity.this.uid = uid;
            CustomTourDetailActivity.this.orderId = orderId;
            CustomTourDetailActivity.this.nickname = nickname;
            CustomTourDetailActivity.this.headUrl = headUrl;
            CustomTourDetailActivity.this.groupId = groupId;
            CustomTourDetailActivity.this.groupName = groupName;
            payOrder();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isPay){
            webView.loadUrl("http://m.miaotu.com/App/detail/?aid=" + getIntent().getStringExtra("id")+"&token="+readPreference("token"));
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
    private void payOrder() {
        new BaseHttpAsyncTask<Void, Void, String>(this, true) {
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
                return HttpRequestUtil.getInstance().payOrder("alipay",orderId,readPreference("token"));
            }
        }.execute();
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
                    showToastMsg("付款完成！");
//                    webView.loadUrl("http://m.miaotu.com/App/joinRes?uid="+uid+"&nickname="+nickname+"&headurl="+headUrl+"&gid="+groupId+"&groupname"+groupName);
                    webView.loadUrl("http://m.miaotu.com/App/joinRes");
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.clearHistory();
                        }
                    }, 1000);
                    isPay=true;
                }else{
                    Toast.makeText(this, "付款未完成", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "付款未完成", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "An invalid Credential was submitted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
