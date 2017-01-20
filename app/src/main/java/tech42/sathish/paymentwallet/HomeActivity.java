package tech42.sathish.paymentwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by lenovo on 19/1/17.
 */

public class HomeActivity extends AppCompatActivity {

    TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
    }

    private void findViews()
    {
        balance = (TextView)findViewById(R.id.balance);

        Bundle bundle = getIntent().getExtras();
        balance.setText(bundle.getString("balance"));

    }
}
