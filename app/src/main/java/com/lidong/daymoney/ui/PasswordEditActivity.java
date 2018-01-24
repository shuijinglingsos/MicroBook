package com.lidong.daymoney.ui;

import com.lidong.daymoney.Global;
import com.lidong.daymoney.PasswordHelper;
import com.lidong.daymoney.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 密码设置
 *
 * @author Lidong
 */
public class PasswordEditActivity extends AppCompatActivity {

    //操作参数
    public static final String arg_op = "op";
    //登录操作
    public static final int op_login = 1;
    //添加密码
    public static final int op_add = 2;
    //修改密码
    public static final int op_modify = 3;
    //清空密码
    public static final int op_clear = 4;

    private EditText txtEdit1, txtEdit2, txtEdit3;
    //操作类型
    private int op;
    private PasswordHelper pwHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pwHelper = new PasswordHelper(this);
        op = getIntent().getIntExtra("op", -1);
        initControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_password_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                break;

            case R.id.menu_ok:
                ok();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        txtEdit1 = (EditText) findViewById(R.id.txt_pw1);
        txtEdit2 = (EditText) findViewById(R.id.txt_pw2);
        txtEdit3 = (EditText) findViewById(R.id.txt_pw3);

        //设置标题和文本框的显示
        switch (op) {
            case op_login:
                setTitle(R.string.title_password_login);
                txtEdit1.setHint(R.string.pw_hint);
                txtEdit2.setVisibility(View.GONE);
                txtEdit3.setVisibility(View.GONE);
                break;
            case op_add:
                setTitle(R.string.title_password_add);
                txtEdit1.setHint(R.string.pw_hint_1);
                txtEdit2.setHint(R.string.pw_hint_2);
                txtEdit3.setVisibility(View.GONE);
                break;
            case op_modify:
                setTitle(R.string.title_password_modify);
                txtEdit1.setHint(R.string.pw_hint_0);
                txtEdit2.setHint(R.string.pw_hint_1);
                txtEdit3.setHint(R.string.pw_hint_2);
                break;
            case op_clear:
                setTitle(R.string.title_password_clear);
                txtEdit1.setHint(R.string.pw_hint);
                txtEdit2.setVisibility(View.GONE);
                txtEdit3.setVisibility(View.GONE);
                break;
        }
    }

    public void ok() {
        switch (op) {
            case op_login:
            case op_clear:
                loginOrClear();
                break;

            case op_add:
                add();
                break;

            case op_modify:
                modify();
                break;
        }
    }

    /**
     * 登录或清空
     */
    private void loginOrClear() {
        String s = this.txtEdit1.getText().toString().trim();

        if (Global.stringIsNullOrEmpty(s)) {
            showToastMessage("密码不能为空");
            return;
        }

        if (!pwHelper.getPassword().equals(s)) {
            showToastMessage("输入密码不正确");
            return;
        }

        if (op == op_clear) {
            pwHelper.clearPassword();
            showToastMessage("清除成功");
        }
        setResult(Activity.RESULT_OK);
        close();
    }

    /**
     * 添加密码
     */
    private void add() {
        String s1 = this.txtEdit1.getText().toString().trim();
        String s2 = this.txtEdit2.getText().toString().trim();

        if (Global.stringIsNullOrEmpty(s1) || Global.stringIsNullOrEmpty(s2)) {
            showToastMessage("密码不能为空");
            return;
        }

        if (!s1.equals(s2)) {
            showToastMessage("二次输入的密码不匹配");
            return;
        }

        pwHelper.setPassword(s1);
        showToastMessage("设置成功");

        setResult(Activity.RESULT_OK);
        close();
    }

    /**
     * 修改密码
     */
    private void modify() {
        String s1 = this.txtEdit1.getText().toString().trim();
        String s2 = this.txtEdit2.getText().toString().trim();
        String s3 = this.txtEdit3.getText().toString().trim();

        if (Global.stringIsNullOrEmpty(s1) || Global.stringIsNullOrEmpty(s2)
                || Global.stringIsNullOrEmpty(s3)) {
            showToastMessage("密码不能为空");
            return;
        }

        if (!s2.equals(s3)) {
            showToastMessage("二次输入的新密码不匹配");
            return;
        }

        if (!pwHelper.getPassword().equals(s1)) {
            showToastMessage("旧密码输入错误");
            return;
        }

        pwHelper.setPassword(s2);

        showToastMessage("修改成功");
        close();
    }

    public void showToastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
//		super.onBackPressed();
        close();
    }

    /**
     * 关闭
     */
    private void close() {
        finish();
        Global.hideDialog(this);
    }
}
