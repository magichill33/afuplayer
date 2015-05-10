package com.yangguangfu.videoplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yangguangfu.videoplayer.SoundView.OnVolumeChangedListener;
import com.yangguangfu.videoplayer.AFuVideoView.SizeChangeLinstener;
import com.yangguangfu.videoplayer.playlist.PlayListsActivity;
import com.yangguangfu.videoplayer.util.SoundService;

/**
 * 
 * @author ����
 *  ������,
 *  ��װ�߼����Զ��岥�Ž���
 *  
 */
public class VideoPlayerActivity extends BaseActivity {
	/**
	 * �����־
	 */
	private final static String TAG = "VideoPlayerActivity";

	/**
	 * װ��Ƶ�ļ���Ϣ���������������б�
	 */

	private static ArrayList<String> playList = null;

	/**
	 * �����б�λ��
	 */
	private static int position;

	/**
	 * �����˶���ʱ��
	 */
	private int playedTime;

	/**
	 * ��Ƶ��ʾ�ؼ�-������൱��Ҫ
	 */
	private AFuVideoView mVideoView = null;
	/**
	 * ���Ž�����
	 */
	private SeekBar seekBar = null;

	/**
	 * ��ʾ��Ƶʱ�䳤��
	 */
	private TextView durationTextView = null;
	/**
	 * ������ʾ��Ƶ�����˶���ʱ��
	 */
	private TextView playedTextView = null;
	/**
	 * ����ʶ����
	 */
	private GestureDetector mGestureDetector = null;
	/**
	 * ��������
	 */
	private AudioManager mAudioManager = null;

	/**
	 * �õ���ǰý������
	 */
	private int currentVolume = 0;

	private Button mGoToPlayList = null;
	private Button mPrevious = null;
	private Button mPlay = null;
	private Button mNext = null;
	private Button mGoToMainMenu = null;
	/**
	 * �������ư�ť
	 */
	private Button mSoundControl = null;

	private Button mControlerExit = null;

	/**
	 * ������Ƶ����
	 */
	private View mControlView = null;
	/**
	 * ������Ƶ����ľ��尴ť
	 */
	private PopupWindow mControlerPopupWindow = null;

	/**
	 * 
	 * ����������С
	 * 
	 */

	private SoundView mSoundView = null;
	/**
	 * �������ƴ���
	 */
	private PopupWindow mSoundWindow = null;

	/**
	 * �ֻ���ģ��������Ļ��
	 */
	private static int screenWidth = 0;
	/**
	 * �ֻ���ģ��������Ļ��
	 */
	private static int screenHeight = 0;
	/**
	 * ���������Ļ�ĸ�
	 */
	private static int controlViewHeight = 0;
	/**
	 * �ೣʱ�����ؿ������
	 */
	private final static int TIME = 6868;
	/**
	 * �Ƿ���ʾ�������
	 */
	private boolean isControllerShow = true;
	/**
	 * �Ƿ���ͣ
	 */
	private boolean isPaused = false;
	/**
	 * �Ƿ�����
	 */
	private boolean isFullScreen = false;
	/**
	 * �Ƿ��Ǿ���
	 */
	private boolean isSilent = false;

	/**
	 * �Ƿ���ʾ�����ؼ�
	 */
	private boolean isSoundShow = false;
	/**
	 * �Ƿ����߲��Ż������Ӧ�÷��𲥷�
	 */
	private boolean isOnline = false;

	/**
	 * ������Ļ
	 */
	private final static int SCREEN_FULL = 0;
	/**
	 * Ĭ����Ļ
	 */
	private final static int SCREEN_DEFAULT = 1;

	private boolean isChangedVideo = false;

	/**
	 * ���ؿ��������Ϣ
	 */
	private final static int HIDE_CONTROLER = 1;

	/**
	 * �ڲ����б��Ƿ�����Ϣ
	 */
	private final static int PAUSE = 3;
	/**
	 * ˢ�²���ʱ����϶���λ����Ϣ
	 */
	private final static int PROGRESS_CHANGED = 0;

	/**
	 * ��ͼ���Ӳ����б�Я������Ƶ��Ϣ
	 */
	private Intent mIntent;
	/**
	 * ��������ݵ�·���������ַ
	 */
	private Uri uri;

