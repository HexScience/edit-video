package com.hecorat.azplugin2.donate;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.donate.util.IabHelper;
import com.hecorat.azplugin2.donate.util.IabResult;
import com.hecorat.azplugin2.donate.util.Inventory;
import com.hecorat.azplugin2.donate.util.Purchase;
import com.hecorat.azplugin2.donate.util.SkuDetails;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.util.ArrayList;

/**
 * Created by bkmsx on 1/6/2017.
 */

public class IabController {
    MainActivity mainActivity;
    IabHelper mIabHelper;
    boolean mSetupFailed;

    public IabController(MainActivity activity) {
        mainActivity = activity;
        setupIabHelper();
    }

    private void setupIabHelper() {
        mIabHelper = new IabHelper(mainActivity, Constants.PUBLIC_KEY);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.e("MainActivity", "sdf Problem setting up In-app Billing: " + result);
                    mainActivity.checkVipWithoutInternet();
                    mSetupFailed = true;
                    return;
                }
                queryPurchase();
            }
        });
    }

    public void buyItem() {
        if (mSetupFailed) {
            toast(mainActivity.getString(R.string.no_internet_connection));
            return;
        }
        try {
            mIabHelper.launchPurchaseFlow(mainActivity, Constants.SKU_DONATE, 10001,
                    mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            toast("exception purchase");
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                toast(mainActivity.getString(R.string.purchase_failed));
            } else if (purchase.getSku().equals(Constants.SKU_DONATE)) {
                toast(mainActivity.getString(R.string.purchase_success));
                mainActivity.removeWaterMark();
            }
        }
    };

    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        mIabHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void queryPurchase() {
        try {
            mIabHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            toast("exception query");
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // handle error here
            } else {
                // does the user have the premium upgrade?
                if (!inventory.hasPurchase(Constants.SKU_DONATE)) {
                    mainActivity.onCheckVipCompleted(false);
                    return;
                }
                Purchase purchase = inventory.getPurchase(Constants.SKU_DONATE);
                log(purchase.getSku());
                log(purchase.getOriginalJson());
                log(purchase.getPurchaseTime()+"");
                log(purchase.getItemType());
                mainActivity.onCheckVipCompleted(true);
            }
        }
    };

    private void queryItems() {
        ArrayList<String> additionalSkuList = new ArrayList<>();
        additionalSkuList.add(Constants.SKU_DONATE);
        try {
            mIabHelper.queryInventoryAsync(true, additionalSkuList, null, mQueryFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                log("There is no inventory");
                return;
            }

            SkuDetails details = inventory.getSkuDetails(Constants.SKU_DONATE);
            log(details.getPrice());
            // update the UI
        }
    };

    private void toast(String msg) {
        Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show();
    }

    private void log(String msg) {
        Log.e("IabController", msg);
    }
}
