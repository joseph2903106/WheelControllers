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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mma.mtake.wheel.component.R;
import wheel.component.controller.OnShowWheelListener;
import wheel.component.utils.UIAdjuster;
import wheel.component.utils.WheelUtility;
import wheel.component.view.WheelControlListener;
import wheel.component.view.WheelFullDatePicker;
import wheel.component.view.WheelFullDatePicker.WheelFullDateSyncListener;

/**
 * * The full time DatePicker in Dialog Style presents the select time
 * immediately.</br> Separate two category of String Array presenting current
 * time </br> from the selecting action to format the date in different style,
 * such as Chinese Style or West Style.</br></br> Therefore, Whenever you want
 * to change the date style, </br>you should find those String array named with
 * "ForDisplay" in the ending </br>to revise whatever you want to be.</br></br>
 * 
 * You can receive the "Calendar Object" through the callback
 * {@link #WheelFullDatePicker.setWheelHandler(View v, Handler wheelHandler)}
 * Using {@link #Handler} to get "Calendar Object" from Message obj
 * immediately.or
 * {@link #WheelFullDatePicker.setWheelListener(int viewID, WheelFullDateSyncListener wheelListener)}
 * in the same way.</br> Adding the method that letting user can restrict
 * ceiling and flooring in the period of available time in WheelFullDatePicker
 * through
 * {@link #WheelFullDatePicker.setRestrictDate(Calendar beforeDate, Calendar afterDate)}
 * </br> Being careful that "beforeDate" must earlier than "afterDate".
 * 
 * @author JosephWang
 * 
 */
@SuppressLint("HandlerLeak")
public class WheelFullDateDialog implements OnKeyListener {
	public static final String TAG = WheelFullDateDialog.class.getSimpleName();
	private Calendar currentCalendar = Calendar.getInstance();
	private Calendar clickCalendar = Calendar.getInstance();
	private Calendar beforeDate;

	private OnShowWheelDialogListener dialogListener = new OnShowWheelDialogListener()
	{
		@Override
		public void onShowWheel(int viewId, WheelFullDatePicker picker)
		{
			picker.scrollToAfterDate();
		}
	};

	private boolean isTouchOutSideCancelable = true;

	public boolean isTouchOutSideCancelable() {
		return isTouchOutSideCancelable;
	}

	/**
	 * 設定是否可以由外部點擊，來關閉poup視窗
	 *
	 * @param isTouchOutSideCancelable
	 */
	public void setTouchOutSideCancelable(boolean isTouchOutSideCancelable)
	{
		this.isTouchOutSideCancelable = isTouchOutSideCancelable;
		if (dialog != null)
		{
			dialog.setCancelable(isTouchOutSideCancelable());
		}
	}

	public OnShowWheelDialogListener getOnShowWheelDialogListener() {
		return dialogListener;
	}

	/**
	 * 增加直接設定 OnShowWheelListener
	 * 
	 * @param showWheelListener
	 */
	public void setOnShowWheelDialogListener(OnShowWheelDialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}

	private Message wheelMsg = new Message();
	private View wheel;
	private Dialog dialog;
	private WheelControlListener<Calendar> controllerListenr;
	private Context act;
	private WheelFullDatePicker date_picker;

	private String year;
	private String month;
	private String date;

	public Calendar getBeforeDate() {
		return beforeDate;
	}

	/***
	 * As the semantics in method.You can restrict ceiling and flooring in the
	 * period of</br> available time in WheelFullDatePicker.Whenever you want to
	 * set WheelFullDatePicker be restricted or not, you can trigger it through
	 * {@link #WheelFullDatePicker.setCanRestrictBetweenDate(boolean setRestrictBetweenDate)}
	 * .Therefore,only under those condition that</br> 1,
	 * isSetRestrictBetweenDate() == true </br> 2, beforeDate!= null </br> 3,
	 * afterDate!= null,and this mechanism will be triggered.</br> Be careful
	 * that not to set afterDate earlier than beforeDate!!</br> </br>
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
	public void setRestrictDate(Calendar beforeDate, Calendar afterDate) {
		if (beforeDate.after(afterDate)) {
			throw new IllegalArgumentException("This is weird that setting afterDate earlier than beforeDate!!! You must think more.");
		} else {
			this.beforeDate = beforeDate;
			this.afterDate = afterDate;
		}
	}

	private boolean showTaiwanYear = false;

	public boolean isShowTaiwanYear() {
		return showTaiwanYear;
	}

	/******** 是否顯示民國年 *****/
	public void setShowTaiwanYear(boolean showTaiwanYear) {
		this.showTaiwanYear = showTaiwanYear;
	}

	private Calendar afterDate;

	public Calendar getAfterDate() {
		return afterDate;
	}

	public Calendar getClickCalendar() {
		return clickCalendar;
	}

	public Calendar getSelectCalendar() {
		return currentCalendar;
	}

