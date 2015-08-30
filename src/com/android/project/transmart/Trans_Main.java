package com.android.project.transmart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Trans_Main extends Activity implements OnClickListener {
	PopupWindow etPopupWindow;
	StateItem stateItem = new StateItem();
	StateIteamDTO stateItemDTO = new StateIteamDTO();
	ArrayList<StateItem> arrayStateItem = new ArrayList<StateItem>();
	ArrayList<StateIteamDTO> arrayStateItemDTO = new ArrayList<StateIteamDTO>();
	ImageView mIv1, mIv2;
	TextView mTv1, mTv2, mTv3, mTv4, mTv5, mTv6;
	private String fromCode, toCode;
	private int fromidInt, toidInt;
	private String m_appId = "615A8D6563DD0FA1D0540F9B26A88A1712775ACE";

	private AnimationDrawable mAni;

	private StateItemDAO mDAO;
	
	private InternetChk internetCheck;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		getEndPopup();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mDAO = new StateItemDAO(this);
		
		internetCheck = new InternetChk(this);		 
		
		boolean chk_3g = internetCheck.isOnline();
		boolean chk_mobile = internetCheck.isOnlineToMobile();
		boolean chk_wifi = internetCheck.isOnlineToWifi();
		
		if(chk_3g == false && chk_mobile == false && chk_wifi == false){
			dialog_warning();
		}else{
			
		}
		
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		mTv3.setText(clipboardManager.getText().toString());
		//getTransComplete();
	}
	
	// 환경설정앱 호출
	public void opMove(){   
    	Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setComponent(new ComponentName(
				"com.android.settings", 
				"com.android.settings.WirelessSettings"));
		startActivity(intent);
    }
	
	private void dialog_warning(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("경고");
		builder.setMessage("인터넷 환경이 원할하지 않지 않습니다. 다시 시도해 주세요.");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// showMsg("which : "+ which);
				if (which == -1) {
					// 확인
					opMove();
				}else {
					// 취소
					finish();
				}
			}
		};

		builder.setPositiveButton("인터넷 환경설정", listener);
		builder.setNegativeButton("종료", listener);
		builder.show();
	}

	private void getEndPopup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("종료");
		builder.setMessage("번역기를 종료 하시겠습니까?");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// showMsg("which : "+ which);
				if (which == -1) {
					finish();
				} else {
					alertDialog.dismiss();
				}
			}
		};

		builder.setPositiveButton("예", listener);
		builder.setNegativeButton("아니오", listener);
		alertDialog = builder.create();
		alertDialog.show();
	}

	private void getSpeak(String tempText, String language)
			throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		String text = URLEncoder.encode(tempText, "utf-8");
		// text = URLEncoder.encode("hi hello myname is kimyongyeon", "utf-8");;
		// language = "en";
		String uri = "http://api.microsofttranslator.com/v2/Http.svc/Speak?appId="
				+ m_appId
				+ "&text="
				+ text
				+ "&language="
				+ language
				+ "&format=audio/mp3";

		Log.d("kimyongyeon", uri);

		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// String encoding = new
		// sun.misc.BASE64Encoder().encode("username      assword".getBytes());
		// conn.setRequestProperty ("Authorization", "Basic " + encoding);
		conn.setRequestMethod("GET");

		conn.connect();
		InputStream in = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		text = reader.readLine();
		// System.out.println(text);
		conn.disconnect();

		MediaPlayer player = new MediaPlayer();
		Uri u = Uri.parse(uri);
		player.setDataSource(this, u);
		player.prepare();
		player.start();
	}

	public void writeFile(InputStream is, OutputStream os) throws IOException {
		int c = 0;
		while ((c = is.read()) != -1)
			os.write(c);
		os.flush();
	}

	public String getBuildRequest(String message, String sourceLanguge,
			String targetLanguage) throws XmlPullParserException {
		// http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=615A8D6563DD0FA1D0540F9B26A88A1712775ACE&text=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94&from=ko&to=en

		String requestString = "http://api.microsofttranslator.com/v2/Http.svc/Translate?";
		// common Request fields (required)
		requestString += "AppId=" + m_appId;
		requestString += "&text=" + message;
		requestString += "&from=" + sourceLanguge;
		requestString += "&to=" + targetLanguage;

		Log.d("kimyongyeon", requestString);

		try {
			/*
			 * URL url = new URL(requestString); HttpURLConnection conn =
			 * (HttpURLConnection) url.openConnection(); // String encoding =
			 * new //
			 * sun.misc.BASE64Encoder().encode("username      assword".getBytes
			 * ()); // conn.setRequestProperty ("Authorization", "Basic " +
			 * encoding); conn.setRequestMethod("GET");
			 * 
			 * conn.connect(); InputStream in = conn.getInputStream();
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(in)); String text = reader.readLine();
			 * //System.out.println(text); conn.disconnect();
			 */
			String text = "";
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			URL url = new URL(requestString);
			InputStream is = url.openStream();
			xpp.setInput(is, "UTF-8");
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("string")) {
						text = xpp.nextText();
					}
				}
				eventType = xpp.next();
			}

			return text;
		} catch (IOException ex) {
			ex.printStackTrace();
			// System.out.println("made it here");
		}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trans_main);

		LinearLayout ll1 = (LinearLayout) findViewById(R.id.trans_main_ll01); // 입력박스
		LinearLayout ll2 = (LinearLayout) findViewById(R.id.trans_main_ll02); // 출발어
		LinearLayout ll3 = (LinearLayout) findViewById(R.id.trans_main_ll03); // 바꾸기
		LinearLayout ll4 = (LinearLayout) findViewById(R.id.trans_main_ll04); // 도착어
		ScrollView sv = (ScrollView) findViewById(R.id.trans_main_sv01); // 스크롤
																			// 뷰
		Button bt1 = (Button) findViewById(R.id.trans_main_bt01);
		mAni = (AnimationDrawable) bt1.getBackground();

		bt1.post(new Runnable() {
			public void run() {
				mAni.start();
			}
		});
		Button bt2 = (Button) findViewById(R.id.trans_main_bt02);
		Button bt3 = (Button) findViewById(R.id.trans_main_bt03);
		//bt3.setVisibility(View.INVISIBLE);
		Button bt4 = (Button) findViewById(R.id.trans_main_bt04);
		
		mIv1 = (ImageView) findViewById(R.id.trans_main_iv01);
		mIv2 = (ImageView) findViewById(R.id.trans_main_iv02);
		mTv1 = (TextView) findViewById(R.id.trans_main_tv01);
		mTv2 = (TextView) findViewById(R.id.trans_main_tv02);
		mTv3 = (TextView) findViewById(R.id.trans_main_tv03);
		mTv4 = (TextView) findViewById(R.id.trans_main_tv04);
		mTv5 = (TextView) findViewById(R.id.trans_main_tv05);
		mTv6 = (TextView) findViewById(R.id.trans_main_tv06);

		ll1.setOnClickListener(this); // 번역입력박스
		ll2.setOnClickListener(this); // 번역입력박스
		ll3.setOnClickListener(this); // 번역입력박스
		ll4.setOnClickListener(this); // 번역입력박스
		sv.setOnClickListener(this);

		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		bt4.setOnClickListener(this);

		try {
			arrayStateItemInit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void arrayStateItemInit() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		// 코드를 불러온다.
		String arrCode[] = getResources().getStringArray(R.array.state_code);
		String arrCodeName[] = getResources().getStringArray(R.array.code_name);
		String strTemp[] = getResources().getStringArray(R.array.code_img);

		Class<R.drawable> drawable = R.drawable.class;
		Field field;
		int temp=0;
		
		for (int i = 0; i < arrCode.length; i++) {
			stateItem = new StateItem();
			stateItem.stateCode = arrCode[i];
			stateItem.text = arrCodeName[i];
			field = drawable.getField(strTemp[i]);
			temp = field.getInt(null);
			stateItem.stateImgintId = temp;
			stateItem.stateImg = getResources().getDrawable(temp);
			arrayStateItem.add(stateItem);
		}
		String initStart = "korean";
		String initEnd = "english";
		fromCode = "ko";
		toCode = "en";
		// 초기 언어 셋팅
		mTv1.setText(initStart);
		mTv2.setText(initEnd);
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		mTv3.setText(clipboardManager.getText().toString());
		mTv4.setText("");
		field = drawable.getField(initStart);
		temp = field.getInt(null);
		fromidInt = temp;
		mIv1.setBackgroundDrawable(getResources()
				.getDrawable(fromidInt));
		field = drawable.getField(initEnd);
		temp = field.getInt(null);
		toidInt = temp;
		mIv2.setBackgroundDrawable(getResources().getDrawable(
				toidInt));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.trans_main_ll01:
			// 번역입력박스
			getPopupWindow();
			break;
		case R.id.trans_main_ll02:
			getStartState();
			break;
		case R.id.trans_main_ll03:
			getStateChage();
			break;
		case R.id.trans_main_ll04:
			getEndState();
			break;
		case R.id.trans_main_sv01:
			getPopupWindow();
			break;
		case R.id.trans_main_bt01: // 읽어주기.
			Log.d("kimyongyeon", "dddddddddddddd");
			String text = mTv4.getText().toString();

			try {
				getSpeak(text, toCode);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				String strToast = getResources().getString(R.string.toast_msg1);
				Toast.makeText(this, strToast, Toast.LENGTH_SHORT).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.trans_main_bt02: // 히스토리
			mDAO = new StateItemDAO(this);
			getHistoryPopup();
			break;
		case R.id.trans_main_bt03: // 설정
			break;
		case R.id.trans_main_bt04: // 메뉴
			getMenuPopup();
			break;
		}
	}

	private void getMenuPopup() {
		View v = View.inflate(this, R.layout.menu, null);
		etPopupWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		etPopupWindow.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에
		// 이벤트를 줄수있습니다.
		etPopupWindow.setBackgroundDrawable(new BitmapDrawable()); // 이부분에 이벤트가
		// 들어오게됩니다.
		etPopupWindow
				.showAtLocation(v, Gravity.CENTER | Gravity.BOTTOM, 0, 200);

		Button bt1 = (Button) v.findViewById(R.id.menu_bt01);
		Button bt2 = (Button) v.findViewById(R.id.menu_bt02);
		Button bt3 = (Button) v.findViewById(R.id.menu_bt03);
		Button bt4 = (Button) v.findViewById(R.id.menu_bt04);
		Button bt5 = (Button) v.findViewById(R.id.menu_bt05);

		bt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 클립보드 복사
				ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager
						.setText(mTv3.getText() + "/n" + mTv4.getText());
				Toast.makeText(getApplicationContext(),
						"복사되었습니다. 원하시는 곳으로 가서 붙여 넣어세요.", Toast.LENGTH_SHORT)
						.show();
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendEmail();
			}
		});
		bt3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage();
			}
		});
		bt4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendTwitter();
			}
		});
		bt5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendFacebook();
			}
		});
	}

	private void sendTwitter() {
		Toast.makeText(this, "트위터 서비스를 준비중 입니다.", Toast.LENGTH_SHORT).show();
	}

	private void sendFacebook() {
		Toast.makeText(this, "페이스북 서비스를 준비중 입니다.", Toast.LENGTH_SHORT).show();
	}

	private void sendEmail() {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_EMAIL, "");
		it.putExtra(Intent.EXTRA_TEXT, mTv3.getText().toString() + ", "
				+ mTv4.getText().toString());
		it.setType("text/plain");
		startActivity(Intent.createChooser(it, "Choose Email Client"));
	}

	private void sendMessage() {
		Uri uri = Uri.parse("smsto:");
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", mTv3.getText().toString() + ", "
				+ mTv4.getText().toString());
		startActivity(it);
	}

	private void getHistoryPopup() {
		View v = View.inflate(this, R.layout.history, null);
		etPopupWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		etPopupWindow.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에
		// 이벤트를 줄수있습니다.
		etPopupWindow.setBackgroundDrawable(new BitmapDrawable()); // 이부분에 이벤트가
		// 들어오게됩니다.
		etPopupWindow.showAtLocation(v, Gravity.CENTER | Gravity.BOTTOM, 0, 0);

		ListView lv = (ListView) v.findViewById(R.id.history_lv01);
		Button bt1 = (Button) v.findViewById(R.id.history_bt1);
		Button bt2 = (Button) v.findViewById(R.id.history_bt2);
		Button bt3 = (Button) v.findViewById(R.id.history_bt3);
		// 일괄삭제
		bt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDelPopup();
			}
		});
		// 공유
		bt2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				arrayStateItemDTO = mDAO.getDataRead();
				String sendData = "";
				for (int i = 0; i < arrayStateItemDTO.size(); i++) {
					stateItemDTO = arrayStateItemDTO.get(i);
					sendData += stateItemDTO.getFromMemo();
					sendData += "\n";
					sendData += stateItemDTO.getToMemo();
					sendData += "\n\n";
				}

				Intent it = new Intent(Intent.ACTION_SEND);
				it.putExtra(Intent.EXTRA_EMAIL, "");
				it.putExtra(Intent.EXTRA_TEXT, sendData.trim());
				it.setType("text/plain");
				startActivity(it);
			}
		});
		// 완료
		bt3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etPopupWindow.dismiss();
			}
		});

		arrayStateItemDTO = mDAO.getDataRead();
		HistoryAdapter sa = new HistoryAdapter(this, arrayStateItemDTO);
		lv.setAdapter(sa);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				StateIteamDTO data = arrayStateItemDTO.get(position);
				mTv5.setText("" + data.getFromTile());
				mTv3.setText(data.getFromMemo());
				mTv6.setText("" + data.getToTitle());
				mTv4.setText(data.getToMemo());
				Drawable d1 = getResources().getDrawable(data.getFromImg());
				Drawable d2 = getResources().getDrawable(data.getToImg());
				mTv1.setText(data.getFromTile());
				mIv1.setBackgroundDrawable(d1);
				mTv2.setText(data.getToTitle());
				mIv2.setBackgroundDrawable(d2);
				fromidInt = data.getFromImg();
				toidInt = data.getToImg();
				fromCode = data.getFromCode();
				toCode = data.getToCode();
				etPopupWindow.dismiss();
			}
		});
	}

	AlertDialog alertDialog;

	private void getDelPopup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("경고");
		builder.setMessage("정말로 모두 삭제하실꺼에요?");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// showMsg("which : "+ which);
				if (which == -1) {
					mDAO.dropT();
					etPopupWindow.dismiss();
				} else {
					alertDialog.dismiss();
				}
			}
		};

		builder.setPositiveButton("예", listener);
		builder.setNegativeButton("아니오", listener);
		alertDialog = builder.create();
		alertDialog.show();
	}

	private void getStateChage() {
		ImageView tempIv = new ImageView(this);
		TextView tempTv = new TextView(this);
		tempIv.setBackgroundDrawable(mIv1.getBackground());
		tempTv.setText(mTv1.getText());
		mIv1.setBackgroundDrawable(mIv2.getBackground());
		mTv1.setText(mTv2.getText());
		mIv2.setBackgroundDrawable(tempIv.getBackground());
		mTv2.setText(tempTv.getText());
		
		String tempCode = fromCode;
		fromCode = toCode;
		toCode = tempCode;
		
		int tempIdint = fromidInt;
		fromidInt = toidInt;
		toidInt = tempIdint;
		
		TextView tempTv2 = new TextView(this);
		TextView tempTv3 = new TextView(this);
		tempTv2.setText(mTv5.getText()); // 출발번역어
		tempTv3.setText(mTv3.getText()); // 출발언어
		mTv5.setText(mTv6.getText());
		mTv3.setText(mTv4.getText());
		mTv6.setText(tempTv2.getText());
		mTv4.setText(tempTv3.getText());
	}

	private void getStartState() {
		View v = View.inflate(this, R.layout.state, null);
		etPopupWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		etPopupWindow.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에
		// 이벤트를 줄수있습니다.
		etPopupWindow.setBackgroundDrawable(new BitmapDrawable()); // 이부분에 이벤트가
		// 들어오게됩니다.
		etPopupWindow.showAtLocation(v, Gravity.CENTER | Gravity.BOTTOM, 0, 0);

		ListView lv = (ListView) v.findViewById(R.id.state_lv01);

		StateAdapter sa = new StateAdapter(this, arrayStateItem);
		lv.setAdapter(sa);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				stateItem = arrayStateItem.get(position);
				mIv1.setBackgroundDrawable(stateItem.stateImg);
				mTv1.setText(stateItem.text);
				mTv5.setText("" + stateItem.text);
				fromCode = stateItem.stateCode;
				fromidInt = stateItem.stateImgintId;
				etPopupWindow.dismiss();
			}
		});
	}

	private void getEndState() {
		View v = View.inflate(this, R.layout.state, null);
		etPopupWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		etPopupWindow.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에
		// 이벤트를 줄수있습니다.
		etPopupWindow.setBackgroundDrawable(new BitmapDrawable()); // 이부분에 이벤트가
		// 들어오게됩니다.
		etPopupWindow.showAtLocation(v, Gravity.CENTER | Gravity.BOTTOM, 0, 0);

		ListView lv = (ListView) v.findViewById(R.id.state_lv01);

		StateAdapter sa = new StateAdapter(this, arrayStateItem);
		lv.setAdapter(sa);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				stateItem = arrayStateItem.get(position);
				mIv2.setBackgroundDrawable(stateItem.stateImg);
				mTv2.setText(stateItem.text);
				mTv6.setText("" + stateItem.text);
				toCode = stateItem.stateCode;
				toidInt = stateItem.stateImgintId;
				etPopupWindow.dismiss();
				String strTemp = mTv4.getText().toString().trim();
				if (!strTemp.equals(""))
					getTransComplete();
			}

		});
	}

	EditText mEt;
	Button mBt;

	private void getPopupWindow() {
		View v = View.inflate(this, R.layout.input, null);
		etPopupWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		etPopupWindow.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에
		// 이벤트를 줄수있습니다.
		etPopupWindow.setBackgroundDrawable(new BitmapDrawable()); // 이부분에 이벤트가
		// 들어오게됩니다.
		etPopupWindow.showAtLocation(v, Gravity.CENTER | Gravity.BOTTOM, 0, 0);

		mEt = (EditText) v.findViewById(R.id.input_et1);
		Button bt1 = (Button) v.findViewById(R.id.input_bt1);
		mBt = (Button) v.findViewById(R.id.input_bt2);
		Button bt3 = (Button) v.findViewById(R.id.input_bt3);
		
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		mEt.setText(clipboardManager.getText().toString());

		if (mEt.getText().toString().trim().equals("")) {
			//mBt.setEnabled(true);
			Drawable d = getResources().getDrawable(R.drawable.translate2);
			mBt.setBackgroundDrawable(d);
		} else {
			//mBt.setEnabled(true);
			Drawable d = getResources().getDrawable(R.drawable.translator_select);
			mBt.setBackgroundDrawable(d);
		}

		mEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (mEt.getText().toString().trim().equals("")) {
					//mBt.setEnabled(false);
					//Drawable d = getResources().getDrawable(R.drawable.translator_select);
					Drawable d = getResources().getDrawable(R.drawable.translate2);
					mBt.setBackgroundDrawable(d);
				} else {
					//mBt.setEnabled(true);
					Drawable d = getResources().getDrawable(R.drawable.translator_select);
					mBt.setBackgroundDrawable(d);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		bt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEt.setText("");
			}
		});

		// 번역
		mBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTv3.setText(mEt.getText());
				etPopupWindow.dismiss();
				getTransComplete();
			}
		});

		bt3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etPopupWindow.dismiss();
			}
		});
	}

	private void getTransComplete() {
		String strCompleteTrans = null;
		try {
			String encordingFrom = null;
			try {
				try {
					if (!mEt.getText().toString().equals("")) {
						encordingFrom = URLEncoder.encode(mEt.getText()
								.toString(), "utf-8");
					} else {
						encordingFrom = URLEncoder.encode(mTv3.getText()
								.toString(), "utf-8");
					}
				} catch (NullPointerException e) {
					encordingFrom = URLEncoder.encode(
							mTv3.getText().toString(), "utf-8");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strCompleteTrans = getBuildRequest(encordingFrom, fromCode, toCode);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mTv4.setText(strCompleteTrans);
		String startLang = mTv1.getText().toString().trim();
		String endLang = mTv2.getText().toString().trim();
		mTv5.setText("" + startLang);
		mTv6.setText("" + endLang);
		StateIteamDTO respData = new StateIteamDTO();
		// 시작언어
		respData.setFromCode(fromCode);
		respData.setFromTile(startLang);
		respData.setFromMemo(mTv3.getText().toString().trim());
		respData.setFromImg(fromidInt);
		// 도착언어
		respData.setToCode(toCode);
		respData.setToTitle(endLang);
		respData.setToMemo(strCompleteTrans);
		respData.setToImg(toidInt);
		
		if(mDAO == null)
			mDAO = new StateItemDAO(this);
		
		mDAO.setDataSave(respData); // 저장
	}

	class StateItem {
		int stateImgintId;
		Drawable stateImg;
		String text;
		String stateCode;
	}

	class StateAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		ArrayList<StateItem> arrytStateitem = new ArrayList<StateItem>();
		StateItem stateItem = new StateItem();

		StateAdapter(Context context, ArrayList<StateItem> arrytStateitem) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.arrytStateitem = arrytStateitem;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return arrytStateitem.size();
		}

		public StateItem getItem(int position) {
			// TODO Auto-generated method stub
			return arrytStateitem.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.state_row, parent,
						false);
			}
			TextView tv_str = (TextView) convertView
					.findViewById(R.id.state_row_tv01);
			ImageView iv_drawble = (ImageView) convertView
					.findViewById(R.id.state_row_iv01);

			stateItem = new StateItem();
			stateItem = arrytStateitem.get(position);
			if (stateItem != null) {
				tv_str.setText(stateItem.text);
				iv_drawble.setBackgroundDrawable(stateItem.stateImg);
			}

			return convertView;
		}
	}

	class HistoryAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		ArrayList<StateIteamDTO> dataList = new ArrayList<StateIteamDTO>();
		StateIteamDTO data = new StateIteamDTO();

		HistoryAdapter(Context context, ArrayList<StateIteamDTO> dataList) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.dataList = dataList;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return dataList.size();
		}

		public StateIteamDTO getItem(int position) {
			// TODO Auto-generated method stub
			return dataList.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.history_row, parent,
						false);
			}
			TextView tv_str1 = (TextView) convertView
					.findViewById(R.id.history_row_tv01);
			TextView tv_str2 = (TextView) convertView
					.findViewById(R.id.history_row_tv02);
			TextView tv_str3 = (TextView) convertView
					.findViewById(R.id.history_row_tv03);
			ImageView iv_drawble1 = (ImageView) convertView
					.findViewById(R.id.history_row_iv01);
			ImageView iv_drawble2 = (ImageView) convertView
					.findViewById(R.id.history_row_iv02);

			data = new StateIteamDTO();
			data = dataList.get(position);
			if (data != null) {
				String msg = data.getFromTile() + " → " + data.getToTitle();
				tv_str1.setText(msg);
				String strTemp1 = data.getFromMemo();
				String strTemp2 = data.getToMemo();
				
				if(strTemp1 != null && strTemp2 != null){
					if (strTemp1.length() < 50) {
						tv_str2.setText(strTemp1);
					} else {
						String strTemp3 = strTemp1.substring(1, 30) + "...";
						tv_str2.setText(strTemp3);
					}
					if (strTemp2.length() < 50) {
						tv_str3.setText(strTemp2);
					} else {
						String strTemp3 = strTemp2.substring(1, 30) + "...";
						tv_str3.setText(strTemp3);
					}
					Drawable fromImg = getResources()
							.getDrawable(data.getFromImg());
					Drawable toImg = getResources().getDrawable(data.getToImg());
					iv_drawble1.setBackgroundDrawable(fromImg);
					iv_drawble2.setBackgroundDrawable(toImg);	
				}
			}

			return convertView;
		}
	}
}
