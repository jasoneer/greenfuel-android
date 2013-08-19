package com.isotix.nufuel;

import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
//import java.io.OutputStreamWriter;

import java.util.ArrayList;

import android.text.format.Time;

public class Network
{
	public static final String SERVER_URL =
	"http://nufuel.isotix.com/mobile.php";
	
	public static final String MOBILE_URL =
	"http://nufuel.isotix.com/mobile/nufuel.apk";
	
	public static final String APP_VERSION = "0.1";

	public static String Encode(String rawString)
	{
		String encString = null;
		try {
			encString = URLEncoder.encode(rawString, "UTF-8");
		} catch(Exception ex) { ex.printStackTrace(); }
		return encString;
	}
	public static String Decode(String encString)
	{
		String decString = null;
		try {
			decString = URLDecoder.decode(encString, "UTF-8");
		} catch(Exception ex) { ex.printStackTrace(); }
		return decString;
	}

	public static StringBuilder GetStringBuilder()
	{
		return new StringBuilder();
	}
	public static StringBuilder GetLoggedInStringBuilder(User user)
	{
		StringBuilder builder = GetStringBuilder();
		
		// User ID
		builder.append("uid=").append(user.id()).append('&');
		// Access Token
		builder.append("tok=").append(user.token()).append('&');
		
		return builder;
	}
	
	/*
	public static void UpdateStationList()
	{
		ArrayList<String> response = SendRequest(BuildStationListRequest());
		for(String line : response)
		{
			System.out.println("RESPONSE: " + line);
			String[] params = line.split(";");
			App.Database.insertStation(new Station(Integer.parseInt(params[0]),
													  Double.parseDouble(params[1]),
													  Double.parseDouble(params[2]),
													  params[3], params[4], params[5],
													  params[6]));
		}
	}*/

	/*
	 * Update Request
	 */
	public static String BuildUpdateAppRequest(User user)
	{
		StringBuilder builder = GetLoggedInStringBuilder(user);
		builder.append("app_check=").append(APP_VERSION);
		
		return builder.toString();
	}

	public static String BuildTestJSONRequest()
	{
		StringBuilder builder = GetStringBuilder();
		builder.append("test_json=");
		
		return builder.toString();
	}
	
	/*
	 * Send Request To Server
	 */
	public static JSON SendRequest(String request)
	{
		if(request == null)
			return null;
		int length = request.getBytes().length;

		System.out.println(request);

		JSON response = null;
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection)new URL(Network.SERVER_URL).openConnection();
			httpConnection.setFixedLengthStreamingMode(length);
			httpConnection.setConnectTimeout(5000);
			httpConnection.setUseCaches(false);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//httpConnection.setRequestProperty("Content-Length", "" + length);
			//httpConnection.setRequestProperty("Content-Language", "en-US");
			
			httpConnection.connect();
			
			DataOutputStream writer = new DataOutputStream(httpConnection.getOutputStream());
			writer.writeBytes(request);
			writer.flush();
			System.out.println(writer.size());
			writer.close();

			InputStream inputStream = httpConnection.getInputStream();
			JSON.Parser parser = new JSON.Parser(inputStream);
			response = parser.parse();
			inputStream.close();

			System.out.println(httpConnection.getResponseMessage());
		} catch(Exception ex) {
			ex.printStackTrace();
			response = null;
		}
		if(httpConnection != null)
			httpConnection.disconnect();
		return response;
	}
	
	public static JSON SendRequestURL(String urlRequest)
	{
		if(urlRequest == null)
			return null;

		System.out.println(urlRequest);

		JSON response = null;
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection)new URL(urlRequest).openConnection();
			httpConnection.setConnectTimeout(5000);
			httpConnection.setUseCaches(false);
			httpConnection.setDoInput(true);
			httpConnection.connect();

			JSON.Parser parser = new JSON.Parser(httpConnection.getInputStream());
			response = parser.parse();
		} catch(Exception ex) {
			ex.printStackTrace();
			response = null;
		}
		if(httpConnection != null)
			httpConnection.disconnect();
		return response;
	}
}
