package com.yangguangfu.videoplayer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yangguangfu.videoplayer.playlist.PlayListsActivity;
import com.yangguangfu.videoplayer.util.ActivityHolder;
import com.yangguangfu.videoplayer.util.Constants;
import com.yangguangfu.videoplayer.util.PrefService;
import com.yangguangfu.videoplayer.util.SetupActivity;
import com.yangguangfu.videoplayer.util.SoundService;
/**
 * 
 * @author 阿福
 * 
 * 1,主要是提供进入个各类的按钮。
 * 2,发退出软件的广播。
 * 3、监听按返回键
 *
 */
public class MainMenuActivity extends BaseActivity implements View.OnClickListener {
	private static final String tag = "MainMenuActivity";
	private PrefService mPrefService;
	private SoundService mSoundService;
	// menu菜单Dialog
	private AlertDialog menuDialog;
	private View mMenuView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		AppConnect.getInstance(this);	
		//设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 保持背光常亮的设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initialization();
		loadLayout();
//		AppConnect.getInstance(this).getPushAd(); 
//		new InitDataTask().execute((Void[])null);

	}

	
	// class
	class InitDataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			initialization();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			loadLayout();
//			setContentView(framelayout);
		}
	}
	//初始化软件设置信息
	private void initialization() {
		//人为设置语言
		if(mPrefService==null){
			mPrefService = new PrefService(this);
		}
		if(Constants.isChangleLocale){
			 PrefService.changleLocale(this, mPrefService);
		 }
		
	}
//加载布局文件
	private void loadLayout() {
		setContentView(R.layout.main_menu);
		
		//初始化声音服务类
		if(mSoundService==null){
			mSoundService =new SoundService(this);
		}
		
		Log.v(tag, "onCreate()");
		
		((ImageView) findViewById(R.id.mainmenu_information)).setOnClickListener(this);
		
		
//		ImageView mImageView =	(ImageView)findViewById(R.id.mainmenu_information);
//		mImageView.setOnClickListener(this);
//		
//		ImageView mImageView2 =	(ImageView)findViewById(R.id.mainmenu_information);
//		mImageView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(MainMenuActivity.this, VersionInfoActivity.class);
//				MainMenuActivity.this.startActivity(intent);
//				
//			}
//		});
//		
		
		((Button) findViewById(R.id.mainmenu_playlist)).setOnClickListener(this);
		((Button) findViewById(R.id.mainmenu_help))
				.setOnClickListener(this);
		((Button) findViewById(R.id.mainmenu_set)).setOnClickListener(this);

		((Button) findViewById(R.id.mainmenu_about_author))
				.setOnClickListener(this);
		((Button) findViewById(R.id.mainmenu_about_software))
				.setOnClickListener(this);

		((Button) findViewById(R.id.mainmenu_exit_player)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
	

		Intent intent;
		switch (v.getId()) {
		case R.id.mainmenu_playlist:
			intent = new Intent(this, PlayListsActivity.class);
			this.startActivityForResult(intent, 999);

			break;
		case R.id.mainmenu_about_software:
//			AppConnect.getInstance(this).showOffers(this);
//			intent = new Intent(this, VersionInfoActivity.class);
//			this.startActivity(intent);

			break;
		case R.id.mainmenu_set:
			intent = new Intent(this, SetupActivity.class);
			this.startActivity(intent);
			break;
		case R.id.mainmenu_exit_player:

			openOptionsDialog();

			break;
		case R.id.mainmenu_information:
			intent = new Intent(this, VersionInfoActivity.class);
			this.startActivity(intent);
			break;

		case R.id.mainmenu_about_author:
			intent = new Intent(this, AuthorActivity.class);
			this.startActivity(intent);
			break;

		case R.id.mainmenu_help:
			intent = new Intent(this, HelpActivity.class);
			this.startActivity(intent);
			break;
		case R.id.exit_player_yes:
//			AppConnect.getInstance(this).finalize();
			intent = new Intent(getPackageName() + ".ExitListenerReceiver");
//			intent = new Intent("com.yangguangfu.videoplayer.ExitListenerReceiver");
			sendBroadcast(intent);
			menuDialog.dismiss();
			 ActivityHolder.getInstance().finishAllActivity();
			
			 finish();
			break;

		case R.id.exit_player_no:
			menuDialog.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(mSoundService!=null){
			mSoundService.playButtonMusic(R.raw.button);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Toast.makeText(MainMenuActivity.this, R.string.app_hint_msg, 0)
					.show();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openOptionsDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	

	/**
	 * 方法功能:
	 * 
	 * 退出本程序对话框.
	 */
	private void openOptionsDialog() {

		mMenuView = View.inflate(this, R.layout.exit_dialog, null);
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(mMenuView);
		menuDialog.show();
		Button exitPlayerYes = (Button) mMenuView.findViewById(R.id.exit_player_yes);
		exitPlayerYes.setOnClickListener(this);
		Button exitPlayerNo = (Button) mMenuView.findViewById(R.id.exit_player_no);
		exitPlayerNo.setOnClickListener(this);

	}

	@Override
	protected void onRestart() {
		initialization();
		super.onRestart();
		Log.v(tag, " onRestart()");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initialization();
		loadLayout();
		Log.v(tag, "onResume()");
	}



}