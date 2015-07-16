package com.miaotu.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.adapter.HorizontalImageWallAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.ImageWall;
import com.miaotu.result.BaseResult;
import com.miaotu.result.ImageWallResult;
import com.miaotu.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GridImageWallActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView rvImageWall;
    private int page=1;
    private final int PAGECOUNT = 15;
    private List<ImageWall> imageWallList;
    private HorizontalImageWallAdapter imageWallAdapter;
    private int lastPosition;
    private TextView tvTitle,tvLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_image_wall);

        initView();
        bindView();
        initData();
    }

    private void initView(){
        tvLeft = (TextView) this.findViewById(R.id.tv_left);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        rvImageWall = (RecyclerView) this.findViewById(R.id.rv_imagewall);
    }

    private void bindView(){
        tvLeft.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("图片墙");
        imageWallList = new ArrayList<>();
        imageWallAdapter = new HorizontalImageWallAdapter(this, imageWallList);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvImageWall.setLayoutManager(gridLayoutManager);
        rvImageWall.setAdapter(imageWallAdapter);
        rvImageWall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastPosition + 1) == imageWallAdapter.getItemCount() &&
                        imageWallList.size() == page * PAGECOUNT) {
                    page += 1;
                    getImageWall();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPosition = gridLayoutManager.findLastVisibleItemPosition();
            }
        });
        getImageWall();
    }

    /**
     * 获取图片墙
     */
    private void getImageWall(){
        new BaseHttpAsyncTask<Void, Void, ImageWallResult>(this){

            @Override
            protected void onCompleteTask(ImageWallResult imageWallResult) {
                if (imageWallResult.getCode() == BaseResult.SUCCESS){
                    imageWallList.clear();
                    imageWallList.addAll(imageWallResult.getImageWallList());
                    imageWallAdapter.notifyItemChanged(imageWallList.size()-1);
                }else {
                    if (StringUtil.isBlank(imageWallResult.getMsg())){
                        showToastMsg("获取图片墙失败");
                    }else {
                        showToastMsg(imageWallResult.getMsg());
                    }
                }
            }

            @Override
            protected ImageWallResult run(Void... params) {
                return HttpRequestUtil.getInstance().getImageWall(
                        readPreference("token"),getIntent().getStringExtra("uid"),1,PAGECOUNT*page);
            }
        }.execute();
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
