package com.yangguangfu.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.yangguangfu.videoplayer.util.SoundService;
/**
 * 
 * @author 阿福
 *    版本信息类
 */

public class VersionInfoActivity extends BaseActivity implements
		OnClickListener {

	private Button mInfomationToSendfriend;
	private Button mGoMainmenu;
	private SoundService mSoundService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.version_infomation_activity);
//		
//		AppConnect.getInstance(this).getPushAd(); 
		mGoMainmenu = (Button)findViewById(R.id.gomainmenu);
		mGoMainmenu.setOnClickListener(this);
		mInfomationToSendfriend = (Button)findViewById(R.id.infomation_sendfriend);
		mInfomationToSendfriend.setOnClickListener(this);
		mSoundService =new SoundService(VersionInfoActivity.this);
	}

	@Override
	public void onClick(View v) {
		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
		
//		Intent intent;
		switch (v.getId()) {
		case R.id.gomainmenu:
			finish();
			break;

		case R.id.infomation_sendfriend:
//			AppConnect.getInstance(this).showMore(this);
//			intent = new Intent(VersionInfoActivity.this, SendEmailFriend.class);
//			startActivity(intent);
			break;

		default:
			break;
		}

	}
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, MainMenuActivity.class);
			startActivity(intent);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	

}