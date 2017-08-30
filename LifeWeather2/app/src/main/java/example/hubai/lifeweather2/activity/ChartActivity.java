package example.hubai.lifeweather2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import example.hubai.lifeweather2.R;
import example.hubai.lifeweather2.view.ChartView;

/**
 * Created by hubai on 2017/3/9.
 */

public class ChartActivity extends AppCompatActivity {
    private ChartView mChartView1;
    private ChartView mChartView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        init();
    }

    private void init() {
        mChartView1 = (ChartView) findViewById(R.id.chart_view_1);
        mChartView2 = (ChartView) findViewById(R.id.chart_view_2);
        setData();
    }

    private void setData() {
        String title = "7次测量甲醛浓度折线图";
        String title2="7次测量温度折线图";
        String[] xLabel1 = {"12-11", "12-12", "12-13", "12-14", "12-15", "12-16", "12-17"};
        String[] xLabel2 = {"2-13", "2-14", "2-15", "2-16", "2-17", "2-18", "2-19"};
        String[] data1 = {"2.92", "2.99", "3.20", "2.98", "2.92", "2.94", "2.90"};
        String[] data2 = {"15.0", "16.0", "15.5", "14.0", "17.0", "17.0", "15.0"};
        mChartView1.setTitle(title);
        mChartView1.setxLabel(xLabel1);
        mChartView1.setData(data1);
        mChartView1.fresh();
        mChartView2.setTitle(title2);
        mChartView2.setxLabel(xLabel2);
        mChartView2.setData(data2);
        mChartView2.fresh();
    }
}
