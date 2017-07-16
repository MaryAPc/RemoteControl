package com.maryapc.remotecontrol.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.maryapc.remotecontrol.R;
import com.maryapc.remotecontrol.presenter.Presenter;
import com.maryapc.remotecontrol.view.ActivityView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.gui.RecognizerActivity;

public class MainActivity extends MvpAppCompatActivity implements ActivityView {

	@BindView(R.id.activity_main_image_button_micro)
	ImageButton mMicroImageButton;
	@BindView(R.id.activity_main_text_view_recognized_text)
	TextView mRecognizedTextView;
	@BindView(R.id.activity_main_text_view_current_ip)
	TextView mCurrentIpTextView;

	@InjectPresenter
	Presenter mPresenter;

	private static final int REQUEST_CODE = 1;

	private static final String TAG = "MainActivity";
	private static final String API_KEY = "343ee944-693b-4f8e-b769-72415b444c96";
	private static final String IP_ADDRESS = "ip_address";
	private static final String APP_PREFERENCES = "app_preferences";
	private String newIpAddress = "";
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		mSharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
		SpeechKit.getInstance().configure(getApplicationContext(), API_KEY);

		String ipAddress = mSharedPreferences.getString(IP_ADDRESS, "");
		if (!ipAddress.equals("")) {
			Presenter.sServerIp = ipAddress;
		}
		mPresenter.setCurrentIpText();

		mMicroImageButton.setOnClickListener(view -> mPresenter.listenCommand());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, requestCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
				final String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT);
				mPresenter.setRecognizedText(result);
				mPresenter.sendCommand(result);
			} else if (resultCode == RecognizerActivity.RESULT_ERROR) {
				String error = ((ru.yandex.speechkit.Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR)).getString();
				Log.e(TAG, error);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_menu_ip_address:
				mPresenter.showSettingDialog();
				break;
		}
		return true;
	}

	@Override
	public void startListen() {
		Intent intent = new Intent(MainActivity.this, RecognizerActivity.class);
		intent.putExtra(RecognizerActivity.EXTRA_MODEL, Recognizer.Model.QUERIES);
		intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Recognizer.Language.RUSSIAN);
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	public void showRecognizedText(String resultText) {
		mRecognizedTextView.setText(resultText);
	}

	@Override
	public void showAnswerDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error)
				.setMessage(R.string.command_not_support)
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Введите новый IP адрес сервера");
		final EditText input = new EditText(this);
		builder.setView(input);
		builder.setPositiveButton("OK", (dialog, whichButton) -> {
			newIpAddress = input.getText().toString();
			if (!newIpAddress.equals("")) {
				Presenter.sServerIp = newIpAddress;
				mPresenter.setCurrentIpText();
				mSharedPreferences.edit().putString(IP_ADDRESS, newIpAddress).apply();
			}
		});
		builder.show();
	}

	@Override
	public void updateIpTextView() {
		String resultText = getString(R.string.ip_address_text, Presenter.sServerIp);
		mCurrentIpTextView.setText(resultText);
	}
}
