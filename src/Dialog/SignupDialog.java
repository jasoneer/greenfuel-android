package com.isotix.nufuel;

import android.content.Context;

import android.view.View;
import android.view.LayoutInflater;

import android.widget.TextView;

public class SignupDialog extends CDialog implements View.OnClickListener
{
	private TextView userText,
					 emailText,
					 passText;
	
	public SignupDialog(Context context)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Create An Account");
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_signup, null);
		
		userText  = (TextView)layout.findViewById(R.id.dialog_signup_user);
		emailText = (TextView)layout.findViewById(R.id.dialog_signup_email);
		passText  = (TextView)layout.findViewById(R.id.dialog_signup_pass);
		
		setView(layout);

		addButton(ACCEPT, "Create", this);
		addDivider();
		addButton(CANCEL, "Cancel", this);
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case ACCEPT:
				JSON json = Network.SendRequest(Network.BuildTestJSONRequest());
				System.out.println(json);
			break;
			default: DialogStack.Back(); break;
		}
	}
}
