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
 * @author 阿福
 *  播放器,
 *  封装逻辑、自定义播放界面
 *  
 */
public class VideoPlayerActivity extends BaseActivity {
	/**
	 * 打出日志
	 */
	private final static String TAG = "VideoPlayerActivity";

	/**
	 * 装视频文件信息的容器——播放列表
	 */

	private static ArrayList<String> playList = null;

	/**
	 * 播放列表位置
	 */
	private static int position;

	/**
	 * 播放了多少时间
	 */
	private int playedTime;

	/**
	 * 视频显示控件-这个类相当重要
	 */
	private AFuVideoView mVideoView = null;
	/**
	 * 播放进度条
	 */
	private SeekBar seekBar = null;

	/**
	 * 显示视频时间长度
	 */
	private TextView durationTextView = null;
	/**
	 * 用于显示视频播放了多少时间
	 */
	private TextView playedTextView = null;
	/**
	 * 手势识别器
	 */
	private GestureDetector mGestureDetector = null;
	/**
	 * 声音管理
	 */
	private AudioManager mAudioManager = null;

	/**
	 * 得到当前媒体音量
	 */
	private int currentVolume = 0;

	private Button mGoToPlayList = null;
	private Button mPrevious = null;
	private Button mPlay = null;
	private Button mNext = null;
	private Button mGoToMainMenu = null;
	/**
	 * 声音控制按钮
	 */
	private Button mSoundControl = null;

	private Button mControlerExit = null;

	/**
	 * 控制视频界面
	 */
	private View mControlView = null;
	/**
	 * 控制视频界面的具体按钮
	 */
	private PopupWindow mControlerPopupWindow = null;

	/**
	 * 
	 * 调节声音大小
	 * 
	 */

	private SoundView mSoundView = null;
	/**
	 * 音量控制窗口
	 */
	private PopupWindow mSoundWindow = null;

	/**
	 * 手机或模拟器的屏幕宽
	 */
	private static int screenWidth = 0;
	/**
	 * 手机或模拟器的屏幕高
	 */
	private static int screenHeight = 0;
	/**
	 * 控制面板屏幕的高
	 */
	private static int controlViewHeight = 0;
	/**
	 * 多常时间隐藏控制面板
	 */
	private final static int TIME = 6868;
	/**
	 * 是否显示控制面板
	 */
	private boolean isControllerShow = true;
	/**
	 * 是否暂停
	 */
	private boolean isPaused = false;
	/**
	 * 是否满屏
	 */
	private boolean isFullScreen = false;
	/**
	 * 是否是静音
	 */
	private boolean isSilent = false;

	/**
	 * 是否显示调音控件
	 */
	private boolean isSoundShow = false;
	/**
	 * 是否在线播放或第三方应用发起播放
	 */
	private boolean isOnline = false;

	/**
	 * 充满屏幕
	 */
	private final static int SCREEN_FULL = 0;
	/**
	 * 默认屏幕
	 */
	private final static int SCREEN_DEFAULT = 1;

	private boolean isChangedVideo = false;

	/**
	 * 隐藏控制面板消息
	 */
	private final static int HIDE_CONTROLER = 1;

	/**
	 * 在播放列表是发的消息
	 */
	private final static int PAUSE = 3;
	/**
	 * 刷新播放时间和拖动条位置消息
	 */
	private final static int PROGRESS_CHANGED = 0;

	/**
	 * 意图，从播放列表携带的视频信息
	 */
	private Intent mIntent;
	/**
	 * 具体的数据的路径或网络地址
	 */
	private Uri uri;

	private AlertDialog menuDialog;
	private View menuView;
	/**
	 * 控制按钮声音
	 */
	private SoundService mSoundService;

