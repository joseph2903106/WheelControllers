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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import mma.mtake.wheel.component.R;
import wheel.component.genview.GenWheelText;
import wheel.component.genview.WheelGeneralAdapter;
import wheel.component.utils.UIAdjuster;

/**
 * The full time DatePicker presents the select time immediately.</br> Separate
 * two category of String Array presenting current time </br> from the selecting
 * action to format the date in different style, such as Chinese Style or West
 * Style.</br></br> Therefore, Whenever you want to change the date style,
 * </br>you should find those String array named with "ForDisplay" in the ending
 * </br>to revise whatever you want to be.</br></br>
 *
 * You can receive the "Calendar Object" through the callback
 * {@link WheelFullDatePicker.setWheelListener(int viewID, WheelFullDateSyncListener wheelListener)}
 * immediately.</br> Adding the method that letting user can restrict ceiling
 * and flooring in the period of available time in WheelFullDatePicker through
 * {@link WheelFullDatePicker.setRestrictDate(Calendar beforeDate, Calendar afterDate)}
 * </br> Being careful that "beforeDate" must be earlier than "afterDate".
 *
 * @author josephWang
 *
 *         <pre class="prettyprint">
 * <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:view ="http://schemas.android.com/apk/res-auto"
 *     android:layout_width="fill_parent"
 *     android:layout_height="fill_parent"
 *     android:orientation="vertical" >
 *
 *             <wheel.component.view.WheelFullDatePicker
 *                 android:id="@+id/date_picker"
 *                 android:layout_width="fill_parent"
 *                 android:layout_height="wrap_content"
 *                 android:layout_margin="10dip"
 *                 view:addZeroIfSmallThanTen="true"
 *                 view:canSetUpPastTime="false"
 *                 view:setCanSetUpFutureTime="false"
 *                 view:setCurrentTimeVisible="true"
 *                 view:showDateLastString="false"
 *                 view:showTaiwanYear="false" />
 *
 *  </LinearLayout>                      
 *
 * WheelFullDatePicker picker = (WheelFullDatePicker) findViewById(R.id.picker);
 * private WheelFullDateSyncListener syncListener = new WheelFullDateSyncListener() {
 * 	&#064;Override
 * 	public void handleDate(int viewID, Calendar calendar) {
 * 		currentDate.setText(DateHelper.dateFormatWithWeek(calendar));
 * 	}
 * };
 * picker.setWheelListener(picker.getId(), syncListener);
 * </pre>
 */

public class WheelFullDatePicker extends LinearLayout {
	public static final String TAG = WheelFullDatePicker.class.getSimpleName();
	private boolean isScrollFinish = true;
	private static String[] weekForDisplay;
	private LinearLayout wheelView_current_time_container;
	private static boolean showTaiwanYear = false;

	private Calendar beforeDate = Calendar.getInstance();
	private Calendar afterDate = Calendar.getInstance();
	private Calendar selectCalendar = Calendar.getInstance();

	private Context ctx;
	private WheelView year;
	private WheelView month;
	private WheelView date;
	private TextView selectTimeText;
	private long mUpadteTime = System.currentTimeMillis();

	private String[] yearArray;
	private String[] monthArray = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	private String[] dateArray;
	private String[] monthArrayForReplace = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	private String[] yearArrayForDisplay = new String[10];
	private String[] monthArrayForDisplay;
	private String[] dateArrayForDisplay;

	private List<String> tmp = new ArrayList<String>();
	private List<String> tmpForDisplay = new ArrayList<String>();

	private String currSelectDisplayYear = "";
	private String currSelectDisplayMonth = "";
	private String currentTime = new SimpleDateFormat("yyyy/MM/dd").format(new Date(mUpadteTime));


	private String currSelectYear = currentTime.substring(0, 4);
	private String currSelectMonth = currentTime.substring(5, 7);
	private String currSelectDate = currentTime.substring(8, 9);

	private ArrayMap<RestrictDateType, RestrictMonth> monthMap = new ArrayMap<RestrictDateType, RestrictMonth>();
	private RestrictDateType restrictDateType = RestrictDateType.OneYear;

	private int indexYear = 0;
	private int indexMonth = 0;
	private int viewID;

	private boolean addZeroIfSmallThanTen = true;
	private boolean canSetUpFutureTime = false;
	private boolean canSetUpPastTime = false;
	private boolean setCurrentTimeVisible = true;
	private boolean setRestrictBetweenDate = false;
	private boolean showDateLastString = true;

	private int YEAR_START  = Integer.parseInt(currentTime.substring(0, 4));
	private int MONTH_START = Integer.parseInt(currentTime.substring(5, 7));
	private int DATE_START  = Integer.parseInt(currentTime.substring(8, currentTime.length()));

	private WheelFullDateSyncListener wheelListener;

	public boolean isShowTaiwanYear() {
		return showTaiwanYear;
	}

	public void setShowTaiwanYear(boolean isTaiwanYear) {
		showTaiwanYear = isTaiwanYear;
	}

	public boolean isScrollFinish() {
		return isScrollFinish;
	}

	public boolean canSetUpFutureTime() {
		return canSetUpFutureTime;
	}

	/*****
	 * Only as Setting this attribute that can user restrict the period of time
	 * to be laster than today as "true",</br> and whenever user select as
	 * "false".
	 *
	 * @return boolean 只有設定此屬性，為"true" 能限制使用者設定晚於今天，為"false" 無限制.
	 */
	public void setCanSetUpFutureTime(boolean canSetUpFutureTime) {
		this.canSetUpFutureTime = canSetUpFutureTime;
		resetDatePicker();
	}

	/*****
	 * Only as Setting this attribute that can user restrict the period of time
	 * to be earlier than today as "true",</br> and whenever user select as
	 * "false".
	 *
	 * @return boolean 只有設定此屬性，為"true" 能限制使用者設定早於今天，為"false" 無限制.
	 */
	public boolean canSetUpPastTime() {
		return canSetUpPastTime;
	}

