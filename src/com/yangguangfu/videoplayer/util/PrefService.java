package com.yangguangfu.videoplayer.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;

/**
 * 
 * @author ����
 * ���𱣴����õ����ԡ��Ƿ��е����Ч���Ƿ�Ĭ�ϲ������߼�������
 * ��ʵ����һ��������
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
	 * ��������
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
     * �����Ƿ񲥷Ű�ť����
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
	  * �����Ƿ񲥷����߼�������
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
	 * ��Ϊ�Ķ����Է���
	 * @param context
	 * @param prefService
	 */
	public static void changleLocale(Context context, PrefService prefService) {
		//����ѡ��
		int languageOption = prefService.getLanguage();
		/*java.util.Locale��Ҫ������ı��ػ�ʱʹ�á�������û��ʲô���ܣ�
		���������Ϊһ�������������������������ı��ػ���*/
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
