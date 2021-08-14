/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.apps4av.avarehelper;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps4av.avarehelper.connections.FileConnectionIn;
import com.apps4av.avarehelper.storage.Preferences;
import com.apps4av.avarehelper.storage.SavedEditText;

/**
 * 
 * @author zkhan
 * 
 */
public class FileFragment extends Fragment {

    private FileConnectionIn mFile;
    private Context mContext;
    private Button mConnectButton;
    private SavedEditText mTextFile;
    private Uri mUri;

    // Request code for selecting a document.
    private static final int PICK_FILE = 2;

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == PICK_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                mUri = resultData.getData();
                mFile.connect(mUri.toString(), false);
                if(mFile.isConnected()) {
                    mFile.start(new Preferences(getActivity()));
                }
                setStates();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        mContext = container.getContext();

        View view = inflater.inflate(R.layout.layout_play, container, false);

        mTextFile = (SavedEditText)view.findViewById(R.id.main_file_name);
        mConnectButton = (Button)view.findViewById(R.id.main_button_connect_file);
        mConnectButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /*
                 * If connected, disconnect
                 */
                if(mFile.isConnected()) {
                    mFile.stop();
                    mFile.disconnect();
                    setStates();
                    return;
                }
                
                /*
                 * Connect to the given file
                 */
                String val = mTextFile.getText().toString();
                if(null != val && (!mFile.isConnected())) {                    
                    mConnectButton.setText(mContext.getString(R.string.Start));
                    openFile(Uri.parse(val));
                }
            }
        });

        /*
         * List of BT devices is same
         */
        mFile = FileConnectionIn.getInstance(mContext);

        setStates();
        return view;

    }
    
    /**
     * 
     */
    private void setStates() {
        if(mFile.isConnected()) {
            mConnectButton.setText(mContext.getString(R.string.Stop));
        }
        else {
            mConnectButton.setText(mContext.getString(R.string.Start));                        
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}