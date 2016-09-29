package wheel.component.genview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by josephwang on 15/7/11.
 */
public interface IGenWheelView {
    public View setup(Context context, int position, View convertView, ViewGroup parent, Object data);
}

