package com.makbe.taskmanager;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.makbe.taskmanager.entity.Task;
import org.jetbrains.annotations.NotNull;

public class TaskAdapter extends ListAdapter<Task, TaskViewHolder> {
	protected TaskAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Task> diffCallback) {
		super(diffCallback);
	}

	@NonNull
	@NotNull
	@Override
	public TaskViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
		return TaskViewHolder.create(viewGroup);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull TaskViewHolder taskViewHolder, int i) {
		Task currentTask = getItem(i);
		taskViewHolder.bind(currentTask);
	}

	static class TaskDiff extends DiffUtil.ItemCallback<Task> {

		@Override
		public boolean areItemsTheSame(@NonNull @NotNull Task task, @NonNull @NotNull Task t1) {
			return task == t1;
		}

		@Override
		public boolean areContentsTheSame(@NonNull @NotNull Task task, @NonNull @NotNull Task t1) {
			return task.getTitle().equals(t1.getTitle());
		}
	}
}
