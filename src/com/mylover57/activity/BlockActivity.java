/**
 * 描述：显示黑名单列表
 */
package com.mylover57.activity;

import com.mylover57.R;
import com.mylover57.app.MyApplication;
import com.mylover57.db.IBlock;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class BlockActivity extends ListActivity {

	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.block_layout);

		MyApplication myApp = (MyApplication) getApplication();
		cursor = myApp.blockQueryAll();
		SimpleCursorAdapter blockAdapter = new SimpleCursorAdapter(this,
				R.layout.block_item, cursor, new String[] { IBlock.Columns.id,
						IBlock.Columns.num, IBlock.Columns.kind }, new int[] {
						R.id.block_item_id, R.id.block_item_num,
						R.id.block_item_kind });
		setListAdapter(blockAdapter);
	}
	//销毁之前回收资源使用过的资源
	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}
}
