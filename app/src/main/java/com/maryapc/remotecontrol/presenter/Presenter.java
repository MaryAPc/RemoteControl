package com.maryapc.remotecontrol.presenter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.maryapc.remotecontrol.MyApplication;
import com.maryapc.remotecontrol.R;
import com.maryapc.remotecontrol.view.ActivityView;

@InjectViewState
public class Presenter extends MvpPresenter<ActivityView> {

	private static final String TAG = "Presenter";

	public static String sServerIp = "192.168.0.103";
	public static final int SERVER_PORT = 8381;

	private Socket mSocket;
	private Context mContext = MyApplication.getInstance();

	public void listenCommand() {
		getViewState().startListen();
	}

	public void setRecognizedText(String resultText) {
		getViewState().showRecognizedText(resultText);
	}

	public void sendCommand(String command) {
		new TCPTask().execute(command);
	}

	public void showSettingDialog() {
		getViewState().showSettingDialog();
	}

	public void setCurrentIpText() {
		getViewState().updateIpTextView();
	}

	private class TCPTask extends AsyncTask<String, Void, Void> {

		private String status = "";

		@Override
		protected Void doInBackground(String... message) {
			try {
				InetAddress serverAddr = InetAddress.getByName(sServerIp);
				mSocket = new Socket(serverAddr, SERVER_PORT);
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
				out.println(message[0]);
				out.flush();
				BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				status = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					mSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (status.equals("error")){
				getViewState().showAnswerDialog(mContext.getString(R.string.error), mContext.getString(R.string.command_not_support));
			} else {
				getViewState().showToast(mContext.getString(R.string.command_done));
			}
		}
	}
}
