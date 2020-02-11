package com.example.carpool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AddPersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person_activity);

        Intent i = getIntent();

        TextView textView = findViewById(R.id.addOrModify);
        textView.setText("Information for " + i.getStringExtra("NAME"));

        EditText editText = findViewById(R.id.enter_address);
        editText.setText(i.getStringExtra("ADDRESS"));

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setEnabled(i.getBooleanExtra("DRIVER", false));
    }

    public void change(View view) {
        EditText editNameText = findViewById(R.id.editNameText);
        EditText address = findViewById(R.id.enter_address);
        CheckBox checkBox = findViewById(R.id.checkBox);

        Intent i = new Intent();
        i.putExtra("NAME", editNameText.getText().toString());
        i.putExtra("ADDRESS", address.getText().toString());
        i.putExtra("DRIVER", checkBox.isChecked());

        setResult(RESULT_OK, i);

        finish();
    }

}
