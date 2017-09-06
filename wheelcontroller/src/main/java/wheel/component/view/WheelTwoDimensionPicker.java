/*
 * Copyright (C) 2010 The Android Open Source Project
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
package wheel.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import mma.mtake.wheel.component.R;
import wheel.component.genview.GenWheelText;
import wheel.component.genview.GenWheelView;
import wheel.component.genview.WheelGeneralAdapter;

/**
 * The two dimension WheelView embedded in layout could delivery the current
 * selected String to any kind View synchronously. Designing two Data Structure
 * to letting right wheel to be binded with left wheel.</br> left wheel-->
 * String Array</br> right wheel--> {@link HashMap} </br> The data in
 * right wheel is associated with the data in left wheel, Therefore, whenever
 * user scroll the left wheel that the right wheel will prepare the data array
 * through the current selecting data in left wheel. That means if the data in
 * left wheel is not associated with the data in right wheel,and the original
 * mechanism will hide the right wheel dynamically. You could use the logics
 * whatever you put the String Array to this HashMap as values,and keeping the
 * one of the left String Array is connected with the key of this HashMap to
 * find the right data.</br></br> Separate two category of String Array
 * presented current time </br> from the selecting action in order to format the
 * date in different style.</br></br> Therefore, whenever you want to change the
 * date style, </br>you should find those String array named with "ForDisplay"
 * in the ending </br>to revise whatever you want to be.</br></br>
 * 
 * @author JosephWang
 * 
 *         <pre class="prettyprint">
 * WheelTwoDimensionPicker everyYearPicker = (WheelTwoDimensionPicker) findViewById(R.id.every_special_date_picker);
 * everyYearPicker.setWheelHandler(everyYearPicker, wheelHandler);
 * WheelTwoDimensionSyncListener wheelListener = new WheelTwoDimensionSyncListener() {
 * 
 * 	&#064;Override
 * 	public void handleOrignal(int viewID, String[] data) {
 * 
 * 	}
 * 
 * 	&#064;Override
 * 	public void handleDisplayString(int viewID, String[] displayString) {
 * 
 * 	}
 * };
 * 
 * everyYearPicker.setWheelListener(everyYearPicker.getId(), wheelListener);
 * 
 * 
 * </pre>
 */

public class WheelTwoDimensionPicker extends LinearLayout {
	public static final String TAG = WheelTwoDimensionPicker.class.getSimpleName();
	private final static int animationTime = 200;
	private View triggerView;
	private Context context;
	private TextView select_data;
	private WheelView left;
	private WheelView right;

	private String[] leftData = { "" };
	private String[] leftDataForDisplay = { "" };

	private ArrayMap<String, String[]> rightData = new ArrayMap<String, String[]>();
	private ArrayMap<String, String[]> rightDataForDisplay = new ArrayMap<String, String[]>();
	private String[] dateArray;
	private String[] dateArrayForDisplay;

	private int indexLeft = 0;
	private int indexRight = 0;

	private String currSelectLeft;

	public String getCurrSelectLeft() {
		return currSelectLeft;
	}

	private String currSelectRight;

	public String getCurrSelectRight() {
		return currSelectRight;
	}

	private String currSelectLeftForDisplay;
	private String currSelectRightForDisplay;
	private ViewGroup select_data_container;
	private GenWheelView leftGenView;
	private GenWheelView rightGenView;

	private int textSize = 22;

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	private int leftWheelVisibleLine = 1;

	public int getLeftWheelVisibleLine() {
		return leftWheelVisibleLine;
	}

	public void setLeftWheelVisibleLine(int leftWheelVisibleLine) {
		this.leftWheelVisibleLine = leftWheelVisibleLine;
	}

	private int rightWheelVisibleLine = 1;

	public int getRightWheelVisibleLine() {
		return rightWheelVisibleLine;
	}

	public void setRightWheelVisibleLine(int rightWheelVisibleLine) {
		this.rightWheelVisibleLine = rightWheelVisibleLine;
	}

	private int viewID;
	private WheelTwoControlListener wheelListener;

	private boolean currentSelectVisible = false;

	public boolean isCurrentSelectVisible() {
		return currentSelectVisible;
	}

	public void setCurrentSelectVisible(boolean currentSelectVisible) {
		this.currentSelectVisible = currentSelectVisible;
		if (select_data_container != null) {
			if (this.currentSelectVisible) {
				select_data_container.setVisibility(View.VISIBLE);
			} else {
				select_data_container.setVisibility(View.GONE);
			}
		}
	}

	private boolean alwaysShowTwoWheel = false;

	public boolean isAlwaysShowTwoWheel() {
		return alwaysShowTwoWheel;
	}

