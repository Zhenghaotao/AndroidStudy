package com.example.treeview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.treeview.adapter.SimpleTreeListViewAdapter;
import com.example.treeview.bean.FileBean;
import com.example.treeview.utils.Node;
import com.example.treeview.utils.TreeListViewAdapter.OnTreeNodeClickListener;

public class MainActivity extends Activity {

	private ListView lv_show;
	private SimpleTreeListViewAdapter<FileBean> mAdapter;
	private List<FileBean> datas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lv_show = (ListView) findViewById(R.id.lv_show);
		initData();
		try {
			mAdapter = new SimpleTreeListViewAdapter<FileBean>(lv_show, this,
					datas,  2);
			mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

				@Override
				public void onClick(Node node, int postition) {
					Toast.makeText(MainActivity.this, "点击了 " + node.getName(),
							0).show();
				}
			});
			lv_show.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, final int position, long id) {
					final EditText et = new EditText(MainActivity.this);
					new AlertDialog.Builder(MainActivity.this).setTitle("Add Node").setView(et).setPositiveButton("Sure", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mAdapter.addExtraNode(position,et.getText().toString());
						}
					}).setNegativeButton("Cancel", null).show();
					return true;
				}
			});
			lv_show.setAdapter(mAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		datas = new ArrayList<FileBean>();
		FileBean bean = new FileBean(1, 0, "根目录1");
		FileBean bean2 = new FileBean(2, 0, "根目录2");
		FileBean bean3 = new FileBean(3, 0, "根目录3");
		FileBean bean4 = new FileBean(4, 1, "根目录1-1");
		FileBean bean5 = new FileBean(5, 1, "根目录1-2");
		FileBean bean6 = new FileBean(6, 5, "根目录1-2-1");
		FileBean bean7 = new FileBean(7, 5, "根目录1-2-2");
		FileBean bean8 = new FileBean(8, 3, "根目录3-1");
		FileBean bean9 = new FileBean(9, 3, "根目录3-2");
		FileBean bean10 = new FileBean(10, 8, "根目录3-1-1");
		FileBean bean11 = new FileBean(11, 8, "根目录3-1-2");
		FileBean bean12 = new FileBean(12, 10, "根目录3-1-1-1");
		FileBean bean13 = new FileBean(13, 10, "根目录3-1-1-2");
		datas.add(bean);
		datas.add(bean2);
		datas.add(bean3);
		datas.add(bean4);
		datas.add(bean5);
		datas.add(bean6);
		datas.add(bean7);
		datas.add(bean8);
		datas.add(bean9);
		datas.add(bean10);
		datas.add(bean11);
		datas.add(bean12);
		datas.add(bean13);
	}

}
