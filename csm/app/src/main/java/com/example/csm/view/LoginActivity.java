package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.csm.R;
import com.example.csm.util.NetworkUtil;
import com.example.csm.viewmodel.AccountManagementViewModel;
import com.example.csm.viewmodel.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;

    private EditText usernameTextView;
    private EditText passwordTextView;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(LoginViewModel.class);

        usernameTextView = (EditText) findViewById(R.id.editTextUsername);
        passwordTextView = (EditText) findViewById(R.id.editTextPassword);

        messageTextView = (TextView) findViewById(R.id.textMessage);
        viewModel.getMessageLiveData().observe(this, messageLiveData -> {
            messageTextView.setText(messageLiveData);
        });

        viewModel.getUserAuthenticadedLiveData().observe(this, userAuthenticatedLiveData -> {
            if (userAuthenticatedLiveData) {
                loginSuccess();
            }
        });
    }

    public void loginSuccess() {
        Intent newIntent = new Intent(this, MainMenuActivity.class);
        this.startActivity(newIntent);
        finish();
    }

    public void onClickButtonLogin(View view) {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        viewModel.login(username, password);
    }
}