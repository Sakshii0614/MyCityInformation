

package com.example.raghunandan.ragumysuru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raghunandan.ragumysuru.R;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText pass, cpass, Email;
    Button Create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        Email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.pwd);
        cpass=(EditText)findViewById(R.id.cpwd);

        Create=(Button)findViewById(R.id.create);
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail=Email.getText().toString();
                String passw=pass.getText().toString();
                String cpassw=cpass.getText().toString();


                if(mail.matches("")){
                    Toast.makeText(SignupActivity.this,"email can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!ValidPassword(passw)){
                    Toast.makeText(SignupActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!cpassw.matches(passw)){
                    Toast.makeText(SignupActivity.this,"Password should match",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
                intent.putExtra("mail",mail);
                intent.putExtra("pass",passw);
                startActivity(intent);
            }
        });
    }
    Pattern lower=Pattern.compile("^.*[a-z].*$");
    Pattern upper=Pattern.compile("^.*[A-Z].*$");
    Pattern num=Pattern.compile("^.*[0-9].*$");
    Pattern spec=Pattern.compile("^.*[@#$%^*;!<>(){},.?&/].*$");

    private boolean ValidPassword(String password) {
        if(password.length()<8){
            return false;
        }
        if(!lower.matcher(password).matches() || !upper.matcher(password).matches() || !num.matcher(password).matches() || !spec.matcher(password).matches()){
            return false;
        }
        else
            return true;

    }
}