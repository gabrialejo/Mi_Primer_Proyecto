package com.phonegap;

import com.phonegap.DroidGap;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

public class KeyBoard {

        private WebView mAppView;
        private DroidGap mGap;

        public KeyBoard(DroidGap gap, WebView view)
        {
            mAppView = view;
            mGap = gap;
        }

        public void showKeyBoardInThisField(String field) {
        	mAppView.loadUrl("javascript:dojo.byId('"+field+"').focus();");
        	mAppView.requestFocus(View.FOCUS_DOWN);
        	mAppView.requestFocus();
            InputMethodManager mgr = (InputMethodManager) mGap.getSystemService(Context.INPUT_METHOD_SERVICE);
            // only will trigger it if no physical keyboard is open
            mgr.showSoftInput(mAppView, InputMethodManager.SHOW_IMPLICIT);
            
            ((InputMethodManager) mGap.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mAppView, 0); 

        }
        
        
        
        public void showKeyBoard() {
        	mAppView.requestFocus(View.FOCUS_DOWN);
            InputMethodManager mgr = (InputMethodManager) mGap.getSystemService(Context.INPUT_METHOD_SERVICE);
            // only will trigger it if no physical keyboard is open
            mgr.showSoftInput(mAppView, InputMethodManager.SHOW_IMPLICIT);
            
            ((InputMethodManager) mGap.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mAppView, 0); 

        }
        
        public void hideKeyBoard() {
            InputMethodManager mgr = (InputMethodManager) mGap.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mAppView.getWindowToken(), 0);
        }



}