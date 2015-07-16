package com.miaotu.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.model.Address;
import com.miaotu.model.ImageWall;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayden on 2015/7/15.
 */
public class ContactListAdapter extends
        RecyclerView.Adapter<ContactListAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<Address> contactlist;
    private LayoutInflater inflater;

    public ContactListAdapter(Context context, List<Address> contactlist){
        this.mContext = context;
        this.contactlist = contactlist;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_addresslist, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.ivControl.setTag(i);
        viewHolder.ivControl.setOnClickListener(this);
        viewHolder.tvName.setText(contactlist.get(i).getNickname());
        viewHolder.tvPs.setText("手机联系人："+contactlist.get(i).getNickname()+"正在使用妙途");
    }

    @Override
    public int getItemCount() {
        return contactlist == null?0:contactlist.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_wall:

                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivControl;
        public TextView tvPs,tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivControl = (ImageView) itemView.findViewById(R.id.iv_control);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPs = (TextView) itemView.findViewById(R.id.tv_ps);
        }
    }
}
