package com.liverowing.liverowing.csafe

/* Standard Short Control Commands */
enum class ShortControlCommands (val value: Int) {
    GETSTATUS_CMD(0x80),
    RESET_CMD(0x81),
    GOIDLE_CMD(0x82),
    GOHAVEID_CMD(0x83),
    GOINUSE_CMD (0x85),
    GOFINISHED_CMD(0x86),
    GOREADY_CMD(0x87),
    BADID_CMD(0x88),
    CTRL_CMD_SHORT_MAX(0x89)
}

/* Standard Short Status Commands */
enum class ShortStatusCommands (val value: Int) {
    GETVERSION_CMD(0x91),
    GETID_CMD(0x92),
    GETUNITS_CMD(0x93),
    GETSERIAL_CMD(0x94),
    GETLIST_CMD(0x98),
    GETUTILIZATION_CMD(0x99),
    GETMOTORCURRENT_CMD(0x9A),
    GETODOMETER_CMD(0x9B),
    GETERRORCODE_CMD(0x9C),
    GETSERVICECODE_CMD(0x9D),
    GETUSERCFG1_CMD(0x9E),
    GETUSERCFG2_CMD(0x9F),
    STATUS_CMD_SHORT_MAX(0xA0)
}

/* Standard Short Data Commands */
enum class ShortDataCommands (val value: Int) {
    GETTWORK_CMD(0xA0),
    GETHORIZONTAL_CMD(0xA1),
    GETVERTICAL_CMD(0xA2),
    GETCALORIES_CMD(0xA3),
    GETPROGRAM_CMD(0xA4),
    GETSPEED_CMD(0xA5),
    GETPACE_CMD(0xA6),
    GETCADENCE_CMD(0xA7),
    GETGRADE_CMD(0xA8),
    GETGEAR_CMD(0xA9),
    GETUPLIST_CMD(0xAA),
    GETUSERINFO_CMD(0xAB),
    GETTORQUE_CMD(0xAC),
    GETHRCUR_CMD(0xB0),
    GETHRTZONE_CMD(0xB2),
    GETMETS_CMD(0xB3),
    GETPOWER_CMD(0xB4),
    GETHRAVG_CMD(0xB5),
    GETHRMAX_CMD(0xB6),
    GETUSERDATA1_CMD(0xBE),
    GETUSERDATA2_CMD(0xBF),
    DATA_CMD_SHORT_MAX(0xC0)
}

/* Standard Short Audio Commands */
enum class ShortAudioCommands (val value: Int) {
    GETAUDIOCHANNEL_CMD(0xC0),
    GETAUDIOVOLUME_CMD(0xC1),
    GETAUDIOMUTE_CMD(0xC2),
    AUDIO_CMD_SHORT_MAX(0xC3)
}

/* Standard Short Text Configuration Commands */
enum class ShortTextConfigurationCommands (val value: Int) {
    ENDTEXT_CMD(0xE0),
    DISPLAYPOPUP_CMD(0xE1),
    TEXTCFG_CMD_SHORT_MAX(0xE2)
}

/* Standard Short Text Status Commands */
enum class ShortTextStatusCommands (val value: Int) {
    GETPOPUPSTATUS_CMD(0xE5),
    TEXTSTATUS_CMD_SHORT_MAX(0xE6)
}


/* Standard Long Control Commands */
enum class LongControlCommands (val value: Int) {
    AUTOUPLOAD_CMD(0x01),
    UPLIST_CMD(0x02),
    UPSTATUSSEC_CMD(0x04),
    UPLISTSEC_CMD(0x05),
    CTRL_CMD_LONG_MAX(0x06)
}

/* Standard Long Configuration Commands */
enum class LongConfigurationCommands (val value: Int) {
    IDDIGITS_CMD(0x10),
    SETTIME_CMD(0x11),
    SETDATE_CMD(0x12),
    SETTIMEOUT_CMD(0x13),
    SETUSERCFG1_CMD(0x1A),
    SETUSERCFG2_CMD(0x1B),
    CFG_CMD_LONG_MAX(0x1C)
}

/* Standard Long Data Commands */
enum class LongDataCommands (val value: Int) {
    SETTWORK_CMD(0x20),
    SETHORIZONTAL_CMD(0x21),
    SETVERTICAL_CMD(0x22),
    SETCALORIES_CMD(0x23),
    SETPROGRAM_CMD(0x24),
    SETSPEED_CMD(0x25),
    SETGRADE_CMD(0x28),
    SETGEAR_CMD(0x29),
    SETUSERINFO_CMD(0x2B),
    SETTORQUE_CMD(0x2C),
    SETLEVEL_CMD(0x2D),
    SETTARGETHR_CMD(0x30),
    SETGOAL_CMD(0x32),
    SETMETS_CMD(0x33),
    SETPOWER_CMD(0x34),
    SETHRZONE_CMD(0x35),
    SETHRMAX_CMD(0x36),
    DATA_CMD_LONG_MAX(0x37)
}

/* Standard Long Audio Commands */
enum class LongAudioCommands (val value: Int) {
    SETCHANNELRANGE_CMD(0x40),
    SETVOLUMERANGE_CMD(0x41),
    SETAUDIOMUTE_CMD(0x42),
    SETAUDIOCHANNEL_CMD(0x43),
    SETAUDIOVOLUME_CMD(0x44),
    AUDIO_CMD_LONG_MAX(0x45)
}

/* Standard Long Text Configuration Commands */
enum class LongTextConfigurationCommands (val value: Int) {
    STARTTEXT_CMD(0x60),
    APPENDTEXT_CMD(0x61),
    TEXTCFG_CMD_LONG_MAX(0x62)
}

/* Standard Long Text Status Commands */
enum class LongTextStatusCommands (val value: Int) {
    GETTEXTSTATUS_CMD(0x65),
    TEXTSTATUS_CMD_LONG_MAX(0x66)
}

/* Standard Long Capabilities Commands */
enum class LongCapabilitiesCommands (val value: Int) {
    GETCAPS_CMD(0x70),
    GETUSERCAPS1_CMD(0x7E),
    GETUSERCAPS2_CMD(0x7F),
    CAP_CMD_LONG_MAX(0x80)
}

/*
    The currently defined CSAFE command space is augmented by adding 4 command
    wrappers to allow pushing and pulling of configuration/data from the
    host to the PM

    SETPMCFG_CMD    Push configuration from host to PM
    SETPMDATA_CMD   Push data from host to PM
    GETPMCFG_CMD    Pull configuration to host from PM
    GETPMDATA_CMD   PUll data to host from PM

    Note: These commands have been added for Concept 2 and do not comply
          with the existing CSAFE command set
*/
enum class LongPerformanceMonitorProprietaryCommands (val value: Int) {
    SETPMCFG_CMD(0x76),
    SETPMDATA_CMD(0x77),
    GETPMCFG_CMD(0x7E),
    GETPMDATA_CMD(0x7F),
    PMPROPRIETARY_CMD_LONG_MAX(0x80)
}