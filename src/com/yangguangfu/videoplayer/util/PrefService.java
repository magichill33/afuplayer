package com.yangguangfu.videoplayer.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;

/**
 * 
 * @author 阿福
 * 负责保存设置的语言、是否有点击音效、是否默认播放作者家乡音乐
 * 其实它是一个工具类
 */
public class PrefService {

	private Context context;

	public PrefService(Context context) {
		this.context = context;
	}

	public int getLanguage() {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		return sPref.getInt(Constants.PREF_LANGUAGE, Constants.LANGUAGE_CHINESE);
	}

	public boolean isButtonMusic() {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		return sPref.getBoolean(Constants.PRE_BUTTON_MUSIC, true);
	}

	public boolean isAuthorHometownMusic() {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		return sPref.getBoolean(Constants.PRE_AUTHOR_HOMETOWN_MUSIC, true);
	}
	/**
	 * 设置语言
	 * @param language
	 */
	public void setLanguage(int language) {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putInt(Constants.PREF_LANGUAGE, language);
		ed.commit();
		return;
	}
    /**
     * 设置是否播放按钮声音
     * @param b
     */
	public void setButtonMusic(boolean b) {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putBoolean(Constants.PRE_BUTTON_MUSIC, b);
		ed.commit();
	}

	 /**
	  * 设置是否播放作者家乡音乐
	  * @param b
	  */
	public void setAuthorHometownMusic(boolean b) {
		SharedPreferences sPref = context.getSharedPreferences(
				Constants.SOFTWARE_NAME, Context.MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putBoolean(Constants.PRE_AUTHOR_HOMETOWN_MUSIC, b);
		ed.commit();
	}

	/**
	 * 人为改动语言方法
	 * @param context
	 * @param prefService
	 */
	public static void changleLocale(Context context, PrefService prefService) {
		//语言选项
		int languageOption = prefService.getLanguage();
		/*java.util.Locale主要在软件的本地化时使用。它本身没有什么功能，
		更多的是作为一个参数辅助其他方法完成输出的本地化。*/
		Locale locale = Locale.CHINESE;
		switch (languageOption) {
		case Constants.LANGUAGE_CHINESE: 
			locale = Locale.CHINESE;
			break;
		case Constants.LANGUAGE_ENGLISH:
			locale = Locale.ENGLISH;
			break;
		case Constants.LANGUAGE_TRAIDTION_CHINESE:
			locale = Locale.TRADITIONAL_CHINESE;
			break;
		default:
			break;
		}

		Configuration config = context.getResources().getConfiguration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
		
		
	}

}
