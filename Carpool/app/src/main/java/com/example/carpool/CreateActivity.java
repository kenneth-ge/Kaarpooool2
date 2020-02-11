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

import org.json.JSONObject;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void create(View view) {
        EditText passwordField = findViewById(R.id.password_create);

        String text = passwordField.getText().toString();

        if(text.length() < 1){
            TextView tooltip = findViewById(R.id.validation_create);

            tooltip.setText(R.string.longer_password);
            return;
        }

        EasyAsync easyAsync = new EasyAsync(() -> {
            try {
                JSONObject obj = InternetHandler.create(text);

                System.out.println(obj);

                Intent intent = new Intent(this, SessionActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                intent.putExtra("PASSWORD", text);
                intent.putExtra("CODE", String.valueOf(obj.get("id")));
                startActivity(intent);
            }catch(Exception e){
                e.printStackTrace();

                runOnUiThread(() -> {
                    TextView tooltip = findViewById(R.id.validation_create);
                    tooltip.setText(R.string.wrong_on_our_end);
                });
            }
        });

        easyAsync.execute();
    }
}
