package tech42.sathish.paymentwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private String imageEncoded = "";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int SELECT_PICTURE = 100;

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
        accountImage.setImageResource(R.drawable.profile);

        register.setOnClickListener(this);
        accountImage.setOnClickListener(this);
    }

    private void getAccountType()
    {
        Bundle bundle = getIntent().getExtras();
        accountType = bundle.getString("accountType");
        URL += accountType + "/";
        URL2 = URL;

        if ( accountType.equals("wallets"))
            accountText.setText("Wallets Account");
        else if ( accountType.equals("rechargers"))
            accountText.setText("Rechargers Account");
        else
            accountText.setText("Revenue Account");
    }

    @Override
    public void onClick(View v) {
        getData();
        if ( v == register ) {
            if(isConnected())
                //createWalletAccount();
            {
                if(dataValidation()) {
                    if(accountType.equals("wallets"))
                        already_Have_Account_or_Not();
                    else
                        createWalletAccount();
                }
                else
                    Toast.makeText(this,"Please enter all values..",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this,"No internet connection..",Toast.LENGTH_SHORT).show();
        }
        else if( v == accountImage)
            selectImage();
    }

    private void nextActivity() {
        Intent next = new Intent(MainActivity.this,RechargeActivity.class);
        next.putExtra("balance","0");
        next.putExtra("ref",String_MobileNumber);
        next.putExtra("image",imageEncoded);
        next.putExtra("name",String_Name);
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
            if(imageEncoded.equals(""))
            {
                Bitmap bitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.profile);
                encodeBitmapAndSaveToFirebase(bitmap);
            }
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            final String requestBody = CreateJSON(String_MobileNumber,String_Name,String_Password,imageEncoded);

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
                    Toast.makeText(MainActivity.this,"This mobile number already have an account..",Toast.LENGTH_SHORT).show();
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

    private String CreateJSON(String textmobile , String textname , String textpassword , String image) throws JSONException {
        JSONObject obj = new JSONObject().put("ref", textmobile) .put("data", new JSONObject().put("name", textname).put("password" , textpassword).put("image",image)) .put("token", "secret") ;
        return obj.toString();
    }

    private boolean dataValidation()
    {
        if(String_MobileNumber.equals("")||String_Name.equals("")||String_Password.equals(""))
            return false;
        else
            return true;
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
                        if(accountValidate(response.toString())) {
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


    private boolean accountValidate(String response)
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

    /*-------------------- Image upload ------------------------------*/

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /* Choose an image from Gallery */

    private void selectImage() {
        final CharSequence[] items = { "Take Photo",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    if(result)
                        onLaunchCamera();

                } else if (items[item].equals("Choose from Library")) {
                    if(result)
                        openImageChooser();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            accountImage.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
        else if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK )
        {
            if (data != null) {
                // Get the URI of the selected file
                final Uri uri = data.getData();
                useImage(uri);
            }
        }
    }

    void useImage(Uri uri)
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            accountImage.setImageBitmap(bitmap);
            encodeBitmapAndSaveToFirebase(bitmap);
        }
        catch(IOException e)
        {
            Log.d("Error",e.getMessage());
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
