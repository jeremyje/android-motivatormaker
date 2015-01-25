package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.beryl.diagnostics.Logger;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.activities.WebGalleryUploadActivity;
import com.futonredemption.makemotivator.contracts.IPosterCommand;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SharePosterDialogBuilder {

	private final Context context;
	
	public SharePosterDialogBuilder(Context context) {
		this.context = context;
	}
	
	protected static Intent createTemplateIntent() {
		Intent shareImage = new Intent(Intent.ACTION_SEND);
		//shareImage.addCategory(Intent.CATEGORY_DEFAULT);
		shareImage.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });

		shareImage.putExtra(Intent.EXTRA_SUBJECT, "subject");
		shareImage.putExtra(Intent.EXTRA_TEXT, "text");
		shareImage.putExtra("sms_body", "text");
		Uri uri = Uri.parse("file:///");
		shareImage.setType("image/*");
		shareImage.putExtra(Intent.EXTRA_STREAM, uri);
		return shareImage;
	}

	protected static void loadHandlers(Context context, Intent baseIntent, IntentListAdapter adapter) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> handlers = pm.queryIntentActivities(baseIntent, PackageManager.MATCH_DEFAULT_ONLY);
		List<ListItemViewModel> list = new ArrayList<ListItemViewModel>();
		final String appPackageName = context.getPackageName();
		for(ResolveInfo handler : handlers) {
			ListItemViewModel model = new ListItemViewModel();
			model.packageName = handler.activityInfo.packageName;
			model.activityName = handler.activityInfo.name;
			model.icon = handler.loadIcon(pm);
			
			if(appPackageName.equals(model.packageName)) {
				model.title = (String) context.getText(R.string.poster_in_poster);
			} else {
				model.title = handler.loadLabel(pm).toString();
			}
			
			list.add(model);
		}
		
		Collections.sort(list);
		
		ListItemViewModel webUpload = new ListItemViewModel();
		webUpload.activityName = WebGalleryUploadActivity.class.getName();
		webUpload.packageName = context.getPackageName();
		webUpload.title = context.getString(R.string.upload_to_motivator_web_gallery);
		webUpload.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
		adapter.add(webUpload);
		
		for(ListItemViewModel item : list) {
			adapter.add(item);
		}
		
        adapter.notifyDataSetChanged();
	}
	
	static class IntentListAdapter extends ArrayAdapter<ListItemViewModel> implements OnItemClickListener {

		public IntentListAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		public IntentListAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Context context = this.getContext();
			
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				//convertView = inflater.inflate(R.layout.intentlist_listitem, parent, false);
				convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
				
				ViewHolder holder = new ViewHolder();
				holder.titleView = (TextView)convertView;
				convertView.setTag(holder);

				holder.titleView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				holder.titleView.setMaxLines(2);
			}
			
			final ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.bind(this.getItem(position));
			return convertView;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListItemViewModel viewModel = (ListItemViewModel)parent.getItemAtPosition(position);
			Logger.d(viewModel.activityName);
			//SharePosterDialogFragment.this.dismiss();
		}
	}
	
	static class ViewHolder {
		TextView titleView;
		
		void bind(ListItemViewModel viewModel) {
			final Context context = titleView.getContext();
			float padding = context.getResources().getDimension(R.dimen.ActivityListDrawablePadding);
			titleView.setCompoundDrawablesWithIntrinsicBounds(viewModel.icon, null, null, null);
			titleView.setCompoundDrawablePadding((int)padding);
			titleView.setText(viewModel.title);
		}
	}
	
	static class ListItemViewModel implements Comparable<ListItemViewModel> {
		public String title;
		public Drawable icon;
		public String packageName;
		public String activityName;
		
		public int compareTo(ListItemViewModel another) {
			return this.title.compareTo(another.title);
		}
	}
	
	public void show(final IPosterCommand posterCommand) {
		final IntentListAdapter adapter = new IntentListAdapter(context, 0);
        
        loadHandlers(context, createTemplateIntent(), adapter);
        
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getText(R.string.share_poster))
		.setCancelable(true).setAdapter(adapter, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final ListItemViewModel viewModel = adapter.getItem(which);
				posterCommand.sharePoster(viewModel.packageName, viewModel.activityName);
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
}
