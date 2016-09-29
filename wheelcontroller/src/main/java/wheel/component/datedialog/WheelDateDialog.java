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
package wheel.component.datedialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import mma.mtake.wheel.component.R;
import wheel.component.controller.OnShowWheelListener;
import wheel.component.genview.GenWheelText;
import wheel.component.genview.WheelGeneralAdapter;
import wheel.component.utils.UIAdjuster;
import wheel.component.utils.WheelUtility;
import wheel.component.view.WheelControlListener;

/**
 * A convenient class to show the DatePicker in the "Wheel" style,and also it is
 * based on the reason to imitate IPhone's style. The design of concept contains
 * that you could embed this WheelDatePicker in any kind View with bottom
 * position. This means whenever you trigger the WheelDatePicker through
 * {@link #WheelDatePicker.setWheelListener(View eachView, String title)}.
 * 
 * 
 * @author josephWang
 * 
 *         <pre class="prettyprint">
 * TextView title = (TextView) findViewById(R.id.calendar_view_title);
 * WheelDatePicker datePicker = new WheelDatePicker(context, out_container, datePickerHandler);
 * datePicker.setWheelListener(title, &quot;&quot;);
 * 
 * private Handler datePickerHandler = new Handler() {
 * 	&#064;Override
 * 	public void handleMessage(Message msg) {
 * 		switch (msg.what) {
 * 		case R.id.calendar_view_title:// 
 * 			Date selectDate = (Date) msg.obj;
 * 			...do something....
 * 			break;
 * 		}
 * 		datePicker.dismissDatePicker();
 * 	}
 * };
 * </pre>
 */
public class WheelDateDialog implements OnKeyListener {
	public static final String TAG = WheelDateDialog.class.getSimpleName();
	private Dialog dialog;
	private WheelControlListener<Date> controllerListenr;
	private Context act;
	private WheelView year;
	private WheelView month;
	private View wheel;
	private Message wheelMsg = new Message();
	private int yearRange = 10;

	public void setYearRange(int yearRange) {
		this.yearRange = yearRange;
		setYearAndMonthArray();
	}

	private String[] yearArray = new String[yearRange];
	private String[] monthArray = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

	private String[] yearArrayForDisplay = new String[10];
	private String[] monthArrayForDisplay;

	private int indexYear;
	private int indexMonth;

	private String currSelectYear;
	private String currSelectMonth;

	public WheelDateDialog(Context act, WheelControlListener<Date> controllerListenr) {
		this.act = act;
		this.controllerListenr = controllerListenr;
		monthArrayForDisplay = act.getResources().getStringArray(R.array.month);
		initWheel();
		setYearAndMonthArray();
		initDailog();
	}

	public WheelDateDialog(Context act, WheelControlListener<Date> controllerListenr, boolean isShowChinesemonth) {
		this.act = act;
		this.controllerListenr = controllerListenr;
		initWheel();
		if (isShowChinesemonth) {
			monthArrayForDisplay = act.getResources().getStringArray(R.array.month);
		} else {
			monthArrayForDisplay = monthArray;
		}
		setYearAndMonthArray(isShowChinesemonth);
		initDailog();
	}

	// 顯示 西元年的位數
	public WheelDateDialog(Activity act, WheelControlListener<Date> controllerListenr, int year_yards) {
		this.act = act;
		this.controllerListenr = controllerListenr;
		monthArrayForDisplay = monthArray;
		setYearAndMonthArray(year_yards);
		initDailog();
	}

	private void initDailog() {
		if (dialog == null) {
			dialog = new Dialog(this.act, R.style.DialogSlideAnim);
		}
	}

	public String getCurrSelectYear() {
		return currSelectYear;
	}

	public String getCurrSelectMonth() {
		return currSelectMonth;
	}

	public String getSelectDateString() {
		String result = "";
		if (currSelectYear.length() > 0 && currSelectMonth.length() > 0) {
			if (UIAdjuster.getLanguage(act)) {
				result = currSelectMonth + "月" + currSelectYear + "年";
			} else {
				result = currSelectMonth + currSelectYear;
			}
		}
		return result;
	}

