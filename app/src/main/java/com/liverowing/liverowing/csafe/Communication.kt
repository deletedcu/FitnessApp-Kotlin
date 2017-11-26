package com.liverowing.liverowing.csafe

import android.util.Log
import com.liverowing.liverowing.model.pm.WorkoutType
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.experimental.xor

/**
 * Created by henrikmalmberg on 2017-11-05.
 */
class Communication {
    companion object {
        /* Frame contents */
        const val CSAFE_EXT_FRAME_START_BYTE = 0xF0
        const val CSAFE_FRAME_START_BYTE = 0xF1
        const val CSAFE_FRAME_END_BYTE = 0xF2
        const val CSAFE_FRAME_STUFF_BYTE = 0xF3

        const val CSAFE_FRAME_MAX_STUFF_OFFSET_BYTE = 0x03

        const val CSAFE_FRAME_FLG_LEN = 2
        const val CSAFE_EXT_FRAME_ADDR_LEN = 2
        const val CSAFE_FRAME_CHKSUM_LEN = 1

        const val CSAFE_SHORT_CMD_TYPE_MSK = 0x80
        const val CSAFE_LONG_CMD_HDR_LENGTH = 2
        const val CSAFE_LONG_CMD_BYTE_CNT_OFFSET = 1
        const val CSAFE_RSP_HDR_LENGTH = 2

        const val CSAFE_FRAME_STD_TYPE = 0
        const val CSAFE_FRAME_EXT_TYPE = 1

        const val CSAFE_DESTINATION_ADDR_HOST = 0x00
        const val CSAFE_DESTINATION_ADDR_ERG_MASTER = 0x01
        const val CSAFE_DESTINATION_ADDR_BROADCAST = 0xFF
        const val CSAFE_DESTINATION_ADDR_ERG_DEFAULT = 0xFD

        const val CSAFE_FRAME_MAXSIZE = 96
        const val CSAFE_INTERFRAMEGAP_MIN = 50 // msec
        const val CSAFE_CMDUPLIST_MAXSIZE = 10
        const val CSAFE_MEMORY_BLOCKSIZE = 64
        const val CSAFE_FORCEPLOT_BLOCKSIZE = 32
        const val CSAFE_HEARTBEAT_BLOCKSIZE = 32

        /* Manufacturer Info */
        const val CSAFE_MANUFACTURE_ID = 22 // assigned by Fitlinxx for Concept2
        const val CSAFE_CLASS_ID = 2 // standard CSAFE equipment
        const val CSAFE_MODEL_NUM_3 = 3 // PM3
        const val CSAFE_MODEL_NUM_4 = 4 // PM4
        const val CSAFE_MODEL_NUM_5 = 5 // PM5???

        const val CSAFE_UNITS_TYPE = 0 // Metric
        const val CSAFE_SERIALNUM_DIGITS = 9

        const val CSAFE_HMS_FORMAT_CNT = 3
        const val CSAFE_YMD_FORMAT_CNT = 3
        const val CSAFE_ERRORCODE_FORMAT_CNT = 3

        /* Command space partitioning for standard commands */
        const val CSAFE_CTRL_CMD_LONG_MIN = 0x01
        const val CSAFE_CFG_CMD_LONG_MIN = 0x10
        const val CSAFE_DATA_CMD_LONG_MIN = 0x20
        const val CSAFE_AUDIO_CMD_LONG_MIN = 0x40
        const val CSAFE_TEXTCFG_CMD_LONG_MIN = 0x60
        const val CSAFE_TEXTSTATUS_CMD_LONG_MIN = 0x65
        const val CSAFE_CAP_CMD_LONG_MIN = 0x70
        const val CSAFE_PMPROPRIETARY_CMD_LONG_MIN = 0x76

        const val CSAFE_CTRL_CMD_SHORT_MIN = 0x80
        const val CSAFE_STATUS_CMD_SHORT_MIN = 0x91
        const val CSAFE_DATA_CMD_SHORT_MIN = 0xA0
        const val CSAFE_AUDIO_CMD_SHORT_MIN = 0xC0
        const val CSAFE_TEXTCFG_CMD_SHORT_MIN = 0xE0
        const val CSAFE_TEXTSTATUS_CMD_SHORT_MIN = 0xE5

        /* Standard Short Control Commands */
        enum class CSAFE_SHORT_CTRL_CMDS(val value: Int) {
            CSAFE_GETSTATUS_CMD(CSAFE_CTRL_CMD_SHORT_MIN), // 0x80
            CSAFE_RESET_CMD(0x81),
            CSAFE_GOIDLE_CMD(0x82),
            CSAFE_GOHAVEID_CMD(0x83),
            CSAFE_GOINUSE_CMD(0x85),
            CSAFE_GOFINISHED_CMD(0x86),
            CSAFE_GOREADY_CMD(0x87),
            CSAFE_BADID_CMD(0x88),
            CSAFE_CTRL_CMD_SHORT_MAX(0x89)
        }

        /* Standard Short Status Commands */
        enum class CSAFE_SHORT_STATUS_CMDS(val value: Int) {
            CSAFE_GETVERSION_CMD(CSAFE_STATUS_CMD_SHORT_MIN), // 0x91
            CSAFE_GETID_CMD(0x92),
            CSAFE_GETUNITS_CMD(0x93),
            CSAFE_GETSERIAL_CMD(0x94),
            CSAFE_GETLIST_CMD(0x98),
            CSAFE_GETUTILIZATION_CMD(0x99),
            CSAFE_GETMOTORCURRENT_CMD(0x9A),
            CSAFE_GETODOMETER_CMD(0x9B),
            CSAFE_GETERRORCODE_CMD(0x9C),
            CSAFE_GETSERVICECODE_CMD(0x9D),
            CSAFE_GETUSERCFG1_CMD(0x9E),
            CSAFE_GETUSERCFG2_CMD(0x9F),
            CSAFE_STATUS_CMD_SHORT_MAX(0xA0)
        }

        /* Standard Short Data Commands */
        enum class CSAFE_SHORT_DATA_CMDS(val value: Int) {
            CSAFE_GETTWORK_CMD(CSAFE_DATA_CMD_SHORT_MIN), // 0xA0
            CSAFE_GETHORIZONTAL_CMD(0xA1),
            CSAFE_GETVERTICAL_CMD(0xA2),
            CSAFE_GETCALORIES_CMD(0xA3),
            CSAFE_GETPROGRAM_CMD(0xA4),
            CSAFE_GETSPEED_CMD(0xA5),
            CSAFE_GETPACE_CMD(0xA6),
            CSAFE_GETCADENCE_CMD(0xA7),
            CSAFE_GETGRADE_CMD(0xA8),
            CSAFE_GETGEAR_CMD(0xA9),
            CSAFE_GETUPLIST_CMD(0xAA),
            CSAFE_GETUSERINFO_CMD(0xAB),
            CSAFE_GETTORQUE_CMD(0xAC),
            CSAFE_GETHRCUR_CMD(0xB0),
            CSAFE_GETHRTZONE_CMD(0xB2),
            CSAFE_GETMETS_CMD(0xB3),
            CSAFE_GETPOWER_CMD(0xB4),
            CSAFE_GETHRAVG_CMD(0xB5),
            CSAFE_GETHRMAX_CMD(0xB6),
            CSAFE_GETUSERDATA1_CMD(0xBE),
            CSAFE_GETUSERDATA2_CMD(0xBF),
            CSAFE_DATA_CMD_SHORT_MAX(0xC0)
        }

        /* Standard Short Audio Commands */
        enum class CSAFE_SHORT_AUDIO_CMDS(val value: Int) {
            CSAFE_GETAUDIOCHANNEL_CMD(CSAFE_AUDIO_CMD_SHORT_MIN), // 0xC0
            CSAFE_GETAUDIOVOLUME_CMD(0xC1),
            CSAFE_GETAUDIOMUTE_CMD(0xC2),
            CSAFE_AUDIO_CMD_SHORT_MAX(0xC3)
        }

        /* Standard Short Text Configuration Commands */
        enum class CSAFE_SHORT_TEXTCFG_CMDS(val value: Int) {
            CSAFE_ENDTEXT_CMD(CSAFE_TEXTCFG_CMD_SHORT_MIN), // 0xE0
            CSAFE_DISPLAYPOPUP_CMD(0xE1),
            CSAFE_TEXTCFG_CMD_SHORT_MAX(0xE2)
        }

        /* Standard Short Text Status Commands */
        enum class CSAFE_SHORT_TEXTSTATUS_CMDS(val value: Int) {
            CSAFE_GETPOPUPSTATUS_CMD(CSAFE_TEXTSTATUS_CMD_SHORT_MIN), // 0xE5
            CSAFE_TEXTSTATUS_CMD_SHORT_MAX(0xE6)
        }

        /* Standard Long Control Commands */
        enum class CSAFE_LONG_CTRL_CMDS(val value: Int) {
            CSAFE_AUTOUPLOAD_CMD(CSAFE_CTRL_CMD_LONG_MIN),         // 0x01
            CSAFE_UPLIST_CMD(0x02),
            CSAFE_UPSTATUSSEC_CMD(0x04),
            CSAFE_UPLISTSEC_CMD(0x05),
            CSAFE_CTRL_CMD_LONG_MAX(0x06)
        }

        /* Standard Long Configuration Commands */
        enum class CSAFE_LONG_CFG_CMDS(val value: Int) {
            CSAFE_IDDIGITS_CMD(CSAFE_CFG_CMD_LONG_MIN), // 0x10
            CSAFE_SETTIME_CMD(0x11),
            CSAFE_SETDATE_CMD(0x12),
            CSAFE_SETTIMEOUT_CMD(0x13),
            CSAFE_SETUSERCFG1_CMD(0x1A),
            CSAFE_SETUSERCFG2_CMD(0x1B),
            CSAFE_CFG_CMD_LONG_MAX(0x1C)
        }

        /* Standard Long Data Commands */
        enum class CSAFE_LONG_DATA_CMDS(val value: Int) {
            CSAFE_SETTWORK_CMD(CSAFE_DATA_CMD_LONG_MIN), // 0x20
            CSAFE_SETHORIZONTAL_CMD(0x21),
            CSAFE_SETVERTICAL_CMD(0x22),
            CSAFE_SETCALORIES_CMD(0x23),
            CSAFE_SETPROGRAM_CMD(0x24),
            CSAFE_SETSPEED_CMD(0x25),
            CSAFE_SETGRADE_CMD(0x28),
            CSAFE_SETGEAR_CMD(0x29),
            CSAFE_SETUSERINFO_CMD(0x2B),
            CSAFE_SETTORQUE_CMD(0x2C),
            CSAFE_SETLEVEL_CMD(0x2D),
            CSAFE_SETTARGETHR_CMD(0x30),
            CSAFE_SETGOAL_CMD(0x32),
            CSAFE_SETMETS_CMD(0x33),
            CSAFE_SETPOWER_CMD(0x34),
            CSAFE_SETHRZONE_CMD(0x35),
            CSAFE_SETHRMAX_CMD(0x36),
            CSAFE_DATA_CMD_LONG_MAX(0x37)
        }

        /* Standard Long Audio Commands */
        enum class CSAFE_LONG_AUDIO_CMDS(val value: Int) {
            CSAFE_SETCHANNELRANGE_CMD(CSAFE_AUDIO_CMD_LONG_MIN), // 0x40
            CSAFE_SETVOLUMERANGE_CMD(0x41),
            CSAFE_SETAUDIOMUTE_CMD(0x42),
            CSAFE_SETAUDIOCHANNEL_CMD(0x43),
            CSAFE_SETAUDIOVOLUME_CMD(0x44),
            CSAFE_AUDIO_CMD_LONG_MAX(0x45)
        }

        /* Standard Long Text Configuration Commands */
        enum class CSAFE_LONG_TEXTCFG_CMDS(val value: Int) {
            CSAFE_STARTTEXT_CMD(CSAFE_TEXTCFG_CMD_LONG_MIN), // 0x60
            CSAFE_APPENDTEXT_CMD(0x61),
            CSAFE_TEXTCFG_CMD_LONG_MAX(0x62)
        }

        /* Standard Long Text Status Commands */
        enum class CSAFE_LONG_TEXTSTATUS_CMDS(val value: Int) {
            CSAFE_GETTEXTSTATUS_CMD(CSAFE_TEXTSTATUS_CMD_LONG_MIN), // 0x65
            CSAFE_TEXTSTATUS_CMD_LONG_MAX(0x66)
        }

        /* Standard Long Capabilities Commands */
        enum class CSAFE_LONG_CAP_CMDS(val value: Int) {
            CSAFE_GETCAPS_CMD(CSAFE_CAP_CMD_LONG_MIN), // 0x70
            CSAFE_GETUSERCAPS1_CMD(0x7E),
            CSAFE_GETUSERCAPS2_CMD(0x7F),
            CSAFE_CAP_CMD_LONG_MAX(0x80)
        }

        /*
        The currently defined CSAFE command space is augmented by adding 4 command
        wrappers to allow pushing and pulling of configuration/data from the
        host to the PM

        CSAFE_SETPMCFG_CMD    Push configuration from host to PM
        CSAFE_SETPMDATA_CMD   Push data from host to PM
        CSAFE_GETPMCFG_CMD    Pull configuration to host from PM
        CSAFE_GETPMDATA_CMD   PUll data to host from PM

        Note: These commands have been added for Concept 2 and do not comply
              with the existing CSAFE command set
        */
        enum class CSAFE_LONG_PMPROPRIETARY_CMDS(val value: Int) {
            CSAFE_SETPMCFG_CMD(CSAFE_PMPROPRIETARY_CMD_LONG_MIN), // 0x76
            CSAFE_SETPMDATA_CMD(0x77),
            CSAFE_GETPMCFG_CMD(0x7E),
            CSAFE_GETPMDATA_CMD(0x7F),
            CSAFE_PMPROPRIETARY_CMD_LONG_MAX(0x80)
        }

        /* Command space partitioning for PM proprietary commands */
        const val CSAFE_GETPMCFG_CMD_SHORT_MIN = 0x80
        const val CSAFE_GETPMCFG_CMD_LONG_MIN = 0x50
        const val CSAFE_SETPMCFG_CMD_SHORT_MIN = 0xE0
        const val CSAFE_SETPMCFG_CMD_LONG_MIN = 0x00
        const val CSAFE_GETPMDATA_CMD_SHORT_MIN = 0xA0
        const val CSAFE_GETPMDATA_CMD_LONG_MIN = 0x68
        const val CSAFE_SETPMDATA_CMD_SHORT_MIN = 0xD0
        const val CSAFE_SETPMDATA_CMD_LONG_MIN = 0x30

        // Custom Short PULL Configuration Commands for PM
        enum class CSAFE_PM_SHORT_PULL_CFG_CMDS(val value: Int) {
            CSAFE_PM_GET_FW_VERSION(CSAFE_GETPMCFG_CMD_SHORT_MIN), // 0x80
            CSAFE_PM_GET_HW_VERSION(0x81),
            CSAFE_PM_GET_HW_ADDRESS(0x82),
            CSAFE_PM_GET_TICK_TIMEBASE(0x83),
            CSAFE_PM_GET_HRM(0x84),
            CSAFE_PM_GET_DATETIME(0x85),
            CSAFE_PM_GET_SCREENSTATESTATUS(0x86),
            CSAFE_PM_GET_RACE_LANE_REQUEST(0x87),
            CSAFE_PM_GET_ERG_LOGICALADDR_REQUEST(0x88),
            CSAFE_PM_GET_WORKOUTTYPE(0x89),
            CSAFE_PM_GET_DISPLAYTYPE(0x8A),
            CSAFE_PM_GET_DISPLAYUNITS(0x8B),
            CSAFE_PM_GET_LANGUAGETYPE(0x8C),
            CSAFE_PM_GET_WORKOUTSTATE(0x8D),
            CSAFE_PM_GET_INTERVALTYPE(0x8E),
            CSAFE_PM_GET_OPERATIONALSTATE(0x8F),
            CSAFE_PM_GET_LOGCARDSTATE(0x90),
            CSAFE_PM_GET_LOGCARDSTATUS(0x91),
            CSAFE_PM_GET_POWERUPSTATE(0x92),
            CSAFE_PM_GET_ROWINGSTATE(0x93),
            CSAFE_PM_GET_SCREENCONTENT_VERSION(0x94),
            CSAFE_PM_GET_COMMUNICATIONSTATE(0x95),
            CSAFE_PM_GET_RACEPARTICIPANTCOUNT(0x96),
            CSAFE_PM_GET_BATTERYLEVELPERCENT(0x97),
            CSAFE_PM_GET_RACEMODESTATUS(0x98),
            CSAFE_PM_GET_INTERNALLOGPARAMS(0x99),
            CSAFE_PM_GET_PRODUCTCONFIGURATION(0x9A),
            CSAFE_PM_GET_ERGSLAVEDISCOVERREQUESTSTATUS(0x9B),
            CSAFE_PM_GET_WIFICONFIG(0x9C),
            CSAFE_PM_GET_CPUTICKRATE(0x9D),
            CSAFE_PM_GET_LOGCARDCENSUS(0x9E),
            CSAFE_PM_GET_WORKOUTINTERVALCOUNT(0x9F),
            CSAFE_GETPMCFG_CMD_SHORT_MAX(0xA0)
        }

        // Custom Short PULL Data Commands for PM
        enum class CSAFE_PM_SHORT_PULL_DATA_CMDS(val value: Int) {
            CSAFE_PM_GET_WORKTIME(CSAFE_GETPMDATA_CMD_SHORT_MIN), // 0xA0
            CSAFE_PM_GET_PROJECTED_WORKTIME(0xA1),
            CSAFE_PM_GET_TOTAL_RESTTIME(0xA2),
            CSAFE_PM_GET_WORKDISTANCE(0xA3),
            CSAFE_PM_GET_TOTAL_WORKDISTANCE(0xA4),
            CSAFE_PM_GET_PROJECTED_WORKDISTANCE(0xA5),
            CSAFE_PM_GET_RESTDISTANCE(0xA6),
            CSAFE_PM_GET_TOTAL_RESTDISTANCE(0xA7),
            CSAFE_PM_GET_STROKE_500MPACE(0xA8),
            CSAFE_PM_GET_STROKE_POWER(0xA9),
            CSAFE_PM_GET_STROKE_CALORICBURNRATE(0xAA),
            CSAFE_PM_GET_SPLIT_AVG_500MPACE(0xAB),
            CSAFE_PM_GET_SPLIT_AVG_POWER(0xAC),
            CSAFE_PM_GET_SPLIT_AVG_CALORICBURNRATE(0xAD),
            CSAFE_PM_GET_SPLIT_AVG_CALORIES(0xAE),
            CSAFE_PM_GET_TOTAL_AVG_500MPACE(0xAF),
            CSAFE_PM_GET_TOTAL_AVG_POWER(0xB0),
            CSAFE_PM_GET_TOTAL_AVG_CALORICBURNRATE(0xB1),
            CSAFE_PM_GET_TOTAL_AVG_CALORIES(0xB2),
            CSAFE_PM_GET_STROKERATE(0xB3),
            CSAFE_PM_GET_SPLIT_AVG_STROKERATE(0xB4),
            CSAFE_PM_GET_TOTAL_AVG_STROKERATE(0xB5),
            CSAFE_PM_GET_AVG_HEARTRATE(0xB6),
            CSAFE_PM_GET_ENDING_AVG_HEARTRATE(0xB7),
            CSAFE_PM_GET_REST_AVG_HEARTRATE(0xB8),
            CSAFE_PM_GET_SPLITTIME(0xB9),
            CSAFE_PM_GET_LASTSPLITTIME(0xBA),
            CSAFE_PM_GET_SPLITDISTANCE(0xBB),
            CSAFE_PM_GET_LASTSPLITDISTANCE(0xBC),
            CSAFE_PM_GET_LASTRESTDISTANCE(0xBD),
            CSAFE_PM_GET_TARGETPACETIME(0xBE),
            CSAFE_PM_GET_STROKESTATE(0xBF),
            CSAFE_PM_GET_STROKERATESTATE(0xC0),
            CSAFE_PM_GET_DRAGFACTOR(0xC1),
            CSAFE_PM_GET_ENCODERPERIOD(0xC2),
            CSAFE_PM_GET_HEARTRATESTATE(0xC3),
            CSAFE_PM_GET_SYNCDATA(0xC4),
            CSAFE_PM_GET_SYNCDATAALL(0xC5),
            CSAFE_PM_GET_RACEDATA(0xC6),
            CSAFE_PM_GET_TICKTIME(0xC7),
            CSAFE_PM_GET_ERRORTYPE(0xC8),
            CSAFE_PM_GET_ERRORVALUE(0xC9),
            CSAFE_PM_GET_STATUSTYPE(0xCA),
            CSAFE_PM_GET_STATUSVALUE(0xCB),
            CSAFE_PM_GET_EPMSTATUS(0xCC),
            CSAFE_PM_GET_DISPLAYUPDATETIME(0xCD),
            CSAFE_PM_GET_SYNCFRACTIONALTIME(0xCE),
            CSAFE_PM_GET_RESTTIME(0xCF),
            CSAFE_GETPMDATA_CMD_SHORT_MAX(0xD0)
        }

        // Custom Short PUSH Data Commands for PM
        enum class CSAFE_PM_SHORT_PUSH_DATA_CMDS(val value: Int) {
            CSAFE_PM_SET_SYNC_DISTANCE(CSAFE_SETPMDATA_CMD_SHORT_MIN), // 0xD0
            CSAFE_PM_SET_SYNC_STROKEPACE(0xD1),
            CSAFE_PM_SET_SYNC_AVG_HEARTRATE(0xD2),
            CSAFE_PM_SET_SYNC_TIME(0xD3),
            CSAFE_PM_SET_SYNC_SPLIT_DATA(0xD4),
            CSAFE_PM_SET_SYNC_ENCODER_PERIOD(0xD5),
            CSAFE_PM_SET_SYNC_VERSION_INFO(0xD6),
            CSAFE_PM_SET_SYNC_RACETICKTIME(0xD7),
            CSAFE_PM_SET_SYNC_DATAALL(0xD8),
            // Unused,                                                      // 0xD9
            // Unused,                                                      // 0xDA
            // Unused,                                                      // 0xDB
            // Unused,                                                      // 0xDC
            // Unused,                                                      // 0xDD
            // Unused,                                                      // 0xDE
            // Unused,                                                      // 0xDF
            CSAFE_SETPMDATA_CMD_SHORT_MAX(0xE0)
        }

        // Custom Short PUSH Configuration Commands for PM
        enum class CSAFE_PM_SHORT_PUSH_CFG_CMDS(val value: Int) {
            CSAFE_PM_SET_RESET_ALL(CSAFE_SETPMCFG_CMD_SHORT_MIN), // 0xE0
            CSAFE_PM_SET_RESET_ERGNUMBER(0xE1),
            // Unused,                                                      // 0xE2
            // Unused,                                                      // 0xE3
            // Unused,                                                      // 0xE4
            // Unused,                                                      // 0xE5
            // Unused,                                                      // 0xE6
            // Unused,                                                      // 0xE7
            CSAFE_PM_GET_WORKOUTDURATION(0xE8),
            CSAFE_PM_GET_WORKOTHER(0xE9),
            // Unused,                                                      // 0xEA
            // Unused,                                                      // 0xEB
            // Unused,                                                      // 0xEC
            // Unused,                                                      // 0xED
            // Unused,                                                      // 0xEE
            // Unused,                                                      // 0xEF
            CSAFE_SETPMCFG_CMD_SHORT_MAX(0xF0)
        }

        // Custom Long PUSH Configuration Commands for PM
        enum class CSAFE_PM_LONG_PUSH_CFG_CMDS(val value: Int) {
            CSAFE_PM_SET_BAUDRATE(CSAFE_SETPMCFG_CMD_LONG_MIN), // 0x00
            CSAFE_PM_SET_WORKOUTTYPE(0x01),
            CSAFE_PM_SET_STARTTYPE(0x02),
            CSAFE_PM_SET_WORKOUTDURATION(0x03),
            CSAFE_PM_SET_RESTDURATION(0x04),
            CSAFE_PM_SET_SPLITDURATION(0x05),
            CSAFE_PM_SET_TARGETPACETIME(0x06),
            CSAFE_PM_SET_INTERVALIDENTIFIER(0x07),
            CSAFE_PM_SET_OPERATIONALSTATE(0x08),
            CSAFE_PM_SET_RACETYPE(0x09),
            CSAFE_PM_SET_WARMUPDURATION(0x0A),
            CSAFE_PM_SET_RACELANESETUP(0x0B),
            CSAFE_PM_SET_RACELANEVERIFY(0x0C),
            CSAFE_PM_SET_RACESTARTPARAMS(0x0D),
            CSAFE_PM_SET_ERGSLAVEDISCOVERYREQUEST(0x0E),
            CSAFE_PM_SET_BOATNUMBER(0x0F),
            CSAFE_PM_SET_ERGNUMBER(0x10),
            CSAFE_PM_SET_COMMUNICATIONSTATE(0x11),
            CSAFE_PM_SET_CMDUPLIST(0x12),
            CSAFE_PM_SET_SCREENSTATE(0x13),
            CSAFE_PM_CONFIGURE_WORKOUT(0x14),
            CSAFE_PM_SET_TARGETAVGWATTS(0x15),
            CSAFE_PM_SET_TARGETCALSPERHR(0x16),
            CSAFE_PM_SET_INTERVALTYPE(0x17),
            CSAFE_PM_SET_WORKOUTINTERVALCOUNT(0x18),
            CSAFE_PM_SET_DISPLAYUPDATERATE(0x19),
            CSAFE_PM_SET_AUTHENPASSWORD(0x1A),
            CSAFE_PM_SET_TICKTIME(0x1B),
            CSAFE_PM_SET_TICKTIMEOFFSET(0x1C),
            CSAFE_PM_SET_RACEDATASAMPLETICKS(0x1D),
            CSAFE_PM_SET_RACEOPERATIONTYPE(0x1E),
            CSAFE_PM_SET_RACESTATUSDISPLAYTICKS(0x1F),
            CSAFE_PM_SET_RACESTATUSWARNINGTICKS(0x20),
            CSAFE_PM_SET_RACEIDLEMODEPARAMS(0x21),
            CSAFE_PM_SET_DATETIME(0x22),
            CSAFE_PM_SET_LANGUAGETYPE(0x23),
            CSAFE_PM_SET_WIFICONFIG(0x24),
            CSAFE_PM_SET_CPUTICKRATE(0x25),
            CSAFE_PM_SET_LOGCARDUSER(0x26),
            CSAFE_PM_SET_SCREENERRORMODE(0x27),
            CSAFE_PM_SET_CABLETEST(0x28),
            CSAFE_PM_SET_USER_ID(0x29),
            CSAFE_PM_SET_USER_PROFILE(0x2A),
            CSAFE_PM_SET_HRM(0x2B),
            CSAFE_PM_SET_RACESTARTINGPYHSICALADDRESS(0x2C),
            CSAFE_PM_SET_HRBELT_INFO(0x2D),
            // Unused,                                                      // 0x2E
            CSAFE_PM_SET_SENSOR_CHANNEL(0x2F), // sensor channel
            CSAFE_SETPMCFG_CMD_LONG_MAX(0x30)
        }

        // Custom Long PUSH Data Commands for PM
        enum class CSAFE_PM_LONG_PUSH_DATA_CMDS(val value: Int) {
            CSAFE_PM_SET_TEAM_DISTANCE(CSAFE_SETPMDATA_CMD_LONG_MIN), // 0x30
            CSAFE_PM_SET_TEAM_FINISH_TIME(0x31),
            CSAFE_PM_SET_RACEPARTICIPANT(0x32),
            CSAFE_PM_SET_RACESTATUS(0x33),
            CSAFE_PM_SET_LOGCARDMEMORY(0x34),
            CSAFE_PM_SET_DISPLAYSTRING(0x35),
            CSAFE_PM_SET_DISPLAYBITMAP(0x36),
            CSAFE_PM_SET_LOCALRACEPARTICIPANT(0x37),
            // Unused,                                                      // 0x38
            CSAFE_PM_SET_EXTENDED_HRM(0x39),                                // 0x39
            // Unused,                                                      // 0x3A
            // Unused,                                                      // 0x3B
            // Unused,                                                      // 0x3C
            // Unused,                                                      // 0x3D
            // Unused,                                                      // 0x3E
            // Unused,                                                      // 0x3F
            // Unused,                                                      // 0x40
            // Unused,                                                      // 0x41
            // Unused,                                                      // 0x42
            // Unused,                                                      // 0x43
            // Unused,                                                      // 0x44
            // Unused,                                                      // 0x45
            // Unused,                                                      // 0x46
            // Unused,                                                      // 0x47
            // Unused,                                                      // 0x48
            // Unused,                                                      // 0x49
            // Unused,                                                      // 0x4A
            // Unused,                                                      // 0x4B
            // Unused,                                                      // 0x4C
            // Unused,                                                      // 0x4D
            CSAFE_PM_SET_ANTRFMODE(0x4E),                                   // mfg support only
            CSAFE_PM_SET_MEMORY(0x4F),                                      // debug only
            CSAFE_SETPMDATA_CMD_LONG_MAX(0x50)
        }

        // Custom Long PULL Configuration Commands for PM
        enum class CSAFE_PM_LONG_PULL_CFG_CMDS(val value: Int) {
            CSAFE_PM_GET_ERGNUMBER(CSAFE_GETPMCFG_CMD_LONG_MIN),           // 0x50
            CSAFE_PM_GET_ERGNUMBERREQUEST(0x51),
            CSAFE_PM_GET_USERIDSTRING(0x52),
            CSAFE_PM_GET_LOCALRACEPARTICIPANT(0x53),
            CSAFE_PM_GET_USER_ID(0x54),
            CSAFE_PM_GET_USER_PROFILE(0x55),
            CSAFE_PM_GET_HRBELT_INFO(0x56),
            CSAFE_PM_GET_EXTENDED_HBELT_INFO(0x57),
            // Unused,                                                      // 0x58
            // Unused,                                                      // 0x59
            // Unused,                                                      // 0x5A
            // Unused,                                                      // 0x5B
            // Unused,                                                      // 0x5C
            // Unused,                                                      // 0x5D
            // Unused,                                                      // 0x5E
            // Unused,                                                      // 0x5F
            // Unused,                                                      // 0x60
            // Unused,                                                      // 0x61
            // Unused,                                                      // 0x62
            // Unused,                                                      // 0x63
            // Unused,                                                      // 0x64
            // Unused,                                                      // 0x65
            // Unused,                                                      // 0x66
            // Unused,                                                      // 0x67
            CSAFE_GETPMCFG_CMD_LONG_MAX(0x68)
        }

        // Custom Long PULL Data Commands for PM
        enum class CSAFE_PM_LONG_PULL_DATA_CMDS(val value: Int) {
            CSAFE_PM_GET_MEMORY(CSAFE_GETPMDATA_CMD_LONG_MIN),             // 0x68
            CSAFE_PM_GET_LOGCARDMEMORY(0x69),
            CSAFE_PM_GET_INTERNALLOGMEMORY(0x6A),
            CSAFE_PM_GET_FORCEPLOTDATA(0x6B),
            CSAFE_PM_GET_HEARTBEATDATA(0x6C),
            CSAFE_PM_GET_UI_EVENTS(0x6D),
            CSAFE_PM_GET_STROKESTATS(0x6E),
            // Unused,                                                      // 0x6F
            // Unused,                                                      // 0x70
            // Unused,                                                      // 0x71
            // Unused,                                                      // 0x72
            // Unused,                                                      // 0x73
            // Unused,                                                      // 0x74
            // Unused,                                                      // 0x75
            // Command Wrapper,                                             // 0x76
            // Command Wrapper,                                             // 0x77
            // Unused,                                                      // 0x78
            // Unused,                                                      // 0x79
            // Unused,                                                      // 0x7A
            // Unused,                                                      // 0x7B
            // Unused,                                                      // 0x7C
            // Unused,                                                      // 0x7D
            // Command Wrapper,                                             // 0x7E
            // Command Wrapper,                                             // 0x7F
            CSAFE_GETPMDATA_CMD_LONG_MAX(0x80)
        }

        /* Status byte flag and mask definitions */
        const val CSAFE_PREVOK_FLG = 0x00
        const val CSAFE_PREVREJECT_FLG = 0x10
        const val CSAFE_PREVBAD_FLG = 0x20
        const val CSAFE_PREVNOTRDY_FLG = 0x30
        const val CSAFE_PREVFRAMESTATUS_MSK = 0x30

        const val CSAFE_SLAVESTATE_ERR_FLG = 0x00
        const val CSAFE_SLAVESTATE_RDY_FLG = 0x01
        const val CSAFE_SLAVESTATE_IDLE_FLG = 0x02
        const val CSAFE_SLAVESTATE_HAVEID_FLG = 0x03
        const val CSAFE_SLAVESTATE_INUSE_FLG = 0x05
        const val CSAFE_SLAVESTATE_PAUSE_FLG = 0x06
        const val CSAFE_SLAVESTATE_FINISH_FLG = 0x07
        const val CSAFE_SLAVESTATE_MANUAL_FLG = 0x08
        const val CSAFE_SLAVESTATE_OFFLINE_FLG = 0x09

        const val CSAFE_FRAMECNT_FLG = 0x80

        const val CSAFE_SLAVESTATE_MSK = 0x0F

        /* CSAFE_AUTOUPLOAD_CMD flag definitions */
        const val CSAFE_AUTOSTATUS_FLG = 0x01
        const val CSAFE_UPSTATUS_FLG = 0x02
        const val CSAFE_UPLIST_FLG = 0x04
        const val CSAFE_ACK_FLG = 0x10
        const val CSAFE_EXTERNCONTROL_FLG = 0x40

        /* CSAFE Slave Capabilities Codes */
        const val CSAFE_CAPCODE_PROTOCOL = 0x00
        const val CSAFE_CAPCODE_POWER = 0x01
        const val CSAFE_CAPCODE_TEXT = 0x02

        /* CSAFE units format definitions: CSAFE_<type>_<unit>_<tens>_<decimals> */
        const val CSAFE_DISTANCE_MILE_0_0 = 0x01
        const val CSAFE_DISTANCE_MILE_0_1 = 0x02
        const val CSAFE_DISTANCE_MILE_0_2 = 0x03
        const val CSAFE_DISTANCE_MILE_0_3 = 0x04
        const val CSAFE_DISTANCE_FEET_0_0 = 0x05
        const val CSAFE_DISTANCE_INCH_0_0 = 0x06
        const val CSAFE_WEIGHT_LBS_0_0 = 0x07
        const val CSAFE_WEIGHT_LBS_0_1 = 0x08
        const val CSAFE_DISTANCE_FEET_1_0 = 0x0A
        const val CSAFE_SPEED_MILEPERHOUR_0_0 = 0x10
        const val CSAFE_SPEED_MILEPERHOUR_0_1 = 0x11
        const val CSAFE_SPEED_MILEPERHOUR_0_2 = 0x12
        const val CSAFE_SPEED_FEETPERMINUTE_0_0 = 0x13
        const val CSAFE_DISTANCE_KM_0_0 = 0x21
        const val CSAFE_DISTANCE_KM_0_1 = 0x22
        const val CSAFE_DISTANCE_KM_0_2 = 0x23
        const val CSAFE_DISTANCE_METER_0_0 = 0x24
        const val CSAFE_DISTANCE_METER_0_1 = 0x25
        const val CSAFE_DISTANCE_CM_0_0 = 0x26
        const val CSAFE_WEIGHT_KG_0_0 = 0x27
        const val CSAFE_WEIGHT_KG_0_1 = 0x28
        const val CSAFE_SPEED_KMPERHOUR_0_0 = 0x30
        const val CSAFE_SPEED_KMPERHOUR_0_1 = 0x31
        const val CSAFE_SPEED_KMPERHOUR_0_2 = 0x32
        const val CSAFE_SPEED_METERPERMINUTE_0_0 = 0x33
        const val CSAFE_PACE_MINUTEPERMILE_0_0 = 0x37
        const val CSAFE_PACE_MINUTEPERKM_0_0 = 0x38
        const val CSAFE_PACE_SECONDSPERKM_0_0 = 0x39
        const val CSAFE_PACE_SECONDSPERMILE_0_0 = 0x3A
        const val CSAFE_DISTANCE_FLOORS_0_0 = 0x41
        const val CSAFE_DISTANCE_FLOORS_0_1 = 0x42
        const val CSAFE_DISTANCE_STEPS_0_0 = 0x43
        const val CSAFE_DISTANCE_REVS_0_0 = 0x44
        const val CSAFE_DISTANCE_STRIDES_0_0 = 0x45
        const val CSAFE_DISTANCE_STROKES_0_0 = 0x46
        const val CSAFE_MISC_BEATS_0_0 = 0x47
        const val CSAFE_ENERGY_CALORIES_0_0 = 0x48
        const val CSAFE_GRADE_PERCENT_0_0 = 0x4A
        const val CSAFE_GRADE_PERCENT_0_2 = 0x4B
        const val CSAFE_GRADE_PERCENT_0_1 = 0x4C
        const val CSAFE_CADENCE_FLOORSPERMINUTE_0_1 = 0x4F
        const val CSAFE_CADENCE_FLOORSPERMINUTE_0_0 = 0x50
        const val CSAFE_CADENCE_STEPSPERMINUTE_0_0 = 0x51
        const val CSAFE_CADENCE_REVSPERMINUTE_0_0 = 0x52
        const val CSAFE_CADENCE_STRIDESPERMINUTE_0_0 = 0x53
        const val CSAFE_CADENCE_STROKESPERMINUTE_0_0 = 0x54
        const val CSAFE_MISC_BEATSPERMINUTE_0_0 = 0x55
        const val CSAFE_BURN_CALORIESPERMINUTE_0_0 = 0x56
        const val CSAFE_BURN_CALORIESPERHOUR_0_0 = 0x57
        const val CSAFE_POWER_WATTS_0_0 = 0x58
        const val CSAFE_ENERGY_INCHLB_0_0 = 0x5A
        const val CSAFE_ENERGY_FOOTLB_0_0 = 0x5B
        const val CSAFE_ENERGY_NM_0_0 = 0x5C

        /* Conversion constants */
        const val CSAFE_KG_TO_LBS = (2.2046)
        const val CSAFE_LBS_TO_KG = (1.0 / CSAFE_KG_TO_LBS)

        /* ID Digits */
        const val CSAFE_IDDIGITS_MIN = 2
        const val CSAFE_IDDIGITS_MAX = 5
        const val CSAFE_DEFAULT_IDDIGITS = 5
        const val CSAFE_DEFAULT_ID = 0
        const val CSAFE_MANUAL_ID = 999999999

        /* Slave State Tiimeout Parameters */
        const val CSAFE_DEFAULT_SLAVESTATE_TIMEOUT = 20        // seconds
        const val CSAFE_PAUSED_SLAVESTATE_TIMEOUT = 220        // seconds
        const val CSAFE_INUSE_SLAVESTATE_TIMEOUT = 6        // seconds
        const val CSAFE_IDLE_SLAVESTATE_TIMEOUT = 30        // seconds

        /* Base Year */
        const val CSAFE_BASE_YEAR = 1900

        /* Default time intervals */
        const val CSAFE_DEFAULT_STATUSUPDATE_INTERVAL = 256        // seconds
        const val CSAFE_DEFAULT_CMDUPLIST_INTERVAL = 256        // seconds


        const val CSAFE_EXTENDED_START_FLAG = 0xF0
        const val CSAFE_STANDARD_START_FLAG = 0xF1
        const val CSAFE_STOP_FLAG = 0xF2
        const val CSAFE_STUFF_FLAG = 0xF3

        const val CSAFE_SETUSERCFG1_CMD = 0x1A
        const val CSAFE_SETHORIZONTAL_CMD = 0x31

        const val CSAFE_PM_SET_WORKOUTTYPE = 0x01

        const val CSAFE_PM_SET_SCREENSTATE = 0x13
        const val CSAFE_PM_CONFIGURE_WORKOUT = 0x14

        const val SCREENTYPE_WORKOUT = 1

        const val SCREENVALUEWORKOUT_PREPARETOROWWORKOUT = 1

        const val CTRL_CMD_SHORT_MIN = 128

        const val PM_TIME_TYPE = 0
        const val PM_DISTANCE_TYPE = 128
    }

