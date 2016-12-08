package com.example.nfcpay;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*Activity�̳�View.OnClickListener����Activityʵ��OnClick(View view)������
 * ��OnClick(View view)��������switch-case�Բ�ͬid�����button������Ӧ�Ĵ���*/
public class HighFunction extends Activity implements OnClickListener{

	private NfcAdapter nfcAdapter;
	private PendingIntent intent;
	String stringWrite;
	Button show_log,clr_log,show_user,bt_save,bt_write;
	private boolean nfcSurport = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highfunc);
		
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        	nfcSurport = false;
        intent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
        
		show_log = (Button)findViewById(R.id.dis_all_log);
		clr_log = (Button)findViewById(R.id.clr_all_log);
		show_user = (Button)findViewById(R.id.dis_all_user);
		bt_save = (Button)findViewById(R.id.btn_save);
		bt_write = (Button)findViewById(R.id.btn_write);
		
		show_log.setOnClickListener(this);
		clr_log.setOnClickListener(this);
		show_user.setOnClickListener(this);
		bt_save.setOnClickListener(this);
		bt_write.setOnClickListener(this);
	}
	
    @Override
	protected void onResume() {
   	// TODO Auto-generated method stub
   	super.onResume();
   	if (nfcSurport)
   		nfcAdapter.enableForegroundDispatch(this, intent, null, null);
	}
    @Override
	protected void onPause() {
   	// TODO Auto-generated method stub
   	super.onPause();
   	if (nfcSurport)
   		nfcAdapter.disableForegroundDispatch(this);
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
		if(mi.getItemId() == R.id.tran_rec)
		{
			Intent intent = new Intent(this, TransLogging.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.dis_all_log:
			showAllLog();
			break;
		case R.id.clr_all_log:
			clrAllLog();
			break;
		case R.id.dis_all_user:
			showAllUser();
			break;
		case R.id.btn_save:
			saveMoney();
			break;
		case R.id.btn_write:
			writeTagValue();
			break;
		default:
			break;
		}
	}
	
	public void showAllLog()
	{
		//Toast.makeText(HighFunction.this,"��ʾ���м�¼", Toast.LENGTH_SHORT).show();
		// ����һ��List���ϣ�List���ϵ�Ԫ����Map
		List<Map<String, Object>> listItems =
				new ArrayList<Map<String, Object>>();
		
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from translog";
		Cursor cursor = sdb.rawQuery(sql, new String[] {});
		int logCount = 0;
		while (cursor.moveToNext())
		{
			logCount++;
			String text = ""+logCount+". "+cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" ��"+cursor.getInt(3);
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("logText", text);
			listItems.add(listItem);
		}
		
		// ����һ��SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.logitem,
				new String[] {"logText"},
				new int[] {R.id.logText});
		ListView list = (ListView) findViewById(R.id.listViewAll);
		// ΪListView����Adapter
		list.setAdapter(simpleAdapter);
		sdb.close();
	}
	public void clrAllLog()
	{
		//Toast.makeText(HighFunction.this,"������м�¼", Toast.LENGTH_SHORT).show();
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "delete from translog";
		Object obj[]={};
		sdb.execSQL(sql,obj);
		sdb.close();
		showAllLog();
	}
	public void showAllUser()
	{
		//Toast.makeText(HighFunction.this,"��ʾ�����û�", Toast.LENGTH_SHORT).show();
		// ����һ��List���ϣ�List���ϵ�Ԫ����Map
		List<Map<String, Object>> listItems =
				new ArrayList<Map<String, Object>>();
		
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from user";
		Cursor cursor = sdb.rawQuery(sql, new String[] {});
		int logCount = 0;
		while (cursor.moveToNext())
		{
			//���һ��������ʾ��������
			if (logCount == 0)
			{
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("userCount","  ");
				listItem.put("nameText", "�ʺ�");
				listItem.put("pwText", "����");
				listItem.put("moneyText", "���");
				listItems.add(listItem);		
			}
				Map<String, Object> listItem = new HashMap<String, Object>();
			logCount++;
			listItem.put("userCount", Integer.toString(logCount)+".");	//�õ�intת��Ϊstring����
			listItem.put("nameText", cursor.getString(0));
			listItem.put("pwText", cursor.getString(1));
			listItem.put("moneyText", cursor.getInt(2));
			listItems.add(listItem);
		}
		
		// ����һ��SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.useritem,
				new String[] {"userCount", "nameText", "pwText", "moneyText"},
				new int[] {R.id.userCount, R.id.nameText, R.id.pwText, R.id.moneyText});
		ListView list = (ListView) findViewById(R.id.listViewAll);	//����log��listView
		// ΪListView����Adapter
		list.setAdapter(simpleAdapter);		
	}
	public void saveMoney()
	{
		EditText et_name,et_money;
		Button bt_save;
		String save_account,save_value;
		int save_money,update_money;
		
		et_name = (EditText)findViewById(R.id.save_account);
		et_money = (EditText)findViewById(R.id.save_value);
		save_account = et_name.getText().toString();
		save_value = et_money.getText().toString();
		
		if(save_account.equals("")||save_value.equals(""))
		{
			Toast.makeText(HighFunction.this,"�������˻��ͳ�ֵ���", Toast.LENGTH_SHORT).show();
			return;
		}
		save_money = Integer.parseInt(save_value);
		
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		
		String sql = "select * from user where username=?";
		Cursor cursor = sdb.rawQuery(sql, new String[] {save_account});
		if(cursor.moveToFirst()==true)
		{
			//��ԭ����������
			update_money = save_money + cursor.getInt(2);
			String sql1 = "update user set account=? where username=?";
			//���������
			Object obj1[]={update_money,save_account};
			sdb.execSQL(sql1,obj1);
			//���½��׼�¼
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String curTime = sDateFormat.format(new Date());
	        String sql2="insert into translog(date,username,action,value) values(?,?,?,?)";
	        Object obj2[]={curTime,save_account,"��ֵ",save_money};
	        sdb.execSQL(sql2, obj2);
			sdb.close();
			Toast.makeText(HighFunction.this,"��ֵ�ɹ���������ʾ�����û��鿴", Toast.LENGTH_SHORT).show();
			cursor.close();
		}
		else
		{
			Toast.makeText(HighFunction.this,"�˻�������", Toast.LENGTH_SHORT).show();
		}
		et_name.setText("");
		et_money.setText("");
	}
	//��д����ʺźͽ����Ϣ���浽ȫ�ֱ���stringWrite��
	public void writeTagValue()
	{
		EditText writeName = (EditText)findViewById(R.id.write_account);
		EditText writeValue = (EditText)findViewById(R.id.write_value);
		if (writeName.getText().toString().equals("") || writeValue.getText().toString().equals(""))
		{
			Toast.makeText(this, "�������ʺźͽ��", Toast.LENGTH_SHORT).show();
			return;
		}
		writeEncode(writeName.getText().toString(), Integer.parseInt(writeValue.getText().toString()));
		Toast.makeText(this, "�뽫�ֻ�NFC��Ӧ������Tag", Toast.LENGTH_SHORT).show();
	}
	
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	super.onNewIntent(intent);
    	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); 
    	if (!nfcSurport)
    		return;
    	writeTag(tag);
    }
    
    public void writeTag(Tag tag)
    {
    	String write;
		EditText writeName = (EditText)findViewById(R.id.write_account);
		EditText writeValue = (EditText)findViewById(R.id.write_value);
		if (writeName.getText().toString().equals("") || writeValue.getText().toString().equals(""))
		{
			Toast.makeText(this, "�������ʺźͽ��", Toast.LENGTH_SHORT).show();
			return;
		}
		write = writeEncode(writeName.getText().toString(), Integer.parseInt(writeValue.getText().toString()));
		writeName.setText("");
		writeValue.setText("");

    	if (write.equals(""))
    	{
    		Toast.makeText(this, "�������ʺźͽ��", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	//ȷ����ǩ֧�ֵļ���
    	String[] techlist=tag.getTechList();
    	if(Arrays.toString(techlist).contains("MifareUltralight"))
    	{
    		MifareUltralight mifareUltralight=MifareUltralight.get(tag);
        	try
        	{
    			mifareUltralight.connect();
    			Toast.makeText(this, "��ʼд�����벻Ҫ�ƶ�", Toast.LENGTH_SHORT).show();
    			mifareUltralight.writePage(4, write.substring(0,4).getBytes(Charset.forName("US-ASCII")));
    			mifareUltralight.writePage(5, write.substring(4,8).getBytes(Charset.forName("US-ASCII")));
    			mifareUltralight.writePage(6, write.substring(8,12).getBytes(Charset.forName("US-ASCII")));
    			mifareUltralight.writePage(7, write.substring(12,16).getBytes(Charset.forName("US-ASCII")));
    			Toast.makeText(this, "д�����", Toast.LENGTH_SHORT).show();
    		}
        	catch (IOException e)
        	{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	finally
        	{
    			try
    			{
    				mifareUltralight.close();
    			}
    			catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
    	else
    	{
    		Toast.makeText(this, "����MifareUltralightle����", Toast.LENGTH_SHORT).show();
    	}
    	stringWrite = "";
    }
    
    //�õ�Ҫд����ַ���
    public String writeEncode(String name, int number)
	{
    	String outString;
		final String checkNumber = "[0-9]+";	//����\\d+��������ֵ�������ʽ����ʾֻ��������
		final String checkName = "[0-9a-zA-Z]+";//����û�����������ʽ����ʾֻ�������ֺ���ĸ

		if (name.equals("") || number == 0)
		{
			System.out.println("write data format error!");
			return null;
		}

		//name�Ӵ�ֻ�������ֺ���ĸ��number�ִ�ֻ��������
		if (!name.matches(checkName) || !(""+number).matches(checkNumber))
		{
			System.out.println("write data format error!");
			return null;
		}

		System.out.println(name);
		System.out.println(""+number);
		outString = setName(name);
		outString += setNumber(number);
		return outString;
	}

	public String setName(String name)
	{
		StringBuffer nameInput = new StringBuffer("00000000");
		int nameLen = name.length();
		//�������������String�ĳ��ȴ���endIndex-startIndex��������һ���µ��ֽ��ں���
		nameInput.replace(0,nameLen+1,nameLen+name);	//�滻�ӵ�1(index 0)����nameLen+1(index nameLen)���ֽ�
		return nameInput.toString();
	}
	public String setNumber(int number)
	{
		StringBuffer numberInput = new StringBuffer("00000000");
		String numberStr = ""+number;
		int numberLen = numberStr.length();
		numberInput.replace(0,numberLen+1,numberLen+numberStr);//�滻�ӵ�1(index 0)����numberLen+1(index numberLen)���ֽ�
		return numberInput.toString();
	}
}
