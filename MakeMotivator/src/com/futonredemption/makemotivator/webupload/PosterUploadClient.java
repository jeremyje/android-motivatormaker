package com.futonredemption.makemotivator.webupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.futonredemption.makemotivator.Constants;

public class PosterUploadClient {

	public static final String EndpointUrl = Constants.Application.WebGalleryUrl + "/api/poster";
	public static final String ClientIdUrl = Constants.Application.WebGalleryUrl + "/api/clientid";
	public PosterUploadClient() {
		
	}
	
	public String getClientId(final Context context) throws JSONException, IllegalStateException, IOException {
		
		
		
		SharedPreferences prefs = context.getSharedPreferences(Constants.Preferences.ApplicationKey, Context.MODE_PRIVATE);
		String clientId = prefs.getString(Constants.Preferences.Key_WebGalleryClientId, "");
		if(clientId == null || clientId.length() < 1) {
			// Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(ClientIdUrl);
		    
	        // Add your data
	        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String jsonString = responseString(response);
	        clientId = getClientId(jsonString);
	        prefs.edit().putString(Constants.Preferences.Key_WebGalleryClientId, clientId).commit();
		}
		
	    return clientId;
	}
	
	public String execute(final PosterUploadWorkTaskModel model, final String clientId, final byte [] imageData) throws ClientProtocolException, IOException {
		
		final HttpParams httpParams = new BasicHttpParams(); 
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
    	final HttpClient httpClient = new DefaultHttpClient(httpParams);
		final HttpContext localContext = new BasicHttpContext();
		final HttpPost httpPost = new HttpPost(EndpointUrl);

		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("title", new StringBody(model.title));
		entity.addPart("subtitle", new StringBody(model.subtitle));
		entity.addPart("frameColor", new StringBody(Integer.toString(model.frameColor)));
		entity.addPart("clientId", new StringBody(clientId));
		File file = new File(model.filePath);
		
		entity.addPart("file", new ByteArrayBody(imageData, file.getName()));
		/*
		entity.addPart("file", new InputStreamBody(in, FileUtils.getFileName(model.filePath)) {
			@Override
			public long getContentLength() {
				return in.
			}
		});
		*/
		httpPost.setEntity(entity);
		
		HttpResponse response = httpClient.execute(httpPost, localContext);
		return responseString(response);
	}
	
	protected String responseString(HttpResponse response) throws IllegalStateException, IOException {
		InputStream contentStream = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(contentStream, "UTF-8");
		BufferedReader reader = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		
		return sb.toString();
	}
	
	public String getClientId(String jsonResponse) throws JSONException {
		JSONObject jo = new JSONObject(jsonResponse);
		return jo.getString("clientId");
	}
	
	public String getUrl(String jsonResponse) throws JSONException {
		JSONObject jo = new JSONObject(jsonResponse);
		return jo.getString("imageUrl");
	}
}
