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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import mma.mtake.wheel.component.R;
import wheel.component.genview.GenWheelText;
import wheel.component.genview.GenWheelView;
import wheel.component.genview.WheelGeneralAdapter;

/**
 * Separate two category of String Array presented current time </br> from the
 * selecting action in order to format the date in different style.</br></br>
 * Therefore, whenever you want to change the date style, </br>you should find
 * those String array named with "ForDisplay" in the ending </br>to revise
 * whatever you want.</br></br>
 * 
 * @author JosephWang
 * 
 */
public class WheelSinglePicker extends LinearLayout {
	public static final String TAG = WheelSinglePicker.class.getSimpleName();
	private Context act;
	private WheelView wheelSingle;
	private String[] data;
	private String[] dataForDisplay;
	private int index = 0;
	private int viewID;

	private GenWheelView genView;

	private boolean isScrollFinish = true;

	public boolean isScrollFinish() {
		return isScrollFinish;
	}

	private WheelSingleSyncListener wheelListener;

	public int getIndex() {
		return index;
	}

	private String currSelect;

	public String getCurrSelect() {
		return currSelect;
	}

	private String currSelectForDisPlay;

	public void setAllData(String[] data, String[] dataForDisplay) {
		index = 0;
		if (wheelSingle != null) {
			wheelSingle.setCurrentItem(0);
		}
		this.data = data;
		this.dataForDisplay = dataForDisplay;
		setUpData();
		new Thread(setTodayThread).start();
	}

	private int singleTextSize = 24;

	public int getSingleTextSize() {
		return singleTextSize;
	}

	public void setSingleTextSize(int singleTextSize) {
		this.singleTextSize = singleTextSize;
	}

	private int visibleLine = 1;

	public int getVisibleLine() {
		return visibleLine;
	}

	public void setVisibleLine(int visibleLine) {
		this.visibleLine = visibleLine;
	}

	public WheelSinglePicker(Context context) {
		super(context);
		act = context;
		initStyle(null);
		initDatePicker();
		setUpData();
		new Thread(setTodayThread).start();
	}

	public WheelSinglePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		act = context;
		initStyle(attrs);
		initDatePicker();
		setUpData();
		new Thread(setTodayThread).start();
	}

	private void initStyle(AttributeSet attrs) {
		int[] linerarLayoutAttrs = { android.R.attr.orientation };
		TypedArray array = act.obtainStyledAttributes(attrs, linerarLayoutAttrs);
		array.recycle();
		array = act.obtainStyledAttributes(attrs, R.styleable.WheelSinglePickerStyle);
		/** 如果滾輪樣式是純粹顯示文字，滾輪的顯示行數 預設為一行 **/
		visibleLine = array.getInteger(R.styleable.WheelSinglePickerStyle_visibleLine, 1);
		/** 如果滾輪樣式是純粹顯示文字，滾輪的文字大小 格式為dip **/
		singleTextSize = array.getInteger(R.styleable.WheelSinglePickerStyle_singleTextSize, 22);
		array.recycle();
	}

	public void setViewGenerator(GenWheelView genView) {
		this.genView = genView;
		setUpData();
		new Thread(setTodayThread).start();
	}

	public void initDatePicker() {
		LinearLayout wheel = (LinearLayout) LayoutInflater.from(act).inflate(R.layout.single_picker_wheel, this, true);
		wheelSingle = (WheelView) wheel.findViewById(R.id.wheel_single);
		wheelSingle.addScrollingListener(scrollListener);
		getCurrSelectForDisplay();
	}

	private void setUpData() {
		if (dataForDisplay != null && dataForDisplay.length > 0) {
			setWheelListener(wheelSingle, dataForDisplay);
			currSelectForDisPlay = dataForDisplay[0];
			currSelect = data[0];
			getCurrSelectForDisplay();
			getSelectData();
		}
	}

	public void getSelectData() {
		index = wheelSingle.getCurrentItem();
		currSelect = data[index];
		currSelectForDisPlay = dataForDisplay[index];
		if (wheelListener != null) {
			wheelListener.handleOrignal(viewID, currSelect);
			if (dataForDisplay != null && dataForDisplay.length > 0 && index < dataForDisplay.length) {
				wheelListener.handleDisplayString(viewID, currSelectForDisPlay);
			}
		}
	}

	private void setWheelListener(WheelView wheelView, String[] data) {
		if (genView == null) {
			genView = new GenWheelText(getVisibleLine(), getSingleTextSize());
		}
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(act, genView);
		viewAdapter.setData(data);
		wheelView.setViewAdapter(viewAdapter);
		wheelView.addChangingListener(changedListener);
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			prepareData(newValue);
			getCurrSelectForDisplay();
		}
	};

	private void prepareData(int newValue) {
		if (data != null && data.length > 0 && dataForDisplay != null && dataForDisplay.length > 0) {
			currSelect = data[newValue];
			currSelectForDisPlay = dataForDisplay[newValue];
		}
		index = newValue;
	}

	public String getCurrSelectForDisplay() {
		return currSelectForDisPlay;
	}

	private Runnable setTodayThread = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
				setTodayHandler.sendMessage(setTodayHandler.obtainMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static interface WheelSingleSyncListener {
		public void handleOrignal(int viewID, String data);

		public void handleDisplayString(int viewID, String displayString);
	}

	private Handler setTodayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			wheelSingle.setCurrentItem(0, false);
		}
	};

	public void setWheelListener(int viewID, WheelSingleSyncListener wheelListener) {
		this.viewID = viewID;
		this.wheelListener = wheelListener;
	}

	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
			isScrollFinish = false;
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			isScrollFinish = true;
			index = wheel.getCurrentItem();
			getSelectData();
		}
	};
}
