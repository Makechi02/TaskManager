package com.makbe.taskmanager.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.makbe.taskmanager.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	void saveTask(Task task);

	@Query("SELECT * FROM tasks")
	LiveData<List<Task>> getAllTasks();

	@Query("DELETE FROM tasks")
	void deleteAll();
}
