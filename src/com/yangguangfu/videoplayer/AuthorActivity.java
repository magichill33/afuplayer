package com.yangguangfu.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
 * 关于作者界面
 */
public class AuthorActivity extends BaseActivity implements
		OnClickListener {
     /**
      * 返回主页按钮
      */
	private Button gomainmenu;
	 /**
     * 我的作品按钮
     */
	private Button author_give_autor;
	 /**
     * 作品图标按钮
     */
	private ImageButton author_goversion;
	 /**
     * 声音播放暂停、开始按钮
     */
	private Button author_music;
	 /**
     * 声音服务类
     */
	private SoundService soundService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置为全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//加载布局文件
		setContentView(R.layout.author_activity);
		Log.v("ThAuthor", "onCreate()");
		
		//初始化按钮
		gomainmenu = (Button) this.findViewById(R.id.author_gomainmenu);
		author_give_autor = (Button) this.findViewById(R.id.author_give_autor);
		author_goversion = (ImageButton) this.findViewById(R.id.author_goversion);
		author_music = (Button) this.findViewById(R.id.author_music);
		
		//设置按钮点击事件
		gomainmenu.setOnClickListener(this);
		author_give_autor.setOnClickListener(this);
		author_goversion.setOnClickListener(this);
		author_music.setOnClickListener(this);
		//初始化声音服务类：播放按钮音效、播放作者家乡音乐
		soundService = new SoundService(AuthorActivity.this);
//		AppConnect.getInstance(this).getPushAd(); 
	}

	@Override
	public void onClick(View v) {
		
		//没点击一次按钮，播放一次点击音效
		if (soundService != null) {
			soundService.playButtonMusic(R.raw.button);
		}

		switch (v.getId()) {
		case R.id.author_gomainmenu:
			finish();
			break;
		case R.id.author_give_autor:
//			AppConnect.getInstance(this).showMore(this);
//			Intent j = new Intent(this, SendEmailAuthor.class);
//			this.startActivityForResult(j, 814);

			break;

		case R.id.author_goversion:
			Intent i = new Intent(this, VersionInfoActivity.class);
//			startActivityForResult(i, 60);
			startActivity(i);
			break;

		case R.id.author_music:
			//播放作者家乡音乐，如果播放的时候，点击将暂停，反正亦然；
			if (soundService.isAuthorHometownMusicPlaying(R.raw.myhome)) {
				soundService.authorHometownMusicPause(R.raw.myhome);

				author_music.setText(R.string.author_music_play);
			} else {
				soundService.authorHometownMusicPlay(R.raw.myhome);
				author_music.setText(R.string.author_music_pause);
			}

			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==60){
			
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (soundService != null) {
			soundService.playButtonMusic(R.raw.button);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(AuthorActivity.this, R.string.app_hint_msg, 0)
					.show();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		//界面销毁是影音停止，并释放资源
		soundService.authorHometownMusicStop(R.raw.myhome);
		super.onDestroy();
	}



	@Override
	protected void onResume() {
		Log.v("ThAuthor", "onResume()");
		//界面获得焦点的时候，播放作者家乡音乐
		soundService.authorHometownMusicPlay(R.raw.myhome);
		super.onResume();
	}


	@Override
	protected void onStop() {
		Log.v("ThAuthor", "onStop()");
		//完全不显示的时候是音乐停止，并释放资源
		soundService.authorHometownMusicStop(R.raw.myhome);
		super.onStop();
	}

}