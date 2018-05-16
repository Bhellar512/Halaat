package Views;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;



import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used in combination with {@link ViewBinder} to provide the basic
 * Android List view adapter. It can internally maintain an {@link ArrayList}
 * and provide certain operations that can be performed on {@link List}.
 * 
 * @param <T>
 *            The entity that will be passed into this adapter
 */
public class ArrayListAdapter<T> extends BaseAdapter implements SpinnerAdapter {
	
	protected Activity mContext;
	protected List<T> arrayList;
	protected ViewBinder<T> viewBinder;
	
	public ArrayListAdapter(Activity context, List<T> arrayList,
                            ViewBinder<T> viewBinder ) {
		mContext = context;
		this.arrayList = arrayList;
		this.viewBinder = viewBinder;
	}
	
	public ArrayListAdapter(Activity context, ViewBinder<T> viewBinder ) {
		this( context, new ArrayList<T>(), viewBinder );
	}
	
	public void setArrayList( List<T> arrayList ) {
		this.arrayList = arrayList;
	}
	
	/**
	 * Clears the internal list
	 */
	public void clearList() {
		arrayList.clear();
		notifyDataSetChanged();
	}
	
	/**
	 * Adds a entity to the list and calls {@link #notifyDataSetChanged()}.
	 * Should not be used if lots of NotificationDummy are added.
	 * 
	 * @see #addAll(List)
	 */
	public void add( T entity ) {
		arrayList.add( entity );
		notifyDataSetChanged();
	}
	
	/**
	 * Adds a NotificationDummy to the list and calls
	 * {@link #notifyDataSetChanged()}. Can be used {
	 * {@link List#subList(int, int)}.
	 * 
	 * @see #addAll(List)
	 */
	public void addAll( List<T> entityList ) {
		arrayList.addAll( entityList );
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return arrayList.size();
	}
	
	@Override
	public Object getItem(int position ) {
		return arrayList.get( position );
	}
	
	@Override
	public long getItemId( int position ) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup arg2 ) {
		
		View convertView = view;
		
		if ( convertView == null ) {
			convertView = viewBinder.createView( mContext );
		}
		
		viewBinder.bindView( arrayList.get( position ), position, 0,
				convertView, mContext );
		
		return convertView;
	}
	
	public T getItemFromList( int index ) {
		return arrayList.get( index );
	}
	
	public List<T> getList() {
		return arrayList;
	}

}
