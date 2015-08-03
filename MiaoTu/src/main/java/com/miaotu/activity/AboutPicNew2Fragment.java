package com.miaotu.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.miaotu.R;

public class AboutPicNew2Fragment extends BaseFragment {

	private ImageView ivAboutPic;
	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_about, container, false);
		findView();
		init();
		return root;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ivAboutPic = null;
		root = null;
	}

	private void findView() {
		ivAboutPic = (ImageView) root.findViewById(R.id.iv_about);
	}

	@SuppressLint("NewApi")
	private void init() {
		ivAboutPic.setBackgroundResource(R.drawable.bg_guide_5);

//		ivAboutPic.setBackgroundResource(R.drawable.about_miaotu_three);
//		ivAboutPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		ivAboutPic.setImageResource(R.drawable.about_miaotu_three);
//		WindowManager wm = (WindowManager) getActivity().getSystemService(
//				Context.WINDOW_SERVICE);
//		Point size = new Point();
//		wm.getDefaultDisplay().getSize(size);
//		int width = size.x;
//		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) ivAboutPic
//				.getLayoutParams();
//		params.width = width;
//		params.height = width * 1575 / 1080;
//		ivAboutPic.setLayoutParams(params);
	}
}
