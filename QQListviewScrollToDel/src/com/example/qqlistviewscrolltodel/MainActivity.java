package com.example.qqlistviewscrolltodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.qqlistviewscrolltodel.QQListView.DelButtonClickListener;

public class MainActivity extends Activity {
	private QQListView ll_show;
	private ArrayAdapter<String> mAdapter;
	private List<String> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initAdapter();
		initView();
		
	}

	private void initAdapter() {
		mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mDatas);
	}

	private void initView() {
		ll_show = (QQListView) findViewById(R.id.ll_show);
		ll_show.setAdapter(mAdapter);
		ll_show.setDelButtonClickListener(new DelButtonClickListener() {
			
			@Override
			public void clickHappend(int position) {
				Toast.makeText(MainActivity.this, position + " : " + mAdapter.getItem(position), 1).show(); 
				mAdapter.remove(mAdapter.getItem(position));
			}
		});
		
		ll_show.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.makeText(MainActivity.this, position + " : " + mAdapter.getItem(position), 1).show(); 
			}
		});
		
	}

	private void initData() {
		mDatas = new ArrayList<String>(Arrays.asList("HelloWorld", "Welcome",
				"Java", "Android", "Servlet", "Struts", "Hibernate", "Spring",
				"HTML5", "Javascript", "Lucene"));
		
	}

}
