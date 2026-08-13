package com.ut.fbn;

import com.ut.pojo.HallTicket;
import com.ut.util.FBNUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompanyDetails extends Parent {

	public static HallTicket hallTicket = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.companydetails);

		if (hallTicket != null) {
			TextView role = (TextView) findViewById(R.id.role);
			role.setText(hallTicket.getRole());

			TextView companyName = (TextView) findViewById(R.id.companyName);
			companyName.setText(hallTicket.getCompanyName());

			TextView domain = (TextView) findViewById(R.id.domain);
			domain.setText(hallTicket.getCompany().getDomain());

			ImageView companyLogo = (ImageView) findViewById(R.id.companyLogo);

			Bitmap bitmap = BitmapFactory.decodeByteArray(hallTicket.getCompany().getLogo(), 0,
					hallTicket.getCompany().getLogo().length);
			companyLogo.setImageBitmap(bitmap);
			companyLogo.setImageBitmap(bitmap);

			final String websiteStr = hallTicket.getCompany().getWebsite();
			LinearLayout layoutW = (LinearLayout) findViewById(R.id.layoutW);
			if (websiteStr != null) {
				layoutW.setVisibility(View.VISIBLE);
				TextView website = (TextView) findViewById(R.id.website);
				website.setText(websiteStr);
				website.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + websiteStr));
						startActivity(browserIntent);
					}
				});
			}else {
				layoutW.setVisibility(View.GONE);
			}

			final String linkedStr = hallTicket.getCompany().getLinkedIn();
			LinearLayout layoutL = (LinearLayout) findViewById(R.id.layoutL);
			if (linkedStr != null) {
				layoutL.setVisibility(View.VISIBLE);
				TextView linkkedIn = (TextView) findViewById(R.id.linkkedIn);
				linkkedIn.setText(linkedStr);
				linkkedIn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + linkedStr));
						startActivity(browserIntent);
					}
				});
			}else {
				layoutL.setVisibility(View.GONE);
			}
			
			final String facebookStr = hallTicket.getCompany().getFacebook();
			LinearLayout layoutF = (LinearLayout) findViewById(R.id.layoutF);
			if (facebookStr != null) {
				layoutF.setVisibility(View.VISIBLE);
				TextView facebook = (TextView) findViewById(R.id.facebook);
				facebook.setText(facebookStr);
				facebook.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + facebookStr));
						startActivity(browserIntent);
					}
				});
			}else {
				layoutF.setVisibility(View.GONE);
			}
			
			final String twitterStr = hallTicket.getCompany().getTwiter();
			LinearLayout layoutT = (LinearLayout) findViewById(R.id.layoutT);
			if (twitterStr != null) {
				layoutT.setVisibility(View.VISIBLE);
				TextView twitter = (TextView) findViewById(R.id.twitter);
				twitter.setText(twitterStr);
				twitter.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + twitterStr));
						startActivity(browserIntent);
					}
				});
			}else {
				layoutT.setVisibility(View.GONE);
			}
			
			final String glassdoorStr = hallTicket.getCompany().getGlassdoor();
			LinearLayout layoutG = (LinearLayout) findViewById(R.id.layoutG);
			if (glassdoorStr != null) {
				layoutG.setVisibility(View.VISIBLE);
				TextView glassdoor = (TextView) findViewById(R.id.glassdoor);
				glassdoor.setText(glassdoorStr);
				glassdoor.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + glassdoorStr));
						startActivity(browserIntent);
					}
				});
			}else {
				layoutW.setVisibility(View.GONE);
			}
			
			
			TextView hallticketDetails = (TextView) findViewById(R.id.hallticketDetails);
			hallticketDetails.setText(Html.fromHtml(hallTicket.toString()));

			/*
			 * Company company = hallTicket.getCompany(); if(company != null) { TextView
			 * companyDetail = (TextView) findViewById(R.id.companyDetail);
			 * companyDetail.setText(FBNUtil.stripHtml(company.toString())); }
			 */
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
