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

package com.example.raghunandan.ragumysuru.ui.shops;

import com.example.raghunandan.ragumysuru.data.local.models.Shop;


public interface ShopListUserActionsListener {

    /**
     * Callback Method of {@link ShopListUserActionsListener} invoked when
     * the user clicks on the Item View itself. This should launch
     * the website link if any.
     *
     * @param itemPosition The adapter position of the Item clicked
     * @param shop         The {@link Shop} associated with the Item clicked
     */
    void onOpenLink(final int itemPosition, Shop shop);

    /**
     * Callback Method of {@link ShopListUserActionsListener} invoked when
     * the user clicks on the Map ImageButton or the Location Info TextView of the Item View.
     * This should launch any Map application passing in the location information.
     *
     * @param itemPosition The adapter position of the Item clicked
     * @param shop         The {@link Shop} associated with the Item clicked
     */
    void onOpenLocation(final int itemPosition, Shop shop);

    /**
     * Callback Method of {@link ShopListUserActionsListener} invoked when
     * the user clicks and holds on to the Location Info TextView of the Item View. This should
     * launch a Share Intent passing in the location information.
     *
     * @param itemPosition The adapter position of the Item clicked and held
     * @param shop         The {@link Shop} associated with the Item clicked and held
     */
    void onLocationLongClicked(final int itemPosition, Shop shop);
}
