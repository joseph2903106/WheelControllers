package wheel.component.view;import android.content.Context;import android.os.Handler;import android.os.Message;import android.util.AttributeSet;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.LinearLayout;import android.widget.TextView;import java.util.HashMap;import kankan.wheel.widget.OnWheelChangedListener;import kankan.wheel.widget.OnWheelScrollListener;import kankan.wheel.widget.WheelView;import mma.mtake.wheel.component.R;import wheel.component.genview.GenWheelText;import wheel.component.genview.GenWheelView;import wheel.component.genview.WheelGeneralAdapter;public class WheelFourDimensionPicker extends LinearLayout {	public static final String TAG = WheelTwoDimensionPicker.class.getSimpleName();	private final static int animationTime = 200;	private View triggerView;	private Context context;	private TextView select_data;	private WheelView one;	private WheelView two;	private WheelView third;	private WheelView four;			private String[] oneData = { "" };	private String[] oneDataDisplay = { "" };	private HashMap<String, String[]> twoData = new HashMap<String, String[]>();	private HashMap<String, String[]> twoDataDisplay = new HashMap<String, String[]>();		private HashMap<String, String[]> thirdData = new HashMap<String, String[]>();	private HashMap<String, String[]> thirdDataDisplay = new HashMap<String, String[]>();		private HashMap<String, String[]> fourData = new HashMap<String, String[]>();	private HashMap<String, String[]> fourDataDisplay = new HashMap<String, String[]>();			private String[] dateArray;	private String[] dateArrayForDisplay;	private int indexOne = 0;	private int indexTwo = 0;	private String currSelectOne;	private String currSelectTwo;	private String currSelectThird;	private String currSelectFour;		public String getCurrSelectOne() {		return currSelectOne;	}	public String getCurrSelectTwo() {		return currSelectTwo;	}	public String getCurrSelectThird() {		return currSelectThird;	}	public String getCurrSelectFour() {		return currSelectFour;	}		private String currSelectOneForDisplay;	private String currSelectTwoForDisplay;	private String currSelectThirdForDisplay;	private String currSelectFourForDisplay;		private ViewGroup select_data_container;	private GenWheelView oneGenView;	private GenWheelView twoGenView;	private int textSize = 22;	public int getTextSize() {		return textSize;	}	public void setTextSize(int textSize) {		this.textSize = textSize;	}	private int leftWheelVisibleLine = 1;	public int getLeftWheelVisibleLine() {		return leftWheelVisibleLine;	}	public void setLeftWheelVisibleLine(int leftWheelVisibleLine) {		this.leftWheelVisibleLine = leftWheelVisibleLine;	}	private int rightWheelVisibleLine = 1;	public int getRightWheelVisibleLine() {		return rightWheelVisibleLine;	}	public void setRightWheelVisibleLine(int rightWheelVisibleLine) {		this.rightWheelVisibleLine = rightWheelVisibleLine;	}	private int viewID;	private WheelFourControlListener wheelListener;	public interface WheelFourControlListener {		public void handleSelect(int viewID, FourWheelSelectData data, int leftIndex, int rightIndex);	}		private boolean currentSelectVisible = false;	public boolean isCurrentSelectVisible() {		return currentSelectVisible;	}	public void setCurrentSelectVisible(boolean currentSelectVisible) {		this.currentSelectVisible = currentSelectVisible;		if (select_data_container != null) {			if (this.currentSelectVisible) {				select_data_container.setVisibility(View.VISIBLE);			} else {				select_data_container.setVisibility(View.GONE);			}		}	}	private boolean alwaysShowTwoWheel = true;	public boolean isAlwaysShowTwoWheel() {		return alwaysShowTwoWheel;	}	public void setAlwaysShowTwoWheel(boolean alwaysShowTwoWheel) {		this.alwaysShowTwoWheel = alwaysShowTwoWheel;	}	public void setViewGenerator(GenWheelView genView) {		setViewGenerator(genView, genView);	}	public void setViewGenerator(GenWheelView leftGenView, GenWheelView rightGenView) {		this.oneGenView = leftGenView;		this.twoGenView = rightGenView;		setUpData();		new Thread(setTodayThread).start();	}	public void setAllData(String[] oneData, 						   HashMap<String, String[]> twoData,						   HashMap<String, String[]> thirdData,						   HashMap<String, String[]> fourData) {		setAllData(oneData, oneData, twoData, twoData, thirdData, thirdData, fourData, fourData);	}		public void setAllData(String[] oneData, String[] oneDataForDisplay, 					       HashMap<String, String[]> twoData, HashMap<String, String[]> twoDataDisplay,					       HashMap<String, String[]> thirdData, HashMap<String, String[]> thirdDataDisplay,					       HashMap<String, String[]> fourData, HashMap<String, String[]> fourDataDisplay) {		indexOne = 0;		indexTwo = 0;		this.oneData = oneData;		this.oneDataDisplay = oneDataForDisplay;		this.twoData = twoData;		this.twoDataDisplay = twoDataDisplay;				this.thirdData = thirdData;		this.thirdDataDisplay = thirdDataDisplay;				this.fourData = fourData;		this.fourDataDisplay = fourDataDisplay;				if (one != null) {			one.setCurrentItem(0);		} 		if (two != null) {			two.setCurrentItem(0);		}		if (third != null) {			third.setCurrentItem(0);		}		if (four != null) {			four.setCurrentItem(0);		}		setUpData();		new Thread(setTodayThread).start();	}	public WheelFourDimensionPicker(Context context) {		super(context);		this.context = context;		initData();		initDatePicker();		new Thread(setTodayThread).start();	}	public WheelFourDimensionPicker(Context context, AttributeSet attrs) {		super(context, attrs);		this.context = context;		initData();		initDatePicker();		new Thread(setTodayThread).start();	}	private void initData() {		twoData.clear();		twoDataDisplay.clear();		for (int i = 0; i < oneData.length; i++) {			twoData.put(oneData[i], dateArray);			twoDataDisplay.put(oneData[i], dateArrayForDisplay);						thirdData.put(oneData[i], dateArray);			thirdDataDisplay.put(oneData[i], dateArrayForDisplay);						fourData.put(oneData[i], dateArray);			fourDataDisplay.put(oneData[i], dateArrayForDisplay);		}		initDatePicker();		setUpData();	}	public void initDatePicker() {		LinearLayout wheel = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.four_dimensional_picker_wheel, this, true);		select_data_container = (ViewGroup) wheel.findViewById(R.id.select_data_container);		select_data = (TextView) wheel.findViewById(R.id.select_data);				one = (WheelView) wheel.findViewById(R.id.one);		two = (WheelView) wheel.findViewById(R.id.two);		third = (WheelView) wheel.findViewById(R.id.third);		four = (WheelView) wheel.findViewById(R.id.four);						one.addScrollingListener(scrollListener);		two.addScrollingListener(scrollListener);		third.addScrollingListener(scrollListener);		four.addScrollingListener(scrollListener);		currSelectOne = oneData[0];		currSelectOneForDisplay = oneDataDisplay[0];				if ((twoData.get(oneData[0])) != null) {			currSelectTwo = twoData.get(oneData[0])[0];			currSelectTwoForDisplay = twoDataDisplay.get(oneData[0])[0];		}		if (twoData != null &&			twoData.get(oneData[0]) != null &&			thirdData != null &&			thirdData.get(twoData.get(oneData[0])[0])[0]!= null) {			currSelectThird = thirdData.get(twoData.get(oneData[0])[0])[0];			currSelectTwoForDisplay = thirdDataDisplay .get(twoDataDisplay .get(oneDataDisplay[0])[0])[0];		}				sendDataObject();		select_data.setText(currSelectOneForDisplay + " " + currSelectTwoForDisplay);	}	private void setUpData() {		if (oneDataDisplay != null && oneDataDisplay.length > 0 && twoData != null && twoDataDisplay != null && twoDataDisplay.get(oneData[0]) != null) {			setWheeViewlListener(one, oneDataDisplay);			setWheeViewlListener(two, twoDataDisplay.get(oneData[0]));		}	}	private FourWheelSelectData currSelectTotal = new FourWheelSelectData();	public FourWheelSelectData getCurrSelectTotal() {		return currSelectTotal;	}	private void adjustRightWheel() {		if (isAlwaysShowTwoWheel()) {			two.setVisibility(View.VISIBLE);		} else {			two.setVisibility(View.GONE);		}	}	private void sendDataObject() {		if (one.getCurrentItem() >= oneData.length) {			one.setCurrentItem(0);		}		currSelectTotal.itemOneData = oneData[one.getCurrentItem()];		currSelectTotal.itemOneDataDisplay = oneDataDisplay[one.getCurrentItem()];		if (two.getVisibility() == View.VISIBLE) {			if (twoData != null && twoData.get(currSelectTotal.itemOneData) != null) {				two.setVisibility(View.VISIBLE);				currSelectTotal.itemTwoData = twoData.get(currSelectTotal.itemOneData)[two.getCurrentItem()];				if (two.getCurrentItem() >= twoDataDisplay.get(currSelectTotal.itemOneData).length) {					two.setCurrentItem(0);				}				currSelectTotal.itemTwoDataDisplay = twoDataDisplay.get(currSelectTotal.itemOneData)[two.getCurrentItem()];				indexTwo = two.getCurrentItem();			} else {				adjustRightWheel();				indexTwo = 0;				currSelectTotal.itemTwoData = "";				currSelectTotal.itemTwoDataDisplay = "";			}		} else {			indexTwo = 0;			currSelectTotal.itemTwoData = "";			currSelectTotal.itemTwoDataDisplay = "";		}		int currentViewId = 0;		if (triggerView != null) {			currentViewId = triggerView.getId();		} else {			currentViewId = viewID;		}		if (wheelListener != null) {			wheelListener.handleSelect(currentViewId, currSelectTotal, one.getCurrentItem(), indexTwo);		}	}	private GenWheelText generalText;	private void setWheeViewlListener(WheelView wheelView, String[] data) {		if (oneGenView != null && twoGenView != null) {			if (wheelView.getId() == R.id.left) {				WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, oneGenView);				viewAdapter.setData(data);				one.setViewAdapter(viewAdapter);			} else if (wheelView.getId() == R.id.right) {				WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, twoGenView);				viewAdapter.setData(data);				two.setViewAdapter(viewAdapter);			}		} else {			if (wheelView.getId() == R.id.left) {				generalText = new GenWheelText(getLeftWheelVisibleLine(), getTextSize());			} else if (wheelView.getId() == R.id.right) {				generalText = new GenWheelText(getRightWheelVisibleLine(), getTextSize());			}			WheelGeneralAdapter viewAdapter = new WheelGeneralAdapter(context, generalText);			viewAdapter.setData(data);			wheelView.setViewAdapter(viewAdapter);		}		wheelView.addChangingListener(changedListener);	}	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {		@Override		public void onChanged(WheelView wheel, int oldValue, int newValue) {			if (wheel.getId() == R.id.left) {				prepareLeftData();				initRightData(prepareRightData());			} else if (wheel.getId() == R.id.right) {				checkRightData();				setUprightData();			}			checkRightData();		}	};	private void checkRightData() {		/***		 * Using "currSelectLeft" as the clue to find out whatever "indexRight"		 * is bigger than the size of "rightData" or not.		 ****/		/****************** 已左邊滾輪的現在選的資料,去找右邊滾輪的資料集合,看 index有沒有超過右邊的滾輪的資料長度 ********************/		if (twoData.get(currSelectOne) != null && (twoData.get(currSelectOne)).length > 0) {			two.setVisibility(View.VISIBLE);			if (two.getCurrentItem() > twoData.get(oneData[one.getCurrentItem()]).length) {				two.setCurrentItem(two.getViewAdapter().getItemsCount() - 1, false);			} else if (indexTwo > two.getViewAdapter().getItemsCount() - 1) {				two.setCurrentItem(two.getViewAdapter().getItemsCount() - 1, false);			}		} else {			if (isAlwaysShowTwoWheel()) {				two.setVisibility(View.VISIBLE);			} else {				if (two.getViewAdapter() != null && two.getViewAdapter().getItemsCount() > 0) {					two.setVisibility(View.VISIBLE);				} else {					two.setVisibility(View.GONE);				}			}		}	}	public void backToZeroIndex() {		if (one != null && two != null) {			one.setCurrentItem(0);			two.setCurrentItem(0);		}	}	private void initRightData(String[] data) {		if (data != null) {			setWheeViewlListener(two, data);		}	};	private void prepareLeftData() {		if (oneData != null && oneData.length > 0 && oneDataDisplay != null && oneDataDisplay.length > 0) {			currSelectOne = oneData[one.getCurrentItem()];			currSelectOneForDisplay = oneDataDisplay[one.getCurrentItem()];		}		indexOne = one.getCurrentItem();		checkRightData();	}	private String[] prepareRightData() {		String[] data = null;		if (twoData != null && twoData.get(oneData[one.getCurrentItem()]) != null) {			data = twoDataDisplay.get(oneData[one.getCurrentItem()]);		}		return data;	}	private void setUprightData() {		if (twoData.get(currSelectOne) != null && twoDataDisplay.get(currSelectOne) != null) {			if (twoData.get(oneData[one.getCurrentItem()]).length < two.getCurrentItem()) {				two.setCurrentItem(twoData.get(oneData[one.getCurrentItem()]).length - 1, false);			}			currSelectTwo = twoData.get(oneData[one.getCurrentItem()])[two.getCurrentItem()];			currSelectTwoForDisplay = twoDataDisplay.get(oneData[one.getCurrentItem()])[two.getCurrentItem()];			indexTwo = two.getCurrentItem();		} else {			indexTwo = 0;		}	}	public int getIndexLeft() {		return indexOne;	}	public int getIndexRight() {		return indexTwo;	}	private Runnable setTodayThread = new Runnable() {		@Override		public void run() {			try {				setTodayHandler.sendMessageDelayed(setTodayHandler.obtainMessage(), 100);			} catch (Exception e) {				e.printStackTrace();			}		}	};	private Handler setTodayHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			one.scroll(0, animationTime);			two.scroll(0, animationTime);			sendDataObject();		}	};	public void setWheelListener(int viewID, WheelFourControlListener wheelListener) {		this.viewID = viewID;		this.wheelListener = wheelListener;	}	/**	 * 目前所選資料 如果是文字的話，所選的資料結構	 * 	 * String leftData ---> 左邊滾輪所選資料 String leftDataDisplay ---> 左邊滾輪所選呈現資料	 * String rightData; ---> 右邊邊滾輪所選資料 String rightDataDisplay ---> 右邊滾輪所選呈現資料	 * 	 * @author josephWang	 * 	 */	public static class FourWheelSelectData {		public String itemOneData;		public String itemOneDataDisplay;				public String itemTwoData;		public String itemTwoDataDisplay;				public String itemThirdData;		public String itemThirdDataDisplay;				public String itemFourData;		public String itemFourDataDisplay;	}	private boolean isScrollFinish = true;	public boolean isScrollFinish() {		return isScrollFinish;	}	private OnWheelScrollListener scrollListener = new OnWheelScrollListener() {		@Override		public void onScrollingStarted(WheelView wheel) {			isScrollFinish = false;		}		@Override		public void onScrollingFinished(WheelView wheel) {			isScrollFinish = true;			indexOne = one.getCurrentItem();			indexTwo = two.getCurrentItem();			if (isScrollFinish) {				sendDataObject();				currSelectOneForDisplay = oneDataDisplay[one.getCurrentItem()];				if (two.getVisibility() == View.VISIBLE) {					if (twoData != null && twoData.get(currSelectTotal.itemOneData) != null) {						currSelectTwoForDisplay = twoDataDisplay.get(currSelectTotal.itemOneData)[two.getCurrentItem()];					} else {						currSelectTwoForDisplay = "";					}				} else {					currSelectTwoForDisplay = "";				}				select_data.setText(currSelectOneForDisplay + " " + currSelectTwoForDisplay);			}		}	};}