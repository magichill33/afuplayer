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
 * @author ����
 *������ͨ�����ܣ�������磬����������������򻺴�·����log�����SDcard�Ƿ����
 */
public class LoginActivity extends BaseActivity {

	private static final String TAG = "LoginActivity";
	/**
	 * �Ƿ���Ϣ�˹ر�����Ҳ��
	 */
	private boolean isClosed = false;
	/**
	 * ������ҳ��Ϣ
	 */
	private final static int SEND_MESSAGE_AND_CLOSE_WINDOW = 1;
	/**
	 * ���ڸ�����Ϣ
	 */
	private final int MSG_ID_CLOSE = 2;
	
	/**
	 * �������������
	 */
	private PrefService prefService;
	/**
	 * �Ի���
	 */
	private AlertDialog menuDialog;
	
	private View menuView;
	
	
	/**
	 * ������Ϣ��Handler��android���첽��һ�ַ�ʽ����Handler���Ըɺܶ��£�������ͬ��
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
		
		
		//��Ϊ��������
		 prefService = new PrefService(this);
		 if(Constants.isChangleLocale){
			 PrefService.changleLocale(this, prefService);
		 }
		
		 setContentView(R.layout.login_activity);
		 //����Activity
		 ActivityHolder.getInstance().addActivity(this);

		Log.v(TAG, "onCreate()");
		
		//�ж��Ƿ���SDcard
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			//�ӳ�3�������ҳ��
			handler	.sendEmptyMessageDelayed(SEND_MESSAGE_AND_CLOSE_WINDOW,
							3000);

		} else {
			//û��SDcard�ǵ����Ի���
			openOptionsDialog();

		}

	}


	/**
	 * ��������:
	 * 
	 * SDCard������ʱ��ʾ�Ƿ�������ء�
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
				// am.restartPackage(getPackageName());// ���°�װ
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
		//�����Ļ���ٽ���
		handler.sendEmptyMessage(SEND_MESSAGE_AND_CLOSE_WINDOW);
		Log.v(TAG, "onTouchEvent(MotionEvent event)");
		return true;
	}
	/**
	 * ������ҳ
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