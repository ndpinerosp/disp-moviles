package co.edu.unal.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "contacto";
    public static final String TABLE_NAME = "empresa_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NOMBRE";
    public static final String COL_3 = "URL";
    public static final String COL_4 = "TELEFONO";
    public static final String COL_5 = "EMAIL";
    public static final String COL_6 = "CLASIFICACION";
    public static final String COL_7 = "PRODUCTOS";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null,1);

    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+ " ("+COL_1+" integer primary key autoincrement," +
                COL_2+" text,"+COL_3+" text,"+COL_4+" integer,"+COL_5+" text,"+COL_6+" text,"+COL_7+" text )");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String name ,String url, int tel ,String email,String calificacion,String products){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,url);
        contentValues.put(COL_4,tel);
        contentValues.put(COL_5,email);
        contentValues.put(COL_6,calificacion);
        contentValues.put(COL_7,products);
        long result= db.insert(TABLE_NAME,null,contentValues);
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+COL_1+","+COL_2+","+COL_6+" from "+TABLE_NAME,null);
        return res;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db= this.getWritableDatabase();
        return db.delete(TABLE_NAME,"ID = ?", new String[]{id});
    }

    public Cursor get_id(String name){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+COL_1+" ,"+COL_2+" from "+TABLE_NAME+ " where "+COL_2+" = ?" ,new String[]{name});
        return res;
    }

    public boolean updateData(String id, String name ,String url, int tel ,String email,String calificacion,String productos){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,url);
        contentValues.put(COL_4,tel);
        contentValues.put(COL_5,email);
        contentValues.put(COL_6,calificacion);
        contentValues.put(COL_7,productos);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{id});
        return true;
    }
    public Cursor clasif(String text){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res= db.rawQuery("select ID,NOMBRE,CLASIFICACION from empresa_table where CLASIFICACION = ?", new String[]{text});
    return  res;
    }


}
