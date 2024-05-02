package com.makbe.taskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddEditTaskActivity extends AppCompatActivity {
	private TextInputLayout titleInputLayout;
	private TextInputLayout descriptionInputLayout;
	private TextInputLayout dueDateInputLayout;

	private TextInputEditText titleInputField;
	private TextInputEditText descriptionInputField;
	private TextInputEditText dueDateInputField;

	private boolean isEditMode;
	private Task taskToEdit;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_task);

		titleInputLayout = findViewById(R.id.layout_input_title);
		descriptionInputLayout = findViewById(R.id.layout_input_description);
		dueDateInputLayout = findViewById(R.id.layout_input_due_date);

		titleInputField = findViewById(R.id.edit_text_title);
		descriptionInputField = findViewById(R.id.edit_text_description);
		dueDateInputField = findViewById(R.id.edit_text_due_date);
		MaterialButton actionBtn = findViewById(R.id.button_action);

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

			titleInputField.setText(taskToEdit.getTitle());
			descriptionInputField.setText(taskToEdit.getDescription());
			dueDateInputField.setText(taskToEdit.getDueDate());

			actionBtn.setText(R.string.edit);
			actionBtn.setIcon(getResources().getDrawable(R.drawable.ic_edit));
		} else {
			actionBtn.setText(R.string.save);
		}

		actionBtn.setOnClickListener(view -> {
			if (isEditMode) {
				updateTask();
			} else {
				saveTask();
			}
		});

		dueDateInputField.setOnClickListener(view -> showDatePickerDialog());
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
					dueDateInputField.setText(selectedDate);
				}, year, month, dayOfMonth);
		datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

		datePickerDialog.show();
	}

	private void saveTask() {
		String title = Objects.requireNonNull(titleInputField.getText()).toString().trim();
		String description = Objects.requireNonNull(descriptionInputField.getText()).toString().trim();
		String dueDate = Objects.requireNonNull(dueDateInputField.getText()).toString().trim();

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
		String title = Objects.requireNonNull(titleInputField.getText()).toString().trim();
		String description = Objects.requireNonNull(descriptionInputField.getText()).toString().trim();
		String dueDate = Objects.requireNonNull(dueDateInputField.getText()).toString().trim();

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
		String title = Objects.requireNonNull(titleInputField.getText()).toString().trim();
		String description = Objects.requireNonNull(descriptionInputField.getText()).toString().trim();
		String dueDate = Objects.requireNonNull(dueDateInputField.getText()).toString().trim();

		if (TextUtils.isEmpty(title)) {
			showMessage("Title is required");
			titleInputLayout.setError("Title is required");
			return false;
		}

		if (TextUtils.isEmpty(description)) {
			showMessage("Description is required");
			descriptionInputLayout.setError("Description is required");
			return false;
		}

		if (TextUtils.isEmpty(dueDate)) {
			showMessage("Due Date is required");
			dueDateInputLayout.setError("Due Date is required");
			return false;
		}

		return true;
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