	public String getYear() {
		return year;
	}

	public String getChineseYear() {
		if (year != null && !year.equals("")) {
			String result = "";
			try {
				result = "" + (Integer.parseInt(year) - 1911);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return "";
		}
	}

	public String getMonth() {
		return month;
	}

	public String getDate() {
		return date;
	}

	public String getChineseRequestDate() {
		StringBuffer result = new StringBuffer();
		result.append(getChineseYear());
		result.append(addZeroIfSmallThenTen(getMonth()));
		result.append(addZeroIfSmallThenTen(getDate()));
		return result.toString();
	}

	public String getRequestDate() {
		StringBuffer result = new StringBuffer();
		result.append(getYear());
		result.append(addZeroIfSmallThenTen(getMonth()));
		result.append(addZeroIfSmallThenTen(getDate()));
		return result.toString();
	}

	private String addZeroIfSmallThenTen(String res) {
		String result = "";
		if (Integer.parseInt(res) < 10) {
			result = "0" + res;
		} else {
			result = res;
		}
		return result;
	}

	public WheelFullDateDialog(Context act, WheelControlListener<Calendar> controllerListenr) {
		this.act = act;
		this.controllerListenr = controllerListenr;
		initWheel();
	}

	/**
	 * Setting the callBack of "WheelListener" with each each
	 * View,independently.Theorefore, API user can control the callback through
	 * "WheelControlListener" with the clue of View's Id.
	 * 
	 * 對每個View 設定獨立的WheelListener.如此一來，使用者可以藉由"WheelControlListener" 依據不同的View
	 * id 控制所有的滾輪行為.
	 * 
	 * @param eachView
	 * @param title
	 */
	public void setWheelListener(View eachView, String title) {
		eachView.setOnClickListener(getWheelClickListener(title));
	}

	private View.OnClickListener getWheelClickListener(final String title) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.clearAnimation();
				UIAdjuster.closeKeyBoard(act);
				if (showWheelListener.showWheel(v)) {
					wheelMsg.what = v.getId();
					wheelMsg.obj = currentCalendar;
					showDatePicker(v, title);
				}
			}
		};
	}

	private void initWheel() {
		if (dialog == null) {
			dialog = new Dialog(act, R.style.DialogSlideAnim);
			dialog.setCancelable(isTouchOutSideCancelable());
			dialog.setCanceledOnTouchOutside(isTouchOutSideCancelable());
			wheel = (View) LayoutInflater.from(act).inflate(R.layout.full_date_wheel_dialog_not_add_view, null);
			date_picker = (WheelFullDatePicker) wheel.findViewById(R.id.date_picker);
			date_picker.setShowTaiwanYear(false);
			wheel.findViewById(R.id.ok).setOnClickListener(wheelButtonListener);
			wheel.findViewById(R.id.cancel).setOnClickListener(wheelButtonListener);
		}
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

	public void showDatePicker(View trigger, String title) {
		date_picker.setCanSetUpPastTime(isCanSetUpPastTime());
		date_picker.setCanSetUpFutureTime(canSetUpFutureTime());
		date_picker.setSetCurrentTimeVisible(isCurrentTimeVisible());
		date_picker.setShowTaiwanYear(isShowTaiwanYear());
		date_picker.setWheelListener(date_picker.getId(), wheelListener);
		date_picker.setCanRestrictBetweenDate(true);
		if (beforeDate != null && afterDate != null) {
			try {
				date_picker.setCanRestrictBetweenDate(true);
				date_picker.setRestrictDate(beforeDate, afterDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != title) {
			((TextView) wheel.findViewById(R.id.title)).setText(title);
		}

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.copyFrom(dialog.getWindow().getAttributes());
		params.width = (int) UIAdjuster.computeDIPtoPixel(trigger.getContext(), 320);
		params.height  = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
//		params.y = (int) UIAdjuster.computeDIPtoPixel(act, getBottomMargin());
		dialog.getWindow().setAttributes(params);
		dialog.setContentView(wheel);
		dialog.show();

		if (dialogListener != null) {
			dialogListener.onShowWheel(wheelMsg.what, getPicker());
		} else {
			getPicker().scrollToAfterDate();
		}
	}

	public WheelFullDatePicker getPicker()
	{
		if (dialog != null && dialog.getWindow() != null && dialog.getWindow().getContainer() != null)
		{
			return ((WheelFullDatePicker) dialog.getWindow().getContainer().findViewById(R.id.date_picker));
		}
		return date_picker;
	}

	private int bottomMargin = 60;

	public int getBottomMargin() {
		return bottomMargin;
	}

	/**
	 * 設定視窗下方的margin，預設60dp
	 * 
	 * @param forMargin
	 */
	public void setBottomMargin(int forMargin) {
		this.bottomMargin = forMargin;
	}

	private View.OnClickListener wheelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ok) {
				if (date_picker.isScrollFinish()) {
					Calendar selectDate = date_picker.getSelectedTime();
					if (!isCanSetUpPastTime() &&
							selectDate.before(getTodayCalendar())) {
						date_picker.setToday();
						return;
					} else if (!canSetUpFutureTime() &&
							selectDate.after(getTodayCalendar())) {
						date_picker.setToday();
						return;
					}
					date_picker.stopScroll();
					clickCalendar = currentCalendar;
					wheelMsg.obj = selectDate;
					if (controllerListenr != null) {
						controllerListenr.handleClick(wheelMsg.what, (Calendar) wheelMsg.obj);
						dismiss();
					}
				}
			} else if (v.getId() == R.id.cancel) {
				dismiss();
			}
		}
	};

	/***
	 * Let WheelFullDatePicker to scroll to flooring time .
	 * 
	 * 讓滾輪時間選單顯示在下限時間
	 */
	public void scrollToAfterDate() {
		if (date_picker != null) {
			date_picker.scrollToAfterDate();
		}
	}

	/***
	 * Let WheelFullDatePicker to scroll to ceiling time .
	 * 
	 * 讓滾輪時間選單顯示在上限時間
	 */
	public void scrollToBeforeDate() {
		if (date_picker != null) {
			date_picker.scrollToBeforeDate();
		}
	}

	/**
	 * Be careful that certain time must be between ceiling Date and flooring
	 * Date,</br> Let WheelFullDatePicker to scroll to certain time .
	 * 
	 * @param certain
	 * @throws Exception
	 */
	public void scrollToCertainDate(Calendar certain) {
		if (date_picker != null) {
			try {
				date_picker.scrollToCertainDate(certain);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 關閉視窗
	 * 
	 */
	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private boolean currentTimeVisible = false;

	public boolean isCurrentTimeVisible() {
		return currentTimeVisible;
	}

	/**
	 * 是否及時顯示選取時間
	 * 
	 * @param currentTimeVisible
	 */
	public void setCurrentTimeVisible(boolean currentTimeVisible) {
		this.currentTimeVisible = currentTimeVisible;
	}

	private boolean canSetUpPastTime = true;

	public boolean isCanSetUpPastTime() {
		return canSetUpPastTime;
	}

	/*****
	 * Only as Setting this attribute that can user restrict the period of time
	 * to be earlier than today as "true",</br> and whenever user select as
	 * "false".
	 * 
	 * @return boolean 只有設定此屬性，為"true" 能限制使用者設定早於今天，為"false" 無限制.
	 */
	public void setCanSetUpPastTime(boolean canSetUpPastTime) {
		this.canSetUpPastTime = canSetUpPastTime;
	}

	private boolean canSetUpFutureTime = true;

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
	}

	public static Calendar getTodayCalendar() {
		long mUpadteTime = System.currentTimeMillis();
		String currentTime = new SimpleDateFormat("yyyy/MM/dd").format(new Date(mUpadteTime));
		String YEAR_START = currentTime.substring(0, 4);
		int year = Integer.parseInt(YEAR_START);
		int month = Integer.parseInt(currentTime.substring(5, 7));
		int date = Integer.parseInt(currentTime.substring(8, 10));

		Calendar currCalendar = Calendar.getInstance();
		currCalendar.clear();
		currCalendar.set(Calendar.YEAR, year);
		currCalendar.set(Calendar.MONTH, month - 1);
		currCalendar.set(Calendar.DAY_OF_MONTH, date);
		return currCalendar;
	}

	public static String getTodayString() {
		long mUpadteTime = System.currentTimeMillis();
		String currentTime = new SimpleDateFormat("yyyy/MM/dd").format(new Date(mUpadteTime));
		String YEAR_START = currentTime.substring(0, 4);
		String year = YEAR_START;
		String month = currentTime.substring(5, 7);
		String date = currentTime.substring(8, 10);
		return "" + year + month + date;
	}

	public static String getChinaTodayString() {
		long mUpadteTime = System.currentTimeMillis();
		String currentTime = new SimpleDateFormat("yyyy/MM/dd").format(new Date(mUpadteTime));
		String YEAR_START = currentTime.substring(0, 4);
		String year = String.valueOf(Integer.parseInt(YEAR_START) - 1911);
		String month = currentTime.substring(5, 7);
		String date = currentTime.substring(8, 10);
		return "" + year + month + date;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		dismiss();
		return onKey(v, keyCode, event);
	}

	private WheelFullDateSyncListener wheelListener = new WheelFullDateSyncListener() {
		@Override
		public void handleDate(int viewID, Calendar calendar) {
			currentCalendar = calendar;
			if (date_picker != null) {
				year = "" + calendar.get(Calendar.YEAR);
				month = "" + (calendar.get(Calendar.MONTH) + 1);
				date = "" + calendar.get(Calendar.DATE);
			}
		}
	};
}