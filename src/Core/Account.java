package com.isotix.nufuel;

import android.content.Intent;

import android.widget.Toast;

public class Account
{
	private static boolean Cancel;

	private final static String SUCCESS = "SUCCESS";
	private final static String FAILURE = "FAILURE";

	public static void Login(String login, String passw)
	{
		final String _login = login;
		final String _passw = passw;
	
		Intent intent = MainService.GetIntent(new Runnable() {
			@Override
			public void run() {
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading",
													   "Logging in user..",
													   null);
					}
				});

				JSON response = Network.SendRequest(BuildUserLoginRequest(_login, _passw));
				if(response != null &&
				   SUCCESS.equals(((JSON.String)((JSON.Object)response).get("code")).value())) {
					App.CurrentUser = new User(response);
					Toast.makeText(App.Current, "Login Successful", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(App.Current, "Login Failed", Toast.LENGTH_SHORT).show();

				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						App.CurrentActivity.findViewById(R.id.main).invalidate();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}

	public static void Logout(User user)
	{
		final User _user = user;
	
		Intent intent = MainService.GetIntent(new Runnable() {
			@Override
			public void run() {
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading",
													   "Logging out user..",
													   null);
					}
				});

				JSON response = Network.SendRequest(BuildUserLogoutRequest(_user));
				if(response != null &&
				   SUCCESS.equals(((JSON.String)((JSON.Object)response).get("code")).value())) {
					App.CurrentUser = null;
					Toast.makeText(App.Current, "Logout Successful", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(App.Current, "Logout Failed", Toast.LENGTH_SHORT).show();
				
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						App.CurrentActivity.findViewById(R.id.main).invalidate();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}

	public static void Create(String uname, String email, String passw)
	{
		final String _uname = email;
		final String _email = email;
		final String _passw = passw;
		
		Intent intent = MainService.GetIntent(new Runnable() {
			@Override
			public void run() {
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading",
													   "Creating user..",
													   null);
					}
				});

				JSON response = Network.SendRequest(BuildUserCreateRequest(_uname, _email, _passw));
				if(response != null &&
				   SUCCESS.equals(((JSON.String)((JSON.Object)response).get("code")).value())) {
					App.CurrentUser = new User(response);
					Toast.makeText(App.Current, "User Created", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(App.Current, "User Creation Failed", Toast.LENGTH_SHORT).show();
				
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						App.CurrentActivity.findViewById(R.id.main).invalidate();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}

	/*
	 * Request Strings
	 */
	private static String BuildUserLoginRequest(String login, String pass)
	{
		if(login == null || pass == null) return null;
	
		StringBuilder builder = Network.GetStringBuilder();

		builder.append("cmd=user_login").append('&');
		builder.append("login=").append(Network.Encode(login)).append('&');
		builder.append("pass=").append(Network.Encode(pass));
		return builder.toString();
	}
	private static String BuildUserLogoutRequest(User user)
	{
		StringBuilder builder = Network.GetLoggedInStringBuilder(user);

		builder.append("cmd=user_logout");
		return builder.toString();
	}
	private static String BuildUserCreateRequest(String uname, String email, String passw)
	{
		StringBuilder builder = Network.GetStringBuilder();

		builder.append("cmd=user_create").append('&');
		builder.append("uname=").append(Network.Encode(uname)).append('&');
		builder.append("email=").append(Network.Encode(email)).append('&');
		builder.append("passw=").append(Network.Encode(passw));
		
		return builder.toString();
	}

	private static String BuildStationListRequest(User user)
	{
		StringBuilder builder = Network.GetLoggedInStringBuilder(user);

		builder.append("cmd=dstation_list");
		return builder.toString();
	}
	private static String BuildStationInfoRequest(User user, int[] idList, String[] optList)
	{
		if(idList == null) return null;
	
		StringBuilder builder = Network.GetLoggedInStringBuilder(user);

		builder.append("cmd=dstation_info").append('&');
		builder.append("id=");
		for(int id : idList)
			builder.append(id).append(',');
		builder.deleteCharAt(builder.length()-1);
		if(optList != null)
		{
			builder.append("&opt=");
			for(String opt : optList)
				builder.append(opt).append(',');
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}
}
