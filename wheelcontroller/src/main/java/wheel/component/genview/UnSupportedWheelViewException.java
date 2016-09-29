package wheel.component.genview;

/**
 * Created by josephwang on 15/7/11.
 */

public class UnSupportedWheelViewException extends Exception {
    private static final long serialVersionUID = 1894662683963152958L;
    String mistake;

    public UnSupportedWheelViewException() {
        super();
        mistake = "Only support List, Map,Object Array,Cursor,SparseArray,SparseBooleanArray,SparseIntArray,Vector, and basic data type";
    }

    public UnSupportedWheelViewException(String err) {
        super(err); // call super class constructor
        mistake = err; // save message
    }

    public String getError() {
        return mistake;
    }
}
