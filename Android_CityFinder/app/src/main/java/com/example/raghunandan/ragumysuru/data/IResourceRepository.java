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

package com.example.raghunandan.ragumysuru.data;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.raghunandan.ragumysuru.data.local.models.Hotel;
import com.example.raghunandan.ragumysuru.data.local.models.Park;
import com.example.raghunandan.ragumysuru.data.local.models.Place;
import com.example.raghunandan.ragumysuru.data.local.models.Restaurant;
import com.example.raghunandan.ragumysuru.data.local.models.Shop;

import java.util.List;


public interface IResourceRepository {

    /**
     * Method that retrieves the List of {@link Place} data for the Place list entries
     * tracked by the {@code arrayResId}.
     *
     * @param arrayResId       The Integer Id of the Array resource that tracks the
     *                         Place list entries to be shown.
     * @param resourceCallback The Callback to be implemented by the caller to receive the result.
     */
    void getPlaceListEntries(@ArrayRes int arrayResId, @NonNull GetResourceCallback<List<Place>> resourceCallback);

    /**
     * Method that retrieves the List of {@link Park} data for the Park list entries
     * tracked by the {@code arrayResId}.
     *
     * @param arrayResId       The Integer Id of the Array resource that tracks the
     *                         Park list entries to be shown.
     * @param resourceCallback The Callback to be implemented by the caller to receive the result.
     */
    void getParkListEntries(@ArrayRes int arrayResId, @NonNull GetResourceCallback<List<Park>> resourceCallback);

    /**
     * Method that retrieves the List of {@link Hotel} data for the Hotel list entries
     * tracked by the {@code arrayResId}.
     *
     * @param arrayResId       The Integer Id of the Array resource that tracks the
     *                         Hotel list entries to be shown.
     * @param resourceCallback The Callback to be implemented by the caller to receive the result.
     */
    void getHotelListEntries(@ArrayRes int arrayResId, @NonNull GetResourceCallback<List<Hotel>> resourceCallback);

    /**
     * Method that retrieves the List of {@link Restaurant} data for the Restaurant list entries
     * tracked by the {@code arrayResId}.
     *
     * @param arrayResId       The Integer Id of the Array resource that tracks the
     *                         Restaurant list entries to be shown.
     * @param resourceCallback The Callback to be implemented by the caller to receive the result.
     */
    void getRestaurantListEntries(@ArrayRes int arrayResId, @NonNull GetResourceCallback<List<Restaurant>> resourceCallback);

    /**
     * Method that retrieves the List of {@link Shop} data for the Shop list entries
     * tracked by the {@code arrayResId}.
     *
     * @param arrayResId       The Integer Id of the Array resource that tracks the
     *                         Shop list entries to be shown.
     * @param resourceCallback The Callback to be implemented by the caller to receive the result.
     */
    void getShopListEntries(@ArrayRes int arrayResId, @NonNull GetResourceCallback<List<Shop>> resourceCallback);

    /**
     * Callback interface for Resource data requests.
     *
     * @param <T> The type of the results expected for the data requested.
     */
    interface GetResourceCallback<T> {
        /**
         * Method invoked when the {@code results} are obtained for the data requested.
         *
         * @param results The {@code results} in the generic type passed.
         */
        void onResults(T results);

        /**
         * Method invoked when the results could not be obtained for the data requested
         * due to some error.
         *
         * @param messageId The String resource of the error message
         * @param args      Variable number of arguments to replace the format specifiers
         *                  in the String resource if any
         */
        void onFailure(@StringRes int messageId, @Nullable Object... args);
    }
}
