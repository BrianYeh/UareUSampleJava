/* 
 * File: 		VerificationActivity.java
 * Created:		2013/05/03
 * 
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.digitalpersona.uareu.UareUSampleJava;

import java.text.DecimalFormat;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.Reader.Priority;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

public class VerificationActivity extends Activity {

    private Button m_back;    
    private String m_sn = "";
    private String m_deviceName = "";

	private String m_enginError;

    private Reader m_reader = null;
	private Bitmap m_bitmap = null;
	private ImageView m_imgView;
	private TextView m_selectedDevice;
	private TextView m_title;
	private boolean m_reset = false;
	private CountDownTimer m_timer = null;

	private TextView m_text;
	private TextView m_text_conclusion;
	private Engine m_engine = null; 
    private Fmd m_fmd = null;
    private int m_score = -1;
	private boolean m_first = true;	
	private boolean m_resultAvailableToDisplay = false;
	private Reader.CaptureResult cap_result = null;
	
	private void initializeActivity()
	{    
        m_title = (TextView) findViewById(R.id.title);      
        m_title.setText("Verification");

	m_enginError = "";

        m_selectedDevice = (TextView) findViewById(R.id.selected_device);
        m_sn = getIntent().getExtras().getString("serial_number");        
        m_deviceName = getIntent().getExtras().getString("device_name");

	m_selectedDevice.setText("Device: " + m_deviceName);

        m_imgView = (ImageView) findViewById(R.id.bitmap_image);    
        m_bitmap = Globals.GetLastBitmap();        
        if (m_bitmap == null) m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        m_imgView.setImageBitmap(m_bitmap);
        m_back = (Button) findViewById(R.id.back);
        
        m_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onBackPressed();
            }
        });    
        
        m_text = (TextView) findViewById(R.id.text);
        m_text_conclusion = (TextView) findViewById(R.id.text_conclusion);  
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine);
        
        initializeActivity();    

        // initiliaze dp sdk
        try {
            m_reader = Globals.getInstance().getReader(m_sn);
			m_reader.Open(Priority.EXCLUSIVE);
            m_engine = UareUGlobal.GetEngine(); 
		} catch (Exception e) {	
			Log.w("UareUSampleJava", "error during init of reader");
			m_sn = "";
			m_deviceName = "";
			onBackPressed();
			return;
		}

		// updates UI continuously
		m_timer = new CountDownTimer(250, 250) {
		     public void onTick(long millisUntilFinished) { }
		     public void onFinish() {
			         m_imgView.setImageBitmap(m_bitmap);
			         m_imgView.invalidate();

	        		 if (cap_result != null && cap_result.quality != null)
	        		 {
					 switch(cap_result.quality)
					 {
					 case FAKE_FINGER:
						 m_text_conclusion.setText("Fake finger");
							 m_bitmap = null;
						 break;
					 case NO_FINGER:
						 m_text_conclusion.setText("No finger");
							 m_bitmap = null;
						 break;
					 case CANCELED:
						 m_text_conclusion.setText("Capture cancelled");	
						 break;
					 case TIMED_OUT:
						 m_text_conclusion.setText("Capture timed out");	
						 break;
					 case FINGER_TOO_LEFT:
						 m_text_conclusion.setText("Finger too left");	
						 break;
					 case FINGER_TOO_RIGHT:
						 m_text_conclusion.setText("Finger too right");	
						 break;
					 case FINGER_TOO_HIGH:
						 m_text_conclusion.setText("Finger too high");	
						 break;
					 case FINGER_TOO_LOW:
						 m_text_conclusion.setText("Finger too low");	
						 break;
					 case FINGER_OFF_CENTER:
						 m_text_conclusion.setText("Finger off center");	
						 break;
					 case SCAN_SKEWED:
						 m_text_conclusion.setText("Scan skewed");	
						 break;
					 case SCAN_TOO_SHORT:
						 m_text_conclusion.setText("Scan too short");	
						 break;
					 case SCAN_TOO_LONG:
						 m_text_conclusion.setText("Scan too long");	
						 break;
					 case SCAN_TOO_SLOW:
						 m_text_conclusion.setText("Scan too slow");	
						 break;
					 case SCAN_TOO_FAST:
						 m_text_conclusion.setText("Scan too fast");	
						 break;
					 case SCAN_WRONG_DIRECTION:
						 m_text_conclusion.setText("Wrong direction");	
						 break;
					 case READER_DIRTY:
						 m_text_conclusion.setText("Reader dirty");	
						 break;
					 case GOOD:
						 m_text_conclusion.setText("");
						 break;			        			 
				     	 default:
				    	 	if (cap_result.image == null)
				    	 	{
							 m_text_conclusion.setText("An error occurred");			        	    		 
				    	 	}
					 }
	        		 }

				if(!m_enginError.isEmpty())
				{
					 m_text_conclusion.setText("Engine: " + m_enginError);			        	    		 

				}
	        		 else if (m_fmd == null)
			         {
			        	 if ((!m_first) && (m_resultAvailableToDisplay))
			        	 {			        		 
			        		 if (m_text_conclusion.getText().length() == 0)
			        		 {
			        			 DecimalFormat formatting = new DecimalFormat("##.######");
			        			 m_text_conclusion.setText("Dissimilarity Score: " + String.valueOf(m_score)+ ", False match rate: " + Double.valueOf(formatting.format((double)m_score/0x7FFFFFFF)) + " (" + (m_score < (0x7FFFFFFF/100000) ? "match" : "no match") + ")"); 
							
							
			        		 }			        			
			        	 }
			        	 
			        	 m_text.setText("Place any finger on the reader");
			         }
			         else
			         {
			        	 m_first = false;
			        	 
			        	 m_text.setText("Place the same or a different finger on the reader");
			         }
		 	     
		    	 if (!m_reset)
		    		 m_timer.start();
		     }
		  }.start();

        // loop capture on a separate thread to avoid freezing the UI
		new Thread(new Runnable()
	    {
	        @Override
	        public void run()
	        {
//	            try {
	        		m_reset = false;
		            while (!m_reset)
		            {
				try {
			            	cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1);
				}
				catch (Exception e) 
				{
					Log.w("UareUSampleJava", "error during capture: " + e.toString());
					m_sn = "";
					m_deviceName = "";
					onBackPressed();
				}

		            	m_resultAvailableToDisplay = false;

		            	// an error occurred
		            	if (cap_result == null || cap_result.image == null) continue;

				try {
					m_enginError="";

				    	// save bitmap image locally
				    	m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());

			    			if (m_fmd == null)
			    				m_fmd = m_engine.CreateFmd(cap_result.image, Fmd.Format.ANSI_378_2004);
			    			else
			    			{
			    				m_score = m_engine.Compare(m_fmd, 0, m_engine.CreateFmd(cap_result.image, Fmd.Format.ANSI_378_2004), 0);
			    				m_fmd = null;
							m_resultAvailableToDisplay = true;
			    			}

				} catch (Exception e) 
				{
					m_enginError = e.toString();
					Log.w("UareUSampleJava", "Engine error: " + e.toString());
				}

		            }
/*
				} catch (Exception e) 
				{
					Log.w("UareUSampleJava", "error during capture");
					m_sn = "";
					m_deviceName = "";
					onBackPressed();
					return;
				}
*/
	        }
	    }).start();  
    }
    
    @Override
    public void onBackPressed() {    	
		try {
			m_reset = true;
			m_reader.CancelCapture();	
			m_reader.Close();

			// re-enable camera
			Globals.getInstance().enableCamera(); 
		} catch (Exception e) {
			Log.w("UareUSampleJava", "error during reader shutdown");
		} 

		Intent i = new Intent();
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		setResult(Activity.RESULT_OK, i);					

//		setResult(Activity.RESULT_OK, new Intent().putExtra("serial_number", m_sn));
    	finish();
    }

    // called when orientation has changed to manually destroy and recreate activity
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    	setContentView(R.layout.activity_engine);     
    	
    	initializeActivity();
    }
}
