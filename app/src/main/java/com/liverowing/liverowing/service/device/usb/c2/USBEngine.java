package com.liverowing.liverowing.service.device.usb.c2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

public class USBEngine {

    private ReentrantLock usbLock = new ReentrantLock(true);

    private static final String TAG = "USBEngine";

    private UsbRequest inReq, outReq;
    private ByteBuffer outBuffer, inBuffer;
    private UsbEndpoint epIn, epOut;
    private UsbInterface interf;
    private UsbDeviceConnection conn;
    boolean run = false;

    private final int MAX_BUFFER_SIZE_OUT = 127;
    private final int MAX_BUFFER_SIZE_IN = 128;


    public boolean start(Context context) {
        UsbManager usbman = (UsbManager) context.getSystemService(context.USB_SERVICE);
        HashMap<String, UsbDevice> devmap = usbman.getDeviceList();

        for (UsbDevice dev : devmap.values())
            if (dev.getVendorId() == 6052) {

                logcatd(dev.getDeviceName() + " " + dev.getDeviceId() + " " + dev.getVendorId() + " " + dev.getProductId());
                conn = usbman.openDevice(dev);

                if (conn == null) {
                    logcate("connection is null");
                    return false;
                }

                interf = dev.getInterface(0);

                logcatd("Interface Count: " + dev.getInterfaceCount());
                if (!conn.claimInterface(interf, true)) {
                    logcate("could not claim interface!");
                    conn.close();
                    break;
                }
                logcatd("claimed interface " + interf.getEndpointCount());
                for (int i = 0; i < interf.getEndpointCount(); i++) {
                    UsbEndpoint ep = interf.getEndpoint(i);
                    logcatd("ep type " + ep.getType() + " ep dir " + ep.getDirection() + " ep addr " + String.format("0x%02X", ep.getAddress()));
                }

                epIn = interf.getEndpoint(0);
                epOut = interf.getEndpoint(1);

                inReq = new UsbRequest();
                inReq.initialize(conn, epIn);
                outReq = new UsbRequest();
                outReq.initialize(conn, epOut);
                run = true;
                return true;
            }
        return false;
    }


    Handler handler;
    byte[] command;
    byte reportID;

    public byte[] getPMData(Handler hndlr, byte id, byte[] command) {
        usbLock.lock();
        synchronized (usbLock) {
            try {
                reportID = id;
                handler = hndlr;
                byte[] content = command;
                byte chk = Csafe.checksum(content);
                byte[] stuffed = Csafe.stuff(content);
                byte[] buf = Csafe.create(reportID, stuffed, stuffed.length, chk);
                byte[] returnData;

                outBuffer = ByteBuffer.wrap(buf);
                inBuffer = ByteBuffer.wrap(new byte[MAX_BUFFER_SIZE_IN]);

                outBuffer.position(0);
                inBuffer.position(0);

                if (conn == null) {
                    return null;
                }

                if (!run) return null;
                outReq.queue(outBuffer, MAX_BUFFER_SIZE_OUT);//queue  request
                outBuffer.position(0);

                String requestString = "", responseString = "";
                requestString = toString(outBuffer.array());
                logcatd("out request " + requestString);

                inReq.queue(inBuffer, MAX_BUFFER_SIZE_IN);
                UsbRequest req = conn.requestWait();
                req = conn.requestWait();

                if (req == null) {
                    return null;
                }

                if (req.getEndpoint().getDirection() == UsbConstants.USB_DIR_IN) {//in request
                    inBuffer.position(0);
                    responseString = toString(inBuffer.array());
                    logcatd("in response " + responseString);
                    inBuffer.position(0);

                    byte[] extract = Csafe.extract(inBuffer.array());
                    if (extract == null) {
                        logcate("error in Csafe extract");
                        return null;
                    }
                    Pair<byte[], Byte> destuff = Csafe.destuff(extract);

                    if (!Csafe.verify(destuff.first, destuff.second)) {
                        String s1 = toString(extract);
                        logcate("verification failed " + s1);
                        return null;
                    } else {
                        returnData = destuff.first;
                    }
                } else {
//					logcate("null or incorrect response" + "\nrequest " + requestString + "\nresponse " + responseString);
                    return null;
                }

                if (handler != null) {
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("Value", returnData);
                    Message message = handler.obtainMessage(0);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    return null;
                } else {
                    return returnData;
                }
            } catch (Exception e) {
                logcate("Exception" + e);
            } catch (Error e) {
                logcate("Error in USBEngine");
            } finally {
                usbLock.unlock();
                usbLock.notifyAll();
            }
        }
        return null;
    }


    public void stop() {
        try {
            if (usbLock.isLocked()) usbLock.wait();

            if (inReq != null) inReq.cancel();
            if (outReq != null) outReq.cancel();

            if (conn != null) {
                conn.releaseInterface(interf);
                logcatd("released interface");
                conn.close();
                logcatd("closed connection");
            }
        } catch (Exception e) {
            logcate("could not unregister receiver, already unregistered");
        }
        Log.d(TAG, "Task ended");
        logcatd("Task ended");
    }


    String cumLog = "";

    public void logcatd(String newData) {
//		Log.d(TAG, newData);
    }

    public void logcate(String newData) {
        Log.e(TAG, newData);
    }

    public String toString(byte[] bytes) {
        String byteString = "";
        for (byte b : bytes) {
            byteString += String.format("%02X ", b);
            if (b == (byte) 0xf2) break;
        }
        return byteString;
    }

}
