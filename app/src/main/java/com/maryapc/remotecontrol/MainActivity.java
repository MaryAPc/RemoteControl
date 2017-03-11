package com.maryapc.remotecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

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

	@InjectPresenter
	Presenter mPresenter;

	private static final int REQUEST_CODE = 1;
	private static final String TAG = "MainActivity";
	private static final String API_KEY = "key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		SpeechKit.getInstance().configure(getApplicationContext(), API_KEY);

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
	public void showUnsupportedCommand() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Ошибка!")
					.setMessage("Команда неподдерживается")
					.setCancelable(true)
					.setNeutralButton("OK", null);
			AlertDialog dialog = builder.create();
			dialog.show();
	}
}
