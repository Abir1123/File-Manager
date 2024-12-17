package com.example.filemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sign_Up extends AppCompatActivity {
    TextView q1,q2,q3;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RequestQueue requestQueue;
    Answers answers;
FirebaseAuth auth;
FirebaseUser user;
    Button signupButton;
    EditText email,password,a1,a2,a3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.semailEditText);
        password=findViewById(R.id.spasswordEditText);
        a1=findViewById(R.id.Q11);
        a2=findViewById(R.id.Q22);
        a3=findViewById(R.id.Q33);
q1=findViewById(R.id.Q1);
q2=findViewById(R.id.Q2);
q3=findViewById(R.id.Q3);
database=FirebaseDatabase.getInstance();
myRef=database.getReference("Answers");
answers=new Answers();

requestQueue= Volley.newRequestQueue(this);
String url="https://api.myjson.online/v1/records/a73850b3-679b-450b-b76b-495cc875abb0";

JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
try{
    JSONArray questions=response.getJSONArray("Questions");
    for (int i=0;i<3;i++){
        JSONObject question=questions.getJSONObject(i);
        String Q1=question.getString("Q1");
        String Q2=question.getString("Q2");
        String Q3=question.getString("Q3");
        q1.setText(Q1);
        q2.setText(Q2);
        q3.setText(Q3);
    }
}catch (JSONException e){
    throw new RuntimeException(e);
}
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }
});
signupButton=findViewById(R.id.signUpButton);
signupButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
String e=email.getText().toString();
String p=password.getText().toString();
String a11=a1.getText().toString();
String a22=a2.getText().toString();
String a33=a3.getText().toString();

if(TextUtils.isEmpty(e)){
    Toast.makeText(Sign_Up.this, "Please enter email", Toast.LENGTH_SHORT).show();
return;
}
if(TextUtils.isEmpty(p)){
    Toast.makeText(Sign_Up.this, "Please enter password", Toast.LENGTH_SHORT).show();
    return;
}
if(TextUtils.isEmpty(a11)||TextUtils.isEmpty(a22)||TextUtils.isEmpty(a33)){
    Toast.makeText(Sign_Up.this, "Please enter all the answers", Toast.LENGTH_SHORT).show();
    return;
}
else{
    addDatatoFirebase(a11,a22,a33);
}
        auth.createUserWithEmailAndPassword(e, p)
                .addOnCompleteListener(Sign_Up.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", e);
                            editor.putString("password", p);
                            editor.apply();

Toast.makeText(Sign_Up.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(Sign_Up.this,MainActivity.class);
                            i.putExtra("Pass",p);
                            startActivity(i);
                        } else {
                            Toast.makeText(Sign_Up.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

});
    }
    private void addDatatoFirebase(String a11, String a22, String a33) {
    answers.setA1(a11);
    answers.setA2(a22);
    answers.setA3(a33);
       myRef.push().setValue(answers)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sign_Up.this, "Data added",
                                Toast.LENGTH_SHORT).show();
                        a1.setText("");
                        a2.setText("");
                        a3.setText("");
                    } else {
                        Toast.makeText(Sign_Up.this, "Failed to add data: " +
                                task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}