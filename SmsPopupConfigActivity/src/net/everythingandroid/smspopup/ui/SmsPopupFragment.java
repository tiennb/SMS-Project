package net.everythingandroid.smspopup.ui;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.everythingandroid.smspopup.BuildConfig;
import net.everythingandroid.smspopup.R;
import net.everythingandroid.smspopup.preferences.ButtonListPreference;
import net.everythingandroid.smspopup.provider.GridEmotionAdapter;
import net.everythingandroid.smspopup.provider.IConstant;
import net.everythingandroid.smspopup.provider.SmsMmsMessage;
import net.everythingandroid.smspopup.service.SmsPopupUtilsService;
import net.everythingandroid.smspopup.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SmsPopupFragment extends Fragment {
	private SmsMmsMessage message;
	private boolean messageViewed = false;
	
	private InputMethodManager inputManager;
	private View inputView;
	
	private SmsPopupButtonsListener mButtonsListener;
	//
	private TextView quickreplyTextView;
	private EditText qrEditText;

	private TextView fromTv;
	private TextView timestampTv;
	private TextView messageTv;
	// private LinearLayout mainLayout;
	// private ScrollView contentMessage;
	// private LinearLayout contentMms;
	// private LinearLayout contentPrivacy;
	//private int contentNum = 0;

	// private int privacyMode = PRIVACY_MODE_OFF;
	// private boolean showUnlockButton = false;
	// private boolean showButtons = true;

	// private QuickContactBadge contactBadge;
	private ImageView imgContactPhoto, imgEmotion;
	// private ViewSwitcher buttonViewSwitcher;

	public static final int PRIVACY_MODE_OFF = 0;
	public static final int PRIVACY_MODE_HIDE_MESSAGE = 1;
	public static final int PRIVACY_MODE_HIDE_ALL = 2;

	private static final String EXTRA_PRIVACY_MODE = "net.everythingandroid.smspopup.privacy_mode";
	private static final String EXTRA_BUTTONS = "net.everythingandroid.smspopup.buttons";
	private static final String EXTRA_UNLOCK_BUTTON = "net.everythingandroid.smspopup.unlock_button";
	private static final String EXTRA_SHOW_BUTTONS = "net.everythingandroid.smspopup.show_buttons";

	private static final int VIEW_SMS = 0;
	private static final int VIEW_MMS = 1;
	private static final int VIEW_PRIVACY_SMS = 2;
	private static final int VIEW_PRIVACY_MMS = VIEW_MMS;

	public static final int BUTTON_VIEW = 100;
	public static final int BUTTON_VIEW_MMS = 101;
	public static final int BUTTON_UNLOCK = 102;
	public static final int BUTTON_PRIVACY = 103;

	private static final int CONTACT_IMAGE_FADE_DURATION = 300;

	private static final int BUTTON_SWITCHER_MAIN_BUTTONS = 0;
	private static final int BUTTON_SWITCHER_UNLOCK_BUTTON = 1;
	private static final String EMPTY_MMS_SUBJECT = "no subject";

	public static SmsPopupFragment newInstance(SmsMmsMessage newMessage,
			int[] buttons, int privacyMode, boolean showUnlockButton,
			boolean showButtons) {

		SmsPopupFragment newFragment = new SmsPopupFragment();
		Bundle args = newMessage.toBundle();
		args.putInt(EXTRA_PRIVACY_MODE, privacyMode);
		args.putIntArray(EXTRA_BUTTONS, buttons);
		args.putBoolean(EXTRA_UNLOCK_BUTTON, showUnlockButton);
		args.putBoolean(EXTRA_SHOW_BUTTONS, showButtons);
		newFragment.setArguments(args);
		return newFragment;
	}

	public static SmsPopupFragment newInstance(SmsMmsMessage newMessage,
			int[] buttons) {
		return newInstance(newMessage, buttons, PRIVACY_MODE_OFF, false, true);
	}

	public SmsPopupFragment() {
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final Bundle args = getArguments();
		message = new SmsMmsMessage(getActivity(), args);

		View v = inflater.inflate(R.layout.custom_sms_dialog, container, false);

		quickreplyTextView = (TextView) v.findViewById(R.id.QuickReplyTextView);
		qrEditText = (EditText) v.findViewById(R.id.QuickReplyEditText);

		// Find the main textviews and layouts

		imgEmotion = (ImageView) v.findViewById(R.id.imgEmotion);
		fromTv = (TextView) v.findViewById(R.id.fromTextView);
		messageTv = (TextView) v.findViewById(R.id.messageTextView);
		// timestampTv = (TextView) v.findViewById(R.id.timestampTextView);
		// contentMessage = (ScrollView) v.findViewById(R.id.contentMessage);
		// contentMms = (LinearLayout) v.findViewById(R.id.contentMms);

		// Find the QuickContactBadge view that will show the contact photo
		imgContactPhoto = (ImageView) v.findViewById(R.id.imgAvatar);
		//
		// final String[] buttonText = getResources().getStringArray(
		// R.array.buttons_text);

		// android.util.Log.i("buttonText: ", buttons[0]+ " " +
		// buttonText[buttons[0]]);

		imgEmotion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickEmotion(imgEmotion);
				
			}
		});
		
		// if (showButtons) {
		final ImageView button1 = (ImageView) v.findViewById(R.id.imgCancel);
		// final PopupButton button1Vals = new PopupButton(buttons[0],
		// buttonText);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mButtonsListener != null) {
					mButtonsListener.onButtonClicked(1);
				} else {

				}

			}
		});

		final ImageView imgEnter = (ImageView) v.findViewById(R.id.imgEnter);
		qrEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (qrEditText.getText().toString().length() > 0) {
					imgEnter.setImageResource(R.drawable.ic_enter);
					imgEnter.setEnabled(true);
				} else {
					imgEnter.setImageResource(R.drawable.ic_enter_disable);
					imgEnter.setEnabled(false);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		imgEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendQuickReply(qrEditText.getText().toString());
				mButtonsListener.onButtonClicked(1);
			}
		});

		// if (message.isMms()) {
		// // The ViewMMS button
		// ((Button) v.findViewById(R.id.viewMmsButton))
		// .setOnClickListener(new PopupButton(BUTTON_VIEW_MMS,
		// buttonText));
		// final String mmsSubject = message.getMessageBody();
		// final TextView mmsSubjectTV = ((TextView) v
		// .findViewById(R.id.mmsSubjectTextView));
		// if (mmsSubject != null && !"".equals(mmsSubject)
		// && !EMPTY_MMS_SUBJECT.equals(mmsSubject)) {
		// mmsSubjectTV.setText(message.getMessageBody());
		// } else {
		// mmsSubjectTV.setVisibility(View.GONE);
		// }
		// }

		populateViews();

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mButtonsListener = (SmsPopupButtonsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement SmsPopupButtonsListener");
		}
	}

	/*
	 * Populate all the main SMS/MMS views with content from the actual
	 * SmsMmsMessage
	 */
	private void populateViews() {
		if (message.isSms()) {
			messageTv.setText(message.getMessageBody());

			quickreplyTextView.setText(getString(R.string.quickreply_from_text,
					message.getContactName()));
		}

		// Set the from, message and header views
		fromTv.setText(message.getAddress());

		Bitmap bm = setPhoto(message.getAddress());

		if (bm != null) {
			imgContactPhoto.setImageBitmap(bm);
		}

		// loadContactPhoto();
		// timestampTv.setText(message.getFormattedTimestamp());

		// setPrivacy(privacyMode, true);
		// refreshButtonViews();
	}

	public Bitmap setPhoto(String phone) {

		Bitmap photo = null;

		try {
			Uri contact_Uri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_URI,
					fetchContactIdByPhone(phone));
			InputStream photo_stream = ContactsContract.Contacts
					.openContactPhotoInputStream(getActivity()
							.getContentResolver(), contact_Uri);
			BufferedInputStream buf = new BufferedInputStream(photo_stream);
			photo = BitmapFactory.decodeStream(buf);
		} catch (Exception e) {
			return null;
		}

		return photo;

	}

	/**
	 * Sends the actual quick reply message
	 */
	private void sendQuickReply(String quickReplyMessage) {
		// hideSoftKeyboard();
		if (quickReplyMessage != null) {
			if (quickReplyMessage.length() > 0 && message != null) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						SmsPopupUtilsService.class);
				i.setAction(SmsPopupUtilsService.ACTION_QUICKREPLY);
				i.putExtras(message.toBundle());
				i.putExtra(SmsMmsMessage.EXTRAS_QUICKREPLY, quickReplyMessage);
				if (BuildConfig.DEBUG)
					Log.v("Sending message to " + message.getContactName());
				WakefulBroadcastReceiver.startWakefulService(getActivity()
						.getApplicationContext(), i);
				Toast.makeText(getActivity(),
						R.string.quickreply_sending_toast, Toast.LENGTH_LONG)
						.show();
				// dismissDialog(DIALOG_QUICKREPLY);
				// removeActiveMessage();
			} else {
				Toast.makeText(getActivity(),
						R.string.quickreply_nomessage_toast, Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	
	public void onClickEmotion(View v) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View popupView = layoutInflater.inflate(R.layout.popup_emotion, null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		final GridView gridEmotion = (GridView) popupView
				.findViewById(R.id.gridEmo);

		ArrayList<String> emoList = new ArrayList<String>();

		for (int i = 0; i < IConstant.emotions.length; i++) {
			emoList.add(IConstant.emotions[i]);
		}

		gridEmotion.setAdapter(new GridEmotionAdapter(getActivity().getApplicationContext(), emoList));

		// Closes the popup window when touch outside of it - when looses focus
		popupWindow.setOutsideTouchable(true);
		// /popupWindow.setFocusable(true);
		// Removes default black background
		popupWindow
				.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
		popupWindow.setFocusable(true);
		gridEmotion.setClickable(true);
		gridEmotion.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				qrEditText.setText(qrEditText.getText().toString()
						+ IConstant.emotions[position]);
				qrEditText.setSelection(qrEditText.getText().length());

				popupWindow.dismiss();
				getActivity().getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			}
		});

		popupWindow.showAsDropDown(v, 0, -600);
		
	}

	// public void resizeLayout(int newWidth, int screenWidth) {
	// LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
	// params.width = newWidth;
	// mainLayout.setLayoutParams(params);
	// }
	//
	// public void setShowUnlockButton(boolean show) {
	// if (show != showUnlockButton) {
	// showUnlockButton = show;
	// refreshButtonViews();
	// }
	// }

	// private void refreshButtonViews() {
	// if (!showButtons) {
	// buttonViewSwitcher.setVisibility(View.GONE);
	// } else {
	// final int currentView = buttonViewSwitcher.getDisplayedChild();
	// if (showUnlockButton) {
	// if (currentView != BUTTON_SWITCHER_UNLOCK_BUTTON) {
	// // Show unlock button
	// buttonViewSwitcher
	// .setDisplayedChild(BUTTON_SWITCHER_UNLOCK_BUTTON);
	// }
	// } else {
	// if (currentView != BUTTON_SWITCHER_MAIN_BUTTONS) {
	// // Show main popup buttons
	// buttonViewSwitcher
	// .setDisplayedChild(BUTTON_SWITCHER_MAIN_BUTTONS);
	// }
	// }
	// }
	// }
	//
	// public void setPrivacy(int newMode) {
	// setPrivacy(newMode, false);
	// }
	//
	// private void setPrivacy(int newMode, boolean initial) {
	//
	// if ((newMode != privacyMode || initial) && message != null) {
	// final int viewPrivacy = message.isSms() ? VIEW_PRIVACY_SMS
	// : VIEW_PRIVACY_MMS;
	//
	// final int viewPrivacyOff = message.isSms() ? VIEW_SMS : VIEW_MMS;
	//
	// if (newMode == PRIVACY_MODE_OFF) {
	// updateContentView(viewPrivacyOff);
	// fromTv.setVisibility(View.VISIBLE);
	// messageViewed = true;
	// if (initial || privacyMode == PRIVACY_MODE_HIDE_ALL) {
	// loadContactPhoto();
	// }
	// } else if (newMode == PRIVACY_MODE_HIDE_MESSAGE) {
	// updateContentView(viewPrivacy);
	// fromTv.setVisibility(View.VISIBLE);
	// loadContactPhoto();
	// } else if (newMode == PRIVACY_MODE_HIDE_ALL) {
	// updateContentView(viewPrivacy);
	// fromTv.setVisibility(View.GONE);
	// }
	// }
	// privacyMode = newMode;
	// }

	// private void updateContentView(int mode) {
	// if (contentMessage != null && contentMms != null
	// && contentPrivacy != null) {
	// if (contentNum != mode) {
	// contentNum = mode;
	// switch (mode) {
	// case VIEW_SMS:
	// contentMessage.setVisibility(View.VISIBLE);
	// contentMms.setVisibility(View.GONE);
	// contentPrivacy.setVisibility(View.GONE);
	// break;
	// case VIEW_MMS:
	// contentMessage.setVisibility(View.GONE);
	// contentMms.setVisibility(View.VISIBLE);
	// contentPrivacy.setVisibility(View.GONE);
	// break;
	// case VIEW_PRIVACY_SMS:
	// contentMessage.setVisibility(View.GONE);
	// contentMms.setVisibility(View.GONE);
	// contentPrivacy.setVisibility(View.VISIBLE);
	// break;
	// }
	// }
	// }
	// }

	// boolean cacheHit = false;
	// if (mButtonsListener != null && message.getContactLookupUri() != null) {
	// final LruCache<Uri, Bitmap> cache = mButtonsListener.getCache();
	// if (cache != null) {
	// final Bitmap bitmap = cache.get(message.getContactLookupUri());
	// if (bitmap != null) {
	// if (BuildConfig.DEBUG)
	// Log.v("loadContactPhoto() - bitmap cache hit");
	// contactBadge.setImageBitmap(bitmap);
	// cacheHit = true;
	// }
	// }
	// }
	//
	// if (!cacheHit) {
	// new FetchContactPhotoTask(contactBadge).execute(message
	// .getContactLookupUri());
	// }
	//
	// contactBadge.setClickable(true);
	// final Uri contactUri = message.getContactLookupUri();
	// if (contactUri != null) {
	// contactBadge.assignContactUri(message.getContactLookupUri());
	// } else {
	// contactBadge.assignContactFromPhone(message.getAddress(), false);
	// }

	public String fetchContactIdByPhone(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = getActivity().getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);

		String contactId = "";

		try {
			if (cursor.moveToFirst()) {
				contactId = cursor.getString(cursor
						.getColumnIndex(PhoneLookup._ID));
				if (contactId != null) {
					return contactId;
				}
			}
		} finally {
			cursor.close();
		}

		return null;

	}

	private class FetchContactPhotoTask extends AsyncTask<Uri, Integer, Bitmap> {
		private final WeakReference<QuickContactBadge> viewReference;

		public FetchContactPhotoTask(QuickContactBadge badge) {
			viewReference = new WeakReference<QuickContactBadge>(badge);
		}

		@Override
		protected Bitmap doInBackground(Uri... params) {
			if (isAdded()) {
				// if (BuildConfig.DEBUG)
				// Log.v("Loading contact photo in background...");
				// final Bitmap bitmap = SmsPopupUtils.getPersonPhoto(
				// getActivity(), params[0]);
				// if (mButtonsListener != null && bitmap != null) {
				// final LruCache<Uri, Bitmap> cache = mButtonsListener
				// .getCache();
				// if (cache != null) {
				// cache.put(params[0], bitmap);
				// }
				// }

				// return bitmap;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap photo) {
			if (BuildConfig.DEBUG)
				Log.v("Done loading contact photo");
			if (photo != null && viewReference != null) {
				final QuickContactBadge badge = viewReference.get();
				if (badge != null && isAdded()) {
					TransitionDrawable mTd = new TransitionDrawable(
							new Drawable[] {
									getResources().getDrawable(
											R.drawable.ic_contact_picture),
									new BitmapDrawable(getResources(), photo) });
					badge.setImageDrawable(mTd);
					mTd.setCrossFadeEnabled(false);
					mTd.startTransition(CONTACT_IMAGE_FADE_DURATION);
				}
			}
		}
	}

	// private class PopupButton implements OnClickListener {
	// final private int buttonId;
	// public boolean isReplyButton;
	// public String buttonText;
	// public int buttonVisibility = View.VISIBLE;
	//
	// public PopupButton(int id, String[] buttonTextArray) {
	// buttonId = id;
	// isReplyButton = false;
	// if (buttonId == ButtonListPreference.BUTTON_REPLY
	// || buttonId == ButtonListPreference.BUTTON_QUICKREPLY
	// || buttonId == ButtonListPreference.BUTTON_REPLY_BY_ADDRESS) {
	// isReplyButton = true;
	// }
	//
	// if (buttonId < buttonTextArray.length) {
	// buttonText = buttonTextArray[buttonId];
	// }
	//
	// if (buttonId == ButtonListPreference.BUTTON_DISABLED) { // Disabled
	// buttonVisibility = View.GONE;
	// }
	// }
	//
	// @Override
	// public void onClick(View v) {
	// mButtonsListener.onButtonClicked(buttonId);
	// }
	// }
	
	/**
	 * Show the soft keyboard and store the view that triggered it
	 */
	private void showSoftKeyboard(View triggerView) {
		if (BuildConfig.DEBUG)
			Log.v("showSoftKeyboard()");
		if (inputManager == null) {
			inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputView = triggerView;
		inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	/**
	 * Hide the soft keyboard
	 */
	private void hideSoftKeyboard() {
		if (inputView == null)
			return;
		if (BuildConfig.DEBUG)
			Log.v("hideSoftKeyboard()");
		if (inputManager == null) {
			inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		inputManager.hideSoftInputFromWindow(
				inputView.getApplicationWindowToken(), 0);
		inputView = null;
	}

	public static interface SmsPopupButtonsListener {
		abstract void onButtonClicked(int buttonType);

		// abstract LruCache<Uri, Bitmap> getCache();
	}

}
