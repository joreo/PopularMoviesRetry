package com.udanano.popularmoviesretry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseOps extends SQLiteOpenHelper {
    public static final int database_version = 6;
    public String CREATE_QUERY = "CREATE TABLE " +
            TableData.TableInfo.TABLE_NAME+ "(" +
            TableData.TableInfo.COLUMN_NAME_POSTER+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_TITLE+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_AVERAGE+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_OVERVIEW+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_RELEASE_DATE+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_POPULARITY+" TEXT,"+
            TableData.TableInfo.COLUMN_NAME_ID+" TEXT);";


    //default constructor
    public DatabaseOps(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, database_version);
        Log.d("Database Ops", "Database Created, or Accessed. Maybe.");
        //context.deleteDatabase(TableData.TableInfo.DATABASE_NAME);
        //somehow i had the wrong columns in the old db and this was the
        //easiest way i saw to dump it
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        Log.d("Database Ops", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

    }

    public void addEntry(DatabaseOps dop, String poster, String title, String average, String overview, String release_date, String popularity, String id) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.COLUMN_NAME_POSTER, poster);
        cv.put(TableData.TableInfo.COLUMN_NAME_TITLE, title);
        cv.put(TableData.TableInfo.COLUMN_NAME_AVERAGE, average);
        cv.put(TableData.TableInfo.COLUMN_NAME_OVERVIEW, overview);
        cv.put(TableData.TableInfo.COLUMN_NAME_RELEASE_DATE, release_date);
        cv.put(TableData.TableInfo.COLUMN_NAME_POPULARITY, popularity);
        cv.put(TableData.TableInfo.COLUMN_NAME_ID, id);

        long k = SQ.insert(TableData.TableInfo.TABLE_NAME, null, cv);
        Log.d("Database Ops", "One Row Inserted");
    }

    public Cursor getEntry(DatabaseOps dop){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {
                TableData.TableInfo.COLUMN_NAME_POSTER,
                TableData.TableInfo.COLUMN_NAME_TITLE,
                TableData.TableInfo.COLUMN_NAME_AVERAGE,
                TableData.TableInfo.COLUMN_NAME_OVERVIEW,
                TableData.TableInfo.COLUMN_NAME_RELEASE_DATE,
                TableData.TableInfo.COLUMN_NAME_POPULARITY,
                TableData.TableInfo.COLUMN_NAME_ID};

        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME, columns, null, null, null, null, null);
        return CR;
    }

    public Cursor findEntry(DatabaseOps dop, String id){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String selection = TableData.TableInfo.COLUMN_NAME_ID + " LIKE ?";
        String columns[] = {TableData.TableInfo.COLUMN_NAME_ID};
        String args[] = {id};
        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME, columns, selection, args, null, null, null);
        return CR;
    }

    public void deleteEntry(DatabaseOps dop, String id) {
        String selection = TableData.TableInfo.COLUMN_NAME_ID + " LIKE ?"; // AND
        String args[] = {id};
        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(TableData.TableInfo.TABLE_NAME, selection, args);

    }
}
