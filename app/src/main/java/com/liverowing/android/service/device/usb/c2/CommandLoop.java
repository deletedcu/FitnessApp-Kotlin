package com.liverowing.android.service.device.usb.c2;


import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;


public class CommandLoop {
    Handler handler;
    byte[][] commands;
    byte reportID;
    int delay;
    USBEngine engine;
    boolean run = true;


    public CommandLoop(Handler handler, byte reportID, byte[][] commands, int delay, USBEngine engine) {
        this.handler = handler;
        this.reportID = reportID;
        this.commands = commands;
        this.delay = delay;
        this.engine = engine;
        start();
    }


    public void start() {
        if (delay == 0) new getData().execute(commands);
        else {
            loop();
        }
    }

    public void stop() {
        run = false;
        if (timer != null) timer.cancel();
    }


    CountDownTimer timer;

    public void loop() {
        if (run & java.lang.Thread.activeCount() < 120) new getData().execute(commands);
        timer = new CountDownTimer(delay, delay) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (run) loop();
            }
        }.start();
    }


    private class getData extends AsyncTask<byte[][], Integer, Void> {
        protected Void doInBackground(byte[][]... commands) {
            byte[][] cmds = commands[0];
            for (byte[] cmd : cmds) {
                if (run) {
                    engine.getPMData(handler, reportID, cmd);
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }


}
