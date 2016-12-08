package com.example.nfcpay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteOpenHelper extends SQLiteOpenHelper {

	//���ݿ�����
    private static final String DBNAME="nfcpay.db";
	//����
    private static final String USERINFO="user";
    private static final String TRANSLOG="translog";

    private static final int TESTVERSION=1;
                
    public SqliteOpenHelper(Context context) {
        super(context, DBNAME, null, TESTVERSION);
        // TODO Auto-generated constructor stub
    }

	//��ʼ����������
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
    	String sql1="create table"+" "+USERINFO+"(username varchar(20),password varchar(20),account integer)";
    	String sql2="create table"+" "+TRANSLOG+"(date datetime,username varchar(20),action varchar(20),value integer)";
    	db.execSQL(sql1);
    	db.execSQL(sql2);
    }
	//ʧ�ܺ�ɾ�������´���
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
           if(newVersion>oldVersion)
           {
               String sql1="drop table if exists"+USERINFO;
               String sql2="drop table if exists"+TRANSLOG;
               db.execSQL(sql1);
               db.execSQL(sql2);
               this.onCreate(db);
           }
    }
}