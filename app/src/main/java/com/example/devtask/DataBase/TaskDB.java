package com.example.devtask.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devtask.models.TaskModel;

import java.util.ArrayList;

public class TaskDB extends SQLiteOpenHelper {


    public static final String task_database = "task_db";
    Context context;
    SQLiteDatabase db_read ;
    SQLiteDatabase db_write;
    Cursor mcursor;
    private static  TaskDB taskDB;


    public TaskDB(Context context) {

        super(context, task_database, null, 1);
        this.context = context;
        db_read = this.getReadableDatabase();
        db_write = this.getWritableDatabase();

    }

    public static TaskDB getInstance(Context ctx) {

        if (taskDB == null) {
            taskDB = new TaskDB(ctx.getApplicationContext());
        }
        return taskDB;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table taskData (repoName text ,repoDesc text , userName text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long insertData(ArrayList<TaskModel> data){


          long i = 0;
          for(TaskModel row : data){

              ContentValues contentValues = new ContentValues();
              contentValues.put("repoName" , row.getName());
              contentValues.put("repoDesc" ,row.getDescription() );
              contentValues.put("userName" , row.getOwner().getLogin());
              i = db_write.insert("taskData", null, contentValues);

          }
        return i;
    }

    public void deleteData(){

        db_write.execSQL("DELETE FROM taskData");

    }




}
