package com.maryapc.remotecontrol.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ActivityView extends MvpView {

	void startListen();

	void showRecognizedText(String resultText);

	void showAnswerDialog(String title, String message);

	void showToast(String message);

	void showSettingDialog();

	void updateIpTextView();
}
