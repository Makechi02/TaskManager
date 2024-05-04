package com.makbe.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makbe.taskmanager.viewModel.TaskViewModel;
import com.makbe.taskmanager.entity.Task;

public class MainActivity extends AppCompatActivity {
	private TaskViewModel taskViewModel;
	protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerForActivityResult(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MaterialToolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
		fabAddTask.setOnClickListener(view -> {
			Intent intent = new Intent(this, AddEditTaskActivity.class);
			activityLauncher.launch(intent, result -> {
				if (result.getResultCode() == RESULT_OK) {
						Intent data = result.getData();
						assert data != null;
						saveTask(data);
				} else {
					Toast.makeText(this, "Task not saved", Toast.LENGTH_SHORT).show();
				}
			});
		});

		RecyclerView recyclerView = findViewById(R.id.recycler_view_tasks);
		final TaskAdapter adapter = new TaskAdapter(new TaskAdapter.TaskDiff());
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
		taskViewModel.getAllTasks().observe(this, adapter::submitList);
	}

	private void saveTask(Intent data) {
		String title = data.getStringExtra("title");
		String description = data.getStringExtra("description");
		String dueDate = data.getStringExtra("dueDate");

		Task newTask = new Task(title, description, dueDate);
		taskViewModel.saveTask(newTask);
	}

//	private void launchEditTaskActivity(int position) {
//		Task task = taskList.get(position);
//		Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
//		intent.putExtra("isEditMode", true);
//		intent.putExtra("position", position);
//		intent.putExtra("taskId", task.getId());
//		intent.putExtra("title", task.getTitle());
//		intent.putExtra("description", task.getDescription());
//		intent.putExtra("dueDate", task.getDueDate());
//		activityLauncher.launch(intent, result -> {
//			if (result.getResultCode() == RESULT_OK) {
//				Intent data = result.getData();
//				assert data != null;
//				editTask(data);
//			}
//		});
//	}

//	private void editTask(Intent data) {
//		String title = data.getStringExtra("title");
//		String description = data.getStringExtra("description");
//		String dueDate = data.getStringExtra("dueDate");
//		long taskId = data.getLongExtra("taskId", 0);
//		int position = data.getIntExtra("position", 0);
//
//		Task updatedTask = new Task(title, description, dueDate);
//		updatedTask.setId(taskId);
//
//		int result = dbHelper.updateTask(updatedTask);
//
//		if (result < 1) {
//			Toast.makeText(this, "Couldn't update task!", Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(this, "Task updated successfully", Toast.LENGTH_LONG).show();
//			taskList.set(position, updatedTask);
//			sortTasksByDueDate();
//			taskAdapter.notifyDataSetChanged();
//		}
//	}

//	private void deleteTask(int position) {
//		Task task = taskList.get(position);
//		dbHelper.deleteTask(task.getId());

//		taskList.remove(position);
//		taskAdapter.notifyDataSetChanged();
//	}

}
