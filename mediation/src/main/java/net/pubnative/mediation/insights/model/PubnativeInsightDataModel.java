// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.insights.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class PubnativeInsightDataModel {

    private static final   String TAG                      = PubnativeInsightDataModel.class.getName();
    protected static final String CONNECTION_TYPE_CELLULAR = "cellular";
    protected static final String CONNECTION_TYPE_WIFI     = "wifi";
    // Tracking info
    public String                             network;
    public List<String>                       attempted_networks;
    public List<String>                       unreachable_networks;
    public List<Integer>                      delivery_segment_ids;
    public List<PubnativeInsightNetworkModel> networks;
    public String                             placement_name;
    public String                             pub_app_version;
    public String                             pub_app_bundle_id;
    public String                             os_version;
    public String                             sdk_version;
    public String                             user_uid; // android advertiser id
    public String                             connection_type; //typ “wifi” or “cellular"
    public String                             device_name;
    public String                             ad_format_code;
    public String                             creative_url; // Creative selected from the ad_format_code value of the config
    public Boolean                            video_start;
    public Boolean                            video_complete;
    public int                                retry;
    public String                             retry_error;
    // User info
    public Integer                            age;
    public String                             education;
    public List<String>                       interests;
    public String                             gender;
    public Boolean                            iap; // In app purchase enabled, Just open it for the user to fill
    public Float                              iap_total; // In app purchase total spent, just open for the user to fill
    public Long                               generated_at;

    //==============================================================================================
    // Object
    //==============================================================================================

    @Override
    public boolean equals(Object object) {

        Log.v(TAG, "equals");
        if (this == object) {
            // return true immediately if both objects are identical.
            return true;
        }
        if (!(object instanceof PubnativeInsightDataModel)) {
            // return immediately if the object is of another class,
            // this is to avoid possible class cast exception in next line.
            return false;
        }
        PubnativeInsightDataModel dataModel = (PubnativeInsightDataModel) object;
        boolean result = isEqual(network, dataModel.network);
        if (result) {
            result = isEqual(attempted_networks, dataModel.attempted_networks);
        }
        if (result) {
            result = isEqual(placement_name, dataModel.placement_name);
        }
        if (result) {
            result = isEqual(pub_app_version, dataModel.pub_app_version);
        }
        if (result) {
            result = isEqual(pub_app_bundle_id, dataModel.pub_app_bundle_id);
        }
        if (result) {
            result = isEqual(os_version, dataModel.os_version);
        }
        if (result) {
            result = isEqual(sdk_version, dataModel.sdk_version);
        }
        if (result) {
            result = isEqual(user_uid, dataModel.user_uid);
        }
        if (result) {
            result = isEqual(connection_type, dataModel.connection_type);
        }
        if (result) {
            result = isEqual(device_name, dataModel.device_name);
        }
        if (result) {
            result = isEqual(ad_format_code, dataModel.ad_format_code);
        }
        if (result) {
            result = isEqual(creative_url, dataModel.creative_url);
        }
        if (result) {
            result = isEqual(video_start, dataModel.video_start);
        }
        if (result) {
            result = isEqual(video_complete, dataModel.video_complete);
        }
        // user info
        if (result) {
            result = isEqual(age, dataModel.age);
        }
        if (result) {
            result = isEqual(education, dataModel.education);
        }
        if (result) {
            result = isEqual(interests, dataModel.interests);
        }
        if (result) {
            result = isEqual(gender, dataModel.gender);
        }
        if (result) {
            result = isEqual(iap, dataModel.iap);
        }
        if (result) {
            result = isEqual(iap_total, dataModel.iap_total);
        }
        return result;
    }

    //==============================================================================================
    // PubnativeInsightDataModel
    //==============================================================================================
    // Private
    //----------------------------------------------------------------------------------------------

    /**
     * This method takes two Objects "first" and "second" as arguments and does a comparison.
     * Returns true if they are equal.
     * Returns false if they are not equal or not comparable.
     */
    private boolean isEqual(Object first, Object second) {

        Log.v(TAG, "isEqual");
        return (first != null) ? first.equals(second) : second == null;
    }

    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Adds network insight data to the insight
     *
     * @param priorityRuleModel valid PubnativePriorityRuleModel object
     * @param responseTime      valid long in milliseconds
     * @param crashModel        valid PubnativeInsightCrashModel or null
     */
    public void addNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, PubnativeInsightCrashModel crashModel) {

        Log.v(TAG, "addNetwork");
        if (networks == null) {
            networks = new ArrayList<PubnativeInsightNetworkModel>();
        }
        PubnativeInsightNetworkModel networkModel = new PubnativeInsightNetworkModel();
        if (priorityRuleModel != null) {
            networkModel.code = priorityRuleModel.network_code;
            networkModel.priority_rule_id = priorityRuleModel.id;
            networkModel.priority_segment_ids = priorityRuleModel.segment_ids;
        }
        networkModel.response_time = responseTime;
        if (crashModel != null) {
            networkModel.crash_report = crashModel;
        }
        networks.add(networkModel);
    }

    /**
     * Adds a network code to the attempted_networks list
     *
     * @param network valid String
     */
    public void addAttemptedNetwork(String network) {

        Log.v(TAG, "addAttemptedNetwork: " + network);
        if (!TextUtils.isEmpty(network)) {
            if (attempted_networks == null) {
                attempted_networks = new ArrayList<String>();
            }
            attempted_networks.add(network);
        }
    }

    /**
     * Adds a network code to the unreachable_networks list
     *
     * @param network valid String
     */
    public void addUnreachableNetwork(String network) {

        Log.v(TAG, "addUnreachableNetwork: " + network);
        if (!TextUtils.isEmpty(network)) {
            if (unreachable_networks == null) {
                unreachable_networks = new ArrayList<String>();
            }
            unreachable_networks.add(network);
        }
    }

    /**
     * Clear all related request tracking insight data
     */
    public void reset() {

        Log.v(TAG, "reset");
        retry = 0;
        retry_error = null;
        network = null;
        networks = null;
        delivery_segment_ids = null;
        attempted_networks = null;
        unreachable_networks = null;
        generated_at = null;
    }

    /**
     * Fills insight data model with default available data.
     *
     * @param context valid Context object
     */
    public void fillDefaults(Context context) {

        Log.v(TAG, "fillDefaults");
        if (context != null) {
            PackageInfo info = PubnativeDeviceUtils.getPackageInfo(context);
            if (info != null) {
                pub_app_version = info.versionName;
                pub_app_bundle_id = info.packageName;
            }
            retry = 0;
            os_version = Build.VERSION.RELEASE;
            device_name = Build.MODEL;
            sdk_version = net.pubnative.mediation.BuildConfig.VERSION_NAME + " (" + net.pubnative.mediation.BuildConfig.VERSION_CODE + ")";
            // Connection type
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String connectionType = CONNECTION_TYPE_CELLULAR;
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        connectionType = CONNECTION_TYPE_WIFI;
                    }
                    connection_type = connectionType;
                }
            }
        }
    }

    public void setTargetting(PubnativeAdTargetingModel targetting) {

        Log.v(TAG, "setTargeting");
        age = targetting.age;
        education = targetting.education;
        interests = targetting.interests;
        gender = targetting.gender;
        iap = targetting.iap;
        iap_total = targetting.iap_total;
    }
}