	private AlertDialog erroVideoDialog;
	private View mErroVideoViewDialog;
	
	
	/**
	 * 注册退出事件监听
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
	 * 注册取消退出事件监听
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
		
		//把视图加载进来
		setContentView(R.layout.video_view);
		// 视频显示,很重要
		mVideoView = (AFuVideoView) findViewById(R.id.vv);
//
//		Log.v(TAG, "onCreate()");
//
//		Log.d("OnCreate", getIntent().toString());
//		// 如果是从
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
		
		// 加载控制视频界面。
		mControlView = getLayoutInflater().inflate(R.layout.controler_view,
				null);

		// PopupWindow 作为一种用户提醒 而且其开销也比Activity要小
		mControlerPopupWindow = new PopupWindow(mControlView);
		
		
		// 加载控制面板
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

		// 显示视频总长度时间
		durationTextView = (TextView) mControlView.findViewById(R.id.duration);
		// 显示视频播放了多少时间
		playedTextView = (TextView) mControlView.findViewById(R.id.has_played);

		mGoToMainMenu = (Button) mControlView	.findViewById(R.id.control_goto_menu);

		// 上一视频
		mPrevious = (Button) mControlView.findViewById(R.id.control_previous);
		
		// 播放视频
		mPlay = (Button) mControlView.findViewById(R.id.control_play_state);
		
		// 下一视频
		mNext = (Button) mControlView.findViewById(R.id.control_next);

		// 控制声音
		mSoundControl = (Button) mControlView.findViewById(R.id.control_sound_control);

		mControlerExit = (Button) mControlView	.findViewById(R.id.controler_exit);
		
		// 播放列表
		mGoToPlayList = (Button) mControlView		.findViewById(R.id.control_goto_play_lists);

		
		
		
		
		
		//点击按钮的监听

		mGoToMainMenu.setOnClickListener(mGoToMainMenuListener);
//		mPrevious.setOnClickListener(mPreviousListener);
		
		mPrevious.setOnClickListener(mPlayListener);
		mPlay.setOnClickListener(mPlayListener);
		
		mNext.setOnClickListener(mNextListener);
		
		mSoundControl.setOnClickListener(mSoundControlListener);
		
		mControlerExit.setOnClickListener(mControlerExitListener);
		
		mGoToPlayList.setOnClickListener(mGoToPlayListListener);

		
		initSoundView();

		// 播放进度条,可以拖动
		seekBar = (SeekBar) mControlView.findViewById(R.id.seekbar);
		
		//1,SeekBar
//		  2,播放前关联
//		  3，拖动监听
		
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			//当拖动发生改变的时候执行
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				
				if (fromUser) {
					mVideoView.seekTo(progress);

				}
			}

			//当拖动刚触动的时候执行
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mHandler.removeMessages(HIDE_CONTROLER);
			}

			//当拖动刚触动的时候执行
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
		// 得到URL
	}

	private void initSoundView() {
		/**
		 * 调音量类
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

		// 音频管理服务
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// 得到视频当前音量
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		// 长按钮效果
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
				//把拖动的提示取消。
				Toast.makeText(VideoPlayerActivity.this, "拖动结束", 1).show();
			}
		});
		//2.3
		mVideoView.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				//监听播放卡、监听开始拖动、监听卡完成、监听拖动缓冲完成。
				switch (what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					//监听播放卡、监听开始拖动
					Toast.makeText(VideoPlayerActivity.this, "开始缓冲", 1).show();
					break;
					
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					//监听卡完成、监听拖动缓冲完成
					Toast.makeText(VideoPlayerActivity.this, "缓冲结束", 1).show();
					break;
					
				

				default:
					break;
				}
				
				return true;
			}
		});

		/**
		 * 监听播放出错时的处理
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
				//视频文件和seekBar关联：视频有多长那么seekBar拖动也在这个范围内
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

				
				//如果是播放浏览器地址或者是文件夹地址
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
			// 当从浏览器发起播放的时候、本地文件夹发起播放
			uri = mIntent.getData();
//			mIntent.setData("");
			
//			 URLEncoder.encode(uri.toString());//http://61.158.246.14/youku/67738070F7B33820298B373A84/0300200100503D4C69876405987B01F3213A7B-C87E-6774-946F-A9ACE72E1B30.mp4
			//得到位置
			position = mIntent.getIntExtra("CurrentPosInMediaIdList", 0);
			//得到播放列表
			playList = mIntent.getStringArrayListExtra("MediaIdList");
		}
		
	}

	//初始化窗口参数
	private void initWindows() {
		// 设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// 保持背光常亮的设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 保持背光常亮的设置(例外一种)
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
			
		//防止频繁点击导致的错了
			long time = System.currentTimeMillis();
			if (time - lastTimemGoToMainMenuListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemGoToMainMenuListener = time;
			
			
			//点击音效
			if (mSoundService != null) {
				mSoundService.playButtonMusic(R.raw.button);
			}

			//回到主页
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
				
				//防止频繁点击导致错误
				long time = System.currentTimeMillis();
				if (time - lastTimemPlayListener < CLICK_INTERVAL) {
					return;
				}
				lastTimemPlayListener = time;
				
				
				//播放按钮音效
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
			
			//防止频繁点击
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
			
			//退出整个软件
			intent = new Intent(getPackageName() + ".ExitListenerReceiver");
			sendBroadcast(intent);
			
			//退出播放器
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
			Toast.makeText(VideoPlayerActivity.this, "不好意思还没有实现", 1).show();
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
	 * 设置控制版本上的按钮能点击
	 */
	private void setButtonEnabledTrue() {
		mGoToMainMenu.setEnabled(true);

		// 上一视频
		mPrevious.setEnabled(true);

		// 下一视频
		mNext.setEnabled(true);

		// 播放列表
		mGoToPlayList.setEnabled(true);
	}

