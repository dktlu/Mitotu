package com.miaotu.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.miaotu.R;
import com.miaotu.adapter.MyFansAdapter;
import com.miaotu.adapter.MyLikeAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.BlackInfo;
import com.miaotu.result.BaseResult;
import com.miaotu.result.BlackResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MyFansFragment extends BaseFragment implements View.OnClickListener{

    private SwipeMenuListView lvBlackList;
    private List<BlackInfo> blackInfoList;
    private MyFansAdapter adapter;
    private View root;
    private String uid;
    private boolean isMine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_my_like, container, false);
        initView();
        initData();
        return root;
    }

    private void initView(){
        lvBlackList = (SwipeMenuListView) root.findViewById(R.id.lv_blacklist);
    }

    private void initData(){
//        uid = readPreference("uid");
        Bundle bundle = getArguments();
        if (!StringUtil.isBlank(bundle.getString("uid"))){
            if (readPreference("uid").equals(bundle.getString("uid"))){
                isMine = true;
            }
            uid = bundle.getString("uid");
        }
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.empty_follow_fans, null);
        ((ImageView)view.findViewById(R.id.iv_empty)).setBackgroundResource(R.drawable.icon_empty_follow);
        ((LinearLayout)lvBlackList.getParent()).setGravity(Gravity.CENTER);
        view.setVisibility(View.GONE);
        ((LinearLayout)lvBlackList.getParent()).addView(view);
        lvBlackList.setEmptyView(view);
        blackInfoList = new ArrayList<>();
        adapter = new MyFansAdapter(this.getActivity(), blackInfoList, readPreference("token"));
        lvBlackList.setAdapter(adapter);
        getFansList(uid);
    }

    /**
     * 获取关注列表
     */
    public void getFansList(final String uid){
        new BaseHttpAsyncTask<Void, Void, BlackResult>(this.getActivity(), true){

            @Override
            protected void onCompleteTask(BlackResult blackResult) {
                if(blackResult.getCode() == BaseResult.SUCCESS){
                    blackInfoList.clear();
                    if(blackResult.getBlackInfos() == null){
//                        adapter.notifyDataSetChanged();
//                        viewStub.inflate();
                        return;
                    }
                    blackInfoList.addAll(blackResult.getBlackInfos());
                    adapter.notifyDataSetChanged();
                    if (isMine) {
                        writePreference("fanscount", blackInfoList.size() + "");
                    }
                }else {
                    if(StringUtil.isBlank(blackResult.getMsg())){
                        MyFansFragment.this.showMyToast("获取黑名单失败");
                    }else {
                        MyFansFragment.this.showMyToast(blackResult.getMsg());
                    }
                }
            }

            @Override
            protected BlackResult run(Void... params) {
                return HttpRequestUtil.getInstance().getFansList(readPreference("token"),
                        uid);
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }
}
