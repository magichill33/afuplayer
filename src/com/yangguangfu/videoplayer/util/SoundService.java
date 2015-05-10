package com.yangguangfu.videoplayer.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.MediaPlayer;

import com.yangguangfu.videoplayer.R;
/**
 * 
 * @author ����
 *    ����������
 *    1,�������߼�������
 *    2�����Ű�ť��Ч
 */
public class SoundService {

	// ������Դ����, ��������Դ��ID��ΪKEY��
	private Map<Integer, MediaPlayer> soundCacheButtonMusic = new HashMap<Integer, MediaPlayer>();
	private Map<Integer, MediaPlayer> soundCacheAuthorHometownMusic = new HashMap<Integer, MediaPlayer>();
	private Context context;
	private PrefService pref;

	public SoundService(Context context) {
		this.context = context;
		if (pref == null) {
			pref = new PrefService(context);
		}
	}
/**
 * ���Ű�ť��Ч
 * @param resId
 */
	public void playButtonMusic(int resId) {

		if (!pref.isAuthorHometownMusic()) {
			return;
		}
		MediaPlayer mp = soundCacheButtonMusic.get(resId);
		if (mp == null) {
			mp = MediaPlayer.create(context, resId);
//			mp = MediaPlayer.create(context, R.raw.button);
			soundCacheButtonMusic.put(resId, mp);
			if (mp != null){
				mp.start();
			}
		} else {
			if (mp != null){
				mp.start();
				}
			}
	}

	public void authorHometownMusicPlay(int resId) {

		if (!pref.isButtonMusic()) {
			return;
		}
		MediaPlayer mp = soundCacheAuthorHometownMusic.get(resId);
		if (mp == null) {
			mp = MediaPlayer.create(context, resId);
//			mp = MediaPlayer.create(context, R.raw.myhome);
			soundCacheAuthorHometownMusic.put(resId, mp);
			mp.start();
			mp.setLooping(true);

		} else {
			if(mp != null){
				mp.start();
				mp.setLooping(true);
			}
			
			 }

	}

	public void authorHometownMusicPause(int resId) {

		if (!pref.isButtonMusic()) {
			return;
		}
		MediaPlayer mp = soundCacheAuthorHometownMusic.get(resId);
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.pause();

			}
		}
	}

	public boolean isAuthorHometownMusicPlaying(int resId) {
		Boolean isAuthorMusicPlaying = false;
		if (!pref.isButtonMusic()) {
			isAuthorMusicPlaying = false;
			return isAuthorMusicPlaying;
		}
		MediaPlayer mp = soundCacheAuthorHometownMusic.get(resId);
		if (mp != null) {
			if (mp.isPlaying()) {
				isAuthorMusicPlaying = true;
			} else {
				isAuthorMusicPlaying = false;
			}
		}
		return isAuthorMusicPlaying;
	}

	public void authorHometownMusicStop(int resId) {

		if (!pref.isButtonMusic()) {
			return;
		}
		MediaPlayer mp = soundCacheAuthorHometownMusic.get(resId);
		if (mp != null) {
			mp.stop();
			soundCacheAuthorHometownMusic.remove(resId);
			mp.release();
			mp = null;

		}
	}

	// �����������û����������װ�����������ҷŵ����档
	private MediaPlayer findResourceFromCache(int resId) {
		MediaPlayer mp = null;
		if (soundCacheButtonMusic.get(resId) == null) {
			mp = MediaPlayer.create(context, resId);
			soundCacheButtonMusic.put(resId, mp);
		} else {
			mp = soundCacheButtonMusic.get(resId);
		}
		return mp;
	}

	public void release() {

		for (Map.Entry<Integer, MediaPlayer> entry : soundCacheButtonMusic.entrySet()) {
			MediaPlayer mp = entry.getValue();
			if (mp != null) {
				if (mp.isPlaying()) {
					mp.stop();
				}
				mp.release();
			}
		}
	}

}
