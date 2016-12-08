package com.example.nfcpay;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	ActionBar actionBar;
	String userName;
	String passWord;
	TextView edit_name, edit_account;
	private Button btn_pay;
	private int payValue = 0;
	String payTo;
	private int account;
	private NfcAdapter nfcAdapter;
	private PendingIntent intent;
	boolean nfcSurport = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ImageView imageNFC = (ImageView) findViewById(R.id.nfc);
        imageNFC.setImageResource(R.drawable.nfc);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		//actionBar.setDisplayHomeAsUpEnabled(true);
        SharedPreferences login_info = getSharedPreferences("login_info", 0);
        userName = login_info.getString("username", "");
        passWord = login_info.getString("password", "");
        edit_name= (TextView)findViewById(R.id.user_name);
        edit_account = (TextView)findViewById(R.id.user_account);
        userShow(userName);
        btn_pay = (Button)findViewById(R.id.btn_pay);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
        	Toast.makeText(this, "�豸��֧��NFC����ʹ��ģ�⹦��", Toast.LENGTH_SHORT);
        	nfcSurport = false;
        }
        intent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflator = new MenuInflater(this);
		// ״̬R.menu.menu_main��Ӧ�Ĳ˵�������ӵ�menu��
		inflator.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	// ѡ��˵��Ĳ˵��������Ļص�����
	public boolean onOptionsItemSelected(MenuItem mi)
	{
		if(mi.isCheckable())
		{
			mi.setChecked(true);
		}
		// �жϵ��������ĸ��˵����������Ե�������Ӧ
		switch (mi.getItemId())
		{
			case R.id.tran_rec:
				Intent intent1 = new Intent(this, TransLogging.class);
				//intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				finish();
				break;
			case R.id.high_funct:
				Intent intent2 = new Intent(this, HighFunction.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
				finish();
				break;
		}
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0)
        {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("ȷ���˳�");
				//.setMessage("�Ի�������");
				// ΪAlertDialog.Builder��ӡ�ȷ������ť
				setPositiveButton(builder);
				// ΪAlertDialog.Builder��ӡ�ȡ������ť
				setNegativeButton(builder)
				.create()
				.show();
        }
        return true;
     }
	
	private AlertDialog.Builder setPositiveButton(
			AlertDialog.Builder builder)
	{
		// ����setPositiveButton������ӡ�ȷ������ť
		return builder.setPositiveButton("ȷ��", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		});
	}
	private AlertDialog.Builder setNegativeButton(
			AlertDialog.Builder builder)
	{
		// ����setNegativeButton������ӡ�ȡ������ť
		return builder.setNegativeButton("ȡ��", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//do nothing
			}
		});
	}
	
	public void userShow(String userName)
	{
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from user where username=?";
		Cursor cursor = sdb.rawQuery(sql, new String[] {userName});
		if(cursor.moveToFirst()==true){
			edit_name.setText(userName);
			//������user�ĵ�2��
			account = cursor.getInt(2);
			edit_account.setText(""+account);
			cursor.close();
			sdb.close();
		}
	}
	
	//�˴�����NFC�¼�
	public void BtnPayOnClick(View view)
	{
		EditText edit1 = (EditText)findViewById(R.id.name_payto);
		EditText edit2 = (EditText)findViewById(R.id.value_payto);
		payTo = edit1.getText().toString();
		String valueString = edit2.getText().toString();
		if(payTo.equals("")||valueString.equals(""))
		{
			Toast.makeText(MainActivity.this,"�������˻��͸�����", Toast.LENGTH_SHORT).show();
			return;
		}
		payValue = Integer.parseInt(valueString);

		AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
				.setTitle("ȷ����"+payTo+"֧��"+payValue);
				//.setMessage("�Ի�������");
				// ΪAlertDialog.Builder��ӡ�ȷ������ť
				setPositiveButton1(builder1);
				// ΪAlertDialog.Builder��ӡ�ȡ������ť
				setNegativeButton1(builder1)
				.create()
				.show();
	}
	private AlertDialog.Builder setPositiveButton1(
			AlertDialog.Builder builder1)
	{
		// ����setPositiveButton������ӡ�ȷ������ť
		return builder1.setPositiveButton("ȷ��", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				userPay();
			}
		});
	}
	private AlertDialog.Builder setNegativeButton1(
			AlertDialog.Builder builder1)
	{
		// ����setNegativeButton������ӡ�ȡ������ť
		return builder1.setNegativeButton("ȡ��", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//do nothing
			}
		});
	}
	
	//֧��������ͨ��ȫ�ֱ���payTo��payValue��ȡ֧�������֧�����
	public void userPay()
	{	
		if(account < payValue)
		{
			Toast.makeText(MainActivity.this,"�������ֵ", Toast.LENGTH_SHORT).show();
			return;
		}
		account-=payValue;
		
		SQLiteOpenHelper helper = new SqliteOpenHelper(this);
		SQLiteDatabase sdb = helper.getReadableDatabase();
		//�������ݿ��и��û������
		String sql1 = "update user set account=? where username=?";
		Object obj1[]={account,userName};
		sdb.execSQL(sql1,obj1);
		//���½��׼�¼
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String curTime = sDateFormat.format(new Date());
         String sql2="insert into translog(date,username,action,value) values(?,?,?,?)";
        Object obj2[]={curTime,userName,"֧��",payValue};
        sdb.execSQL(sql2, obj2);
		sdb.close();
		Toast.makeText(MainActivity.this,"֧���ɹ�", Toast.LENGTH_SHORT).show();
		edit_account.setText(""+account);
		//���ȫ�ֱ���֧�������֧�����
		payTo = "";
		payValue = 0;
	}

	//NFC�Ļص�����
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	super.onNewIntent(intent);
    	Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

    	if (!nfcSurport)
    		return;
    	//���readTag����false��������֧�����ù��̽���
    	if (!readTag(tag))
    		return;
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
				.setTitle("ȷ����"+payTo+"֧��"+payValue);
				//.setMessage("�Ի�������");
				// ΪAlertDialog.Builder��ӡ�ȷ������ť
				setPositiveButton1(builder1);
				// ΪAlertDialog.Builder��ӡ�ȡ������ť
				setNegativeButton1(builder1)
				.create()
				.show();
    	return;
    }
    
    public boolean readTag(Tag tag)
    {
    	String[] techlist=tag.getTechList();
    	String inPut;
    	if(Arrays.toString(techlist).contains("MifareUltralight"))
    	{
	    	MifareUltralight mifareUltralight=MifareUltralight.get(tag);
	    	try
	    	{
	    		mifareUltralight.connect();	
	    		byte[] data=mifareUltralight.readPages(4);
	    		//return new String(data,Charset.forName("US-ASCII"));
	    		//��ȡ��NFC���ݣ����浽�ֲ�����inPut��
	    		inPut = new String(data,Charset.forName("US-ASCII"));
	    		ReadDecode(inPut);
	    		return true;
			}
	    	catch (Exception e)
	    	{
				// TODO: handle exception
	    		return false;
			}
	    	finally
	    	{
				try
				{
					mifareUltralight.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
    	}
    	else
    	{
    		Toast.makeText(this, "����MifareUltralightle����", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    }
    
    //�����NFC��ǩ��ȡ�����ݲ�������ȫ�ֱ���payTo��payValue��
    public void ReadDecode(String inPut)
	{
		int nameLen,numLen;
		final String checkNumber = "[0-9]+";	//����\\d+��������ֵ�������ʽ����ʾֻ��������
		final String checkName = "[0-9a-zA-Z]+";//����û�����������ʽ����ʾֻ�������ֺ���ĸ

		//��鳤���Ƿ�Ϊ16Byte
		if (inPut.length() != 16)
		{
			System.out.println("input data format error!");
			return;
		}
		//����1�ֽں͵�9�ֽ��Ƿ�Ϊ���֣�����������[1,6]
		if (Character.isDigit(inPut.charAt(0)) && Character.isDigit(inPut.charAt(8)))
		{
			nameLen = Integer.parseInt(inPut.substring(0,1));
			numLen = Integer.parseInt(inPut.substring(8,9));
			System.out.println(nameLen);
			if (nameLen < 1 || nameLen > 6 || numLen < 1 || numLen > 6)
			{
				System.out.println("input data format error!");
				return;
			}
		}
		else
		{
			System.out.println("input data format error!");
			return;
		}
		//name�Ӵ�ֻ�������ֺ���ĸ��number�ִ�ֻ��������
		if (!inPut.substring(1,nameLen+1).matches(checkName) || !inPut.substring(9,numLen+9).matches(checkNumber))
		{
			System.out.println("input data format error!");
			return;
		}

		payTo = getName(inPut);
		payValue = getNumber(inPut);
	}

	public String getName(String inPut)
	{
		int nameLen = Integer.parseInt(inPut.substring(0,1));	//ȡ��1���ַ�
		return inPut.substring(1,nameLen+1);	//ȡ��1���ַ���������nameLen���ַ�
	}
	public int getNumber(String inPut)
	{
		int numberLen = Integer.parseInt(inPut.substring(8,9));	//ȡ��9���ַ�
		return Integer.parseInt(inPut.substring(9,numberLen+9));//ȡ��9���ַ���������numberLen���ַ�
	}
}