package pathstudy.taotao.com.circleindicator_master;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pathstudy.taotao.com.circleindicator_master.widget.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    private List<View> viewList;
    private ViewPager viewPager;
    private CircleIndicator circleINdicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        circleINdicator = (CircleIndicator) findViewById(R.id.indicator);
        circleINdicator.setViewPager(viewPager);
    }

    private void initData(){
        viewList = new ArrayList<View>();
        Random random = new Random();
        for(int i=0;i<5;i++){
            View view = new View(this);
            view.setBackgroundColor(0xff000000| random.nextInt(0x00ffffff));
            viewList.add(view);
        }
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    };
}
