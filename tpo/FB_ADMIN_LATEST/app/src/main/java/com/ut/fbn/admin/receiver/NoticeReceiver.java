package com.ut.fbn.admin.receiver;

import com.ut.fbn.admin.util.FBNUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NoticeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String userName = FBNUtil.getRegisteredUderName(context);
		if (userName != null) {

		}
	}

}