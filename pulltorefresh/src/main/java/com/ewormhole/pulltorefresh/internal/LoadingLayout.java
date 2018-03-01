/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ewormhole.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ewormhole.pulltorefresh.ILoadingLayout;
import com.ewormhole.pulltorefresh.PullToRefreshBase.Mode;
import com.ewormhole.pulltorefresh.PullToRefreshBase.Orientation;
import com.ewormhole.pulltorefresh.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements
		ILoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private FrameLayout mInnerLayout;

	protected final ImageView mHeaderImage;
	protected final ProgressBar mHeaderProgress;

	private boolean mUseIntrinsicAnimation;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

	private ImageView mImgGoods;

	private ImageView mImgPerson;

	private int widthP;

	private int heightP;
	private boolean p;

	private AnimationDrawable animP;

	private float heightG;

	private float widthG;

	public LoadingLayout(Context context, final Mode mode,
			final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;

		switch (scrollDirection) {
		case HORIZONTAL:
			LayoutInflater.from(context).inflate(
					R.layout.pull_to_refresh_header_horizontal, this);
			break;
		case VERTICAL:
		default:
			LayoutInflater.from(context).inflate(
					R.layout.pull_to_refresh_header_vertical, this);
			break;
		}

		mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
		mHeaderText = (TextView) mInnerLayout
				.findViewById(R.id.pull_to_refresh_text);
		mHeaderProgress = (ProgressBar) mInnerLayout
				.findViewById(R.id.pull_to_refresh_progress);
		mSubHeaderText = (TextView) mInnerLayout
				.findViewById(R.id.pull_to_refresh_sub_text);
		mHeaderImage = (ImageView) mInnerLayout
				.findViewById(R.id.pull_to_refresh_image);

		mImgGoods = (ImageView) mInnerLayout.findViewById(R.id.imageView1);
		mImgPerson = (ImageView) mInnerLayout.findViewById(R.id.imageView2);

		LayoutParams lp = (LayoutParams) mInnerLayout
				.getLayoutParams();

		switch (mode) {
		case PULL_FROM_END:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP
					: Gravity.LEFT;

			// Load in labels
			mPullLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_pull_label);
			mRefreshingLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_refreshing_label);
			mReleaseLabel = context
					.getString(R.string.pull_to_refresh_from_bottom_release_label);
			break;

		case PULL_FROM_START:
		default:
			lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM
					: Gravity.RIGHT;

			// Load in labels
			mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
			mRefreshingLabel = context
					.getString(R.string.pull_to_refresh_refreshing_label);
			mReleaseLabel = context
					.getString(R.string.pull_to_refresh_release_label);
			break;
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs
					.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				ViewCompat.setBackground(this, background);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance,
					styleID);
			setTextAppearance(styleID.data);
		}
		if (attrs
				.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(
					R.styleable.PullToRefresh_ptrSubHeaderTextAppearance,
					styleID);
			setSubTextAppearance(styleID.data);
		}

		// Text Color attrs need to be set after TextAppearance attrs
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
			ColorStateList colors = attrs
					.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
			if (null != colors) {
				setTextColor(colors);
			}
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
			ColorStateList colors = attrs
					.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
			if (null != colors) {
				setSubTextColor(colors);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs
					.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
		case PULL_FROM_START:
		default:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
			} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
				Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
			}
			break;

		case PULL_FROM_END:
			if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
			} else if (attrs
					.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
				Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
				imageDrawable = attrs
						.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
			}
			break;
		}

		// If we don't have a user defined drawable, load the default
		if (null == imageDrawable) {
			imageDrawable = context.getResources().getDrawable(
					getDefaultDrawableResId());
		}

		// Set Drawable, and save width/height
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
		case HORIZONTAL:
			return mInnerLayout.getWidth();
		case VERTICAL:
		default:
			if (!p) {
				// ��ȡ�ߴ�
				widthP = mImgPerson.getWidth();
				heightP = mImgPerson.getHeight();
				widthG = mImgGoods.getWidth();
				heightG = mImgGoods.getHeight();
				Log.e("widthP=", "" + widthP);
				Log.e("heightP=", "" + heightP);
				p = true;
			}
			return mInnerLayout.getHeight();
		}
	}

	public final void hideAllViews() {
		if (View.VISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mHeaderImage.getVisibility()) {
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
		if (View.VISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.INVISIBLE);
		}
	}

	public final void onPull(float scaleOfLayout) {
		float i = scaleOfLayout;
		float j = scaleOfLayout;
		Log.e("onPull", "scaleOfLayout=" + scaleOfLayout);
		if (!mUseIntrinsicAnimation) {
			onPullImpl(scaleOfLayout);
		}
		if (mImgGoods.getVisibility() != View.VISIBLE) {
			mImgGoods.setVisibility(View.VISIBLE);
		}
		if (i > 1) {
			i = 1;
		}
		//͸���ȶ���
		ObjectAnimator animeAlphaP = ObjectAnimator.ofFloat(mImgPerson, "alpha", -1, 1).setDuration(300);
		animeAlphaP.setCurrentPlayTime((long) (i * 300));
		ObjectAnimator animeAlphaG = ObjectAnimator.ofFloat(mImgGoods, "alpha", -1, 1).setDuration(300);
		animeAlphaG.setCurrentPlayTime((long) (i * 300));

		//���Ŷ���
		ViewHelper.setPivotX(mImgPerson, 0);
        ViewHelper.setPivotY(mImgPerson, 0);
        ObjectAnimator animePX = ObjectAnimator.ofFloat(mImgPerson, "scaleX", 0, 1).setDuration(300);
		animePX.setCurrentPlayTime((long) (i * 300));
		ObjectAnimator animePY = ObjectAnimator.ofFloat(mImgPerson, "scaleY", 0, 1).setDuration(300);
		animePY.setCurrentPlayTime((long) (i * 300));
//		android.widget.RelativeLayout.LayoutParams paramsP = (android.widget.RelativeLayout.LayoutParams) mImgPerson
//				.getLayoutParams();
//		paramsP.height = (int) (heightP * scaleOfLayout);
//		paramsP.width = (int) (widthP * scaleOfLayout);
//		Log.e("onPull", "params.height=" + paramsP.height + "params.width"
//				+ paramsP.width);
//		mImgPerson.setLayoutParams(paramsP);
		if (j > 0.8) {
			j = 0.8f;
		}

		ObjectAnimator animeGX = ObjectAnimator.ofFloat(mImgGoods, "scaleX", 0, 1).setDuration(300);
		animeGX.setCurrentPlayTime((long) (j * 300 * 1.25));
		ObjectAnimator animeGY = ObjectAnimator.ofFloat(mImgGoods, "scaleY", 0, 1).setDuration(300);
		animeGY.setCurrentPlayTime((long) (j * 300 * 1.25));
//		android.widget.RelativeLayout.LayoutParams paramsG = (android.widget.RelativeLayout.LayoutParams) mImgGoods
//				.getLayoutParams();
//		paramsG.height = (int) (heightG * scaleOfLayout * 1.25);
//		paramsG.width = (int) (widthG * scaleOfLayout * 1.25);
//		mImgGoods.setLayoutParams(paramsG);
	}

	public final void pullToRefresh() {
		if (null != mSubHeaderText) {
			mSubHeaderText.setText(mPullLabel);
		}

		// Now call the callback
		pullToRefreshImpl();
	}

	public final void refreshing() {
		if (null != mSubHeaderText) {
			mSubHeaderText.setText(mRefreshingLabel);
		}

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).start();
		} else {
			// Now call the callback
			refreshingImpl();
		}

		if (animP == null) {
			mImgPerson.setImageResource(R.drawable.refreshing_anim);
//			mImgPerson.setImageDrawable(getResources().getDrawable(
//					R.drawable.refreshing_anim));
			animP = (AnimationDrawable) mImgPerson.getDrawable();
		}
		animP.start();
		if (mImgGoods.getVisibility() == View.VISIBLE) {
			mImgGoods.setVisibility(View.INVISIBLE);
		}
	}

	public final void releaseToRefresh() {
		if (null != mSubHeaderText) {
			mSubHeaderText.setText(mReleaseLabel);
		}
		// Now call the callback
		releaseToRefreshImpl();
	}

	public final void reset() {

		// mHeaderImage.setVisibility(View.VISIBLE);

		if (mUseIntrinsicAnimation) {
			((AnimationDrawable) mHeaderImage.getDrawable()).stop();
		} else {
			// Now call the callback
			resetImpl();
		}

		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(mSubHeaderText.getText())) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setVisibility(View.VISIBLE);
			}
		}
		if (animP != null) {
			animP.stop();
			animP = null;
		}
