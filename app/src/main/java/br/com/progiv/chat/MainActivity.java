package br.com.progiv.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    private EditText editTextEmail;
    private EditText editTextName;

    private Button buttonEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initiailizing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);

        buttonEnter = (Button) findViewById(R.id.buttonEnter);

        buttonEnter.setOnClickListener(this);

        //If the user is already logged in
        //Starting chat room
        if (AppController.getInstance().isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ChatRoomActivity.class));
        }/**/

    }

    //Method to register user
    private void registerUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Entrando no chat");
        progressDialog.show();

        final String nome = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        // Optional Parameters to pass as POST request
        JSONObject js = new JSONObject();
        try {
            js.put("nome",nome);
            js.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(URLs.URL_REGISTER, js,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                        try {
                            Log.d("Response", String.valueOf(response));
                            JSONObject obj = response;
                            Log.d("Objeto", String.valueOf(obj));
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            String email = obj.getString("email");

                            //Login user
                            AppController.getInstance().loginUser(id,name,email);

                            //Starting chat room we need to create this activity
                            startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String x = error.getMessage();
                    }
                }) {
        };
        AppController.getInstance().addToRequestQueue(jsonObjectReq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Checking if user is logged in
        if(AppController.getInstance().isLoggedIn()){
            finish();
            startActivity(new Intent(this, ChatRoomActivity.class));
        }
    }


    @Override
    public void onClick(View v) {
        registerUser();
    }

}