package net.everythingandroid.smspopup.provider;

import java.util.ArrayList;

import net.everythingandroid.smspopup.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
public class GridEmotionAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> emotion;

	public GridEmotionAdapter(Context context, ArrayList<String> emo) {
		super();
		this.mContext = context;
		this.emotion = emo;
	}

	@Override
	public int getCount() {
		return emotion.size();
	}

	@Override
	public Object getItem(int position) {
		return emotion.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_row, parent, false);
			holder.tvEmo = (TextView) convertView.findViewById(R.id.tvEmotion);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.tvEmo.setText(emotion.get(position));

		return convertView;
	}

	private static class ViewHolder {
		TextView tvEmo;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
