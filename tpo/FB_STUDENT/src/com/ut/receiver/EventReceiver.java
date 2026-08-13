package com.ut.receiver;

import com.ut.task.EventRequestTask;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		final DBController controller = new DBController(context);
		Integer event = controller.selectNotification(FBNConstants.OPENNING_NOTOFICATION);
		if (event != null && event == 1) {
			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			EventRequestTask task = new EventRequestTask(context, intent,registerdEnrollment);
			task.execute();
		}
	}

	

}