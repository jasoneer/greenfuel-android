package com.isotix.greenfuel.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Stack;

public class DialogStack
{
	private static ProgressDialog progressDialog;
	private static Stack<Dialog> dialogStack = new Stack<Dialog>();

	public static void Clear()
	{
		if(!dialogStack.empty())
			dialogStack.peek().dismiss();
		dialogStack.clear();
	}

	public static void Show(Dialog dialog)
	{
		if(!dialogStack.empty())
			dialogStack.peek().dismiss();
		dialogStack.push(dialog);
		dialog.show();
	}

	public static void Back()
	{
		if(dialogStack.empty())
			return;
		dialogStack.pop().dismiss();

		if(!dialogStack.empty())
			dialogStack.peek().show();
	}

	public static void ShowProgressDialog(Context context, String title, String text,
										  DialogInterface.OnCancelListener listener)
	{
		progressDialog = ProgressDialog.show(context, title, text, true,
											 listener != null ? true : false, listener);
	}

	public static void SetProgressDialogTitle(String title)
	{
		if(progressDialog != null)
			progressDialog.setTitle(title);
	}

	public static void SetProgressDialogMessage(String message)
	{
		if(progressDialog != null)
			progressDialog.setMessage(message);
	}

	public static void DismissProgressDialog()
	{
		if(progressDialog != null)
			progressDialog.dismiss();
		progressDialog = null;
	}
}
