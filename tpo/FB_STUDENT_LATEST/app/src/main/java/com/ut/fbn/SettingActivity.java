package com.ut.fbn;

import java.util.Timer;
import java.util.TimerTask;

import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class SettingActivity extends ParentFragmentActivity {
	static final int NUM_ITEMS = 6;
	ImageFragmentPagerAdapter imageFragmentPagerAdapter;
	ViewPager viewPager;
	public static final String[] IMAGE_NAME = { "eagle", "horse", "bonobo", "wolf", "owl", "bear", };

	private CheckBox openingNotificationCheck,noticeNotificationBox,feeNotificationBox;

	private Button saveSetting;

	int currentPage = 0;
	Timer timer;
	final long DELAY_MS = 500;// delay in milliseconds before task is to be executed
	final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
	int NUM_PAGES = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(imageFragmentPagerAdapter);
		/* After setting the adapter use the timer */
		final Handler handler = new Handler();
		final Runnable Update = new Runnable() {
			public void run() {
				if (currentPage == NUM_PAGES - 1) {
					currentPage = 0;
				}
				viewPager.setCurrentItem(currentPage++, true);
			}
		};

		timer = new Timer(); // This will create a new Thread
		timer.schedule(new TimerTask() { // task to be scheduled
			@Override
			public void run() {
				handler.post(Update);
			}
		}, DELAY_MS, PERIOD_MS);

		openingNotificationCheck = (CheckBox) findViewById(R.id.openingNotificationBox);
		noticeNotificationBox = (CheckBox) findViewById(R.id.noticeNotificationBox);
		feeNotificationBox = (CheckBox) findViewById(R.id.feeNotificationBox);
		final DBController controller = new DBController(context);
		Integer opening = controller.selectNotification(FBNConstants.OPENNING_NOTOFICATION);
		if ( opening != null && opening  == 1) {
			openingNotificationCheck.setChecked(true);
		} else {
			openingNotificationCheck.setChecked(false);
		}
		Integer notice = controller.selectNotification(FBNConstants.NOTICE_NOTOFICATION);
		if (notice != null && notice  == 1) {
			noticeNotificationBox.setChecked(true);
		} else {
			noticeNotificationBox.setChecked(false);
		}
		Integer fee = controller.selectNotification(FBNConstants.FEE_NOTOFICATION);
		if (fee != null && fee  == 1) {
			feeNotificationBox.setChecked(true);
		} else {
			feeNotificationBox.setChecked(false);
		}

		saveSetting = (Button) findViewById(R.id.saveSetting);
		saveSetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (openingNotificationCheck.isChecked()) {
					controller.insertIntoNotification(FBNConstants.OPENNING_NOTOFICATION, 1);
				} else {
					controller.insertIntoNotification(FBNConstants.OPENNING_NOTOFICATION, 0);
				}
				
				if (noticeNotificationBox.isChecked()) {
					controller.insertIntoNotification(FBNConstants.NOTICE_NOTOFICATION, 1);
				} else {
					controller.insertIntoNotification(FBNConstants.NOTICE_NOTOFICATION, 0);
				}
				
				if (feeNotificationBox.isChecked()) {
					controller.insertIntoNotification(FBNConstants.FEE_NOTOFICATION, 1);
				} else {
					controller.insertIntoNotification(FBNConstants.FEE_NOTOFICATION, 0);
				}

				FBNUtil.showSucessDialog("Setting Saved successfully!", context);
			}

		});
	}

	public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
		public ImageFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			SwipeFragment fragment = new SwipeFragment();
			return SwipeFragment.newInstance(position);
		}
	}

	public static class SwipeFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
			ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
			Bundle bundle = getArguments();
			int position = bundle.getInt("position");
			String imageFileName = IMAGE_NAME[position];
			int imgResId = getResources().getIdentifier(imageFileName, "drawable", "com.ut.fbn");
			imageView.setImageResource(imgResId);
			return swipeView;
		}

		static SwipeFragment newInstance(int position) {
			SwipeFragment swipeFragment = new SwipeFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("position", position);
			swipeFragment.setArguments(bundle);
			return swipeFragment;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		} else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}

}