	public void setCanSetUpPastTime(boolean canSetUpPastTime) {
		this.canSetUpPastTime = canSetUpPastTime;
		resetDatePicker();
	}

	public boolean isSetCurrentTimeVisible() {
		return setCurrentTimeVisible;
	}

	public boolean isSetRestrictBetweenDate() {
		return setRestrictBetweenDate;
	}

	/*****
	 * Only as Setting this attribute that letting the mechanism to be
	 * trigger.false to be dissable.
	 *
	 *
	 * @return boolean 只有設定此屬性，為"true" 能啟動時間限制機制，為"false" 關閉.
	 */
	public void setCanRestrictBetweenDate(boolean setRestrictBetweenDate) {
		this.setRestrictBetweenDate = setRestrictBetweenDate;
	}

	/*****
	 * Only as Setting this attribute that can user see the period of time they
	 * select,</br> and "false" to be Invisible.
	 *
	 *
	 * @return boolean 只有設定此屬性，為"true" 能令用者看到所選的時間，為"false" 看不到.
	 */
	public void setSetCurrentTimeVisible(boolean setCurrentTimeVisible) {
		this.setCurrentTimeVisible = setCurrentTimeVisible;
		if (wheelView_current_time_container != null) {
			if (this.setCurrentTimeVisible) {
				wheelView_current_time_container.setVisibility(View.VISIBLE);
			} else {
				wheelView_current_time_container.setVisibility(View.GONE);
			}
		}
	}
	public Calendar getBeforeDate() {
		return beforeDate;
	}

	/***
	 * As the semantics in method.You can restrict ceiling and flooring in the
	 * period of</br> available time in WheelFullDatePicker.Whenever you want to
	 * set WheelFullDatePicker be restricted or not, you can trigger it through
	 * {@link WheelFullDatePicker.setCanRestrictBetweenDate(boolean setRestrictBetweenDate)}
	 * .Therefore,only under both condition that</br> 1,
	 * isSetRestrictBetweenDate() == true </br> 2, beforeDate!= null </br> 3,
	 * afterDate!= null </br> Be careful that not to set afterDate earlier than
	 * beforeDate!!</br> </br>
	 *
	 * 限制滾輪時間選單的選取時間。 要達成限制條件,要先設定
	 * {@link #setCanRestrictBetweenDate(boolean setRestrictBetweenDate)}
	 * true.</br> 如果要取消限制條件
	 * {@link #setCanRestrictBetweenDate(boolean setRestrictBetweenDate)}
	 * false.</br> 並設定上下限時間
	 *
	 * @param beforeDate
	 * @param afterDate
	 * @throws Exception
	 */
	public void setRestrictDate(Calendar beforeDate, Calendar afterDate) throws Exception {
		setRestrictDate(beforeDate, afterDate, true);
	}

	public void setRestrictDate(Calendar beforeDate, Calendar afterDate, boolean isBackTobeforeDate) throws Exception {
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		this.beforeDate = new GregorianCalendar(beforeDate.get(Calendar.YEAR),
				beforeDate.get(Calendar.MONTH),
				beforeDate.get(Calendar.DAY_OF_MONTH));
		this.beforeDate.set(Calendar.HOUR_OF_DAY, 0);
		this.beforeDate.set(Calendar.MINUTE, 0);
		this.beforeDate.set(Calendar.SECOND, 0);

		this.afterDate  = new GregorianCalendar(afterDate.get(Calendar.YEAR),
				afterDate.get(Calendar.MONTH),
				afterDate.get(Calendar.DAY_OF_MONTH));

		this.afterDate.set(Calendar.HOUR_OF_DAY, 23);
		this.afterDate.set(Calendar.MINUTE, 59);
		this.afterDate.set(Calendar.SECOND, 59);
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		if (beforeDate.after(afterDate)) {
			throw new IllegalArgumentException("This is weird that setting afterDate earlier than beforeDate!!! You must think more.");
		} else {
			setRestrictDate(isBackTobeforeDate);
		}
	}

	public Calendar getAfterDate() {
		return afterDate;
	}

	public TextView getSelectTimeText() {
		return selectTimeText;
	}

	public String getCurrSelectDisplayYear() {
		return currSelectDisplayYear;
	}

	public String getCurrSelectDisplayMonth() {
		return currSelectDisplayMonth;
	}

	public String getCurrSelectDisplayDate() {
		return addZeroIfSmallThanTen();
	}

	public boolean isAddZeroIfSmallThanTen() {
		return addZeroIfSmallThanTen;
	}

	public void setAddZeroIfSmallThanTen(boolean addZeroIfSmallThanTen) {
		this.addZeroIfSmallThanTen = addZeroIfSmallThanTen;
	}

	private String addZeroIfSmallThanTen() {
		String result = "";
		if (addZeroIfSmallThanTen) {
			if (Integer.parseInt(currSelectDate) < 10) {
				result = ("0" + currSelectDate);
			} else {
				result = currSelectDate;
			}
		} else {
			result = currSelectDate;
		}
		return result;
	}

	public boolean isShowDateLastString() {
		return showDateLastString;
	}

	public void setShowDateLastString(boolean showDateLastString) {
		this.showDateLastString = showDateLastString;
	}

	public WheelFullDatePicker(Context context) {
		super(context);
		ctx = context;
		initStyle(null);
		initYearPeriod();
		initDateArray(YEAR_START, MONTH_START);
		initDatePicker();
		new Thread(setTodayThread).start();
	}

