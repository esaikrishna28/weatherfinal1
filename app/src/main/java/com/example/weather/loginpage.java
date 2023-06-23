package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginpage extends AppCompatActivity {
    Button loginbtn;
    EditText inputemail,inputpassword;
    FirebaseAuth mAuth;
    FirebaseUser muser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        inputpassword=(EditText)findViewById(R.id.editTextTextPasswordlog);
        inputemail=(EditText)findViewById(R.id.editTextTextEmailAddresslog);

        mAuth = FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();

        loginbtn=(Button) findViewById(R.id.login);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performlogin();


            }
        });



    }
    private void performlogin() {
        String email=inputemail.getText().toString();
        String password=inputpassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(loginpage.this,"login sussefull",Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(loginpage.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else
                {
                    Toast.makeText(loginpage.this,"Regestration unsuccesfull",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}