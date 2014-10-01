package com.codepath.apps.basictwitter;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FilterDialog.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FilterDialog#newInstance} factory method to create an instance of this
 * fragment.
 * 
 */
public class ComposeDialog extends DialogFragment implements OnClickListener {
	private static final String ARG_PARAM1 = "param1";
	
	private EditText etText;
	private Button tweetButton;
	private TextView tvCount;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @return A new instance of fragment FilterDialog.
	 */
	public static ComposeDialog newInstance() {
		ComposeDialog fragment = new ComposeDialog();
		return fragment;
	}

	public ComposeDialog() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams wLayoutParams = dialog.getWindow().getAttributes();
		wLayoutParams.gravity = Gravity.BOTTOM;
		wLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wLayoutParams.width = LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(wLayoutParams);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_compose_dialog, container,
				false);
		
		etText = (EditText)view.findViewById(R.id.etText);	
		tweetButton = (Button)view.findViewById(R.id.btnTweet);
		tvCount = (TextView)view.findViewById(R.id.tvCharCount);
		tweetButton.setOnClickListener(this);
		
		etText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {	
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String text = (s != null) ? s.toString() : null;
				if(text != null && text.length() >0) {
					tvCount.setText(Integer.toString(140-text.length()));
					if(text.length() <= 140) {					  
					  tweetButton.setEnabled(true);
					  tvCount.setTextColor(getResources().getColor(android.R.color.primary_text_light));
					}else{
					  tweetButton.setEnabled(false);
					  tvCount.setTextColor(getResources().getColor(android.R.color.holo_red_light));
					}
				}else{
					tvCount.setText(Integer.toString(140));
					tvCount.setTextColor(getResources().getColor(android.R.color.primary_text_light));
					tweetButton.setEnabled(false);
				}				
			}
		});
		
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//apply custom styles
		/*
		int contentId = Resources.getSystem().getIdentifier("content", "id", "android");
		FrameLayout contentView = (FrameLayout)getDialog().findViewById(contentId);
		contentView.setBackgroundColor(getResources().getColor(R.color.translucent_light_teal));
		*/
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		
		public void onFragmentInteraction(String tweetText);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnTweet:
			if(validate()) {
				
				if (mListener != null) {
					mListener.onFragmentInteraction(etText.getText().toString());
				}
				etText.setText("");
				dismiss();
			}else{
				etText.setError(getResources().getString(R.string.error_notext));
			}
			
			break;
		}
		
	}
	
	private boolean validate() {
		if(etText.getText() != null && (etText.getText().length() > 0 && etText.getText().length() < 141)) {
			return true;
		}
		return false;
	}

}

