package com.ut.receiver;

import com.ut.task.FeeReminderRequestTask;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FeeReminderReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final DBController controller = new DBController(context);
		Integer fee = controller.selectNotification(FBNConstants.FEE_NOTOFICATION);
		if (fee != null && fee == 1) {
			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			if (registerdEnrollment != null) {
				FeeReminderRequestTask task = new FeeReminderRequestTask(context, intent, registerdEnrollment);
				task.execute();
			}
		}
	}

}