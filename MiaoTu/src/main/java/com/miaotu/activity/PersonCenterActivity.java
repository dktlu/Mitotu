package com.miaotu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.adapter.HorizontalImageWallAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.imutil.ContactInfo;
import com.miaotu.imutil.IMDatabaseHelper;
import com.miaotu.model.ImageWall;
import com.miaotu.model.ModifyPersonInfo;
import com.miaotu.result.ImageWallResult;
import com.miaotu.result.PersonInfoResult;
import com.miaotu.result.BaseResult;
import com.miaotu.result.PhotoUploadResult;
import com.miaotu.util.MD5;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;
import com.miaotu.view.CircleImageView;
import com.miaotu.view.FlowLayout;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PersonCenterActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_username,tv_content_home,tv_content_school,tv_content_lifearea,
            tv_content_job,tv_content_workarea,tv_content_freetime,
            tv_title,tv_left,tv_right;
    private FlowLayout fl_tag;
    private ImageView iv_gender;
    private View view7,view_bottom;
    private LinearLayout ll_tag;
    private RelativeLayout rl_home,rl_school,rl_lifearea,rl_workarea,rl_job,rl_freetime;
    private CircleImageView iv_head_photo;
    private TextView tv_start,tv_sign,tv_like,tv_trends,tv_tip_trends;
    private RelativeLayout rl_follow,rl_chating,rl_bottom,rl_join,rl_like,rl_start,rl_state;
    private TextView tv_follow,tv_age,tv_more;
    private ImageView iv_follow,iv_background;
    private PersonInfoResult result;
    private String token,uid;
    private boolean isMine;
    private static final String IMAGE_FILE_LOCATION = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/miaotu/bg.jpg";
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//
    private ArrayList<PhotoModel> photoList;
    private TextView tvFollowCount,tvFansCount,tvEmotionStatus,tvBudget;
    private RecyclerView rvImageWall;
    private int page=1;
    private final int PAGECOUNT = 10;
    private List<ImageWall> imageWallList;
    private HorizontalImageWallAdapter imageWallAdapter;
    private int lastPosition;
    private ViewStub viewStub;
    private LinearLayout ll_sexandage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        initView();
        bindView();
        initData();
    }

    private void initView(){
        tv_more = (TextView) this.findViewById(R.id.tv_more);
        tv_age = (TextView) this.findViewById(R.id.tv_age);
        ll_sexandage = (LinearLayout) this.findViewById(R.id.ll_sexandage);
        viewStub = (ViewStub) this.findViewById(R.id.viewstub);
        tvFansCount = (TextView) this.findViewById(R.id.tv_fans_count);
        tvFollowCount = (TextView) this.findViewById(R.id.tv_follow_count);
        tvEmotionStatus = (TextView) this.findViewById(R.id.tv_emotion_status);
        tvBudget = (TextView) this.findViewById(R.id.tv_budget);
        rvImageWall = (RecyclerView) this.findViewById(R.id.rv_imagewall);
        tv_follow = (TextView) this.findViewById(R.id.tv_follow);
        iv_follow = (ImageView) this.findViewById(R.id.iv_follow);
        tv_start = (TextView) this.findViewById(R.id.tv_start);
        tv_sign = (TextView) this.findViewById(R.id.tv_sign);
        tv_like = (TextView) this.findViewById(R.id.tv_like);
        tv_trends = (TextView) this.findViewById(R.id.tv_trends);
        tv_tip_trends = (TextView) this.findViewById(R.id.tv_tip_trends);
        rl_follow = (RelativeLayout) this.findViewById(R.id.rl_follow);
        rl_chating = (RelativeLayout) this.findViewById(R.id.rl_chating);
        rl_bottom = (RelativeLayout) this.findViewById(R.id.rl_bottom);
        iv_head_photo = (CircleImageView) this.findViewById(R.id.iv_head_photo);
        rl_home = (RelativeLayout) this.findViewById(R.id.rl_home);
        rl_school = (RelativeLayout) this.findViewById(R.id.rl_school);
        rl_lifearea = (RelativeLayout) this.findViewById(R.id.rl_lifearea);
        rl_workarea = (RelativeLayout) this.findViewById(R.id.rl_workarea);
        rl_job = (RelativeLayout) this.findViewById(R.id.rl_job);
        rl_freetime = (RelativeLayout) this.findViewById(R.id.rl_freetime);
        rl_join = (RelativeLayout) this.findViewById(R.id.rl_join);
        rl_like = (RelativeLayout) this.findViewById(R.id.rl_like);
        rl_start = (RelativeLayout) this.findViewById(R.id.rl_start);
        rl_state = (RelativeLayout) this.findViewById(R.id.rl_state);
        view7 = this.findViewById(R.id.view7);
        view_bottom = this.findViewById(R.id.view_bottom);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_left = (TextView) this.findViewById(R.id.tv_left);
        tv_right = (TextView) this.findViewById(R.id.tv_right);
        tv_content_home = (TextView) this.findViewById(R.id.tv_content_home);
        tv_content_school = (TextView) this.findViewById(R.id.tv_content_school);
        tv_content_lifearea = (TextView) this.findViewById(R.id.tv_content_lifearea);
        tv_content_workarea = (TextView) this.findViewById(R.id.tv_content_workarea);
        tv_content_job = (TextView) this.findViewById(R.id.tv_content_job);
        tv_content_freetime = (TextView) this.findViewById(R.id.tv_content_freetime);
        tv_username = (TextView) this.findViewById(R.id.tv_username);
        iv_gender = (ImageView) this.findViewById(R.id.iv_gender);
        fl_tag = (FlowLayout) this.findViewById(R.id.fl_tag);
        ll_tag = (LinearLayout) this.findViewById(R.id.ll_tag);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = (metric.widthPixels *669) / 1080;
        iv_background = (ImageView) this.findViewById(R.id.iv_background);
        iv_background.setLayoutParams(new RelativeLayout.LayoutParams(
                width, height));
        File myDir = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/miaotu");
        myDir.mkdirs();
    }

    private void bindView(){
        tv_right.setVisibility(View.GONE);
        tv_title.setText("个人主页");
        tv_left.setOnClickListener(this);
        rl_chating.setOnClickListener(this);
        rl_follow.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        rl_join.setOnClickListener(this);
        rl_start.setOnClickListener(this);
        rl_state.setOnClickListener(this);
        iv_background.setOnClickListener(this);
        iv_head_photo.setOnClickListener(this);
        tv_more.setOnClickListener(this);
    }

    /**
     * 初始化接口返回个人信息的控件
     * @param personInfoResult
     */
    private void initPersonInfoData(PersonInfoResult personInfoResult){
        if("true".equals(personInfoResult.getPersonInfo().getIslike())){    //是否关注此人
            changeBtnFollow(true);
        }
        UrlImageViewHelper.setUrlDrawable(iv_head_photo,
                personInfoResult.getPersonInfo().getHeadurl(),
                R.drawable.icon_default_head_photo);
        photoList = new ArrayList<>();
        PhotoModel photoModel = new PhotoModel();
        photoModel.setOriginalPath(personInfoResult.getPersonInfo().getHeadurl());
        photoList.add(photoModel);
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getHome())){
            rl_home.setVisibility(View.VISIBLE);
            tv_content_home.setText(personInfoResult.getPersonInfo().getHome());
        }
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getGraduateschool())){
            rl_school.setVisibility(View.VISIBLE);
            tv_content_school.setText(personInfoResult.getPersonInfo().getGraduateschool());
        }
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getLifearea())){
            rl_lifearea.setVisibility(View.VISIBLE);
            tv_content_lifearea.setText(personInfoResult.getPersonInfo().getLifearea());
        }
        tvEmotionStatus.setText(personInfoResult.getPersonInfo().getMaritalstatus());
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getLikecount())){
            tvFollowCount.setText(personInfoResult.getPersonInfo().getLikecount());
        }else {
            tvFollowCount.setText("0");
        }
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getCollectcount())){
            tvFansCount.setText(personInfoResult.getPersonInfo().getCollectcount());
        }else {
            tvFansCount.setText("0");
        }
        tvBudget.setText(personInfoResult.getPersonInfo().getBudget());
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getWorkarea())){
            rl_workarea.setVisibility(View.VISIBLE);
            tv_content_workarea.setText(personInfoResult.getPersonInfo().getWorkarea());
        }
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getWork())){
            rl_job.setVisibility(View.VISIBLE);
            tv_content_job.setText(personInfoResult.getPersonInfo().getWork());
        }
        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getFreetime())){
            rl_freetime.setVisibility(View.VISIBLE);
            tv_content_freetime.setText(personInfoResult.getPersonInfo().getFreetime());
        }
        tv_username.setText(personInfoResult.getPersonInfo().getNickname());
        if("男".equals(personInfoResult.getPersonInfo().getGender())){
            iv_gender.setBackgroundResource(R.drawable.icon_boy);
        }else {
            iv_gender.setBackgroundResource(R.drawable.icon_girl);
            ll_sexandage.setBackgroundResource(R.drawable.bg_woman);
        }
        tv_age.setText(personInfoResult.getPersonInfo().getAge());
        if (!StringUtil.isBlank(personInfoResult.getPersonInfo().getPicurl())){
            UrlImageViewHelper.setUrlDrawable(iv_background,
                    personInfoResult.getPersonInfo().getPicurl(),
                    R.drawable.icon_default_background);
        }

        if(!StringUtil.isBlank(personInfoResult.getPersonInfo().getTags())){
            String[] tags = personInfoResult.getPersonInfo().getTags().split(",");
            int count = 0;
            for(String tag:tags){
                View view = LayoutInflater.from(this).inflate(R.layout.item_hottag, null);
                TextView tv_content = (TextView) view.findViewById(R.id.tv_tag);
                tv_content.setText(tag);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = Util.dip2px(PersonCenterActivity.this,10);
                params.rightMargin = Util.dip2px(PersonCenterActivity.this, 10);
                view.setLayoutParams(params);
                fl_tag.addView(view);
                fl_tag.requestLayout();
                count++;
            }
            if(count == 0){
                ll_tag.setVisibility(View.GONE);
                view_bottom.setVisibility(View.GONE);
            }
        }else {
            view_bottom.setVisibility(View.GONE);
            ll_tag.setVisibility(View.GONE);
        }
    }

    private void initData(){
        token = readPreference("token");
        String id = readPreference("uid");
        uid = getIntent().getStringExtra("uid");
        if(id.equals(uid)){
            showMyPage(true);
            isMine = true;
        }
        imageWallList = new ArrayList<>();
        imageWallAdapter = new HorizontalImageWallAdapter(this, imageWallList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvImageWall.setLayoutManager(linearLayoutManager);
        rvImageWall.setAdapter(imageWallAdapter);
        rvImageWall.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastPosition + 1) == imageWallAdapter.getItemCount()&&
                        imageWallList.size() == page*PAGECOUNT){
                    page += 1;
                    getImageWall();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
        readPersonInfo(token, uid);
        getImageWall();
    }

    /**
     * 获取用户信息
     * @param token
     * @param uid
     */
    private void readPersonInfo(final String token, final String uid){

        new BaseHttpAsyncTask<Void, Void, PersonInfoResult>(this, true){

            @Override
            protected void onCompleteTask(PersonInfoResult personInfoResult) {
                if(personInfoResult.getCode() == BaseResult.SUCCESS){
                    result = personInfoResult;
                    initPersonInfoData(personInfoResult);
                }else{
                    if(StringUtil.isEmpty(personInfoResult.getMsg())){
                        showToastMsg("获取用户信息失败");
                    }else{
                        showToastMsg(personInfoResult.getMsg());
                    }
                }
            }

            @Override
            protected PersonInfoResult run(Void... params) {
                return HttpRequestUtil.getInstance().getPersonInfo(token, uid);
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_left:
                finish();
                break;
            case R.id.rl_chating:
                Intent chatIntent = new Intent(PersonCenterActivity.this, ChatsActivity.class);
                chatIntent.putExtra("chatType", ChatsActivity.CHATTYPE_SINGLE);
                chatIntent.putExtra("id", MD5.md5(result.getPersonInfo().getUid()));
                chatIntent.putExtra("uid", result.getPersonInfo().getUid());
                chatIntent.putExtra("name", result.getPersonInfo().getNickname());
                chatIntent.putExtra("headphoto", result.getPersonInfo().getHeadurl());
                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setImId(MD5.md5(result.getPersonInfo().getUid()));
                contactInfo.setUid(result.getPersonInfo().getUid());
                contactInfo.setNickName(result.getPersonInfo().getNickname());
                contactInfo.setHeadPhoto(result.getPersonInfo().getHeadurl());
                IMDatabaseHelper imDatabaseHelper = new IMDatabaseHelper(getApplicationContext());
                imDatabaseHelper.saveContactInfo(contactInfo);
                startActivity(chatIntent);
                break;
            case R.id.rl_follow:
                like(token, result.getPersonInfo().getUid());
                break;
            case R.id.rl_like:
                Intent likeIntent = new Intent(PersonCenterActivity.this, TogetherAndCustomTourActivity.class);
                likeIntent.putExtra("type","like");
                likeIntent.putExtra("uid",uid);
                likeIntent.putExtra("title","喜欢的约游");
                likeIntent.putExtra("isOwner", true);
                if (!isMine){
                    likeIntent.putExtra("title","TA喜欢的约游");
                    likeIntent.putExtra("isOwner", false);
                }
                startActivity(likeIntent);
                break;
            case R.id.rl_join:
                Intent joinIntent = new Intent(PersonCenterActivity.this, TogetherAndCustomTourActivity.class);
                joinIntent.putExtra("type","join");
                joinIntent.putExtra("uid",uid);
                joinIntent.putExtra("title","已报名的约游");
                joinIntent.putExtra("isOwner", true);
                if (!isMine){
                    joinIntent.putExtra("title","TA报名的约游");
                    joinIntent.putExtra("isOwner", false);
                }
                startActivity(joinIntent);
                break;
            case R.id.rl_start: //发起的
                Intent startIntent = new Intent(PersonCenterActivity.this, TogetherAndCustomTourActivity.class);
                startIntent.putExtra("type","owner");
                startIntent.putExtra("uid",uid);
                startIntent.putExtra("title","发起的约游");
                startIntent.putExtra("isOwner", true);
                if (!isMine){
                    startIntent.putExtra("title","TA发起的约游");
                    startIntent.putExtra("isOwner", false);
                }
                startActivity(startIntent);
                break;
            case R.id.rl_state:
                Intent stateIntent = new Intent(PersonCenterActivity.this, PublishStateActivity.class);
                stateIntent.putExtra("uid", uid);
                stateIntent.putExtra("title","发布的动态");
                stateIntent.putExtra("flag", isMine);
                if (!isMine){
                    stateIntent.putExtra("title","TA的动态");
                }
                startActivity(stateIntent);
                break;
            case R.id.iv_background:
                if (isMine){
                    chosePhoto(2);
                }
                break;
            case R.id.iv_head_photo:
                Bundle bundle = new Bundle();
                bundle.putSerializable("photos", photoList);
                bundle.putSerializable("position", 0);
                CommonUtils.launchActivity(PersonCenterActivity.this, PhotoPreviewActivity.class, bundle);
                break;
            case R.id.tv_more:
                Intent imagewallIntent = new Intent(PersonCenterActivity.this, GridImageWallActivity.class);
                startActivity(imagewallIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 自己看自己的个人信息
     * @param isShow
     */
    private void showMyPage(boolean isShow){
        if(isShow){
            rl_bottom.setVisibility(View.GONE);
            tv_start.setText("我发起的约游");
            tv_sign.setText("我报名的约游");
            tv_like.setText("我喜欢的约游");
            tv_trends.setText("我发布的动态");
            tv_tip_trends.setText("发布的动态");
        }
    }

    /**
     * 切换关注/取消关注按钮
     * @param isfollow
     */
    private void changeBtnFollow(boolean isfollow){
        if(isfollow){
            iv_follow.setVisibility(View.GONE);
            tv_follow.setText("取消关注");
            tv_follow.setTextColor(getResources().getColor(R.color.grey64));
        }else{
            iv_follow.setVisibility(View.VISIBLE);
            tv_follow.setText("关注");
            tv_follow.setTextColor(getResources().getColor(R.color.text_orange));
        }
    }

    /**
     * 添加/取消喜欢接口
     * @param token
     * @param touser
     */
    private void like(final String token, final String touser) {

        new BaseHttpAsyncTask<Void, Void, BaseResult>(this, false) {

            @Override
            protected void onCompleteTask(BaseResult baseResult) {
                if (baseResult.getCode() == BaseResult.SUCCESS) {
                    int count = 0;
                    if (!StringUtil.isBlank(readPreference("followcount"))){
                        count = Integer.parseInt(readPreference("followcount"));
                    }

                    if("true".equals(result.getPersonInfo().getIslike())){
                        changeBtnFollow(false);
                        result.getPersonInfo().setIslike("false");
                        showToastMsg("已取消关注");
                        count-=1;
                        if (count < 1){
                            count = 0;
                        }
                    }else{
                        changeBtnFollow(true);
                        result.getPersonInfo().setIslike("true");
                        showToastMsg("已关注");
                        count+=1;
                    }
                    setResult(1001);
                    writePreference("followcount", count+"");
                } else {
                    if (StringUtil.isBlank(baseResult.getMsg())) {
                        showToastMsg("操作失败");
                    } else {
                        showToastMsg(baseResult.getMsg());
                    }
                }
            }

            @Override
            protected BaseResult run(Void... params) {
                return HttpRequestUtil.getInstance().like(token, touser);
            }
        }.execute();
    }

    /**
     * 相册选择照片
     * @param index
     */
    public void chosePhoto(int index) {
        File fos = null;
        try {
            fos = new File(IMAGE_FILE_LOCATION);
            fos.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(fos);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        if (index == 1) {
            startActivityForResult(intent, 3); // 如果requestCode=3，是修改头像
        } else if (index == 2) {
            startActivityForResult(intent, 2); // requestCode=22,是相册添加照片
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (imageUri != null) {
                        File file = new File(imageUri.getPath());
                        List<File> imgs = new ArrayList<File>();
                        imgs.add(file);
                        addPhoto(imgs);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 向服务器添加照片
     *
     * @param imgs
     */
    private void addPhoto(final List<File> imgs) {
        new BaseHttpAsyncTask<Void, Void, PhotoUploadResult>(this) {

            @Override
            protected void onCompleteTask(final PhotoUploadResult result) {
                if (result.getCode() == BaseResult.SUCCESS) {
                    String photourl = result.getPhotoList().get(0);
                    ModifyPersonInfo info = new ModifyPersonInfo();
                    info.setToken(readPreference("token"));
                    info.setPic_url(photourl);
                    UrlImageViewHelper.setUrlDrawable(iv_background, photourl, R.drawable.icon_default_bbs_photo);
                    modifyUserInfo(info);
                } else {
                    if (StringUtil.isBlank(result.getMsg())) {
                        showToastMsg("操作失败");
                    } else {
                        showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected PhotoUploadResult run(Void... params) {
                return HttpRequestUtil.getInstance().uploadPhoto(imgs);
            }

        }.execute();
    }

    /**
     * 修改用户信息
     */
    private void modifyUserInfo(final ModifyPersonInfo info) {
        new BaseHttpAsyncTask<Void, Void, BaseResult>(this, true) {

            @Override
            protected void onCompleteTask(BaseResult baseResult) {
                if (baseResult.getCode() == BaseResult.SUCCESS) {
                    showToastMsg("修改成功");
                } else {
                    if (StringUtil.isBlank(baseResult.getMsg())) {
                        showToastMsg("修改信息失败");
                    } else {
                        showToastMsg(baseResult.getMsg());
                    }
                }
            }

            @Override
            protected BaseResult run(Void... params) {
                return HttpRequestUtil.getInstance().modifyPersonInfo(info);
            }
        }.execute();
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
                    if (imageWallList.size() < 1){
                        rvImageWall.setVisibility(View.GONE);
                        viewStub.inflate();
                        return;
                    }
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
                        readPreference("token"),1,PAGECOUNT*page);
            }
        }.execute();
    }
}
