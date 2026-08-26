package com.ut.fbn.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.CompanyDetails;
import com.ut.fbn.R;
import com.ut.pojo.EligibleHallTicket;
import com.ut.pojo.HallTicket;
import com.ut.task.HallTicketTask;
import com.ut.task.ShareHallTicketTask;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This Adapter class is used to created a eligible list.
 * 
 * @author Indrajeet Sawarkar
 * 
 *
 */
public class EligibleHallTicketCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<EligibleHallTicket> list = new ArrayList<EligibleHallTicket>();
	private Context context;
	private Activity parentActivity;

	public EligibleHallTicketCustomAdapter(List<EligibleHallTicket> list, Context context, Activity parentActivity) {
		this.list = list;
		this.context = context;
		this.parentActivity = parentActivity;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int pos) {
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return list.get(pos).getHallTicket().getHallTicketId();
		// just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.eligibleeventlistviewadapter, null);
		}

		// Handle TextView and display string from your list
		EligibleHallTicket eligibleHallTicket = list.get(position);

		HallTicket hallTicket = eligibleHallTicket.getHallTicket();

		FBNUtil.setCompanyDetail(view, hallTicket);
		
		LinearLayout appliedOnLayout = (LinearLayout) view.findViewById(R.id.appliedOnLayout);
		TextView appliedOn = (TextView) view.findViewById(R.id.appliedOn);

		if (!"N".equals(eligibleHallTicket.getAppliedOn())) {
			appliedOn.setText(eligibleHallTicket.getAppliedOn());
			appliedOnLayout.setVisibility(View.VISIBLE);
		} else {
			appliedOnLayout.setVisibility(View.GONE);
		}
		LinearLayout approvedOnLayout = (LinearLayout) view.findViewById(R.id.approvedOnLayout);
		TextView approvedOn = (TextView) view.findViewById(R.id.approvedOn);
		if (!"N".equals(eligibleHallTicket.getApprovedOn())) {
			approvedOn.setText(eligibleHallTicket.getApprovedOn());
			approvedOnLayout.setVisibility(View.VISIBLE);
		} else {
			approvedOnLayout.setVisibility(View.GONE);
		}
		
		// Handle buttons and add onClickListeners
		Button applyButton = (Button) view.findViewById(R.id.apply_btn);
		Button denyButton = (Button) view.findViewById(R.id.deny_btn);
		Button shareButton = (Button) view.findViewById(R.id.shareImage_btn);
		Button showdetails = (Button) view.findViewById(R.id.showDetails);
		if (eligibleHallTicket.getIsApplied()) {
			applyButton.setVisibility(View.GONE);
			denyButton.setVisibility(View.VISIBLE);
		} else {
			applyButton.setVisibility(View.VISIBLE);
			denyButton.setVisibility(View.GONE);
		}

		applyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
						EligibleHallTicket eligibleHallTicket = list.get(position);
						HallTicketTask hallTicketTask = new HallTicketTask(FBNUtil.getRegisteredUserName(context),
								eligibleHallTicket.getHallTicket().getHallTicketId(), true, context, parentActivity);
						hallTicketTask.execute();
						notifyDataSetChanged();
				} else {
					FBNUtil.showInternetErrorDetails(parentActivity);
				}

			}
		});
		denyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmationDialog(list.get(position));
			}
		});

		if (eligibleHallTicket.getIsApplied() && eligibleHallTicket.getIsApproved()) {

			shareButton.setVisibility(View.VISIBLE);
			shareButton.setTooltipText("Please click here to Save/Share Hall Ticket");
			shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
						EligibleHallTicket eligibleHallTicket = list.get(position);
						String enrollmentNo = FBNUtil.getRegisteredUserName(context);
						String fineName = enrollmentNo + "_" + eligibleHallTicket.getHallTicket().getHallTicketId()
								+ ".PDF";
						File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
						if (file != null && file.exists()) {
							Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
							Intent share = new Intent();
							share.setAction(Intent.ACTION_SEND);
							share.setType("application/pdf");
							share.putExtra(Intent.EXTRA_STREAM, uri);
							share.putExtra(Intent.EXTRA_TEXT, "Hallticket of " + enrollmentNo);
							share.putExtra(Intent.EXTRA_TEXT,
									"Company Company ID " + eligibleHallTicket.getHallTicket().getHallTicketId());
							share.putExtra(Intent.EXTRA_SUBJECT, "Hallticket of " + enrollmentNo);
							share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							parentActivity.startActivity(Intent.createChooser(share, "Share File"));
						} else {
								ShareHallTicketTask hallTicketTask = new ShareHallTicketTask(enrollmentNo,
										eligibleHallTicket.getHallTicket().getHallTicketId(), context, parentActivity);
								hallTicketTask.execute();
						}
					} else {
						FBNUtil.showInternetErrorDetails(parentActivity);
					}
				}
			});

		} else {
			shareButton.setVisibility(View.INVISIBLE);
		}

		LinearLayout eligibleLayout = (LinearLayout) view.findViewById(R.id.eligibleLayout);
		
		eligibleLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EligibleHallTicket eligibleHallTicket = list.get(position);
				CompanyDetails.hallTicket = eligibleHallTicket.getHallTicket();
				navigateToCompanyDetailsActivity();
			}
		});
		
		showdetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EligibleHallTicket eligibleHallTicket = list.get(position);
				CompanyDetails.hallTicket = eligibleHallTicket.getHallTicket();
				navigateToCompanyDetailsActivity();
			}
		});

		return view;
	}
	
	public void navigateToCompanyDetailsActivity() {
		Intent homeIntent = new Intent(context, CompanyDetails.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

	private void showConfirmationDialog(final EligibleHallTicket eligibleHallTicket) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
		builder.setTitle("Confirmaation");
		builder.setMessage("Are you sure to Denay?");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
						String enrollmentNo = FBNUtil.getRegisteredUserName(context);
						HallTicketTask hallTicketTask = new HallTicketTask(enrollmentNo,
								eligibleHallTicket.getHallTicket().getHallTicketId(), false, context, parentActivity);
						hallTicketTask.execute();
						notifyDataSetChanged();
					} else {
						FBNUtil.showInternetErrorDetails(parentActivity);
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}