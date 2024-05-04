package com.makbe.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.makbe.taskmanager.entity.Task;
import org.jetbrains.annotations.NotNull;

public class TaskViewHolder extends RecyclerView.ViewHolder {
	private final TextView textViewTitle;
	private final TextView textViewDescription;
	private final TextView textViewDueDate;

	public TaskViewHolder(@NonNull @NotNull View itemView) {
		super(itemView);

		textViewTitle = itemView.findViewById(R.id.text_view_task_title);
		textViewDescription = itemView.findViewById(R.id.text_view_task_description);
		textViewDueDate = itemView.findViewById(R.id.text_view_task_due_date);

		itemView.setOnClickListener(view -> {
			Context context = view.getContext();
			Intent intent = new Intent(context, TaskDetailActivity.class);
			intent.putExtra("title", textViewTitle.getText().toString());
			intent.putExtra("description", textViewDescription.getText().toString());
			intent.putExtra("dueDate", textViewDueDate.getText().toString());
			context.startActivity(intent);
		});
	}

	public static TaskViewHolder create(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
		return new TaskViewHolder(view);
	}

	public void bind(Task task) {
		textViewTitle.setText(task.getTitle());
		textViewDescription.setText(task.getDescription());
		textViewDueDate.setText(String.format("Due Date: %s", task.getDueDate()));
	}
}
