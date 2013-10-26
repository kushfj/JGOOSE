/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;

/**
 * Class containing <code>String</code> constants to be used within the package
 */
public class ConstStrings {
	// String Constants
	public static final String EMPTY_STRING = "";
	public static final String NEWLINE = "\n";

	// Exception description string constants
	public static final String INVALID_APDU = "Invalid APDU";
	public static final String INVALID_APPID = "Invalid GOOSE message APPID";
	public static final String INVALID_VALUE = "Invalid value supplied";
	public static final String INVALID_GOOSE_MSG = "Invalid GOOSEMessage";
	public static final String INVALID_LENGTH = "Invalid length";
	public static final String INVALID_MAC_ADDR = "Invalid hardware MAC address";
	public static final String UNABLE_TO_INIT = "Unable to initialise value";
	public static final String UNKNOWN_EXCEPTION = "Unknown exception";

	// GOOSE PDU label constants
	public static final String GOCBREF_LABEL = "gocbRef: ";
	public static final String TIME_ALLOWED_TO_LIVE_LABEL = "timeAllowedtoLive: ";
	public static final String DATSET_LABEL = "datSet: ";
	public static final String GOID_LABEL = "goID: ";
	public static final String T_LABEL = "t: ";
	public static final String STNUM_LABEL = "stNum: ";
	public static final String SQNUM_LABEL = "sqNum: ";
	public static final String TEST_LABEL = "test: ";
	public static final String CONFREV_LABEL = "confRev: ";
	public static final String NDSCOM_LABEL = "ndsCom: ";
	public static final String NUMDATSET_ENTRIES_LABEL = "numDatSetEntries: ";
}
