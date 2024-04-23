package com.makbe.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
	private final List<Task> taskList;

	private OnTaskOptionsClickListener listener;

	public TaskAdapter(List<Task> taskList) {
		this.taskList = taskList;
	}

	@NonNull
	@Override
	public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
		return new TaskViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
		Task task = taskList.get(position);
		holder.bind(task);

		holder.optionsButton.setOnClickListener(view -> showOptionsPopupMenu(view, position));
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	public interface OnTaskOptionsClickListener {
		void onEditClick(int position);
		void onDeleteClick(int position);
	}

	public void setOnTaskOptionsClickListener(OnTaskOptionsClickListener listener) {
		this.listener = listener;
	}

	private void showOptionsPopupMenu(View view, final int position) {
		PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
		popupMenu.inflate(R.menu.task_options_menu);
		popupMenu.setOnMenuItemClickListener(item -> {
			int id = item.getItemId();
			if(id == R.id.action_edit && listener != null) {
				listener.onEditClick(position);
				return true;
			} else if(id == R.id.action_delete && listener != null) {
				listener.onDeleteClick(position);
				return true;
			}
			return false;
		});
		popupMenu.show();
	}

	public static class TaskViewHolder extends RecyclerView.ViewHolder {
		private final TextView textViewTitle;
		private final TextView textViewDescription;
		private final TextView textViewDueDate;

		private final ImageView optionsButton;

		public ImageView iconEdit;
		public ImageView iconDelete;

		public TaskViewHolder(@NonNull View itemView) {
			super(itemView);
			textViewTitle = itemView.findViewById(R.id.text_view_task_title);
			textViewDescription = itemView.findViewById(R.id.text_view_task_description);
			textViewDueDate = itemView.findViewById(R.id.text_view_task_due_date);
			optionsButton = itemView.findViewById(R.id.options_button);

			iconEdit = itemView.findViewById(R.id.action_edit);
			iconDelete = itemView.findViewById(R.id.action_delete);

			itemView.setOnClickListener(view -> {
				Context context = view.getContext();
				Intent intent = new Intent(context, TaskDetailActivity.class);
				intent.putExtra("title", textViewTitle.getText().toString());
				intent.putExtra("description", textViewDescription.getText().toString());
				intent.putExtra("dueDate", textViewDueDate.getText().toString());
				context.startActivity(intent);
			});

		}

		public void bind(Task task) {
			textViewTitle.setText(task.getTitle());
			textViewDescription.setText(task.getDescription());
			textViewDueDate.setText(String.format("Due Date: %s", task.getDueDate()));
		}
	}
}
