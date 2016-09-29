package wheel.component.demo;import android.app.Activity;import android.os.Bundle;import android.util.Log;import android.widget.TextView;import java.util.Calendar;import wheel.component.controller.WheelController;import wheel.component.datedialog.OnShowWheelDialogListener;import wheel.component.datedialog.WheelPopUpFullDateDialog;import wheel.component.view.WheelControlListener;import wheel.component.view.WheelFullDatePicker;public class WheelDialogActivity extends Activity {	private TextView test_left;	private TextView test_left_bottom;	private TextView test_right;	private TextView test_top;	private String[] data;	private String[] dataOne= new String[10];	private String[] dataTwo= new String[10];	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.test_popup);		test_left = (TextView) findViewById(R.id.test_left);		test_left_bottom = (TextView) findViewById(R.id.test_left_bottom);		test_right = (TextView) findViewById(R.id.test_right);		test_top = (TextView) findViewById(R.id.test_top);		for (int i = 0; i < 10; i++) {			dataOne[i] = "one " + i;			dataTwo[i] = "Two " + i;		}		//		WheelFullDateDialog fullDateDialog = new WheelFullDateDialog(this, listener1);////		fullDateDialog.setWheelListener(test_top, "test_top");////		WheelPopUpFullDateDialog fullPopUpDialog = new WheelPopUpFullDateDialog(this, listener1);////		fullPopUpDialog.setShowTaiwanYear(true);//		fullPopUpDialog.setCanSetUpFutureTime(true);//		fullPopUpDialog.setCanSetUpPastTime(true);//		fullPopUpDialog.setCanRestrictBetweenDate(true);//		fullPopUpDialog.setRestrictDate(beforeDate(), afterDate());////		fullPopUpDialog.setWheelListener(test_left_bottom, "test_left_bottom");//		fullPopUpDialog.setDialogListener(onShowWheelDialogListener);//		WheelDateDialog dialog = new WheelDateDialog(this, listener1);////		dialog.setWheelListener(test_right, "test_right");//		//		data = new String[20];//		for (int i = 0; i < 20; i++) {//			data[i] = "test" + i;//		}		WheelController controller = new WheelController(this, listener1);		controller.setWheelListener(test_left, dataOne);		controller.setWheelListener(test_right, dataTwo);	}	private WheelControlListener listener1 = new WheelControlListener() {		@Override		public void handleClick(int viewId, Object obj) {			if (viewId == R.id.test_left) {				test_left.setText(obj.toString());			} else if (viewId == R.id.test_right) {//				Calendar curr = Calendar.getInstance();//				curr.setTime((Date) obj);//				test_right.setText(WheelPopUpFullDateDialog.showCalendarText(curr));				test_right.setText(obj.toString());			} else if (viewId == R.id.test_top) {				test_top.setText(WheelPopUpFullDateDialog.showCalendarText((Calendar) obj));			}		}	};	private OnShowWheelDialogListener onShowWheelDialogListener = new OnShowWheelDialogListener() {		@Override		public void onShowWheel(int viewId, WheelFullDatePicker picker) {			if (viewId == R.id.test_left) {				picker.scrollToBeforeDate();			} else if (viewId == R.id.test_left_bottom) {				picker.scrollToAfterDate();			} else if (viewId == R.id.test_right) {				try {					picker.scrollToCertainDate(certainDate(1));				} catch (Exception e) {				}			} else if (viewId == R.id.test_right_bottom) {				try {					picker.scrollToCertainDate(certainDate(2));				} catch (Exception e) {				}			} else if (viewId == R.id.test_top) {				try {					picker.scrollToCertainDate(certainDate(3));				} catch (Exception e) {				}			} else if (viewId == R.id.test_special) {				try {					picker.scrollToCertainDate(certainDate(4));				} catch (Exception e) {				}			}		}	};	public Calendar certainDate(int index) {		Calendar want = Calendar.getInstance();		want.add(Calendar.MONTH, +index);		return want;	}	/** 上2個月月初 **/	public Calendar beforeDate() {		Calendar want = Calendar.getInstance();		want.add(Calendar.YEAR, -2);		want.add(Calendar.MONTH, -2);		want.set(Calendar.DAY_OF_MONTH, 15);// 下個月月底		Log.d("josephWang", "beforeDate " + WheelPopUpFullDateDialog.showCalendarText(want));		return want;	}	/** 下2個月月底 **/	public Calendar afterDate() {		Calendar want = Calendar.getInstance();		// want.add(Calendar.YEAR, 2);		want.add(Calendar.MONTH, 10);		want.set(Calendar.DAY_OF_MONTH, 15);// 下個月月底// 下個月月底		Log.d("josephWang", "afterDate " + WheelPopUpFullDateDialog.showCalendarText(want));		return want;	}}