package com.ut.fbn.adapter;

import java.util.HashMap;
import java.util.List;

import com.ut.pojo.HallTicket;

import android.content.Context;
import android.widget.ArrayAdapter;

public class StableArrayAdapter extends ArrayAdapter<HallTicket> {

		HashMap<HallTicket, Integer> mIdMap = new HashMap<HallTicket, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId, List<HallTicket> bloodrequests) {
			super(context, textViewResourceId, bloodrequests);

			int i = 0;
			for (HallTicket hallTicket : bloodrequests) {
				mIdMap.put(hallTicket, i);
				i++;
			}
		}

		@Override
		public long getItemId(int position) {
			HallTicket item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}