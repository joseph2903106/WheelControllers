package wheel.component.demo;import android.app.Activity;import android.content.Context;import android.os.Bundle;import android.support.v4.util.ArrayMap;import android.view.View;import android.widget.ImageView;import android.widget.TextView;import android.widget.Toast;import java.util.ArrayList;import java.util.List;import wheel.component.controller.OnShowWheelListener;import wheel.component.controller.TwoWheelDataCollection;import wheel.component.controller.WheelScrollToController;import wheel.component.controller.WheelTwoPopupController;import wheel.component.genview.GenWheelView;import wheel.component.view.WheelTwoControlListener;import wheel.component.view.WheelTwoDimensionPicker.TwoWheelSelectData;public class WheelTwoPopupControllerActivity extends Activity {	private TextView test_left;	private TextView test_right;	private TextView test_left_2;	private TextView test_right_2;	private int[] drawableLeftArray = {R.drawable.canada, R.drawable.france, R.drawable.ukraine, R.drawable.usa};	private String[] citys = {"Canada", "France", "Ukraine", "Usa"};	private int[] drawableRightArray = {R.drawable.japanmoney, R.drawable.usmoney, R.drawable.eruomoney, R.drawable.ukonwmoney};	private String[] money = {"japanMoney", "usMoney", "eruoMoney", "unKnonwMoney"};	private TwoWheelDataCollection twoWheelData = new TwoWheelDataCollection();	private TwoWheelDataCollection twoWheelData_2 = new TwoWheelDataCollection();	private String[] monthArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};	private WheelTwoPopupController controller;	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.test_controller);		test_left = (TextView) findViewById(R.id.test_left);		test_right = (TextView) findViewById(R.id.test_right);		test_left_2 = (TextView) findViewById(R.id.test_left_2);		test_right_2 = (TextView) findViewById(R.id.test_right_2);		findViewById(R.id.picker).setVisibility(View.GONE);		findViewById(R.id.picker_2).setVisibility(View.GONE);		twoWheelData.leftData = monthArray;		twoWheelData.leftDataForDisplay = monthArray;		twoWheelData.rightData = new ArrayMap<String, String[]>();		twoWheelData.rightDataForDisplay = new ArrayMap<String, String[]>();		twoWheelData_2.leftData = new String[0];		twoWheelData_2.leftDataForDisplay = new String[0];		twoWheelData_2.rightData = new ArrayMap<String, String[]>();		twoWheelData_2.rightDataForDisplay = new ArrayMap<String, String[]>();		initData();		controller = new WheelTwoPopupController(this, twoWheelListener);		controller.setWheelListener(test_left, twoWheelData);		controller.setWheelListener(test_right, twoWheelData);		controller.setScrollToController(scrollToController);		controller.setOnShowWheelListenr(onShowWheelListenr);				initData2();		// WheelTwoPopupController controller2 = new		// WheelTwoPopupController(this, twoWheelListener_2, new GenLeftView(),		// new GenRightView());		controller.setWheelListener(test_left_2, twoWheelData_2);		controller.setWheelListener(test_right_2, twoWheelData_2);	}	private WheelScrollToController scrollToController = new WheelScrollToController() {		@Override		public void onShowWheel(View v) {			controller.scrollWheelByIndex(3, 4);		}	};	private String[] rightDataTest2(int index) {		String[] result = new String[4];		for (int i = 0; i < 4; i++) {			result[i] = "index " + index;		}		return result;	}	private class GenLeftView extends GenWheelView {		@Override		protected View genBody(Context context, View convertView, Object element, int position) {			View body = getLayoutInflater().inflate(R.layout.custom_wheel_inner, null);			ImageView icn = (ImageView) body.findViewById(R.id.icon);			TextView text = (TextView) body.findViewById(R.id.text);			icn.setBackgroundDrawable(getResources().getDrawable(drawableLeftArray[position]));			text.setText(element.toString());			return body;		}	}	private class GenRightView extends GenWheelView {		@Override		protected View genBody(Context context, View convertView, Object element, int position) {			View body = getLayoutInflater().inflate(R.layout.custom_wheel_inner, null);			ImageView icn = (ImageView) body.findViewById(R.id.icon);			TextView text = (TextView) body.findViewById(R.id.text);			icn.setBackgroundDrawable(getResources().getDrawable(drawableRightArray[position]));			text.setText(element.toString());			return body;		}	}	private OnShowWheelListener onShowWheelListenr = new OnShowWheelListener() {		@Override		public boolean showWheel(View v) {			if (v.getId() == R.id.test_left) {				Toast.makeText(WheelTwoPopupControllerActivity.this, "onShowWheelListenr test_left", Toast.LENGTH_LONG).show();			} else if (v.getId() == R.id.test_left_2) {				Toast.makeText(WheelTwoPopupControllerActivity.this, "onShowWheelListenr test_left_2", Toast.LENGTH_LONG).show();			}			return true;		}	};	private void initData2() {		twoWheelData_2.rightData.clear();		twoWheelData_2.leftData = citys;		twoWheelData_2.leftDataForDisplay = citys;		for (int i = 0; i < 4; i++) {			if (i <= 2) {				twoWheelData_2.rightData.put(twoWheelData_2.leftData[i], rightDataTest2(i));				twoWheelData_2.rightDataForDisplay.put(twoWheelData_2.leftData[i], rightDataTest2(i));			}		}	}	private List<String> tmp = new ArrayList<String>();	private String[] dateArray;	private void initDateArray(int month) {		tmp.clear();		// Generate day string array		switch (month) {		/********** 大月 **************/		case 1:// 大月		case 3:		case 5:		case 7:		case 8:		case 10:		case 12:			produceDate(31);// 大月			break;		/********** 小月 **************/		case 4:		case 6:		case 9:		case 11:			produceDate(30);// 小月			break;		/********** 二月 **************/		case 2:			produceDate(28);// 小月			break;		}	}	private void produceDate(int size) {		for (int i = 1; i <= size; i++) {			tmp.add(String.valueOf(i));		}		dateArray = tmp.toArray(new String[0]);	}	private void initData() {		twoWheelData.rightData.clear();		twoWheelData.rightDataForDisplay.clear();		for (int i = 0; i < monthArray.length; i++) {			initDateArray(Integer.parseInt(monthArray[i]));			twoWheelData.rightData.put(monthArray[i], dateArray);			twoWheelData.rightDataForDisplay.put(monthArray[i], dateArray);		}	}	private String[] rightData2(int index) {		for (int i = 0; i < 4; i++) {			money[i] = "index " + index + " rightDataForDisplay " + i;		}		return money;	}	private WheelTwoControlListener twoWheelListener = new WheelTwoControlListener() {		@Override		public void handleSelect(int viewID, TwoWheelSelectData data, int leftIndex, int rightIndex) {			test_left.setText(data.leftDataDisplay);			test_right.setText(data.rightDataDisplay);		}	};	private WheelTwoControlListener twoWheelListener_2 = new WheelTwoControlListener() {		@Override		public void handleSelect(int viewID, TwoWheelSelectData data, int leftIndex, int rightIndex) {			test_left_2.setText(data.leftDataDisplay);			test_right_2.setText(data.rightDataDisplay);		}	};}