	private AlertDialog menuDialog;
	private View menuView;
	/**
	 * ���ư�ť����
	 */
	private SoundService mSoundService;

	private AlertDialog erroVideoDialog;
	private View mErroVideoViewDialog;
	
	
	/**
	 * ע���˳��¼�����
	 * 
	 */
	ExitListenerReceiver exitre = null;

	private void regListener() {
		exitre = new ExitListenerReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(this.getPackageName() + "."
				+ "ExitVideoPlayerActivity");
		this.registerReceiver(exitre, intentfilter);
	}

	/**
	 * ע��ȡ���˳��¼�����
	 * 
	 */
	private void unregisterListener() {
		if (exitre != null) {
			unregisterReceiver(exitre);
			exitre = null;
		}

	}

	class ExitListenerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			((Activity) arg0).finish();

		}

	}

	public class PausePlayerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("yangguangfu.mediaplayer.pause".equals(intent.getAction())) {
				mHandler.sendEmptyMessageDelayed(PAUSE, 950);
			}

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		
		initWindows();
		
		//����ͼ���ؽ���
		setContentView(R.layout.video_view);
		// ��Ƶ��ʾ,����Ҫ
		mVideoView = (AFuVideoView) findViewById(R.id.vv);
//
//		Log.v(TAG, "onCreate()");
//
//		Log.d("OnCreate", getIntent().toString());
//		// ����Ǵ�
//		// uri = getIntent().getData();
//
//		Log.d(TAG, "The main thread id = " + Thread.currentThread().getId()
//				+ "\n");

		getPlaydata();


		initContronlView();
		
		initVideoView();

		startPlay();

	}

	@SuppressWarnings("deprecation")
	private void initContronlView() {
		
		getScreenSize();
		
		mSoundService = new SoundService(this);
		
		// ���ؿ�����Ƶ���档
		mControlView = getLayoutInflater().inflate(R.layout.controler_view,
				null);

		// PopupWindow ��Ϊһ���û����� �����俪��Ҳ��ActivityҪС
		mControlerPopupWindow = new PopupWindow(mControlView);
		
		
		// ���ؿ������
		Looper.myQueue().addIdleHandler(new IdleHandler() {

			@Override
			public boolean queueIdle() {

				// TODO Auto-generated method stub
				if (mControlerPopupWindow != null && mVideoView.isShown()) {
					mControlerPopupWindow.showAtLocation(mVideoView,
							Gravity.BOTTOM, 0, 0);

					mControlerPopupWindow.update(0, 0, screenWidth,
							controlViewHeight);

					Log.d(TAG, "The worker thread id = "
							+ Thread.currentThread().getId() + "\n");
				}

				return false;
			}
		});

		// ��ʾ��Ƶ�ܳ���ʱ��
		durationTextView = (TextView) mControlView.findViewById(R.id.duration);
		// ��ʾ��Ƶ�����˶���ʱ��
		playedTextView = (TextView) mControlView.findViewById(R.id.has_played);

		mGoToMainMenu = (Button) mControlView	.findViewById(R.id.control_goto_menu);

		// ��һ��Ƶ
		mPrevious = (Button) mControlView.findViewById(R.id.control_previous);
		
		// ������Ƶ
		mPlay = (Button) mControlView.findViewById(R.id.control_play_state);
		
		// ��һ��Ƶ
		mNext = (Button) mControlView.findViewById(R.id.control_next);

		// ��������
		mSoundControl = (Button) mControlView.findViewById(R.id.control_sound_control);

		mControlerExit = (Button) mControlView	.findViewById(R.id.controler_exit);
		
		// �����б�
		mGoToPlayList = (Button) mControlView		.findViewById(R.id.control_goto_play_lists);

		
		
		
		
		
		//�����ť�ļ���

		mGoToMainMenu.setOnClickListener(mGoToMainMenuListener);
//		mPrevious.setOnClickListener(mPreviousListener);
		
		mPrevious.setOnClickListener(mPlayListener);
		mPlay.setOnClickListener(mPlayListener);
		
		mNext.setOnClickListener(mNextListener);
		
		mSoundControl.setOnClickListener(mSoundControlListener);
		
		mControlerExit.setOnClickListener(mControlerExitListener);
		
		mGoToPlayList.setOnClickListener(mGoToPlayListListener);

		
		initSoundView();

		// ���Ž�����,�����϶�
		seekBar = (SeekBar) mControlView.findViewById(R.id.seekbar);
		
		//1,SeekBar
//		  2,����ǰ����
//		  3���϶�����
		
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			//���϶������ı��ʱ��ִ��
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				
				if (fromUser) {
					mVideoView.seekTo(progress);

				}
			}

			//���϶��մ�����ʱ��ִ��
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mHandler.removeMessages(HIDE_CONTROLER);
			}

			//���϶��մ�����ʱ��ִ��
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});
		
		

		mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				
				
				long time = System.currentTimeMillis();
				if (time - lastTimeonDoubleTap < CLICK_INTERVAL) {
					return true;
				}
				lastTimeonDoubleTap = time;
				
				

				if (isFullScreen) {
					setVideoScale(SCREEN_DEFAULT);
				} else {
					setVideoScale(SCREEN_FULL);
				}
				
				
				isFullScreen = !isFullScreen;

				if (isControllerShow) {
					showController();
				}

				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				
				long time = System.currentTimeMillis();
				if (time - lastTimeonSingleTapConfirmed < CLICK_INTERVAL) {
					return true;
				}
				lastTimeonSingleTapConfirmed = time;
				
				

				if (!isControllerShow) {
					showController();
					hideControllerDelay();
				} else {
					cancelDelayHide();
					hideController();
				}
				
				
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				
				
				long time = System.currentTimeMillis();
				if (time - lastTimeonLongPress < CLICK_INTERVAL) {
					return;
				}
				lastTimeonLongPress = time;
				
				
				if (isPaused) {
					mVideoView.start();
					mPlay.setText(R.string.control_pause_state);
					cancelDelayHide();
					hideControllerDelay();
				} else {
					mVideoView.pause();
					mPlay.setText(R.string.control_play_state);
					cancelDelayHide();
					showController();
				}
				isPaused = !isPaused;

			}
		});

		// position = -1;
		// �õ�URL
	}

	private void initSoundView() {
		/**
		 * ��������
		 * 
		 */
		mSoundView = new SoundView(this);
		mSoundView.setOnVolumeChangeListener(new OnVolumeChangedListener() {

			@Override
			public void setYourVolume(int index) {

				cancelDelayHide();
				updateVolume(index);
				hideControllerDelay();
			}
		});

		mSoundWindow = new PopupWindow(mSoundView);

		// ��Ƶ�������
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// �õ���Ƶ��ǰ����
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		// ����ťЧ��
		mSoundControl.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				
				
				long time = System.currentTimeMillis();
				if (time - lastTimeonLongClick < CLICK_INTERVAL) {
					return true;
				}
				lastTimeonLongClick = time;
				
				

				if (isSilent) {
					mSoundControl.setText(R.string.control_soundControl);
				} else {
					mSoundControl.setText(R.string.control_soundControl_mute);
				}
				isSilent = !isSilent;
				updateVolume(currentVolume);
				cancelDelayHide();
				hideControllerDelay();
				return true;
			}

		});
	}

	private void initVideoView() {
		mVideoView.setOnSeekCompleteListener(new OnSeekCompleteListener() {
			
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				// TODO Auto-generated method stub
				//���϶�����ʾȡ����
				Toast.makeText(VideoPlayerActivity.this, "�϶�����", 1).show();
			}
		});
		//2.3
		mVideoView.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				//�������ſ���������ʼ�϶�����������ɡ������϶�������ɡ�
				switch (what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					//�������ſ���������ʼ�϶�
					Toast.makeText(VideoPlayerActivity.this, "��ʼ����", 1).show();
					break;
					
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					//��������ɡ������϶��������
					Toast.makeText(VideoPlayerActivity.this, "�������", 1).show();
					break;
					
				

				default:
					break;
				}
				
				return true;
			}
		});

		/**
		 * �������ų���ʱ�Ĵ���
		 */
		mVideoView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				
				if (mVideoView != null) {
					mVideoView.stopPlayback();
				}

				errorsInformationDialog();
				return true;

			}

		});

		mVideoView.setSizeChangeLinstener(new SizeChangeLinstener() {

			@Override
			public void doMyThings() {
				// TODO Auto-generated method stub
				setVideoScale(SCREEN_DEFAULT);
			}

		});

		

		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				
				
				setVideoScale(SCREEN_DEFAULT);
				
				isFullScreen = false;
				if (isControllerShow) {
					showController();
				}

				int i = mVideoView.getDuration();
				Log.d("onCompletion", "" + i);
				//��Ƶ�ļ���seekBar��������Ƶ�ж೤��ôseekBar�϶�Ҳ�������Χ��
				seekBar.setMax(i);
				
				
				
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				if (hour > 0) {
					durationTextView.setText(String.format("%02d:%02d:%02d",
							hour, minute, second));
				} else {
					durationTextView.setText(String.format("%02d:%02d", minute,
							second));
				}
				
				mVideoView.start();

				mPlay.setText(R.string.control_pause_state);
				
				hideControllerDelay();
				
				mHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {

				
				//����ǲ����������ַ�������ļ��е�ַ
				if (uri != null) {
					if (isOnline) {
						if (mVideoView != null) {
							finish();
							mVideoView.stopPlayback();
						}

						isOnline = false;
					} else {
						if (mVideoView != null) {
							finish();
							mVideoView.stopPlayback();
						}

						isOnline = false;
					}

				} else {

					int n = playList.size();
					isOnline = false;
					if (++position < n) {
						mVideoView.setVideoPath(playList.get(position));
					} else {
						Toast.makeText(VideoPlayerActivity.this,
								R.string.last_video_msg, 0).show();
						--position;
						// vv.setVideoPath(playList.get(position).path);
					}

				}

			}
		});
	}

	private void getPlaydata() {
		mIntent = getIntent();
		if (mIntent != null) {
			// ������������𲥷ŵ�ʱ�򡢱����ļ��з��𲥷�
			uri = mIntent.getData();
//			mIntent.setData("");
			
//			 URLEncoder.encode(uri.toString());//http://61.158.246.14/youku/67738070F7B33820298B373A84/0300200100503D4C69876405987B01F3213A7B-C87E-6774-946F-A9ACE72E1B30.mp4
			//�õ�λ��
			position = mIntent.getIntExtra("CurrentPosInMediaIdList", 0);
			//�õ������б�
			playList = mIntent.getStringArrayListExtra("MediaIdList");
		}
		
	}

	//��ʼ�����ڲ���
	private void initWindows() {
		// ����û�б���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// ���ֱ��ⳣ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// ���ֱ��ⳣ��������(����һ��)
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		regListener();
	}
	
	
	OnPreparedListener linstent = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer arg0) {

			setVideoScale(SCREEN_DEFAULT);
			isFullScreen = false;
			if (isControllerShow) {
				showController();
			}

			int i = mVideoView.getDuration();
			Log.d("onCompletion", "" + i);
			seekBar.setMax(i);
			i /= 1000;
			int minute = i / 60;
			int hour = minute / 60;
			int second = i % 60;
			minute %= 60;
			if (hour > 0) {
				durationTextView.setText(String.format("%02d:%02d:%02d",
						hour, minute, second));
			} else {
				durationTextView.setText(String.format("%02d:%02d", minute,
						second));
			}
			mVideoView.start();

			mPlay.setText(R.string.control_pause_state);
			hideControllerDelay();
			mHandler.sendEmptyMessage(PROGRESS_CHANGED);
		}
	};

	private static long CLICK_INTERVAL = 800;
	private long lastTime;
	private long lastTimeonProgressChanged;
	private long lastTimeonDoubleTap;
	private long lastTimeonSingleTapConfirmed;
	private long lastTimeonLongPress;
	private long lastTimeonLongClick;
	private long lastTimemGoToMainMenuListener;
	private long lastTimemPreviousListener;
	private long lastTimemPlayListener;
	private long lastTimemNextListener;
	private long lastTimemControlerExitListener;
	private long lastTimemSoundControlListener;
	private long lastTimemGoToPlayListListener;

	private View.OnClickListener mGoToMainMenuListener = new View.OnClickListener() {
		public void onClick(View v) {
			
		//��ֹƵ��������µĴ���
			long time = System.currentTimeMillis();
			if (time - lastTimemGoToMainMenuListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemGoToMainMenuListener = time;
			
			
			//�����Ч
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}

			//�ص���ҳ
			Intent intent = new Intent(VideoPlayerActivity.this,
					MainMenuActivity.class);
			startActivity(intent);
		}
	};

	private View.OnClickListener mPreviousListener = new View.OnClickListener() {
		public void onClick(View v) {
			long time = System.currentTimeMillis();
			
			if (time - lastTimemPreviousListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemPreviousListener = time;
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			isOnline = false;
			int n = playList.size();
			if (--position >= 0 && position < n) {
				mVideoView.setVideoPath(playList.get(position));

				cancelDelayHide();
				hideControllerDelay();
			} else {
				/* VideoPlayer.this.finish(); */
				Toast.makeText(VideoPlayerActivity.this,
						R.string.front_video_msg, 0).show();
				position = 0;
				if (position >= 0 && position < n) {
					mVideoView.setVideoPath(playList.get(position));
				}

			}
		}
	};

	private View.OnClickListener mPlayListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.control_play_state:
				
				//��ֹƵ��������´���
				long time = System.currentTimeMillis();
				if (time - lastTimemPlayListener < CLICK_INTERVAL) {
					return;
				}
				lastTimemPlayListener = time;
				
				
				//���Ű�ť��Ч
				if (mSoundService != null) {
					mSoundService.playButtonMusic(R.raw.button);
				}
				
				
				cancelDelayHide();
				

				if (isPaused) {
					mVideoView.start();
					mPlay.setText(R.string.control_pause_state);

					hideControllerDelay();
				} else {
					mVideoView.pause();
					mPlay.setText(R.string.control_play_state);

				}
				isPaused = !isPaused;

				
				break;

			case R.id.control_previous:
				

//				long time = System.currentTimeMillis();
//				if (time - lastTimemPreviousListener < CLICK_INTERVAL) {
//					return;
//				}
//				lastTimemPreviousListener = time;
				
				if (mSoundService != null) {
					mSoundService.playButtonMusic(R.raw.button);
				}
				isOnline = false;
				int n = playList.size();
				if (--position >= 0 && position < n) {
					mVideoView.setVideoPath(playList.get(position));

					cancelDelayHide();
					hideControllerDelay();
					
				} else {
					/* VideoPlayer.this.finish(); */
					Toast.makeText(VideoPlayerActivity.this,
							R.string.front_video_msg, 0).show();
					position = 0;
					if (position >= 0 && position < n) {
						mVideoView.setVideoPath(playList.get(position));
					}

				}
			
				
				
				break;
			}
			
			
		}
	};

	private View.OnClickListener mNextListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			//��ֹƵ�����
			long time = System.currentTimeMillis();
			if (time - lastTimemNextListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemNextListener = time;
			
			
			
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			
			
			if(playList == null)
				return;
//			
//			String i = "test";
//			
//			if(i.endsWith("ddd")){
//				
//			}
//			--------------------
//			if("ddd".endsWith(i)){
//				
//			}
				
			if(playList != null){
				int n = playList.size();
				isOnline = false;
				if (++position < n && position >= 0) {
					
					String playUri = playList.get(position);
					mVideoView.setVideoPath(playUri);
					
					cancelDelayHide();
					hideControllerDelay();
					
					
				} else {
					//
					Toast.makeText(VideoPlayerActivity.this,
							R.string.last_video_msg, 0).show();
					if (position > 0) {
						--position;
					}

					if (position >= 0 && position < n) {
						
						String playUri = playList.get(position);
						mVideoView.setVideoPath(playUri);
					}

				}
			}

		
		}
	};

	private View.OnClickListener mSoundControlListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			long time = System.currentTimeMillis();
			if (time - lastTimemSoundControlListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemSoundControlListener = time;
			
			
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}

			cancelDelayHide();
			
			if (isSoundShow) {
				mSoundWindow.dismiss();
			} else {
				if (mSoundWindow.isShowing()) {
					mSoundWindow.update(15, 0, SoundView.MY_WIDTH,
							SoundView.MY_HEIGHT);
				} else {
					mSoundWindow.showAtLocation(mVideoView, Gravity.RIGHT
							| Gravity.CENTER_VERTICAL, 15, 0);
					mSoundWindow.update(15, 0, SoundView.MY_WIDTH,
							SoundView.MY_HEIGHT);
				}
			}
			isSoundShow = !isSoundShow;
			hideControllerDelay();
		}
	};

	private View.OnClickListener mControlerExitListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			long time = System.currentTimeMillis();
			if (time - lastTimemControlerExitListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemControlerExitListener = time;
			
			
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			openOptionsDialog();

		}
	};

	private View.OnClickListener mGoToPlayListListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			long time = System.currentTimeMillis();
			if (time - lastTimemGoToPlayListListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemGoToPlayListListener = time;
			
			
			
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			
			
			Intent intentList = new Intent(VideoPlayerActivity.this,PlayListsActivity.class);
			startActivity(intentList);
			
			cancelDelayHide();

		}
	};
	private Intent intent;
	private View.OnClickListener exitPlayerTrueListener = new View.OnClickListener() {
		public void onClick(View v) {
			// long time = System.currentTimeMillis();
			// if (time - lastTime < CLICK_INTERVAL) {
			// return;
			// }
			// lastTime = time;
			
//			AppConnect.getInstance(VideoPlayerActivity.this).finalize();
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			
			//�˳��������
			intent = new Intent(getPackageName() + ".ExitListenerReceiver");
			sendBroadcast(intent);
			
			//�˳�������
			Intent intent2 = new Intent(getPackageName()
					+ ".ExitVideoPlayerActivity");
			sendBroadcast(intent2);
			
			
			menuDialog.dismiss();
			finish();
		}
	};

	private View.OnClickListener exitPlayerFalseListener = new View.OnClickListener() {
		public void onClick(View v) {
			// long time = System.currentTimeMillis();
			// if (time - lastTime < CLICK_INTERVAL) {
			// return;
			// }
			// lastTime = time;
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			menuDialog.dismiss();
		}
	};

	private View.OnClickListener mErrorLearnListener = new View.OnClickListener() {
		public void onClick(View v) {
			// long time = System.currentTimeMillis();
			// if (time - lastTime < CLICK_INTERVAL) {
			// return;
			// }
			// lastTime = time;
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			Toast.makeText(VideoPlayerActivity.this, "������˼��û��ʵ��", 1).show();
		}
	};
	private View.OnClickListener mErrorExitListener = new View.OnClickListener() {
		public void onClick(View v) {
			// long time = System.currentTimeMillis();
			// if (time - lastTime < CLICK_INTERVAL) {
			// return;
			// }
			// lastTime = time;
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}
			finish();
			erroVideoDialog.dismiss();
		}
	};

	private void startPlay() {
		if (uri != null && mVideoView != null) {
			mVideoView.stopPlayback();
			mVideoView.setVideoURI(uri);
			Log.i("afu", uri.toString());
			isOnline = true;
			setButtonEnabledFalse();

			mPlay.setText(R.string.control_pause_state);
		} else {
			if (mVideoView != null) {
				if (position != -1) {
					
					isOnline = false;
					isChangedVideo = true;
					
					String playUri = playList.get(position);

					mVideoView.setVideoPath(playUri);

					setButtonEnabledTrue();

				}
				mPlay.setText(R.string.control_play_state);
				// intent = getIntent();
				// if (intent != null) {
				// dataExtras = intent.getExtras();
				// if (dataExtras != null) {
				// data = dataExtras.getInt("dataKey");
				// // Log.v("MainActivity.playList.size()",String.valueOf(
				// // LoginActivity.playList.size()));
				// Log.v("MainActivity.playList.___data)",
				// String.valueOf(data));
				// position = data;
				// if (position != -1) {
				// isOnline = false;
				// isChangedVideo = true;
				//
				// mVideoView.setVideoPath(playList.get(position)/* .path */);
				//
				// setButtonEnabledTrue();
				//
				// }
				//
				// }
				//
				// }
			}

		}
	}

	/**
	 * ���ÿ��ư汾�ϵİ�ť�ܵ��
	 */
	private void setButtonEnabledTrue() {
		mGoToMainMenu.setEnabled(true);

		// ��һ��Ƶ
		mPrevious.setEnabled(true);

		// ��һ��Ƶ
		mNext.setEnabled(true);

		// �����б�
		mGoToPlayList.setEnabled(true);
	}

	/**
	 * ���ÿ��ư汾�ϵİ�ť���ܵ��
	 */
	private void setButtonEnabledFalse() {
		mGoToMainMenu.setEnabled(false);

		// ��һ��Ƶ
		mPrevious.setEnabled(false);

		// ��һ��Ƶ
		mNext.setEnabled(false);

		// �����б�
		mGoToPlayList.setEnabled(false);
	}

	/**
	 * ������Ƶ������Ļ��С��ģʽ
	 * 
	 * @param flag
	 */
	private void setVideoScale(int flag) {

		switch (flag) {
		case SCREEN_FULL:

			Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: "
					+ screenHeight);
			mVideoView.setVideoScale(screenWidth, screenHeight);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;

		case SCREEN_DEFAULT:

			int videoWidth = mVideoView.getVideoWidth();
			int videoHeight = mVideoView.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight - 25;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {

					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {

					mWidth = mHeight * videoWidth / videoHeight;
				} else {

				}
			}

			mVideoView.setVideoScale(mWidth, mHeight);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;
		}
	}

	/**
	 * ������Ϣ�����߿�������ڶ���������ء�
	 */
	private void hideControllerDelay() {
		mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	/**
	 * ���ؿ������
	 */
	private void hideController() {
		
		//���ؿ������
		if (mControlerPopupWindow.isShowing()) {
			mControlerPopupWindow.update(0, 0, 0, 0);
			isControllerShow = false;
		}

		//���ص����������
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
			isSoundShow = false;
		}
	}

	/**
	 * ɾ�����������Ϣ
	 */
	private void cancelDelayHide() {
		mHandler.removeMessages(HIDE_CONTROLER);
	}

	/**
	 * ��ʾ�������
	 */
	private void showController() {
		mControlerPopupWindow.update(0, 0, screenWidth, controlViewHeight);
		isControllerShow = true;
	}

	/**
	 * ���ڴ����϶������ȸ��¡�����ʱ����ȸ��¡����ؿ��������Ϣ
	 */
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {

			case PROGRESS_CHANGED:
				// Log.d(TAG, "The handler thread id = "
				// + Thread.currentThread().getId() + "\n");
				
				//�õ���ǰ����λ��
				int i = mVideoView.getCurrentPosition();
				
				//���²��Ž���
				seekBar.setProgress(i);
				
				if (isOnline) {
					
					int j = mVideoView.getBufferPercentage();
					
					int secondaryProgress = (j * seekBar.getMax() / 100);
					//���»����˶��٣�ͨ������������Դʱ���õ�
					seekBar.setSecondaryProgress(secondaryProgress);
				} else {
					// �������ǲ���Ҫ��2���ȣ�����Ϊ0
					seekBar.setSecondaryProgress(0);
				}

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				if (hour > 0) {
					playedTextView.setText(String.format("%02d:%02d:%02d",
							hour, minute, second));
				} else {
					playedTextView.setText(String.format("%02d:%02d", minute,
							second));
				}

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
				break;

			case HIDE_CONTROLER:
				hideController();
				Log.d(TAG, "The handler thread id = "
						+ Thread.currentThread().getId() + "\n");
				break;
			case PAUSE:
				if (mVideoView != null) {
					mVideoView.pause();
					mPlay.setText(R.string.control_play_state);
				}

				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG, " onTouchEvent(MotionEvent event)");

		//�ǳ��ؼ�
		boolean result = mGestureDetector.onTouchEvent(event);

		if (!result) {
			if (event.getAction() == MotionEvent.ACTION_UP) {

				/*
				 * if(!isControllerShow){ showController();
				 * hideControllerDelay(); }else { cancelDelayHide();
				 * hideController(); }
				 */
			}
			result = super.onTouchEvent(event);
		}

		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.v(TAG, " onConfigurationChanged()");

		getScreenSize();
		if (isControllerShow) {

			cancelDelayHide();
			hideController();
			showController();
			hideControllerDelay();
		}

		super.onConfigurationChanged(newConfig);
	}

	/**
	 * ����������С
	 * ���0-15
	 * 
	 * @param index
	 */
	private void updateVolume(int index) {
		if (mAudioManager != null) {
			if (isSilent) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			} else {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index,
						0);
			}
			currentVolume = index;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (mSoundService != null) {
			mSoundService.playButtonMusic(R.raw.button);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, MainMenuActivity.class);
			this.startActivity(intent);

			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// openOptionsDialog();
			Intent intent = new Intent(getPackageName()
					+ ".ExitVideoPlayerActivity");
			sendBroadcast(intent);
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ����ֻ���ģ�������θߺͿ�������ÿ������ĸ߶�
	 */
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		controlViewHeight = screenHeight / 4;

	}

	/**
	 * 
	 * 
	 * ����ʱ������Ϣ��ʾ�Ի���.
	 */
	private void errorsInformationDialog() {

		mErroVideoViewDialog = View.inflate(this, R.layout.error_video_dialog,
				null);
		erroVideoDialog = new AlertDialog.Builder(this).create();
		erroVideoDialog.setView(mErroVideoViewDialog);
		erroVideoDialog.show();
		Button mErrorLearn = (Button) mErroVideoViewDialog
				.findViewById(R.id.error_video_learn);
		mErrorLearn.setOnClickListener(mErrorLearnListener);
		Button mErrorExit = (Button) mErroVideoViewDialog
				.findViewById(R.id.error_video_sure);
		mErrorExit.setOnClickListener(mErrorExitListener);

	}

	/**
	 * 
	 * 
	 * �˳�������Ի���.
	 */
	private void openOptionsDialog() {

		menuView = View.inflate(this, R.layout.exit_dialog, null);
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.show();
		Button exitPlayerTrue = (Button) menuView	.findViewById(R.id.exit_player_yes);
		exitPlayerTrue.setOnClickListener(exitPlayerTrueListener);
		
		Button exitPlayerFalse = (Button) menuView.findViewById(R.id.exit_player_no);
		exitPlayerFalse.setOnClickListener(exitPlayerFalseListener);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, " onActivityResult()");

		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			int result = data.getIntExtra("CHOOSE", -1);
			Log.d("RESULT", "" + result);
			if (result != -1) {
				isOnline = false;
				isChangedVideo = true;
				mVideoView.setVideoPath(playList.get(result));

				position = result;

			} else {
				String url = data.getStringExtra("CHOOSE_URL");
				if (url != null) {
					mVideoView.setVideoPath(url);
					isOnline = true;
					isChangedVideo = true;

				}
			}

			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		Log.v(TAG, " onPause()");
		playedTime = mVideoView.getCurrentPosition();
		mVideoView.pause();
		mPlay.setText(R.string.control_play_state);
		mHandler.sendEmptyMessage(PAUSE);
		super.onPause();
	}

	private PausePlayerReceiver mPausePlayerReceiver;

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume()");
		if (!isChangedVideo) {
			mVideoView.seekTo(playedTime);
			mVideoView.start();
		} else {
			isChangedVideo = false;
		}
		if (mVideoView.isPlaying()) {
			mPlay.setText(R.string.control_pause_state);
			hideControllerDelay();
		}
		// adView.requestFreshAd();
		Log.d("REQUEST", "NEW AD !");
		IntentFilter filter = new IntentFilter("yangguangfu.mediaplayer.pause");
		mPausePlayerReceiver = new PausePlayerReceiver();
		registerReceiver(mPausePlayerReceiver, filter);

		/**
		 * ����Ϊ����
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, " onDestroy()");
		unregisterListener();
		if (mControlerPopupWindow.isShowing()) {
			mControlerPopupWindow.dismiss();
		}
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
		}

		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.removeMessages(HIDE_CONTROLER);
		if (mVideoView.isPlaying()) {
			mVideoView.stopPlayback();
		}
		if (playList != null) {
			playList.clear();
		}
		unregisterReceiver(mPausePlayerReceiver);
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG, " onRestart()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.v(TAG, "onSaveInstanceState()");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v(TAG, "onStart())");
	}

	@Override
	protected void onStop() {

		// if(!isOnline){
		// finish();
		// }
		mHandler.sendEmptyMessage(PAUSE);
		super.onStop();
		Log.v(TAG, "onStop()");
	}

}