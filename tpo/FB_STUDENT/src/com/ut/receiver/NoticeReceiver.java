package com.ut.receiver;

import com.ut.task.NoticRequestTask;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoticeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final DBController controller = new DBController(context);
		Integer notice = controller.selectNotification(FBNConstants.NOTICE_NOTOFICATION);
		if (notice != null && notice == 1) {
			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			if (registerdEnrollment != null) {
				NoticRequestTask task = new NoticRequestTask(context, intent, registerdEnrollment);
				task.execute();
			}
		}
	}

}