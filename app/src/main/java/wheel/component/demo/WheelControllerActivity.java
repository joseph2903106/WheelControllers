package wheel.component.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import wheel.component.controller.OnShowWheelListener;
import wheel.component.controller.WheelController;
import wheel.component.genview.GenWheelView;
import wheel.component.view.WheelControlListener;
public class WheelControllerActivity extends Activity {
	private TextView test_left;
	private TextView test_right;
	private String[] data_test_left;
	private String[] data_right;
	private int[] drawableArray = { R.drawable.canada, R.drawable.france, R.drawable.ukraine, R.drawable.usa, R.drawable.canada, R.drawable.france, R.drawable.ukraine, R.drawable.usa };
	private String[] citys = { "Canada", "France", "Ukraine", "Usa", "Canada", "France", "Ukraine", "Usa" };
	private ArrayList<WheelData> listData = new ArrayList<WheelData>();
	private WheelController controller;
	private WheelController controller2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_controller);
		test_left = (TextView) findViewById(R.id.test_left);
		test_right = (TextView) findViewById(R.id.test_right);

		test_left.setText("General Wheel Adapter");
		test_right.setText("Custom Wheel Adapter");
		
		findViewById(R.id.picker).setVisibility(View.GONE);
		findViewById(R.id.container_2).setVisibility(View.GONE);

		initSpecialData();

		data_test_left = new String[20];
		data_right = new String[20];
		for (int i = 0; i < 20; i++) {
			data_test_left[i] = "data_test_left_" + i;
			data_right[i] = "data_right_" + i;
		}

		controller = new WheelController(this, listener);
		controller.setWheelListener(test_left, data_test_left);
		controller.setWheelListener(test_right, data_right);
//		controller.setOnShowWheelListenr(onShowWheelListenr);
		// WheelController controller2 = new WheelController(this, listener, new
		// GenView());
		controller2 = new WheelController(this, listener, new GenViewWithObject());

		// controller2.setWheelListener(test_right, citys);
		controller2.setWheelListener(test_right, listData);
	}

	private class GenView extends GenWheelView {
		@Override
		protected View genBody(Context context, View convertView, Object element, int position) {
			View body = getLayoutInflater().inflate(R.layout.custom_wheel_inner, null);
			ImageView icn = (ImageView) body.findViewById(R.id.icon);
			TextView text = (TextView) body.findViewById(R.id.text);
			icn.setBackgroundDrawable(getResources().getDrawable(drawableArray[position]));
			text.setText(element.toString());
			return body;
		}
	}
	
	private class GenViewWithObject extends GenWheelView {
		@Override
		protected View genBody(Context context, View convertView, Object element, int position) {
			WheelData inner = (WheelData) element;
			View body = getLayoutInflater().inflate(R.layout.custom_wheel_inner, null);
			ImageView icn = (ImageView) body.findViewById(R.id.icon);
			TextView text = (TextView) body.findViewById(R.id.text);
			icn.setBackgroundDrawable(getResources().getDrawable(inner.drawable));
			text.setText(inner.title);
			return body;
		}
	}

	private void initSpecialData() {
		listData.clear();
		for (int i = 0; i < citys.length; i++) {
			WheelData inner = new WheelData();
			inner.title = citys[i];
			inner.drawable = drawableArray[i];
			listData.add(inner);
		}
	}

	private class WheelData {
		public String title;
		public int drawable = 0;
	}

	private WheelControlListener listener = new WheelControlListener() {
		@Override
		public void handleClick(int viewId, Object obj) {
			if (viewId == R.id.test_left) {
				test_left.setText(obj.toString());
			} else if (viewId == R.id.test_right) {
				WheelData inner = (WheelData) obj;
				test_right.setText(inner.title);
				Log.d("josephwang", "controller2 getIndex " + controller2.getIndex());
			}
		}
	};

	private OnShowWheelListener onShowWheelListenr = new OnShowWheelListener() {

		@Override
		public boolean showWheel(View v) {
			switch (v.getId()) {
			case R.id.test_left:
				controller.setTitleText("Test");
			
				break;
			}
			if (v.getId() == R.id.test_left) {
				Toast.makeText(WheelControllerActivity.this, "onShowWheelListenr test_left", Toast.LENGTH_LONG).show();
			}
			return true;
		}
	};
}
