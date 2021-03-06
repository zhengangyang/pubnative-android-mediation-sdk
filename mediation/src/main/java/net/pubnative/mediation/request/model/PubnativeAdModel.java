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

package net.pubnative.mediation.request.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.library.utils.ImageDownloader;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightModel;

import java.util.HashMap;
import java.util.Map;

public abstract class PubnativeAdModel {

    private static final String                TAG                       = PubnativeAdModel.class.getSimpleName();
    // Model
    protected            Context               mContext                  = null;
    protected            Listener              mListener                 = null;
    // Tracking
    protected            PubnativeInsightModel mInsightModel             = null;
    protected            boolean               mImpressionTracked        = false;
    protected            boolean               mClickTracked             = false;
    // View
    protected            View                  mTitleView                = null;
    protected            View                  mDescriptionView          = null;
    protected            View                  mIconView                 = null;
    protected            View                  mBannerView               = null;
    protected            View                  mRatingView               = null;
    protected            View                  mCallToActionView         = null;
    // Cached assets
    protected            Map<String, Bitmap>   mCachedAssets             = null;
    protected            int                   mRemainingCacheableAssets = 0;

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Listener with all callbacks of the model
     */
    public interface Listener {

        /**
         * Callback that will be invoked when the impression is confirmed
         *
         * @param model model where the impression was confirmed
         */
        void onAdImpressionConfirmed(PubnativeAdModel model);

        /**
         * Callback that will be invoked when the ad click was detected
         *
         * @param model model where the click was confirmed
         */
        void onAdClick(PubnativeAdModel model);
    }

    /**
     * Listener with callback for assets
     */
    public interface FetchListener {
        /**
         * Callback that will be invoked when all the assets caching completed
         */
        void onFetchFinished();
    }

