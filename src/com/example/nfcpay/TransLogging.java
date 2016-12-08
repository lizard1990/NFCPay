package com.example.nfcpay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TransLogging extends Activity {

	TextView translogtxt;
	SimpleDateFormat sDateFormat;
	String userName;
	int payValue;
	Button clrBt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

        SharedPreferences login_info = getSharedPreferences("login_info", 0);
		userName = login_info.getString("username", "");
		
		showLog(userName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// ѡ��˵��Ĳ˵��������Ļص�����
	public boolean onOptionsItemSelected(MenuItem mi)
	{
		if(mi.isCheckable())
		{
			mi.setChecked(true);
		}
		// �жϵ��������ĸ��˵����������Ե�������Ӧ
		if(mi.getItemId() == R.id.high_funct)
		{
				Intent intent = new Intent(this, HighFunction.class);
				// ��Ӷ����Flag����Activityջ�д���FirstActivity֮�ϵ�Activity����
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// ����intent��Ӧ��Activity
				startActivity(intent);
				finish();
		}
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) 
        {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
        }
        return true;
     }
	
	public void showLog(String userName)
	{
		// ����һ��List���ϣ�List���ϵ�Ԫ����Map
		List<Map<String, Object>> listItems =
				new ArrayList<Map<String, Object>>();
		
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from translog where username=?";
		Cursor cursor = sdb.rawQuery(sql, new String[] {userName});
		int logCount = 0;
		while (cursor.moveToNext())
		{
			logCount++;
			String text = ""+logCount+". "+cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" ��"+cursor.getInt(3);
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("logText", text);
			listItems.add(listItem);
		}
		
		/*Map<String, Object> listItem1 = new HashMap<String, Object>();
		Map<String, Object> listItem2 = new HashMap<String, Object>();
		String text1 = "test1";
		listItem1.put("logText", text1);
		listItems.add(listItem1);
		String text2 = "test2";
		listItem2.put("logText", text2);
		listItems.add(listItem2);*/
		
		
		// ����һ��SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.logitem,
				new String[] {"logText"},
				new int[] {R.id.logText});
		ListView list = (ListView) findViewById(R.id.listView);
		// ΪListView����Adapter
		list.setAdapter(simpleAdapter);
	}
	
	public void BtnclrUserLog(View view)
	{
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "delete from translog where username=?";
		Object obj[]={userName};
		sdb.execSQL(sql,obj);
		sdb.close();
		showLog(userName);
	}
}
