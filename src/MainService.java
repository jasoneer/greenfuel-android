package com.isotix.nufuel;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.ArrayList;

import android.content.Intent;
import android.content.Context;

import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;

import java.nio.charset.Charset;
import android.util.Log;

public class MainService extends Service implements Runnable
{
	/*
	 * Background Service Thread
	 */
	private Handler serviceHandler;
	
	public void run()
	{
		//Thread.getDefaultUncaughtExceptionHandler();
		//Thread.setDefaultUncaughtExceptionHandler();

		Looper.prepare();
		serviceHandler = new Handler();
		Looper.loop();
	}

	/*
	 * Intent Method
	 */
	private static Runnable ServiceRunnable;
	 
	public static Intent GetIntent(Runnable runnable)
	{
		ServiceRunnable = runnable;
		return new Intent(App.Current, MainService.class);
	}

	/*
	 * Service Methods
	 */
	@Override
    public void onCreate()
    {
    		App.CurrentService = this;
    		new Thread(this).start();
    	}
	@Override
    public void onDestroy() { }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
    		if(ServiceRunnable != null) {
    			serviceHandler.post(ServiceRunnable);
    			ServiceRunnable = null;
    		}
    		return START_STICKY;
    	}
	
	/*
	 * Unused Methods
	 */
	@Override // A client is binding to service with bindService()
    public IBinder onBind(Intent intent) { return null; }
	@Override // All clients have unbound with unbindService()
    public boolean onUnbind(Intent intent) { return false; }
    @Override // A client is rebinding to service with bindService()
	public void onRebind(Intent intent) { }
}


/*public static final String CMD = "com.isotix.Command";
	public static final String ARG = "com.isotix.Argument";
	
	private static Intent ServiceIntent;*/

/*
	 * Intent Argument
	 */
	/*private static Object ServiceArgument;
	ServiceIntent.putExtra(CMD, command);
	public static void SetIntentArgument(Object object) { ServiceArgument = object; }*/

/*
	 * Background Service Handler
	 */
	/*public static final int START = 0;
	public static final int LOGIN = 1;
	
	public static final int UPDATE = 2;
	 
	private static final class MainServiceHandler extends Handler
	{
		@Override
		public void handleMessage(Message message)
		{
			switch(message.what)
			{
				case UPDATE:
					//response = Network.SendRequest(Network.BuildUpdateAppRequest());
				break;
			}
			ArrayList<String> response = null;
				User user = null;
			
				State.Sending(true);
				try {
					switch(command) {
						case START:
							response = Send(Network.BuildUpdateAppRequest());
							if(response != null && response.get(0).equals("YES")) {
								MainThread.post(new Runnable() { public void run() {
									SoActivity.Current.event(SoActivity.UPDATE_APP); } });
							} else {
								MainThread.post(new Runnable() { public void run() {
									SoActivity.Current.event(SoActivity.UPDATE_SKP); } });
							}
						break;
						case LOGIN:
							MainThread.post(new Runnable() { public void run() {
								SoActivity.Current.event(SoActivity.LOGIN_SERVER_INIT); } });
							Network.ServerAuthCode = (String)Arguments.remove(argIndex);
							response = Send(Network.BuildUserLoginRequest(Network.ServerAuthCode));
							if(response != null) {
								String[] params = response.get(0).split(",");
								System.out.println("Char Value: " + (params[0].codePointAt(2)));
								System.out.println("String Length: " + params[0].length());
								System.out.println("Byte Length: " + params[0].getBytes().length);
								
								State.CurrentUser(new User(params[0], params[1], params[2],
														   params[3], params[4]));
														   
								//Network.ServerAuthCode = response.get(1);
							}
							MainThread.post(new Runnable() { public void run() {
								SoActivity.Current.event(SoActivity.LOGIN_SERVER_DONE); } });
						break;
						case FETCH_ROUTES:
							user = (User)Arguments.remove(argIndex);
							response = Send(Network.BuildUserRoutesRequest(user));
							for(String line : response)
							{
								System.out.println("RESPONSE: " + line);
								String[] params = line.split(",");
								user.AddRoute(new Route(params[0], params[1], params[2], params[3]));
							}
						break;
						case SEND_ROUTE:
							MainThread.post(new Runnable() { public void run() {
								SoActivity.Current.event(SoActivity.SEND_ROUTE_STARTED); } });
							response = Send(Network.BuildCreateRouteRequest());
							System.out.println("Route Sent: " + response.get(0));
							MainThread.post(new Runnable() { public void run() {
								SoActivity.Current.event(SoActivity.SEND_ROUTE_STOPPED); } });
						break;
						case FETCH_FRIENDS:
							user = (User)Arguments.remove(argIndex);
							response = Send(Network.BuildUserFriendsRequest(user));
							for(String line : response)
							{
								System.out.println("RESPONSE: " + line);
								String[] params = line.split(",");
								//user.AddFriend(new User(params[0], null, params[1], params[2], params[3]));
							}
						break;
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				State.Sending(false);
		}
	}*/
