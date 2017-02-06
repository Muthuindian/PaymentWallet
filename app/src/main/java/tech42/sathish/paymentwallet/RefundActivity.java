package tech42.sathish.paymentwallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RefundActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView balance;
    private EditText unique_id;
    private String revenue_balance,string_id,string_amount,string_name,string_image,string_usernmae;
    private TextView recharge;
    private ProgressDialog progressDialog;
    private String URL = "https://walletcase.herokuapp.com/refunds";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        findViews();
        getRechargerData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
    }

    private void findViews()
    {
        balance = (TextView)findViewById(R.id.balance);
        unique_id = (EditText)findViewById(R.id.unique_id);
        recharge = (TextView)findViewById(R.id.recharge);

        recharge.setOnClickListener(this);
    }

    private void getRechargerData()
    {
        Bundle bundle = getIntent().getExtras();
        revenue_balance = bundle.getString("balance");
        string_name = bundle.getString("ref");
        string_image = bundle.getString("image");
        string_usernmae = bundle.getString("name");
        balance.setText(revenue_balance);
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
        string_id = unique_id.getText().toString().trim();
    }

    private boolean dataValidation()
    {
        if(string_id.equals(""))
            return false;
        else
            return true;
    }

    public void createRecharge()
    {
        progressDialog = new ProgressDialog(RefundActivity.this);
        progressDialog.setMessage("Amount Transfering..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final String requestBody = CreateJSON(string_id);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Refunded Successfully..",Toast.LENGTH_LONG).show();
                    clear();
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(RefundActivity.this,"This unique id was already used..",Toast.LENGTH_SHORT).show();
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

    private String CreateJSON(String textuniqueid) throws JSONException {
        JSONObject obj = new JSONObject().put("ref", textuniqueid).put("data", new JSONObject().put("k1", "v1")) .put("token", "secret") ;
        return obj.toString();
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        Intent next = new Intent(RefundActivity.this,AccoutSelectionActivity.class);
                        startActivity(next);
                        finish();
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);

        TextView text_id = (TextView)header.findViewById(R.id.id);
        TextView text_name = (TextView)header.findViewById(R.id.name);
        text_id.setText(string_name);
        text_name.setText(string_usernmae);

        ImageView image = (ImageView)header.findViewById(R.id.image);
        try {
            image.setImageBitmap(decodeFromFirebaseBase64(string_image));
        }
        catch(IOException e)
        {
            Log.d("Error",e.getMessage());
        }

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    // String decode to bitmap
    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public void clear()
    {
        unique_id.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