	public WheelFullDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		initStyle(attrs);
		initYearPeriod();
		initDateArray(YEAR_START, MONTH_START);
		initDatePicker();
		new Thread(setTodayThread).start();
	}

	private void initStyle(AttributeSet attrs) {
		int[] linerarLayoutAttrs = { android.R.attr.orientation };
		TypedArray array = ctx.obtainStyledAttributes(attrs, linerarLayoutAttrs);
		array.recycle();
		weekForDisplay = ctx.getResources().getStringArray(R.array.week_in_calendar);
		array = ctx.obtainStyledAttributes(attrs, R.styleable.WheelFullDatePickerStyle);
		showTaiwanYear        = array.getBoolean(R.styleable.WheelFullDatePickerStyle_showTaiwanYear, false);
		addZeroIfSmallThanTen = array.getBoolean(R.styleable.WheelFullDatePickerStyle_addZeroIfSmallThanTen, true);
		setCurrentTimeVisible = array.getBoolean(R.styleable.WheelFullDatePickerStyle_setCurrentTimeVisible, true);
		canSetUpPastTime      = array.getBoolean(R.styleable.WheelFullDatePickerStyle_canSetUpPastTime, false);
		canSetUpFutureTime    = array.getBoolean(R.styleable.WheelFullDatePickerStyle_canSetUpFutureTime, false);
		showDateLastString    = array.getBoolean(R.styleable.WheelFullDatePickerStyle_showDateLastString, true);
		monthArrayForDisplay  = ctx.getResources().getStringArray(R.array.month);
		array.recycle();
		if (!showDateLastString) {
			for (int i = 0; i < monthArrayForDisplay.length; i++) {
				if (monthArrayForDisplay[i].contains("月")) {
					monthArrayForDisplay[i] = monthArrayForDisplay[i].replace("月", "");
				}
			}
		}
	}

	private void initDateArray(int year, int month) {
		// Generate day string array
		switch (month) {
			/********** 大月 **************/
			case 1:// 大月
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				produceDate(31);// 大月
				break;
			/********** 小月 **************/
			case 4:
			case 6:
			case 9:
			case 11:
				produceDate(30);// 小月
				break;
			/********** 二月 **************/
			case 2:
				invokeFeburary(year);
				break;
		}
		restrictBetweenDate();
		adjustSelectPosition();
	}

	private void restrictBetweenDate() {
		if (setRestrictBetweenDate &&
				beforeDate != null &&
				afterDate != null &&
				monthMap.get(restrictDateType) != null &&
				monthMap.get(restrictDateType).beforeMonth != null &&
				monthMap.get(restrictDateType).afterMonth != null) {

			if (Integer.parseInt(currSelectYear) == beforeDate.get(Calendar.YEAR) &&
					Integer.parseInt(currSelectMonth) == (beforeDate.get(Calendar.MONTH) + 1)) {

				produceDate(beforeDate.get(Calendar.DAY_OF_MONTH));
				produceRestrictDate(beforeDate.get(Calendar.YEAR), beforeDate.get(Calendar.MONTH) + 1, beforeDate.get(Calendar.DAY_OF_MONTH));
				date.setCurrentItem(beforeDate.get(Calendar.DAY_OF_MONTH) - 1, false);

			} else if (Integer.parseInt(currSelectYear) == afterDate.get(Calendar.YEAR) &&
					Integer.parseInt(currSelectMonth) == (afterDate.get(Calendar.MONTH) + 1)) {

				produceDate(afterDate.get(Calendar.DAY_OF_MONTH));
				date.setCurrentItem(afterDate.get(Calendar.DAY_OF_MONTH) - 1, false);
			}
		}
	}

	private void produceRestrictDate(int year, int month, int start) {
		tmp.clear();
		tmpForDisplay.clear();
		int end = 0;
		switch (month) {
			/********** 大月 **************/
			case 1:// 大月
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				end = 31;
				break;
			/********** 小月 **************/
			case 4:
			case 6:
			case 9:
			case 11:
				end = 30;
				break;
			/********** 二月 **************/
			case 2:
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					end = 29;
				} else {
					end = 28;
				}
				break;
		}

		for (int i = start; i <= end; i++) {
			tmp.add(String.valueOf(i));
			genDate(i);
		}
		dateArray = tmp.toArray(new String[0]);
		dateArrayForDisplay = tmpForDisplay.toArray(new String[0]);
		changeDateBySelect();
	}

	private void adjustSelectPosition() {
		if (Integer.parseInt(currSelectDate) > dateArray.length) {
			date.setCurrentItem(dateArray.length - 1, false);
		}
	}

	private void adjustFeburaryByYear(int year, int month) {
		switch (month) {
			case 2:
				invokeFeburary(year);
				break;
		}
		adjustSelectPosition();
	}

	public static enum RestrictDateType {
		OneYear, More;
	}

	public static class RestrictMonth {
		public String[] beforeMonth;
		public String[] beforeMonthDisplay;
		public String[] afterMonth;
		public String[] afterMonthDisplay;
	}


	private void setRestrictDate(boolean isBackTobeforeDate) {
		if (beforeDate != null && afterDate != null) {
			tmp.clear();
			tmpForDisplay.clear();

			int period = afterDate.get(Calendar.YEAR) - beforeDate.get(Calendar.YEAR);
			if (period > 0) {// 跨一年以上
				for (int i = 0; i <= period; i++) {
					tmp.add(String.valueOf(beforeDate.get(Calendar.YEAR) + i));
					genYear(beforeDate.get(Calendar.YEAR), i);
				}
			} else if (period == 0) {// 無跨年
				tmp.add(String.valueOf(beforeDate.get(Calendar.YEAR)));
				genYear(beforeDate.get(Calendar.YEAR), 0);
			}

			if (period >= 1) {// 跨一年以上
				restrictDateType = RestrictDateType.More;
				monthMap.put(restrictDateType, produceMonthForCrossYear());
			} else if (period == 0) {// 無跨年
				RestrictMonth restrictDateMonth = new RestrictMonth();
				int range = (afterDate.get(Calendar.MONTH) + 1) - (beforeDate.get(Calendar.MONTH) + 1) + 1;
				if (range > 0) {
					restrictDateMonth.beforeMonth = new String[range];
					restrictDateMonth.afterMonth = new String[range];

					restrictDateMonth.beforeMonthDisplay = new String[range];
					restrictDateMonth.afterMonthDisplay = new String[range];
					for (int i = 0; i < range; i++) {
						restrictDateMonth.beforeMonth[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i);
						restrictDateMonth.afterMonth[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i);
						if (!showDateLastString) {
							restrictDateMonth.beforeMonthDisplay[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i);
							restrictDateMonth.afterMonthDisplay[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i);
						} else {
							restrictDateMonth.beforeMonthDisplay[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i) + "月";
							restrictDateMonth.afterMonthDisplay[i] = "" + ((beforeDate.get(Calendar.MONTH) + 1) + i) + "月";
						}
					}
					restrictDateType = RestrictDateType.OneYear;
					monthMap.put(restrictDateType, restrictDateMonth);
				}
			}

			yearArray = tmp.toArray(new String[0]);
			yearArrayForDisplay = tmpForDisplay.toArray(new String[0]);
			monthArray = monthMap.get(restrictDateType).beforeMonth;
			monthArrayForDisplay = monthMap.get(restrictDateType).beforeMonthDisplay;

			changeYearBySelect(yearArrayForDisplay);
			changeMonthBySelect(monthArrayForDisplay);

			currSelectYear = yearArray[0];
			currSelectMonth = String.valueOf(beforeDate.get(Calendar.MONTH) + 1);
			backToZeroPosition();
		}
	}

	private void backToZeroPosition() {
		year.setCurrentItem(0, false);
		month.setCurrentItem(0, false);
		date.setCurrentItem(0, false);
	}

	private RestrictMonth produceMonthForCrossYear() {
		RestrictMonth restrictDateMonth = new RestrictMonth();
		/********************** 產生限制日期 上限的月份 *************************/
		int beforeRange = 12 - (beforeDate.get(Calendar.MONTH) + 1) + 1;
		restrictDateMonth.beforeMonth = new String[beforeRange];
		restrictDateMonth.beforeMonthDisplay = new String[beforeRange];
		for (int i = 0; i < beforeRange; i++) {
			restrictDateMonth.beforeMonth[i] = String.valueOf(((beforeDate.get(Calendar.MONTH) + 1) + i));
			if (!showDateLastString) {
				restrictDateMonth.beforeMonthDisplay[i] = String.valueOf(((beforeDate.get(Calendar.MONTH) + 1) + i));
			} else {
				restrictDateMonth.beforeMonthDisplay[i] = String.valueOf(((beforeDate.get(Calendar.MONTH) + 1) + i)) + "月";
			}
		}
		/********************** 產生限制日期 上限的月份 *************************/

		/********************** 產生限制日期 下限的月份 *************************/
		int afterRange = (afterDate.get(Calendar.MONTH) + 1);
		restrictDateMonth.afterMonth = new String[afterRange];
		restrictDateMonth.afterMonthDisplay = new String[afterRange];
		for (int i = 0; i < afterRange; i++) {
			restrictDateMonth.afterMonth[i] = String.valueOf((i + 1));
			if (!showDateLastString) {
				restrictDateMonth.afterMonthDisplay[i] = String.valueOf((i + 1));
			} else {
				restrictDateMonth.afterMonthDisplay[i] = String.valueOf((i + 1)) + "月";
			}
		}
		/********************** 產生限制日期 下限的月份 *************************/
		return restrictDateMonth;
	}

	private void initYearPeriod() {
		tmp.clear();
		tmpForDisplay.clear();
		if (canSetUpFutureTime() && canSetUpPastTime()) {// 可以設定未來時間與過去時間
			for (int i = 10 - 1; i > 0; i--) {
				tmp.add(String.valueOf(YEAR_START - i));
				genYear(YEAR_START, -i);
			}
			/************ 今年 **************/
			tmp.add(String.valueOf(YEAR_START));
			genYear(YEAR_START, 0);
			/************ 今年之後10年 **************/
			for (int i = 1; i <= 10; i++) {
				tmp.add(String.valueOf(YEAR_START + i));
				genYear(YEAR_START, i);
			}
		} else if (canSetUpFutureTime() && !canSetUpPastTime()) {// 只能設定未來時間
			/************ 今年 **************/
			tmp.add(String.valueOf(YEAR_START));
			genYear(YEAR_START, 0);
			/************ 今年之後10年 **************/
			for (int i = 1; i <= 10; i++) {
				tmp.add(String.valueOf(YEAR_START + i));
				genYear(YEAR_START, i);
			}
		} else if (!canSetUpFutureTime() && canSetUpPastTime()) {// 只能設定過去時間
			for (int i = 10 - 1; i > 0; i--) {
				tmp.add(String.valueOf(YEAR_START - i));
				genYear(YEAR_START, -i);
			}
			/************ 今年 **************/
			tmp.add(String.valueOf(YEAR_START));
			genYear(YEAR_START, 0);
		} else if (!canSetUpFutureTime() && !canSetUpPastTime()) {// 都不能設定未來時間與過去時間_只有今年
			tmp.add(String.valueOf(YEAR_START));
			genYear(YEAR_START, 0);
		}
		yearArray = tmp.toArray(new String[0]);
		yearArrayForDisplay = tmpForDisplay.toArray(new String[0]);
	}

	private List<String> genYear(int YEAR_START, int index) {
		if (showTaiwanYear) {
			if (showDateLastString) {
				tmpForDisplay.add(String.valueOf(YEAR_START - 1911 + index) + getValuesByLanguage("年", ""));
			} else {
				tmpForDisplay.add(String.valueOf(YEAR_START - 1911 + index));
			}
		} else {
			if (showDateLastString) {
				tmpForDisplay.add(String.valueOf(YEAR_START + index) + getValuesByLanguage("年", ""));
			} else {
				tmpForDisplay.add(String.valueOf(YEAR_START + index));
			}
		}
		return tmpForDisplay;
	}

	private List<String> genDate(int index) {
		if (index < 10) {
			if (showDateLastString) {
				if (isAddZeroIfSmallThanTen()) {
					tmpForDisplay.add("0" + String.valueOf(index) + getValuesByLanguage("日", ""));
				} else {
					tmpForDisplay.add(String.valueOf(index) + getValuesByLanguage("日", ""));
				}
			} else {
				if (isAddZeroIfSmallThanTen()) {
					tmpForDisplay.add("0" + String.valueOf(index));
				} else {
					tmpForDisplay.add(String.valueOf(index));
				}
			}
		} else {
			if (showDateLastString) {
				tmpForDisplay.add(String.valueOf(index) + getValuesByLanguage("日", ""));
			} else {
				tmpForDisplay.add(String.valueOf(index));
			}
		}
		return tmpForDisplay;
	}

	private String getValuesByLanguage(String chinese, String english) {
		return (UIAdjuster.getLanguage(ctx) ? chinese : english);
	}

	private void produceDate(int size) {
		tmp.clear();
		tmpForDisplay.clear();
		produceDateInner(size);
		dateArray = tmp.toArray(new String[0]);
		dateArrayForDisplay = tmpForDisplay.toArray(new String[0]);
	}

	private void produceDateInner(int size) {
		for (int i = 1; i <= size; i++) {
			tmp.add(String.valueOf(i));
			genDate(i);
		}
	}

	/**
	 * 二月份處理 包含潤年
	 *
	 * @param currSelectYear
	 */
	private void invokeFeburary(int currSelectYear) {
		tmp.clear();
		tmpForDisplay.clear();
		if ((currSelectYear % 4 == 0 && currSelectYear % 100 != 0) || currSelectYear % 400 == 0) {
			produceDateInner(29);
		} else {
			produceDateInner(28);
		}
		dateArray = tmp.toArray(new String[0]);
		dateArrayForDisplay = tmpForDisplay.toArray(new String[0]);
	}

	/**
	 *
	 */
	public void initDatePicker() {
		LinearLayout wheel = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.full_date_picker_wheel, this, true);
		wheelView_current_time_container = (LinearLayout) wheel.findViewById(R.id.wheelView_current_time_container);
		year = (WheelView) wheel.findViewById(R.id.year);
		month = (WheelView) wheel.findViewById(R.id.month);
		date = (WheelView) wheel.findViewById(R.id.date);
		selectTimeText = (TextView) wheel.findViewById(R.id.selectTime);

		setWheelListener(year, yearArrayForDisplay);
		setWheelListener(month, monthArrayForDisplay);
		setWheelListener(date, dateArrayForDisplay);
		year.addScrollingListener(scrollListener);
		month.addScrollingListener(scrollListener);
		date.addScrollingListener(scrollListener);
		initFirstDate();
		if (setCurrentTimeVisible) {
			wheelView_current_time_container.setVisibility(View.VISIBLE);
		} else {
			wheelView_current_time_container.setVisibility(View.GONE);
		}
	}

	public void resetDatePicker() {
		initYearPeriod();
		initDateArray(YEAR_START, MONTH_START);
		setWheelListener(year, yearArrayForDisplay);
		setWheelListener(month, monthArrayForDisplay);
		setWheelListener(date, dateArrayForDisplay);
		year.addScrollingListener(scrollListener);
		month.addScrollingListener(scrollListener);
		date.addScrollingListener(scrollListener);
		initFirstDate();
		if (setCurrentTimeVisible) {
			wheelView_current_time_container.setVisibility(View.VISIBLE);
		} else {
			wheelView_current_time_container.setVisibility(View.GONE);
		}
	}

	private void initFirstDate() {
		currSelectYear = String.valueOf(YEAR_START);
		currSelectMonth = String.valueOf(MONTH_START);
		currSelectDate = String.valueOf(DATE_START);
		sendCalendar(getSelectedTime(getInteger(currSelectYear), getInteger(currSelectMonth), getInteger(currSelectDate)));
		selectTimeText.setText(dateFormatWithWeek(getSelectedTime(getInteger(currSelectYear), getInteger(currSelectMonth), getInteger(currSelectDate))));
	}

	private void sendCalendar(Calendar date) {
		if (wheelListener != null) {
			wheelListener.handleDate(viewID, date);
		}
	}

	private void setWheelListener(WheelView wheelView, String[] data) {
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(ctx, genView);
		viewAdapter.setData(data);
		wheelView.setViewAdapter(viewAdapter);
		wheelView.addChangingListener(changedListener);
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel.getId() == R.id.year) {
				currSelectYear = yearArray[newValue];
				currSelectDisplayYear = yearArrayForDisplay[newValue];
				indexYear = newValue;
				if (setRestrictBetweenDate &&
						beforeDate != null &&
						afterDate != null &&
						monthMap.get(restrictDateType) != null &&
						monthMap.get(restrictDateType).beforeMonth != null &&
						monthMap.get(restrictDateType).afterMonth != null) {

					if (Integer.parseInt(currSelectYear) == beforeDate.get(Calendar.YEAR)) {
						if (indexMonth >= monthMap.get(restrictDateType).beforeMonth.length) {
							indexMonth = 0;
						}
						monthArray = monthMap.get(restrictDateType).beforeMonth;
						currSelectMonth = monthArray[0];
						int monthInteger = Integer.parseInt(monthMap.get(restrictDateType).beforeMonth[indexMonth]);
						adjustFeburaryByYear(Integer.parseInt(yearArray[indexYear]), monthInteger);
						changeMonthBySelect(monthMap.get(restrictDateType).beforeMonthDisplay);
						month.setCurrentItem(0, false);
						produceRestrictDate(beforeDate.get(Calendar.YEAR),
								beforeDate.get(Calendar.MONTH) + 1,
								beforeDate.get(Calendar.DAY_OF_MONTH));
						date.setCurrentItem(0, false);
					} else if (Integer.parseInt(currSelectYear) == afterDate.get(Calendar.YEAR)) {
						if (indexMonth >= monthMap.get(restrictDateType).afterMonth.length) {
							indexMonth = 0;
						}
						monthArray = monthMap.get(restrictDateType).afterMonth;
						int monthInteger = Integer.parseInt(monthMap.get(restrictDateType).afterMonth[indexMonth]);

						adjustFeburaryByYear(Integer.parseInt(yearArray[indexYear]), monthInteger);
						changeMonthBySelect(monthMap.get(restrictDateType).afterMonthDisplay);
						month.setCurrentItem(monthMap.get(restrictDateType).afterMonth.length - 1, false);

						currSelectMonth = monthArray[monthArray.length - 1];

						produceDate(afterDate.get(Calendar.DATE));
						changeDateBySelect();

					} else {
						resumeToGeneralMonth();
						month.setCurrentItem(0, false);
						produceDate(31);
						changeDateBySelect();
					}
				} else {
					resumeToGeneralMonth();
					month.setCurrentItem(0, false);
					produceDate(31);
					changeDateBySelect();
				}
				changeDateBySelect();
			} else if (wheel.getId() == R.id.month) {
				indexMonth = newValue;
				if (setRestrictBetweenDate &&
						beforeDate != null &&
						afterDate != null &&
						monthMap.get(restrictDateType) != null &&
						monthMap.get(restrictDateType).beforeMonth != null &&
						monthMap.get(restrictDateType).afterMonth != null) {

					if (Integer.parseInt(currSelectYear) == beforeDate.get(Calendar.YEAR)) {
						currSelectMonth = monthMap.get(restrictDateType).beforeMonth[newValue];
						initDateArray(Integer.parseInt(yearArray[indexYear]),
								Integer.parseInt(monthMap.get(restrictDateType).beforeMonth[indexMonth]));
					} else if (Integer.parseInt(currSelectYear) == afterDate.get(Calendar.YEAR)) {
						currSelectMonth = monthMap.get(restrictDateType).afterMonth[newValue];
						initDateArray(Integer.parseInt(yearArray[indexYear]),
								Integer.parseInt(monthMap.get(restrictDateType).afterMonth[indexMonth]));
					} else {
						currSelectMonth = monthArray[newValue];
						initDateArray(Integer.parseInt(yearArray[indexYear]),
								Integer.parseInt(monthArray[indexMonth]));
					}
				} else {
					currSelectMonth = monthArray[newValue];
					initDateArray(Integer.parseInt(yearArray[indexYear]),
							Integer.parseInt(monthArray[indexMonth]));
				}
				restrictBetweenDate();
				changeDateBySelect();
			} else if (wheel.getId() == R.id.date) {
				currSelectDate = dateArray[newValue];
			}
		}
	};
	private Calendar certainDate;

	/**
	 * Be careful that certain time must be between ceiling Date and flooring
	 * Date,</br> Let WheelFullDatePicker to scroll to certain time .
	 *
	 * @param certain
	 * @throws Exception
	 */
	public void scrollToCertainDate(Calendar certain) throws Exception {
		if (certain == null) {
			throw new IllegalArgumentException("You could not set Certain Calendar in null.");
		} else if (certain.after(getMaxCalendar()) || certain.before(getMinCalendar())) {

			Log.d(TAG, " scrollToCertainDate   calendarToString certain  " + calendarToString(certain));
			Log.d(TAG, " scrollToCertainDate   calendarToString getMaxCalendar  " + calendarToString(getMaxCalendar()));
			Log.d(TAG, " scrollToCertainDate   calendarToString getMinCalendar  " + calendarToString(getMinCalendar()));

			Log.d(TAG, " scrollToCertainDate certain.after(getMaxCalendar())  " + certain.after(getMaxCalendar()));
			Log.d(TAG, " scrollToCertainDate certain.before(getMinCalendar()) " + certain.before(getMinCalendar()));

			throw new IllegalArgumentException("This is weird that setting certain Calendar out range in period of available time!!! You must think more");
		}
		scrollToCertainDateInner(certain);
	}

	public static String calendarToString(Calendar date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date.getTime());
	}

	private void scrollToCertainDateInner(Calendar certain) {
		certainDate = certain;
		int indexYear = 0;
		int indexMonth = 0;
		int indexDate = 0;

		boolean inYearArry = false;
		boolean inMonthArray = false;
		boolean inDateArray = false;

		for (int i = 0; i < yearArray.length; i++) {
			if (Integer.parseInt(yearArray[i]) == certain.get(Calendar.YEAR)) {
				inYearArry = true;
				indexYear = i;
				break;
			}
			if (inYearArry) {
				break;
			}
		}
		if (inYearArry) {
			year.setCurrentItem(indexYear, false);
		}

		for (int i = 0; i < monthArray.length; i++) {
			if (Integer.parseInt(monthArray[i]) == (certain.get(Calendar.MONTH) + 1)) {
				inMonthArray = true;
				indexMonth = i;
				break;
			}
			if (inMonthArray) {
				break;
			}
		}
		if (inMonthArray) {
			month.setCurrentItem(indexMonth, false);
		}

		for (int i = 0; i < dateArray.length; i++) {
			if (Integer.parseInt(dateArray[i]) == certain.get(Calendar.DATE)) {
				inDateArray = true;
				indexDate = i;
				break;
			}
			if (inDateArray) {
				break;
			}
		}
		if (inDateArray) {
			date.setCurrentItem(indexDate, false);
		}
	}

	public Calendar getMaxCalendar() {
		if (afterDate != null) {
			return afterDate;
		} else {
			/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
//			Calendar date = new GregorianCalendar(Integer.parseInt(yearArray[yearArray.length - 1])-1, Calendar.DECEMBER + 1, 31);
			Calendar date = new GregorianCalendar(Integer.parseInt(yearArray[yearArray.length - 1]), Calendar.DECEMBER + 1, 31);
			/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
			date.set(Calendar.HOUR_OF_DAY, 23);
			date.set(Calendar.MINUTE, 59);
			date.set(Calendar.SECOND, 59);
			afterDate = date;
			return date;
		}
	}

	public Calendar getMinCalendar() {
		if (beforeDate != null) {
			return beforeDate;
		} else {
			/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
//			Calendar date = new GregorianCalendar(Integer.parseInt(yearArray[0])-1, Calendar.JANUARY + 1, 1);
			Calendar date = new GregorianCalendar(Integer.parseInt(yearArray[0]), Calendar.JANUARY, 1);
			/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			beforeDate  = date;
			return date;
		}
	}

	/***
	 * Let WheelFullDatePicker to scroll to ceiling time .
	 *
	 * 讓滾輪時間選單顯示在上限時間
	 */
	public void scrollToBeforeDate() {
		year.setCurrentItem(0, false);
		monthArray = monthMap.get(restrictDateType).beforeMonth;
		monthArrayForDisplay = monthMap.get(restrictDateType).beforeMonthDisplay;
		changeMonthBySelect(monthArrayForDisplay);
		produceRestrictDate(beforeDate.get(Calendar.YEAR), beforeDate.get(Calendar.MONTH) + 1, beforeDate.get(Calendar.DAY_OF_MONTH));
		changeDateBySelect();
		month.setCurrentItem(0, false);
		date.setCurrentItem(0, false);
		currSelectYear = String.valueOf(beforeDate.get(Calendar.YEAR));
		currSelectMonth = monthMap.get(restrictDateType).beforeMonth[0];
		currSelectDate = String.valueOf(beforeDate.get(Calendar.DATE));
		sendCalendar(getSelectedTime(getInteger(currSelectYear), getInteger(currSelectMonth)-1, getInteger(currSelectDate)));
	}

	private int getInteger(String res) {
		return Integer.parseInt(res);
	}

	/***
	 * Let WheelFullDatePicker to scroll to flooring time .
	 *
	 * 讓滾輪時間選單顯示在下限時間
	 */
	public void scrollToAfterDate() {
		year.setCurrentItem(yearArray.length - 1, false);
		monthArray = monthMap.get(restrictDateType).afterMonth;
		monthArrayForDisplay = monthMap.get(restrictDateType).afterMonthDisplay;
		changeMonthBySelect(monthArrayForDisplay);
		produceDate(afterDate.get(Calendar.DATE));
		changeDateBySelect();

		if (monthMap.get(restrictDateType) != null &&
				monthMap.get(restrictDateType).afterMonth.length <= monthArray.length) {
			month.setCurrentItem(monthMap.get(restrictDateType).afterMonth.length - 1, false);
		} else {
			month.setCurrentItem(0, false);
		}
		if (afterDate != null &&
				afterDate.get(Calendar.DATE) <= dateArray.length) {
			date.setCurrentItem(afterDate.get(Calendar.DATE) - 1, false);
		} else {
			date.setCurrentItem(0, false);
		}
		currSelectYear = String.valueOf(afterDate.get(Calendar.YEAR));
		currSelectMonth = monthMap.get(restrictDateType).afterMonth[monthMap.get(restrictDateType).afterMonth.length - 1];
		currSelectDate = String.valueOf(afterDate.get(Calendar.DATE));
		sendCalendar(getSelectedTime(getInteger(currSelectYear), getInteger(currSelectMonth)-1, getInteger(currSelectDate)));
	}

	private void resumeToGeneralMonth() {
		monthArray = monthArrayForReplace;
		monthArrayForDisplay = ctx.getResources().getStringArray(R.array.month);
		currSelectMonth = monthArray[0];
		changeMonthBySelect(monthArrayForDisplay);
		adjustFeburaryByYear(Integer.parseInt(yearArray[indexYear]), Integer.parseInt(monthArray[indexMonth]));
	}

	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {
			isScrollFinish = false;
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			if (!canSetUpPastTime()) {
				if (getSelectedTime().before(getTodayCalendar())) {
					setToday();
					return;
				}
			} else if (!canSetUpFutureTime()) {
				if (getSelectedTime().after(getTodayCalendar())) {
					setToday();
					return;
				}
			}
			isScrollFinish = true;
			if (isScrollFinish) {
				selectTimeText.setText(dateFormatWithWeek(getSelectedTime()));
				sendCalendar(getSelectedTime());
			}
		}
	};

	public static Calendar getTodayCalendar() {
		return Calendar.getInstance();
	}

	public void stopScroll() {
		year.stopScrolling();
		month.stopScrolling();
		date.stopScrolling();
	}

	public static final int TODAY_SINGAL = 20120710;
	public static final int SET_CERTAIN_DTAE_SINGAL = 20130308;
	private Runnable setTodayThread = new Runnable() {
		@Override
		public void run() {
			try {
				Message msg = new Message();
				msg.what = TODAY_SINGAL;
				setDayHandler.sendMessageDelayed(msg, 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler setDayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TODAY_SINGAL:
					if (setRestrictBetweenDate &&
							beforeDate != null &&
							afterDate != null) {
						if (certainDate != null) {
							try {
								scrollToCertainDate(certainDate);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							year.setCurrentItem(0, false);
							month.setCurrentItem(0, false);
							if (beforeDate.get(Calendar.DAY_OF_MONTH) - 1 >= 0 &&
									monthMap.get(restrictDateType) != null &&
									monthMap.get(restrictDateType).beforeMonth != null &&
									(beforeDate.get(Calendar.DAY_OF_MONTH) - 1) < monthMap.get(restrictDateType).beforeMonth.length) {
								date.setCurrentItem(beforeDate.get(Calendar.DAY_OF_MONTH) - 1, false);
							} else {
								date.setCurrentItem(0, false);
							}
						}
					} else {
						if (canSetUpFutureTime() && canSetUpPastTime()) {// 可以設定未來時間與過去時間
							year.setCurrentItem(9, false);
						} else if (canSetUpFutureTime() && !canSetUpPastTime()) {// 只能設定未來時間
							year.setCurrentItem(0, false);
						} else if (!canSetUpFutureTime() && canSetUpPastTime()) {// 只能設定過去時間
							year.setCurrentItem(yearArray.length - 1, false);
						} else if (!canSetUpFutureTime() && !canSetUpPastTime()) {// 只有今年
							year.setCurrentItem(0, false);
						}
						month.setCurrentItem(getRightIndex(MONTH_START), false);
						date.setCurrentItem(getRightIndex(DATE_START), false);
					}
					isScrollFinish = true;
					break;
			}
			selectTimeText.setText(dateFormatWithWeek(getSelectedTime()));
			sendCalendar(getSelectedTime());
		}
	};

	private int getRightIndex(int data) {
		return ((data - 1 > 0) ? data - 1 : 0);
	}

	private GenWheelText genView = new GenWheelText();

	private void changeYearBySelect(String[] yearArray) {
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(ctx, genView);
		viewAdapter.setData(yearArray);
		year.setViewAdapter(viewAdapter);
	}

	private void changeMonthBySelect(String[] monthArray) {
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(ctx, genView);
		viewAdapter.setData(monthArray);
		month.setViewAdapter(viewAdapter);
	}

	private void changeDateBySelect() {
		WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(ctx, genView);
		viewAdapter.setData(dateArrayForDisplay);
		date.setViewAdapter(viewAdapter);
	}

	public String getCurrSelectYear() {
		return currSelectYear;
	}

	public String getCurrSelectMonth() {
		return currSelectMonth;
	}

	public String getCurrSelectDate() {
		return currSelectDate;
	}

	public String getSelectDateString() {
		String result = "";
		if (currSelectYear.length() > 0 && currSelectMonth.length() > 0) {
			if (UIAdjuster.getLanguage(ctx)) {
				result = currSelectYear + "年" + currSelectMonth + "月" + currSelectDate + "日";
			} else {
				result = currSelectYear + "/" + currSelectMonth;
			}
		}
		return result;
	}
	public static String dateFormatWithWeek(Calendar calendar) {
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		calendar = new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(calendar.getTimeInMillis());
		StringBuffer sb = new StringBuffer();
		sb.append(format.format(date));
		sb.append(" ");
		sb.append(getDayName(calendar.get(Calendar.DAY_OF_WEEK)));
		String result = "";
		if (showTaiwanYear) {
			result = String.valueOf(Integer.parseInt(sb.toString().substring(0, 4)) - 1911) + sb.toString().substring(4);
		} else {
			result = sb.toString();
		}
		return result;
	}

	public static String dateFormat(Calendar calendar) {
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		calendar = new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(calendar.getTimeInMillis());
		StringBuffer sb = new StringBuffer();
		sb.append(format.format(date));
		String result = "";
		if (showTaiwanYear) {
			result = String.valueOf(Integer.parseInt(sb.toString().substring(0, 4)) - 1911) + sb.toString().substring(4);
		} else {
			result = sb.toString();
		}
		return result;
	}

//	public Calendar getSelectCalendar() {
//		return selectCalendar;
//	}

	public static String getDayName(int day) {
		return weekForDisplay[day - 1];
	}

	private Calendar getSelectedTime(int year, int month, int date) {
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
//		Calendar calendar = new GregorianCalendar(year, month - 1, date);
		Calendar calendar = new GregorianCalendar(year, month, date);
		/**********************轉換成格林威治時間，避免Java Calendar 只支援到1970/01/01 的問題************************/
		selectCalendar = calendar;
		return selectCalendar;
	}

	public Calendar getSelectedTime() {
		Log.d(TAG , " setDayHandler monthArray.length " + monthArray.length);
		Log.d(TAG , " setDayHandler month.getCurrentItem " + month.getCurrentItem());
		Log.d(TAG , " setDayHandler monthArray[month.getCurrentItem()] " + monthArray[month.getCurrentItem()]);
		Log.d(TAG , " setDayHandler (Integer.parseInt(monthArray[month.getCurrentItem()]) - 1) " + (Integer.parseInt(monthArray[month.getCurrentItem()]) - 1));

		Log.d(TAG , " setDayHandler date.getCurrentItem() " + date.getCurrentItem());
		Log.d(TAG , " setDayHandler dateArray[date.getCurrentItem()] " + dateArray[date.getCurrentItem()]);
		Log.d(TAG , " setDayHandler date.getCurrentItem() " + date.getCurrentItem());

		return getSelectedTime(Integer.parseInt(yearArray[year.getCurrentItem()]),
				               Integer.parseInt(monthArray[month.getCurrentItem()]) - 1,
							   Integer.parseInt(dateArray[date.getCurrentItem()]));
	}

	public void setToday() {
		Message msg = new Message();
		msg.what = TODAY_SINGAL;
		setDayHandler.handleMessage(msg);
	}

	public interface WheelFullDateSyncListener {
		 void handleDate(int viewID, Calendar calendar);
	}

	public void setWheelListener(int viewID, WheelFullDateSyncListener wheelListener) {
		this.viewID = viewID;
		this.wheelListener = wheelListener;
	}
}