	private void setYearAndMonthArray() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		yearArray = new String[yearRange];
		for (int i = 0; i < 10; i++) {
			yearArray[i] = String.valueOf(year + i);
			yearArrayForDisplay[i] = String.valueOf(year + i) + (UIAdjuster.getLanguage(act) ? "年" : "");
		}
	}

	private void setYearAndMonthArray(boolean bisshowChineseYear) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		yearArray = new String[yearRange];
		for (int i = 0; i < 10; i++) {
			yearArray[i] = String.valueOf(year + i);
			if (bisshowChineseYear) {
				yearArrayForDisplay[i] = String.valueOf(year + i) + (UIAdjuster.getLanguage(act) ? "年" : "");
			} else {
				yearArrayForDisplay[i] = String.valueOf(year + i).substring(2);
			}
		}
	}

	private void setYearAndMonthArray(int year_yards) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		yearArray = new String[yearRange];
		for (int i = 0; i < 10; i++) {
			yearArray[i] = String.valueOf(year + i);
			yearArrayForDisplay[i] = String.valueOf(year + i).substring(4 - year_yards);
		}
	}

	public void setWheelListener(View eachView, String title) {
		eachView.setOnClickListener(getWheelClickListener(title));
	}

	private OnShowWheelListener showWheelListener = new OnShowWheelListener() {
		@Override
		public boolean showWheel(View v) {
			return true;
		}
	};

	public OnShowWheelListener getOnShowWheelListener() {
		return showWheelListener;
	}

	/**
	 * 增加直接設定 OnShowWheelListener
	 * 
	 * @param showWheelListener
	 */
	public void setOnShowWheelListener(OnShowWheelListener showWheelListener) {
		this.showWheelListener = showWheelListener;
	}

	private View.OnClickListener getWheelClickListener(final String title) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIAdjuster.closeKeyBoard(act);
				if (showWheelListener.showWheel(v)) {
					indexYear = 0;
					indexMonth = 0;

					currSelectYear = yearArray[indexYear];
					currSelectMonth = monthArray[indexMonth];

					wheelMsg.what = v.getId();
					wheelMsg.obj = getDate(yearArray[indexYear], monthArray[indexMonth]);
					showDatePicker(v, title);
				}
			}
		};
	}

	private void initWheel() {
		if (wheel == null) {
			wheel = (View) LayoutInflater.from(act).inflate(R.layout.date_picker_wheel, null);
			year = (WheelView) wheel.findViewById(R.id.year);
			month = (WheelView) wheel.findViewById(R.id.month);
			wheel.findViewById(R.id.ok).setOnClickListener(wheelButtonListener);
			wheel.findViewById(R.id.cancel).setOnClickListener(wheelButtonListener);
			year.addScrollingListener(scrollListener);
			month.addScrollingListener(scrollListener);
		}
	}

	/**
	 * 
	 * @param layout
	 * @param wheel
	 * @param title
	 * @param show
	 * @param handler
	 * @return {@link WheelView}
	 */
	public void showDatePicker(View trigger, String title) {
		setWheelListener(year, yearArrayForDisplay);
		setWheelListener(month, monthArrayForDisplay);

		indexYear = year.getCurrentItem();
		indexMonth = month.getCurrentItem();

		currSelectYear = yearArray[indexYear];
		currSelectMonth = monthArray[indexMonth];

		wheelMsg.obj = getDate(yearArray[indexYear], monthArray[indexMonth]);

		if (null != title) {
			((TextView) wheel.findViewById(R.id.title)).setText(title);
		}

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.copyFrom(dialog.getWindow().getAttributes());
		switch (WheelUtility.getScreenOrientation(trigger.getContext()))
		{
			case Configuration.ORIENTATION_PORTRAIT:
				params.width = WindowManager.LayoutParams.MATCH_PARENT;
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				params.width = (int) UIAdjuster.computeDIPtoPixel(trigger.getContext(), 320);
				break;
		}

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.BOTTOM;
		params.y = (int) UIAdjuster.computeDIPtoPixel(act, getBottomMargin());
		dialog.getWindow().setAttributes(params);
		dialog.setContentView(wheel);
		dialog.show();
	}

	private int bottomMargin = 60;

	public int getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(int forMargin) {
		this.bottomMargin = forMargin;
	}

	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private View.OnClickListener wheelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ok) {
				if (isScrollFinish()) {
					controllerListenr.handleClick(wheelMsg.what, (Date) wheelMsg.obj);
					dismiss();
				}
			} else if (v.getId() == R.id.cancel) {
				dismiss();
			}
		}
	};

	private Date getDate(String yearString, String month) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(yearString);
		sBuffer.append(month);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date date = null;

		try {
			date = sdf.parse(sBuffer.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private GenWheelText genView = new GenWheelText();

	private void setWheelListener(WheelView wheelView, String[] data) {
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(act, genView);
		viewAdapter.setData(data);
		wheelView.setViewAdapter(viewAdapter);
		wheelView.addChangingListener(changeListener);
	}

	private OnWheelChangedListener changeListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel.getId() == R.id.year) {
				currSelectYear = yearArray[newValue];
				indexYear = newValue;
			} else if (wheel.getId() == R.id.month) {
				currSelectMonth = monthArray[newValue];
				indexMonth = newValue;
			}
			wheelMsg.obj = getDate(yearArray[indexYear], monthArray[indexMonth]);
		}
	};

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
			indexYear = year.getCurrentItem();
			indexMonth = month.getCurrentItem();
			currSelectYear = yearArray[indexYear];
			currSelectMonth = monthArray[indexMonth];
			wheelMsg.obj = getDate(yearArray[indexYear], monthArray[indexMonth]);
			controllerListenr.handleClick(wheelMsg.what, (Date) wheelMsg.obj);
		}
	};

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		dismiss();
		return onKey(v, keyCode, event);
	}
}