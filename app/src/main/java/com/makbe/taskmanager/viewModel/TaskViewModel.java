package com.makbe.taskmanager.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.makbe.taskmanager.entity.Task;
import com.makbe.taskmanager.repository.TaskRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
	private final TaskRepository taskRepository;
	private final LiveData<List<Task>> allTasks;

	public TaskViewModel(@NotNull Application application) {
		super(application);
		taskRepository = new TaskRepository(application);
		allTasks = taskRepository.getAllTasks();
	}

	public LiveData<List<Task>> getAllTasks() {
		return allTasks;
	}

	public void saveTask(Task task) {
		taskRepository.saveTask(task);
	}


}
