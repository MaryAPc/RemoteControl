package com.maryapc.remotecontrol;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

@InjectViewState
public class Presenter extends MvpPresenter<ActivityView> {

	public void listenCommand() {
		getViewState().startListen();
	}

	public void setRecognizedText(String resultText) {
		getViewState().showRecognizedText(resultText);
	}
}