    fun justRow(): ByteArray {
        val commands = listOf(
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_SET_WORKOUTTYPE, listOf(WorkoutType.JUSTROW_SPLITS.value)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_CONFIGURE_WORKOUT, listOf(1)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_SET_SCREENSTATE, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT))
        )

        return wrap(commands)
    }

    fun fixedWorkout(workoutType: WorkoutType, workoutDuration: Int, splitDuration: Int, targetPaceTime: Int): ByteArray {
        var type: Int

        when (workoutType) {
            WorkoutType.FIXEDDIST_NOSPLITS,
            WorkoutType.FIXEDDIST_SPLITS -> {
                type = PM_DISTANCE_TYPE
                if ((workoutDuration < 100) || (workoutDuration > 50000)) {
                    throw Exception("Workout duration not in range 100..50000")
                }
            }

            WorkoutType.FIXEDTIME_NOSPLITS,
            WorkoutType.FIXEDTIME_SPLITS -> {
                type = PM_TIME_TYPE
                if ((workoutDuration < 2000) || (workoutDuration > 3599900)) {
                    throw Exception("Workout time not in range 0:20..9:59:59")
                }
            }

            WorkoutType.FIXED_CALORIE,
            WorkoutType.FIXED_WATTMINUTES -> {
                type = PM_DISTANCE_TYPE
                if ((workoutDuration < 1) || (workoutDuration > 65534)) {
                    throw Exception("Workout calories/watt not in range 1..65534")
                }
            }

            else -> throw Exception("Invalid workoutType: " + workoutType)
        }

        if (type == PM_DISTANCE_TYPE) {
            // Minimum duration is 100
            // Also, the minimum duration must not cause the total number of splits
            // to exceed the maximum of 30
            if ((splitDuration < 100) || (splitDuration > workoutDuration) || ((workoutDuration / splitDuration) > 30)) {
                throw Exception("Invalid split: (splitDuration < 100) || (splitDuration > workoutDuration) || ((workoutDuration / splitDuration) > 30)")
            }
        } else {
            // Minimum duration is 20 seconds (since .01 resolution, 2000)
            // Also, the minimum duration must not cause the total number of splits
            // to exceed the maximum of 30
            if ((splitDuration < 2000) || (splitDuration > workoutDuration) || ((workoutDuration / splitDuration) > 30)) {
                throw Exception("Invalid split: (splitDuration < 2000) || (splitDuration > workoutDuration) || ((workoutDuration / splitDuration) > 30)")
            }
        }

        val commands = mutableListOf(
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMDATA_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_WORKOUTTYPE.value, listOf(workoutType.value)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_WORKOUTDURATION.value, listOf(type, workoutDuration shr 24 and 0xFF, workoutDuration shr 16 and 0xFF, workoutDuration shr 8 and 0xFF, workoutDuration and 0xFF)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_SPLITDURATION.value, listOf(type, splitDuration shr 24 and 0xFF, splitDuration shr 16 and 0xFF, splitDuration shr 8 and 0xFF, splitDuration and 0xFF))
        )

        if (targetPaceTime > 0) {
            Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_TARGETPACETIME.value, listOf(targetPaceTime shr 24 and 0xFF, targetPaceTime shr 16 and 0xFF, targetPaceTime shr 8 and 0xFF, targetPaceTime and 0xFF))
        }

        commands.addAll(
                arrayOf(
                        Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_CONFIGURE_WORKOUT.value, listOf(1)),
                        Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_SCREENSTATE.value, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT))
                )
        )

        return wrap(commands)
    }

    fun fixedIntervalWorkout(workoutType: WorkoutType, workoutDuration: Int, restDuration: Int, targetPaceTime: Int): ByteArray {
        var type: Int

        if (workoutType === WorkoutType.FIXEDDIST_INTERVAL) {
            type = PM_DISTANCE_TYPE

            // Validate the workoutType duration (100-50000 m) and rest duration (0 - 9:55)
            if ((workoutDuration < 100) || (workoutDuration > 50000) || (restDuration > 119)) {
                throw Exception("Invalid workoutDuration or restDuration: (workoutDuration < 100) || (workoutDuration > 50000) || (restDuration > 119)")
            }
        } else if (workoutType === WorkoutType.FIXEDTIME_INTERVAL) {
            type = PM_TIME_TYPE

            // Validate the duration (:20 - 9:59:59) and rest duration (0 - 9:55)
            if ((workoutDuration < 2000) || (workoutDuration > 3599900) || (restDuration > 119)) {
                throw Exception("Invalid workoutDuration or restDuration: (workoutDuration < 2000) || (workoutDuration > 3599900)|| (restDuration > 119)")
            }
        } else {
            throw Exception("Invalid workoutType: " + workoutType)
        }

        val commands = mutableListOf(
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMDATA_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_WORKOUTTYPE.value, listOf(workoutType.value)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_WORKOUTDURATION.value, listOf(type, workoutDuration shr 24 and 0xFF, workoutDuration shr 16 and 0xFF, workoutDuration shr 8 and 0xFF, workoutDuration and 0xFF)),
                Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_RESTDURATION.value, listOf(restDuration shr 8 and 0xFF, restDuration and 0xFF))
        )

        if (targetPaceTime > 0) {
            Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_TARGETPACETIME.value, listOf(targetPaceTime shr 24 and 0xFF, targetPaceTime shr 16 and 0xFF, targetPaceTime shr 8 and 0xFF, targetPaceTime and 0xFF))
        }

        commands.addAll(
                arrayOf(
                        Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_CONFIGURE_WORKOUT.value, listOf(1)),
                        Command(CSAFE_LONG_PMPROPRIETARY_CMDS.CSAFE_SETPMCFG_CMD.value, CSAFE_PM_LONG_PUSH_CFG_CMDS.CSAFE_PM_SET_SCREENSTATE.value, listOf(SCREENTYPE_WORKOUT, SCREENVALUEWORKOUT_PREPARETOROWWORKOUT))
                )
        )

        return wrap(commands)
    }



    fun wrap(commands: List<Command>): ByteArray {
        val buffer = ByteArrayOutputStream()

        for (command in commands) {
            buffer.write(command.command)
            if (command.command >= CTRL_CMD_SHORT_MIN) {
                if (command.detailCommand != null || command.data != null) {
                    throw Exception("short commands can not contain data or a detail command")
                }
            } else {
                if (command.detailCommand != null) {
                    var dataLength = 1
                    if (command.data != null && command.data.isNotEmpty()) {
                        dataLength += command.data.size + 1
                    }

                    buffer.write(dataLength)
                    buffer.write(command.detailCommand)
                }

                if (command.data != null && command.data.isNotEmpty()) {
                    buffer.write(command.data.size)
                    command.data.forEach({ buffer.write(it) })
                }
            }
        }

        val wrapped = ByteArrayOutputStream(buffer.size() + 3)
        wrapped.write(CSAFE_STANDARD_START_FLAG)
        wrapped.write(buffer.toByteArray())
        wrapped.write(checksum(buffer.toByteArray()).toInt())
        wrapped.write(CSAFE_STOP_FLAG)

        return wrapped.toByteArray()
    }

    fun checksum(bytes: ByteArray): Byte {
        var checksum: Byte = 0x00
        for (i in bytes) checksum = checksum xor i
        return checksum
    }


    data class Command(val command: Int, val detailCommand: Int?, val data: List<Int>?)
}
