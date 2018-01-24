package com.lidong.daymoney.ui;

import com.lidong.daymoney.Category;
import com.lidong.daymoney.CategoryProvider;
import com.lidong.daymoney.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 收入/支出编辑
 */
public class CategoryEditorActivity extends AppCompatActivity {

	private int category, op, index;
	private EditText txtName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_editor);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		category = getIntent().getIntExtra("category", -1);
		op = getIntent().getIntExtra("op", -1);
		index = getIntent().getIntExtra("index", -1);

		if (op == 0) {
			setTitle(category == 0 ? R.string.title_category_expend_add : R.string.title_category_income_add);
		} else {
			setTitle(category == 0 ? R.string.title_category_expend_modify : R.string.title_category_income_modify);
		}

		txtName = (EditText) findViewById(R.id.txt_name);

		// 修改的情况下
		if (op == 1) {
			txtName.setText(CategoryActivity.listCategory.get(index).name);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_category_editor, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			case R.id.menu_save:
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void save() {

		String name = txtName.getText().toString().trim();

		if (name.equals("")) {
			Toast.makeText(this, R.string.category_name_hint, Toast.LENGTH_SHORT).show();
			return;
		}

		if (op == 0) {
			// 添加

			Category c = new Category();
			// c.id=0;
			c.name = name;
			c.io_type = category;
			c.status = 0;

			c.id = new CategoryProvider(this).insert(c);
			if (c.id != -1) {
				CategoryActivity.listCategory.add(c);
			} else {
				Toast.makeText(this, R.string.save_failure, Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			// 修改

			Category c = new Category();
			c.id = CategoryActivity.listCategory.get(index).id;
			c.name = name;
			c.io_type = CategoryActivity.listCategory.get(index).io_type;
			c.status = CategoryActivity.listCategory.get(index).status;

			if (new CategoryProvider(this).update(c) > 0)
				CategoryActivity.listCategory.get(index).name = name;
			else {
				Toast.makeText(this, R.string.save_failure, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
		setResult(Activity.RESULT_OK);
		finish();
	}
}
