/*
 * Copyright 2019 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.raghunandan.ragumysuru.ui.hotels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.raghunandan.ragumysuru.R;
import com.example.raghunandan.ragumysuru.data.AppRepository;
import com.example.raghunandan.ragumysuru.data.IResourceRepository;
import com.example.raghunandan.ragumysuru.data.local.models.Hotel;
import com.example.raghunandan.ragumysuru.ui.BaseView;
import com.example.raghunandan.ragumysuru.ui.MainActivity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class HotelListPresenter implements HotelListContract.Presenter {

    //Constant used for Logs
    private static final String LOG_TAG = HotelListPresenter.class.getSimpleName();

    //Instance of the App Repository
    @NonNull
    private final AppRepository mAppRepository;

    //The View Interface of this Presenter
    @NonNull
    private final HotelListContract.View mHotelListView;

    //Boolean to avoid unnecessary downloads of the Hotels data
    private AtomicBoolean mHotelsLoadedBoolean = new AtomicBoolean(false);

    /**
     * Constructor of {@link HotelListPresenter}
     *
     * @param appRepository Instance of {@link AppRepository} for accessing the data
     * @param hotelListView The View Instance {@link HotelListContract.View} of this Presenter
     */
    public HotelListPresenter(@NonNull AppRepository appRepository, @NonNull HotelListContract.View hotelListView) {
        mAppRepository = appRepository;
        mHotelListView = hotelListView;

        //Registering the View with the Presenter
        mHotelListView.setPresenter(this);
    }

    /**
     * Method that initiates the work of a Presenter which is invoked by the View
     * that implements the {@link BaseView}
     */
    @Override
    public void start() {

        //Download the list of Hotels to be shown when not downloaded previously
        if (mHotelsLoadedBoolean.compareAndSet(false, true)) {
            loadHotels();
        }
    }

    /**
     * Method that downloads the list of Hotels to be updated to the View
     */
    private void loadHotels() {
        //Display progress indicator
        mHotelListView.showProgressIndicator();

        //Retrieving the Hotels from the Repository
        mAppRepository.getHotelListEntries(R.array.hotel_list_entries, new IResourceRepository.GetResourceCallback<List<Hotel>>() {
            /**
             * Method invoked when the {@code results} are obtained for the data requested.
             *
             * @param results The {@code results} in the generic type passed
             *                which is a List of {@link Hotel} data downloaded
             */
            @Override
            public void onResults(List<Hotel> results) {
                //Update the Hotels to be shown
                updateHotels(results);

                //Hide progress indicator
                mHotelListView.hideProgressIndicator();
            }

            /**
             * Method invoked when the results could not be obtained for the data requested
             * due to some error.
             *
             * @param messageId The String resource of the error message
             * @param args      Variable number of arguments to replace the format specifiers
             */
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                //Hide progress indicator
                mHotelListView.hideProgressIndicator();

                //Show the error message
                mHotelListView.showError(messageId, args);
            }
        });

    }

    /**
     * Method that updates the list of {@link Hotel} data to be shown, to the View.
     *
     * @param hotels The List of {@link Hotel} data to be shown by the View.
     */
    @Override
    public void updateHotels(List<Hotel> hotels) {
        if (hotels != null && hotels.size() > 0) {
            //When we have the list of Hotels

            //Submitting the list of Hotels to the View
            mHotelListView.loadHotels(hotels);

            //Updating the boolean to TRUE, to prevent further downloads of the same data
            mHotelsLoadedBoolean.set(true);
        }
    }

    /**
     * Method invoked when the User clicks on the Refresh Menu button
     * shown by the {@link MainActivity}
     */
    @Override
    public void onRefreshMenuClicked() {
        loadHotels();
    }

    /**
     * Method invoked when the user clicks on the Item View itself. This should launch
     * the {@code webUrl} link if any.
     *
     * @param webUrl String containing the URL of the Web Page to be
     *               launched in a browser application
     */
    @Override
    public void openLink(String webUrl) {
        if (TextUtils.isEmpty(webUrl)) {
            //When we do not have the URL, show an error message
            mHotelListView.showNoLinkError();
        } else {
            //When we have the URL, delegate to the View to launch the Web page
            mHotelListView.launchWebLink(webUrl);
        }
    }

    /**
     * Method invoked when the user clicks on the Map ImageButton or the Location Info
     * TextView of the Item View. This should launch any Map application
     * passing in the {@code location} information.
     *
     * @param location String containing the Location information to be sent to a Map application.
     */
    @Override
    public void openLocation(String location) {
        //Delegating to the View to launch the Map application for the location address
        mHotelListView.launchMapLocation(location);
    }

    /**
     * Method invoked when the user clicks on the Call ImageButton or the Contact Info
     * TextView of the Item View. This should launch any Dialer application
     * passing in the Contact Number {@code contactNumber}.
     *
     * @param contactNumber String containing the Contact Number information to be sent
     *                      to a Dialer application.
     */
    @Override
    public void openContact(String contactNumber) {
        //Delegating to the View to launch the Phone Dialer
        mHotelListView.dialNumber(contactNumber);
    }

    /**
     * Method invoked when the user clicks and holds on to the Contact Info TextView
     * of the Item View. This should launch a Share Intent passing
     * in the Contact Number.
     *
     * @param contactNumber String containing the Contact Number information to be
     *                      shared via an Intent.
     */
    @Override
    public void shareContact(String contactNumber) {
        if (!TextUtils.isEmpty(contactNumber)) {
            //Delegating to the View to share the Contact information when available
            mHotelListView.launchShareContact(contactNumber);
        }
    }

    /**
     * Method invoked when the user clicks and holds on to the Location Info
     * TextView of the Item View. This should launch a Share Intent passing
     * in the location information.
     *
     * @param location String containing the Location information to be shared via an Intent.
     */
    @Override
    public void shareLocation(String location) {
        //Delegating to the View to share the location information
        mHotelListView.launchShareLocation(location);
    }


}
