package com.makbe.taskmanager;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private TaskAdapter taskAdapter;
	private List<Task> taskList;

	private TaskDbHelper dbHelper;

	protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerForActivityResult(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbHelper = new TaskDbHelper(this);

		RecyclerView recyclerView = findViewById(R.id.recycler_view_tasks);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
		fabAddTask.setOnClickListener(view -> {
			Intent intent = new Intent(this, AddEditTaskActivity.class);
			activityLauncher.launch(intent, result -> {
				if (result.getResultCode() == RESULT_OK) {
						Intent data = result.getData();
						assert data != null;
						saveTask(data);
				}
			});
		});

		taskList = dbHelper.getAllTasks();
		sortTasksByDueDate();

		taskAdapter = new TaskAdapter(taskList);

		taskAdapter.setOnTaskOptionsClickListener(new TaskAdapter.OnTaskOptionsClickListener() {
			@Override
			public void onEditClick(int position) {
				launchEditTaskActivity(position);
			}

			@Override
			public void onDeleteClick(int position) {
				deleteTask(position);
			}
		});

		recyclerView.setAdapter(taskAdapter);

		ItemTouchHelper itemTouchHelper = getItemTouchHelper();
		itemTouchHelper.attachToRecyclerView(recyclerView);

	}

	private @NotNull ItemTouchHelper getItemTouchHelper() {
		ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder viewHolder1) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
				int position = viewHolder.getAdapterPosition();
				if (i == ItemTouchHelper.LEFT) {
					deleteTask(position);
				} else if (i == ItemTouchHelper.RIGHT) {
					launchEditTaskActivity(position);
				}
			}

			@Override
			public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

				View view = viewHolder.itemView;
				Drawable icon;
				ColorDrawable background;

				int backgroundCornerOffset = 20;

				if (dX > 0) {
					icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_edit);
					background = new ColorDrawable(Color.GREEN);
					background.setBounds(
							view.getLeft(),
							view.getTop(),
							view.getLeft() + ((int) dX) + backgroundCornerOffset,
							view.getBottom()
					);
				} else {
					icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
					background = new ColorDrawable(Color.RED);
					background.setBounds(
							view.getRight() + ((int) dX),
							view.getTop(),
							view.getRight(),
							view.getBottom()
					);
				}

				background.draw(c);
				assert icon != null;
				int iconMargin = (view.getHeight() - icon.getIntrinsicHeight()) / 2;
				int iconTop = view.getTop() + (view.getHeight() - icon.getIntrinsicHeight()) / 2;
				int iconBottom = iconTop + icon.getIntrinsicHeight();

				int iconLeft;
				int iconRight;
				if (dX > 0) {
					iconLeft = view.getLeft() + iconMargin;
					iconRight = view.getLeft() + iconMargin + icon.getIntrinsicWidth();
				} else {
					iconLeft = view.getRight() - iconMargin - icon.getIntrinsicWidth();
					iconRight = view.getRight() - iconMargin;
				}
				icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
				icon.draw(c);
			}
		};

		return new ItemTouchHelper(touchHelperCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		taskAdapter.notifyDataSetChanged();
	}

	private void saveTask(Intent data) {
		String title = data.getStringExtra("title");
		String description = data.getStringExtra("description");
		String dueDate = data.getStringExtra("dueDate");

		Task newTask = new Task(title, description, dueDate);
		long id = dbHelper.addTask(newTask);
		newTask.setId(id);
		taskList.add(newTask);
		sortTasksByDueDate();
		taskAdapter.notifyDataSetChanged();
	}

	private void launchEditTaskActivity(int position) {
		Task task = taskList.get(position);
		Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
		intent.putExtra("isEditMode", true);
		intent.putExtra("position", position);
		intent.putExtra("taskId", task.getId());
		intent.putExtra("title", task.getTitle());
		intent.putExtra("description", task.getDescription());
		intent.putExtra("dueDate", task.getDueDate());
		activityLauncher.launch(intent, result -> {
			if (result.getResultCode() == RESULT_OK) {
				Intent data = result.getData();
				assert data != null;
				editTask(data);
			}
		});
	}

	private void editTask(Intent data) {
		String title = data.getStringExtra("title");
		String description = data.getStringExtra("description");
		String dueDate = data.getStringExtra("dueDate");
		long taskId = data.getLongExtra("taskId", 0);
		int position = data.getIntExtra("position", 0);

		Task updatedTask = new Task(title, description, dueDate);
		updatedTask.setId(taskId);

		int result = dbHelper.updateTask(updatedTask);

		if (result < 1) {
			Toast.makeText(this, "Couldn't update task!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Task updated successfully", Toast.LENGTH_LONG).show();
			taskList.set(position, updatedTask);
			sortTasksByDueDate();
			taskAdapter.notifyDataSetChanged();
		}
	}

	private void deleteTask(int position) {
		Task task = taskList.get(position);
		dbHelper.deleteTask(task.getId());

		taskList.remove(position);
		taskAdapter.notifyDataSetChanged();
	}

	private void sortTasksByDueDate() {
		taskList.sort(Comparator.comparing(Task::getDueDate));
	}

}
