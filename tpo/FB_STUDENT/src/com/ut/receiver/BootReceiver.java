package com.ut.receiver;

import java.util.List;

import com.ut.fbn.R;
import com.ut.pojo.HallTicket;
import com.ut.util.FBNUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	private Context contextT;

	public List<HallTicket> events;

	public void onReceive(Context context, Intent intent) {

		contextT = context;
		if (FBNUtil.checkInternet(contextT, BootReceiver.class)) {
			Toast.makeText(context, context.getString(R.string.FBN_Syn_Started), Toast.LENGTH_LONG).show();
			// Your code to execute when Boot Completd
			Toast.makeText(context, context.getString(R.string.FBN_Syn_Completed), Toast.LENGTH_LONG).show();
		}
	}
}