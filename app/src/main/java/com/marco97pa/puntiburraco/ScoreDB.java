package com.marco97pa.puntiburraco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marco F on 02/08/2017.
 */

public class ScoreDB {
    static final String KEY_ID = "_id";
    static final String KEY_PLAYER1 = "player1";
    static final String KEY_PLAYER2 = "player2";
    static final String KEY_PLAYER3 = "player3";
    static final String KEY_POINT1 = "point1";
    static final String KEY_POINT2 = "point2";
    static final String KEY_POINT3 = "point3";
    static final String KEY_DATE = "date";
    static final String TAG = "ScoresDB";
    static final String DATABASE_NAME = "ScoreDB";
    static final String DATABASE_TABLE = "results";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATION = "create table results (_id integer primary key autoincrement, "
            + "player1 text not null, player2 text not null, player3 text,"
            + "point1 integer, point2 integer, point3 integer,"
            + "date text not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public ScoreDB(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            // invoco il costruttore della classe base
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATION);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    /*
    Apro la connessione al DB
    */
    public ScoreDB open() throws SQLException
    {
        // ottengo accesso al DB anche in scrittura
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /*
    Chiudo la connessione al DB
    */
    public void close()
    {
        // chiudo la connessione al DB
        DBHelper.close();
    }

    /*
    Estraggo elenco di tutti i clienti
    */
    public Cursor getAllScores()
    {
        // applico il metodo query senza applicare nessuna clausola WHERE
        return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_PLAYER1, KEY_PLAYER2, KEY_PLAYER3, KEY_POINT1, KEY_POINT2, KEY_POINT3, KEY_DATE}, null, null, null, null, KEY_ID + " DESC");
    }

    /*
    Inserimento di un nuovo cliente nella tabella
    */
    public long insertScore(String player1, String player2, String player3, int point1, int point2, int point3, String date)
    {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PLAYER1, player1);
        initialValues.put(KEY_PLAYER2, player2);
        initialValues.put(KEY_PLAYER3, player3);
        initialValues.put(KEY_POINT1, point1);
        initialValues.put(KEY_POINT2, point2);
        initialValues.put(KEY_POINT3, point3);
        initialValues.put(KEY_DATE, date);
        // applico il metodo insert
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertScore(String player1, String player2, int point1, int point2, String date)
    {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PLAYER1, player1);
        initialValues.put(KEY_PLAYER2, player2);
        initialValues.put(KEY_PLAYER3, "");
        initialValues.put(KEY_POINT1, point1);
        initialValues.put(KEY_POINT2, point2);
        initialValues.put(KEY_POINT3, -1);
        initialValues.put(KEY_DATE, date);
        // applico il metodo insert
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    /*
    Cancellazione di un risultato nella tabella
    */
    public boolean deleteScore(long scoreId)
    {
        // applico il metodo delete
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + scoreId, null) > 0;
    }

}
