package com.isotix.greenfuel.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.isotix.greenfuel.R;

public class LoginDialog extends CDialog implements View.OnClickListener
{
	private TextView loginText,
					 passText;

	private SignupDialog signupDialog;
	
	public LoginDialog(Context context)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Login To Account");
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_login, null);

		layout.findViewById(R.id.dialog_login_signup).setOnClickListener(this);
		
		loginText = (TextView)layout.findViewById(R.id.dialog_login_login);
		passText = (TextView)layout.findViewById(R.id.dialog_login_pass);
		
		setView(layout);

		addButton(ACCEPT, "Login", this);
		addDivider();
		addButton(CANCEL, "Cancel", this);

		signupDialog = new SignupDialog(context);
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case ACCEPT:
				
			break;
			case R.id.dialog_login_signup:
				DialogStack.Show(signupDialog);
			break;
			default: DialogStack.Back(); break;
		}
	}
}
