package com.yangguangfu.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


/**
 * 
 * @author ����
 * @version ����ʱ�䣺2011-4-1 ����09:43:38 BaseActivity��˵��:Acitivty������
 *          ����������������ʵ��onCreate ���� �ڸ�����ע����һ��BroadcastReceiver ���ڽ����˳���Ϣ
 *          �ڽ��յ���Ϣ֮���������
 * 
 *          -------------------------------- ʹ�� ���Լ����е�activity�м̳и���
 *          ����Ҫ�˳������ʱ���͹㲥 Intent intent = new
 *          Intent(context.getPackageName()+".ExitListenerReceiver");
 * 
 *          context.sendBroadcast(intent); ���ɡ�
 *          
 *         �˳�����
 */
public abstract class BaseActivity extends Activity {
	
	
	/**
	 * �˳��¼�����
	 * 
	 */
	public ExitListenerReceiver exitre = null;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		regListener();
	}

	
	/**
	 * ע���˳��¼�����
	 * 
	 */
	private void regListener() {
		exitre = new ExitListenerReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(this.getPackageName() + "."
				+ "ExitListenerReceiver");
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
  /**
   * �㲥�Ž����ߣ����ڽ��չر�Activity��Ϣ�����ر�û�йص�Activity
   * @author ����
   *
   */
	class ExitListenerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			((Activity) arg0).finish();

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterListener();

	}

}