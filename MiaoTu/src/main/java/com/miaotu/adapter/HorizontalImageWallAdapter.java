package com.miaotu.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.model.ImageWall;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayden on 2015/7/15.
 */
public class HorizontalImageWallAdapter extends
        RecyclerView.Adapter<HorizontalImageWallAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<ImageWall> imagelist;
    private LayoutInflater inflater;
    private ArrayList<PhotoModel> photoList;

    public HorizontalImageWallAdapter(Context context, List<ImageWall> imagelist){
        this.mContext = context;
        this.imagelist = imagelist;
        inflater = LayoutInflater.from(mContext);
        photoList = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_imagewall, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        PhotoModel photoModel = new PhotoModel();
        photoModel.setOriginalPath(imagelist.get(i).getUrl());
        photoList.add(photoModel);
        UrlImageViewHelper.setUrlDrawable(viewHolder.ivWall,
                imagelist.get(i).getUrl(), R.drawable.icon_default_bbs_photo);
        viewHolder.ivWall.setTag(i);
        viewHolder.ivWall.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return imagelist == null?0:imagelist.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_wall:
                Bundle bundle = new Bundle();
                bundle.putSerializable("photos", photoList);
                bundle.putSerializable("position", (int) view.getTag());
                CommonUtils.launchActivity(mContext, PhotoPreviewActivity.class, bundle);
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivWall;

        public ViewHolder(View itemView) {
            super(itemView);
            ivWall = (ImageView) itemView.findViewById(R.id.iv_wall);
        }
    }
}
