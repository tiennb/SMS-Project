package net.everythingandroid.smspopup.controls;

import net.everythingandroid.smspopup.R;
import android.content.Context;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QmTextWatcher implements TextWatcher {
	private TextView mTextView;
	private ImageView mSendButton;
	private static final int CHARS_REMAINING_BEFORE_COUNTER_SHOWN = 160;
	private static int MAX_CHARS = 160;

	public QmTextWatcher(Context context, TextView updateTextView,
			ImageView sendButton) {
		mTextView = updateTextView;
		mSendButton = sendButton;
	}

	public QmTextWatcher(Context context, TextView updateTextView) {
		mTextView = updateTextView;
		mSendButton = null;
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		getQuickReplyCounterText(s, mTextView, mSendButton);
	}

	public static void getQuickReplyCounterText(CharSequence s,
			TextView mTextView, ImageView mSendButton) {
		if (mSendButton != null) {
			if (s.length() > 0) {
				mSendButton.setImageResource(R.drawable.ic_enter);
				mSendButton.setEnabled(true);
			} else {
				mSendButton.setImageResource(R.drawable.ic_enter_disable);
				mSendButton.setEnabled(false);
			}
		}

		// if (s.length() < (80 - CHARS_REMAINING_BEFORE_COUNTER_SHOWN)) {
		// mTextView.setVisibility(View.GONE);
		// return;
		// }

		/*
		 * SmsMessage.calculateLength returns an int[4] with: int[0] being the
		 * number of SMS's required, int[1] the number of code units used,
		 * int[2] is the number of code units remaining until the next message.
		 * int[3] is the encoding type that should be used for the message.
		 */
		int[] params = SmsMessage.calculateLength(s, false);

		int charsCount = params[1];
		int msgCount = params[0];
		int remainingInCurrentMessage = params[2];

		Log.i("Count: ", params[0] + " " + params[1] + " " + params[2] + " "
				+ params[3] + " ");

		if (params[3] == 1) {
			MAX_CHARS = 160;
		} else if (params[3] == 3) {
			MAX_CHARS = 70;
		}

		if (msgCount <= 1) {
			mTextView.setText(charsCount + " / " + MAX_CHARS);
		} else {
			mTextView.setText("(" + msgCount + ") " + charsCount + " / "
					+ MAX_CHARS * 2);
		}

	}
}
