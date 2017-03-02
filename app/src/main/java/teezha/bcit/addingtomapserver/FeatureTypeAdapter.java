package teezha.bcit.addingtomapserver;

//==============================================================================
// File         : FeatureTypeAdapter3.java
//
// Current Author: Robert Hewlett
//
// Previous Author: None
//
// Contact Info: rob.hewy@gmail.com
//
// Purpose : Organize a list of FeatureTypeInfo in GUI land
//
// Dependencies: None
//
// Modification Log :
//    --> Created FEB-22-2015 (rh)
//    --> Updated MMM-DD-YYYY (fl)
//
// =============================================================================
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FeatureTypeAdapter extends ArrayAdapter<FeatureTypeInfo>
		implements SpinnerAdapter {
	// ==================================================================
	// Fields: Context is required and means the activity that
	// the list or spinner is par of. 
	// The Adapter helps the spinner display its data 
	// The data is List<FeatureTypeInfo> items
	// ==================================================================
	private Context context;
	private int resourse;
	private List<FeatureTypeInfo> items;

	// ==================================================================
	// The constructor: need a list, UI and the activity we are part of
	// ==================================================================
	public FeatureTypeAdapter(Context context, int resource,
			List<FeatureTypeInfo> objects) {
		super(context, resource, objects);

		this.items = objects;
		this.resourse = resource;
		this.context = context;

	}

	// ==================================================================
	// For every item in the list 
	// (the for is done by someone else e.g. spinner or listview)
	// Take a row of data and display it in a view
	// ==================================================================
	@Override
	public View getView(int i, View v, ViewGroup parent) {
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.resourse, null);
		}

		FeatureTypeInfo fti = items.get(i);
		if (fti != null) {
			TextView txvName = (TextView) v.findViewById(R.id.name);
			ImageView imvBitmap = (ImageView) v.findViewById(R.id.bitmap);

			txvName.setText(fti.getName());
			imvBitmap.setImageBitmap(fti.getBitmap());
		}
		return v;
	}


	// ==================================================================
	// If the UI is a spinner and not a list you can do something different 
	// or do exactly the same this as a ListView ... we do the same thing
	// ==================================================================
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}

}
