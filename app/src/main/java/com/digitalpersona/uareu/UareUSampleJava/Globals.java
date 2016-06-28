/* 
 * File: 		Globals.java
 * Created:		2013/05/03
 * 
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.digitalpersona.uareu.UareUSampleJava;

import java.nio.ByteBuffer;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

public class Globals {    
    public Reader getReader(String serialNumber) throws UareUException {				
		getReaders();
    	
		for (int nCount = 0; nCount < readers.size(); nCount++)
			if (readers.get(nCount).GetDescription().serial_number.equals(serialNumber)) 
			{
				if(readers.get(nCount).GetDescription().technology == Reader.Technology.HW_TECHNOLOGY_OPTICAL)
				{
					disableCamera();
				}

				return readers.get(nCount);
			}
		return null;
    }
    
    public ReaderCollection getReaders() throws UareUException
    {
		readers = UareUGlobal.GetReaderCollection();	

		readers.GetReaders();
		
    	return readers;    	
    }

    private Camera m_Camera = null;

    // re-enable camera on exit
    public void enableCamera()
    {

		try
		{
	    	if (m_Camera != null)
	    	{
	    		m_Camera.release();
	    		m_Camera = null;    		
	    	}			
		}
		catch (Exception e)
		{
		}

    }
  
  
	// prevents gallery app from popping up whenever a finger is detected by the fingerprint reader
	private void disableCamera()
	{

		try
		{
			if (m_Camera == null)
			{
		        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
		            CameraInfo camInfo = new CameraInfo();
		            Camera.getCameraInfo(camNo, camInfo);
		           
		            if (camInfo.facing==(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
		                m_Camera = Camera.open(camNo);
		            }
		        }
		        if (m_Camera == null) {
		             m_Camera = Camera.open();
		        }
			}		
		}
		catch (Exception e)
		{
			
		}

	}


    private ReaderCollection readers = null;
    private static Globals instance;

    static {
        instance = new Globals();
    }

    public static Globals getInstance() {
        return Globals.instance;
    }

    private static Bitmap m_lastBitmap = null;
    
    public static void ClearLastBitmap()
    {
    	m_lastBitmap = null;
    }
    
    public static Bitmap GetLastBitmap()
    {
    	return m_lastBitmap;
    }

    public static Bitmap GetBitmapFromRaw(byte[] Src, int width, int height)
    {	
		byte [] Bits = new byte[Src.length*4]; 
		int i = 0;
		for(i=0;i<Src.length;i++)
		{
		    Bits[i*4] = Bits[i*4+1] = Bits[i*4+2] = (byte)Src[i]; 
		    Bits[i*4+3] = -1;
		}
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
		
		// save bitmap to history to be restored when screen orientation changes
		m_lastBitmap = bitmap;
		return bitmap;
	}
}
