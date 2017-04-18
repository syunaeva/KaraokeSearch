package com.ulduzsoft.karaoke.karaokesearch;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

         final EditText uname = (EditText) findViewById(R.id.username_edittext);

        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // Create an explicit Intent for starting the Main Class
                // Activity
                String loginName = uname.getText().toString();
                if (loginName.length() > 0) {
                    Intent MainActivityIntent = new Intent(LoginScreen.this,
                            MainActivity.class);
                    MainActivityIntent.putExtra("loginName", loginName);
                    // Use the Intent to start the Main Activity
                    startActivity(MainActivityIntent);
                }

            }

        });
    }
}

