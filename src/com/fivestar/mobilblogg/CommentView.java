/**
 * 
 */
package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class CommentView extends ListActivity  {
	ProgressDialog dialog;
	Thread commentThread;
	ListActivity activity;
	MobilbloggApp app;
	int imgid = 0;
	ListView list;
	CommentViewAdapter adapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		activity = this;
		imgid = Integer.parseInt(getIntent().getStringExtra("imgid"));
				
		list = (ListView)findViewById(android.R.id.list);
		dialog = new ProgressDialog(CommentView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());

		this.setTitle("Kommentarer");
		
		dialog.show();
		commentThread = new Thread() {
			public void run() {

				final String jsonresponse = app.com.getComments(imgid);				

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								int len = json.length();
								CommentInfo ci = new CommentInfo(len);
								for(int i=0; i<len;i++) {
									ci.username[i]   = json.getJSONObject(i).get("author").toString();
									ci.comment[i]    = json.getJSONObject(i).get("comment").toString();
									ci.createdate[i] = json.getJSONObject(i).get("createdate").toString();
									ci.avatar[i] = app.com.getProfileAvatar(ci.username[i]);
								}
								System.out.println("SET ADAPTER");
								adapter = new CommentViewAdapter(activity, ci);
								activity.setListAdapter(adapter);
							} catch (JSONException j) {
								System.out.println("JSON error:" + j.toString());
							}
						} else {
							System.out.println("StartPage failure");
							Toast.makeText(activity, "Hämtningen misslyckades", Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		commentThread.start();
	}
			
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}