package tech42.sathish.paymentwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mobileNumber;
    private EditText name;
    private EditText password;
    private TextView accountText;
    private ImageView accountImage;
    private Button register;
    private String String_MobileNumber,String_Name,String_Password,accountType;
    private ProgressDialog progressDialog;
    private String URL = "https://walletcase.herokuapp.com/",URL2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Assign id
        findViews();

        getAccountType();
    }


    private void findViews() {
        mobileNumber = (EditText)findViewById( R.id.mobile_number );
        name = (EditText)findViewById( R.id.name );
        password = (EditText)findViewById( R.id.password );
        register = (Button)findViewById( R.id.register );
        accountText = (TextView)findViewById(R.id.account_text);
        accountImage = (ImageView)findViewById(R.id.account_image);

        register.setOnClickListener(this);
    }

    private void getAccountType()
    {
        Bundle bundle = getIntent().getExtras();
        accountType = bundle.getString("accountType");
        URL += accountType + "/";
        URL2 = URL;

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
        if ( v == register ) {
            if(isConnected())
                //createWalletAccount();
            already_Have_Account_or_Not();
            else
                Toast.makeText(this,"No internet connection..",Toast.LENGTH_SHORT).show();
        }
    }

    private void nextActivity() {
        Intent next = new Intent(MainActivity.this,HomeActivity.class);
        next.putExtra("balance","0");
        startActivity(next);
        finish();
        progressDialog.dismiss();
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    public void createWalletAccount()
    {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Creating account..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final String requestBody = "{\n" +
                    "    \"ref\": \""+String_MobileNumber+"\",\n" +
                    "    \"data\": {\n" +
                    "        \"name\": \""+String_Name+"\",\n" +
                    "        \"password\": \""+String_Password+"\"\n" +
                    "    },\n" +
                    "    \"token\": \"secret\"\n" +
                    "}";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    nextActivity();
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(getApplicationContext(),"Mobile number was already registered..",Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData()
    {
        String_MobileNumber = mobileNumber.getText().toString().trim();
        String_Name = name.getText().toString().trim();
        String_Password = password.getText().toString().trim();
    }


    private void already_Have_Account_or_Not()
    {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Mobile number Validating..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        URL2 += String_MobileNumber;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL2, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(loginValidate(response.toString())) {
                            progressDialog.dismiss();
                            createWalletAccount();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"This mobile number already have an account..",Toast.LENGTH_SHORT).show();
                        }
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


    private boolean loginValidate(String response)
    {
        JSONObject dataObject=null;
        try {
            JSONObject jsonObject = new JSONObject(response.toString());

             dataObject = new JSONObject(jsonObject.getString("data"));
        }
        catch(JSONException e)
        {
            dataObject = null;
        }
        // validate login

        if(dataObject == null)
            return true;
        else
            return false;
    }


}
