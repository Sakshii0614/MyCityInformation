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


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.raghunandan.ragumysuru.R;
import com.example.raghunandan.ragumysuru.data.local.models.Shop;
import com.example.raghunandan.ragumysuru.ui.BasePresenter;
import com.example.raghunandan.ragumysuru.ui.BaseView;
import com.example.raghunandan.ragumysuru.ui.common.ListItemSpacingDecoration;
import com.example.raghunandan.ragumysuru.utils.ColorUtility;
import com.example.raghunandan.ragumysuru.utils.IntentUtility;
import com.example.raghunandan.ragumysuru.utils.SnackbarUtility;
import com.example.raghunandan.ragumysuru.workers.ImageDecoderFragment;

import java.util.ArrayList;
import java.util.List;


public class ShopListFragment extends Fragment
        implements ShopListContract.View, SwipeRefreshLayout.OnRefreshListener {

    //Constant used for logs
    private static final String LOG_TAG = ShopListFragment.class.getSimpleName();

    //Bundle constants for persisting the data throughout System config changes
    private static final String BUNDLE_SHOPS_LIST_KEY = "ShopList.Shops";

    //The Presenter interface for this View
    private ShopListContract.Presenter mPresenter;

    //References to the Views shown in this Fragment
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewContentList;

    //Adapter of the RecyclerView
    private ShopListAdapter mAdapter;

    /**
     * Mandatory Empty Constructor of {@link ShopListFragment}.
     * This is required by the {@link android.support.v4.app.FragmentManager} to instantiate
     * the fragment (e.g. upon screen orientation changes).
     */
    public ShopListFragment() {
    }

    /**
     * Static Factory constructor that creates an instance of {@link ShopListFragment}
     *
     * @return Instance of {@link ShopListFragment}
     */
    @NonNull
    public static ShopListFragment newInstance() {
        return new ShopListFragment();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Returns the View for the fragment's UI ('R.layout.layout_main_content_page')
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout 'R.layout.layout_main_content_page'
        //Passing false as we are attaching the layout ourselves
        View rootView = inflater.inflate(R.layout.layout_main_content_page, container, false);

        //Finding the Views
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_content_page);
        mRecyclerViewContentList = rootView.findViewById(R.id.recyclerview_content_page);

        //Initialize SwipeRefreshLayout
        setupSwipeRefresh();

        //Initialize RecyclerView
        setupRecyclerView();

        //Setting the background color for the root view
        rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.shopListBackgroundColor));

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Method that initializes the SwipeRefreshLayout 'R.id.swipe_refresh_content_page'
     * and its listener
     */
    private void setupSwipeRefresh() {
        //Registering the refresh listener which triggers a new load on swipe to refresh
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //Configuring the Colors for Swipe Refresh Progress Indicator
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtility.obtainColorsFromTypedArray(requireContext(), R.array.swipe_refresh_colors, R.color.colorPrimary));
    }

    /**
     * Method that initializes a RecyclerView with its Adapter for loading and displaying the list of Shops.
     */
    private void setupRecyclerView() {
        //Creating a Vertical Linear Layout Manager with the default layout order
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        //Setting the Layout Manager to use
        mRecyclerViewContentList.setLayoutManager(linearLayoutManager);

        //Initializing the Adapter for the RecyclerView
        mAdapter = new ShopListAdapter(requireContext(), new ShopListItemUserActionsListener());

        //Setting the Adapter for RecyclerView
        mRecyclerViewContentList.setAdapter(mAdapter);

        //Retrieving the Item spacing to use
        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.all_list_items_spacing);

        //Setting Item offsets using Item Decoration
        mRecyclerViewContentList.addItemDecoration(new ListItemSpacingDecoration(itemSpacing, itemSpacing));
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //On Subsequent launch

            //Restoring the list of Shops
            mPresenter.updateShops(savedInstanceState.getParcelableArrayList(BUNDLE_SHOPS_LIST_KEY));
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Saving the State
        outState.putParcelableArrayList(BUNDLE_SHOPS_LIST_KEY, mAdapter.getShopList());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        //Start loading the Shops
        mPresenter.start();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        //Dispatching the event to the Presenter to reload the data
        mPresenter.onRefreshMenuClicked();
    }

    /**
     * Method that registers the Presenter {@code presenter} with the View implementing {@link BaseView}
     *
     * @param presenter Presenter instance implementing the {@link BasePresenter}
     */
    @Override
    public void setPresenter(ShopListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Method that displays the Progress indicator
     */
    @Override
    public void showProgressIndicator() {
        //Enabling the Swipe to Refresh if disabled prior to showing the Progress indicator
        if (!mSwipeRefreshLayout.isEnabled()) {
            mSwipeRefreshLayout.setEnabled(true);
        }
        //Displaying the Progress Indicator only when not already shown
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * Method that hides the Progress indicator
     */
    @Override
    public void hideProgressIndicator() {
        //Hiding the Progress indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Method that updates the RecyclerView's Adapter with new {@code shopList} data.
     *
     * @param shopList List of {@link Shop}s loaded from the Resources.
     */
    @Override
    public void loadShops(List<Shop> shopList) {
        //Submitting the Shops data to the Adapter to load
        mAdapter.submitList(shopList);
    }

    /**
     * Method invoked when an error is encountered during information
     * retrieval process.
     *
     * @param messageId String Resource of the error Message to be displayed
     * @param args      Variable number of arguments to replace the format specifiers
     */
    @Override
    public void showError(int messageId, @Nullable Object... args) {
        if (getView() != null) {
            //When we have the root view

            //Evaluating the message to be shown
            String messageToBeShown;
            if (args != null && args.length > 0) {
                //For the String Resource with args
                messageToBeShown = getString(messageId, args);
            } else {
                //For the String Resource without args
                messageToBeShown = getString(messageId);
            }

            if (!TextUtils.isEmpty(messageToBeShown)) {
                //Displaying the Snackbar message of indefinite time length
                //when we have the error message to be shown

                new SnackbarUtility(Snackbar.make(getView(), messageToBeShown, Snackbar.LENGTH_INDEFINITE))
                        .revealCompleteMessage() //Removes the limit on max lines
                        .setDismissAction(R.string.snackbar_action_ok) //For the Dismiss "OK" action
                        .showSnack();
            }
        }
    }

    /**
     * Method invoked when there is no Web Page URL of the {@link Shop} item being clicked.
     */
    @Override
    public void showNoLinkError() {
        showError(R.string.error_all_no_link);
    }

    /**
     * Method invoked when the user clicks on the Item View itself. This should launch
     * a browser application for the URL {@code webUrl} of the Web Page passed.
     *
     * @param webUrl String containing the URL of the Web Page
     */
    @Override
    public void launchWebLink(String webUrl) {
        //Launching the Browser application for the Web Page URL passed
        IntentUtility.openLink(requireContext(), webUrl);
    }

    /**
     * Method invoked when the user clicks on the Map ImageButton or the Location Info
     * TextView of the Item View. This should launch any Map application
     * passing in the {@code location} information.
     *
     * @param location String containing the Location information to be sent to a Map application.
     */
    @Override
    public void launchMapLocation(String location) {
        //Launching the Map application for the location information passed
        IntentUtility.openMap(requireContext(), location);
    }

    /**
     * Method invoked when the user clicks and holds on to the Location Info TextView of the Item View.
     * This should launch a Share Intent passing in the location information.
     *
     * @param location String containing the Location information to be shared via an Intent.
     */
    @Override
    public void launchShareLocation(String location) {
        //Launching the Share Intent to share the location text
        IntentUtility.shareText(requireActivity(), location, getString(R.string.chooser_title_all_location));
    }

    /**
     * {@link ListAdapter} class for the RecyclerView to load the list of Shops to be displayed.
     */
    private static class ShopListAdapter extends ListAdapter<Shop, ShopListAdapter.ViewHolder> {

        //Payload Constants used to rebind the "Expanded/Collapsed" state of the list items for the position stored here
        private static final String PAYLOAD_EXPAND_CARD = "Payload.Expand.ItemPosition";
        private static final String PAYLOAD_COLLAPSE_CARD = "Payload.Collapse.ItemPosition";
        /**
         * {@link DiffUtil.ItemCallback} for calculating the difference between two {@link Shop} objects
         */
        private static DiffUtil.ItemCallback<Shop> DIFF_SHOPS
                = new DiffUtil.ItemCallback<Shop>() {
            /**
             * Called to check whether two objects represent the same item.
             * <p>
             * For example, if your items have unique ids, this method should check their id equality.
             *
             * @param oldItem The item in the old list.
             * @param newItem The item in the new list.
             * @return True if the two items represent the same object or false if they are different.
             *
             * @see DiffUtil.Callback#areItemsTheSame(int, int)
             */
            @Override
            public boolean areItemsTheSame(Shop oldItem, Shop newItem) {
                //Returning the comparison of the Shop's Id
                return oldItem.getId() == newItem.getId();
            }

            /**
             * Called to check whether two items have the same data.
             * <p>
             * This information is used to detect if the contents of an item have changed.
             * <p>
             * This method to check equality instead of {@link Object#equals(Object)} so that you can
             * change its behavior depending on your UI.
             * <p>
             * For example, if you are using DiffUtil with a
             * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapter}, you should
             * return whether the items' visual representations are the same.
             * <p>
             * This method is called only if {@link #areItemsTheSame(Shop, Shop)} returns {@code true} for
             * these items.
             *
             * @param oldItem The item in the old list.
             * @param newItem The item in the new list.
             * @return True if the contents of the items are the same or false if they are different.
             *
             * @see DiffUtil.Callback#areContentsTheSame(int, int)
             */
            @Override
            public boolean areContentsTheSame(Shop oldItem, Shop newItem) {
                //Returning the comparison of Name
                return oldItem.getName().equals(newItem.getName());
            }
        };
        //Context for reading resources
        private Context mContext;
        //Listener for the User actions on the Shop List Items
        private ShopListUserActionsListener mActionsListener;
        //Stores the Item Position of the Last expanded card
        private int mLastExpandedItemPosition = RecyclerView.NO_POSITION;
        //The Data of this Adapter that stores a list of Shops to be displayed
        private ArrayList<Shop> mShopList;

        /**
         * Constructor of {@link ShopListAdapter}
         *
         * @param context             A {@link Context} for reading resources
         * @param userActionsListener Instance of {@link ShopListUserActionsListener}
         */
        ShopListAdapter(Context context, ShopListUserActionsListener userActionsListener) {
            super(DIFF_SHOPS);
            mContext = context;
            //Registering the User Actions Listener
            mActionsListener = userActionsListener;
        }

        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         */
        @NonNull
        @Override
        public ShopListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Inflating the item layout 'R.layout.item_shop_list'
            //Passing False since we are attaching the layout ourselves
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_list, parent, false);
            //Returning the Instance of ViewHolder for the inflated Item View
            return new ViewHolder(itemView);
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull ShopListAdapter.ViewHolder holder, int position) {
            //Get the data at the position
            Shop shop = getItem(position);

            //Bind the Views with the data at the position
            holder.bind(position, shop);

            //When we have an Item View that was last expanded
            if (mLastExpandedItemPosition > RecyclerView.NO_POSITION) {
                if (mLastExpandedItemPosition == position) {
                    //Ensures that the Item View remains expanded when being reused
                    holder.expandItemView();
                } else {
                    //Ensures that the Item View remains collapsed when being reused
                    holder.collapseItemView();
                }
            }
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method
         * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
         * the given position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Partial bind vs full bind:
         * <p>
         * The payloads parameter is a merge list from {@link #notifyItemChanged(int, Object)} or
         * {@link #notifyItemRangeChanged(int, int, Object)}.  If the payloads list is not empty,
         * the ViewHolder is currently bound to old data and Adapter may run an efficient partial
         * update using the payload info.  If the payload is empty,  Adapter must run a full bind.
         * Adapter should not assume that the payload passed in notify methods will be received by
         * onBindViewHolder().  For example when the view is not attached to the screen, the
         * payload in notifyItemChange() will be simply dropped.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         * @param payloads A non-null list of merged payloads. Can be empty list if requires full
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                //Propagating to super when there are no payloads
                super.onBindViewHolder(holder, position, payloads);
            } else {
                //When we have a payload for partial update

                //Get the Payload bundle
                Bundle bundle = (Bundle) payloads.get(0);
                //Iterate over the bundle keys
                for (String keyStr : bundle.keySet()) {
                    switch (keyStr) {
                        case PAYLOAD_EXPAND_CARD:
                            //For the Item to be expanded

                            //Get the position from the bundle
                            int itemPositionToExpand = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (itemPositionToExpand > RecyclerView.NO_POSITION
                                    && itemPositionToExpand == mLastExpandedItemPosition) {
                                //When the position is for the Item to be expanded, expand the Item View
                                holder.expandItemView();
                            }
                            break;
                        case PAYLOAD_COLLAPSE_CARD:
                            //For the Item to be collapsed

                            //Get the position from the bundle
                            int itemPositionToCollapse = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (itemPositionToCollapse > RecyclerView.NO_POSITION) {
                                //When the Item View Position is valid, collapse the Item View
                                holder.collapseItemView();
                            }
                            break;
                    }
                }
            }
        }

        /**
         * Submits a new list to be diffed, and displayed.
         * <p>
         * If a list is already being displayed, a diff will be computed on a background thread, which
         * will dispatch Adapter.notifyItem events on the main thread.
         *
         * @param list The new list to be displayed.
         */
        @Override
        public void submitList(List<Shop> list) {
            //Preparing the list to store a copy of the Adapter data
            if (mShopList == null) {
                //Initializing the list when not initialized
                mShopList = new ArrayList<>();
            } else if (mShopList.size() > 0) {
                //Clearing the list if it has content already
                mShopList.clear();
            }
            //Adding all the contents of the list submitted
            mShopList.addAll(list);
            //Propagating the list to super
            super.submitList(list);
        }

        /**
         * Getter Method for the data of this Adapter.
         *
         * @return List of {@link Shop} shown by the Adapter
         */
        ArrayList<Shop> getShopList() {
            return mShopList;
        }

        /**
         * Method that updates/changes the {@link Shop} Item to be expanded.
         *
         * @param position The Position of the {@link Shop} Item to be expanded.
         */
        void changeItemExpanded(int position) {
            //Collapse any previously expanded card
            if (mLastExpandedItemPosition > RecyclerView.NO_POSITION) {
                setItemCollapsed(mLastExpandedItemPosition);
            }
            //Creating a Bundle to do a partial update for expanding the card
            Bundle payloadBundle = new Bundle(1);
            //Put the position of the item into the bundle for update
            payloadBundle.putInt(PAYLOAD_EXPAND_CARD, position);
            //Store the position of the item being expanded for tracking
            mLastExpandedItemPosition = position;
            //Notify the state change at the item position, to expandItemView
            notifyItemChanged(position, payloadBundle);
        }

        /**
         * Method that updates the {@link Shop} Item to be collapsed.
         *
         * @param position The Position of the {@link Shop} Item to be collapsed.
         */
        void setItemCollapsed(int position) {
            //Creating a Bundle to do a partial update for collapsing the card
            Bundle payloadBundle = new Bundle(1);
            //Put the position of the item into the bundle for update
            payloadBundle.putInt(PAYLOAD_COLLAPSE_CARD, position);
            //Notify the state change at the item position, to collapse
            notifyItemChanged(position, payloadBundle);
            //Reset the last expanded position if the same item is being collapsed
            if (mLastExpandedItemPosition == position) {
                mLastExpandedItemPosition = RecyclerView.NO_POSITION;
            }
        }

        /**
         * ViewHolder class for caching View components of the template item view 'R.layout.item_shop_list'
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            //References to the Views required, in the Item View
            private TextView mTextViewShopName;
            private TextView mTextViewShopRating;
            private RatingBar mRatingBar;
            private TextView mTextViewShopTypes;
            private ImageView mImageViewShopPhoto;
            private TextView mTextViewShopTimings;
            private TextView mTextViewShopLocation;
            private Button mButtonExpandCollapse;
            private ImageButton mImageButtonLocation;
            private Group mGroupShopLocation;
            private View mViewTopRibbon;

            /**
             * Constructor of {@link ViewHolder}
             *
             * @param itemView Inflated Instance of the Item View 'R.layout.item_shop_list'
             */
            ViewHolder(View itemView) {
                super(itemView);

                //Finding the Views needed
                mTextViewShopName = itemView.findViewById(R.id.text_shop_list_item_title);
                mTextViewShopRating = itemView.findViewById(R.id.text_shop_list_item_rating_value);
                mRatingBar = itemView.findViewById(R.id.ratingbar_shop_list_item_rating);
                mTextViewShopTypes = itemView.findViewById(R.id.text_shop_list_item_type);
                mImageViewShopPhoto = itemView.findViewById(R.id.image_shop_list_item_photo);
                mTextViewShopTimings = itemView.findViewById(R.id.text_shop_list_item_timings);
                mTextViewShopLocation = itemView.findViewById(R.id.text_shop_list_item_location);
                mButtonExpandCollapse = itemView.findViewById(R.id.btn_shop_list_item_expand_collapse);
                mImageButtonLocation = itemView.findViewById(R.id.imgbtn_shop_list_item_location);
                mGroupShopLocation = itemView.findViewById(R.id.group_shop_list_item_location);
                mViewTopRibbon = itemView.findViewById(R.id.view_shop_list_item_top_ribbon);

                //Registering the Click listeners on the required views
                mButtonExpandCollapse.setOnClickListener(this);
                mImageButtonLocation.setOnClickListener(this);
                mTextViewShopLocation.setOnClickListener(this);
                mTextViewShopLocation.setOnLongClickListener(this);
                itemView.setOnClickListener(this);
            }

            /**
             * Method that binds the views with the data {@code shop} at the position
             *
             * @param position The position of the Item in the list
             * @param shop     The {@link Shop} data at the Item position
             */
            void bind(int position, Shop shop) {
                //Bind the Shop Name
                mTextViewShopName.setText(shop.getName());
                //Bind the Shop Rating Value
                mTextViewShopRating.setText(String.valueOf(shop.getRating()));
                //Bind the RatingBar with the Rating Value
                mRatingBar.setRating(shop.getRating());
                //Bind the Shop Types
                mTextViewShopTypes.setText(shop.getShopTypes());
                //Bind the Shop Photo if available
                ImageDecoderFragment.newInstance(((FragmentActivity) mContext).getSupportFragmentManager(), position)
                        .executeAndUpdate(mImageViewShopPhoto, shop.getPhotoResId());
                //Bind the Shop Timings
                mTextViewShopTimings.setText(shop.getTimings());
                //Bind the Location Info
                mTextViewShopLocation.setText(shop.getLocation());
                //Bind the Colors using the Palette if derived
                if (shop.isSwatchGenerated()) {
                    //Use the Palette Colors when we have them
                    mViewTopRibbon.setBackgroundColor(shop.getSwatchRgbColor());
                    mTextViewShopName.setTextColor(shop.getSwatchTitleTextColor());
                    mButtonExpandCollapse.setTextColor(shop.getSwatchRgbColor());
                } else {
                    //When we do not have any Palette, use the default colors
                    int colorPrimary = ContextCompat.getColor(mContext, R.color.colorPrimary);
                    mViewTopRibbon.setBackgroundColor(colorPrimary);
                    TextViewCompat.setTextAppearance(mTextViewShopName, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Title_Inverse);
                    mButtonExpandCollapse.setTextColor(colorPrimary);
                }
            }

            /**
             * Called when a view has been clicked.
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                //Checking if the adapter position is valid
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    //When the adapter position is valid

                    //Get the data at the position
                    Shop shop = getItem(adapterPosition);

                    //Get the View Id clicked
                    int clickedViewId = view.getId();

                    //Taking action based on the view clicked
                    if (clickedViewId == itemView.getId()) {
                        //When the entire Item View is clicked

                        //Dispatch the event to the action listener
                        mActionsListener.onOpenLink(adapterPosition, shop);

                    } else if (clickedViewId == mButtonExpandCollapse.getId()) {
                        //When the "Expand/Collapse" button is clicked

                        if (mLastExpandedItemPosition == adapterPosition) {
                            //When the same Item View was previously expanded, collapse the Item View
                            setItemCollapsed(adapterPosition);
                        } else {
                            //Else, expand the Item View
                            changeItemExpanded(adapterPosition);
                        }

                    } else if (clickedViewId == mImageButtonLocation.getId()
                            || clickedViewId == mTextViewShopLocation.getId()) {
                        //When the Location ImageButton or the Location Info is clicked

                        //Dispatch the event to the action listener
                        mActionsListener.onOpenLocation(adapterPosition, shop);
                    }
                }
            }

            /**
             * Called when a view has been clicked and held.
             *
             * @param view The view that was clicked and held.
             * @return true if the callback consumed the long click, false otherwise.
             */
            @Override
            public boolean onLongClick(View view) {
                //Checking if the adapter position is valid
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    //Get the data at the position
                    Shop shop = getItem(adapterPosition);

                    //Get the View Id clicked
                    int clickedViewId = view.getId();

                    //Taking action based on the view clicked
                    if (clickedViewId == mTextViewShopLocation.getId()) {
                        //When the Location Info is long pressed

                        //Dispatch the event to the action listener
                        mActionsListener.onLocationLongClicked(adapterPosition, shop);

                        //Returning True to indicate the event was consumed
                        return true;
                    }

                }
                //Returning False when no event was consumed
                return false;
            }

            /**
             * Method that expands the Item View by revealing the Location Info of the Shop.
             */
            void expandItemView() {
                //Setting the Button Text to Collapse
                mButtonExpandCollapse.setText(R.string.action_collapse);
                //Hiding the ImageButton for Location
                mImageButtonLocation.setVisibility(View.GONE);
                //Revealing the Location Info
                mGroupShopLocation.setVisibility(View.VISIBLE);
            }

            /**
             * Method that collapses the Item View by hiding the Location Info of the Shop.
             */
            void collapseItemView() {
                //Setting the Button Text to Expand
                mButtonExpandCollapse.setText(R.string.action_expand);
                //Revealing the ImageButton for Location
                mImageButtonLocation.setVisibility(View.VISIBLE);
                //Hiding the Location Info
                mGroupShopLocation.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Listener that implements {@link ShopListUserActionsListener} to receive
     * event callbacks for User actions on RecyclerView list of Shops.
     */
    private class ShopListItemUserActionsListener implements ShopListUserActionsListener {

        /**
         * Callback Method of {@link ShopListUserActionsListener} invoked when
         * the user clicks on the Item View itself. This should launch
         * the website link if any.
         *
         * @param itemPosition The adapter position of the Item clicked
         * @param shop         The {@link Shop} associated with the Item clicked
         */
        @Override
        public void onOpenLink(int itemPosition, Shop shop) {
            //Delegating to the Presenter to handle the event
            mPresenter.openLink(shop.getWebsite());
        }

        /**
         * Callback Method of {@link ShopListUserActionsListener} invoked when
         * the user clicks on the Map ImageButton or the Location Info TextView of the Item View.
         * This should launch any Map application passing in the location information.
         *
         * @param itemPosition The adapter position of the Item clicked
         * @param shop         The {@link Shop} associated with the Item clicked
         */
        @Override
        public void onOpenLocation(int itemPosition, Shop shop) {
            //Delegating to the Presenter to handle the event
            mPresenter.openLocation(shop.getLocation());
        }

        /**
         * Callback Method of {@link ShopListUserActionsListener} invoked when
         * the user clicks and holds on to the Location Info TextView of the Item View. This should
         * launch a Share Intent passing in the location information.
         *
         * @param itemPosition The adapter position of the Item clicked and held
         * @param shop         The {@link Shop} associated with the Item clicked and held
         */
        @Override
        public void onLocationLongClicked(int itemPosition, Shop shop) {
            //Delegating to the Presenter to handle the event
            mPresenter.shareLocation(shop.getLocation());
        }
    }
}
