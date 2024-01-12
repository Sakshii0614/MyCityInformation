

package com.example.raghunandan.ragumysuru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raghunandan.ragumysuru.R;

public class LoginActivity extends AppCompatActivity {

    Button Signupbut;
    Button Loginbut;
    EditText userna,passwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Signupbut=(Button) findViewById(R.id.create);
        userna = (EditText) findViewById(R.id.luser);
        passwo = (EditText) findViewById(R.id.lpwd);

        Loginbut = (Button) findViewById(R.id.login);

        String fetcheduser = getIntent().getStringExtra("mail");
        String fetchedpass = getIntent().getStringExtra("pass");


        Loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String typeduser = userna.getText().toString();
                String typedpass = passwo.getText().toString();

                if (fetcheduser.equals(typeduser) && fetchedpass.equals(typedpass)) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                    userna.setText("");
                    passwo.setText("");
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password dosen't exist", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        Signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });


        //new Handler().postDelayed(new Runnable() {
          //  @Override
            //public void run() {
              //  Intent i=new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(i);
                //finish();
            //}
        //},5000);


    }

}