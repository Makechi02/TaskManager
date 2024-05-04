package com.makbe.taskmanager.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.makbe.taskmanager.dao.TaskDao;
import com.makbe.taskmanager.database.TaskDatabase;
import com.makbe.taskmanager.entity.Task;

import java.util.List;

public class TaskRepository {
	private final TaskDao taskDao;
	private final LiveData<List<Task>> allTasks;

	public TaskRepository(Application application) {
		TaskDatabase taskDatabase = TaskDatabase.getDatabase(application);
		taskDao = taskDatabase.taskDao();
		allTasks = taskDao.getAllTasks();
	}

	public LiveData<List<Task>> getAllTasks() {
		return allTasks;
	}

	public void saveTask(Task task) {
		TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.saveTask(task));
	}
}
