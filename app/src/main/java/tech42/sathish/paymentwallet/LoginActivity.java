package tech42.sathish.paymentwallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mobile_number;
    private EditText password;
    private Button login;
    private TextView register;
    private String string_mobilenumber,string_password;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
    }

    private void findViews() {
        mobile_number = (EditText)findViewById( R.id.mobile_number );
        password = (EditText)findViewById( R.id.password );
        login = (Button)findViewById( R.id.login );
        register = (TextView)findViewById( R.id.register );

        login.setOnClickListener( this );
        register.setOnClickListener( this );
    }


        @Override
        public void onClick(View v) {
            getData();
            if ( v == login ) {
                // Handle clicks for login
                checkLogin();
            } else if ( v == register ) {
                // Handle clicks for register
                registerActivity();
            }
        }

    private void registerActivity() {
        Intent next = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(next);
    }


    public void checkLogin()
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://walletcase.herokuapp.com/wallets/" + string_mobilenumber;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                        nextActivity();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    public void getData()
    {
     string_mobilenumber = mobile_number.getText().toString().trim();
     string_password = password.getText().toString().trim();
    }

    private void nextActivity() {
        Intent next = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(next);
    }

}
