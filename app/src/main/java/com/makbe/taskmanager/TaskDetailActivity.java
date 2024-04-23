package com.makbe.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String description = intent.getStringExtra("description");
		String dueDate = intent.getStringExtra("dueDate");

		setTitle(title);

		TextView titleTextView = findViewById(R.id.text_task_title);
		TextView descriptionTextView = findViewById(R.id.text_task_description);
		TextView dueDateTextView = findViewById(R.id.text_task_due_date);

		titleTextView.setText(title);
		descriptionTextView.setText(description);
		dueDateTextView.setText(dueDate);
	}
}
