package io.weichao.flow_layout.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.weichao.flow_layout.R;
import io.weichao.flow_layout.view.FlowView;

public class MainActivity extends AppCompatActivity {
    public static final int PADDING = 10;
    public static final int PADDING_2 = FlowView.HORIZONTAL_SPACING + FlowView.VERTICAL_SPACING;

    private TextView mTvSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout rootView = new RelativeLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setBackgroundResource(R.drawable.bg);
        setContentView(rootView);

        View view = genFlowView();
        rootView.addView(view);
    }

    private View genFlowView() {
        List<String> list = initData();

        FlowView flowView = new FlowView(this);
        flowView.setPadding(PADDING, PADDING, PADDING, PADDING);
        for (int i = 0; i < list.size(); i++) {
            TextView tv = (TextView) View.inflate(this, R.layout.textview_white_border, null);
            tv.setText(list.get(i));
            tv.setOnClickListener(new TextViewOnClickListener());
            flowView.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return flowView;
    }

    private List<String> initData() {
        ArrayList<String> list = new ArrayList<>();

        list.add("餐厅");
        list.add("旅馆");
        list.add("电影院");
        list.add("KTV");
        list.add("酒吧");
        list.add("咖啡厅");
        list.add("网吧");
        list.add("商场");
        list.add("公交站");
        list.add("加油站");
        list.add("停车场");
        list.add("超市");
        list.add("药店");
        list.add("ATM");
        list.add("银行");
        list.add("医院");
        list.add("卫生间");

        return list;
    }

    private class TextViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            if (mTvSelected == tv) {
                return;
            }

            changeSelectedTextView(tv);
            mTvSelected = tv;
        }

        private void changeSelectedTextView(TextView tv) {
            tv.setBackgroundResource(R.drawable.selector_white_border_selected);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(PADDING_2, PADDING_2, PADDING_2, PADDING_2);
            if (mTvSelected == null) {
                return;
            }

            mTvSelected.setBackgroundResource(R.drawable.selector_white_border_normal);
            mTvSelected.setTextColor(Color.WHITE);
            mTvSelected.setPadding(PADDING_2, PADDING_2, PADDING_2, PADDING_2);
        }
    }
}
