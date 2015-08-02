package com.miaotu.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Together;
import com.miaotu.result.BaseResult;
import com.miaotu.result.TogetherDetailResult;
import com.miaotu.util.StringUtil;

public class JoinTogetherStep2 extends BaseActivity implements View.OnClickListener {
    private TextView tvLeft, tvTitle;
    private Button btnNext;
    private EditText etName, etTel;
    private Together together;
    private TextView tvNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_together_step2);
        initView();
        bindView();
        initData();
    }

    private void initView(){
        tvNotice = (TextView) findViewById(R.id.tv_notice);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        btnNext = (Button) findViewById(R.id.btn_next);
        etName = (EditText) findViewById(R.id.et_name);
        etTel = (EditText) findViewById(R.id.et_tel);
    }

    private void bindView(){
        tvLeft.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvNotice.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("报名");
        together = (Together) getIntent().getSerializableExtra("together");
        SpannableString sp = new SpannableString("报名须知");
        sp.setSpan(new URLSpan(""), 0, sp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_href)),
                0, sp.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvNotice.setText(sp);
    }

    private boolean validate() {
        if (StringUtil.isBlank(etName.getText().toString())) {
            JoinTogetherStep2.this.showMyToast("请输入姓名！");
            return false;
        }
        if (StringUtil.isBlank(etTel.getText().toString())) {
            JoinTogetherStep2.this.showMyToast("请输入手机号！");
            return false;
        }
        if (!StringUtil.isPhoneNumber(etTel.getText().toString())) {
            JoinTogetherStep2.this.showMyToast("请输入正确手机号！");
            return false;
        }
        return true;
    }

    private void join() {
        new BaseHttpAsyncTask<Void, Void, BaseResult>(JoinTogetherStep2.this, true) {
            @Override
            protected void onCompleteTask(BaseResult result) {
                if (tvTitle == null) {
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
                    JoinTogetherStep2.this.showMyToast("报名成功！");
                    Intent intent = new Intent(JoinTogetherStep2.this,
                            JoinFinishedActicity.class);
                    intent.putExtra("uid", together.getUid());
                    intent.putExtra("nickname", together.getNickname());
                    intent.putExtra("headurl", together.getHeadPhoto());
                    intent.putExtra("gid", together.getGroupId());
                    intent.putExtra("groupname", together.getGroupname());
                    intent.putExtra("remark", together.getComment());
                    intent.putExtra("yid", together.getId());
                    if (together.getPicList() != null && together.getPicList().size() > 0) {
                        intent.putExtra("picurl", together.getPicList().get(0).getUrl());
                    }
                    startActivityForResult(intent, 1);
                    setResult(1);
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        JoinTogetherStep2.this.showMyToast("报名约游失败");
                    } else {
                        JoinTogetherStep2.this.showMyToast(result.getMsg());
                    }
                }
            }

            @Override
            protected BaseResult run(Void... params) {
                return HttpRequestUtil.getInstance().joinTogether(readPreference("token"),
                        together.getId(), StringUtil.trimAll(etName.getText().toString()),
                        StringUtil.trimAll(etTel.getText().toString()));
            }

        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (validate()) {
                    join();
                }
                break;
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_notice:
                Intent noticeIntent = new Intent();
                noticeIntent.setClass(JoinTogetherStep2.this, SignNoticeActivity.class);
                noticeIntent.putExtra("together", "1");
                startActivity(noticeIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            setResult(1);
            finish();
        }
    }
}