    /**
     * Sets the a listener for tracking callbacks
     *
     * @param listener valid Listener
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Fetch all required things
     *
     * @param listener Valid FetchListener
     */
    public void fetch(FetchListener listener) {

        Log.v(TAG, "fetch");
        fetchAssets(listener);
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected void fetchAssets(FetchListener listener) {

        Log.v(TAG, "fetchAssets");
        if (listener == null) {
            Log.w(TAG, "Listener is not set");
        }
        // This needs to be updated whenever we are going to cache more resources.
        mRemainingCacheableAssets = 2;
        cacheAsset(getIconUrl(), listener); // cache icon
        cacheAsset(getBannerUrl(), listener); // cache banner
    }

    protected void cacheAsset(final String url, final FetchListener listener) {

        Log.v(TAG, "cacheAsset");
        if (TextUtils.isEmpty(url)) {
            checkCachedAssets(listener);
        } else {
            new ImageDownloader().load(url, new ImageDownloader.Listener() {
                @Override
                public void onImageLoad(String url, Bitmap bitmap) {
                    addCachedAsset(url, bitmap);
                    checkCachedAssets(listener);
                }

                @Override
                public void onImageFailed(String url, Exception exception) {
                    Log.e(TAG, "Asset download error: " + url, exception);
                    checkCachedAssets(listener);
                }
            });
        }
    }

    protected void addCachedAsset(String url, Bitmap asset) {

        Log.v(TAG, "addCachedAsset");
        if (mCachedAssets == null) {
            mCachedAssets = new HashMap<String, Bitmap>();
        }

        if (!TextUtils.isEmpty(url) && asset != null) {
            mCachedAssets.put(url, asset);
        }
    }

    protected void checkCachedAssets(FetchListener listener) {

        Log.v(TAG, "checkCachedAssets");
        mRemainingCacheableAssets--;
        if (mRemainingCacheableAssets == 0) {
            if (listener != null) {
                listener.onFetchFinished();
            }
        }
    }

    protected Bitmap getAsset(String url) {

        Log.v(TAG, "getAsset");
        Bitmap result = null;
        if (mCachedAssets != null) {
            result = mCachedAssets.get(url);
        }
        return result;
    }

    //==============================================================================================
    // ABSTRACT
    //==============================================================================================
    // MODEL FIELDs
    //----------------------------------------------------------------------------------------------

    /**
     * gets title of the current ad
     *
     * @return short string with ad title
     */
    public abstract String getTitle();

    /**
     * gets description of the current ad
     *
     * @return long string with ad details
     */
    public abstract String getDescription();

    /**
     * gets the URL where to download the ad icon from
     *
     * @return icon URL string
     */
    public abstract String getIconUrl();

    /**
     * gets the URL where to download the ad banner from
     *
     * @return banner URL string
     */
    public abstract String getBannerUrl();

    /**
     * gets the icon image for ad.
     *
     * @return icon bitmap image.
     */
    public Bitmap getIcon(){
        return getAsset(getIconUrl());
    }

    /**
     * gets the banner image for ad.
     *
     * @return banner bitmap image.
     */
    public Bitmap getBanner(){
        return getAsset(getBannerUrl());
    }

    /**
     * gets the call to action string (download, free, etc)
     *
     * @return call to action string
     */
    public abstract String getCallToAction();

    /**
     * gets the star rating in a base of 5 stars
     *
     * @return float with value between 0.0 and 5.0
     */
    public abstract float getStarRating();

    /**
     * gets the advertising disclosure item for the current network (Ad choices, Sponsor label, etc)
     *
     * @param context context
     * @return Disclosure view to be added on top of the ad.
     */
    public abstract View getAdvertisingDisclosureView(Context context);

    /**
     * set native ad that may contain video and rich media (facebook network only)
     *
     * @param view view
     * @return View view that will have rich media including video.
     */
    public View setNativeAd(View view) {
        return null;
    }

    //----------------------------------------------------------------------------------------------
    // VIEW TRACKING
    //----------------------------------------------------------------------------------------------

    /**
     * Sets the title view for tracking
     *
     * @param view valid View containing the title
     * @return this object
     */
    public PubnativeAdModel withTitle(View view) {

        Log.v(TAG, "withTitle");
        mTitleView = view;
        return this;
    }

    /**
     * Sets the description view for tracking
     *
     * @param view valid View containing the description
     * @return this object
     */
    public PubnativeAdModel withDescription(View view) {

        Log.v(TAG, "withDescription");
        mDescriptionView = view;
        return this;
    }

    /**
     * Sets the icon view for tracking
     *
     * @param view valid View containing the icon
     * @return this object
     */
    public PubnativeAdModel withIcon(View view) {

        Log.v(TAG, "withIcon");
        mIconView = view;
        return this;
    }

    /**
     * Sets the banner view for tracking
     *
     * @param view valid View containing the banner
     * @return this object
     */
    public PubnativeAdModel withBanner(View view) {

        Log.v(TAG, "withBanner");
        mBannerView = view;
        return this;
    }

    /**
     * Sets the rating view for tracking
     *
     * @param view valid View containing the rating
     * @return this object
     */
    public PubnativeAdModel withRating(View view) {

        Log.v(TAG, "withRating");
        mRatingView = view;
        return this;
    }

    /**
     * Sets the call to action view for tracking
     *
     * @param view valid View containing the call to action
     * @return this object
     */
    public PubnativeAdModel withCallToAction(View view) {

        Log.v(TAG, "withCallToAction");
        mCallToActionView = view;
        return this;
    }

    //----------------------------------------------------------------------------------------------
    // TRACKING
    //----------------------------------------------------------------------------------------------

    /**
     * Start tracking a view to automatically confirm impressions and handle clicks
     *
     * @param context context
     * @param adView  view that will handle clicks and will be tracked to confirm impression
     */
    public abstract void startTracking(Context context, ViewGroup adView);

    /**
     * Stop using the view for confirming impression and handle clicks
     */
    public abstract void stopTracking();

    public abstract void setLinkCaching(boolean enable);
    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets extended tracking (used to initialize the view)
     *
     * @param insightModel insight model with all the tracking data
     */
    public void setInsightModel(PubnativeInsightModel insightModel) {

        Log.v(TAG, "setInsightModel");
        mInsightModel = insightModel;
        // We set the creative based on  the model creative
        if (mInsightModel != null) {
            if (PubnativePlacementModel.AdFormatCode.NATIVE_ICON.equals(mInsightModel.getAdFormat())) {
                mInsightModel.setCreativeUrl(getIconUrl());
            } else {
                mInsightModel.setCreativeUrl(getBannerUrl());
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeOnAdImpressionConfirmed() {

        Log.v(TAG, "invokeOnAdImpressionConfirmed");
        if (!mImpressionTracked && mInsightModel != null) {
            mImpressionTracked = true;
            mInsightModel.sendImpressionInsight();
            if (mListener != null) {
                mListener.onAdImpressionConfirmed(this);
            }
        }
    }

    protected void invokeOnAdClick() {

        Log.v(TAG, "invokeOnAdClick");
        if (!mClickTracked && mInsightModel != null) {
            mClickTracked = true;
            mInsightModel.sendClickInsight();
        }
        if (mListener != null) {
            mListener.onAdClick(this);
        }
    }
}
