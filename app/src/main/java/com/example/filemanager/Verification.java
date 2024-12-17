package com.example.filemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Verification extends AppCompatActivity {
FirebaseAuth auth;
EditText email,password;
String e,p;
Button signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification);
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.semailEditText);
        password=findViewById(R.id.spasswordEditText);
        signin=findViewById(R.id.signUpButton);
        Intent pi=getIntent();
        String path=pi.getStringExtra("Path");
        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j=new Intent(Verification.this,MainActivity.class);
                startActivity(j);
            }
        });
signin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        e=email.getText().toString();
        p=password.getText().toString();
        if(TextUtils.isEmpty(e)){
            Toast.makeText(Verification.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(p)){
            Toast.makeText(Verification.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(Verification.this, new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()){
            Toast.makeText(Verification.this, "Verification successful", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(Verification.this,MainActivity.class);
            i.putExtra("True",path);
            startActivity(i);
        }else{
            Toast.makeText(Verification.this, "Verification failed", Toast.LENGTH_SHORT).show();
        }
    }
});
    }
});
findViewById(R.id.pass).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent k=new Intent(Verification.this,Password.class);
        startActivity(k);
    }
});
    }
}