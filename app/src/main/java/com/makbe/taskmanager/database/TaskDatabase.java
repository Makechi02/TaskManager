package com.makbe.taskmanager.database;

import android.content.Context;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.makbe.taskmanager.dao.TaskDao;
import com.makbe.taskmanager.entity.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
	public abstract TaskDao taskDao();

	private static volatile TaskDatabase INSTANCE;
	private static final int NUMBER_OF_THREADS = 4;
	public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static TaskDatabase getDatabase(final Context context) {
		if (INSTANCE == null) {
			synchronized (TaskDatabase.class) {
				if (INSTANCE == null) {
					INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class, "task_database")
							.addCallback(roomDatabaseCallback)
							.build();
				}
			}
		}
		return INSTANCE;
	}

	private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
		@Override
		public void onCreate(@NotNull SupportSQLiteDatabase db) {
			super.onCreate(db);

			databaseWriteExecutor.execute(() -> {
				TaskDao taskDao = INSTANCE.taskDao();
				taskDao.deleteAll();

				Task codingTask = new Task("Coding", "Some cool description", "2024-05-01");
				Task tkTask = new Task("TK", "Light Weight", "2024-05-01");

				taskDao.saveTask(codingTask);
				taskDao.saveTask(tkTask);
			});
		}
	};
}
