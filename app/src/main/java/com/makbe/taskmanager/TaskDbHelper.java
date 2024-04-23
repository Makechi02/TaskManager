package com.makbe.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Tasks.db";

	private static final String TABLE_NAME = "tasks";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_DUE_DATE = "due_date";

	private static final String CREATE_TABLE_QUERY =
			"CREATE TABLE " + TABLE_NAME + " (" +
					COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_TITLE + " TEXT, " +
					COLUMN_DESCRIPTION + " TEXT, " +
					COLUMN_DUE_DATE + " TEXT)";

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + TABLE_NAME;

	public TaskDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public long addTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, task.getTitle());
		values.put(COLUMN_DESCRIPTION, task.getDescription());
		values.put(COLUMN_DUE_DATE, task.getDueDate());
		long id = db.insert(TABLE_NAME, null, values);
		db.close();
		return id;
	}

	public List<Task> getAllTasks() {
		List<Task> taskList = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		if (cursor.moveToFirst()) {
			do {
				Task task = new Task();
				task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
				task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
				task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
				task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)));
				taskList.add(task);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return taskList;
	}

	public int updateTask(Task task) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, task.getTitle());
		values.put(COLUMN_DESCRIPTION, task.getDescription());
		values.put(COLUMN_DUE_DATE, task.getDueDate());
		int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
				new String[]{String.valueOf(task.getId())});
		db.close();
		return rowsAffected;
	}

	public void deleteTask(long taskId) {
		int id = (int) taskId;
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id);
		db.close();

	}
}
