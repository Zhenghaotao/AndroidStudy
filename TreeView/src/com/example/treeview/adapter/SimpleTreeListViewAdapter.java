package com.example.treeview.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.treeview.R;
import com.example.treeview.utils.Node;
import com.example.treeview.utils.TreeHelper;
import com.example.treeview.utils.TreeListViewAdapter;

public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter<T>{

	public SimpleTreeListViewAdapter(ListView mTree, Context context,
			List<T> datas, int defaultExpandLevel)
			throws IllegalAccessException, IllegalArgumentException {
		super(mTree, context, datas, defaultExpandLevel);
	}

	@Override
	public View getConvertView(Node node, int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, parent,false);
			holder.iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
			holder.tv_item_text = (TextView) convertView.findViewById(R.id.tv_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(node.getIcon() == -1){
			holder.iv_item_icon.setVisibility(View.INVISIBLE);
		} else {
			holder.iv_item_icon.setVisibility(View.VISIBLE);
			holder.iv_item_icon.setImageResource(node.getIcon());
		}
		
		holder.tv_item_text.setText(node.getName());
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView iv_item_icon;
		TextView tv_item_text;
	}
	/**
	 * 动态插入节点
	 * @param position
	 * @param text
	 */
	public void addExtraNode(int position, String text) {
		Node node = mVisibleNodes.get(position);
		int indexOf = mAllNodes.indexOf(node);
		Node extraNode = new Node(-1,node.getId(),text);
		extraNode.setParent(node);
		node.getChildren().add(extraNode);
		
		mAllNodes.add(indexOf + 1, extraNode);
		mVisibleNodes = TreeHelper.filterVisibleNodes(mAllNodes);
		notifyDataSetChanged();
	}

}
