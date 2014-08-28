package com.boha.coursemaker.util;

import android.content.Context;
import android.os.Build;
import com.boha.coursemaker.dto.GcmDeviceDTO;

public class GCMDeviceUtil {

	public static GcmDeviceDTO getDevice(Context ctx) {
		GcmDeviceDTO d = new GcmDeviceDTO();
		
		d.setModel(Build.MODEL);
		d.setManufacturer(Build.MANUFACTURER);
		d.setSerialNumber(Build.SERIAL);
		d.setProduct(Build.DISPLAY);
		
		return d;
	}
}
