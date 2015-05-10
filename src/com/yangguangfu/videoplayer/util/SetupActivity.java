package com.yangguangfu.videoplayer.util;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yangguangfu.videoplayer.BaseActivity;
import com.yangguangfu.videoplayer.R;
/**
 * 
 * @author 阿福
 *   设置类，负责设置
 *
 */
public class SetupActivity extends BaseActivity implements
		View.OnClickListener, View.OnTouchListener,
		GestureDetector.OnGestureListener {

	private ImageView options_language_left;// =0x7f07000b;
	private ImageView options_language_right;// =0x7f07000d;
	private TextView options_language_select;// =0x7f07000c;
	private GestureDetector gesture;
	private PrefService pref;
	private TextView options_title;// =0x7f070009;
	private TextView options_music;// =0x7f07000e;
	private TextView options_sound;// =0x7f070010;
	private ToggleButton options_music_button;// =0x7f07000f;
	private ToggleButton options_sound_button;// =0x7f070011;
	private TextView options_language;//
	private  Button options_sure;//
	private boolean isMusic;
	private boolean isSound;
	private SoundService mSoundService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		pref = new PrefService(this);
		if(Constants.isChangleLocale){
			PrefService.changleLocale(this, pref);
		 }
		
		setContentView(R.layout.setup_activiy);
		
		mSoundService =new SoundService(this);
		options_language = (TextView) this.findViewById(R.id.options_language);//
		options_language_left = (ImageView) this
				.findViewById(R.id.options_language_left);// =0x7f07000b;
		options_language_right = (ImageView) this
				.findViewById(R.id.options_language_right);// =0x7f07000d;
		options_language_select = (TextView) this
				.findViewById(R.id.options_language_select);// =0x7f07000c;
		options_music = (TextView) this.findViewById(R.id.options_music);// =0x7f07000e;
		options_sound = (TextView) this.findViewById(R.id.options_sound);// =0x7f070010;
		options_title = (TextView) this.findViewById(R.id.options_title);// =0x7f070009;
		pref = new PrefService(this);
		gesture = new GestureDetector(this);
		options_sure = (Button) this.findViewById(R.id.options_sure);// =0x7f070009;
		options_sure.setOnClickListener(this);
		options_language_select.setOnTouchListener(this);
		options_language_left.setOnClickListener(this);
		options_language_right.setOnClickListener(this);

		options_music_button = (ToggleButton) this
				.findViewById(R.id.options_music_button);// =0x7f070009;
		options_sound_button = (ToggleButton) this
				.findViewById(R.id.options_sound_button);// =0x7f070009;
		isMusic = pref.isButtonMusic();
		isSound = pref.isAuthorHometownMusic();

		options_music_button.setChecked(isMusic);
		options_sound_button.setChecked(isSound);

		options_music_button.setOnClickListener(this);
		options_sound_button.setOnClickListener(this);
		updateUI();
//		AppConnect.getInstance(this).getPushAd(); 
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(SetupActivity.this, R.string.options_hint_msg, 0)
					.show();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			// Toast.makeText(SetupActivity.this, R.string.options_hint_msg,
			// 0).show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void updateUI() {

		options_title.setText(R.string.options_title);

		int languageIndex = pref.getLanguage();
		switch (languageIndex) {
		case Constants.LANGUAGE_CHINESE:
			options_language_select.setText(R.string.options_language_chinese);
			options_language.setText(R.string.language_chinese);
			options_music.setText(R.string.options_music_chinese);
			options_sound.setText(R.string.options_sound_chinese);
			options_sure.setText(R.string.options_sure_chinese);
			if (isSound) {
				options_sound_button.setText(R.string.options_on_chinese);
			} else {
				options_sound_button.setText(R.string.options_off_chinese);
			}
			if (isMusic) {
				options_music_button.setText(R.string.options_on_chinese);
			} else {
				options_music_button.setText(R.string.options_off_chinese);
			}

			break;
		case Constants.LANGUAGE_ENGLISH:
			options_language_select.setText(R.string.options_language_english);
			options_language.setText(R.string.options_language);
			options_music.setText(R.string.options_music);
			options_sound.setText(R.string.options_sound);
			options_sure.setText(R.string.options_sure_english);
			if (isSound) {
				options_sound_button.setText(R.string.options_on_english);
			} else {
				options_sound_button.setText(R.string.options_off_english);
			}
			if (isMusic) {
				options_music_button.setText(R.string.options_on_english);
			} else {
				options_music_button.setText(R.string.options_off_english);
			}

			break;
		case Constants.LANGUAGE_TRAIDTION_CHINESE:
			options_title.setText(R.string.options_title_trandition_chinese);
			options_language_select
					.setText(R.string.options_language_trandition_chinese);
			options_language.setText(R.string.language_trandition_chinese);
			options_music.setText(R.string.options_music_trandition_chinese);
			options_sound.setText(R.string.options_sound_trandition_chinese);
			options_sure.setText(R.string.options_sure_trandition_chinese);
			if (isSound) {
				options_sound_button
						.setText(R.string.options_on_trandition_chinese);
			} else {
				options_sound_button
						.setText(R.string.options_off_trandition_chinese);
			}
			if (isMusic) {
				options_music_button
						.setText(R.string.options_on_trandition_chinese);
			} else {
				options_music_button
						.setText(R.string.options_off_trandition_chinese);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
		if ((e1.getX() - e2.getX()) > 5) {
			handleLeftFling(true);
			return true;
		} else if ((e2.getX() - e1.getX()) > 5) {
			handleLeftFling(false);
		}
		return false;
	}

	@SuppressWarnings("static-access")
	private void handleLeftFling(boolean isLeft) {

		int languageIndex = pref.getLanguage();
		if (isLeft) {
			languageIndex = (languageIndex - 1) % 3;
		} else {
			languageIndex = (languageIndex + 1) % 3;
		}
		if (languageIndex < 0) {
			languageIndex = languageIndex + 3;
		}

		if(Constants.isChangleLocale){
			pref.setLanguage(languageIndex);
			pref.changleLocale(this, pref);
			updateUI();
		 }
		

	}

	@Override
	public void onClick(View v) {
		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
		switch (v.getId()) {
		case R.id.options_language_left:
			handleLeftFling(true);
			break;
		case R.id.options_language_right:
			handleLeftFling(false);
			break;

		case R.id.options_music_button:
			pref.setButtonMusic(((ToggleButton) v).isChecked());
			break;
		case R.id.options_sound_button:
			pref.setAuthorHometownMusic(((ToggleButton) v).isChecked());
			break;

		case R.id.options_sure:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.options_language_select) {
			gesture.onTouchEvent(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


}
