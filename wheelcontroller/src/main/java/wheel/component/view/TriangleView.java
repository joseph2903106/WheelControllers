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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import mma.mtake.wheel.component.R;
import wheel.component.utils.UIAdjuster;

/**
 * Just to present the direction in the triangle shape. You can design the
 * "TriangleView" with the attribute in XML as "shapeArrow". Such as that 'UP',
 * 'DOWN', 'LEFT',and 'RIGHT'.
 * 
 * 使用範例
 * 
 * <pre class="prettyprint">
 *  <RelativeLayout
 *             xmlns:android="http://schemas.android.com/apk/res/android"
 *             xmlns:triangle="http://schemas.android.com/apk/res-auto"
 *             android:layout_width="fill_parent"
 *             android:layout_height="wrap_content" >
 *        <mma.wheel.component.view.TriangleView
 *                 android:id="@+id/center_top_triangle"
 *                 android:layout_width="50dp"
 *                 android:layout_height="25dp"
 *                 triangle:shapeArrow="UP"
 *                 triangle:triangleColor="@color/arrow_bg"/>
 * *   </RelativeLayout>
 * </pre>
 * 
 * @author JosephWang
 * 
 */
public class TriangleView extends LinearLayout {
	private Paint trianglePaint;
	private Path trianglePath;
	private Context ctx;
	private Direction type = Direction.UP;
	private int color = R.color.arrow_bg;
	private int width = 40;
	@SuppressWarnings("deprecation")
	public TriangleView(Context context) {
		super(context);
		ctx = context;
		initStyle(null);
		shapeTriangle();
		setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));//
	}
	@SuppressWarnings("deprecation")
	public TriangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		initStyle(attrs);
		shapeTriangle();
		setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));//
	}

	private void shapeTriangle() {
		trianglePaint = new Paint();
		trianglePaint.setStyle(Style.FILL);
		trianglePaint.setColor(color);
		trianglePath = getEquilateralTriangle((int) UIAdjuster.computeDIPtoPixel(ctx, width), type);
	}

	private void initStyle(AttributeSet attrs) {
		int[] linerarLayoutAttrs = { android.R.attr.orientation };
		TypedArray array = ctx.obtainStyledAttributes(attrs, linerarLayoutAttrs);
		String widthString = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
		widthString = widthString.replace("dip", "").replace("dp", "").replace("px", "").trim();
		try {
			width = (int) Float.parseFloat(widthString);
		} catch (Exception e) {
			width = 40;
		}
		array.recycle();
		array = ctx.obtainStyledAttributes(attrs, R.styleable.TriangleViewStyle);
		type = Direction.getStatus(array.getInt(R.styleable.TriangleViewStyle_shapeArrow, Direction.UP.ordinal()));
		if (array.getColor(R.styleable.TriangleViewStyle_triangleColor, color) != 0) {
			color = array.getColor(R.styleable.TriangleViewStyle_triangleColor, color);
		}
		array.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(trianglePath, trianglePaint);
	}

	private Path getEquilateralTriangle(int width, Direction direction) {
		Point p2 = null;
		Point p3 = null;
		Path path = new Path();
		int height = width / 2;
		switch (direction) {
		case UP:
			p2 = new Point(0, height);
			p3 = new Point(width, height);
			path.moveTo(width / 2, 0);
			break;
		case RIGHT:
			p2 = new Point(height, height);
			p3 = new Point(0, width);
			path.moveTo(0, 0);
			break;
		case DOWN:
			p2 = new Point(width, 0);
			p3 = new Point(height, height);
			path.moveTo(0, 0);
			break;
		case LEFT:
			p2 = new Point(height, 0);
			p3 = new Point(p2.x, width);
			path.moveTo(0, height);
			break;
		}
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		return path;
	}

	public enum Direction {
		UP, DOWN, RIGHT, LEFT;
		public static Direction getStatus(int index) {
			return Direction.values()[index];
		}
	}
}