	public void setAlwaysShowTwoWheel(boolean alwaysShowTwoWheel) {
		this.alwaysShowTwoWheel = alwaysShowTwoWheel;
	}

	public void setViewGenerator(GenWheelView genView) {
		setViewGenerator(genView, genView);
	}

	public void setViewGenerator(GenWheelView leftGenView, GenWheelView rightGenView) {
		this.leftGenView = leftGenView;
		this.rightGenView = rightGenView;
		setUpData();
		new Thread(setTodayThread).start();
	}

	public void setAllData(String[] leftData, ArrayMap<String, String[]> rightData) {
		setAllData(leftData, leftData, rightData, rightData);
	}

	public void setAllData(String[] leftData, String[] leftDataForDisplay, ArrayMap<String, String[]> rightData, ArrayMap<String, String[]> rightDataForDisplay) {
		indexLeft = 0;
		indexRight = 0;
		this.leftData = leftData;
		this.leftDataForDisplay = leftDataForDisplay;
		this.rightData = rightData;
		this.rightDataForDisplay = rightDataForDisplay;
		if (left != null) {
			left.setCurrentItem(0);
		} else if (right != null) {
			right.setCurrentItem(0);
		}
		setUpData();
		if ((this.rightData.get(this.leftData[0])) != null) {
			right.setVisibility(View.VISIBLE);
		}
		new Thread(setTodayThread).start();
	}

	public WheelTwoDimensionPicker(Context context) {
		super(context);
		this.context = context;
		initStyle(null);
		initData();
		initDatePicker();
		new Thread(setTodayThread).start();
	}

