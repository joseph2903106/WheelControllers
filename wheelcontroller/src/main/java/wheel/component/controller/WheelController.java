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
package wheel.component.controller;

import wheel.component.genview.GenWheelText;
import wheel.component.genview.GenWheelView;
import wheel.component.genview.UnSupportedWheelViewException;
import wheel.component.genview.WheelGeneralAdapter;
import wheel.component.utils.UIAdjuster;
import wheel.component.utils.WheelUtility;
import wheel.component.view.WheelControlListener;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import mma.mtake.wheel.component.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Message;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 利用WindowManager 動態的顯示滾輪視窗。</br> 請用WheelControlListener 接收callback與選擇的結果</br>
 * OnShowWheelListenr --->可以在滾輪顯示之前，做一些動作，如提示視窗
 * 
 * A convenient class controls all wheel dialog in the same activity through the
 * single {@link #WheelControlListener}.</br> Based on the reason that
 * diversifying the situation on each function,designing this controller has to
 * be the independent object. </br>Adding {@link #OnshowWheelListenr} that gives
 * the chance to handling something {@link #showWheel(View v)} occurred while
 * {@link #onTouchEvent} is triggered,before wheel Dialog is showing. This
 * controller uses the id of view to distinguish the different
 * {@link #onTouchEvent} what view is triggered . Through the clue of View's
 * Id,the WheelControllerListener which receives the call back has to be
 * designed to be like follows example:</br>
 * 
 * @author JosephWang
 * 
 *         <pre class="prettyprint">
 * WheelController controller;
 * String[] testTomAccount = new String[] { &quot;512-12345678&quot;, &quot;512-543543254&quot;, &quot;512-543543254&quot;, &quot;512-54254254&quot;, &quot;512-25453878&quot;, &quot;512-75836786&quot; };
 * TextView viewIdOne;
 * 
 * controller = new WheelController(this, (RelativeLayout) findViewById(R.id.layout), wListener);
 * viewIdOne = (TextView) findViewById(R.id.viewIdOne);
 * 
 * controller.setWheelListener(inAccount, new String[] { &quot;&quot; }, showWheelListener);
 * 
 * private WheelControllerListener wListener = new WheelControllerListener() {
 * 	&#064;Override
 * 	public void handleClick(int viewId, Object obj) {
 * 		switch (viewId) {
 * 		case R.id.out_account:// 轉出帳號
 * 			outAccount.setText(String.valueOf(obj));
 * 			getAvailableBalance();
 * 			break;
 * 		case R.id.search_bank_code:// ﻿查詢代號
 * 			bankCode.setText(bankCodeData.get(wController.getIndex()).get(&quot;BankId&quot;));
 * 			intentData.BanName = bankCodeData.get(wController.getIndex()).get(&quot;BankDesc&quot;);
 * 			inAccount.setText(&quot;&quot;);
 * 			break;
 * 		case R.id.in_account:// 轉入帳號
 * 		case R.id.bank_code:// ﻿查詢代號
 * 			if (isSelectAccount) {
 * 				bankCode.setText(inAccounts.get(wController.getIndex()).get(&quot;Kinbr&quot;));
 * 				inAccount.setText(inAccounts.get(wController.getIndex()).get(&quot;AccountNoIn&quot;));
 * 				intentData.BanName = inAccounts.get(wController.getIndex()).get(&quot;Kinbr&quot;) + &quot; &quot; + inAccounts.get(wController.getIndex()).get(&quot;ChinKinbr&quot;);
 * 			}
 * 			break;
 * 		}
 * 	}
 * };
 * private OnShowWheelListener showWheelListener = new OnShowWheelListener() {
 * 	&#064;Override
 * 	public boolean showWheel(View v) {
 * 		UIAdjuster.closeKeyBoard(NoneAgreeTransfer.this);
 * 		if (v.getId() == R.id.out_account) {
 * 			if (outAccountArray == null || outAccountArray.length == 0) {
 * 				JDialog.showMessage(NoneAgreeTransfer.this, &quot;錯誤&quot;, &quot;無轉出帳號可選擇&quot;);
 * 				return false;
 * 			} else {
 * 				return true;
 * 			}
 * 		} else if (v.getId() == R.id.in_account || v.getId() == R.id.bank_code) {
 * 			if (outAccount.getText().toString().equals(&quot;&quot;) &amp;&amp; isSelectAccount) {
 * 				JDialog.showMessage(NoneAgreeTransfer.this, &quot;錯誤&quot;, &quot;請先選擇轉出帳號&quot;);
 * 				return false;
 * 			} else if (inAccounts != null &amp;&amp; inAccounts.size() == 0 &amp;&amp; isSelectAccount) {
 * 				JDialog.showMessage(NoneAgreeTransfer.this, &quot;錯誤&quot;, &quot;此轉出帳號無約定轉入帳號&quot;);
 * 				return false;
 * 			} else {
 * 				if (isSelectAccount) {
 * 					return true;
 * 				} else {
 * 					inAccount.requestFocus();
 * 					UIAdjuster.showKeyBoard(NoneAgreeTransfer.this, inAccount);
 * 					return false;
 * 				}
 * 			}
 * 		} else if (v.getId() == R.id.search_bank_code) {
 * 			if (bankCodeData == null || (bankCodeData != null &amp;&amp; bankCodeData.size() == 0)) {
 * 				JDialog.showMessage(NoneAgreeTransfer.this, &quot;錯誤&quot;, &quot;請先選擇轉出帳號&quot;);
 * 				return false;
 * 			} else {
 * 				return true;
 * 			}
 * 		} else {
 * 			return true;
 * 		}
 * 	}
 * };
 * @author JosephWang
 * </pre>
 */
public class WheelController implements OnKeyListener {
	public static final String TAG = WheelController.class.getSimpleName();
	private Dialog dialog;
	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	private SparseArray<Object> collection = new SparseArray<Object>();
	private Message wheelMsg = new Message();
	private Object sArray;// 顯示在滾輪上的資料結構
	@SuppressWarnings("rawtypes")
	private WheelControlListener controllerListener;
	private WheelView wheelView;
	private int index = 0;
	private View wheel;
	private GenWheelView genView;
	private TextView titleView;
	private String titleText = "";
	private int currentClickViewId = 0;

	private boolean isTouchOutSideCancelable = true;

	public boolean isTouchOutSideCancelable() {
		return isTouchOutSideCancelable;
	}

	/**
	 * 設定是否可以由外部點擊，來關閉poup視窗
	 *
	 * @param isTouchOutSideCancelable
	 */
	public void setTouchOutSideCancelable(boolean isTouchOutSideCancelable) {
		this.isTouchOutSideCancelable = isTouchOutSideCancelable;
		if (dialog != null)
		{
			dialog.setCancelable(isTouchOutSideCancelable());
		}
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	/**
	 * get The index in Current Select Data Collection.</br>
	 * 
	 * 返回目前所選資料集合的index
	 * 
	 * @return index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * get The size in Current Select Data Collection.</br>
	 * 
	 * 返回目前所選資料集合的大小
	 * 
	 * @return size
	 * @throws IllegalAccessException 
	 */
	
	public int getDataSize(){
		if (currentClickViewId > 0) {
			return getDataSize(currentClickViewId);
		} else {
			return 0;
		}
	}
	
	public int getDataSize(int viewId) {
		return WheelUtility.getDataSize(collection.get(viewId));
	}

	public Object getDataByIndex(int index)
	{
		return getDataByIndex(currentClickViewId, index);
	}
	
	/********************
	 * 依據index 取得物件
	 * ***********************/
	public Object getDataByIndex(int viewId, int index)  {
		if (collection.size() > 0) {
			return WheelUtility.getDataByIndex(collection.get(viewId), index);	
		} else {
			return 0;
		}
	}
	private Context activity;

	/**
	 * 設定顯示一般文字的滾輪
	 * 
	 * @param curr
	 * @param controllerListenr
	 * @param genView
	 */
	@SuppressWarnings("rawtypes")
	public WheelController(Context curr, WheelControlListener controllerListenr) {
		this(curr, controllerListenr, null);
	}

	/**
	 * 設定顯示特定樣式的滾輪
	 * 
	 * @param curr
	 * @param controllerListenr
	 * @param genView
	 */
	@SuppressWarnings("rawtypes")
	public WheelController(Context curr, WheelControlListener controllerListenr, GenWheelView genView) {
		this.activity = curr;
		this.controllerListener = controllerListenr;
		this.genView = genView;
		initWheel();
		initDailog();
	}

	private void initDailog() {
		if (dialog == null) {
			dialog = new Dialog(activity, R.style.DialogSlideAnim);
			dialog.setCanceledOnTouchOutside(isTouchOutSideCancelable());
			dialog.setCancelable(isTouchOutSideCancelable());
		}
	}

	/**
	 * 對個別的的View設定滾輪事件
	 * 
	 * @param eachView
	 * @param collection
	 *            在與許的資料結構 (Only support List, Map,Object
	 *            Array,Cursor,SparseArray
	 *            ,SparseBooleanArray,SparseIntArray,Vector, and basic data
	 *            type)
	 */

	public void setWheelListener(View eachView, Object collection) {
		eachView.setOnClickListener(getWheelListener(collection, 1));
	}

	/**
	 * 對個別的的View設定滾輪事件
	 * 
	 * @param eachView
	 * @param collection
	 *            在與許的資料結構 (Only support List, Map,Object
	 *            Array,Cursor,SparseArray
	 *            ,SparseBooleanArray,SparseIntArray,Vector, and basic data
	 *            type)
	 * @param line
	 *            顯示文字行數
	 */
	public void setWheelListener(View eachView, Object collection, int line) {
		eachView.setOnClickListener(getWheelListener(collection, line));
	}

	private View.OnClickListener getWheelListener(final Object data, final int line) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentClickViewId = v.getId();
				UIAdjuster.closeKeyBoard(activity);
				if (onShowWheelListener.showWheel(v)) {
					index = 0;
					wheelMsg.what = v.getId();
					collection.put(v.getId(), data);
					WheelUtility.setUpWheelSelectData(data, wheelMsg);
					changeInPutItems(v, data, line);
				}				
			}
		};
	}

	/**
	 * 初始化滾輪視窗元件
	 */
	private void initWheel() {
		wheel = (View) LayoutInflater.from(activity).inflate(R.layout.wheel, null);
		titleView = (TextView) wheel.findViewById(R.id.title);
		wheelView = (WheelView) wheel.findViewById(R.id.wheel_view);
		wheelView.addChangingListener(changedListener);
		wheelView.addScrollingListener(scrollListener);
		wheel.findViewById(R.id.ok).setOnClickListener(buttonClickListener);
		wheel.findViewById(R.id.cancel).setOnClickListener(buttonClickListener);
	}

	private void changeInPutItems(View v, Object data, final int line) {
		titleView.setText("" + getTitleText());
		if (collection.get(v.getId()) == null) {
			sArray = new Object();
			collection.put(v.getId(), sArray);
		}
		sArray = collection.get(v.getId());
		index = 0;
		wheelView.setCurrentItem(0);
		if (genView == null) {
			genView = new GenWheelText(line, textSize);
		}
		WheelGeneralAdapter adapter = new WheelGeneralAdapter(activity, genView);
		try {
			adapter.setData(sArray);
		} catch (UnSupportedWheelViewException e) {
			e.printStackTrace();
		}
		wheelView.setViewAdapter(adapter);

		/******************** 初始化視窗 ***********************/
		params.copyFrom(dialog.getWindow().getAttributes());
		params.width = (int) UIAdjuster.computeDIPtoPixel(v.getContext(), 320);
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
//		params.y = (int) UIAdjuster.computeDIPtoPixel(act, getBottomMargin());
		dialog.getWindow().setAttributes(params);
		dialog.setContentView(wheel);
		dialog.show();
		/******************** 初始化視窗 ***********************/
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			index = newValue;
			wheelMsg.obj = getDataByIndex(newValue);
		}
	};

	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ok) {
				if (isScrollFinish()) {
					index = wheelView.getCurrentItem();
					wheelMsg.obj = getDataByIndex(index);
					controllerListener.handleClick(wheelMsg.what, wheelMsg.obj);
					dismiss();
				}
			} else if (v.getId() == R.id.cancel) {
				dismiss();
			}
		}
	};

	public Object getSelectData() {
		return wheelMsg.obj;
	}

	private boolean isScrollFinish = true;

	/**
	 * 滾輪是否結束滾動的動畫
	 * 
	 * @return boolean
	 */
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
			index = wheel.getCurrentItem();
			wheelMsg.obj = getDataByIndex(index);
		}
	};

	private int textSize = 22;

	public int getTextSize() {
		return textSize;
	}

	/**
	 * 設定顯示在滾輪的字體大小 in dip，預設22dip
	 * 
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
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

	/**
	 * Close the WheelDialog.</br> 關閉滾輪
	 * **/
	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public OnShowWheelListener onShowWheelListener = new OnShowWheelListener() {
		@Override
		public boolean showWheel(View v) {
			return true;
		}
	};

	public OnShowWheelListener getOnshowWheelListenr() {
		return onShowWheelListener;
	}

	/**
	 * 設定在滾輪顯示之前，可以做預設動作的callBack return true : 顯示滾輪視窗</br> return false :
	 * 不顯示滾輪視窗
	 * 
	 * @author JosephWang
	 * 
	 */
	public void setOnShowWheelListenr(OnShowWheelListener onshowWheelListenr) {
		this.onShowWheelListener = onshowWheelListenr;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		dismiss();
		return onKey(v, keyCode, event);
	}
}