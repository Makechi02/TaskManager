package com.makbe.taskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddEditTaskActivity extends AppCompatActivity {
	private EditText editTextTitle;
	private EditText editTextDescription;
	private EditText editTextDueDate;
	private boolean isEditMode;
	private Task taskToEdit;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_task);

		editTextTitle = findViewById(R.id.edit_text_title);
		editTextDescription = findViewById(R.id.edit_text_description);
		editTextDueDate = findViewById(R.id.edit_text_due_date);
		Button buttonAction = findViewById(R.id.button_save);

		Intent intent = getIntent();
		if (intent.hasExtra("isEditMode")) {
			isEditMode = getIntent().getBooleanExtra("isEditMode", false);

			long taskId = getIntent().getLongExtra("taskId", 0);
			String title = getIntent().getStringExtra("title");
			String description = getIntent().getStringExtra("description");
			String dueDate = getIntent().getStringExtra("dueDate");
			position = getIntent().getIntExtra("position", 0);

			taskToEdit = new Task(title, description, dueDate);
			taskToEdit.setId(taskId);

			editTextTitle.setText(taskToEdit.getTitle());
			editTextDescription.setText(taskToEdit.getDescription());
			editTextDueDate.setText(taskToEdit.getDueDate());

			buttonAction.setText(R.string.edit);
		} else {
			buttonAction.setText(R.string.save);
		}

		buttonAction.setOnClickListener(view -> {
			if (isEditMode) {
				updateTask();
			} else {
				saveTask();
			}
		});

		editTextDueDate.setOnClickListener(view -> showDatePickerDialog());
	}

	private void showDatePickerDialog() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if (taskToEdit != null && taskToEdit.getDueDate() != null && !taskToEdit.getDueDate().isEmpty()) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				Date date = sdf.parse(taskToEdit.getDueDate());
				calendar.setTime(date);
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);
				dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTaskActivity.this,
				(view, year1, monthOfYear, dayOfMonth1) -> {
					String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth1);
					editTextDueDate.setText(selectedDate);
				}, year, month, dayOfMonth);
		datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

		datePickerDialog.show();
	}

	private void saveTask() {
		String title = editTextTitle.getText().toString().trim();
		String description = editTextDescription.getText().toString().trim();
		String dueDate = editTextDueDate.getText().toString().trim();

		if (validateFields()) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("title", title);
			resultIntent.putExtra("description", description);
			resultIntent.putExtra("dueDate", dueDate);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
	}

	private void updateTask() {
		String title = editTextTitle.getText().toString().trim();
		String description = editTextDescription.getText().toString().trim();
		String dueDate = editTextDueDate.getText().toString().trim();

		if (validateFields()) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("title", title);
			resultIntent.putExtra("description", description);
			resultIntent.putExtra("dueDate", dueDate);
			resultIntent.putExtra("taskId", taskToEdit.getId());
			resultIntent.putExtra("position", position);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
	}

	private boolean validateFields() {
		String title = editTextTitle.getText().toString().trim();
		String description = editTextDescription.getText().toString().trim();
		String dueDate = editTextDueDate.getText().toString().trim();

		if (TextUtils.isEmpty(title)) {
			showMessage("Title is required");
			editTextTitle.setError("Title is required");
			return false;
		}

		if (TextUtils.isEmpty(description)) {
			showMessage("Description is required");
			editTextDescription.setError("Description is required");
			return false;
		}

		if (TextUtils.isEmpty(dueDate)) {
			showMessage("Due Date is required");
			editTextDueDate.setError("Due Date is required");
			return false;
		}

		return true;
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