//		mImgPerson.setImageResource(R.drawable.app_refresh_people_0);
//		mImgPerson.setImageDrawable(getResources().getDrawable(
//				R.drawable.app_refresh_people_0));
		if (mImgGoods.getVisibility() == View.VISIBLE) {
			mImgGoods.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable
		mHeaderImage.setImageDrawable(imageDrawable);
		mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable);

		// Now call the callback
		onLoadingDrawableSet(imageDrawable);
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	public final void showInvisibleViews() {
		if (View.INVISIBLE == mHeaderText.getVisibility()) {
			mHeaderText.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
			mHeaderProgress.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mHeaderImage.getVisibility()) {
			// mHeaderImage.setVisibility(View.VISIBLE);
		}
		if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();

	protected abstract void refreshingImpl();

	protected abstract void releaseToRefreshImpl();

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);

				// Only set it to Visible if we're GONE, otherwise VISIBLE will
				// be set soon
				if (View.GONE == mSubHeaderText.getVisibility()) {
					mSubHeaderText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setSubTextAppearance(int value) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setSubTextColor(ColorStateList color) {
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

	private void setTextAppearance(int value) {
		if (null != mHeaderText) {
			mHeaderText.setTextAppearance(getContext(), value);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextAppearance(getContext(), value);
		}
	}

	private void setTextColor(ColorStateList color) {
		if (null != mHeaderText) {
			mHeaderText.setTextColor(color);
		}
		if (null != mSubHeaderText) {
			mSubHeaderText.setTextColor(color);
		}
	}

}
