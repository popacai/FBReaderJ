/*
 * Copyright (C) 2010-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.network;

import java.util.*;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.fbreader.network.*;
import org.geometerplus.fbreader.network.tree.NetworkCatalogRootTree;
import org.geometerplus.android.fbreader.covers.CoverManager;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

public class AllCatalogsActivity extends ListActivity {
	final NetworkLibrary library = NetworkLibrary.Instance();
	CheckListAdapter myAdapter;
	ArrayList<String> ids = new ArrayList<String>();
	ArrayList<String> inactiveIds = new ArrayList<String>();
	
	public final static String IDS_LIST = "org.geometerplus.android.fbreader.network.IDS_LIST";
	public final static String INACTIVE_IDS_LIST = "org.geometerplus.android.fbreader.network.INACTIVE_IDS_LIST";
	
	private boolean isChanged = false;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.network_library_filter);
		
		Intent intent = getIntent();
		ids = intent.getStringArrayListExtra(IDS_LIST);
		inactiveIds = intent.getStringArrayListExtra(INACTIVE_IDS_LIST);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	
		ArrayList<CheckItem> idItems = new ArrayList<CheckItem>();
			idItems.add(new CheckSection(getLabelByKey("active")));

		        //final List<CheckItem> items = new ArrayList<CheckItem>();
			for(String i : ids){
				idItems.add(new CheckItem(i, true, library.getCatalogTreeByUrlAll(i)));
			}
			//for (CheckItem i : items) {
			//	idItems.add(i);
			//}
		
			idItems.add(new CheckSection(getLabelByKey("inactive")));
			for(String i : inactiveIds){
				idItems.add(new CheckItem(i, false, library.getCatalogTreeByUrlAll(i)));
			}
		
		
                myAdapter = new CheckListAdapter(this, R.layout.checkbox_item, idItems, this);
                DragSortListView list = getListView();
                list.setAdapter(myAdapter);
                list.setDropListener(onDrop);
                list.setRemoveListener(onRemove);
	}

        private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    if(to <= 0){
                        to = 1;
                    }
                    DragSortListView list = getListView();
                    CheckItem item = myAdapter.getItem(from);
                    myAdapter.remove(item);
                    myAdapter.insert(item, to);
                    myAdapter.reCheckAll(item, to);
                    list.moveCheckState(from, to);
                }
            }
        };

    private RemoveListener onRemove =
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                DragSortListView list = getListView();
                CheckItem item = myAdapter.getItem(which);
                myAdapter.remove(item);
                list.removeCheckState(which);
            }
        };


        @Override
        public DragSortListView getListView() {
               return (DragSortListView) super.getListView();
        }
	
	private String getLabelByKey(String keyName) {
		return NetworkLibrary.resource().getResource("allCatalogs").getResource(keyName).getValue();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(isChanged){
			ArrayList<String> ids = new ArrayList<String>();
                        for (int i = 0; i < myAdapter.getCount(); i++){
                                CheckItem item = myAdapter.getItem(i);
				if(!item.isSection() && item.isChecked()){
					ids.add(item.getId());
				}
			}
			library.setActiveIds(ids);
			library.synchronize();
		}
	}
	
	private class CheckItem implements Comparable<CheckItem>{
		private String myId;
		private boolean isChecked;
		NetworkTree myTree = null;
		
		public CheckItem(String id, boolean checked, NetworkTree tree){
			myId = id;
			isChecked = checked;
			if(tree instanceof NetworkCatalogRootTree){
				myTree = tree;
			}else{
				System.out.println("Tree parameter should be an instance of NetworkCatalogRootTree");
			}
		}
		
		public CheckItem(String id, boolean checked){
			myId = id;
			isChecked = checked;
		}
		
		public String getId(){
			return myId;
		}
		
		public NetworkTree getTree(){
			return myTree;
		}
		
		public String getTitle(){
                        if(myTree != null){
			    return myTree.getLink().getTitle();
                        }else{
                            return "";
                        }
		}
		
		public String getTitleLower(){
			return getTitle().toLowerCase(Locale.getDefault());
		}
		
		public boolean isChecked(){
			return isChecked;
		}
		
		public void setChecked(boolean value){
			isChecked = value;
		}
		
		public boolean isSection(){
			return false;
		}

		@Override
		public int compareTo(CheckItem another) {
			return getTitleLower().compareTo(another.getTitleLower());
		}
	}
	
	private class CheckSection extends CheckItem{
		public CheckSection(String title){
			super(title, false);
		}
		public boolean isSection(){
			return true;
		}
	}
	
	private class CheckListAdapter extends ArrayAdapter<CheckItem> {
		ListActivity myActivity;
		private CoverManager myCoverManager;
		private ArrayList<CheckItem> items = new ArrayList<CheckItem>();
		
		public CheckListAdapter(Context context, int textViewResourceId, List<CheckItem> objects, ListActivity activity) {
			super(context, textViewResourceId, objects);
			myActivity = activity;
			items.addAll(objects);
		}
		
		public ArrayList<CheckItem> getItems(){
			return items;
		}
                
                public void reCheckAll(CheckItem item, int index){
                       boolean flag = false;
                       for (int i=0; i < getCount(); i++){
                           CheckItem it = getItem(i);
                           if(it.isSection()){
                               if(i>0){
                                  if(index > i){
                                     flag = false;
                                  }else{
                                     flag = true;
                                  }
                                  break;
                               }
                           }
                       }
                        
                       if(item != null){
                           if(item.isChecked() != flag){
                               item.setChecked(flag);
                           }
                           isChanged = true;
                       } 
                }

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			
			View v = convertView;
			CheckItem item = this.getItem(position); 
			
		    if (item != null) {
		    	if(item.isSection()){
		    	        LayoutInflater vi;
		    		vi = LayoutInflater.from(getContext());
		    		v = vi.inflate(R.layout.checkbox_section, null);
		    		TextView tt = (TextView) v.findViewById(R.id.title);
		    		if (tt != null) {
		    			tt.setText(item.getId());
		    		}
		    	}else{
				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				    v = vi.inflate(R.layout.checkbox_item, null);

                                    if (myCoverManager == null) {
                                                v.measure(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                final int coverHeight = v.getMeasuredHeight();
                                                myCoverManager = new CoverManager(myActivity, coverHeight * 15 / 12, coverHeight);
                                                v.requestLayout();
                                        }
				    

				    NetworkTree t = item.getTree();
		    	
		    		if(t != null){
		    			INetworkLink link = t.getLink();
		    			TextView tt = (TextView)v.findViewById(R.id.title);
		    			if (tt != null) {
		    				tt.setText(link.getTitle());
		    			}
		    			tt = (TextView)v.findViewById(R.id.subtitle);
		    			if (tt != null) {
		    				tt.setText(link.getSummary());
		    			}
		    			
		    			ImageView coverView = (ImageView)v.findViewById(R.id.drag_handle);
		    			if (!myCoverManager.trySetCoverImage(coverView, t)) {
		    				coverView.setImageResource(R.drawable.ic_list_library_books);
		    			}
		    			
		    			CheckBox ch = (CheckBox)v.findViewById(R.id.check_item);
		    			if (ch != null) {
		    				ch.setText("");
		    				ch.setChecked(item.isChecked());
		    				ch.setTag(item);
		    				ch.setOnClickListener( new View.OnClickListener() {  
		    					public void onClick(View v) {  
		    						CheckBox cb = (CheckBox)v;  
		    						CheckItem checkedItem = (CheckItem) cb.getTag();
		    						if(checkedItem != null){
		    							checkedItem.setChecked(cb.isChecked());
		    						}
		    						isChanged = true;
		    					}
		    				});  
		    			}
		    		}
		    	}
		    }
			return v;
		}
	}
}
