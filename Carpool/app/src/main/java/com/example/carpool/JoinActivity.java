package com.example.carpool;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.carpool.util.EasyAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void join(View view) {
        EditText editText = findViewById(R.id.join_code);
        EditText password = findViewById(R.id.join_password);

        EasyAsync easyAsync = new EasyAsync(() -> {
            try {
                long l = -1;
                try {
                    l = Long.parseLong(editText.getText().toString());
                }catch(NumberFormatException e){
                    return;
                }
                JSONObject obj = InternetHandler.login(l, password.getText().toString());

                System.out.println(obj);

                if(!obj.getBoolean(("success"))){
                    runOnUiThread(() -> {
                        TextView tooltip = findViewById(R.id.error_tooltip);
                        tooltip.setText(R.string.invalid_username_or_password);
                    });

                    return;
                }

                Intent intent = new Intent(this, SessionActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                intent.putExtra("PASSWORD", password.getText().toString());
                intent.putExtra("CODE", String.valueOf(l));
                startActivity(intent);
            }catch(IOException | JSONException e){
                e.printStackTrace();

                runOnUiThread(() -> {
                    TextView tooltip = findViewById(R.id.error_tooltip);
                    tooltip.setText(R.string.invalid_username_or_password);
                });
            }
        });

        easyAsync.execute();
    }
}
