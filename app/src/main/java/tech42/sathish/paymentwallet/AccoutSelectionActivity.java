package tech42.sathish.paymentwallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AccoutSelectionActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout walletLayout;
    private LinearLayout rechargeLayout;
    private LinearLayout revenueLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accout_selection);

        findViews();
    }

    private void findViews() {

        walletLayout = (LinearLayout)findViewById( R.id.wallet_layout );
        rechargeLayout = (LinearLayout)findViewById( R.id.recharge_layout );
        revenueLayout = (LinearLayout)findViewById( R.id.revenue_layout );

        walletLayout.setOnClickListener(this);
        rechargeLayout.setOnClickListener(this);
        revenueLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == walletLayout)
            nextActivity("wallets");
        else if(v == rechargeLayout)
            nextActivity("rechargers");
        else if(v == revenueLayout)
            nextActivity("revenues");
    }

    private void nextActivity(String accountType)
    {
        Intent next = new Intent(AccoutSelectionActivity.this,LoginActivity.class);
        next.putExtra("accountType",accountType);
        startActivity(next);
    }
}