	public WheelTwoDimensionPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initStyle(attrs);
		initData();
		initDatePicker();
		new Thread(setTodayThread).start();
	}

	public void scrollWheel(int leftIndex, int rightIndex) {
		if (leftData == null || leftIndex >= leftData.length) {
			indexLeft = 0;
			indexRight = 0;
			left.setCurrentItem(0, false);
			right.setCurrentItem(0, false);
		} else {
			left.setCurrentItem(leftIndex, false);
		}
		if (rightData == null ||
			rightData.get(leftData[left.getCurrentItem()]) == null || 
			rightIndex >= rightData.get(leftData[left.getCurrentItem()]).length) {
			indexRight = 0;
			right.setCurrentItem(0, false);
		} else {
			right.setCurrentItem(rightIndex, false);
		}
	}

	public void scrollWheel(int leftIndex, int rightIndex, boolean animated) {
		if (leftData == null || leftIndex >= leftData.length) {
			indexLeft = 0;
			indexRight = 0;
			left.setCurrentItem(0, animated);
			right.setCurrentItem(0, animated);
		} else {
			left.setCurrentItem(leftIndex, animated);
		}
		if (rightData == null ||
			rightData.get(left.getCurrentItem()) == null || 
			rightIndex >= rightData.get(left.getCurrentItem()).length) {
			indexRight = 0;
			right.setCurrentItem(0, animated);
		} else {
			right.setCurrentItem(rightIndex, animated);
		}
	}
	private void initStyle(AttributeSet attrs) {
		int[] linerarLayoutAttrs = { android.R.attr.orientation };
		TypedArray array = context.obtainStyledAttributes(attrs, linerarLayoutAttrs);
		array.recycle();
		array = context.obtainStyledAttributes(attrs, R.styleable.WheelTwoDimensionPickerStyle);
		/** 是否總是顯示兩個滾輪 **/
		alwaysShowTwoWheel = array.getBoolean(R.styleable.WheelTwoDimensionPickerStyle_alwaysShowTwoWheel, false);
		/** 如果滾輪樣式是純粹顯示文字，左邊滾輪的顯示行數 預設為一行 **/
		leftWheelVisibleLine = array.getInteger(R.styleable.WheelTwoDimensionPickerStyle_leftWheelVisibleLine, 1);
		/** 如果滾輪樣式是純粹顯示文字，右邊滾輪的顯示行數 預設為一行 **/
		rightWheelVisibleLine = array.getInteger(R.styleable.WheelTwoDimensionPickerStyle_rightWheelVisibleLine, 1);
		/** 如果滾輪樣式是純粹顯示文字，兩邊滾輪的文字大小 格式為dip **/
		textSize = array.getInteger(R.styleable.WheelTwoDimensionPickerStyle_wheelTwoDimensionTextSize, 22);
		/** 是否即時顯示所選資料 **/
		currentSelectVisible = array.getBoolean(R.styleable.WheelTwoDimensionPickerStyle_isCurrentSelectVisible, false);
		array.recycle();
	}

	private void initData() {
		rightData.clear();
		rightDataForDisplay.clear();
		for (int i = 0; i < leftData.length; i++) {
			rightData.put(leftData[i], dateArray);
			rightDataForDisplay.put(leftData[i], dateArrayForDisplay);
		}
		initDatePicker();
		setUpData();
	}

	public void initDatePicker() {
		LinearLayout wheel = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.two_dimensional_picker_wheel, this, true);
		select_data_container = (ViewGroup) wheel.findViewById(R.id.select_data_container);
		select_data = (TextView) wheel.findViewById(R.id.select_data);
		left = (WheelView) wheel.findViewById(R.id.left);
		right = (WheelView) wheel.findViewById(R.id.right);
		left.addScrollingListener(scrollListener);
		right.addScrollingListener(scrollListener);

		currSelectLeft = leftData[0];
		currSelectLeftForDisplay = leftDataForDisplay[0];
		if ((rightData.get(leftData[0])) != null) {
			currSelectRight = rightData.get(leftData[0])[0];
			currSelectRightForDisplay = rightDataForDisplay.get(leftData[0])[0];
		}
		sendDataObject();
		select_data.setText(currSelectLeftForDisplay + " " + currSelectRightForDisplay);
	}

	private void setUpData() {
		if (leftDataForDisplay != null && leftDataForDisplay.length > 0 && rightData != null && rightDataForDisplay != null && rightDataForDisplay.get(leftData[0]) != null) {
			setWheeViewlListener(left, leftDataForDisplay);
			setWheeViewlListener(right, rightDataForDisplay.get(leftData[0]));
		}
	}

	private TwoWheelSelectData currSelectTotal = new TwoWheelSelectData();

	public TwoWheelSelectData getCurrSelectTotal() {
		return currSelectTotal;
	}

	private void adjustRightWheel() {
		if (isAlwaysShowTwoWheel()) {
			right.setVisibility(View.VISIBLE);
		} else {
			right.setVisibility(View.GONE);
		}
	}

	private void sendDataObject() {
		if (left.getCurrentItem() >= leftData.length) {
			left.setCurrentItem(0);
		}

		currSelectTotal.leftData = leftData[left.getCurrentItem()];
		currSelectTotal.leftDataDisplay = leftDataForDisplay[left.getCurrentItem()];
		if (right.getVisibility() == View.VISIBLE) {
			if (rightData != null && rightData.get(currSelectTotal.leftData) != null) {
				right.setVisibility(View.VISIBLE);
				currSelectTotal.rightData = rightData.get(currSelectTotal.leftData)[right.getCurrentItem()];
				if (right.getCurrentItem() >= rightDataForDisplay.get(currSelectTotal.leftData).length) {
					right.setCurrentItem(0);
				}
				currSelectTotal.rightDataDisplay = rightDataForDisplay.get(currSelectTotal.leftData)[right.getCurrentItem()];
				indexRight = right.getCurrentItem();
			} else {
				adjustRightWheel();
				indexRight = 0;
				currSelectTotal.rightData = "";
				currSelectTotal.rightDataDisplay = "";
			}
		} else {
			indexRight = 0;
			currSelectTotal.rightData = "";
			currSelectTotal.rightDataDisplay = "";
		}
		int currentViewId = 0;
		if (triggerView != null) {
			currentViewId = triggerView.getId();
		} else {
			currentViewId = viewID;
		}

		if (wheelListener != null) {
			wheelListener.handleSelect(currentViewId, currSelectTotal, left.getCurrentItem(), indexRight);
		}
	}

	private GenWheelText generalText;

	private void setWheeViewlListener(WheelView wheelView, String[] data) {
		if (leftGenView != null && rightGenView != null) {
			if (wheelView.getId() == R.id.left) {
				WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, leftGenView);
				viewAdapter.setData(data);
				left.setViewAdapter(viewAdapter);
			} else if (wheelView.getId() == R.id.right) {
				WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, rightGenView);
				viewAdapter.setData(data);
				right.setViewAdapter(viewAdapter);
			}
		} else {
			if (wheelView.getId() == R.id.left) {
				generalText = new GenWheelText(getLeftWheelVisibleLine(), getTextSize());
			} else if (wheelView.getId() == R.id.right) {
				generalText = new GenWheelText(getRightWheelVisibleLine(), getTextSize());
			}
			WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, generalText);
			viewAdapter.setData(data);
			wheelView.setViewAdapter(viewAdapter);
		}
		wheelView.addChangingListener(changedListener);
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel.getId() == R.id.left) {
				prepareLeftData();
				initRightData(prepareRightData());
			} else if (wheel.getId() == R.id.right) {
				checkRightData();
				setUprightData();
			}
			checkRightData();
		}
	};

	private void checkRightData() {
		/***
		 * Using "currSelectLeft" as the clue to find out whatever "indexRight"
		 * is bigger than the size of "rightData" or not.
		 ****/
		/****************** 已左邊滾輪的現在選的資料,去找右邊滾輪的資料集合,看 index有沒有超過右邊的滾輪的資料長度 ********************/
		if (rightData.get(currSelectLeft) != null && (rightData.get(currSelectLeft)).length > 0) {
			right.setVisibility(View.VISIBLE);
			if (right.getCurrentItem() > rightData.get(leftData[left.getCurrentItem()]).length) {
				right.setCurrentItem(right.getViewAdapter().getItemsCount() - 1, false);
			} else if (indexRight > right.getViewAdapter().getItemsCount() - 1) {
				right.setCurrentItem(right.getViewAdapter().getItemsCount() - 1, false);
			}
		} else {
			if (isAlwaysShowTwoWheel()) {
				right.setVisibility(View.VISIBLE);
			} else {
				if (right.getViewAdapter() != null && right.getViewAdapter().getItemsCount() > 0) {
					right.setVisibility(View.VISIBLE);
				} else {
					right.setVisibility(View.GONE);
				}
			}
		}
	}

	public void backToZeroIndex() {
		if (left != null && right != null) {
			left.setCurrentItem(0);
			right.setCurrentItem(0);
		}
	}

	private void initRightData(String[] data) {
		if (data != null) {
			setWheeViewlListener(right, data);
		}
	};

	private void prepareLeftData() {
		if (leftData != null && leftData.length > 0 && leftDataForDisplay != null && leftDataForDisplay.length > 0) {
			currSelectLeft = leftData[left.getCurrentItem()];
			currSelectLeftForDisplay = leftDataForDisplay[left.getCurrentItem()];
		}
		indexLeft = left.getCurrentItem();
		checkRightData();
	}

	private String[] prepareRightData() {
		String[] data = null;
		if (rightData != null && rightData.get(leftData[left.getCurrentItem()]) != null) {
			data = rightDataForDisplay.get(leftData[left.getCurrentItem()]);
		}
		return data;
	}

	private void setUprightData() {
		if (rightData.get(currSelectLeft) != null && rightDataForDisplay.get(currSelectLeft) != null) {
			if (rightData.get(leftData[left.getCurrentItem()]).length < right.getCurrentItem()) {
				right.setCurrentItem(rightData.get(leftData[left.getCurrentItem()]).length - 1, false);
			}
			currSelectRight = rightData.get(leftData[left.getCurrentItem()])[right.getCurrentItem()];
			currSelectRightForDisplay = rightDataForDisplay.get(leftData[left.getCurrentItem()])[right.getCurrentItem()];
			indexRight = right.getCurrentItem();
		} else {
			indexRight = 0;
		}
	}

	public int getIndexLeft() {
		return indexLeft;
	}

	public int getIndexRight() {
		return indexRight;
	}

	private Runnable setTodayThread = new Runnable() {
		@Override
		public void run() {
			try {
				setTodayHandler.sendMessageDelayed(setTodayHandler.obtainMessage(), 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler setTodayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			left.scroll(0, animationTime);
			right.scroll(0, animationTime);
			sendDataObject();
		}
	};

	public void setWheelListener(int viewID, WheelTwoControlListener wheelListener) {
		this.viewID = viewID;
		this.wheelListener = wheelListener;
	}

	/**
	 * 目前所選資料 如果是文字的話，所選的資料結構
	 * 
	 * String leftData ---> 左邊滾輪所選資料 String leftDataDisplay ---> 左邊滾輪所選呈現資料
	 * String rightData; ---> 右邊邊滾輪所選資料 String rightDataDisplay ---> 右邊滾輪所選呈現資料
	 * 
	 * @author josephWang
	 * 
	 */
	public static class TwoWheelSelectData {
		public String leftData;
		public String leftDataDisplay;
		public String rightData;
		public String rightDataDisplay;
	}

	private boolean isScrollFinish = true;

	public boolean isScrollFinish() {
		return isScrollFinish;
	}

	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
			isScrollFinish = false;
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			isScrollFinish = true;
			indexLeft = left.getCurrentItem();
			indexRight = right.getCurrentItem();
			if (isScrollFinish) {
				sendDataObject();
				currSelectLeftForDisplay = leftDataForDisplay[left.getCurrentItem()];
				if (right.getVisibility() == View.VISIBLE) {
					if (rightData != null && rightData.get(currSelectTotal.leftData) != null) {
						currSelectRightForDisplay = rightDataForDisplay.get(currSelectTotal.leftData)[right.getCurrentItem()];
					} else {
						currSelectRightForDisplay = "";
					}
				} else {
					currSelectRightForDisplay = "";
				}
				select_data.setText(currSelectLeftForDisplay + " " + currSelectRightForDisplay);
			}
		}
	};
}
