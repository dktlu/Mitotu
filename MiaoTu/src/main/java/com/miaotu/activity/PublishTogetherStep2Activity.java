package com.miaotu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.form.PublishTogether;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.result.BaseResult;
import com.miaotu.result.PublishTogetherResult;
import com.miaotu.util.LogUtil;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;
import com.photoselector.model.PhotoModel;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class PublishTogetherStep2Activity extends BaseActivity implements OnClickListener {
    PublishTogether publishTogether;
    private EditText etComment;
    private TextView tvTitle, tvLeft, tvRight, tvFontCount;
    private RadioGroup rgShare;
    private CheckBox rbSelected;
    private CheckBox rbCircle, rbWechat, rbWeibo, rbZone;
    private boolean isMinus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_together_step2);
        findView();
        bindView();
        init();
    }

    private void findView() {
        etComment = (EditText) findViewById(R.id.et_comment);
        tvFontCount = (TextView) findViewById(R.id.tv_fontcount);
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rgShare = (RadioGroup) findViewById(R.id.rg_share);
        rbCircle = (CheckBox) findViewById(R.id.rb_share_circle);
        rbWechat = (CheckBox) findViewById(R.id.rb_share_wechat);
        rbWeibo = (CheckBox) findViewById(R.id.rb_share_weibo);
        rbZone = (CheckBox) findViewById(R.id.rb_share_zone);
    }

    private void bindView() {
        tvRight.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        rbSelected = rbCircle;
        rbCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (rbSelected == null || rbSelected.getId() == rbCircle.getId()) {
                        rbCircle.setChecked(true);
                        rbSelected = rbCircle;
                    } else {
                        if (rbSelected.getId() != rbCircle.getId()) {
                            rbSelected.setChecked(false);
                            rbCircle.setChecked(true);
                            rbSelected = rbCircle;
                        }
                    }
                } else {
                    if (rbSelected.getId() == rbCircle.getId()) {
                        rbSelected = null;
                        rbCircle.setChecked(false);
                    }
                }
            }
        });
        rbWechat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (rbSelected == null || rbSelected.getId() == rbWechat.getId()) {
                        rbWechat.setChecked(true);
                        rbSelected = rbWechat;
                    } else {
                        if (rbSelected.getId() != rbWechat.getId()) {
                            rbSelected.setChecked(false);
                            rbWechat.setChecked(true);
                            rbSelected = rbWechat;
                        }
                    }
                } else {
                    if (rbSelected.getId() == rbWechat.getId()) {
                        rbSelected = null;
                        rbWechat.setChecked(false);
                    }
                }
            }
        });
        rbWeibo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (rbSelected == null || rbSelected.getId() == rbWeibo.getId()) {
                        rbWeibo.setChecked(true);
                        rbSelected = rbWeibo;
                    } else {
                        if (rbSelected.getId() != rbWeibo.getId()) {
                            rbSelected.setChecked(false);
                            rbWeibo.setChecked(true);
                            rbSelected = rbWeibo;
                        }
                    }
                } else {
                    if (rbSelected.getId() == rbWeibo.getId()) {
                        rbSelected = null;
                        rbWeibo.setChecked(false);
                    }
                }
            }
        });
        rbZone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (rbSelected == null || rbSelected.getId() == rbZone.getId()) {
                        rbZone.setChecked(true);
                        rbSelected = rbZone;
                    } else {
                        if (rbSelected.getId() != rbZone.getId()) {
                            rbSelected.setChecked(false);
                            rbZone.setChecked(true);
                            rbSelected = rbZone;
                        }
                    }
                } else {
                    if (rbSelected.getId() == rbZone.getId()) {
                        rbSelected = null;
                        rbZone.setChecked(false);
                    }
                }
            }
        });

        etComment.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvFontCount.setText(getChineseLength(charSequence.toString()) + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count = getChineseLength(editable.toString() + "");
                isMinus = false;
                if (count > 140) {
//                    tvFontCount.setText(140 - count + "");
                    SpannableStringBuilder style = new SpannableStringBuilder(140 - count + "");
                    style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff8000")), 0, style.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvFontCount.setText(style);
                    isMinus = true;
                }
            }
        });
        etComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    String content = etComment.getText().toString();
                    content += "  ";
                    etComment.setText(content);
                    etComment.setSelection(content.length());
                    return true;
                }
                return false;
            }
        });
    }

    private void init() {
        publishTogether = (PublishTogether) getIntent().getSerializableExtra("publishTogether");
        tvTitle.setText("发起旅行");
//        tvRight.setText("发布");
        SpannableStringBuilder style = new SpannableStringBuilder("发布");
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff8000")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRight.setText(style);
    }

    private void publish() {
        new BaseHttpAsyncTask<Void, Void, PublishTogetherResult>(PublishTogetherStep2Activity.this, true) {
            @Override
            protected void onCompleteTask(PublishTogetherResult result) {
                if (tvRight == null) {
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
                    PublishTogetherStep2Activity.this.showMyToast("发布成功！");
                    String hearurl = "";
                    if (publishTogether.getImg() != null &&
                            publishTogether.getImg().split(",").length > 0) {
                        hearurl = publishTogether.getImg().split(",")[0];
                    }
//                    rbSelected = (RadioButton)findViewById(rgShare.getCheckedRadioButtonId());
                    if (rbSelected != null) {
                        share(rbSelected.getId(), result.getTogether().getId(),
                                result.getTogether().getComment(), hearurl);
                    }
                    Intent togetherIntent = new Intent(PublishTogetherStep2Activity.this,
                            TogetherDetailActivity.class);
                    togetherIntent.putExtra("id", result.getTogether().getId());
                    startActivityForResult(togetherIntent, 1);
                    setResult(1);
                    finish();
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        PublishTogetherStep2Activity.this.showMyToast("发布失败！");
                    } else {
                        PublishTogetherStep2Activity.this.showMyToast(result.getMsg());
                    }
                }
            }

            @Override
            protected PublishTogetherResult run(Void... params) {
                return HttpRequestUtil.getInstance().publishTogether(publishTogether);
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                if (!StringUtil.isBlank(etComment.getText().toString().trim())) {
                    if (isMinus){
                        PublishTogetherStep2Activity.this.showMyToast("字数超限");
                        return;
                    }
                    publishTogether.setRemark(etComment.getText().toString().trim());
                    publishTogether.setToken(readPreference("token"));
                    publish();
                } else {
                    PublishTogetherStep2Activity.this.showMyToast("请输入旅行推荐语！");
                }
                break;
            case R.id.tv_left:
                finish();
        }
    }

    /**
     * 分享
     *
     * @param id
     * @param yid
     * @param remark
     * @param headurl
     */
    private void share(int id, String yid, String remark, String headurl) {
        ShareSDK.initSDK(this);
        switch (id) {
            case R.id.rb_share_circle:
                WechatMoments.ShareParams wmsp = new WechatMoments.ShareParams();
                if (StringUtil.isBlank(headurl)) {
                    wmsp.setShareType(Platform.SHARE_TEXT);
                } else {
                    wmsp.setShareType(Platform.SHARE_WEBPAGE);
                    wmsp.setImageUrl(headurl + "200×200");
                    wmsp.setUrl("http://m.miaotu.com/ShareLine/?yid=" + yid);
                }
                wmsp.setTitle(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                wmsp.setText(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                Platform wechatmoments = ShareSDK.getPlatform(WechatMoments.NAME);
                wechatmoments.setPlatformActionListener(new PlatFormListener());
                wechatmoments.share(wmsp);
                break;
            case R.id.rb_share_wechat:
                Wechat.ShareParams wcsp = new Wechat.ShareParams();
                if (StringUtil.isBlank(headurl)) {
                    wcsp.setShareType(Platform.SHARE_TEXT);
                } else {
                    wcsp.setShareType(Platform.SHARE_WEBPAGE);
                    wcsp.setImageUrl(headurl + "200x200");
                    wcsp.setUrl("http://m.miaotu.com/ShareLine/?yid=" + yid);
                }
                wcsp.setTitle(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                wcsp.setText(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatFormListener());
                wechat.share(wcsp);
                break;
            case R.id.rb_share_weibo:
                SinaWeibo.ShareParams wbsp = new SinaWeibo.ShareParams();
                wbsp.setText(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                if (!StringUtil.isBlank(headurl)) {
                    wbsp.setImageUrl(headurl + "200x200");
                }
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                weibo.setPlatformActionListener(new PlatFormListener());
                weibo.share(wbsp);
                break;
            case R.id.rb_share_zone:
                QZone.ShareParams qqsp = new QZone.ShareParams();
                qqsp.setTitle(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                qqsp.setTitleUrl("http://m.miaotu.com/ShareLine/?yid=" + yid); // 标题的超链接
                qqsp.setText(remark + "\n http://m.miaotu.com/ShareLine/?yid=" + yid);
                qqsp.setImageUrl(headurl + "200x200");
                qqsp.setSite(getString(R.string.app_name));
                qqsp.setSiteUrl("http://m.miaotu.com/ShareLine/?yid=" + yid);

                Platform qzone = ShareSDK.getPlatform(QZone.NAME);
                qzone.setPlatformActionListener(new PlatFormListener()); // 设置分享事件回调
                // 执行图文分享
                qzone.share(qqsp);
                break;
        }
    }

    class PlatFormListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            PublishTogetherStep2Activity.this.showMyToast("分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            PublishTogetherStep2Activity.this.showMyToast("分享失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            PublishTogetherStep2Activity.this.showMyToast("取消分享");
        }
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param validateStr 指定的字符串
     * @return 字符串的长度
     */
    public int getChineseLength(String validateStr) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < validateStr.length(); i++) {
            /* 获取一个字符 */
            String temp = validateStr.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
