/*
 * @(#)MessageDigest1Applet.java Authors: Huafang Ji, Maxim V. Sokolnikov
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.javacard.cjck.tests.api.javacard.security.messagedigest.messagedigest1;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.CryptoException;
import javacard.security.Key;
import javacard.security.MessageDigest;

public class MessageDigest1Applet extends Applet {

    /**
     * CLA byte: jcre test class
     */
    private final static byte CLA_JCRE_TEST = (byte)0x80;

    /**
     * INS byte: DoTests command
     */
    private final static byte INS_DO_TESTS = (byte)0x20;

    private static final byte CJCK_PASS = (byte)0;
    private static final byte CJCK_FAIL = (byte)1;
    private static final byte CJCK_FAIL1 = (byte)2;
    private static final byte CJCK_FAIL2 = (byte)3;
    private static final byte CJCK_FAIL3 = (byte)4;
    private static final byte CJCK_FAIL4 = (byte)5;
    private static final byte CJCK_FAIL5 = (byte)6;
    private static final byte CJCK_FAIL6 = (byte)7;
    private static final byte CJCK_FAIL7 = (byte)8;
    private static final byte CJCK_FAIL8 = (byte)9;
    private static final byte CJCK_FAIL9 = (byte)10;
    private static final byte CJCK_SW1  = (byte)0x9b;
    private static final byte CJCK_SYSERR = (byte)0xfe;
    private static final byte CJCK_SYSERROR = (byte)0xff;

    /**
     * constructor
     * @param bArray from install().
     * @param bOffset from install().
     * @param bLength from install().
     */
    protected MessageDigest1Applet(byte bArray[], short bOffset, byte bLength) {
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new MessageDigest1Applet(bArray, bOffset, bLength);
    }

    public static void throwPassed() throws ISOException {
        ISOException.throwIt(Util.makeShort(CJCK_SW1, CJCK_PASS));
    }

    public static void throwFailed(byte status) throws ISOException {
        if (status == CJCK_PASS) {
            status = CJCK_SYSERROR;
        }
        ISOException.throwIt(Util.makeShort(CJCK_SW1, status));
    }

    public void process(APDU apdu) {

        byte status = CJCK_PASS;
        byte[] buffer = apdu.getBuffer();

        if ((buffer[ISO7816.OFFSET_CLA] == CLA_JCRE_TEST) ||
            (buffer[ISO7816.OFFSET_CLA] == ISO7816.CLA_ISO7816)) {

		  short bytesReceived = 0;
		  if (buffer[ISO7816.OFFSET_LC] != 0) {
		      bytesReceived = apdu.setIncomingAndReceive();

		  }
            switch (buffer[ISO7816.OFFSET_INS]) {

            case ISO7816.INS_SELECT:
                // verify that the AIDs match
                if (!JCSystem.getAID().equals(buffer, ISO7816.OFFSET_CDATA,
                                              buffer[ISO7816.OFFSET_LC]))
                    ISOException.throwIt(ISO7816.SW_APPLET_SELECT_FAILED);
                break;

            case INS_DO_TESTS:    // do one or more tests
                // Lc = number of tests
                // Data = array of test codes
                if (buffer[ISO7816.OFFSET_P1] == (byte)0) {
                    short numTests = (short)(buffer[ISO7816.OFFSET_LC] & 0xFF);

                    // must receive all tests in one block (1-32 tests allowed)
                    if (numTests != bytesReceived)
                        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

                    // get codes of tests to perform from APDU, perform test
                    // and overwrite test code byte in buffer with test result
                    for (short i = ISO7816.OFFSET_CDATA;
                         i < ((short)(ISO7816.OFFSET_CDATA + numTests)); i++) {
                        buffer[i] = this.doTest(buffer[i], buffer, ISO7816.OFFSET_CDATA,
                                                buffer[ISO7816.OFFSET_LC]);
                        if (buffer[i] != CJCK_PASS)
                            status = CJCK_FAIL;
                    }
                    apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, numTests);
                } else {
                    status = this.doTest(buffer[ISO7816.OFFSET_P1], buffer,
                                         ISO7816.OFFSET_CDATA,
                                         buffer[ISO7816.OFFSET_LC]);
                }
                ISOException.throwIt(Util.makeShort(CJCK_SW1, status));

                break;

            default: // unsupported INS
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else { // unsupported CLA
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }



    private static final byte CREATED = (byte)0;
    private static final byte UNCREATED = (byte)0x01;
    private static final byte UNEXPECTED_EX = (byte)0x02;
    private static final byte INVALID_ALGORITHM = (byte)-1;
    private static final byte[] inBuffer1 = {
        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04,
        (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08
    };
    private static final byte[] inBuffer2 = {
        (byte)0x01, (byte)0x02
    };
    private static MessageDigest messagedigest;
    private static byte md_creation_status = UNCREATED;
    private static byte[] outBuffer1  = new byte[(short)30];
    private static byte[] outBuffer2 = new byte[(short)30];                 


    /**
     * @Description  This test checks the followings: 
     *               i) method getInstance() should create an instance of MessageDigest if the selected
     *                  algorithm is supported, otherwise CryptoException.NO_SUCH_ALGORITHM is thrown.
     *              ii) method getAlgorithm() should returned ALG_MD5, or ALG_SHA, or ALG_RIPEMD160
     *             iii) method getLength() should return the correct length for the specific algorithm
     */
    public static byte testMethod001(byte algorithm, boolean externalAccess) {
        MessageDigest md;
        byte result = (byte)0;
        try {
            //get instanceof MessageDigest
            md = MessageDigest.getInstance(algorithm, externalAccess);
        } catch (Exception e) {
            if ((e instanceof CryptoException) &&
                ((CryptoException)e).getReason() == CryptoException.NO_SUCH_ALGORITHM) {
                return CJCK_PASS;
            }
            return CJCK_SYSERR;
        }
        try {   
            if (md == null) {
                result |= (byte)0x01;
            }
            //test method getAlgorithm
            if (md.getAlgorithm() != algorithm) { 
                result |= (byte)0x02;
            }
            //check method getLength()    
            short s = md.getLength();
            if (algorithm == MessageDigest.ALG_SHA) {
                if (s != (byte)20) {
                    result |= (byte)0x04;
                }
            }
        } catch (Exception e) {
            return CJCK_FAIL;  
        }
        return result; // PASS
    }   
    
  
   /** 
    * @Description  This test uses invalid algorithm, and it expects the method to throw
    *               CryptoException.NO_SUCH_ALGORITHM exception
    */
    public static byte testMethod002(boolean externalAccess) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(INVALID_ALGORITHM, externalAccess);
            return CJCK_FAIL;
        } catch (CryptoException e) {
            if (e.getReason() == CryptoException.NO_SUCH_ALGORITHM) {
                return CJCK_PASS;
            } 
            return (byte)e.getReason();
        } catch (Exception e) {
            return CJCK_SYSERR;
        }
    }
  
  
   /**
    * @Description  The following two tests checks that MessageDigest object 
    *               resets to its initial state after the card reset
    */
    public static byte testReset(byte algorithm) {
        try {
            messagedigest = MessageDigest.getInstance(algorithm, false);
            if (messagedigest == null) {
                return (byte)0x01;
            }
        } catch (Exception e) {
            if ((e instanceof CryptoException) &&
                ((CryptoException)e).getReason() == CryptoException.NO_SUCH_ALGORITHM) {
                    md_creation_status = UNCREATED;
                    return CJCK_PASS;
                } else {
                    md_creation_status = UNEXPECTED_EX;
                    return CJCK_SYSERR;
                }
        }
        try {
            messagedigest.doFinal(inBuffer1, (short)0, (short)inBuffer1.length, outBuffer1, (short)0);
            messagedigest.update(inBuffer2, (short)0, (short)inBuffer2.length);
        } catch (Exception e) {
            md_creation_status = UNEXPECTED_EX;
            return CJCK_FAIL;
        }
        md_creation_status = CREATED;
        return CJCK_PASS;
    }
    
    
    /*
     * verify that the object resets to its initial state
     */
    public static byte verify() {
        if (md_creation_status == CREATED) {
            if (messagedigest != null) {
                try {
                    short s = messagedigest.doFinal(inBuffer1, (short)0,
                                                    (short)inBuffer1.length,
                                                    outBuffer2, (short)0);
                    if (Util.arrayCompare(outBuffer1, (short)0,
                                          outBuffer2, (short)0,
                                          (short)s) == (byte)0) {
                        return CJCK_PASS;
                    }
                    return (byte)0x01;
                } catch (Exception e) {
                    return CJCK_SYSERR;
                }
            } 
            return (byte)0x02;
        }
        if (md_creation_status == UNCREATED) {
            return CJCK_PASS;
        } 
        return CJCK_FAIL;        
    }        

    byte Constructor01(byte[] buffer, short offset, short length) {

        try {
            MessageDigestTests msg = new MessageDigestTests();
        } catch (Exception e) {
            return CJCK_FAIL;
        }
        return CJCK_PASS;
    }

    byte ALG_MD5_002(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_MD5, true);
    }

    byte ALG_MD5_003(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_MD5, false);
    }

    byte ALG_RIPEMD160_004(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_RIPEMD160, true);
    }

    byte ALG_RIPEMD160_005(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_RIPEMD160, false);
    }

    byte ALG_SHA_006(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_SHA, true);
    }

    byte ALG_SHA_007(byte[] buffer, short offset, short length) {
        return testMethod001(MessageDigest.ALG_SHA, false);
    }

    byte Case_008(byte[] buffer, short offset, short length) {
        return testMethod002(true);
    }

    byte Case_009(byte[] buffer, short offset, short length) {
        return testMethod002(false);
    }

    byte Reset_010(byte[] buffer, short offset, short length) {
        return testReset(MessageDigest.ALG_SHA);
    }

    byte Reset_011(byte[] buffer, short offset, short length) {
        return testReset(MessageDigest.ALG_MD5);
    }

    byte Reset_012(byte[] buffer, short offset, short length) {
        return testReset(MessageDigest.ALG_RIPEMD160);
    }

    byte Verify(byte[] buffer, short offset, short length) {
        return verify();
    }

    void cleanUp() {
    }
    /*
     * doTest() - perform test indicated by testCode
     */
    public byte doTest(byte testCode, byte[] buffer, short offset, short length) {
        try {
            switch (testCode) {
            // The trivial success case, is required for and testcase excluding and memory clean-up
            case 0:
                try {
                    cleanUp();
                    if (javacard.framework.JCSystem.isObjectDeletionSupported()) {
                        javacard.framework.JCSystem.requestObjectDeletion();
                    }
                } catch (Throwable t) {
                }
                return (byte)0;
            case 1:
                return Constructor01(buffer, offset, length);
            case 2:
                return ALG_MD5_002(buffer, offset, length);
            case 3:
                return ALG_MD5_003(buffer, offset, length);
            case 4:
                return ALG_RIPEMD160_004(buffer, offset, length);
            case 5:
                return ALG_RIPEMD160_005(buffer, offset, length);
            case 6:
                return ALG_SHA_006(buffer, offset, length);
            case 7:
                return ALG_SHA_007(buffer, offset, length);
            case 8:
                return Case_008(buffer, offset, length);
            case 9:
                return Case_009(buffer, offset, length);
            case 10:
                return Reset_010(buffer, offset, length);
            case 11:
                return Reset_011(buffer, offset, length);
            case 12:
                return Reset_012(buffer, offset, length);
            case 13:
                return Verify(buffer, offset, length);
            default:
                // failure code - no test found for given testCode
                return CJCK_SYSERROR;
            }
        } catch (ISOException e) {
            return ((byte)(e.getReason() >>> (short)8) == CJCK_SW1)
                    ? (byte)e.getReason() : CJCK_SYSERROR;
        } catch (Throwable t) {
            return CJCK_SYSERR;
        }
    }
}
