package com.miaotu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.easemob.util.DateUtils;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.activity.BaseActivity;
import com.miaotu.activity.BaseFragmentActivity;
import com.miaotu.activity.JoinedListActivity;
import com.miaotu.activity.PersonCenterActivity;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.RemindLikeTogether;
import com.miaotu.result.BaseResult;
import com.miaotu.result.ReviewResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;
import com.miaotu.view.CircleImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jayden on 2015/5/29.
 */
public class RemindJoinTogetherListAdapter extends BaseAdapter{

    private Context mcontext;
    private List<RemindLikeTogether> remindLikes;
    private LayoutInflater mLayoutInflater = null;

    public RemindJoinTogetherListAdapter(Context context, List<RemindLikeTogether> remindLikes){
        this.mcontext = context;
        this.remindLikes = remindLikes;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return remindLikes == null?0:remindLikes.size();
    }

    @Override
    public Object getItem(int i) {
        return remindLikes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = mLayoutInflater.inflate(R.layout.item_remind_join_tour, null);
            holder = new ViewHolder();
            holder.ivPhoto = (CircleImageView) view.findViewById(R.id.iv_head_photo);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_time);
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_status);
            holder.ivPic = (ImageView) view.findViewById(R.id.iv_right);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        UrlImageViewHelper.setUrlDrawable(holder.ivPhoto, remindLikes.get(i).getRemindLikeTogetherInfo().getHeadUrl(), R.drawable.icon_default_head_photo);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Util.isNetworkConnected(mcontext)) {
                    return;
                }
                Intent intent = new Intent(mcontext, PersonCenterActivity.class);
                intent.putExtra("uid", remindLikes.get(i).getRemindLikeTogetherInfo().getUid());
                mcontext.startActivity(intent);
            }
        });
        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, JoinedListActivity.class);
                intent.putExtra("flag", "1");
                intent.putExtra("yid", remindLikes.get(i).getRemindLikeTogetherInfo().getYid());
                intent.putExtra("title", remindLikes.get(i).getRemindLikeTogetherInfo().getContent());
                ((BaseFragmentActivity)mcontext).startActivityForResult(intent, i);
            }
        });
        try {
            holder.tvDate.setText(DateUtils.getTimestampString(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(remindLikes.get(i).getCreated())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvName.setText(remindLikes.get(i).getRemindLikeTogetherInfo().getNickname());
        holder.tvContent.setText(remindLikes.get(i).getRemindLikeTogetherInfo().getContent());
        return view;
    }

    public class ViewHolder{
        CircleImageView ivPhoto;
        ImageView ivPic;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }

}
