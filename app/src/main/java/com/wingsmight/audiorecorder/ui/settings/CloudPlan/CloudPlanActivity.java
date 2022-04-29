package com.wingsmight.audiorecorder.ui.settings.CloudPlan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.wingsmight.audiorecorder.CloudDatabase;
import com.wingsmight.audiorecorder.CloudStoragePlan;
import com.wingsmight.audiorecorder.R;

public class CloudPlanActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static final String PRODUCT_ID = "paid_cloud_plan";


    private View buyPaidCloudPlan;
    private BillingProcessor billingProcessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_plan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        billingProcessor = new BillingProcessor(this, PRODUCT_ID, this);
        billingProcessor.initialize();

        buyPaidCloudPlan = findViewById(R.id.buyPaidCloudPlan);
        buyPaidCloudPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingProcessor.purchase(CloudPlanActivity.this, PRODUCT_ID);
            }
        });
    }

    // this event will enable the bac function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // IBillingHandler implementation
    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, PurchaseInfo purchaseInfo) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        if (productId.equals(PRODUCT_ID)) {
            CloudDatabase.changeStoragePlan(CloudStoragePlan.Plan.paid2GB, new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {

                }
            });
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }
}