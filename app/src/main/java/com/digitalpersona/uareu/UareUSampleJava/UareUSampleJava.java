/* 
 * File: 		UareUSampleJava.java
 * Created:		2013/05/03
 * 
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.digitalpersona.uareu.UareUSampleJava;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import android.content.Context;
import android.app.PendingIntent; 
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import android.util.Log;


public class UareUSampleJava extends Activity {
	private final int GENERAL_ACTIVITY_RESULT = 1;

	private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
	
	private TextView m_selectedDevice;
	private Button m_getReader;
	private Button m_getCapabilities;
	private Button m_captureFingerprint;
	private Button m_streamImage;
	private Button m_enrollment;
	private Button m_verification;
	private Button m_identification;
	private String m_sn = "";
	private String m_deviceName = "";

	private boolean isCamera;

	Reader m_reader;

	@Override
	public void onStop() {
		// reset you to initial state when activity stops
		m_selectedDevice.setText("Device: (No Reader Selected)");
		
		setButtonsEnabled(false);
		
		super.onStop();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_getReader = (Button) findViewById(R.id.get_reader);
		m_getCapabilities = (Button) findViewById(R.id.get_capabilities);
		m_captureFingerprint = (Button) findViewById(R.id.capture_fingerprint);
		m_streamImage = (Button) findViewById(R.id.stream_image);
		m_enrollment = (Button) findViewById(R.id.enrollment);
		m_verification = (Button) findViewById(R.id.verification);
		m_identification = (Button) findViewById(R.id.identification);
		m_selectedDevice = (TextView) findViewById(R.id.selected_device);

		setButtonsEnabled(false);
		
		m_selectedDevice.setText("Device: (No Reader Selected)");

		// register handler for UI elements
		m_getReader.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchGetReader();
			}
		});

		m_getCapabilities.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchGetCapabilities();
			}
		});

		m_captureFingerprint.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchCaptureFingerprint();
			}
		});

		m_streamImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchStreamImage();
			}
		});

		m_enrollment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchEnrollment();
			}
		});

		m_verification.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchVerification();
			}
		});

		m_identification.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchIdentification();
			}
		});
	}

	protected void launchGetReader() {
		Intent i = new Intent(UareUSampleJava.this, GetReaderActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchGetCapabilities() {
		Intent i = new Intent(UareUSampleJava.this,
				GetCapabilitiesActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchCaptureFingerprint() {
		Intent i = new Intent(UareUSampleJava.this,
				CaptureFingerprintActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchStreamImage() {
		Intent i = new Intent(UareUSampleJava.this, StreamImageActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchEnrollment() {
		Intent i = new Intent(UareUSampleJava.this, EnrollmentActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchVerification() {
		Intent i = new Intent(UareUSampleJava.this, VerificationActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchIdentification() {
		Intent i = new Intent(UareUSampleJava.this,
				IdentificationActivity.class);
		i.putExtra("serial_number", m_sn);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	@Override
	public void onBackPressed() { 
		super.onBackPressed();
	}

	protected void setButtonsEnabled(Boolean enabled) {
		m_getCapabilities.setEnabled(enabled);
		m_streamImage.setEnabled(enabled);
		m_captureFingerprint.setEnabled(enabled);
		m_enrollment.setEnabled(enabled);
		m_verification.setEnabled(enabled);
		m_identification.setEnabled(enabled);
	}

	protected void setButtonsEnabled_Capture(Boolean enabled) {
		m_captureFingerprint.setEnabled(enabled);
		m_enrollment.setEnabled(enabled);
		m_verification.setEnabled(enabled);
		m_identification.setEnabled(enabled);
	}

	protected void setButtonsEnabled_Stream(Boolean enabled) {
		m_streamImage.setEnabled(enabled);
	}


	protected void CheckDevice() {
		try {
			m_reader.Open(Priority.EXCLUSIVE);
			setButtonsEnabled(true);
			setButtonsEnabled_Capture(m_reader.GetCapabilities().can_capture); 
			setButtonsEnabled_Stream(m_reader.GetCapabilities().can_stream); 
			m_reader.Close();
			Globals.getInstance().enableCamera();
		} catch (UareUException e1) {
			displayReaderNotFound();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {


		if (data == null)
		{
			displayReaderNotFound();
			return;
		}
		
		Globals.ClearLastBitmap();
		m_sn = (String) data.getExtras().get("serial_number");
		m_deviceName = (String) data.getExtras().get("device_name");
		m_selectedDevice.setText("Device: " + m_deviceName);
		
		switch (requestCode) {
		case GENERAL_ACTIVITY_RESULT:
				
				if( 	(m_deviceName != null && !m_deviceName.isEmpty()) &&
					(m_sn != null && !m_sn.isEmpty()) )
				{
					try {
						m_reader = Globals.getInstance().getReader(m_sn);

						if(null != m_reader){
							//for video devices we have to set permissions
							try{
								String strFile = "/dev/" + m_deviceName.substring(0, m_deviceName.indexOf('{'));
								Process sh = Runtime.getRuntime().exec(new String[]{ "su", "-c", "chmod 666 " + strFile});
								sh.waitFor();
							}
							catch(java.io.IOException e){ e.printStackTrace(); }
							catch(java.lang.InterruptedException e){ e.printStackTrace(); }
								
							CheckDevice();
						} else {
							displayReaderNotFound();
						}
					} catch (UareUException e) {
						displayReaderNotFound();
					}
				} else { 
					displayReaderNotFound();
				}
			
			break;
		}

	}
	
	private void displayReaderNotFound()
	{
		// empty serial number means reader not found		
		m_selectedDevice.setText("Device: (No Reader Selected)");
		
		Globals.getInstance().enableCamera();

		setButtonsEnabled(false);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setTitle("Reader Not Found");

		alertDialogBuilder
				.setMessage("Plug in a reader and try again.")
				.setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();		
	}


}
