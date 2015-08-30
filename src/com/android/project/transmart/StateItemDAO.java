package com.android.project.transmart;

import java.util.ArrayList;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class StateItemDAO {
	
	private static final String DBNAME = "transmart.db";
	
	// 테이블은 대문자가 들어갈 수 없다.
	private static final String TB_HISTORY = "history";
	private static SQLiteDatabase db;
	private static Activity activity;
	private static String TOAST_INSERT_MSG = "데이터 입력 성공";
	private static String TOAST_UPDATE_MSG = "데이터 수정 성공";
	private static String TOAST_DEL_MSG = "데이터 삭제 성공";
	private static String TOAST_READ_MSG = "데이터 읽기 성공";
	private static String TOAST_READ_FAIL_MSG = "데이터 읽기 실패";
	private static String TOAST_CREATE_TABLE_MSG = "테이블 생성 성공";
	/*private static String TOAST_INSERT_MSG = "";
	private static String TOAST_UPDATE_MSG = "";
	private static String TOAST_DEL_MSG = "";
	private static String TOAST_READ_MSG = "";
	private static String TOAST_READ_FAIL_MSG = "";
	private static String TOAST_CREATE_TABLE_MSG = "";*/

	StateItemDAO(Activity activity) {
		this.activity = activity;
		dbConnection(); // DB연결
		//dropT();
		//dbDel();
		createT();
	}

	// DB Connection
	private void dbConnection() {
		db = activity.openOrCreateDatabase(DBNAME, activity.MODE_PRIVATE, null);
	}

	// DB Close
	public void dbClose() {
		db.close();
	}

	// DB Delete
	public void dbDel() {
		activity.deleteDatabase(DBNAME);
	}

	// Table 삭제(일괄삭제)
	public static void dropT() {
		try {
			String sql1 = "drop table " + TB_HISTORY;
			db.execSQL(sql1);
			//Toast.makeText(activity, TOAST_DEL_MSG, Toast.LENGTH_SHORT).show();
		} catch (android.database.SQLException se) {}
	}

	private String sql;
	private void createT() {

		// 히스토리 테이블
		sql = "create table " + TB_HISTORY
				+ "(_ID integer primary key autoincrement, " 
				+	"fromtile text,"
				+	"frommemo text,"
				+	"fromcode text,"
				+	"fromimg integer,"
				+	"totitle text,"
				+	"tomemo text,"
				+   "tocode text,"
				+	"toimg integer);";
		try {
			db.execSQL(sql);
			//Toast.makeText(activity, TOAST_CREATE_TABLE_MSG, Toast.LENGTH_SHORT).show();
		} catch (android.database.SQLException se) {}
	}

	// SQLlite 데이터 삭제(한행삭제)
	public static void setDataDelete(String id) {
		String sql = "delete from " + TB_HISTORY + " where _ID=?";

		String _idStr = id;
		if (_idStr != null)
			_idStr = _idStr.trim();
		int _id = Integer.parseInt(_idStr);

		try {
			Object bindArgs[] = { _id };
			db.execSQL(sql, bindArgs);
			//Toast.makeText(activity, TOAST_DEL_MSG, Toast.LENGTH_SHORT).show();
		} catch (android.database.SQLException se) {}
	}

	// SQLlite 데이터 저장
	// name:이름, contents:컨텐츠명, juso:URL주소
	private static Object bind[];
	private static String m_insertSQL;
	public static void setDataSave(Object respData) {
		String passColums = "(fromtile, frommemo, fromcode, fromimg, " +
				"totitle, tomemo, tocode, toimg) values(?,?,?,?,?,?,?,?);";

		String sql = "insert into " + TB_HISTORY + passColums;
		StateIteamDTO data = new StateIteamDTO();
		data = (StateIteamDTO) respData;
		Object bindArgs[] = { data.getFromTile(), data.getFromMemo(), data.getFromCode(), data.getFromImg(),
				data.getToTitle(), data.getToMemo(), data.getToCode(), data.getToImg()};
		m_insertSQL = sql;
		
		bind = bindArgs;

		try {
			db.execSQL(m_insertSQL, bind);
			//Toast.makeText(activity, TOAST_INSERT_MSG, Toast.LENGTH_SHORT).show();
		} catch (android.database.SQLException se) {
			//Toast.makeText(activity, se.getMessage(), Toast.LENGTH_SHORT).show();
			Log.d("kimyongyeon", se.getMessage());
		}

	}
	
	// SQLlite 데이터 읽기 == > List 객체에 저장 ==> ListView에 뿌림.
	// null : 리드할 내용이 없슴.
	private static String m_sqlRead;
	public static ArrayList<StateIteamDTO> getDataRead() {
		// password table read
		StateIteamDTO data = new StateIteamDTO();
		ArrayList<StateIteamDTO> listData = new ArrayList<StateIteamDTO>();
		m_sqlRead = "select * from " + TB_HISTORY + " ORDER BY _ID DESC";

		Cursor c = db.rawQuery(m_sqlRead, null);
		try{
			if (c.moveToFirst()) {
				do {
					// 여기에 DB에서 읽어본 데이터를 mylist에 저장해야 한다.
					data = new StateIteamDTO();
					data.setIdx(c.getInt(0));
					data.setFromTile(c.getString(1));
					data.setFromMemo(c.getString(2));
					data.setFromCode(c.getString(3));
					data.setFromImg(c.getInt(4));
					data.setToTitle(c.getString(5));
					data.setToMemo(c.getString(6));
					data.setToCode(c.getString(7));
					data.setToImg(c.getInt(8));
					listData.add(data);
				} while (c.moveToNext());
				
				Toast.makeText(activity, TOAST_READ_MSG, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(activity, TOAST_READ_FAIL_MSG, Toast.LENGTH_SHORT).show();
			}
			c.close();	
		}catch(Exception e){
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return listData;
	}
}
