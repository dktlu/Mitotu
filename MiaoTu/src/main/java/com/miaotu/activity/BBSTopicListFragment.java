package com.miaotu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.adapter.TopiclistAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.jpush.MessageCountDatabaseHelper;
import com.miaotu.form.MFriendsInfo;
import com.miaotu.model.LikeInfo;
import com.miaotu.model.Recommend;
import com.miaotu.model.Topic;
import com.miaotu.result.BaseResult;
import com.miaotu.result.LikeResult;
import com.miaotu.result.TopicListResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ying on 2015/3/6.
 */
public class BBSTopicListFragment extends BaseFragment implements View.OnClickListener {
    private View root;
    private Button btnLeft;
    private PullToRefreshListView lvTopics;
    private View layoutMore;
    private List<Topic> topicList;
    private TopiclistAdapter adapter;
    private static int PAGECOUNT = 10;
    private int curPageCount = 0;
    private boolean isLoadMore = false;
    private ImageView ivPublish, ivDot;
    private MFriendsInfo info;
    private RadioGroup rgTitle;
    private RadioButton tab1, tab2;
    private boolean isClick;
    private String tempTitle = "nearby";//区分最热和身旁
    private LinearLayout llMore;
    private ImageView ivHeadPhoto, ivAdd;
    private TextView tvName, tvPs, tvMore;
    private View topView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_bbs_topic_list,
                container, false);
        layoutMore = inflater.inflate(R.layout.pull_to_refresh_more, null);
        topView = inflater.inflate(R.layout.header_more, null);
        findView();
        bindView();
        init();
        return root;
    }

    private void findView() {
        tvMore = (TextView) topView.findViewById(R.id.tv_more);
        tvName = (TextView) topView.findViewById(R.id.tv_name);
        tvPs = (TextView) topView.findViewById(R.id.tv_ps);
        ivAdd = (ImageView) topView.findViewById(R.id.iv_add);
        ivHeadPhoto = (ImageView) topView.findViewById(R.id.iv_head_photo);
        llMore = (LinearLayout) topView.findViewById(R.id.ll_more);
        ivDot = (ImageView) root.findViewById(R.id.iv_dot);
        ivPublish = (ImageView) root.findViewById(R.id.iv_publish);
        btnLeft = (Button) root.findViewById(R.id.btn_left);
        lvTopics = (PullToRefreshListView) root.findViewById(R.id.lv_topics);
        rgTitle = (RadioGroup) root.findViewById(R.id.rg_title);
        tab1 = (RadioButton) root.findViewById(R.id.tab1);
        tab2 = (RadioButton) root.findViewById(R.id.tab2);
    }

    private void bindView() {
        lvTopics.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                boolean isMore = false;
                if ("nearby".equals(tempTitle)) {
                    isMore = true;
                }
                getTopics(false, info, isMore);

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }

        });

        lvTopics.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
//                showToastMsg("滑动到底了");
                if (!isLoadMore && curPageCount == topicList.size()) {
                    loadMore();
                }
//                lvTopics.setRefreshing(true);
            }

        });
        lvTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //从1开始 跳转到帖子详情页
                Intent intent = new Intent(getActivity(), BBSTopicDetailActivity.class);
                if (lvTopics.getRefreshableView().getHeaderViewsCount() > 1) {
                    if (i > 1){
                        intent.putExtra("topic", topicList.get(i - 2));
                        startActivityForResult(intent, i - 2);
                    }
                } else {
                    if (i > 0){
                        intent.putExtra("topic", topicList.get(i - 1));
                        startActivityForResult(intent, i - 1);
                    }
                }
            }
        });
