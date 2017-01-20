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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mobileNumber;
    private EditText email;
    private EditText password;
    private Button register;
    private String String_MobileNumber,String_Email,String_Password;
    private ProgressDialog progressDialog;
    private static final String URL = "https://walletcase.herokuapp.com/wallets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Assign id
        findViews();
    }

    private void findViews() {
        mobileNumber = (EditText)findViewById( R.id.mobile_number );
        email = (EditText)findViewById( R.id.email );
        password = (EditText)findViewById( R.id.password );
        register = (Button)findViewById( R.id.register );

        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        getData();
        if ( v == register ) {
            if(isConnected())
                createWalletAccount();
            else
                Toast.makeText(this,"No internet connection..",Toast.LENGTH_SHORT).show();
        }
    }

    private void nextActivity() {
        Intent next = new Intent(MainActivity.this,HomeActivity.class);
        next.putExtra("balance","0");
        startActivity(next);
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
                    "        \"email\": \""+String_Email+"\",\n" +
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
        String_Email = email.getText().toString().trim();
        String_Password = password.getText().toString().trim();
    }
}
