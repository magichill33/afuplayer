package com.yangguangfu.videoplayer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yangguangfu.videoplayer.util.ActivityHolder;
import com.yangguangfu.videoplayer.util.Constants;
import com.yangguangfu.videoplayer.util.PrefService;

/**
 * 
 * @author 阿福
 *启动类通常功能：检查网络，检查升级，创建程序缓存路径、log、检查SDcard是否存在
 */
public class LoginActivity extends BaseActivity {

	private static final String TAG = "LoginActivity";
	/**
	 * 是否发消息了关闭启动也了
	 */
	private boolean isClosed = false;
	/**
	 * 启动主页消息
	 */
	private final static int SEND_MESSAGE_AND_CLOSE_WINDOW = 1;
	/**
	 * 关于该类消息
	 */
	private final int MSG_ID_CLOSE = 2;
	
	/**
	 * 软件参数设置类
	 */
	private PrefService prefService;
	/**
	 * 对话框
	 */
	private AlertDialog menuDialog;
	
	private View menuView;
	
	
	/**
	 * 出来消息，Handler是android中异步的一种方式，用Handler可以干很多事，比如歌词同步
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEND_MESSAGE_AND_CLOSE_WINDOW:
				if (!isClosed) {
					isClosed = true;
					startMainMenuActivity();
					handler.sendEmptyMessageDelayed(MSG_ID_CLOSE, 1000);
				}
				super.handleMessage(msg);
			case MSG_ID_CLOSE:
				finish();

				break;
			default:
				break;
			}
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//人为设置语言
		 prefService = new PrefService(this);
		 if(Constants.isChangleLocale){
			 PrefService.changleLocale(this, prefService);
		 }
		
		 setContentView(R.layout.login_activity);
		 //管理Activity
		 ActivityHolder.getInstance().addActivity(this);

		Log.v(TAG, "onCreate()");
		
		//判断是否有SDcard
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			//延长3秒进入主页面
			handler	.sendEmptyMessageDelayed(SEND_MESSAGE_AND_CLOSE_WINDOW,
							3000);

		} else {
			//没有SDcard是弹出对话框
			openOptionsDialog();

		}

	}


	/**
	 * 方法功能:
	 * 
	 * SDCard不存在时提示是否继续加载。
	 */
	private void openOptionsDialog() {

		menuView = View.inflate(this, R.layout.is_sdcard_dialog, null);
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.show();
		Button exitPlayerYes = (Button) menuView
				.findViewById(R.id.is_sdcard_yes);
		exitPlayerYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ActivityManager am = (ActivityManager)
				// getSystemService(Context.ACTIVITY_SERVICE);
				// am.restartPackage(getPackageName());// 重新包装
				Intent intent = new Intent(getPackageName()
						+ ".ExitListenerReceiver");
				sendBroadcast(intent);
				menuDialog.dismiss();

			}
		});
		Button exitPlayerNo = (Button) menuView.findViewById(R.id.is_sdcard_no);
		exitPlayerNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessageDelayed(SEND_MESSAGE_AND_CLOSE_WINDOW,
						1000);
				menuDialog.dismiss();

			}
		});

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//点击屏幕快速进入
		handler.sendEmptyMessage(SEND_MESSAGE_AND_CLOSE_WINDOW);
		Log.v(TAG, "onTouchEvent(MotionEvent event)");
		return true;
	}
	/**
	 * 启动主页
	 */
	private void startMainMenuActivity() {

		Log.v(TAG, "closeWindow()");
		Intent intent = new Intent(this, MainMenuActivity.class);
		this.startActivity(intent);
	}
	
	   @Override
	protected void onDestroy() {
	    	  super.onDestroy(); 
	    	 ActivityHolder.getInstance().removeActivity(this);
	        handler.removeMessages(SEND_MESSAGE_AND_CLOSE_WINDOW);
	        handler.removeMessages(MSG_ID_CLOSE);
	      
	        
	  }

}