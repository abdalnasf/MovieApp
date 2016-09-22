package com.example.abdalnassef.movieapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
public class FavoriteAdapter {
    FavDbHelper favDbHelper;
    Context context;

    public FavoriteAdapter(Context context) {
        favDbHelper = new FavDbHelper(context);
        this.context=context;
    }


    public long AddFav(String poster_id, String POSTER, String TITEL, String DATE, String VOTE, String OVERVIEW){
        SQLiteDatabase sqLiteDatabase = favDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavDbHelper.POSTER, POSTER);
        contentValues.put(FavDbHelper.TITEL, TITEL);
        contentValues.put(FavDbHelper.DATE, DATE);
        contentValues.put(FavDbHelper.VOTE, VOTE);
        contentValues.put(FavDbHelper.OVERVIEW, OVERVIEW);
        contentValues.put(FavDbHelper.POSTER_ID, poster_id);

        long id = sqLiteDatabase.insert(FavDbHelper.TABLE_NAME, null, contentValues);

        sqLiteDatabase.close();
        return id;
    }
    public void RemoveFav(String id) {
        SQLiteDatabase sqLiteDatabase = favDbHelper.getWritableDatabase();

        sqLiteDatabase.delete(favDbHelper.TABLE_NAME,favDbHelper.POSTER_ID+"=?",new String[]{id});

        sqLiteDatabase.close();
    }
    public Movie[] AllFav(){
        int i =0;
        SQLiteDatabase db2= favDbHelper.getWritableDatabase();
        String [] columns2={FavDbHelper.ID, FavDbHelper. POSTER, FavDbHelper.TITEL, FavDbHelper.DATE, FavDbHelper.VOTE, FavDbHelper.OVERVIEW};
        Cursor cursor=db2.query( FavDbHelper.TABLE_NAME, columns2, null, null, null,null,null);
        Movie[] movie = new Movie[cursor.getCount()];
        while (cursor.moveToNext()){
            String POSTER=cursor.getString(1);
            String TITEL=cursor.getString(2);
            String DATE=cursor.getString(3);
            String VOTE=cursor.getString(4);
            String OVERVIE=cursor.getString(5);
            String poster_id="dd";
            movie[i] = new Movie();
            movie[i].setPoster_url(POSTER);
            movie[i].setDate(DATE);
            movie[i].setVote(VOTE);
            movie[i].setTitel(TITEL);
            movie[i].setOverview(OVERVIE);
            movie[i].setId(poster_id);
            i++;
        }
        return movie;
    }

    public String getData(String Poster_Id){
        SQLiteDatabase db=favDbHelper.getWritableDatabase();
        String [] columns={FavDbHelper.ID, FavDbHelper. POSTER,FavDbHelper.TITEL,FavDbHelper.DATE,FavDbHelper.VOTE,FavDbHelper.OVERVIEW};
        Cursor cursor=db.query( FavDbHelper.TABLE_NAME, columns, FavDbHelper.POSTER_ID+"='"+Poster_Id+"'", null, null, null,null,null);
        String data=null;
        while (cursor.moveToNext()){
            int index1=cursor.getColumnIndex(FavDbHelper.POSTER);
            data=cursor.getString(index1);
        }

        return data;
    }

    static class FavDbHelper extends SQLiteOpenHelper {
        private final static String DATABASE_NAME = "favorites";
        private final static int DATABASE_VERSION = 1;
        private final static String TABLE_NAME = "movies";
        private final static String ID = "_id";
        private final static String  POSTER = "poster";
        private final static String TITEL = "titel";
        private final static String DATE = "date";
        private final static String VOTE = "vote";
        private final static String OVERVIEW = "overview";
        private final static String POSTER_ID = "poster_id";
        private final static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"+POSTER+" VARCHAR(255) ,"+TITEL+" VARCHAR(255) ,"+DATE+" VARCHAR(255) ,"+VOTE+" VARCHAR(255) ,"+OVERVIEW+" VARCHAR(255) ,"+POSTER_ID+" INTEGER);";
        private final static String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public FavDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TABLE);
            }catch (SQLException e){
                Toast.makeText(context, "DB Error \n"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            try {
                sqLiteDatabase.execSQL(DROP_TABLE);
                onCreate(sqLiteDatabase);
            }catch (SQLException e)
            {
                Toast.makeText(context, "DB Error \n"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
