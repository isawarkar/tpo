package com.ut.fbn.admin;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.pojo.Company;

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

	public static Company company = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.companydetails);

		if (company != null) {
			try {
				TextView companyId = (TextView) findViewById(R.id.companyId);
				companyId.setText("Company ID:" + String.valueOf(company.getCompanyID()));

				TextView companyName = (TextView) findViewById(R.id.companyName);
				companyName.setText("Company Name:" + company.getCompanyname());

				TextView domain = (TextView) findViewById(R.id.domain);
				domain.setText("Domain:" + company.getDomain());

				ImageView companyLogo = (ImageView) findViewById(R.id.companyLogo);

				Bitmap bitmap = BitmapFactory.decodeByteArray(company.getLogo(), 0, company.getLogo().length);
				companyLogo.setImageBitmap(bitmap);
				
				final String websiteStr = company.getWebsite();
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
				} else {
					layoutW.setVisibility(View.GONE);
				}

				final String linkedStr = company.getLinkedIn();
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
				} else {
					layoutL.setVisibility(View.GONE);
				}

				final String facebookStr = company.getFacebook();
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
				} else {
					layoutF.setVisibility(View.GONE);
				}

				final String twitterStr = company.getTwiter();
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
				} else {
					layoutT.setVisibility(View.GONE);
				}

				final String glassdoorStr = company.getGlassdoor();
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
				} else {
					layoutW.setVisibility(View.GONE);
				}

				TextView hallticketDetails = (TextView) findViewById(R.id.hallticketDetails);
				hallticketDetails.setText(Html.fromHtml(company.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
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
