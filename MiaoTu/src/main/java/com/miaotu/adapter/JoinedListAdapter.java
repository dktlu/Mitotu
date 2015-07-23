package com.miaotu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.activity.BaseActivity;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.JoinedListInfo;
import com.miaotu.result.BaseResult;
import com.miaotu.result.ReviewResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jayden on 2015/6/3.
 */
public class JoinedListAdapter extends BaseAdapter {

    private Context mcontext;
    private List<JoinedListInfo> joinedListInfoList;
    private LayoutInflater inflater;
    private boolean isTogether; //判断一起去还是妙旅团跳转过来
    private String yid;

    public JoinedListAdapter(Context mcontext, List<JoinedListInfo> joinedListInfoList,
                             boolean isTogether, String yid) {
        this.mcontext = mcontext;
        this.joinedListInfoList = joinedListInfoList;
        inflater = LayoutInflater.from(mcontext);
        this.isTogether = isTogether;
        this.yid = yid;
    }

    @Override
    public int getCount() {
        return joinedListInfoList == null ? 0 : joinedListInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return joinedListInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_join_list, null);
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvNickName = (TextView) view.findViewById(R.id.tv_nickname);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
            holder.tvIdentity = (TextView) view.findViewById(R.id.tv_identity);
            holder.tvPhone = (TextView) view.findViewById(R.id.tv_phone);
            holder.ivCall = (ImageView) view.findViewById(R.id.iv_call);
            holder.ivHeadPhoto = (ImageView) view.findViewById(R.id.iv_head_photo);
            holder.rlIdentity = (RelativeLayout) view.findViewById(R.id.rl_identity);
            holder.rlOption = (RelativeLayout) view.findViewById(R.id.rl_option);
            holder.llOptionTip = (LinearLayout) view.findViewById(R.id.ll_refused);
            holder.tvAgree = (TextView) view.findViewById(R.id.tv_agree);
            holder.tvRefuse = (TextView) view.findViewById(R.id.tv_refuse);
            holder.tvRefused = (TextView) view.findViewById(R.id.tv_refused);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        JoinedListInfo info = joinedListInfoList.get(i);
        holder.tvName.setText(info.getUsername());
        holder.tvNickName.setText("(昵称：" + info.getNickname() + ")");
        holder.tvDate.setText(info.getCreated());
        holder.tvPhone.setText(info.getUserphone());
        holder.tvIdentity.setText(info.getUsercard());
        UrlImageViewHelper.setUrlDrawable(holder.ivHeadPhoto, info.getHeadurl(), R.drawable.icon_default_head);
        holder.ivCall.setTag(i);
        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.DIAL");
                intent1.setData(Uri.parse("tel:" + joinedListInfoList.get(pos).getUserphone()));
                mcontext.startActivity(intent1);
            }
        });
        if (isTogether) {
            holder.rlIdentity.setVisibility(View.GONE);
            if ("-1".equals(info.getStatus())){
                holder.rlOption.setVisibility(View.GONE);
                holder.llOptionTip.setVisibility(View.VISIBLE);
                holder.tvRefused.setText("已拒绝（取消）");
            }else if ("1".equals(info.getStatus())){
                holder.rlOption.setVisibility(View.GONE);
                holder.llOptionTip.setVisibility(View.VISIBLE);
                holder.tvRefused.setText("已恩准");
            }else {
                holder.rlOption.setVisibility(View.VISIBLE);
                holder.llOptionTip.setVisibility(View.GONE);
            }
            holder.tvAgree.setOnClickListener(new ClickListener(holder.rlOption,
                    holder.llOptionTip, holder.tvRefused, yid, info.getUid()));
            holder.tvRefuse.setOnClickListener(new ClickListener(holder.rlOption,
                    holder.llOptionTip, holder.tvRefused, yid, info.getUid()));
            holder.tvRefused.setOnClickListener(new ClickListener(holder.rlOption,
                    holder.llOptionTip, holder.tvRefused, yid, info.getUid()));
        }else {
            holder.rlOption.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * @param relativeLayout
     * @param linearLayout
     * @param tvRefused
     * @param changeid       1：拒绝 2 同意
     */
    private void changeOptionView(RelativeLayout relativeLayout,
                                  LinearLayout linearLayout,
                                  TextView tvRefused, int changeid) {
        if (changeid == 1) {
            relativeLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            tvRefused.setText("已拒绝（取消）");
        } else if (changeid == 2) {
            relativeLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            tvRefused.setText("已恩准");
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
    }

    public class ViewHolder {
        TextView tvName;
        TextView tvNickName;
        TextView tvDate;
        TextView tvIdentity;
        TextView tvPhone;
        ImageView ivHeadPhoto;
        ImageView ivCall;
        RelativeLayout rlIdentity;
        RelativeLayout rlOption;
        LinearLayout llOptionTip;
        TextView tvAgree;
        TextView tvRefuse;
        TextView tvRefused;
    }

    class ClickListener implements View.OnClickListener {

        private RelativeLayout rlOption;
        private LinearLayout llOptionTip;
        private TextView tvRefused;
        private boolean isTimeOut;
        private String yid,uid;

        public ClickListener(RelativeLayout rlOption,
                             LinearLayout llOptionTip,
                             TextView tvRefused, String yid, String uid) {
            this.rlOption = rlOption;
            this.llOptionTip = llOptionTip;
            this.tvRefused = tvRefused;
            this.yid = yid;
            this.uid = uid;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_agree: //同意
//                    changeOptionView(rlOption, llOptionTip, tvRefused, 2);
//                    tvRefused.setTag(2);
                    reviewUser(yid, uid, "1", rlOption, llOptionTip, tvRefused);
                    break;
                case R.id.tv_refuse:    //拒绝
//                    changeOptionView(rlOption, llOptionTip, tvRefused, 1);
//                    tvRefused.setTag(1);
                    reviewUser(yid,uid,"-1",rlOption,llOptionTip,tvRefused);

                    break;
                case R.id.tv_refused:       //取消
//                    changeOptionView(rlOption, llOptionTip, tvRefused, 0);
                    if ((int) view.getTag() == 1) {
                        if (!isTimeOut){
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    //
                                    isTimeOut = true;
                                }
                            }, 300000);
                        }
                        if (isTimeOut){
                            createDialog(rlOption,
                                    llOptionTip, tvRefused, yid, uid);
                        }else {
                            ((BaseActivity)mcontext).showToastMsg("您已经拒绝了TA的报名\n" +
                                    "5分钟后才能修改");
                        }
                    }
                    break;
            }
        }
    }

    private void createDialog(final RelativeLayout rlOption,
                              final LinearLayout llOptionTip,
                              final TextView tvRefused, final String yid, final String uid) {
        final Dialog dialog = new AlertDialog.Builder(mcontext).create();
        dialog.setCancelable(true);
        dialog.show();
        View v = LayoutInflater.from(mcontext).inflate(
                R.layout.dialog_message_empty, null);
        v.setBackgroundResource(R.drawable.bg_dialog_choose_register);
        dialog.setContentView(v);
        Button btnCancle = (Button) v.findViewById(R.id.btn_cancel);
        Button btnConfirm = (Button) v.findViewById(R.id.btn_confirm);
        TextView tvContent = (TextView) v.findViewById(R.id.tv_content);
        tvContent.setText("您是否要修改决定,同意对方报名？");
        btnCancle.setText("取消修改");
        btnConfirm.setText("同意报名");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Util.isNetworkConnected(mcontext)) {
                    ((BaseActivity) mcontext).showToastMsg("当前未联网，请检查网络设置");
                    return;
                }
                //同意的接口
                reviewUser(yid,uid,"1",rlOption,llOptionTip,tvRefused);
                dialog.dismiss();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = Util.dip2px(mcontext, 240);
        params.height = Util.dip2px(mcontext, 149);
        dialog.getWindow().setAttributes(params);
    }

    /**
     * 审核用户
     * @param yid
     * @param uid
     * @param status
     */
    private void reviewUser(final String yid, final String uid, final String status,
                            final RelativeLayout relativeLayout,
                            final LinearLayout linearLayout, final TextView tvRefused){
        new BaseHttpAsyncTask<Void, Void, BaseResult>((Activity)mcontext){

            @Override
            protected void onCompleteTask(BaseResult reviewResult) {
                if (reviewResult.getCode() == BaseResult.SUCCESS){
                    if ("1".equals(status)){    //同意
                        changeOptionView(relativeLayout, linearLayout, tvRefused, 2);
                        tvRefused.setTag(2);
                    }else { //拒绝
                        changeOptionView(relativeLayout, linearLayout, tvRefused, 1);
                        tvRefused.setTag(1);
                    }
                }else {
                    if (StringUtil.isBlank(reviewResult.getMsg())){
                        ((BaseActivity)mcontext).showToastMsg("操作失败");
                    }else {
                        ((BaseActivity)mcontext).showToastMsg(reviewResult.getMsg());

                    }
                }
            }

            @Override
            protected BaseResult run(Void... params) {
                return HttpRequestUtil.getInstance().reviewUser(
                        ((BaseActivity)mcontext).readPreference("token"), yid,uid, status);
            }
        }.execute();
    }
}
