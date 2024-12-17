package com.example.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Password extends AppCompatActivity {
EditText a1,a2,a3;
TextView q1,q2,q3;
Answers answers;
    RequestQueue requestQueue;
DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);
myRef=FirebaseDatabase.getInstance().getReference();
answers=new Answers();
        a1=findViewById(R.id.Q11);
        a2=findViewById(R.id.Q22);
        a3=findViewById(R.id.Q33);
        q1=findViewById(R.id.Q1);
        q2=findViewById(R.id.Q2);
        q3=findViewById(R.id.Q3);
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    answers = dataSnapshot.getValue(Answers.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
            });
        findViewById(R.id.Turnin).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String ans1=a1.getText().toString();

                String ans2=a2.getText().toString();
                String ans3=a3.getText().toString();
                Log.d("11", ans1);
if(ans1.equals(answers.getA1())&&ans2.equals(answers.getA2())&&ans3.equals(answers.getA3())){
    Intent i=new Intent(Password.this,MainActivity.class);
    i.putExtra("Ok",123);
    startActivity(i);
}
else{
    a1.setText("");
    a2.setText("");
    a3.setText("");

}
            }
        });
    }
}