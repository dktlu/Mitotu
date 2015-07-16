package com.miaotu.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.model.Banner;

public class SignNoticeActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvNotice,tvLeft,tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_notice);
        initView();
        bindView();
        initData();
    }

    private void initView(){
        tvNotice = (TextView) this.findViewById(R.id.tv_notice);
        tvLeft = (TextView) this.findViewById(R.id.tv_left);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
    }

    private void initData(){

        tvTitle.setText("预订须知");
        if ("1".equals(getIntent().getStringExtra("together"))){
            tvNotice.setText(getResources().getString(R.string.togethernotice));
        }else {
            tvNotice.setText(getResources().getString(R.string.customtournotice));
        }
    }

    private void bindView(){
        tvLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_left:
                finish();
                break;
        }
    }
}
