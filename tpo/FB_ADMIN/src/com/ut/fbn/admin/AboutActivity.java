package com.ut.fbn.admin;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends ParentFragmentActivity implements OnMapReadyCallback {

	private TextView versionNo;

	private GoogleMap mMap;

	private Button submit;

	public Menu menu;

	Context context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		versionNo = (TextView) findViewById(R.id.versionNo);
		context = getApplicationContext();
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			versionNo.setText(info.versionCode + "(" + info.versionName + ")");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		submit = (Button) findViewById(R.id.submitButton);

		// add button listener
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				navigateToHomeActivity();
			}
		});
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.
		LatLng uddanda = new LatLng(23.230440023157634, 77.43486195802689);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(uddanda);
		markerOptions.title("Uddanda Technologies");
		markerOptions.snippet(
				"Uddanda Technologies \n45, Zone-II M.P. Nagar Bhopal(M.P.)\n Zip Code:462021 Mobile:+919589555592 \nEmail:admin@uddandatechnologies.com");
		mMap.addMarker(markerOptions);
		mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {

				Context context = getApplicationContext(); // or getActivity(),
															// YourActivity.this,
															// etc.

				LinearLayout info = new LinearLayout(context);
				info.setOrientation(LinearLayout.VERTICAL);

				TextView title = new TextView(context);
				title.setTextColor(Color.BLACK);
				title.setGravity(Gravity.CENTER);
				title.setTypeface(null, Typeface.BOLD);
				title.setText(marker.getTitle());

				TextView snippet = new TextView(context);
				snippet.setTextColor(Color.GRAY);
				snippet.setText(marker.getSnippet());

				info.addView(title);
				info.addView(snippet);

				return info;
			}
		});

		mMap.moveCamera(CameraUpdateFactory.newLatLng(uddanda));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		}else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}

}
