package tech42.sathish.paymentwallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Button login,register;
    private TextView accountText;
    private ImageView accountImage;
    private ProgressDialog progressDialog;

    private String string_mobilenumber,string_password,json_mobilenumber,json_password,json_balance,accountType;
    private String URL = "https://walletcase.herokuapp.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();

        getAccountType();

    }

    private void findViews() {
        mobile_number = (EditText)findViewById( R.id.mobile_number );
        password = (EditText)findViewById( R.id.password );
        login = (Button)findViewById( R.id.login );
        register = (Button) findViewById( R.id.register );
        accountText = (TextView)findViewById(R.id.account_text);
        accountImage = (ImageView)findViewById(R.id.account_image);

        login.setOnClickListener( this );
        register.setOnClickListener( this );
    }


    private void getAccountType()
    {
        Bundle bundle = getIntent().getExtras();
        accountType = bundle.getString("accountType");
        URL += accountType;

        if ( accountType.equals("wallets"))
        {
            accountImage.setImageResource(R.drawable.wallet);
            accountText.setText("Wallets Account");
        }
        else if ( accountType.equals("rechargers"))
        {
            accountImage.setImageResource(R.drawable.recharge);
            accountText.setText("Rechargers Account");
        }
        else
        {
            accountImage.setImageResource(R.drawable.revenue);
            accountText.setText("Revenue Account");
        }
    }

        @Override
        public void onClick(View v) {
            getData();
            if ( v == login ) {
                // Handle clicks for login
                if(dataValidate())
                checkLogin();
                else
                    Toast.makeText(getApplicationContext(),"Please enter both the values",Toast.LENGTH_SHORT).show();
            } else if ( v == register ) {
                // Handle clicks for register
                registerActivity();
            }
        }

    private boolean dataValidate()
    {
        if(string_mobilenumber.equals("")||string_password.equals(""))
            return false;
        else
            return true;
    }

    private void registerActivity() {
        Intent next = new Intent(LoginActivity.this,MainActivity.class);
        next.putExtra("accountType",accountType);
        startActivity(next);
    }


    public void checkLogin()
    {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Signing in..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        URL += string_mobilenumber;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(loginValidate(response.toString()))
                            nextActivity();
                        else{
                            Toast.makeText(LoginActivity.this,"Mobile number or Password was incorrect",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();}

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
        Intent next = new Intent(LoginActivity.this,HomeActivity.class);
        next.putExtra("balance",json_balance);
        startActivity(next);
        progressDialog.dismiss();
    }


    private boolean loginValidate(String response)
    {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());

            json_mobilenumber = jsonObject.getString("ref");
            json_balance = jsonObject.getString("balance");
            JSONObject dataObject = new JSONObject(jsonObject.getString("data"));
            json_password = dataObject.getString("password");

        }
        catch(JSONException e)
        {
            Log.d("Error: ",e.getMessage());
        }
        // validate login

        if(( string_mobilenumber.equals(json_mobilenumber)) && ( string_password.equals(json_password)))
            return true;
        else
            return false;

    }

}
