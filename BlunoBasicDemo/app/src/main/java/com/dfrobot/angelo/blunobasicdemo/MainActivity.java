package com.dfrobot.angelo.blunobasicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BlunoLibrary {

	Button button_onOff, button_setNewMinTemp, button_connect;
	TextView statusView, currentTempView;
	EditText newMinTempInput;

	//블루투스 연결될 때 버튼을 누를 수 있도록 한다.
	private boolean isButtonOnOffActive;
	private boolean isButtonSetMinTempActive;

	//병의 상태 (병이 작동중인지? 병이 지금 보온 중인지?)
	private boolean isBottleActivated;
	private boolean isBottleHeating;

	private String lastMinTemp;
	private String currentTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		//블루노 나노 프로세스 시작
		onCreateProcess();
		//시리얼 통신 시작!
		serialBegin(57600);


		//버튼과 텍스트뷰 불러오기
		statusView = (TextView) findViewById(R.id.statusView);
		currentTempView = (TextView) findViewById(R.id.currentTempView);
		newMinTempInput = (EditText) findViewById(R.id.newMinTempInput);

		button_onOff = (Button) findViewById(R.id.button_onOff);
		button_setNewMinTemp = (Button) findViewById(R.id.button_setNewMinTemp);

		//처음에 어떻게 변수를 선언하거나 창, 버튼 등에 어떤 문자를 띄울지 설정한다.
		lastMinTemp = "20\n";

		button_connect = (Button) findViewById(R.id.button_connect);
		button_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 블루노 나노 스켄을 실시한다.
				buttonScanOnClickProcess();
			}
		});

		//블루노 나노 작동 자체를 아예 멈추거나 작동시킬때
		button_onOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isButtonOnOffActive){
					Toast.makeText(MainActivity.this, "보온병을 on/off 할 수 없습니다.", Toast.LENGTH_SHORT).show();
				}

				//작동 여부를 누를 수 있을 때 (버튼 텍스트 자체는 나중에 바꾼다!)
				else{
					//400 이상의 변수를 보내서 작동을 중지한다.
					if(isBottleActivated){
						serialSend("404\n");
					}
					else{
						//마지막으로 저장된 minTemp를 보낸다. (작동시키는 효과)
						serialSend(lastMinTemp);
					}
				}
			}
		});

		//병의 최저 온도를 수정하고자 할 때
		//newMinTempInput에서 테스트를 가져와서 보낸다.
		button_setNewMinTemp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isButtonSetMinTempActive){
					Toast.makeText(MainActivity.this, "보온병을 on/off 할 수 없습니다.", Toast.LENGTH_SHORT).show();
				}
				else{
					serialSend(newMinTempInput.getText().toString());
					newMinTempInput.setText("");    //입력창 비우기
				}
			}
		});

	}

	// 블루투스 연결 상태가 바뀌면 이에 맞는 action을 취한다.
	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionStateEnum) {
		switch (theConnectionStateEnum) {
			case isConnected:
				Toast.makeText(this, "젖병과 연결되었습니다", Toast.LENGTH_SHORT).show();
				isButtonOnOffActive = true;
				isButtonSetMinTempActive = true;
				button_setNewMinTemp.setText("설정하기");
				break;
			case isConnecting:
				Toast.makeText(this, "젖병과 연결중...", Toast.LENGTH_SHORT).show();
				break;
			case isToScan:
				isButtonOnOffActive = false;
				isButtonSetMinTempActive = false;
				statusView.setText("젖병과 연결하세요");
				button_onOff.setText("사용불가");
				button_setNewMinTemp.setText("사용불가");
				Toast.makeText(this, "연결 대기중", Toast.LENGTH_SHORT).show();
				break;
			case isScanning:
				break;
			case isDisconnecting:
				isButtonOnOffActive = false;
				isButtonSetMinTempActive = false;
				statusView.setText("젖병과 연결하세요");
				button_onOff.setText("사용불가");
				button_setNewMinTemp.setText("사용불가");
				Toast.makeText(this, "연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPauseProcess() {
		super.onPauseProcess();
		onPauseProcess();
	}

	@Override
	protected void onStop() {
		super.onStop();
		onStopProcess();
	}

	@Override
	protected void onResume() {
		super.onResume();
		onResumeProcess();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();
	}

	/*
	 * <시리얼 통신 결과 처리>
	 *
	 * 시리얼 통신으로 받은 메세지를
	 * state # heating _ currentTemp ( minTemp )
	 * 로 나눠서 처리한다.
	 * */
	@Override
	public void onSerialReceived(String theString) {
		if(theString.length() < 15 || theString==null){

		}else{

			String msg_state = theString.substring((theString.indexOf("~"))+1, theString.indexOf("#"));
			String msg_heating = theString.substring(theString.indexOf("#") + 1, theString.indexOf("_"));;
			String msg_currentTemp = theString.substring(theString.indexOf("_") + 1, theString.indexOf("["));
			String msg_minTemp = theString.substring(theString.indexOf("[") + 1, theString.indexOf("]"));

			//병 자체가 작동을 멈춘 경우
			if(msg_state.equalsIgnoreCase("Mdac")){
				statusView.setText("작동 중지");
				button_onOff.setText("OFF");
				isBottleActivated = false;
			}

			//작동이 멈추지 않은 경우
			else {
				button_onOff.setText("ON");
				isBottleActivated = true;

				if (msg_state.equalsIgnoreCase("Minv")){
					Toast.makeText(this, "유효하지 않은 최소 온도입니다...\n(섭씨 50도까지!)", Toast.LENGTH_SHORT).show();
				}
				else if(msg_state.equalsIgnoreCase("Mcgm")){
					Toast.makeText(this, "최저온도를 섭씨" + msg_minTemp + "C 로\n업데이트 완료!", Toast.LENGTH_SHORT).show();
				}
				//발열 여부는 항상 업데이트한다!!
				if(msg_heating.equalsIgnoreCase("stp")){
					statusView.setText("대기중");
					isBottleHeating = false;
				}else if(msg_heating.equalsIgnoreCase("run")){
					statusView.setText("발열중");
					isBottleHeating = true;
				}

				//...실시간 온도 업데이트(작동되는 경우만)

				currentTempView.setText(msg_currentTemp);

			}

			lastMinTemp = msg_minTemp;


		}//else

	}


}
