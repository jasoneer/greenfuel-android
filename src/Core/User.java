package com.isotix.nufuel;

import java.util.ArrayList;

public class User
{
	private int _id;
	private String _uname, _email, _passw;
	private String _token;

	private ArrayList<Station> desiredStationList;
	
	public User(int id, String uname, String email, String passw)
	{
		_id = id;
		_uname = uname;
		_email = email;
		_passw = passw;

		desiredStationList = new ArrayList<Station>();
	}

	public User(JSON json)
	{
		JSON.Object jsonObject = (JSON.Object)json;
		_id = (int)((JSON.Number)jsonObject.get("id")).value();
		_token = ((JSON.String)jsonObject.get("token")).value();
	}

	public ArrayList<Station> desiredStationList() { return desiredStationList; }
	
	public int id() { return _id; }
	public String uname() { return _uname; }
	public String email() { return _email; }
	public String passw() { return _passw; }

	public void uname(String uname) { _uname = uname; }
	public void email(String email) { _email = email; }
	public void passw(String passw) { _passw = passw; }

	public String token() { return _token; }
	public void token(String token) { token = _token; }

	/*public User(Cursor cursor)
	{
		this(cursor.getInt(cursor.getColumnIndex("id")),
			 cursor.getString(cursor.getColumnIndex("uname")),
			 cursor.getString(cursor.getColumnIndex("email")),
			 cursor.getString(cursor.getColumnIndex("passw")));
	}*/
}
