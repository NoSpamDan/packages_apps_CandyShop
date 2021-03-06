/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.candy.candyshop.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.TintTypedArray;
import com.google.android.material.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;

public class BottomNavigationViewCustom extends FrameLayout {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private static final int[] DISABLED_STATE_SET = {-android.R.attr.state_enabled};

    private final MenuBuilder mMenu;
    private final BottomNavigationMenuCustom mMenuView;
    private final BottomNavigationPresenter mPresenter = new BottomNavigationPresenter();
    private MenuInflater mMenuInflater;

    private OnNavigationItemSelectedListener mListener;

    public BottomNavigationViewCustom(Context context) {
        this(context, null);
    }

    public BottomNavigationViewCustom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationViewCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Create the menu
        mMenu = new BottomNavigationMenu(context);

        mMenuView = new BottomNavigationMenuCustom(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mMenuView.setLayoutParams(params);

        mPresenter.setBottomNavigationMenuView(mMenuView);
        mMenuView.setPresenter(mPresenter);
        mMenu.addMenuPresenter(mPresenter);
        mPresenter.initForMenu(getContext(), mMenu);

        // Custom attributes
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.BottomNavigationView, defStyleAttr,
                R.style.Widget_Design_BottomNavigationView);

        if (a.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            mMenuView.setIconTintList(
                    a.getColorStateList(R.styleable.BottomNavigationView_itemIconTint));
        } else {
            mMenuView.setIconTintList(
                    createDefaultColorStateList(android.R.attr.textColorSecondary));
        }
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            mMenuView.setItemTextColor(
                    a.getColorStateList(R.styleable.BottomNavigationView_itemTextColor));
        } else {
            mMenuView.setItemTextColor(
                    createDefaultColorStateList(android.R.attr.textColorSecondary));
        }
        if (a.hasValue(R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    R.styleable.BottomNavigationView_elevation, 0));
        }

        int itemBackground = a.getResourceId(R.styleable.BottomNavigationView_itemBackground, 0);
        mMenuView.setItemBackgroundRes(itemBackground);

        if (a.hasValue(R.styleable.BottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.BottomNavigationView_menu, 0));
        }
        a.recycle();

        addView(mMenuView, params);
        if (Build.VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context);
        }

        mMenu.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                return mListener != null && !mListener.onNavigationItemSelected(item);
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });
    }

    /**
     * Set a listener that will be notified when a bottom navigation item is selected.
     *
     * @param listener The listener to notify
     */
    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Returns the {@link Menu} instance associated with this bottom navigation bar.
     */
    @NonNull
    public Menu getMenu() {
        return mMenu;
    }

    /**
     * Inflate a menu resource into this navigation view.
     *
     * <p>Existing items in the menu will not be modified or removed.</p>
     *
     * @param resId ID of a menu resource to inflate
     */
    public void inflateMenu(int resId) {
        mPresenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, mMenu);
        mPresenter.setUpdateSuspended(false);
        mPresenter.updateMenuView(true);
    }

    /**
     * @return The maximum number of items that can be shown in BottomNavigationView.
     */
    public int getMaxItemCount() {
        return BottomNavigationMenu.MAX_ITEM_COUNT;
    }

    /**
     * Returns the tint which is applied to our menu items' icons.
     *
     * @see #setItemIconTintList(ColorStateList)
     *
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    @Nullable
    public ColorStateList getItemIconTintList() {
        return mMenuView.getIconTintList();
    }

    /**
     * Set the tint which is applied to our menu items' icons.
     *
     * @param tint the tint to apply.
     *
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    public void setItemIconTintList(@Nullable ColorStateList tint) {
        mMenuView.setIconTintList(tint);
    }

    /**
     * Returns colors used for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @see #setItemTextColor(ColorStateList)
     *
     * @return the ColorStateList of colors used for the different states of the menu items text.
     *
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     */
    @Nullable
    public ColorStateList getItemTextColor() {
        return mMenuView.getItemTextColor();
    }

    /**
     * Set the colors to use for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @see #getItemTextColor()
     *
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     */
    public void setItemTextColor(@Nullable ColorStateList textColor) {
        mMenuView.setItemTextColor(textColor);
    }

    /**
     * Returns the background resource of the menu items.
     *
     * @see #setItemBackgroundResource(int)
     *
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    @DrawableRes
    public int getItemBackgroundResource() {
        return mMenuView.getItemBackgroundRes();
    }

    /**
     * Set the background of our menu items to the given resource.
     *
     * @param resId The identifier of the resource.
     *
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    public void setItemBackgroundResource(@DrawableRes int resId) {
        mMenuView.setItemBackgroundRes(resId);
    }

    /**
     * Listener for handling events on bottom navigation items.
     */
    public interface OnNavigationItemSelectedListener {

        /**
         * Called when an item in the bottom navigation menu is selected.
         *
         * @param item The selected item
         *
         * @return true to display the item as the selected item and false if the item should not
         *         be selected. Consider setting non-selectable items as disabled preemptively to
         *         make them appear non-interactive.
         */
        boolean onNavigationItemSelected(@NonNull MenuItem item);
    }

    private void addCompatibilityTopDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(
                ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color));
        FrameLayout.LayoutParams dividerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(
                        R.dimen.design_bottom_navigation_shadow_height));
        divider.setLayoutParams(dividerParams);
        addView(divider);
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SupportMenuInflater(getContext());
        }
        return mMenuInflater;
    }

    private ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        final TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(
                getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(
                androidx.appcompat.R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{
                DISABLED_STATE_SET,
                CHECKED_STATE_SET,
                EMPTY_STATE_SET
        }, new int[]{
                baseColor.getColorForState(DISABLED_STATE_SET, defaultColor),
                colorPrimary,
                defaultColor
        });
    }
}
