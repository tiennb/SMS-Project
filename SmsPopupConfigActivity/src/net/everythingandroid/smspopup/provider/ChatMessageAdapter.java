package net.everythingandroid.smspopup.provider;

import java.util.ArrayList;

import net.everythingandroid.smspopup.R;
import net.everythingandroid.smspopup.util.ChatMessage;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
public class ChatMessageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ChatMessage> mMessages;

	public ChatMessageAdapter(Context context, ArrayList<ChatMessage> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage message = mMessages.get(position);

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list_chat_sms_layout, parent, false);

			holder.tvMessageReceiver = (TextView) convertView
					.findViewById(R.id.tvMessageReceiver);
			holder.tvMessageSender = (TextView) convertView
					.findViewById(R.id.tvMessageSender);
			holder.tvTimeReceiver = (TextView) convertView
					.findViewById(R.id.tvTimeReceiver);
			holder.tvTimeSender = (TextView) convertView
					.findViewById(R.id.tvTimeSender);
			holder.rlChatReceiver = (RelativeLayout) convertView
					.findViewById(R.id.rlChatReceiver);
			holder.rlChatSender = (RelativeLayout) convertView
					.findViewById(R.id.rlChatSender);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		// Check whether message is mine to show green background and align
		// to right
		if (message.isReceiver()) {

			holder.rlChatSender.setVisibility(View.GONE);
			holder.rlChatReceiver.setVisibility(View.VISIBLE);

			holder.tvMessageReceiver.setText(message.getMessageChat());
			holder.tvTimeReceiver.setText(message.getTimeChat());

		}
		// If not mine then it is from sender to show orange background and
		// align to left
		else {

			holder.rlChatSender.setVisibility(View.VISIBLE);
			holder.rlChatReceiver.setVisibility(View.GONE);

			holder.tvMessageSender.setText(message.getMessageChat());
			holder.tvTimeSender.setText(message.getTimeChat());
		}

		return convertView;
	}

	private static class ViewHolder {
		TextView tvMessageReceiver, tvTimeReceiver;
		TextView tvMessageSender, tvTimeSender;
		RelativeLayout rlChatSender, rlChatReceiver;

		ImageView imgDoctorPhoto;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
