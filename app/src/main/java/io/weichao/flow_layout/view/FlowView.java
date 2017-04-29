package io.weichao.flow_layout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowView extends ViewGroup {
    public static final int HORIZONTAL_SPACING = 10;
    public static final int VERTICAL_SPACING = 10;

    private class Line {
        private int mWidth;// 行占用的宽度
        private int mHeight;// 当前行的高度
        private List<View> mChildList = new ArrayList<>();// 记录当前行管理的view对象

        public void addChild(View child) {
            mChildList.add(child);
            // 让当前行高度等于最高的一个孩子的高度
            mHeight = mHeight < child.getMeasuredHeight() ? child.getMeasuredHeight() : mHeight;
            mWidth += child.getMeasuredWidth();
        }

        // 分配行里面每个TextView的位置
        public void layout(int left, int top) {
            mWidth += (mChildList.size() - 1) * HORIZONTAL_SPACING;
            int childrenWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - mWidth;
            if (mChildList == null || mChildList.size() == 0) {
                return;
            }

            int childWidth = childrenWidth / mChildList.size();
            for (int i = 0; i < mChildList.size(); i++) {
                View child = mChildList.get(i);
                // 重新测量孩子
                if (childWidth > 0) {
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() + childWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY);
                    child.measure(widthMeasureSpec, heightMeasureSpec);
                }
                // 分配孩子的位置
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                // 让left加上 上一个孩子的宽度
                left += child.getMeasuredWidth();
                left += HORIZONTAL_SPACING;
            }
        }

        public int getHeight() {
            return mHeight;
        }
    }

    private List<Line> mLineList = new ArrayList<>();// 管理当前控件所有行的集合
    private int mUsedWidth;// 当前行使用的空间
    private Line mLine;
    private int mTotalHeight;

    public FlowView(Context context) {
        this(context, null);
    }

    public FlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 测量方法 有可能调用多次
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLineList.clear();
        mLine = null;
        mUsedWidth = 0; // 把之前缓存数据全部清空
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);// 获取到当前FlowLayout的宽的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();// 把padding减去
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);// 获取到当前FlowLayout的高的模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        // 测量每个孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 如果父容器是精确的值 子View就是AT_Most 其余情况和父容器一样
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);
            // 要计算子view规则
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            if (mLine == null) {
                mLine = new Line();
            }
            int childWidth = child.getMeasuredWidth();// 获取孩子的宽
            mUsedWidth += HORIZONTAL_SPACING;
            mUsedWidth += childWidth;
            if (mUsedWidth <= widthSize) {
                mLine.addChild(child);
            } else {
                newLine();
                mUsedWidth += childWidth;
                mLine.addChild(child);// 把孩子添加到新的一行上
            }
        }
        if (!mLineList.contains(mLine)) {
            mLineList.add(mLine);// 把最后一行添加到集合中
        }
        int totalHeight = 0;
        for (int i = 0; i < mLineList.size(); i++) {
            totalHeight += mLineList.get(i).getHeight();
        }
        totalHeight += (mLineList.size() - 1) * VERTICAL_SPACING;
        totalHeight += getPaddingTop();
        totalHeight += getPaddingBottom();
        setTotalHeight(totalHeight);
        setMeasuredDimension(widthSize + getPaddingLeft() + getPaddingRight(), resolveSize(totalHeight, heightMeasureSpec));
    }

    // 分配每个孩子的位置
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        top += getPaddingTop();
        for (int i = 0; i < mLineList.size(); i++) {
            Line line = mLineList.get(i);
            line.layout(left + getPaddingLeft(), top); // 分配行的位置 然后再交给每个行去分配位置
            top += line.getHeight();
            top += VERTICAL_SPACING;
        }
    }

    private void newLine() {
        mLineList.add(mLine);// 把之前的行添加到集合中
        mLine = new Line();
        mUsedWidth = 0;
    }

    public int getTotalHeight() {
        return mTotalHeight;
    }

    public void setTotalHeight(int totalHeight) {
        mTotalHeight = totalHeight;
    }
}