//
//        btnRight.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    private void init() {
        if (readPreference("topic_comment").equals("1")) {
            ivDot.setVisibility(View.VISIBLE);
        } else {
            ivDot.setVisibility(View.GONE);
        }

        btnLeft.setBackgroundResource(R.drawable.icon_topic_message);
        ViewGroup.LayoutParams params1 = btnLeft.getLayoutParams();
        params1.width = Util.dip2px(getActivity(), 24);
        params1.height = Util.dip2px(getActivity(), 24);
        btnLeft.setLayoutParams(params1);
        btnLeft.setVisibility(View.VISIBLE);
        topicList = new ArrayList<>();
        String token = readPreference("token");
        adapter = new TopiclistAdapter(getActivity(), topicList, token, false);
        lvTopics.setAdapter(adapter);

        info = new MFriendsInfo();
        String lat = readPreference("latitude");    //纬度
        String lon = readPreference("longitude");    //经度
        lat = "30.312021";
        lon = "120.255116";
        info.setLatitude(lat);
        info.setLongitude(lon);
        info.setToken(token);
        info.setNum("");
        info.setPage("");
        info.setType("nearby");

        View emptyview = LayoutInflater.from(BBSTopicListFragment.this.getActivity()).
                inflate(R.layout.activity_empty, null);
        Button btnSearch = (Button) emptyview.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tab1.setChecked(true);
                tab1.setText("身旁");
                info.setType("nearby");
                getTopics(true, info, true);
            }
        });
        lvTopics.setEmptyView(emptyview);

        getTopics(true, info, true);
    }

    //刷新左上角提示
    public void refreshMessage() {
        try {
            ivDot.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //刷新社区页面
    private void refresh() {
        try {
            boolean isMore = false;
            if ("nearby".equals(tempTitle)) {
                isMore = true;
            }
            getTopics(false, info, isMore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取消息数量
     */
    private void getMessageCount() {
        MessageCountDatabaseHelper helper = new MessageCountDatabaseHelper(getActivity());
        if (helper.getMessageNum("topic") > 0) {
            ivDot.setVisibility(View.VISIBLE);
        } else {
            ivDot.setVisibility(View.GONE);
        }
    }

    /**
     * 获取妙友动态
     *
     * @param isShow
     * @param info
     */
    private void getTopics(boolean isShow, final MFriendsInfo info, final boolean isMore) {
        new BaseHttpAsyncTask<Void, Void, TopicListResult>(getActivity(), isShow) {
            @Override
            protected void onCompleteTask(TopicListResult result) {
                if (topicList == null) {
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
                    topicList.clear();
                    if (result.getTopics() == null) {
                        adapter.notifyDataSetChanged();
                        lvTopics.getRefreshableView().removeFooterView(layoutMore);
                        return;
                    }
                    topicList.addAll(result.getTopics());
                    adapter.notifyDataSetChanged();
//                    showToastMsg("lastvisibale:"+lvTopics.getRefreshableView().getLastVisiblePosition()+"  count: "+lvTopics.getRefreshableView().getCount()+" first:"+lvTopics.getRefreshableView().getFirstVisiblePosition());
                    if (lvTopics.getRefreshableView().getFooterViewsCount() == 1 && topicList.size() == PAGECOUNT) {
                        lvTopics.getRefreshableView().addFooterView(layoutMore);
                    }
                    if (isMore) {
                        if (result.getRecommends() != null && result.getRecommends().size() > 0) {
                            //刷新更多妙友
                            if (lvTopics.getRefreshableView().getHeaderViewsCount() == 1) {
                                lvTopics.getRefreshableView().addHeaderView(topView);
                            }
                            refreshMoreView(result.getRecommends());
                        } else {
                            if (lvTopics.getRefreshableView().getHeaderViewsCount() > 1) {
                                lvTopics.getRefreshableView().removeHeaderView(topView);
                            }
                        }
                    } else {
                        if (lvTopics.getRefreshableView().getHeaderViewsCount() > 1) {
                            lvTopics.getRefreshableView().removeHeaderView(topView);
                        }
                    }
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        showToastMsg("获取话题列表失败！");
                    } else {
                        showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected TopicListResult run(Void... params) {
                curPageCount = PAGECOUNT;
//                info.setNum(curPageCount+"");
                return HttpRequestUtil.getInstance().getTopicList(info);
            }

            @Override
            protected void finallyRun() {
                if (topicList == null) {
                    return;
                }
                lvTopics.onRefreshComplete();
            }
        }.execute();
    }

    private void loadMore() {
        new BaseHttpAsyncTask<Void, Void, TopicListResult>(getActivity(), false) {
            @Override
            protected void onCompleteTask(TopicListResult result) {
                if (topicList == null) {
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
                    topicList.clear();
                    topicList.addAll(result.getTopics());
                    adapter.notifyDataSetChanged();
                    if (topicList.size() != curPageCount) {
                        lvTopics.getRefreshableView().removeFooterView(layoutMore);
                    }
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        showToastMsg("获取话题列表失败！");
                    } else {
                        showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected TopicListResult run(Void... params) {
                isLoadMore = true;
                curPageCount += PAGECOUNT;
                info.setNum(curPageCount + "");
                return HttpRequestUtil.getInstance().getTopicList(info);
            }

            @Override
            protected void finallyRun() {
                if (topicList == null) {
                    return;
                }
                lvTopics.onRefreshComplete();
                isLoadMore = false;
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_publish:
                //发表新话题
                Intent intent = new Intent(getActivity(), BBSPublishTopicActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_left:
                //社区提醒
                ivDot.setVisibility(View.GONE);
                writePreference("topic_comment", "0");
                MessageCountDatabaseHelper helper = new MessageCountDatabaseHelper(getActivity());
                long l = helper.resetMessageNo("topic");
                Intent intent1 = new Intent(getActivity(), BBSMessageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tab1:
                if (isClick) {
                    tab1.setChecked(true);
                    info.setType(tempTitle);
                    getTopics(true, info, true);
                    if (lvTopics.getRefreshableView().getFooterViewsCount() > 1) {
                        lvTopics.getRefreshableView().removeFooterView(layoutMore);
                    }
                } else {
                    showPopWindow(this.getActivity(), tab1);
                }
                isClick = false;
                break;
            case R.id.tab2: //好友
                info.setType("like");
                getTopics(true, info, false);
                isClick = true;
                if (lvTopics.getRefreshableView().getFooterViewsCount() > 1) {
                    lvTopics.getRefreshableView().removeFooterView(layoutMore);
                }
                break;
            case R.id.tv_more:
                Intent findMoreIntent = new Intent();
                findMoreIntent.setClass(BBSTopicListFragment.this.getActivity(),
                        FindMFriendsActivity.class);
                startActivity(findMoreIntent);
                break;
            case R.id.iv_add:
                like(readPreference("token"), (String)ivAdd.getTag());
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            //发表帖子成功
            refresh();
        }
        if (resultCode == 1001) {        //喜欢
            topicList.get(requestCode).setIslike("true");
            adapter.notifyDataSetChanged();
            LikeInfo info = new LikeInfo();
            info.setUid(readPreference("uid"));
            info.setHeadurl(readPreference("headphoto"));
            topicList.get(requestCode).getLikelist().add(info);
        } else if (resultCode == 1002) {   //取消喜欢
            topicList.get(requestCode).setIslike("false");
            adapter.notifyDataSetChanged();
            for (LikeInfo info : topicList.get(requestCode).getLikelist()) {
                if (readPreference("uid").equals(info.getUid())) {
                    topicList.get(requestCode).getLikelist().remove(info);
                    break;
                }
            }
        }
    }

    /**
     * 显示身旁/最热的pop框
     *
     * @param context
     * @param parent
     */
    private void showPopWindow(Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopWindow = inflater.inflate(R.layout.pop_friends, null, false);
        final PopupWindow popWindow = new PopupWindow(vPopWindow,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView tvNearby = (TextView) vPopWindow.findViewById(R.id.tv_beside);
        TextView tvHot = (TextView) vPopWindow.findViewById(R.id.tv_hot);
        tvNearby.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempTitle = "nearby";
                tab1.setText("身旁");
                info.setType(tempTitle);
                if (lvTopics.getRefreshableView().getFooterViewsCount() > 1) {
                    lvTopics.getRefreshableView().removeFooterView(layoutMore);
                }
                getTopics(true, info, true);
                popWindow.dismiss();
            }
        });

        tvHot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempTitle = "hot";
                tab1.setText("最热");
                info.setType(tempTitle);
                if (lvTopics.getRefreshableView().getFooterViewsCount() > 1) {
                    lvTopics.getRefreshableView().removeFooterView(layoutMore);
                }
                getTopics(true, info, false);
                popWindow.dismiss();
            }
        });
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_dialog));
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.showAsDropDown(parent);
    }

    //刷新更多妙友
    private void refreshMoreView(List<Recommend> recommends) {
        if (recommends.size() > 0) {
            UrlImageViewHelper.setUrlDrawable(ivHeadPhoto, recommends.get(0).getHeadurl());
            tvName.setText(recommends.get(0).getNickname());
            tvPs.setText(recommends.get(0).getContent());
            ivAdd.setTag(recommends.get(0).getUid());
            ivAdd.setBackgroundResource(R.drawable.icon_add);
        }
    }

    private void like(final String token, final String touid){
        new BaseHttpAsyncTask<Void, Void, LikeResult>(this.getActivity()){

            @Override
            protected void onCompleteTask(LikeResult result) {
                if (result.getCode() == BaseResult.SUCCESS){
                    int count = 0;
                    if (!StringUtil.isBlank(readPreference("followcount"))){
                        count = Integer.parseInt(readPreference("followcount"));
                    }
                    if ("0".equals(result.getLikeInfo().getLid())){
                        ivAdd.setBackgroundResource(R.drawable.icon_add);
                        count-=1;
                        if (count < 0){
                            count = 0;
                        }
                        showToastMsg("取消喜欢成功");
                    }else {
                        ivAdd.setBackgroundResource(R.drawable.icon_minus);
                        count+=1;
                        showToastMsg("喜欢成功");
                    }
                    writePreference("followcount", count+"");
                }else {
                    if (StringUtil.isBlank(result.getMsg())){
                        showToastMsg("添加好友失败");
                    }else {
                        showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected LikeResult run(Void... params) {
                return HttpRequestUtil.getInstance().likeforparam(token, touid);
            }
        }.execute();
    }
}
