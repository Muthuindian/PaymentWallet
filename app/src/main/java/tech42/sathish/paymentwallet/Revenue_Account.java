package tech42.sathish.paymentwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import customfonts.MyButton;
import customfonts.MyEditText;
import customfonts.MyTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mari on 1/20/17.
 */

public class Revenue_Account extends Activity implements View.OnClickListener{

    EditText mobile,name,password;
    Button signup;
    String textmobile , textname , textpassword="";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String url = "https://walletcase.herokuapp.com/revenues/";
    String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData();
    }

    private void getData() {
        mobile = (EditText) findViewById(R.id.mobile_number);
        name = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.register);
        signup.setOnClickListener(this);
    }


    String CreateJSON(String textmobile , String textname , String textpassword) throws JSONException {
        JSONObject obj = new JSONObject().put("ref", textmobile) .put("data", new JSONObject().put("name", textname).put("password" , textpassword)) .put("token", "secret") ;
        return obj.toString();
    }

    String post(String json) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();
        final ProgressDialog progressDialog = new ProgressDialog(Revenue_Account.this);
        progressDialog.setMessage("Creating account..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestBody requestbody = RequestBody.create(JSON , json);
        Request request = new Request.Builder().url(url).post(requestbody).build();
       /* try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }*/
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.cancel();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = response.body().string();
                progressDialog.cancel();

                Intent login = new Intent(Revenue_Account.this , LoginActivity.class);
                startActivity(login);


            }
        });
        return result;
    }

    @Override
    public void onClick(View v) {
        if(v==signup)
        {
            textmobile=mobile.getText().toString();
            textname = name.getText().toString();
            textpassword = password.getText().toString();
            try {
                String response = post(CreateJSON(textmobile , textname , textpassword));
                //Log.v("Response", response);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
