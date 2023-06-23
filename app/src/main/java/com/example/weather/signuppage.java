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

public class signuppage extends AppCompatActivity {
    Button submit;
    EditText inputemail,inputpassword;

    FirebaseAuth mAuth;
    FirebaseUser muser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuppage);
        inputpassword=(EditText)findViewById(R.id.editTextTextPassword);
        inputemail=(EditText)findViewById(R.id.editTextTextEmailAddress);

        mAuth = FirebaseAuth.getInstance();
        muser=mAuth.getCurrentUser();

        submit=(Button) findViewById(R.id.submit);

        // starting of sign in using email and password
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perforauth();

            }

            private void perforauth() {
                String email=inputemail.getText().toString();
                String password=inputpassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(signuppage.this,"regestraion succesfull",Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(signuppage.this, loginpage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else
                        {
                            Toast.makeText(signuppage.this,"Regestration unsuccesfull",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });//ending of sign in using email and password


    }
}