package tech42.sathish.paymentwallet;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView balance;
    private EditText unique_id;
    private EditText receiver_mobile_number;
    private String recharger_mobile_number,recharger_balance,string_mobilenumber,string_id,string_amount;
    private EditText amount;
    private TextView recharge;
    private ProgressDialog progressDialog;
    private String URL = "https://walletcase.herokuapp.com/recharges";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        findViews();
        getRechargerData();
    }

    private void findViews()
    {
        balance = (TextView)findViewById(R.id.balance);
        unique_id = (EditText)findViewById(R.id.unique_id);
        receiver_mobile_number = (EditText)findViewById(R.id.mobile_number);
        amount = (EditText)findViewById(R.id.amount);
        recharge = (TextView)findViewById(R.id.recharge);

        recharge.setOnClickListener(this);
    }

    private void getRechargerData()
    {
        Bundle bundle = getIntent().getExtras();
        recharger_balance = bundle.getString("balance");
        recharger_mobile_number = bundle.getString("ref");
        balance.setText(recharger_balance);
    }

    @Override
    public void onClick(View v) {
        viewData();
        if (v == recharge) {
            if(dataValidation())
                createRecharge();
        }
    }

    private void viewData()
    {
        string_amount = amount.getText().toString().trim();
        string_id = unique_id.getText().toString().trim();
        string_mobilenumber = receiver_mobile_number.getText().toString();
    }

    private boolean dataValidation()
    {
        if(string_id.equals("")||string_mobilenumber.equals("")||string_amount.equals(""))
            return false;
        else
            return true;
    }

    public void createRecharge()
    {
        progressDialog = new ProgressDialog(RechargeActivity.this);
        progressDialog.setMessage("Amount Recharging..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final String requestBody = CreateJSON(string_id,string_mobilenumber,recharger_mobile_number,string_amount);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),string_amount + " rupess was successfully recharged from " + recharger_mobile_number+ " to "+string_mobilenumber,Toast.LENGTH_LONG).show();
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(RechargeActivity.this,"This unique id was already used..",Toast.LENGTH_SHORT).show();
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

    private String CreateJSON(String textuniqueid, String textwallet, String textrecharger , String textamount) throws JSONException {
        JSONObject obj = new JSONObject().put("ref", textuniqueid).put("wallet",textwallet).put("recharger",textrecharger).put("amount",textamount).put("data", new JSONObject().put("k1", "v1")) .put("token", "secret") ;
        return obj.toString();
    }


}