	/**
	 * 设置控制版本上的按钮不能点击
	 */
	private void setButtonEnabledFalse() {
		mGoToMainMenu.setEnabled(false);

		// 上一视频
		mPrevious.setEnabled(false);

		// 下一视频
		mNext.setEnabled(false);

		// 播放列表
		mGoToPlayList.setEnabled(false);
	}

	/**
	 * 设置视频播放屏幕大小的模式
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
	 * 发送信息，告诉控制面板在多少秒后隐藏。
	 */
	private void hideControllerDelay() {
		mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	/**
	 * 隐藏控制面板
	 */
	private void hideController() {
		
		//隐藏控制面板
		if (mControlerPopupWindow.isShowing()) {
			mControlerPopupWindow.update(0, 0, 0, 0);
			isControllerShow = false;
		}

		//隐藏调音控制面板
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
			isSoundShow = false;
		}
	}

	/**
	 * 删除隐藏面板消息
	 */
	private void cancelDelayHide() {
		mHandler.removeMessages(HIDE_CONTROLER);
	}

	/**
	 * 显示控制面板
	 */
	private void showController() {
		mControlerPopupWindow.update(0, 0, screenWidth, controlViewHeight);
		isControllerShow = true;
	}

	/**
	 * 用于处理拖动调进度更新、播放时间进度更新、隐藏控制面板消息
	 */
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {

			case PROGRESS_CHANGED:
				// Log.d(TAG, "The handler thread id = "
				// + Thread.currentThread().getId() + "\n");
				
				//得到当前播放位置
				int i = mVideoView.getCurrentPosition();
				
				//更新播放进度
				seekBar.setProgress(i);
				
				if (isOnline) {
					
					int j = mVideoView.getBufferPercentage();
					
					int secondaryProgress = (j * seekBar.getMax() / 100);
					//更新缓冲了多少，通常播放网络资源时候用到
					seekBar.setSecondaryProgress(secondaryProgress);
				} else {
					// 这里我们不需要第2进度，所以为0
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

		//非常关键
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
	 * 设置音量大小
	 * 最大0-15
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
	 * 获得手机或模拟器屏蔽高和宽，并计算好控制面板的高度
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
	 * 播放时出错信息提示对话框.
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
	 * 退出本程序对话框.
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
		 * 设置为横屏
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