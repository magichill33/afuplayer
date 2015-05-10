package com.yangguangfu.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yangguangfu.videoplayer.util.SoundService;
/**
 * 
 * @author 阿福
 *  使用说明
 */
public class HelpActivity extends BaseActivity implements OnClickListener {

	private Button mHelpGoMainmenu;
	private Button mHelpSuggest;
	private ImageButton mHelpGoVersion;
	private SoundService mSouSoundService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
//		AppConnect.getInstance(this).getPushAd(); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mHelpGoMainmenu = (Button) this.findViewById(R.id.help_gomainmenu);
		mHelpSuggest = (Button) this.findViewById(R.id.help_suggest);
		mHelpGoVersion = (ImageButton) this.findViewById(R.id.help_goversion);
		mSouSoundService = new SoundService(HelpActivity.this);
		mHelpGoMainmenu.setOnClickListener(this);
		mHelpSuggest.setOnClickListener(this);
		mHelpGoVersion.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (mSouSoundService != null) {
			mSouSoundService.playButtonMusic(R.raw.button);
		}

		switch (v.getId()) {
		case R.id.help_gomainmenu:
			finish();
			break;
		case R.id.help_suggest:
//			AppConnect.getInstance(this).showFeedback();

//			Intent i = new Intent(this, SendEmailAdvice.class);
//			this.startActivityForResult(i, 699);

			break;
		case R.id.help_goversion:
			Intent g = new Intent(this, VersionInfoActivity.class);
			startActivityForResult(g, 70);
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

		if (mSouSoundService != null) {
			mSouSoundService.playButtonMusic(R.raw.button);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(HelpActivity.this, R.string.app_hint_msg, 0).show();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


}