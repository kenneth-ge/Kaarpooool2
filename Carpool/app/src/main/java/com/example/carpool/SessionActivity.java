package com.example.carpool;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpool.util.EasyAsync;
import com.example.carpool.util.PersonDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SessionActivity extends AppCompatActivity {

    private String code, password;
    private JSONObject jsonData;
    private TextView errorTooltipSession;

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);

        errorTooltipSession = findViewById(R.id.error_tooltip_session);

        TextView codeStuff = findViewById(R.id.code_header);

        Intent i = getIntent();

        code = i.getStringExtra("CODE");
        password = i.getStringExtra("PASSWORD");

        codeStuff.setText("Code: " + code);

        updateData();
    }

    public void calculate(View view) {
        EasyAsync easyAsync = new EasyAsync(() -> {
            try {
                final String s = InternetHandler.calculate(code);

                runOnUiThread(() -> {
                    Intent i = new Intent(this, RouteActivity.class);
                    i.putExtra("ROUTE", s);

                    startActivity(i);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        easyAsync.execute();
    }

    public void updateDestination(View view) {
        EditText editText = findViewById(R.id.destination);
        String newDest = editText.getText().toString();

        try {
            jsonData.put("dest", newDest);
        } catch (JSONException e) {
            e.printStackTrace();
            errorTooltipSession.setText(R.string.something_wrong);
        }

        EasyAsync easyAsync = new EasyAsync(() -> {
            try {
                jsonData = InternetHandler.update(code, jsonData);
                runOnUiThread(this::updateData);
            }catch(Exception e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    errorTooltipSession.setText(R.string.something_wrong);
                });
            }
        });

        easyAsync.execute();
    }

    public void updateData(){
        EasyAsync easyAsync = new EasyAsync(() -> {
            try {
                JSONObject data = InternetHandler.query_session(Long.parseLong(code));
                System.out.println("Data: " + data);
                jsonData = data.getJSONObject("json");
                System.out.println("Json data: " + jsonData);

                System.out.println("update data: " + jsonData.toString());

                runOnUiThread(() -> {
                    errorTooltipSession.setText(jsonData.toString());

                    JSONArray array = null;
                    try {
                        array = jsonData.getJSONArray("people");

                        LinearLayout ll = findViewById(R.id.linearLayout);

                        ll.removeAllViews();

                        for(int i = 0; i < array.length(); i++) {
                            JSONObject obj = (JSONObject) array.get(i);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            Button btn = new Button(getApplicationContext());
                            btn.setText(obj.getString("name"));

                            PersonDetails personDetails = new PersonDetails();
                            personDetails.name = obj.getString("name");
                            personDetails.address = obj.getString("address");
                            personDetails.driver = obj.getBoolean("driver");

                            btn.setOnClickListener(this::updatePerson);

                            btn.setTag(personDetails);

                            ll.addView(btn, lp);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        errorTooltipSession.setText(R.string.something_wrong);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    errorTooltipSession.setText(R.string.something_wrong);
                });
            }
        });

        easyAsync.execute();
    }

    public void updatePerson(View v) {
        PersonDetails pd = (PersonDetails) v.getTag();

        Intent intent = new Intent(this, AddPersonActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        intent.putExtra("NAME", pd.name);
        intent.putExtra("ADDRESS", pd.address);
        intent.putExtra("DRIVER", pd.driver);

        startActivity(intent);
    }

    public void addPerson(View view) {
        Intent intent = new Intent(this, AddPersonActivity.class);

        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        intent.putExtra("NAME", "new group member");
        intent.putExtra("ADDRESS", "");
        intent.putExtra("DRIVER", false);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onResume(){
        super.onResume();

        updateData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Activity resluts");

        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

                String name = data.getStringExtra("NAME");
                String address = data.getStringExtra("ADDRESS");
                boolean driver = data.getBooleanExtra("DRIVER", false);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", name);
                    obj.put("address", address);
                    obj.put("driver", driver);

                    JSONArray array = jsonData.getJSONArray("people");

                    boolean thing = false;
                    for(int i = 0; i < array.length(); i++){
                        JSONObject element = (JSONObject) array.get(i);
                        if(element.get("name") == name){
                            array.put(i, obj);
                            thing = true;
                            break;
                        }
                    }

                    if(!thing) {
                        array.put(obj);
                    }

                    jsonData.put("people", array);

                    EasyAsync easyAsync = new EasyAsync(() -> {
                        try {
                            InternetHandler.update(code, jsonData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    easyAsync.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void share(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Carpool Session", "Add yourself to my pickup route!\nCode: " + code + "\nPassword: " + password);
        clipboard.setPrimaryClip(clip);

        Context context = getApplicationContext();
        CharSequence text = "Copied route data to your clipboard! :D";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
