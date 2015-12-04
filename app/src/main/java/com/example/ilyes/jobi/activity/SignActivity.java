package com.example.ilyes.jobi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ilyes.jobi.R;
import com.example.ilyes.jobi.database.UserDataSource;
import com.example.ilyes.jobi.other.Util;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class SignActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    EditText mEmailET;

    @NotEmpty
    @Password
    EditText mPasswordET;

    Button mSubmitBtn;
    Button mSignUpWorker;
    Button mSignUpClient;
    UserDataSource dataSource;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        // Get ref to the views
        mEmailET = (EditText) findViewById(R.id.emial_et);
        mPasswordET = (EditText) findViewById(R.id.password_et);
        mSubmitBtn = (Button) findViewById(R.id.submit_btn);
        mSignUpWorker = (Button) findViewById(R.id.signin_worker_btn);
        mSignUpClient = (Button) findViewById(R.id.signin_client_btn);
        dataSource = new UserDataSource(this);


        // The EditText validator
        validator = new Validator(this);
        validator.setValidationListener(this);

        // When click on submit button get data from the view
        // and check if the user is a worker or a client
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate the input before using it
                validator.validate();
            }
        });


        // Click Sign up as a Worker
        mSignUpWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this, SignUpWorkerActivity.class));
            }
        });


        // Click Sign up as a Client
        mSignUpClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this, SignUpClientActivity.class));
            }
        });

    }

    @Override
    public void onValidationSucceeded() {

        // If the validation succeeded
        // get the data from the EditText
        // and search for the user
        // in the workers and clients table
        String email = mEmailET.getText().toString();
        String password = mPasswordET.getText().toString();

        dataSource.open();

        Intent intent = new Intent(SignActivity.this, MainActivity.class);


        if (dataSource.isWorkerExist(email, password)) {

            // Put in the intent the id and the type of the user
            intent.putExtra(Util.ID_FLAG, dataSource.getWorkerId(email, password) + "");
            intent.putExtra(Util.USER_TYPE_FLAG, "worker");

            startActivity(intent);
            finish();

        } else if (dataSource.isClientExist(email, password)) {

            intent.putExtra(Util.ID_FLAG, dataSource.getClientId(email, password) + "");
            intent.putExtra(Util.USER_TYPE_FLAG, "client");

            startActivity(intent);
            finish();
        } else {
            // Print user does not exixst
            Toast.makeText(SignActivity.this, "user does not exist", Toast.LENGTH_SHORT).show();
        }

        dataSource.close();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}