package Views;

import android.content.Context;
import android.util.AttributeSet;



public class AnyTextView extends android.support.v7.widget.AppCompatTextView {
	
	public AnyTextView( Context context ) {
		super( context );
	}
	
	public AnyTextView(Context context, AttributeSet attrs ) {
		super( context, attrs );
		
		if ( !isInEditMode() ) {
			Util.setTypeface( attrs, this );
		}
	}
	
	public AnyTextView(Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		
		if ( !isInEditMode() ) {
			Util.setTypeface( attrs, this );
		}
	}
}
