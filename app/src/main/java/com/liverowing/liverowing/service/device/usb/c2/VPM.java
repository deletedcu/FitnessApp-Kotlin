package com.liverowing.liverowing.service.device.usb.c2;

import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VPM {

	private static final String TAG = "VPM";
	final public int READ_INTERVAL = 500;

	final byte GET_STATUS = 		(byte) 0x80;                   		
	final byte GET_WORK = 			(byte) 0xA0;                             
	final byte GET_HORIZONTAL = 	(byte) 0xA1;                     
	final byte GET_CALORIES = 		(byte) 0xA3;                        
	final byte GET_CADENCE = 		(byte) 0xA7;                          
	final byte GET_POWER = 			(byte) 0xB4;                          
	final byte GET_PACE = 			(byte) 0xA6;                            
	final byte GET_HRCUR = 			(byte) 0xB0;                                                  
	final byte GET_PROGRAM = 		(byte) 0xA4;                                                                      
	final byte GET_ID = 			(byte) 0x91;                                                        
	final byte GET_SERIAL = 		(byte) 0x94;                                                        
	final byte GET_ODOMETER = 		(byte) 0x9B;                                                 
	final byte GET_ERRORCODE =	 	(byte) 0x9C;                                
	final byte GET_USERINFO = 		(byte) 0xAB;                                                  

	final byte[] shortCommands = {
			//GET_STATUS,
			//GET_WORK,
			GET_HORIZONTAL,
			GET_CALORIES,
			//GET_CADENCE,
			//GET_POWER,
			//GET_PACE,
			GET_HRCUR,
			//GET_PROGRAM,
			//GET_ID,
			//GET_SERIAL,
			//GET_ODOMETER,
			//GET_ERRORCODE,
			//GET_USERINFO		
	};

	byte   SET_USERCFG1 = 0x1a;
	byte[] GET_STROKESTATS = {SET_USERCFG1, 0x02, 0x6e, 00};
	byte[] GET_WORKOUTTYPE = {SET_USERCFG1, 0x01, (byte)0x89}; 
	//byte[] GET_DRAGFACTOR = {SET_USERCFG1, 0x01, (byte)0xC1};                  
	//byte[] GET_STROKESTATE = {SET_USERCFG1, 0x01, (byte)0xBF};    
	//byte[] GET_WORKTIME = {SET_USERCFG1, 0x01, (byte)0xA0};                   
	//byte[] GET_WORKDISTANCE = {SET_USERCFG1, 0x01, (byte)0xA3};            
	//byte[] GET_ERRORVALUE = {SET_USERCFG1, 0x01, (byte)0xC0};                    
	byte[] GET_WORKOUTSTATE = {SET_USERCFG1, 0x01, (byte)0x8D};              
	byte[] GET_WORKOUTINTERVALCOUNT = {SET_USERCFG1, 0x01, (byte)0x9F};       
	byte[] GET_INTERVALTYPE = {SET_USERCFG1, 0x01, (byte)0x8E};   
	//	byte[] GET_RESTTIME = {SET_USERCFG1, 0x01, (byte)0xCF};                 
	//byte[] GET_DISPLAYTYPE = {SET_USERCFG1, 0x01, (byte)0x8A};     
	//byte[] GET_DISPLAYUNITS = {SET_USERCFG1, 0x01, (byte)0x8B};  

	byte GET_PMCFGCMD = (byte)0x7E;	
	//byte[] GET_FW_VERSION = {GET_PMCFGCMD, 0x01, (byte)0x80};			//e.g returns ASCII for 8200-0 (PM3 loader)
	//byte[] GET_HW_VERSION = {GET_PMCFGCMD, 0x01, (byte)0x81};			//e.g returns ASCII for 8200-000022-315 (PM3 Application)
	byte[] GET_HWADDRESS = {GET_PMCFGCMD, 0x01, (byte)0x82};
	byte[] GET_HRM = {GET_PMCFGCMD, 0x01, (byte)0x84};
	byte[] GET_DATETIME = {GET_PMCFGCMD, 0x01, (byte)0x85};
	byte[] GET_PRODUCTCONFIGURATION= {GET_PMCFGCMD, 0x01, (byte)0x9A};
	byte[] GET_INTERNALLOGPARAMS = {GET_PMCFGCMD, 0x01, (byte)0x99};
	byte[] GET_INTERNALLOGMEMORY = {GET_PMCFGCMD, 0x07, 0x6a, 0x05};
	byte[] GET_HRBELT_INFO = {GET_PMCFGCMD, 0x03, 0x56, 0x01};     //e.g. f1 7e 03 56 01 00 2a f2

	byte SET_PMCFGCMD = (byte)0x76;
	byte[] SET_SCREENSTATE ={SET_PMCFGCMD, 0x04, 0x13, 0x02};  //e.g. f1 76 04 13 02 byte1 byte2 checksum f2
	byte[] SET_AUTHENPASSWORD ={SET_PMCFGCMD, 0x0e, 0x1a, 0x0c};   //e.g. f1 76 0e 1a 0c 17 d9 4e 64  BA 3E 34 A2 3F DE 5F 57 71 f2
	byte[] SET_DATETIME ={SET_PMCFGCMD, 0x09, 0x22, 0x07};   
	byte[] SET_HRBELT_INFO ={SET_PMCFGCMD, 0x07, 0x2d, 0x05};     // f1 76 07 2d 05 01 02 04 51 18 17 f2


	final byte GET_PMDATACMD = 				(byte)0x7F;
	final byte GET_WORKTIME = 				(byte)0xA0;     		
	final byte GET_PROJECTED_WORKTIME = 	(byte)0xA1;     		
	final byte GET_TOTAL_RESTTIME =			(byte)0xA2;     		
	final byte GET_WORKDISTANCE = 			(byte)0xA3;     		
	final byte GET_TOTAL_WORKDISTANCE = 	(byte)0xA4;     		
	final byte GET_PROJECTED_WORKDISTANCE = (byte)0xA5;     		
	final byte GET_RESTDISTANCE = 			(byte)0xA6;     		
	final byte GET_TOTAL_RESTDISTANCE = 	(byte)0xA7;
	final byte GET_STROKE_500M_PACE = 		(byte)0xA8;     		
	final byte GET_STROKE_POWER = 			(byte)0xA9;     		
	final byte GET_STROKE_CALORICBRUNRATE = (byte)0xAA;     		
	final byte GET_SPLIT_AVG_500M_PACE = 	(byte)0xAB;     		
	final byte GET_SPLIT_AVG_POWER = 		(byte)0xAC;     		
	final byte GET_SPLIT_AVG_CALORICBURNRATE = (byte)0xAD;
	final byte GET_SPLIT_AVG_CALORIES = 	(byte)0xAE;     		
	final byte GET_TOTAL_AVG_500MPACE = 	(byte)0xAF;     		
	final byte GET_TOTAL_AVG_POWER = 		(byte)0xB0;     		
	final byte GET_TOTAL_AVG_CALORICBURNRATE = 	(byte)0xB1;     		
	final byte GET_TOTAL_AVG_CALORIES = 	(byte)0xB2;     		
	final byte GET_STROKE_RATE = 			(byte)0xB3;     		
	final byte GET_SPLIT_AVG_STROKERATE = 	(byte)0xB4;     		
	final byte GET_TOTAL_AVG_STROKERATE = 	(byte)0xB5;     		
	final byte GET_AVG_HEART_RATE = 		(byte)0xB6;     		
	final byte GET_ENDING_AVG_HEART_RATE = 	(byte)0xB7;     		
	final byte GET_REST_AVG_HEART_RATE = 	(byte)0xB8;     		
	final byte GET_SPLITTIME = 				(byte)0xB9;     		    		
	final byte GET_LAST_SPLITTIME = 		(byte)0xBA;           
	final byte GET_SPLITDISTANCE = 			(byte)0xBB;     		
	final byte GET_LAST_SPLITDISTANCE = 	(byte)0xBC; 	     
	final byte GET_LAST_RESTDISTANCE = 		(byte)0xBD;     		
	final byte GET_TARGETPACETIME = 		(byte)0xBE;     		
	final byte GET_STROKESTATE = 			(byte)0xBF;            
	final byte GET_STROKERATESTATE = 		(byte)0xC0;            
	final byte GET_DRAGFACTOR =				(byte)0xC1;            
	final byte GET_ENCODER_PERIOD = 		(byte)0xC2;            
	final byte GET_HEARTRATESTATE = 		(byte)0xC3;            
	final byte GET_SYNC_DATA = 				(byte)0xC4;            
	final byte GET_SYNC_DATAALL = 			(byte)0xC5;            
	final byte GET_RACE_DATA = 				(byte)0xC6;            
	final byte GET_TICK_TIME = 				(byte)0xC7;            
	final byte GET_ERRORTYPE = 				(byte)0xC8;            
	final byte GET_ERRORVALUE = 			(byte)0xC9;            
	final byte GET_STATUSTYPE = 			(byte)0xCA;            
	final byte GET_STATUSVALUE = 			(byte)0xCB;            
	final byte GET_EPMSTATUS = 				(byte)0xCC;            
	final byte GET_DISPLAYUPDATETIME = 		(byte)0xCD;            
	final byte GET_SYNCFRACTIONALTIME = 	(byte)0xCE;            
	final byte GET_RESTTIME = 				(byte)0xCF;     

	final byte[] PMDataCommands = {
			GET_WORKTIME, 
			//		GET_PROJECTED_WORKTIME, 
			GET_TOTAL_RESTTIME, 
			GET_WORKDISTANCE, 
			GET_TOTAL_WORKDISTANCE,     		
			//		GET_PROJECTED_WORKDISTANCE,  		
			GET_RESTDISTANCE,    		
			GET_TOTAL_RESTDISTANCE,
			GET_STROKE_500M_PACE,  		
			GET_STROKE_POWER,   		
			GET_STROKE_CALORICBRUNRATE,   		
			GET_SPLIT_AVG_500M_PACE,		
			GET_SPLIT_AVG_POWER,
			//		GET_SPLIT_AVG_CALORICBURNRATE,
			GET_SPLIT_AVG_CALORIES,		
			GET_TOTAL_AVG_500MPACE,  		
			GET_TOTAL_AVG_POWER,   		
			//		GET_TOTAL_AVG_CALORICBURNRATE, 		
			GET_TOTAL_AVG_CALORIES,
			GET_STROKE_RATE,
			GET_SPLIT_AVG_STROKERATE,
			GET_TOTAL_AVG_STROKERATE,
			GET_AVG_HEART_RATE,
			GET_ENDING_AVG_HEART_RATE,
			GET_REST_AVG_HEART_RATE,
			GET_SPLITTIME,
			GET_LAST_SPLITTIME,
			GET_SPLITDISTANCE,
			GET_LAST_SPLITDISTANCE,
			GET_LAST_RESTDISTANCE,	
			//		GET_TARGETPACETIME,
			//		GET_STROKESTATE,
			//		GET_STROKERATESTATE,
			GET_DRAGFACTOR,
			//		GET_ENCODER_PERIOD,
			//		GET_HEARTRATESTATE,
			//		GET_SYNC_DATA,
			//		GET_SYNC_DATAALL,
			//		GET_RACE_DATA,
			//		GET_TICK_TIME,
			//		GET_ERRORTYPE,
			//		GET_ERRORVALUE,
			//		GET_STATUSTYPE,
			//		GET_STATUSVALUE,
			//		GET_EPMSTATUS,
			//		GET_DISPLAYUPDATETIME,
			//		GET_SYNCFRACTIONALTIME,
			GET_RESTTIME
	};


	public class WorkoutType {
		final static int JustRowNoSplits = 0;
		final static int JustRowSplits = 1;
		final static int FixedDistanceNoSplits = 2;
		final static int FixedDistanceSplits = 3;
		final static int FixedTimeNoSplits = 4;
		final static int FixedTimeSplits = 5;
		final static int FixedTimeInterval = 6;
		final static int FixedDistanceInterval = 7;
		final static int VariableInterval = 8;
		final static int VariableIntervalUndefinedRest = 9;
		final static int FixedCalorieSplits = 10;
		final static int FixedWattSecSplits = 11;
	}

	public class WorkoutState {
		final static int WaitingToBegin = 0;
		final static int WorkoutRow = 1;
		final static int CountDownPause = 2;
		final static int IntervalRest = 3;
		final static int WorkTimeInterval = 4;
		final static int WorkDistanceInterval = 5;
		final static int RestIntervalEndToWorkTimeIntervalBegin = 6;
		final static int RestIntervalEndToWorkDistanceIntervalBegin = 7;
		final static int WorkTimeIntervalEndToRestIntervalBegin = 8;
		final static int WorkDistanceIntervalEndToRestIntervalBegin = 9;
		final static int WorkoutEnd = 10;
		final static int WorkoutTerminate = 11;
		final static int WorkoutLogged = 12;
		final static int WorkoutRearm = 13;
	}

	public class RowingState {
		final static int Inactive = 0;
		final static int Active = 1;
	}

	public class IntervalType {
		final static int Time = 0;
		final static int Distance = 1;
		final static int Rest = 2;
	}

	public class SplitType {
		final static int Time = 0;
		final static int Distance = 1;
		final static int RestDistance = 2;
	}

	public class ReportID {
		final static byte s = 0x01;
		final static byte m = 0x04;
		final static byte l = 0x02;	
	}

	CommandLoop statusLoop, strokeStatsLoop, workoutTypeLoop, workoutIntervalCountLoop, intervalTypeLoop, shortCommandLoop, PMDATACMDLoop, workoutStateLoop;
	final  Handler nullHandler = null;
	USBEngine engine;


	//************************************************************************************************************************	


	public void start(Context context) {
		engine = new USBEngine();
		engine.start(context);     
		authenticatePM();
		statusLoop = new CommandLoop(statusHandler, ReportID.s, commandToArray(byteToCommand(GET_STATUS)), READ_INTERVAL / 2, engine);
		getHWFWVersions();
		status = -1;	
	}


	int FWVersion, HWVersion, PMVersion;
	boolean newFirmware = false;
	public void getHWFWVersions() {
		try {
			byte ID[] = engine.getPMData(nullHandler, ReportID.s, byteToCommand(GET_ID));
			PMVersion = (ID[5] & 0xFF);
			HWVersion = (ID[7] & 0xFF) * 0x100 + (ID[6] & 0xFF);
			FWVersion = (ID[9] & 0xFF) * 0x100 + (ID[8] & 0xFF);
		}
		catch(Exception e) {}
	}


	int HWAddress;
	byte[] HWAddressBytes;
	int [] authenticationPassword;
	int serialNumber;
	boolean authenticated;
	public void authenticatePM() {
		try {
			authenticated = false;
			while (!authenticated) {
				byte[] returnData = engine.getPMData(nullHandler, ReportID.s, GET_HWADDRESS);
				HWAddress = (returnData[5] & 0xff) * 0x1000000 + (returnData[6] & 0xff) *0x10000 + (returnData[7] & 0xff) *0x100 + (returnData[8] & 0xff);
				HWAddressBytes = new byte[4]; 
				System.arraycopy(returnData, 5, HWAddressBytes, 0, 4);

				Authentication authenticate = new Authentication();
				byte[] authenticationBytes;
				serialNumber = HWAddress;

				authenticationPassword = authenticate.getPassword(serialNumber);
				byte []authenticationPasswordBytes = new byte[8];
				for (int i=0; i<4; i++) {
					authenticationPasswordBytes[i] = (byte)(authenticationPassword[0] & 0xFF);
					authenticationPasswordBytes[i+4] = (byte)(authenticationPassword[1] & 0xFF);
					authenticationPassword[0] = authenticationPassword[0] >>> 8;
					authenticationPassword[1] = authenticationPassword[1] >>> 8;
				}
				authenticationBytes =  concatenate(SET_AUTHENPASSWORD, HWAddressBytes);
				authenticationBytes =  concatenate(authenticationBytes, authenticationPasswordBytes);

				returnData = engine.getPMData(nullHandler, ReportID.s, authenticationBytes);
				int value = (returnData[5] & 0xff);
				if (value == 1) {
					authenticated = true;
				}
			}	
		}
		catch (Exception e){}
		if (authenticated) startUSBLoops();
	}


	
	String infoBarString = "";
    public String getInfoBarString(){
        if (status == -1) infoBarString = "Please Connect PM";    
        else if (status == 1 ) infoBarString = "Ready to Start Workout";
        else if (status > 1)  infoBarString = "Workout Active";

        if (workoutState == WorkoutState.WorkoutLogged) infoBarString = "Workout Completed";
        else if (workoutState == WorkoutState.WorkoutTerminate) infoBarString = "Workout Terminated";

        if (status > -1 && !authenticated)  infoBarString = "Trying to authenticate PM";
        return infoBarString;
    }


	

	//=======================================================================




	boolean usbLoopsStarted = false;
	public void startUSBLoops() {

		byte[][] PMDataCommandsArray = new byte[PMDataCommands.length][3];
		int i=0;
		for (Byte cmd : PMDataCommands) {
			PMDataCommandsArray[i] = makePMDataCmd(cmd);
			i+=1;
		}

		byte[][] shortCommandsArray = new byte[shortCommands.length][1];		
		i=0;
		for (Byte cmd : shortCommands) {
			byte[] foobar = byteToCommand(cmd);
			shortCommandsArray[i] = foobar;
			i+=1;
		}

		PMDATACMDLoop = new CommandLoop(GetPMDataCmdHandler, ReportID.s, PMDataCommandsArray, READ_INTERVAL, engine);        
		shortCommandLoop = new CommandLoop(shortCommandsHandler, ReportID.s, shortCommandsArray, READ_INTERVAL, engine);        

		strokeStatsLoop = new CommandLoop(strokeStatsHandler, ReportID.m, commandToArray(GET_STROKESTATS), READ_INTERVAL, engine);
		workoutTypeLoop = new CommandLoop(workoutTypeHandler, ReportID.s, commandToArray(GET_WORKOUTTYPE), READ_INTERVAL, engine);
		workoutIntervalCountLoop = new CommandLoop(workoutIntervalCountHandler, ReportID.s, commandToArray(GET_WORKOUTINTERVALCOUNT), READ_INTERVAL, engine);
		workoutStateLoop = new CommandLoop(workoutStateHandler, ReportID.s, commandToArray(GET_WORKOUTSTATE), READ_INTERVAL, engine);
		intervalTypeLoop = new CommandLoop(intervalTypeHandler, ReportID.s, commandToArray(GET_INTERVALTYPE), READ_INTERVAL, engine);

		usbLoopsStarted = true;

		getPMHRBeltInfo();
	}

	
	public byte[] makePMDataCmd(byte command) {
		return new byte[] {GET_PMDATACMD, 0x01, command};
	}
	

	public void stop(){
		engine.run = false;
		statusLoop.stop();

		if (usbLoopsStarted) {
			strokeStatsLoop.stop();
			workoutTypeLoop.stop();           
			workoutIntervalCountLoop.stop();           
			workoutStateLoop.stop();           
			intervalTypeLoop.stop();           
			PMDATACMDLoop.stop();
			shortCommandLoop.stop();
		}
		engine.stop();
	}

	
	public String getDateTime() {
		String dateString = null;
		try {		
			byte[] returnData = engine.getPMData(nullHandler, ReportID.s, GET_DATETIME);
			int hour = returnData[5] & 0xFF;
			int minute = returnData[6] & 0xFF;
			int meridiem = returnData[7] & 0xFF;
			int month = returnData[8] & 0xFF;
			int day = returnData[9] & 0xFF;
			int year = ((returnData[10] & 0xFF) * 0x100) + (returnData[11] & 0xFF);

			dateString = getDate(year, month, day, hour + 12 * meridiem, minute);	
			return dateString;
		}
		catch(Exception e) {}
		return null;
	}


	public void setDateTime(Date date) {

		byte [] dateTimeBytes = new byte[7];
		dateTimeBytes[0] = (byte)((date.getHours() % 12) & 0xFF);
		dateTimeBytes[1] = (byte)((date.getMinutes()) & 0xFF);
		dateTimeBytes[2] = (byte)((date.getHours() / 12) & 0xFF);
		dateTimeBytes[3] = (byte)((date.getMonth() + 1) & 0xFF);
		dateTimeBytes[4] = (byte)((date.getDate()) & 0xFF);		
		dateTimeBytes[5] = (byte)(((1900 + date.getYear()) / 256) & 0xFF);
		dateTimeBytes[6] = (byte)(((1900 + date.getYear()) % 256) & 0xFF);

		byte[] setDateTimeCommand =  concatenate(SET_DATETIME, dateTimeBytes);
		engine.getPMData(nullHandler, ReportID.s, setDateTimeCommand);	
	}


	int pmHRBeltInfo;	
	public int getPMHRBeltInfo() {
		try {
			pmHRBeltInfo = 0;
			byte[] getHRBeltInfoBytes = concatenate(GET_HRBELT_INFO, new byte[]{0x01}); 	//add user number
			byte returnData[] = engine.getPMData(nullHandler, ReportID.s, getHRBeltInfoBytes);
			pmHRBeltInfo = (returnData[6] & 0xFF) * 0x1000000 + (returnData[7] & 0xFF) * 0x10000 + (returnData[8] & 0xFF) * 0x100 + (returnData[9] & 0xFF);

		}
		catch(Exception e) {commandError = command;} 
		return pmHRBeltInfo;
	}

	
	public void setPMHRBeltInfo(int info) {
		pmHRBeltInfo = info;
		byte[] infoBytes = new byte[4];
		for (int i=3; i>=0; i--) {
			infoBytes[i] = (byte)(info & 0xFF);
			info = info >>> 8;
		}
		byte[] setHRBeltInfoBytes = concatenate(SET_HRBELT_INFO, new byte[]{0x01});    		
		setHRBeltInfoBytes = concatenate(setHRBeltInfoBytes, infoBytes);    
		engine.getPMData(nullHandler, ReportID.s, setHRBeltInfoBytes);		
	}


	int totalDistance, lastTotalDistance;    
	int calories, latchedCalories, currentHeartRate;
	final  Handler shortCommandsHandler = new Handler() {                                         
		public void handleMessage(Message msg) {
			try {
				int temp;
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				command = (byte)(returnData[1] & 0xFF);				
				switch(command) {
				case GET_STATUS:
					break;

				case GET_HORIZONTAL:
					temp = get2BytesInt(msg);
					if (temp != -1) totalDistance = temp; 
					break;

				case GET_HRCUR:
					temp = get1BytesInt(msg);
					if (temp != -1) currentHeartRate = temp;
					break;

				case GET_CALORIES:
					temp = get2BytesInt(msg);
					if (temp != -1) calories = temp;                                  
					break;

				case GET_ID:
					break;

				}
			}
			catch(Exception e) {commandError = command;} 
		}
	};


	public int status = -1;
	final  Handler statusHandler = new Handler() {                                   
		public void handleMessage(Message msg) {
			int value;
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				value = returnData[0] & 0xFF;
				status = value & 0x0F;
			} 
			catch(Exception e)          {status = -1;} 	
		}
	};


	public double strokeDistance, strokeDriveTime, strokeRecoveryTime, strokeLength, workPerStroke, strokeSpeed;
	public int strokeCount, strokePeakForce, strokeImpulseForce, strokeAverageForce;
	final  Handler strokeStatsHandler = new Handler() {                                         
		public void handleMessage(Message msg) {
			try {
				int index = 5;
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				strokeDistance = ((double)((returnData[0 + index] & 0xFF) + (returnData[1 + index] & 0xFF)*256))/100;
				strokeDriveTime = ((double)(returnData[2 + index] & 0xFF))/100;
				if (strokeDriveTime !=0) strokeSpeed = strokeDistance / strokeDriveTime;
				strokeRecoveryTime = (((double)(returnData[3 + index] & 0xFF) + (returnData[4 + index] & 0xFF)*256))/100;
				strokeLength = ((double)(returnData[5 + index] & 0xFF))/100;
				strokeCount = (returnData[6 + index] & 0xFF) + (returnData[7 + index] & 0xFF)*256;
				strokePeakForce = (int)Math.round(((double)((returnData[8 + index] & 0xFF) + (returnData[9 + index] & 0xFF)*256))/100/2.20462);
				strokeImpulseForce = (int)Math.round(((double)((returnData[10 + index] & 0xFF) + (returnData[11 + index] & 0xFF)*256))/100/2.20462);
				strokeAverageForce = (int)Math.round(((double)((returnData[12 + index] & 0xFF) + (returnData[13 + index] & 0xFF)*256))/100/2.20462);
				workPerStroke = (returnData[14 + index] & 0xFF) + (returnData[15 + index] & 0xFF)*256;
			} 
			catch(Exception e)          {} 
		}
	};



	public int workoutType;
	final  Handler workoutTypeHandler = new Handler() {                                          
		public void handleMessage(Message msg) {
			int value;
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				value = returnData[5] & 0xFF;
			} 
			catch(Exception e)          {value = -1;} 
			if (value != -1) workoutType = value;
		}
	};

	
	public int workoutIntervalCount;
	final  Handler workoutIntervalCountHandler = new Handler() {                                          
		public void handleMessage(Message msg) {
			int value;
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				value = returnData[5] & 0xFF;
			} 
			catch(Exception e)          {value = -1;} 
			if (value != -1) workoutIntervalCount= value;                                            
		}
	};


	public int workoutState;
	boolean alreadyLoggedWorkout = true;
	final  Handler workoutStateHandler = new Handler() {                                          
		public void handleMessage(Message msg) {
			int value;
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				value = returnData[5] & 0xFF;
			} 
			catch(Exception e)          {value = -1;} 
			if (value != -1) workoutState= value;
		}
	};



	public int intervalType, lastIntervalType;
	final  Handler intervalTypeHandler = new Handler() {                                          
		public void handleMessage(Message msg) {
			int value;
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				value = returnData[5] & 0xFF;
			} 
			catch(Exception e)          {value = -1;} 
			if (value != -1) {
				intervalType= value;  
			}
		}
	};




	public double lastSplitTime;
	public int lastSplitDistance, splitAvgCalories, dragFactor, strokeState, workDistance, errorValue, totalWorkDistance, restDistance, totalRestDistance;
	public int unCaughtCommand;
	public double workTime, restTime, totalRestTime;
	public double stroke500MPace, splitAvg500MPace, totalAvg500MPace;
	public double splitTime;
	public int avgHeartRate, endingAvgHeartRate, restAvgHeartRate;
	public int strokeRate, splitAvgStrokeRate, totalAvgStrokeRate;
	public int strokePower, splitAvgPower, totalAvgPower;
	public int splitDistance, lastRestDistance;
	public int strokeCaloricBurnRate, splitAvgCaloricBurnRate, totalAvgCaloricBurnRate;
	int commandError;
	byte command;

	final  Handler GetPMDataCmdHandler = new Handler() {                                         
		public void handleMessage(Message msg) {
			try {
				Bundle bundle = msg.getData();
				byte[] returnData = bundle.getByteArray("Value");
				command = (byte)(returnData[3] & 0xFF);
				int data = getData_PMDataCmd(returnData, 5, returnData[4]);

				switch (command) {
				case GET_WORKTIME:
					workTime = ((double)data)/100.0;
					break;

				case GET_PROJECTED_WORKTIME:
					break;

				case GET_TOTAL_RESTTIME: 
					totalRestTime = ((double)data)/100.0;
					break;

				case GET_WORKDISTANCE:   
					workDistance = data;
					break;

				case GET_TOTAL_WORKDISTANCE:
					totalWorkDistance = data;
					break;

				case GET_PROJECTED_WORKDISTANCE:
					break;

				case GET_RESTDISTANCE:
					restDistance = data;
					break;

				case GET_TOTAL_RESTDISTANCE:
					totalRestDistance = data;
					break;

				case GET_STROKE_500M_PACE:  		
					stroke500MPace = ((double)data)/100.0;
					break;

				case GET_STROKE_POWER:
					strokePower = data;
					break;

				case GET_STROKE_CALORICBRUNRATE:
					strokeCaloricBurnRate = data;
					break;

				case GET_SPLIT_AVG_500M_PACE:	
					splitAvg500MPace = ((double)data)/100.0;
					break;

				case GET_SPLIT_AVG_POWER:
					splitAvgPower = data;
					break;

				case GET_SPLIT_AVG_CALORICBURNRATE:
					splitAvgCaloricBurnRate = data;
					break;

				case GET_SPLIT_AVG_CALORIES:
					splitAvgCalories = data;
					break;

				case GET_TOTAL_AVG_500MPACE:
					totalAvg500MPace = ((double)data)/100.0;
					break;

				case GET_TOTAL_AVG_POWER:	
					totalAvgPower = data;
					break;

				case GET_TOTAL_AVG_CALORICBURNRATE:		
					totalAvgCaloricBurnRate = data;
					break;

				case GET_TOTAL_AVG_CALORIES:
					splitAvgCalories = data;
					break;

				case GET_STROKE_RATE:
					strokeRate = data;
					break;

				case GET_SPLIT_AVG_STROKERATE:
					splitAvgStrokeRate = data;
					break;

				case GET_TOTAL_AVG_STROKERATE:	
					totalAvgStrokeRate = data;
					break;

				case GET_AVG_HEART_RATE:	
					avgHeartRate = data;
					break;

				case GET_ENDING_AVG_HEART_RATE:
					endingAvgHeartRate = data;
					break;

				case GET_REST_AVG_HEART_RATE:
					restAvgHeartRate = data;
					break;

				case GET_SPLITTIME:  	
					splitTime = ((double)data)/100;
					break;

				case GET_LAST_SPLITTIME:    
					lastSplitTime = ((double)data)/100;
					break;

				case GET_SPLITDISTANCE: 
					splitDistance = data;
					break;

				case GET_LAST_SPLITDISTANCE:     
					lastSplitDistance = data;
					break;

				case GET_LAST_RESTDISTANCE: 
					lastRestDistance = data;
					break;

				case GET_TARGETPACETIME:
					break;

				case GET_STROKESTATE:   
					strokeState = data;
					break;

				case GET_STROKERATESTATE: 
					break;

				case GET_DRAGFACTOR:  
					dragFactor = data;
					break;

				case GET_ENCODER_PERIOD: 
					break;

				case GET_HEARTRATESTATE:  
					break;

				case GET_SYNC_DATA:  
					break;

				case GET_SYNC_DATAALL: 
					break;

				case GET_RACE_DATA: 
					break;

				case GET_TICK_TIME: 
					break;

				case GET_ERRORTYPE:
					break;

				case GET_ERRORVALUE:
					errorValue = data;
					break;

				case GET_STATUSTYPE:
					break;

				case GET_STATUSVALUE:
					break;

				case GET_EPMSTATUS:
					break;

				case GET_DISPLAYUPDATETIME:
					break;

				case GET_SYNCFRACTIONALTIME:
					break;

				case GET_RESTTIME:
					restTime = ((double)data)/100;
					break;

				default:
					unCaughtCommand = command;
					Log.d(TAG, "" + unCaughtCommand);
				}			
			} 
			catch(Exception e) {commandError = command;} 
		}
	};

	public int getData_PMDataCmd(byte[] byteArray, int start, int length){
		int data = 0;
		for (int i=0; i<length; i++) {
			data = (data <<8) | (byteArray[start+i] & 0xFF);
		}
		return data;
	}



	////////////////////////////////////////////////////////////////////////////////


	public String formatTime(double seconds, int decimalPoints){
		int hour, min, sec, frac;
		String hourString, minString, secString, fracString;
		hour  = (int)(seconds / 3600);
		min = (int)((seconds % 3600) / 60 );
		sec = (int)(seconds % 60);
		frac = ((int)(seconds * Math.pow(10,decimalPoints)) % (int)Math.pow(10, decimalPoints));

		if (hour > 0) hourString = hour + ":";
		else hourString = "";
		if (hour >0) minString = pad(min);
		else if (min == 0) minString =  "";
		else minString = "" + min;
		secString = ":" + pad(sec);
		if (decimalPoints == 0) fracString = "";
		else fracString = "." + frac;
		String timeString = hourString + minString + secString + fracString;
		return timeString;

	}

	public synchronized int get2BytesInt(Message msg) {
		int value;
		try {
			Bundle bundle = msg.getData();
			byte[] returnData = bundle.getByteArray("Value");
			value = (returnData[3] & 0xFF) + (returnData[4] & 0xFF)*256;
		} 
		catch(Exception e)          {value = -1;} 
		return value;
	}

	public synchronized int get1BytesInt(Message msg) {
		int value;
		try {
			Bundle bundle = msg.getData();
			byte[] returnData = bundle.getByteArray("Value");
			value = returnData[3] & 0xFF;
		} 
		catch(Exception e)          {value = -1;} 
		return value;
	}

	protected String pad(int value) {
		Integer valueInt = new Integer(value);
		String valueString = valueInt.toString();
		if (valueString.length() == 1)
			valueString = "0" + valueString;
		return valueString;
	}

	public String getDate(int year, int month, int day, int hour, int minute) {
		return year + "-" + pad(month) + "-" + pad(day) + " " + pad(hour) + ":" + pad(minute);
	}

	public synchronized byte[] concatenate(byte[] A, byte[] B) {
		byte [] C= new  byte[A.length + B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
		return C;
	}

	public String toString(byte[] bytes) {	
		if (bytes == null) return null;
		String byteString = "";
		for (byte b : bytes) {
			byteString += String.format("%02X ", b);
		}
		return byteString;	
	}

	public byte[] byteToCommand(byte byteCmd) {
		return new byte[] {byteCmd};
	}

	public byte[][] commandToArray(byte[] cmd) {
		return new byte[][] {cmd};
	}



}

