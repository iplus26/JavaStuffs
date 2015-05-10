/*
 * ID3V2Test.java
 *
 * Created on 1-Jan-2004
 *
 * Copyright (C)2004,2005 Paul Grebenc
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * $Id: ID3V2Test.java,v 1.28 2005/12/10 05:32:57 paul Exp $
 */

package org.blinkenlights.jid3.test;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.crypt.*;
import org.blinkenlights.jid3.io.*;
import org.blinkenlights.jid3.util.*;
import org.blinkenlights.jid3.v2.*;

/**
 * @author paul
 *
 * Run tests on V2.3.0 support.
 */
public class ID3V2Test extends TestCase
{
    /**
     * Constructor for ID3V2Test.
     * @param arg0
     */
    public ID3V2Test(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ID3V2Test.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // all expected results of tests assume no padding
        try
        {
            ID3V2Tag.setDefaultPaddingLength(0);
        }
        catch (ID3Exception e) {}
        
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testReadV2_3_0Tag()
    {
        try
        {
            File oSourceFile = new File(AllTests.s_RootPath + "v2_3_0tags.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
            
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            System.out.println("\n*** v2_3_0tags.mp3 tag:");
            ID3Util.printTags(aoID3Tag);
            if (aoID3Tag.length != 1)
            {
                fail("There should be exactly one set of tags in this file.");
            }
            if ( ! (aoID3Tag[0] instanceof ID3V2_3_0Tag) )
            {
                fail("Expected ID3V2_3_0Tag.");
            }
            else
            {
                ID3V2Tag oID3V2Tag = (ID3V2Tag)aoID3Tag[0];
                
                int iPaddingLength = oID3V2Tag.getPaddingLength();
                if (iPaddingLength == 133)
                {
                    System.out.println("Padding length: " + oID3V2Tag.getPaddingLength());
                }
                else
                {
                    fail("Padding length is 133, not " + iPaddingLength + ".");
                }
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

/*    public void testWriteV2_3_0Tag()
    {
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_testresult.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_testresult.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            
            TALBTextInformationID3V2Frame oTALB = new TALBTextInformationID3V2Frame("Album");
            //oTALB.setCompressionFlag(true);
            oID3V2_3_0Tag.setFrame(oTALB);
            //oID3V2_3_0Tag.setAlbum("Album");
            TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("Artist");
            oID3V2_3_0Tag.setFrame(oTPE1);
            //oID3V2_3_0Tag.removeFrame(TPE1TextInformationID3V2Frame.class);
            
            oID3V2_3_0Tag.setPaddingLength(8);
        
            System.out.println(oID3V2_3_0Tag.toString());
        
            oMediaFile.setID3Tag(oID3V2_3_0Tag);
            oMediaFile.sync();
            
            // check against expected result
            ID3Util.compare(AllTests.s_RootPath + "v2_3_0tags.mp3", AllTests.s_RootPath + "id3_v2_3_0_testresult.mp3");
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }*/
    
    private void runTagVerifyTest(ID3V2_3_0Tag oID3V2_3_0Tag, String sExpectedPrefix)
    {
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            oMediaFile.setID3Tag(oID3V2_3_0Tag);
            oMediaFile.sync();
            
            // check against expected result
            byte[] abyPrefix = ID3Util.convertFrhedToByteArray(sExpectedPrefix);
            
            ID3Util.compareFilePrefix(oSourceFile, abyPrefix);

            // test our ability to read back the file
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            if (aoID3Tag.length != 1)
            {
                fail("There should be exactly one set of tags in this file.");
            }
            if ( ! (aoID3Tag[0] instanceof ID3V2_3_0Tag) )
            {
                fail("Expected ID3V2_3_0Tag.");
            }
            else
            {
                ID3V2_3_0Tag oReadID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                
                // AENC
                AENCID3V2Frame[] aoAENCID3V2Frame1 = oID3V2_3_0Tag.getAENCFrames();
                AENCID3V2Frame[] aoAENCID3V2Frame2 = oReadID3V2_3_0Tag.getAENCFrames();
                
                if (aoAENCID3V2Frame1.length != aoAENCID3V2Frame2.length)
                {
                    fail(aoAENCID3V2Frame1.length + " AENC frame(s) written, " + aoAENCID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoAENCID3V2Frame1.length; i++)
                {
                    if ( ! aoAENCID3V2Frame1[i].equals(aoAENCID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoAENCID3V2Frame1[i].toString() + " and " + aoAENCID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // APIC
                APICID3V2Frame[] aoAPICID3V2Frame1 = oID3V2_3_0Tag.getAPICFrames();
                APICID3V2Frame[] aoAPICID3V2Frame2 = oReadID3V2_3_0Tag.getAPICFrames();
                
                if (aoAPICID3V2Frame1.length != aoAPICID3V2Frame2.length)
                {
                    fail(aoAPICID3V2Frame1.length + " APIC frame(s) written, " + aoAPICID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoAPICID3V2Frame1.length; i++)
                {
                    if ( ! aoAPICID3V2Frame1[i].equals(aoAPICID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoAPICID3V2Frame1[i].toString() + " and " + aoAPICID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // COMM
                COMMID3V2Frame[] aoCOMMID3V2Frame1 = oID3V2_3_0Tag.getCOMMFrames();
                COMMID3V2Frame[] aoCOMMID3V2Frame2 = oReadID3V2_3_0Tag.getCOMMFrames();
                
                if (aoCOMMID3V2Frame1.length != aoCOMMID3V2Frame2.length)
                {
                    fail(aoCOMMID3V2Frame1.length + " COMM frame(s) written, " + aoCOMMID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoCOMMID3V2Frame1.length; i++)
                {
                    if ( ! aoCOMMID3V2Frame1[i].equals(aoCOMMID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoCOMMID3V2Frame1[i].toString() + " and " + aoCOMMID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // ENCR
                ENCRID3V2Frame[] aoENCRID3V2Frame1 = oID3V2_3_0Tag.getENCRFrames();
                ENCRID3V2Frame[] aoENCRID3V2Frame2 = oReadID3V2_3_0Tag.getENCRFrames();
                
                if (aoENCRID3V2Frame1.length != aoENCRID3V2Frame2.length)
                {
                    fail(aoENCRID3V2Frame1.length + " ENCR frame(s) written, " + aoENCRID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoENCRID3V2Frame1.length; i++)
                {
                    if ( ! aoENCRID3V2Frame1[i].equals(aoENCRID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoENCRID3V2Frame1[i].toString() + " and " + aoENCRID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // GEOB
                GEOBID3V2Frame[] aoGEOBID3V2Frame1 = oID3V2_3_0Tag.getGEOBFrames();
                GEOBID3V2Frame[] aoGEOBID3V2Frame2 = oReadID3V2_3_0Tag.getGEOBFrames();
                
                if (aoGEOBID3V2Frame1.length != aoGEOBID3V2Frame2.length)
                {
                    fail(aoGEOBID3V2Frame1.length + " GEOB frame(s) written, " + aoGEOBID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoGEOBID3V2Frame1.length; i++)
                {
                    if ( ! aoGEOBID3V2Frame1[i].equals(aoGEOBID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoGEOBID3V2Frame1[i].toString() + " and " + aoGEOBID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // GRID
                GRIDID3V2Frame[] aoGRIDID3V2Frame1 = oID3V2_3_0Tag.getGRIDFrames();
                GRIDID3V2Frame[] aoGRIDID3V2Frame2 = oReadID3V2_3_0Tag.getGRIDFrames();
                
                if (aoGRIDID3V2Frame1.length != aoGRIDID3V2Frame2.length)
                {
                    fail(aoGRIDID3V2Frame1.length + " GRID frame(s) written, " + aoGRIDID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoGRIDID3V2Frame1.length; i++)
                {
                    if ( ! aoGRIDID3V2Frame1[i].equals(aoGRIDID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoGRIDID3V2Frame1[i].toString() + " and " + aoGRIDID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // LINK
                LINKID3V2Frame[] aoLINKID3V2Frame1 = oID3V2_3_0Tag.getLINKFrames();
                LINKID3V2Frame[] aoLINKID3V2Frame2 = oReadID3V2_3_0Tag.getLINKFrames();
                
                if (aoLINKID3V2Frame1.length != aoLINKID3V2Frame2.length)
                {
                    fail(aoLINKID3V2Frame1.length + " LINK frame(s) written, " + aoLINKID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoLINKID3V2Frame1.length; i++)
                {
                    if ( ! aoLINKID3V2Frame1[i].equals(aoLINKID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoLINKID3V2Frame1[i].toString() + " and " + aoLINKID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // PRIV
                PRIVID3V2Frame[] aoPRIVID3V2Frame1 = oID3V2_3_0Tag.getPRIVFrames();
                PRIVID3V2Frame[] aoPRIVID3V2Frame2 = oReadID3V2_3_0Tag.getPRIVFrames();
                
                if (aoPRIVID3V2Frame1.length != aoPRIVID3V2Frame2.length)
                {
                    fail(aoPRIVID3V2Frame1.length + " PRIV frame(s) written, " + aoPRIVID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoPRIVID3V2Frame1.length; i++)
                {
                    if ( ! aoPRIVID3V2Frame1[i].equals(aoPRIVID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoPRIVID3V2Frame1[i].toString() + " and " + aoPRIVID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // POPM
                POPMID3V2Frame[] aoPOPMID3V2Frame1 = oID3V2_3_0Tag.getPOPMFrames();
                POPMID3V2Frame[] aoPOPMID3V2Frame2 = oReadID3V2_3_0Tag.getPOPMFrames();
                
                if (aoPOPMID3V2Frame1.length != aoPOPMID3V2Frame2.length)
                {
                    fail(aoPOPMID3V2Frame1.length + " POPM frame(s) written, " + aoPOPMID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoPOPMID3V2Frame1.length; i++)
                {
                    if ( ! aoPOPMID3V2Frame1[i].equals(aoPOPMID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoPOPMID3V2Frame1[i].toString() + " and " + aoPOPMID3V2Frame2[i].toString() + ".");
                    }
                }

                // SYLT
                SYLTID3V2Frame[] aoSYLTID3V2Frame1 = oID3V2_3_0Tag.getSYLTFrames();
                SYLTID3V2Frame[] aoSYLTID3V2Frame2 = oReadID3V2_3_0Tag.getSYLTFrames();
                
                if (aoSYLTID3V2Frame1.length != aoSYLTID3V2Frame2.length)
                {
                    fail(aoSYLTID3V2Frame1.length + " SYLT frame(s) written, " + aoSYLTID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoSYLTID3V2Frame1.length; i++)
                {
                    if ( ! aoSYLTID3V2Frame1[i].equals(aoSYLTID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoSYLTID3V2Frame1[i].toString() + " and " + aoSYLTID3V2Frame2[i].toString() + ".");
                    }
                }

                // TXXX
                TXXXTextInformationID3V2Frame[] aoTXXXID3V2Frame1 = oID3V2_3_0Tag.getTXXXTextInformationFrames();
                TXXXTextInformationID3V2Frame[] aoTXXXID3V2Frame2 = oReadID3V2_3_0Tag.getTXXXTextInformationFrames();
                
                if (aoTXXXID3V2Frame1.length != aoTXXXID3V2Frame2.length)
                {
                    fail(aoTXXXID3V2Frame1.length + " TXXX frame(s) written, " + aoTXXXID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoTXXXID3V2Frame1.length; i++)
                {
                    if ( ! aoTXXXID3V2Frame1[i].equals(aoTXXXID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoTXXXID3V2Frame1[i].toString() + " and " + aoTXXXID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // UFID
                UFIDID3V2Frame[] aoUFIDID3V2Frame1 = oID3V2_3_0Tag.getUFIDFrames();
                UFIDID3V2Frame[] aoUFIDID3V2Frame2 = oReadID3V2_3_0Tag.getUFIDFrames();
                
                if (aoUFIDID3V2Frame1.length != aoUFIDID3V2Frame2.length)
                {
                    fail(aoUFIDID3V2Frame1.length + " UFID frame(s) written, " + aoUFIDID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoUFIDID3V2Frame1.length; i++)
                {
                    if ( ! aoUFIDID3V2Frame1[i].equals(aoUFIDID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoUFIDID3V2Frame1[i].toString() + " and " + aoUFIDID3V2Frame2[i].toString() + ".");
                    }
                }

                // USLT
                USLTID3V2Frame[] aoUSLTID3V2Frame1 = oID3V2_3_0Tag.getUSLTFrames();
                USLTID3V2Frame[] aoUSLTID3V2Frame2 = oReadID3V2_3_0Tag.getUSLTFrames();
                
                if (aoUSLTID3V2Frame1.length != aoUSLTID3V2Frame2.length)
                {
                    fail(aoUSLTID3V2Frame1.length + " USLT frame(s) written, " + aoUSLTID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoUSLTID3V2Frame1.length; i++)
                {
                    if ( ! aoUSLTID3V2Frame1[i].equals(aoUSLTID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoUSLTID3V2Frame1[i].toString() + " and " + aoUSLTID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // WCOM
                WCOMUrlLinkID3V2Frame[] aoWCOMUrlLinkID3V2Frame1 = oID3V2_3_0Tag.getWCOMUrlLinkFrames();
                WCOMUrlLinkID3V2Frame[] aoWCOMUrlLinkID3V2Frame2 = oReadID3V2_3_0Tag.getWCOMUrlLinkFrames();
                
                if (aoWCOMUrlLinkID3V2Frame1.length != aoWCOMUrlLinkID3V2Frame2.length)
                {
                    fail(aoWCOMUrlLinkID3V2Frame1.length + " WCOM frame(s) written, " + aoWCOMUrlLinkID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoWCOMUrlLinkID3V2Frame1.length; i++)
                {
                    if ( ! aoWCOMUrlLinkID3V2Frame1[i].equals(aoWCOMUrlLinkID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoWCOMUrlLinkID3V2Frame1[i].toString() + " and " + aoWCOMUrlLinkID3V2Frame2[i].toString() + ".");
                    }
                }

                // WOAR
                WOARUrlLinkID3V2Frame[] aoWOARUrlLinkID3V2Frame1 = oID3V2_3_0Tag.getWOARUrlLinkFrames();
                WOARUrlLinkID3V2Frame[] aoWOARUrlLinkID3V2Frame2 = oReadID3V2_3_0Tag.getWOARUrlLinkFrames();
                
                if (aoWOARUrlLinkID3V2Frame1.length != aoWOARUrlLinkID3V2Frame2.length)
                {
                    fail(aoWOARUrlLinkID3V2Frame1.length + " WOAR frame(s) written, " + aoWOARUrlLinkID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoWOARUrlLinkID3V2Frame1.length; i++)
                {
                    if ( ! aoWOARUrlLinkID3V2Frame1[i].equals(aoWOARUrlLinkID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoWOARUrlLinkID3V2Frame1[i].toString() + " and " + aoWOARUrlLinkID3V2Frame2[i].toString() + ".");
                    }
                }

                // WXXX
                WXXXUrlLinkID3V2Frame[] aoWXXXUrlLinkID3V2Frame1 = oID3V2_3_0Tag.getWXXXUrlLinkFrames();
                WXXXUrlLinkID3V2Frame[] aoWXXXUrlLinkID3V2Frame2 = oReadID3V2_3_0Tag.getWXXXUrlLinkFrames();
                
                if (aoWXXXUrlLinkID3V2Frame1.length != aoWXXXUrlLinkID3V2Frame2.length)
                {
                    fail(aoWXXXUrlLinkID3V2Frame1.length + " WXXX frame(s) written, " + aoWXXXUrlLinkID3V2Frame2.length + " frame(s) read.");
                }
                
                for (int i=0; i < aoWXXXUrlLinkID3V2Frame1.length; i++)
                {
                    if ( ! aoWXXXUrlLinkID3V2Frame1[i].equals(aoWXXXUrlLinkID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoWXXXUrlLinkID3V2Frame1[i].toString() + " and " + aoWXXXUrlLinkID3V2Frame2[i].toString() + ".");
                    }
                }
                
                // all single frames mapped from frame id
                ID3V2Frame[] aoID3V2Frame1 = oID3V2_3_0Tag.getSingleFrames();
                ID3V2Frame[] aoID3V2Frame2 = oReadID3V2_3_0Tag.getSingleFrames();
                Set oTagFrameSet1 = new HashSet();
                for (int i=0; i < aoID3V2Frame1.length; i++)
                {
                    oTagFrameSet1.add(aoID3V2Frame1[i].getClass().getName());
                }
                Set oTagFrameSet2 = new HashSet();
                for (int i=0; i < aoID3V2Frame2.length; i++)
                {
                    oTagFrameSet2.add(aoID3V2Frame2[i].getClass().getName());
                }
                
                if ((aoID3V2Frame1.length != aoID3V2Frame2.length) || ( ! oTagFrameSet1.equals(oTagFrameSet2)))
                {
                    StringBuffer sbFail = new StringBuffer();
                    sbFail.append("Single frames written (");
                    for (int i=0; i < aoID3V2Frame1.length; i++)
                    {
                        sbFail.append(new String(aoID3V2Frame1[i].getClass().getName()) + ",");
                    }
                    if (aoID3V2Frame1.length > 0)
                    {
                        sbFail = (StringBuffer)sbFail.deleteCharAt(sbFail.length() - 1);  // remove last comma
                    }
                    sbFail.append("), single frames read (");
                    for (int i=0; i < aoID3V2Frame2.length; i++)
                    {
                        sbFail.append(new String(aoID3V2Frame2[i].getClass().getName()) + ",");
                    }
                    if (aoID3V2Frame2.length > 0)
                    {
                        sbFail = (StringBuffer)sbFail.deleteCharAt(sbFail.length() - 1);  // remove last comma
                    }
                    sbFail.append(")");
                    
                    fail(sbFail.toString());
                }
                
                for (int i=0; i < aoID3V2Frame1.length; i++)
                {
                    if ( ! aoID3V2Frame1[i].equals(aoID3V2Frame2[i]))
                    {
                        fail("Tags do not match: " + aoID3V2Frame1[i].toString() + " and " + aoID3V2Frame2[i].toString() + ".");
                    }
                }
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTALBFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TALBTextInformationID3V2Frame oTALB = new TALBTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTALBTextInformationFrame(oTALB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TALB<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }
        
        try
        {
            // unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TALBTextInformationID3V2Frame oTALB = new TALBTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oTALB.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setTALBTextInformationFrame(oTALB);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>ATALB<bh:00><bh:00><bh:00>7<bh:00><bh:00><bh:01><bh:ff><bh:fe>a<bh:00>b<bh:00>c<bh:00>d<bh:00>e<bh:00>f<bh:00>g<bh:00>h<bh:00>i<bh:00>j<bh:00>k<bh:00>l<bh:00>m<bh:00>n<bh:00>o<bh:00>p<bh:00>q<bh:00>r<bh:00>s<bh:00>t<bh:00>u<bh:00>v<bh:00>w<bh:00>x<bh:00>y<bh:00>z<bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTBPMFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TBPMTextInformationID3V2Frame oTBPM = new TBPMTextInformationID3V2Frame(123456789);
            oID3V2_3_0Tag.setTBPMTextInformationFrame(oTBPM);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:14>TBPM<bh:00><bh:00><bh:00><bh:0a><bh:00><bh:00><bh:00>123456789";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTCOMFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TCOMTextInformationID3V2Frame oTCOM = new TCOMTextInformationID3V2Frame("abcdefg/hijklmnopq/rstuvwxyz");
            oID3V2_3_0Tag.setTCOMTextInformationFrame(oTCOM);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'TCOM<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>abcdefg/hijklmnopq/rstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTCONFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            ContentType oContentType = new ContentType();
            oContentType.setGenre(ContentType.Genre.Blues);
            oContentType.setGenre(ContentType.Genre.Rock);
            oContentType.setIsCover(true);
            oContentType.setIsRemix(true);
            oContentType.setRefinement("refinement");
            TCONTextInformationID3V2Frame oTCON = new TCONTextInformationID3V2Frame(oContentType);
            oID3V2_3_0Tag.setTCONTextInformationFrame(oTCON);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>$TCON<bh:00><bh:00><bh:00><bh:1a><bh:00><bh:00><bh:00>(0)(17)(CR)(RX)refinement";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTCOPFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TCOPTextInformationID3V2Frame oTCOP = new TCOPTextInformationID3V2Frame(1234, "abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTCOPTextInformationFrame(oTCOP);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>*TCOP<bh:00><bh:00><bh:00> <bh:00><bh:00><bh:00>1234 abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTDATFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TDATTextInformationID3V2Frame oTDAT = new TDATTextInformationID3V2Frame(25, 12);
            oID3V2_3_0Tag.setTDATTextInformationFrame(oTDAT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>TDAT<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>2512";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTDLYFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TDLYTextInformationID3V2Frame oTDLY = new TDLYTextInformationID3V2Frame(12345);
            oID3V2_3_0Tag.setTDLYTextInformationFrame(oTDLY);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:10>TDLY<bh:00><bh:00><bh:00><bh:06><bh:00><bh:00><bh:00>12345";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTENCFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TENCTextInformationID3V2Frame oTENC = new TENCTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTENCTextInformationFrame(oTENC);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TENC<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTEXTFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TEXTTextInformationID3V2Frame oTEXT = new TEXTTextInformationID3V2Frame("abcdefg/hijklmnopq/rstuvwxyz");
            oID3V2_3_0Tag.setTEXTTextInformationFrame(oTEXT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'TEXT<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>abcdefg/hijklmnopq/rstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTFLTFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TFLTTextInformationID3V2Frame oTFLT = new TFLTTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTFLTTextInformationFrame(oTFLT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TFLT<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTIMEFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TIMETextInformationID3V2Frame oTIME = new TIMETextInformationID3V2Frame(8, 5);
            oID3V2_3_0Tag.setTIMETextInformationFrame(oTIME);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>TIME<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>0805";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TIMETextInformationID3V2Frame oTIME = new TIMETextInformationID3V2Frame(8, 5);
            oTIME.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setTIMETextInformationFrame(oTIME);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:15>TIME<bh:00><bh:00><bh:00><bh:0b><bh:00><bh:00><bh:01><bh:ff><bh:fe>0<bh:00>8<bh:00>0<bh:00>5<bh:00>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTIT1Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TIT1TextInformationID3V2Frame oTIT1 = new TIT1TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTIT1TextInformationFrame(oTIT1);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TIT1<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTIT2Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TIT2TextInformationID3V2Frame oTIT2 = new TIT2TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTIT2TextInformationFrame(oTIT2);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TIT2<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTIT3Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TIT3TextInformationID3V2Frame oTIT3 = new TIT3TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTIT3TextInformationFrame(oTIT3);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TIT3<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTKEYFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TKEYTextInformationID3V2Frame oTKEY = new TKEYTextInformationID3V2Frame("C#m");
            oID3V2_3_0Tag.setTKEYTextInformationFrame(oTKEY);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>TKEY<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:00>C#m";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTLANFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TLANTextInformationID3V2Frame oTLAN = new TLANTextInformationID3V2Frame("eng");
            oID3V2_3_0Tag.setTLANTextInformationFrame(oTLAN);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>TLAN<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:00>eng";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTLENFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TLENTextInformationID3V2Frame oTLEN = new TLENTextInformationID3V2Frame(12345);
            oID3V2_3_0Tag.setTLENTextInformationFrame(oTLEN);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:10>TLEN<bh:00><bh:00><bh:00><bh:06><bh:00><bh:00><bh:00>12345";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTMEDFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TMEDTextInformationID3V2Frame oTMED = new TMEDTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTMEDTextInformationFrame(oTMED);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TMED<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTOALFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TOALTextInformationID3V2Frame oTOAL = new TOALTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTOALTextInformationFrame(oTOAL);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TOAL<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTOFNFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TOFNTextInformationID3V2Frame oTOFN = new TOFNTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTOFNTextInformationFrame(oTOFN);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TOFN<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTOLYFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TOLYTextInformationID3V2Frame oTOLY = new TOLYTextInformationID3V2Frame("abcdefg/hijklmnopq/rstuvwxyz");
            oID3V2_3_0Tag.setTOLYTextInformationFrame(oTOLY);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'TOLY<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>abcdefg/hijklmnopq/rstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTOPEFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TOPETextInformationID3V2Frame oTOPE = new TOPETextInformationID3V2Frame("abcdefg/hijklmnopq/rstuvwxyz");
            oID3V2_3_0Tag.setTOPETextInformationFrame(oTOPE);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'TOPE<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>abcdefg/hijklmnopq/rstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTORYFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TORYTextInformationID3V2Frame oTORY = new TORYTextInformationID3V2Frame(1999);
            oID3V2_3_0Tag.setTORYTextInformationFrame(oTORY);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>TORY<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>1999";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTOWNFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TOWNTextInformationID3V2Frame oTOWN = new TOWNTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTOWNTextInformationFrame(oTOWN);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TOWN<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTPE1Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("abcdefg/hijklmnopq/rstuvwxyz");
            oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'TPE1<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>abcdefg/hijklmnopq/rstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTPE2Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPE2TextInformationID3V2Frame oTPE2 = new TPE2TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTPE2TextInformationFrame(oTPE2);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TPE2<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTPE3Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPE3TextInformationID3V2Frame oTPE3 = new TPE3TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTPE3TextInformationFrame(oTPE3);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TPE3<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTPE4Frame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPE4TextInformationID3V2Frame oTPE4 = new TPE4TextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTPE4TextInformationFrame(oTPE4);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TPE4<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    public void testTPOSFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPOSTextInformationID3V2Frame oTPOS = new TPOSTextInformationID3V2Frame(12);
            oID3V2_3_0Tag.setTPOSTextInformationFrame(oTPOS);
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0d>TPOS<bh:00><bh:00><bh:00><bh:03><bh:00><bh:00><bh:00>12";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
            
            oTPOS = new TPOSTextInformationID3V2Frame(12, 34);
            oID3V2_3_0Tag.setTPOSTextInformationFrame(oTPOS);
            sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:10>TPOS<bh:00><bh:00><bh:00><bh:06><bh:00><bh:00><bh:00>12/34";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTPUBFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TPUBTextInformationID3V2Frame oTPUB = new TPUBTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTPUBTextInformationFrame(oTPUB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TPUB<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    public void testTRCKFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TRCKTextInformationID3V2Frame oTRCK = new TRCKTextInformationID3V2Frame(12);
            oID3V2_3_0Tag.setTRCKTextInformationFrame(oTRCK);
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0d>TRCK<bh:00><bh:00><bh:00><bh:03><bh:00><bh:00><bh:00>12";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
            
            oTRCK = new TRCKTextInformationID3V2Frame(12, 34);
            oID3V2_3_0Tag.setTRCKTextInformationFrame(oTRCK);
            sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:10>TRCK<bh:00><bh:00><bh:00><bh:06><bh:00><bh:00><bh:00>12/34";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTRDAFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TRDATextInformationID3V2Frame oTRDA = new TRDATextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTRDATextInformationFrame(oTRDA);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TRDA<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTRSNFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TRSNTextInformationID3V2Frame oTRSN = new TRSNTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTRSNTextInformationFrame(oTRSN);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TRSN<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTRSOFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TRSOTextInformationID3V2Frame oTRSO = new TRSOTextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTRSOTextInformationFrame(oTRSO);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TRSO<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTSIZFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TSIZTextInformationID3V2Frame oTSIZ = new TSIZTextInformationID3V2Frame(12345);
            oID3V2_3_0Tag.setTSIZTextInformationFrame(oTSIZ);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:10>TSIZ<bh:00><bh:00><bh:00><bh:06><bh:00><bh:00><bh:00>12345";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTSRCFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TSRCTextInformationID3V2Frame oTSRC = new TSRCTextInformationID3V2Frame("123456789012");
            oID3V2_3_0Tag.setTSRCTextInformationFrame(oTSRC);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:17>TSRC<bh:00><bh:00><bh:00><bh:0d><bh:00><bh:00><bh:00>123456789012";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTSSEFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TSSETextInformationID3V2Frame oTSSE = new TSSETextInformationID3V2Frame("abcdefghijklmnopqrstuvwxyz");
            oID3V2_3_0Tag.setTSSETextInformationFrame(oTSSE);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>%TSSE<bh:00><bh:00><bh:00><bh:1b><bh:00><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTXXXFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TXXXTextInformationID3V2Frame oTXXX = new TXXXTextInformationID3V2Frame("description", "information");
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX);
            oTXXX = new TXXXTextInformationID3V2Frame("description2", "information2");
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>FTXXX<bh:00><bh:00><bh:00><bh:18><bh:00><bh:00><bh:00>description<bh:00>informationTXXX<bh:00><bh:00><bh:00><bh:1a><bh:00><bh:00><bh:00>description2<bh:00>information2";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TXXXTextInformationID3V2Frame oTXXX = new TXXXTextInformationID3V2Frame("description", "information");
            oTXXX.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX);
            oTXXX = new TXXXTextInformationID3V2Frame("description2", "information2");
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>aTXXX<bh:00><bh:00><bh:00>3<bh:00><bh:00><bh:01><bh:ff><bh:fe>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00><bh:00><bh:00><bh:ff><bh:fe>i<bh:00>n<bh:00>f<bh:00>o<bh:00>r<bh:00>m<bh:00>a<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00>TXXX<bh:00><bh:00><bh:00><bh:1a><bh:00><bh:00><bh:00>description2<bh:00>information2";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testTYERFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            TYERTextInformationID3V2Frame oTYER = new TYERTextInformationID3V2Frame(1999);
            oID3V2_3_0Tag.setTYERTextInformationFrame(oTYER);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>TYER<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>1999";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWCOMFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WCOMUrlLinkID3V2Frame oWCOM = new WCOMUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.addWCOMUrlLinkFrame(oWCOM);
            oWCOM = new WCOMUrlLinkID3V2Frame("http://www.grebenc.ca");
            oID3V2_3_0Tag.addWCOMUrlLinkFrame(oWCOM);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>FWCOM<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.orgWCOM<bh:00><bh:00><bh:00><bh:15><bh:00><bh:00>http://www.grebenc.ca";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWCOPFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WCOPUrlLinkID3V2Frame oWCOP = new WCOPUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWCOPUrlLinkFrame(oWCOP);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WCOP<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWOAFFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WOAFUrlLinkID3V2Frame oWOAF = new WOAFUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWOAFUrlLinkFrame(oWOAF);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WOAF<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWOARFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WOARUrlLinkID3V2Frame oWOAR = new WOARUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.addWOARUrlLinkFrame(oWOAR);
            oWOAR = new WOARUrlLinkID3V2Frame("http://www.grebenc.ca");
            oID3V2_3_0Tag.addWOARUrlLinkFrame(oWOAR);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>FWOAR<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.orgWOAR<bh:00><bh:00><bh:00><bh:15><bh:00><bh:00>http://www.grebenc.ca";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWOASFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WOASUrlLinkID3V2Frame oWOAS = new WOASUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWOASUrlLinkFrame(oWOAS);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WOAS<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWORSFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WORSUrlLinkID3V2Frame oWORS = new WORSUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWORSUrlLinkFrame(oWORS);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WORS<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWPAYFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WPAYUrlLinkID3V2Frame oWPAY = new WPAYUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWPAYUrlLinkFrame(oWPAY);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WPAY<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWPUBFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WPUBUrlLinkID3V2Frame oWPUB = new WPUBUrlLinkID3V2Frame("http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.setWPUBUrlLinkFrame(oWPUB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'WPUB<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWXXXFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WXXXUrlLinkID3V2Frame oWXXX = new WXXXUrlLinkID3V2Frame("description", "http://jid3.blinkenlights.org");
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX);
            oWXXX = new WXXXUrlLinkID3V2Frame("another description", "http://www.grebenc.ca");
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>hWXXX<bh:00><bh:00><bh:00>*<bh:00><bh:00><bh:00>another description<bh:00>http://www.grebenc.caWXXX<bh:00><bh:00><bh:00>*<bh:00><bh:00><bh:00>description<bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8559-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            WXXXUrlLinkID3V2Frame oWXXX = new WXXXUrlLinkID3V2Frame("description", "http://jid3.blinkenlights.org");
            oWXXX.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX);
            oWXXX = new WXXXUrlLinkID3V2Frame("another description", "http://www.grebenc.ca");
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>vWXXX<bh:00><bh:00><bh:00>*<bh:00><bh:00><bh:00>another description<bh:00>http://www.grebenc.caWXXX<bh:00><bh:00><bh:00>8<bh:00><bh:00><bh:01><bh:ff><bh:fe>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00><bh:00><bh:00>http://jid3.blinkenlights.org";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testAENCFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.addAENCFrame(new AENCID3V2Frame("owner identifier", 123, 456, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
            oID3V2_3_0Tag.addAENCFrame(new AENCID3V2Frame("owner identifier 2", 234, 567, new byte[] { 0x06, 0x07, 0x08, 0x09, 0x0a }));
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>JAENC<bh:00><bh:00><bh:00><bh:1a><bh:00><bh:00>owner identifier<bh:00><bh:00>{<bh:01><bh:c8><bh:01><bh:02><bh:03><bh:04><bh:05>AENC<bh:00><bh:00><bh:00><bh:1c><bh:00><bh:00>owner identifier 2<bh:00><bh:00><bh:ea><bh:02>7<bh:06><bh:07><bh:08><bh:09><bh:0a>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testAPICFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.addAPICFrame(new APICID3V2Frame("image/png", APICID3V2Frame.PictureType.Artist, "Artist image.", new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
            oID3V2_3_0Tag.addAPICFrame(new APICID3V2Frame("image/jpeg", APICID3V2Frame.PictureType.FrontCover, "Front cover.", new byte[] { 0x05, 0x04, 0x03, 0x02, 0x01 }));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>RAPIC<bh:00><bh:00><bh:00><bh:1f><bh:00><bh:00><bh:00>image/png<bh:00><bh:08>Artist image.<bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>APIC<bh:00><bh:00><bh:00><bh:1f><bh:00><bh:00><bh:00>image/jpeg<bh:00><bh:03>Front cover.<bh:00><bh:05><bh:04><bh:03><bh:02><bh:01>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            APICID3V2Frame oAPIC = new APICID3V2Frame("image/png", APICID3V2Frame.PictureType.Artist, "Artist image.", new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 });
            oAPIC.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addAPICFrame(oAPIC);
            oID3V2_3_0Tag.addAPICFrame(new APICID3V2Frame("image/jpeg", APICID3V2Frame.PictureType.FrontCover, "Front cover.", new byte[] { 0x05, 0x04, 0x03, 0x02, 0x01 }));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>bAPIC<bh:00><bh:00><bh:00>/<bh:00><bh:00><bh:01>image/png<bh:00><bh:08><bh:ff><bh:fe>A<bh:00>r<bh:00>t<bh:00>i<bh:00>s<bh:00>t<bh:00> <bh:00>i<bh:00>m<bh:00>a<bh:00>g<bh:00>e<bh:00>.<bh:00><bh:00><bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>APIC<bh:00><bh:00><bh:00><bh:1f><bh:00><bh:00><bh:00>image/jpeg<bh:00><bh:03>Front cover.<bh:00><bh:05><bh:04><bh:03><bh:02><bh:01>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testCOMMFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.addCOMMFrame(new COMMID3V2Frame("eng", "short description", "actual text"));
            oID3V2_3_0Tag.addCOMMFrame(new COMMID3V2Frame("rus", "next description", "next actual text"));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>ZCOMM<bh:00><bh:00><bh:00>!<bh:00><bh:00><bh:00>engshort description<bh:00>actual textCOMM<bh:00><bh:00><bh:00>%<bh:00><bh:00><bh:00>rusnext description<bh:00>next actual text";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8559-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            COMMID3V2Frame oCOMM = new COMMID3V2Frame("eng", "short description", "actual text");
            oCOMM.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addCOMMFrame(oCOMM);
            oID3V2_3_0Tag.addCOMMFrame(new COMMID3V2Frame("rus", "next description", "next actual text"));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>{COMM<bh:00><bh:00><bh:00>B<bh:00><bh:00><bh:01>eng<bh:ff><bh:fe>s<bh:00>h<bh:00>o<bh:00>r<bh:00>t<bh:00> <bh:00>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00><bh:00><bh:00><bh:ff><bh:fe>a<bh:00>c<bh:00>t<bh:00>u<bh:00>a<bh:00>l<bh:00> <bh:00>t<bh:00>e<bh:00>x<bh:00>t<bh:00>COMM<bh:00><bh:00><bh:00>%<bh:00><bh:00><bh:00>rusnext description<bh:00>next actual text";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testCOMRFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.setCOMRFrame(new COMRID3V2Frame("cad12.99",
                                                           "25250101",
                                                           "http://jid3.blinkenlights.org",
                                                           COMRID3V2Frame.RECEIVED_AS_FILE_OVER_THE_INTERNET,
                                                           "seller",
                                                           "description",
                                                           "image/png",
                                                           new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>]COMR<bh:00><bh:00><bh:00>S<bh:00><bh:00><bh:00>cad12.99<bh:00>25250101http://jid3.blinkenlights.org<bh:00><bh:03>seller<bh:00>description<bh:00>image/png<bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            COMRID3V2Frame oCOMR = new COMRID3V2Frame("cad12.99",
                                                      "25250101",
                                                      "http://jid3.blinkenlights.org",
                                                      COMRID3V2Frame.RECEIVED_AS_FILE_OVER_THE_INTERNET,
                                                      "seller",
                                                      "description",
                                                      "image/png",
                                                      new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 });
            oCOMR.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setCOMRFrame(oCOMR);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>tCOMR<bh:00><bh:00><bh:00>j<bh:00><bh:00><bh:01>cad12.99<bh:00>25250101http://jid3.blinkenlights.org<bh:00><bh:03><bh:ff><bh:fe>s<bh:00>e<bh:00>l<bh:00>l<bh:00>e<bh:00>r<bh:00><bh:00><bh:00><bh:ff><bh:fe>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00><bh:00><bh:00>image/png<bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testENCRFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.addENCRFrame(new ENCRID3V2Frame("http://jid3.blinkenlights.org", (byte)0x80, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>.ENCR<bh:00><bh:00><bh:00>$<bh:00><bh:00>http://jid3.blinkenlights.org<bh:00><bh:80><bh:01><bh:02><bh:03><bh:04><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
        
        // test with empty owner identifier
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oID3V2_3_0Tag.addENCRFrame(new ENCRID3V2Frame("", (byte)0x80, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:11>ENCR<bh:00><bh:00><bh:00><bh:07><bh:00><bh:00><bh:00><bh:80><bh:01><bh:02><bh:03><bh:04><bh:05>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testEQUAFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            EQUAID3V2Frame oEQUA = new EQUAID3V2Frame((byte)16);
            oEQUA.setAdjustment(oEQUA.new Adjustment(true, 16383, new byte[] { 0x01, 0x02 }));
            oEQUA.setAdjustment(oEQUA.new Adjustment(false, 32767, new byte[] { 0x03, 0x04 }));
            oID3V2_3_0Tag.setEQUAFrame(oEQUA);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:13>EQUA<bh:00><bh:00><bh:00><bh:09><bh:00><bh:00><bh:10><bh:7f><bh:ff><bh:03><bh:04><bh:bf><bh:ff><bh:01><bh:02>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testETCOFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            ETCOID3V2Frame oETCO = new ETCOID3V2Frame(ETCOID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS);
            oETCO.addEvent(new ETCOID3V2Frame.Event(ETCOID3V2Frame.EventType.END_OF_INITIAL_SILENCE, 12));
            oETCO.addEvent(new ETCOID3V2Frame.Event(ETCOID3V2Frame.EventType.OUTRO_START, 23));
            oID3V2_3_0Tag.setETCOFrame(oETCO);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:15>ETCO<bh:00><bh:00><bh:00><bh:0b><bh:00><bh:00><bh:02><bh:01><bh:00><bh:00><bh:00><bh:0c><bh:04><bh:00><bh:00><bh:00><bh:17>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testGEOBFrame()
    {
        try
        {
            // ISO-8559-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            GEOBID3V2Frame oGEOB = new GEOBID3V2Frame("image/png", "filename", "content description", new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 });
            oID3V2_3_0Tag.addGEOBFrame(oGEOB);
            oGEOB = new GEOBID3V2Frame("image/jpeg", "filename2", "another content description", new byte[] { 0x05, 0x04, 0x03, 0x02, 0x01 });
            oID3V2_3_0Tag.addGEOBFrame(oGEOB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>xGEOB<bh:00><bh:00><bh:00>7<bh:00><bh:00><bh:00>image/jpeg<bh:00>filename2<bh:00>another content description<bh:00><bh:05><bh:04><bh:03><bh:02><bh:01>GEOB<bh:00><bh:00><bh:00>-<bh:00><bh:00><bh:00>image/png<bh:00>filename<bh:00>content description<bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8559-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            GEOBID3V2Frame oGEOB = new GEOBID3V2Frame("image/png", "filename", "content description", new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 });
            oGEOB.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addGEOBFrame(oGEOB);
            oGEOB = new GEOBID3V2Frame("image/jpeg", "filename2", "another content description", new byte[] { 0x05, 0x04, 0x03, 0x02, 0x01 });
            oID3V2_3_0Tag.addGEOBFrame(oGEOB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:01><bh:19>GEOB<bh:00><bh:00><bh:00>7<bh:00><bh:00><bh:00>image/jpeg<bh:00>filename2<bh:00>another content description<bh:00><bh:05><bh:04><bh:03><bh:02><bh:01>GEOB<bh:00><bh:00><bh:00>N<bh:00><bh:00><bh:01>image/png<bh:00><bh:ff><bh:fe>f<bh:00>i<bh:00>l<bh:00>e<bh:00>n<bh:00>a<bh:00>m<bh:00>e<bh:00><bh:00><bh:00><bh:ff><bh:fe>c<bh:00>o<bh:00>n<bh:00>t<bh:00>e<bh:00>n<bh:00>t<bh:00> <bh:00>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>i<bh:00>o<bh:00>n<bh:00><bh:00><bh:00><bh:01><bh:02><bh:03><bh:04><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testGRIDFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            GRIDID3V2Frame oGRID = new GRIDID3V2Frame("http://jid3.blinkenlights.org", (byte)0x88, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 });
            oID3V2_3_0Tag.addGRIDFrame(oGRID);
            oGRID = new GRIDID3V2Frame("http://abcd.blinkenlights.org", (byte)0x22, new byte[] { 0x05, 0x04, 0x03, 0x02, 0x01 });
            oID3V2_3_0Tag.addGRIDFrame(oGRID);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:5c>GRID<bh:00><bh:00><bh:00>$<bh:00><bh:00>http://jid3.blinkenlights.org<bh:00><bh:88><bh:01><bh:02><bh:03><bh:04><bh:05>GRID<bh:00><bh:00><bh:00>$<bh:00><bh:00>http://abcd.blinkenlights.org<bh:00>\"<bh:05><bh:04><bh:03><bh:02><bh:01>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testIPLSFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            IPLSID3V2Frame oIPLS = new IPLSID3V2Frame();
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement1", "person1"));
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement1", "person2"));
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement2", "person3"));
            oID3V2_3_0Tag.setIPLSFrame(oIPLS);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>JIPLS<bh:00><bh:00><bh:00>@<bh:00><bh:00><bh:00>involvement1<bh:00>person1<bh:00>involvement1<bh:00>person2<bh:00>involvement2<bh:00>person3<bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }
        
        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            IPLSID3V2Frame oIPLS = new IPLSID3V2Frame();
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement1", "person1"));
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement1", "person2"));
            oIPLS.addInvolvedPerson(new IPLSID3V2Frame.InvolvedPerson("involvement2", "person3"));
            oIPLS.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setIPLSFrame(oIPLS);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:01><bh:15>IPLS<bh:00><bh:00><bh:00><bh:8b><bh:00><bh:00><bh:01><bh:ff><bh:fe>i<bh:00>n<bh:00>v<bh:00>o<bh:00>l<bh:00>v<bh:00>e<bh:00>m<bh:00>e<bh:00>n<bh:00>t<bh:00>1<bh:00><bh:00><bh:00><bh:ff><bh:fe>p<bh:00>e<bh:00>r<bh:00>s<bh:00>o<bh:00>n<bh:00>1<bh:00><bh:00><bh:00><bh:ff><bh:fe>i<bh:00>n<bh:00>v<bh:00>o<bh:00>l<bh:00>v<bh:00>e<bh:00>m<bh:00>e<bh:00>n<bh:00>t<bh:00>1<bh:00><bh:00><bh:00><bh:ff><bh:fe>p<bh:00>e<bh:00>r<bh:00>s<bh:00>o<bh:00>n<bh:00>2<bh:00><bh:00><bh:00><bh:ff><bh:fe>i<bh:00>n<bh:00>v<bh:00>o<bh:00>l<bh:00>v<bh:00>e<bh:00>m<bh:00>e<bh:00>n<bh:00>t<bh:00>2<bh:00><bh:00><bh:00><bh:ff><bh:fe>p<bh:00>e<bh:00>r<bh:00>s<bh:00>o<bh:00>n<bh:00>3<bh:00><bh:00><bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testLINKFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            LINKID3V2Frame oLINK = new LINKID3V2Frame("TAG1".getBytes(), "filelocation1", "additionaldata1");
            oID3V2_3_0Tag.addLINKFrame(oLINK);
            oLINK = new LINKID3V2Frame("TAG2".getBytes(), "filelocation2", "additionaldata2");
            oID3V2_3_0Tag.addLINKFrame(oLINK);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>VLINK<bh:00><bh:00><bh:00>!<bh:00><bh:00>TAG1filelocation1<bh:00>additionaldata1LINK<bh:00><bh:00><bh:00>!<bh:00><bh:00>TAG2filelocation2<bh:00>additionaldata2";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testMCDIFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            MCDIID3V2Frame oMCDI = new MCDIID3V2Frame(new byte[] { 0x01, 0x02, 0x03, 0x04 });
            oID3V2_3_0Tag.setMCDIFrame(oMCDI);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>MCDI<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:01><bh:02><bh:03><bh:04>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testMLLTFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            MLLTID3V2Frame oMLLT = new MLLTID3V2Frame(new byte[] { 0x01, 0x02, 0x03, 0x04 });
            oID3V2_3_0Tag.setMLLTFrame(oMLLT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>MLLT<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:01><bh:02><bh:03><bh:04>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testOWNEFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            OWNEID3V2Frame oOWNE = new OWNEID3V2Frame("cad12.34", "20000102", "seller");
            oID3V2_3_0Tag.setOWNEFrame(oOWNE);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>\"OWNE<bh:00><bh:00><bh:00><bh:18><bh:00><bh:00><bh:00>cad12.34<bh:00>20000102seller";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8559-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            OWNEID3V2Frame oOWNE = new OWNEID3V2Frame("cad12.34", "20000102", "seller");
            oOWNE.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setOWNEFrame(oOWNE);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>*OWNE<bh:00><bh:00><bh:00> <bh:00><bh:00><bh:01>cad12.34<bh:00>20000102<bh:ff><bh:fe>s<bh:00>e<bh:00>l<bh:00>l<bh:00>e<bh:00>r<bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testPRIVFrame()
    {
        try
        {
            ID3Tag.useStrict(true);
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            PRIVID3V2Frame oPRIV = new PRIVID3V2Frame("abcdefghijklmnopqrstuvwxyz", new byte[] { 0x01, 0x02, 0x03, 0x04 });
            oID3V2_3_0Tag.addPRIVFrame(oPRIV);
            try
            {
                oID3V2_3_0Tag.addPRIVFrame(oPRIV);
                fail("Adding the same PRIV frame to a tag twice should have generated an exception.");
            }
            catch (Exception e) {}
            oPRIV = new PRIVID3V2Frame("owner identifier", "private data".getBytes());
            oID3V2_3_0Tag.addPRIVFrame(oPRIV);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>PPRIV<bh:00><bh:00><bh:00><bh:1f><bh:00><bh:00>abcdefghijklmnopqrstuvwxyz<bh:00><bh:01><bh:02><bh:03><bh:04>PRIV<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00>owner identifier<bh:00>private data";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testPCNTFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            PCNTID3V2Frame oPCNT = new PCNTID3V2Frame(42);
            oID3V2_3_0Tag.setPCNTFrame(oPCNT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>PCNT<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:00><bh:00><bh:00>*";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testPOPMFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            POPMID3V2Frame oPOPM = new POPMID3V2Frame("user@domain.com", 42, 12345);
            oID3V2_3_0Tag.addPOPMFrame(oPOPM);
            oPOPM = new POPMID3V2Frame("user@otherdomain.com", 43);
            oID3V2_3_0Tag.addPOPMFrame(oPOPM);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>?POPM<bh:00><bh:00><bh:00><bh:15><bh:00><bh:00>user@domain.com<bh:00>*<bh:00><bh:00>09POPM<bh:00><bh:00><bh:00><bh:16><bh:00><bh:00>user@otherdomain.com<bh:00>+";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testPOSSFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            POSSID3V2Frame oPOSS = new POSSID3V2Frame(POSSID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, 12345);
            oID3V2_3_0Tag.setPOSSFrame(oPOSS);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>POSS<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:02><bh:00><bh:00>09";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testRBUFFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            RBUFID3V2Frame oRBUF = new RBUFID3V2Frame(123456, true, 42);
            oID3V2_3_0Tag.setRBUFFrame(oRBUF);
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:12>RBUF<bh:00><bh:00><bh:00><bh:08><bh:00><bh:00><bh:01><bh:e2>@<bh:01><bh:00><bh:00><bh:00>*";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
            
            oRBUF = new RBUFID3V2Frame(123456, false);
            oID3V2_3_0Tag.setRBUFFrame(oRBUF);
            sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>RBUF<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:01><bh:e2>@<bh:00>";
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testRVADFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            RVADID3V2Frame oRVAD = new RVADID3V2Frame(new byte[] { 0x01, 0x02, 0x03, 0x04 });
            oID3V2_3_0Tag.setRVADFrame(oRVAD);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0e>RVAD<bh:00><bh:00><bh:00><bh:04><bh:00><bh:00><bh:01><bh:02><bh:03><bh:04>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testRVRBFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            RVRBID3V2Frame oRVRB = new RVRBID3V2Frame(12345, 54321, 1, 2, 3, 4, 5, 6, 7, 8);
            oID3V2_3_0Tag.setRVRBFrame(oRVRB);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:16>RVRB<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>09<bh:d4>1<bh:01><bh:02><bh:03><bh:04><bh:05><bh:06><bh:07><bh:08>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testSYLTFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            SYLTID3V2Frame oSYLT = new SYLTID3V2Frame("eng", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, SYLTID3V2Frame.ContentType.LYRICS, "content descriptor");
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("one", 1));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("three", 3));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("two", 2));
            oID3V2_3_0Tag.addSYLTFrame(oSYLT);
            
            oSYLT = new SYLTID3V2Frame("rus", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MPEG_FRAMES, SYLTID3V2Frame.ContentType.TRIVIA, "another description");
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("abc", 4));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("def", 5));
            oID3V2_3_0Tag.addSYLTFrame(oSYLT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>qSYLT<bh:00><bh:00><bh:00>3<bh:00><bh:00><bh:00>eng<bh:02><bh:01>content descriptor<bh:00>one<bh:00><bh:00><bh:00><bh:00><bh:01>two<bh:00><bh:00><bh:00><bh:00><bh:02>three<bh:00><bh:00><bh:00><bh:00><bh:03>SYLT<bh:00><bh:00><bh:00>*<bh:00><bh:00><bh:00>rus<bh:01><bh:06>another description<bh:00>abc<bh:00><bh:00><bh:00><bh:00><bh:04>def<bh:00><bh:00><bh:00><bh:00><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            SYLTID3V2Frame oSYLT = new SYLTID3V2Frame("eng", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, SYLTID3V2Frame.ContentType.LYRICS, "content descriptor");
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("one", 1));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("three", 3));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("two", 2));
            oSYLT.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addSYLTFrame(oSYLT);
            
            oSYLT = new SYLTID3V2Frame("rus", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MPEG_FRAMES, SYLTID3V2Frame.ContentType.TRIVIA, "another description");
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("abc", 4));
            oSYLT.addSyncEntry(new SYLTID3V2Frame.SyncEntry("def", 5));
            oID3V2_3_0Tag.addSYLTFrame(oSYLT);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:01><bh:1a>SYLT<bh:00><bh:00><bh:00><bh:5c><bh:00><bh:00><bh:01>eng<bh:02><bh:01><bh:ff><bh:fe>c<bh:00>o<bh:00>n<bh:00>t<bh:00>e<bh:00>n<bh:00>t<bh:00> <bh:00>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>o<bh:00>r<bh:00><bh:00><bh:00><bh:ff><bh:fe>o<bh:00>n<bh:00>e<bh:00><bh:00><bh:00><bh:00><bh:00><bh:00><bh:01><bh:ff><bh:fe>t<bh:00>w<bh:00>o<bh:00><bh:00><bh:00><bh:00><bh:00><bh:00><bh:02><bh:ff><bh:fe>t<bh:00>h<bh:00>r<bh:00>e<bh:00>e<bh:00><bh:00><bh:00><bh:00><bh:00><bh:00><bh:03>SYLT<bh:00><bh:00><bh:00>*<bh:00><bh:00><bh:00>rus<bh:01><bh:06>another description<bh:00>abc<bh:00><bh:00><bh:00><bh:00><bh:04>def<bh:00><bh:00><bh:00><bh:00><bh:05>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testSYTCFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            SYTCID3V2Frame oSYTC = new SYTCID3V2Frame(SYTCID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS);
            oSYTC.addTempoChange(new SYTCID3V2Frame.TempoChange(12, 1));
            oSYTC.addTempoChange(new SYTCID3V2Frame.TempoChange(255, 2));
            oSYTC.addTempoChange(new SYTCID3V2Frame.TempoChange(510, 3));
            oID3V2_3_0Tag.setSYTCFrame(oSYTC);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:1c>SYTC<bh:00><bh:00><bh:00><bh:12><bh:00><bh:00><bh:02><bh:0c><bh:00><bh:00><bh:00><bh:01><bh:ff><bh:00><bh:00><bh:00><bh:00><bh:02><bh:ff><bh:ff><bh:00><bh:00><bh:00><bh:03>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testUFIDFrame()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            UFIDID3V2Frame oUFID = new UFIDID3V2Frame("owner identifier", new byte[] { 0x01, 0x02, 0x03, 0x04 });
            oID3V2_3_0Tag.addUFIDFrame(oUFID);
            
            oUFID = new UFIDID3V2Frame("owner two", new byte[] { 0x04, 0x03, 0x02, 0x01 });
            oID3V2_3_0Tag.addUFIDFrame(oUFID);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>7UFID<bh:00><bh:00><bh:00><bh:15><bh:00><bh:00>owner identifier<bh:00><bh:01><bh:02><bh:03><bh:04>UFID<bh:00><bh:00><bh:00><bh:0e><bh:00><bh:00>owner two<bh:00><bh:04><bh:03><bh:02><bh:01>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testUSERFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            USERID3V2Frame oUSER = new USERID3V2Frame("eng", "Terms of use.");
            oID3V2_3_0Tag.setUSERFrame(oUSER);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:1b>USER<bh:00><bh:00><bh:00><bh:11><bh:00><bh:00><bh:00>engTerms of use.";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("ISO-8859-1 test: " + ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            USERID3V2Frame oUSER = new USERID3V2Frame("eng", "Terms of use.");
            oUSER.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.setUSERFrame(oUSER);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>*USER<bh:00><bh:00><bh:00> <bh:00><bh:00><bh:01>eng<bh:ff><bh:fe>T<bh:00>e<bh:00>r<bh:00>m<bh:00>s<bh:00> <bh:00>o<bh:00>f<bh:00> <bh:00>u<bh:00>s<bh:00>e<bh:00>.<bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail("Unicode test: " + ID3Exception.getStackTrace(e));
        }
    }

    public void testUSLTFrame()
    {
        try
        {
            // ISO-8859-1 test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            USLTID3V2Frame oUSLT = new USLTID3V2Frame("eng", "content descriptor", "lyrics");
            oID3V2_3_0Tag.addUSLTFrame(oUSLT);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'USLT<bh:00><bh:00><bh:00><bh:1d><bh:00><bh:00><bh:00>engcontent descriptor<bh:00>lyrics";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }

        try
        {
            // Unicode test
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            USLTID3V2Frame oUSLT = new USLTID3V2Frame("eng", "content descriptor", "lyrics");
            oUSLT.setTextEncoding(TextEncoding.UNICODE);
            oID3V2_3_0Tag.addUSLTFrame(oUSLT);
            
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>DUSLT<bh:00><bh:00><bh:00>:<bh:00><bh:00><bh:01>eng<bh:ff><bh:fe>c<bh:00>o<bh:00>n<bh:00>t<bh:00>e<bh:00>n<bh:00>t<bh:00> <bh:00>d<bh:00>e<bh:00>s<bh:00>c<bh:00>r<bh:00>i<bh:00>p<bh:00>t<bh:00>o<bh:00>r<bh:00><bh:00><bh:00><bh:ff><bh:fe>l<bh:00>y<bh:00>r<bh:00>i<bh:00>c<bh:00>s<bh:00>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testUnsynchronization()
    {
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            PRIVID3V2Frame oPRIV = new PRIVID3V2Frame("owner", new byte[] { 0x01, 0x02, (byte)0xff, (byte)0xf0, 0x03, 0x04 });
            oID3V2_3_0Tag.addPRIVFrame(oPRIV);
            oID3V2_3_0Tag.setUnsynchronization(true);
            String sPrefix = "ID3<bh:03><bh:00><bh:80><bh:00><bh:00><bh:00><bh:17>PRIV<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>owner<bh:00><bh:01><bh:02><bh:ff><bh:00><bh:f0><bh:03><bh:04>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);  // unsynchronization should be applied
            
            oID3V2_3_0Tag.setUnsynchronization(false);
            sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:16>PRIV<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>owner<bh:00><bh:01><bh:02><bh:ff><bh:f0><bh:03><bh:04>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);  // no unsynchronization applied
            
            oID3V2_3_0Tag = new ID3V2_3_0Tag();
            oPRIV = new PRIVID3V2Frame("owner", new byte[] { 0x01, 0x02, (byte)0xff, (byte)0xf0, 0x03, (byte)0xff });
            oID3V2_3_0Tag.addPRIVFrame(oPRIV);
            oID3V2_3_0Tag.setUnsynchronization(true);
            sPrefix = "ID3<bh:03><bh:00><bh:80><bh:00><bh:00><bh:00><bh:18>PRIV<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>owner<bh:00><bh:01><bh:02><bh:ff><bh:00><bh:f0><bh:03><bh:ff><bh:00>";
            
            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);  // unsynchronization with special case when last byte 0xff
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testConveienceMethods()
    {
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            ID3V2Tag oID3V2Tag = new ID3V2_3_0Tag();
            oID3V2Tag.setArtist("Artist");
            oID3V2Tag.setTitle("Song Title");
            oID3V2Tag.setAlbum("Album");
            oID3V2Tag.setYear(2004);
            oID3V2Tag.setTrackNumber(3, 9);
            oID3V2Tag.setGenre("Blues");
            oMediaFile.setID3Tag(oID3V2Tag);
            oMediaFile.sync();
            
            // read file back
            oMediaFile = new MP3File(oSourceFile);
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            oID3V2Tag = (ID3V2Tag)aoID3Tag[0];    // there is only one set.. what we just wrote
            if ( ! oID3V2Tag.getArtist().equals("Artist"))
            {
                fail("Set/get artist by convenience methods do not return what was set.  Returned [" + oID3V2Tag.getArtist() + "]");
            }
            if ( ! oID3V2Tag.getTitle().equals("Song Title"))
            {
                fail("Set/get song title by convenience methods do not return what was set.  Returned [" +
                     oID3V2Tag.getTitle() + "]");
            }
            if ( ! oID3V2Tag.getAlbum().equals("Album"))
            {
                fail("Set/get album by convenience methods do not return what was set.  Returned [" + oID3V2Tag.getAlbum() + "]");
            }
            if (oID3V2Tag.getYear() != 2004)
            {
                fail("Set/get year by convenience methods do not return what was set.  Returned [" + oID3V2Tag.getYear() + "]");
            }
            if ((oID3V2Tag.getTrackNumber() != 3) || (oID3V2Tag.getTotalTracks() != 9))
            {
                fail("Set/get track number convenience methods do not return what was set.  Returned [" +
                     oID3V2Tag.getTrackNumber() + "/" + oID3V2Tag.getTotalTracks() + "]");
            }
            if ( ! oID3V2Tag.getGenre().equals("Blues"))
            {
                fail("Set/get genre convenience methods do not return what was set.  Returned [" +
                     oID3V2Tag.getGenre() + "]"); 
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testIFileSource()
    {
        try
        {
            // get a copy of a file with v2.3.0 tags to test reading
            ID3Util.copy(AllTests.s_RootPath + "v1_1tags.mp3", AllTests.s_RootPath + "id3_v1_1_tagtest.mp3");
            
            File oFile = new File(AllTests.s_RootPath + "id3_v1_1_tagtest.mp3");
            IFileSource oFileSource = new FileSource(oFile);
            MP3File oMP3File = new MP3File(oFileSource);
            
            ID3Tag oID3Tag = oMP3File.getID3V1Tag();
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testExtendedHeader()
    {
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            ID3V2Tag oID3V2Tag = new ID3V2_3_0Tag();
            oID3V2Tag.setArtist("Artist");
            
            // set extended header and CRC flags
            oID3V2Tag.setExtendedHeader(true);
            oID3V2Tag.setCRC(true);
            
            oID3V2Tag.setPaddingLength(5);
            
            oMediaFile.setID3Tag(oID3V2Tag);
            oMediaFile.sync();

            // read file back
            oMediaFile = new MP3File(oSourceFile);
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            oID3V2Tag = (ID3V2Tag)aoID3Tag[0];    // there is only one set.. what we just wrote
            if ( ! oID3V2Tag.getExtendedHeader())
            {
                fail("Set extended header before writing.  Not set on reading back.");
            }
            if ( ! oID3V2Tag.getCRC())
            {
                fail("Set CRC before writing.  Not set on reading back.");
            }
            if ( ! oID3V2Tag.getArtist().equals("Artist"))
            {
                fail("Unexpected artist value.  Returned [" + oID3V2Tag.getArtist() + "]");
            }
            
            // rewrite again with extended header but no CRC
            oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            oID3V2Tag = new ID3V2_3_0Tag();
            oID3V2Tag.setArtist("Artist");
            
            // set extended header and CRC flags
            oID3V2Tag.setExtendedHeader(true);
            
            oID3V2Tag.setPaddingLength(5);
            
            oMediaFile.setID3Tag(oID3V2Tag);
            oMediaFile.sync();

            // read file back
            oMediaFile = new MP3File(oSourceFile);
            aoID3Tag = oMediaFile.getTags();
            oID3V2Tag = (ID3V2Tag)aoID3Tag[0];    // there is only one set.. what we just wrote
            if ( ! oID3V2Tag.getExtendedHeader())
            {
                fail("Set extended header before writing.  Not set on reading back.");
            }
            if (oID3V2Tag.getCRC())
            {
                fail("Did not set CRC before writing.  CRC set on reading back.");
            }
            if ( ! oID3V2Tag.getArtist().equals("Artist"))
            {
                fail("Unexpected artist value.  Returned [" + oID3V2Tag.getArtist() + "]");
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    public void testFailOnZeroFrames()
    {
        try
        {
            // get a copy of an unmodified file to edit (although it is a failure if the file is edited in this test!)
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file
            ID3V2Tag oID3V2Tag = new ID3V2_3_0Tag();
            // but don't set any frames in it...
            
            oMediaFile.setID3Tag(oID3V2Tag);
            try
            {
                oMediaFile.sync();
            }
            catch (Exception e2)
            {
                // good, this was supposed to fail, because we tried to write a v2 tag with no frames
                return;
            }
            
            // if we were able to sync this file, we failed
            fail("We successfully sync'ed a v2 tag with no frames, when we should have failed.");
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testUseStrict()
    {
        try
        {
            // create an invalid frame (TYER with no value) to test strict and non-strict reading of it
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>TYER<bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>abcd";
            String sSourceFile = AllTests.s_RootPath + "id3_v2_3_0_invalid.mp3";
            FileInputStream oFIS = null;
            FileOutputStream oFOS = null;
            try
            {
                oFIS = new FileInputStream(AllTests.s_RootPath + "notags.mp3");
                oFOS = new FileOutputStream(sSourceFile);

                // start with invalid tag
                oFOS.write(ID3Util.convertFrhedToByteArray(sPrefix));
                // copy mp3 file over
                byte[] abyBuffer = new byte[16384];
                int iNumRead;
                while ((iNumRead = oFIS.read(abyBuffer)) != -1)
                {
                    oFOS.write(abyBuffer, 0, iNumRead);
                }
                oFOS.flush();
            }
            finally
            {
                try { oFIS.close(); } catch (Exception e) {}
                try { oFOS.close(); } catch (Exception e) {}
            }
            
            // now, try to read this file, non-strict
            ID3Tag.useStrict(false);
            MediaFile oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                ID3Tag[] aoID3Tag = oMediaFile.getTags();
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                if (oID3V2_3_0Tag.getTYERTextInformationFrame() != null)
                {
                    fail("The invalid TYER frame should have been ignored when read in non-strict mode.");
                }
            }
            catch (Exception e)
            {
                // we should not have caught an exception when strict reading is not set
                fail("With non-strict setting, reading this invalid file should not have generated an exception: " + ID3Exception.getStackTrace(e));
            }
            
            // try again, strict
            ID3Tag.useStrict(true);
            oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                oMediaFile.getTags();
                // we should not get this far
                fail("With strict setting, reading this invalid file should have generated an exception.");
            }
            catch (InvalidFrameID3Exception e)
            {
                // we should be here
            }
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testInvalidFrameId()
    {
        try
        {
            // create a frame with an invalid frame id ("COM ") to test strict and non-strict reading of it
            // (this test inspired by the illegal frames which iTunes adds to MP3 files)
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00><bh:0f>COM <bh:00><bh:00><bh:00><bh:05><bh:00><bh:00><bh:00>abcd";
            String sSourceFile = AllTests.s_RootPath + "id3_v2_3_0_invalid.mp3";
            FileInputStream oFIS = null;
            FileOutputStream oFOS = null;
            try
            {
                oFIS = new FileInputStream(AllTests.s_RootPath + "notags.mp3");
                oFOS = new FileOutputStream(sSourceFile);

                // start with invalid tag
                oFOS.write(ID3Util.convertFrhedToByteArray(sPrefix));
                // copy mp3 file over
                byte[] abyBuffer = new byte[16384];
                int iNumRead;
                while ((iNumRead = oFIS.read(abyBuffer)) != -1)
                {
                    oFOS.write(abyBuffer, 0, iNumRead);
                }
                oFOS.flush();
            }
            finally
            {
                try { oFIS.close(); } catch (Exception e) {}
                try { oFOS.close(); } catch (Exception e) {}
            }
            
            // now, try to read this file, non-strict
            ID3Tag.useStrict(false);
            MediaFile oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                ID3Tag[] aoID3Tag = oMediaFile.getTags();
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                UnknownID3V2Frame[] aoUnknownID3V2Frame = oID3V2_3_0Tag.getUnknownFrames();
                if (aoUnknownID3V2Frame.length != 1)
                {
                    fail("There should be one unknown frame in this tag when read in non-strict mode.");
                }
                if ( ! (new String(aoUnknownID3V2Frame[0].getFrameId()).equals("COM ")))
                {
                    fail("The one unknown frame in this tag should have a frame ID of 'COM '.");
                }
            }
            catch (Exception e)
            {
                // we should not have caught an exception when strict reading is not set
                fail("With non-strict setting, reading this invalid file should not have generated an exception: " + ID3Exception.getStackTrace(e));
            }
            
            // try again, strict
            ID3Tag.useStrict(true);
            oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                oMediaFile.getTags();
                // we should not get this far
                fail("With strict setting, reading this invalid file should have generated an exception.");
            }
            catch (InvalidFrameID3Exception e)
            {
                // we should be here
            }
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testWriteUnknownFrame()
    {
        ID3Tag.useStrict(false);
        
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v2.3.0 tag to file containing an unknown frame
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            byte[] abyFrameData = new byte[] { 0x01, 0x02, 0x03, 0x04 };
            UnknownID3V2Frame oUnknownID3V2Frame = new UnknownID3V2Frame("ABCD", abyFrameData);
            oID3V2_3_0Tag.addUnknownFrame(oUnknownID3V2Frame);
            
            oMediaFile.setID3Tag(oID3V2_3_0Tag);
            
            oMediaFile.sync();

            // read file back
            oMediaFile = new MP3File(oSourceFile);
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];    // there is only one set.. what we just wrote
            UnknownID3V2Frame[] aoUnknownID3V2Frame = oID3V2_3_0Tag.getUnknownFrames();
            if (aoUnknownID3V2Frame.length != 1)
            {
                fail("Expected to find one unknown frame after reading tag back.");
            }
            if ( ! oUnknownID3V2Frame.equals(aoUnknownID3V2Frame[0]))
            {
                fail("Unknown tag before and after writing are not identical.");
            }
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testReadRandomMP3s()
    {
        ID3Tag.useStrict(false);
        try
        {
            File oSourceDir = new File("c:/temp/mp3");
            recurseDirectoryForMP3s(oSourceDir);
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    private void recurseDirectoryForMP3s(File oDirectory)
        throws Exception
    {
        File[] aoMP3File = getMP3FileList(oDirectory);
        for (int i=0; i < aoMP3File.length; i++)
        {
            // mp3 file to read
            MP3File oMP3File = new MP3File(aoMP3File[i]);
            try
            {
                oMP3File.getTags(); // don't care to do anything with them.. this is just a test to see if reading fails
            }
            catch (Exception e)
            {
                throw new Exception("Failed reading MP3 tags from " + aoMP3File[i] + ".", e);
            }
        }
        File[] aoDirectory = getSubDirectories(oDirectory);
        for (int i=0; i < aoDirectory.length; i++)
        {
            // subdirectory to recurse into
            System.out.println("Recursing into subdirectory: " + aoDirectory[i].getAbsolutePath());
            recurseDirectoryForMP3s(aoDirectory[i]);
        }
    }
    
    private File[] getMP3FileList(File oDirectory)
    {
        File[] aoMP3File = oDirectory.listFiles(new FilenameFilter()
        {
            public boolean accept(File oFile, String sName)
            {
                return sName.toLowerCase().endsWith(".mp3");
            }
        });
        
        return aoMP3File;
    }
    
    private File[] getSubDirectories(File oDirectory)
    {
        File[] aoDirectory = oDirectory.listFiles(new FilenameFilter()
        {
            public boolean accept(File oFile, String sName)
            {
                File oPotential = new File(oFile, sName);
                return oPotential.isDirectory();
            }
        });
        
        return aoDirectory;
    }
    
    public void testReadDuplicateFrames()
    {
        try
        {
            // create a tag with duplicate frames in violation of the spec, to test strict and non-strict reading of it
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>8COMM<bh:00><bh:00><bh:00><bh:12><bh:00><bh:00><bh:00>engabcd<bh:00>Comment 1COMM<bh:00><bh:00><bh:00><bh:12><bh:00><bh:00><bh:00>engabcd<bh:00>Comment 2";
            String sSourceFile = AllTests.s_RootPath + "id3_v2_3_0_duplicate.mp3";
            FileInputStream oFIS = null;
            FileOutputStream oFOS = null;
            try
            {
                oFIS = new FileInputStream(AllTests.s_RootPath + "notags.mp3");
                oFOS = new FileOutputStream(sSourceFile);

                // start with invalid tag
                oFOS.write(ID3Util.convertFrhedToByteArray(sPrefix));
                // copy mp3 file over
                byte[] abyBuffer = new byte[16384];
                int iNumRead;
                while ((iNumRead = oFIS.read(abyBuffer)) != -1)
                {
                    oFOS.write(abyBuffer, 0, iNumRead);
                }
                oFOS.flush();
            }
            finally
            {
                try { oFIS.close(); } catch (Exception e) {}
                try { oFOS.close(); } catch (Exception e) {}
            }
            
            // now, try to read this file, non-strict
            ID3Tag.useStrict(false);
            MediaFile oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                ID3Tag[] aoID3Tag = oMediaFile.getTags();
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                COMMID3V2Frame[] aoCOMMID3V2Frame = oID3V2_3_0Tag.getCOMMFrames();
                if (aoCOMMID3V2Frame.length != 1)
                {
                    fail("In non-strict mode, exactly one of the duplicate COMM frames in this file should have been kept.");
                }
                if ( ! aoCOMMID3V2Frame[0].getActualText().equals("Comment 2"))
                {
                    fail("In non-strict mode, when duplicate COMM frames are ignored, the last one read should be kept.");
                }
            }
            catch (Exception e)
            {
                // we should not have caught an exception when strict reading is not set
                fail("With non-strict setting, reading this invalid file should not have generated an exception: " + ID3Exception.getStackTrace(e));
            }
            
            // try again, strict
            ID3Tag.useStrict(true);
            oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                oMediaFile.getTags();
                // we should not get this far
                fail("With strict setting, reading a file with duplicate COMM frames should have generated an exception.");
            }
            catch (ID3Exception e)
            {
                // we should be here
            }
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public void testFrameConflicts()
    {
        ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();

        // AENC test
        AENCID3V2Frame oAENC1 = null, oAENC2 = null, oAENC3 = null;
        try
        {
            oAENC1 = new AENCID3V2Frame("owner1", 1, 2, new byte[] { 0x01 });
            oID3V2_3_0Tag.addAENCFrame(oAENC1);

            oAENC2 = new AENCID3V2Frame("owner2", 3, 4, new byte[] { 0x02 });
            oID3V2_3_0Tag.addAENCFrame(oAENC2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two AENC frames.");
        }
        try
        {
            oAENC2.setOwnerIdentifier("owner1");
            
            fail("We should not have been able to set two AENC frames with the same owner identifier.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was an AENC frame with owner identifier "owner1", and we
            // tried to set another with the same value (each AENC frame in a tag must have a unique owner identifier)
        }
        try
        {
            oAENC1.setOwnerIdentifier("owner3");

            oAENC3 = new AENCID3V2Frame("owner3", 1, 2, new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third AENC frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addAENCFrame(oAENC3);
            
            fail("We should not have been able to add a new AENC frame with the same owner identifier as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the owner identifier of oAENC1 to "owner3", we should not be
            // able to add a new AENC frame with owner identifier "owner3"
        }
        
        // APIC test
        APICID3V2Frame oAPIC1 = null, oAPIC2 = null, oAPIC3 = null;
        try
        {
            oAPIC1 = new APICID3V2Frame("image/gif", APICID3V2Frame.PictureType.Artist, "artist1", new byte[] { 0x01 });
            oID3V2_3_0Tag.addAPICFrame(oAPIC1);
            
            oAPIC2 = new APICID3V2Frame("image/gif", APICID3V2Frame.PictureType.Artist, "artist2", new byte[] { 0x02 });
            oID3V2_3_0Tag.addAPICFrame(oAPIC2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two APIC frames.");
        }
        try
        {
            oAPIC2.setDescription("artist1");
            
            fail("We should not have been able to set two APIC frames with the same description.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was an APIC frame with description "artist1", and we
            // tried to set another with the same value (each APIC frame in a tag must have a unique description)
        }
        try
        {
            oAPIC1.setDescription("artist3");
            
            oAPIC3 = new APICID3V2Frame("image/gif", APICID3V2Frame.PictureType.Artist, "artist3", new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third APIC frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addAPICFrame(oAPIC3);

            fail("We should not have been able to add a new APIC frame with the same description as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the description of oAPIC1 to "artist3", we should not be
            // able to add a new APIC frame with owner identifier "artist3"
        }
        
        // COMM test
        COMMID3V2Frame oCOMM1 = null, oCOMM2 = null, oCOMM3 = null;
        try
        {
            oCOMM1 = new COMMID3V2Frame("eng", "short1", "Comment 1.");
            oID3V2_3_0Tag.addCOMMFrame(oCOMM1);
            
            oCOMM2 = new COMMID3V2Frame("eng", "short2", "Comment 2.");
            oID3V2_3_0Tag.addCOMMFrame(oCOMM2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two COMM frames.");
        }
        try
        {
            oCOMM2.setComment("eng", "short1", "Comment 2.");
            
            fail("We should not have been able to set two COMM frames with the same language and short description.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a COMM frame with language "eng" and short description
            // "short1", and we tried to set another with the same value
        }
        try
        {
            oCOMM1.setComment("eng", "short3", "Comment 1.");
            
            oCOMM3 = new COMMID3V2Frame("eng", "short3", "Comment 3.");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third COMM frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addCOMMFrame(oCOMM3);

            fail("We should not have been able to add a new COMM frame with the same language and short description as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the short description of oCOMM1 to "short3", we should not be
            // able to add a new COMM frame with the same language "eng" and short description "short3"
        }
        
        // ENCR test
        ENCRID3V2Frame oENCR1 = null, oENCR2 = null, oENCR3 = null;
        try
        {
            oENCR1 = new ENCRID3V2Frame("owner1", (byte)0x81, new byte[] { 0x01 });
            oID3V2_3_0Tag.addENCRFrame(oENCR1);
            
            oENCR2 = new ENCRID3V2Frame("owner2", (byte)0x82, new byte[] { 0x02 });
            oID3V2_3_0Tag.addENCRFrame(oENCR2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two ENCR frames.");
        }
        try
        {
            oENCR2.setEncryptionDetails("owner1", (byte)0x81, new byte[] { 0x01 });
            
            fail("We should not have been able to set two ENCR frames with the same method symbol and owner identifier.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was an ENCR frame with method symbol 0x81 and owner identifier
            // "owner1", and we tried to set another with the same values
        }
        try
        {
            oENCR1.setEncryptionDetails("owner3", (byte)0x81, new byte[] { 0x01 });
            
            oENCR3 = new ENCRID3V2Frame("owner3", (byte)0x83, new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third ENCR frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addENCRFrame(oENCR3);

            fail("We should not have been able to add a new ENCR frame with the same owner identifier as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the owner identifier of oENCR1 to "owner3", we should not be
            // able to add a new ENCR frame with the same owner identifier
        }
        try
        {
            oENCR1.setEncryptionDetails("owner1", (byte)0x81, new byte[] { 0x01 });
            
            oENCR3 = new ENCRID3V2Frame("owner3", (byte)0x81, new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and recreate the third ENCR frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addENCRFrame(oENCR3);

            fail("We should not have been able to add a new ENCR frame with the same method symbol as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the method symbol of oENCR1 to 0x81, we should not be
            // able to add a new ENCR frame with the same method symbol
        }

        // GEOB test
        GEOBID3V2Frame oGEOB1 = null, oGEOB2 = null, oGEOB3 = null;
        try
        {
            oGEOB1 = new GEOBID3V2Frame("text/html", "filename1.html", "contentdescription1", new byte[] { 0x01 });
            oID3V2_3_0Tag.addGEOBFrame(oGEOB1);
            
            oGEOB2 = new GEOBID3V2Frame("text/html", "filename2.html", "contentdescription2", new byte[] { 0x02 });
            oID3V2_3_0Tag.addGEOBFrame(oGEOB2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two GEOB frames.");
        }
        try
        {
            oGEOB2.setEncapsulatedObject("text/html", "filename2.html", "contentdescription1", new byte[] { 0x02 });
            
            fail("We should not have been able to set two GEOB frames with the same content description.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a GEOB frame with content description
            // "contentdescription1", and we tried to set another with the same value
        }
        try
        {
            oGEOB1.setEncapsulatedObject("text/html", "filename1.html", "contentdescription3", new byte[] { 0x01 });
            
            oGEOB3 = new GEOBID3V2Frame("text/html", "filename3.html", "contentdescription3", new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third GEOB frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addGEOBFrame(oGEOB3);

            fail("We should not have been able to add a new GEOB frame with the same content description as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the content description of oGEOB1 to "contentdescription3",
            // we should not be able to add a new GEOB frame with the same content description
        }

        // GRID test
        GRIDID3V2Frame oGRID1 = null, oGRID2 = null, oGRID3 = null;
        try
        {
            oGRID1 = new GRIDID3V2Frame("owner1", (byte)0x01, new byte[] { 0x01 });
            oID3V2_3_0Tag.addGRIDFrame(oGRID1);
            
            oGRID2 = new GRIDID3V2Frame("owner2", (byte)0x02, new byte[] { 0x02 });
            oID3V2_3_0Tag.addGRIDFrame(oGRID2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two GRID frames.");
        }
        try
        {
            oGRID2.setGroupIdentificationRegistration("owner1", (byte)0x01, new byte[] { 0x02 });
            
            fail("We should not have been able to set two GRID frames with the same group symbol and owner identifier.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a GRID frame with method symbol 0x01 and owner identifier
            // "owner1", and we tried to set another with the same values
        }
        try
        {
            oGRID1.setGroupIdentificationRegistration("owner3", (byte)0x01, new byte[] { 0x01 });
            
            oGRID3 = new GRIDID3V2Frame("owner3", (byte)0x03, new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third GRID frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addGRIDFrame(oGRID3);

            fail("We should not have been able to add a new GRID frame with the same owner identifier as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the owner identifier of oGRID1 to "owner3", we should not be
            // able to add a new GRID frame with the same owner identifier
        }
        try
        {
            oGRID1.setGroupIdentificationRegistration("owner1", (byte)0x01, new byte[] { 0x01 });
            
            oGRID3 = new GRIDID3V2Frame("owner3", (byte)0x01, new byte[] { 0x03 });
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and recreate the third GRID frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addGRIDFrame(oGRID3);

            fail("We should not have been able to add a new GRID frame with the same method symbol as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the method symbol of oGRID1 to 0x01, we should not be
            // able to add a new GRID frame with the same method symbol
        }

        // LINK test
        LINKID3V2Frame oLINK1 = null, oLINK2 = null, oLINK3 = null;
        try
        {
            oLINK1 = new LINKID3V2Frame("ABCD".getBytes(), "http://www.a.com", "additional1");
            oID3V2_3_0Tag.addLINKFrame(oLINK1);
            
            oLINK2 = new LINKID3V2Frame("EFGH".getBytes(), "http://www.b.com", "additional2");
            oID3V2_3_0Tag.addLINKFrame(oLINK2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two LINK frames.");
        }
        try
        {
            oLINK2.setContents("ABCD".getBytes(), "http://www.a.com", "additional1");
            
            fail("We should not have been able to set two LINK frames with the same contents.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a LINK frame with the same contents
        }
        try
        {
            oLINK1.setContents("IJKL".getBytes(), "http://www.c.com", "additional3");
            
            oLINK3 = new LINKID3V2Frame("IJKL".getBytes(), "http://www.c.com", "additional3");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third LINK frmaes.");
        }
        try
        {
            oID3V2_3_0Tag.addLINKFrame(oLINK3);

            fail("We should not have been able to add a new LINK frame with the same contents as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the content description of oLINK1,
            // we should not be able to add a new LINK frame with the same contents
        }

        // POPM test
        POPMID3V2Frame oPOPM1 = null, oPOPM2 = null, oPOPM3 = null;
        try
        {
            oPOPM1 = new POPMID3V2Frame("a@b.com", 1, 1);
            oID3V2_3_0Tag.addPOPMFrame(oPOPM1);
            
            oPOPM2 = new POPMID3V2Frame("b@c.com", 2, 2);
            oID3V2_3_0Tag.addPOPMFrame(oPOPM2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two POPM frames.");
        }
        try
        {
            oPOPM2.setPopularity("a@b.com", 2, 2);
            
            fail("We should not have been able to set two POPM frames with the same email address.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a POPM frame with the same email address
        }
        try
        {
            oPOPM1.setPopularity("c@d.com", 3, 3);
            
            oPOPM3 = new POPMID3V2Frame("c@d.com", 3, 3);
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third POPM frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addPOPMFrame(oPOPM3);

            fail("We should not have been able to add a new POPM frame with the same email address as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the email address of oPOPM1,
            // we should not be able to add a new POPM frame with the same email address
        }
        
        // PRIV test
        PRIVID3V2Frame oPRIV1 = null, oPRIV2 = null, oPRIV3 = null;
        try
        {
            oPRIV1 = new PRIVID3V2Frame("http://www.a.com", "additional1".getBytes());
            oID3V2_3_0Tag.addPRIVFrame(oPRIV1);
            
            oPRIV2 = new PRIVID3V2Frame("http://www.b.com", "additional2".getBytes());
            oID3V2_3_0Tag.addPRIVFrame(oPRIV2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two PRIV frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oPRIV2.setPrivateInformation("http://www.a.com", "additional1".getBytes());
            
            fail("We should not have been able to set two PRIV frames with the same contents.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a PRIV frame with the same contents
        }
        try
        {
            oPRIV1.setPrivateInformation("http://www.c.com", "additional3".getBytes());
            
            oPRIV3 = new PRIVID3V2Frame("http://www.c.com", "additional3".getBytes());
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third PRIV frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addPRIVFrame(oPRIV3);

            fail("We should not have been able to add a new PRIV frame with the same contents as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the contents of oPRIV1,
            // we should not be able to add a new PRIV frame with the same contents
        }

        // SYLT test
        SYLTID3V2Frame oSYLT1 = null, oSYLT2 = null, oSYLT3 = null;
        try
        {
            oSYLT1 = new SYLTID3V2Frame("eng", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, SYLTID3V2Frame.ContentType.CHORD, "contentdescriptor1");
            oID3V2_3_0Tag.addSYLTFrame(oSYLT1);
            
            oSYLT2 = new SYLTID3V2Frame("eng", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, SYLTID3V2Frame.ContentType.CHORD, "contentdescriptor2");
            oID3V2_3_0Tag.addSYLTFrame(oSYLT2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two SYLT frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oSYLT2.setContentDescriptor("contentdescriptor1");
            
            fail("We should not have been able to set two SYLT frames with the same language and content descriptor.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a SYLT frame with the same language and content descriptor
        }
        try
        {
            oSYLT1.setContentDescriptor("contentdescriptor3");
            
            oSYLT3 = new SYLTID3V2Frame("eng", SYLTID3V2Frame.TimestampFormat.ABSOLUTE_MILLISECONDS, SYLTID3V2Frame.ContentType.CHORD, "contentdescriptor3");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third SYLT frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addSYLTFrame(oSYLT3);

            fail("We should not have been able to add a new SYLT frame with the same language and content descriptor as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the content descriptor of oSYLT1,
            // we should not be able to add a new SYLT frame with the same language and content descriptor
        }
        
        // TXXX test
        TXXXTextInformationID3V2Frame oTXXX1 = null, oTXXX2 = null, oTXXX3 = null;
        try
        {
            oTXXX1 = new TXXXTextInformationID3V2Frame("description1", "information1");
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX1);
            
            oTXXX2 = new TXXXTextInformationID3V2Frame("description2", "information2");
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two TXXX frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oTXXX2.setDescriptionAndInformation("description1", "information2");
            
            fail("We should not have been able to set two TXXX frames with the same description.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a TXXX frame with the same description
        }
        try
        {
            oTXXX1.setDescriptionAndInformation("description3", "information1");
            
            oTXXX3 = new TXXXTextInformationID3V2Frame("description3", "information3");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third TXXX frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addTXXXTextInformationFrame(oTXXX3);

            fail("We should not have been able to add a new TXXX frame with the same description as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the description of oTXXX1,
            // we should not be able to add a new TXXX frame with the same description
        }

        // UFID test
        UFIDID3V2Frame oUFID1 = null, oUFID2 = null, oUFID3 = null;
        try
        {
            oUFID1 = new UFIDID3V2Frame("owner1", "identifier1".getBytes());
            oID3V2_3_0Tag.addUFIDFrame(oUFID1);
            
            oUFID2 = new UFIDID3V2Frame("owner2", "identifier2".getBytes());
            oID3V2_3_0Tag.addUFIDFrame(oUFID2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two UFID frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oUFID2.setUniqueIdentifier("owner1", "identifier2".getBytes());
            
            fail("We should not have been able to set two UFID frames with the same owner identifier.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a UFID frame with the same owner identifier
        }
        try
        {
            oUFID1.setUniqueIdentifier("owner3", "identifier1".getBytes());
            
            oUFID3 = new UFIDID3V2Frame("owner3", "identifier3".getBytes());
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third UFID frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addUFIDFrame(oUFID3);

            fail("We should not have been able to add a new UFID frame with the same owner identifier as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the content descriptor of oUFID1,
            // we should not be able to add a new UFID frame with the same owner identifier
        }

        // USLT test
        USLTID3V2Frame oUSLT1 = null, oUSLT2 = null, oUSLT3 = null;
        try
        {
            oUSLT1 = new USLTID3V2Frame("eng", "contentdescriptor1", "lyrics1");
            oID3V2_3_0Tag.addUSLTFrame(oUSLT1);
            
            oUSLT2 = new USLTID3V2Frame("eng", "contentdescriptor2", "lyrics2");
            oID3V2_3_0Tag.addUSLTFrame(oUSLT2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two USLT frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oUSLT2.setContentDescriptor("contentdescriptor1");
            
            fail("We should not have been able to set two USLT frames with the same language and content descriptor.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a USLT frame with the same language and content descriptor
        }
        try
        {
            oUSLT1.setContentDescriptor("contentdescriptor3");
            
            oUSLT3 = new USLTID3V2Frame("eng", "contentdescriptor3", "lyrics3");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third USLT frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addUSLTFrame(oUSLT3);

            fail("We should not have been able to add a new USLT frame with the same language and content descriptor as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the content descriptor of oUSLT1,
            // we should not be able to add a new USLT frame with the same language and content descriptor
        }

        // WCOM test
        WCOMUrlLinkID3V2Frame oWCOM1 = null, oWCOM2 = null, oWCOM3 = null;
        try
        {
            oWCOM1 = new WCOMUrlLinkID3V2Frame("http://www.a.com");
            oID3V2_3_0Tag.addWCOMUrlLinkFrame(oWCOM1);
            
            oWCOM2 = new WCOMUrlLinkID3V2Frame("http://www.b.com");
            oID3V2_3_0Tag.addWCOMUrlLinkFrame(oWCOM2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two WCOM frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oWCOM2.setCommercialInformation("http://www.a.com");
            
            fail("We should not have been able to set two WCOM frames with the same URL.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a WCOM frame with the same URL
        }
        try
        {
            oWCOM1.setCommercialInformation("http://www.c.com");
            
            oWCOM3 = new WCOMUrlLinkID3V2Frame("http://www.c.com");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third WCOM frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addWCOMUrlLinkFrame(oWCOM3);

            fail("We should not have been able to add a new WCOM frame with the same URL as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the URL of oWCOM1,
            // we should not be able to add a new WCOM frame with the same URL
        }

        // WOAR test
        WOARUrlLinkID3V2Frame oWOAR1 = null, oWOAR2 = null, oWOAR3 = null;
        try
        {
            oWOAR1 = new WOARUrlLinkID3V2Frame("http://www.a.com");
            oID3V2_3_0Tag.addWOARUrlLinkFrame(oWOAR1);
            
            oWOAR2 = new WOARUrlLinkID3V2Frame("http://www.b.com");
            oID3V2_3_0Tag.addWOARUrlLinkFrame(oWOAR2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two WOAR frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oWOAR2.setOfficialArtistWebPage("http://www.a.com");
            
            fail("We should not have been able to set two WOAR frames with the same URL.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a WOAR frame with the same URL
        }
        try
        {
            oWOAR1.setOfficialArtistWebPage("http://www.c.com");
            
            oWOAR3 = new WOARUrlLinkID3V2Frame("http://www.c.com");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third WOAR frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addWOARUrlLinkFrame(oWOAR3);

            fail("We should not have been able to add a new WOAR frame with the same URL as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the URL of oWOAR1,
            // we should not be able to add a new WOAR frame with the same URL
        }

        // WXXX test
        WXXXUrlLinkID3V2Frame oWXXX1 = null, oWXXX2 = null, oWXXX3 = null;
        try
        {
            oWXXX1 = new WXXXUrlLinkID3V2Frame("description1", "http://www.a.com");
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX1);
            
            oWXXX2 = new WXXXUrlLinkID3V2Frame("description2", "http://www.b.com");
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX2);
        }
        catch (Exception e)
        {
            fail("We should have been able to create the first two WXXX frames: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oWXXX2.setDescriptionAndUrl("description1", "http://www.b.com");
            
            fail("We should not have been able to set two WXXX frames with the same description.");
        }
        catch (Exception e)
        {
            // we should be here.. because there already was a WXXX frame with the same description
        }
        try
        {
            oWXXX1.setDescriptionAndUrl("description3", "http://www.a.com");
            
            oWXXX3 = new WXXXUrlLinkID3V2Frame("description3", "http://www.c.com");
        }
        catch (Exception e)
        {
            fail("We should have been able to modify the first and create the third WXXX frmaes: " + ID3Exception.getStackTrace(e));
        }
        try
        {
            oID3V2_3_0Tag.addWXXXUrlLinkFrame(oWXXX3);

            fail("We should not have been able to add a new WXXX frame with the same description as an existing frame.");
        }
        catch (Exception e)
        {
            // we should be here.. because after we changed the URL of oWXXX1,
            // we should not be able to add a new WXXX frame with the same description
        }
    }
    
    /** Test with encryption that leaves the byte array the same length. */
    public void testEncryption1()
    {
        ROTCryptoAgent oROTAgent = new ROTCryptoAgent();
        ID3Encryption.getInstance().registerCryptoAgent(oROTAgent);
        
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            
            ENCRID3V2Frame oENCR = new ENCRID3V2Frame(oROTAgent.getOwnerIdentifier(), (byte)0x89, new byte[] { 0x01 });
            oID3V2_3_0Tag.addENCRFrame(oENCR);
            
            TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("hello");
            oTPE1.setEncryption((byte)0x89);
            oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'ENCR<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>jid3-rot1<bh:00><bh:89><bh:01>TPE1<bh:00><bh:00><bh:00><bh:07><bh:00>@<bh:89><bh:01>ifmmp";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    /** Test with encryption that changes the length of the byte array. */
    public void testEncryption2()
    {
        ExpandCryptoAgent oExpandAgent = new ExpandCryptoAgent();
        ID3Encryption.getInstance().registerCryptoAgent(oExpandAgent);
        
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            
            ENCRID3V2Frame oENCR = new ENCRID3V2Frame(oExpandAgent.getOwnerIdentifier(), (byte)0x89, new byte[] { 0x01 });
            oID3V2_3_0Tag.addENCRFrame(oENCR);
            
            TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("hello");
            oTPE1.setEncryption((byte)0x89);
            oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>6ENCR<bh:00><bh:00><bh:00><bh:09><bh:00><bh:00>expand<bh:00><bh:89><bh:01>TPE1<bh:00><bh:00><bh:00><bh:19><bh:00>@<bh:89><bh:00><bh:00><bh:00><bh:00><bh:00><bh:00><bh:00>h<bh:00><bh:00><bh:00>e<bh:00><bh:00><bh:00>l<bh:00><bh:00><bh:00>l<bh:00><bh:00><bh:00>o";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public void testEncryptionWithCompression()
    {
        ROTCryptoAgent oROTAgent = new ROTCryptoAgent();
        ID3Encryption.getInstance().registerCryptoAgent(oROTAgent);
        
        try
        {
            ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
            
            ENCRID3V2Frame oENCR = new ENCRID3V2Frame(oROTAgent.getOwnerIdentifier(), (byte)0x89, new byte[] { 0x01 });
            oID3V2_3_0Tag.addENCRFrame(oENCR);
            
            TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("hello");
            oTPE1.setEncryption((byte)0x89);
            oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);
            oTPE1.setCompressionFlag(true);

            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>3ENCR<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>jid3-rot1<bh:00><bh:89><bh:01>TPE1<bh:00><bh:00><bh:00><bh:13><bh:00><bh:c0><bh:00><bh:00><bh:00><bh:06><bh:89>y<bh:9d>d<bh:c9>I<bh:ce><bh:ca><bh:ca><bh:08><bh:01><bh:07>.<bh:03><bh:16>";

            runTagVerifyTest(oID3V2_3_0Tag, sPrefix);
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    public void testEncryptionNotSupported()
    {
        try
        {
            // create a tag with an encrypted TPE1 frame that has no registered agent
            String sPrefix = "ID3<bh:03><bh:00><bh:00><bh:00><bh:00><bh:00>'ENCR<bh:00><bh:00><bh:00><bh:0c><bh:00><bh:00>jid3-????<bh:00><bh:89><bh:01>TPE1<bh:00><bh:00><bh:00><bh:06><bh:00>@<bh:89><bh:01>ifmmp";
            String sSourceFile = AllTests.s_RootPath + "id3_v2_3_0_encrypted.mp3";
            FileInputStream oFIS = null;
            FileOutputStream oFOS = null;
            try
            {
                oFIS = new FileInputStream(AllTests.s_RootPath + "notags.mp3");
                oFOS = new FileOutputStream(sSourceFile);

                // start with invalid tag
                oFOS.write(ID3Util.convertFrhedToByteArray(sPrefix));
                // copy mp3 file over
                byte[] abyBuffer = new byte[16384];
                int iNumRead;
                while ((iNumRead = oFIS.read(abyBuffer)) != -1)
                {
                    oFOS.write(abyBuffer, 0, iNumRead);
                }
                oFOS.flush();
            }
            finally
            {
                try { oFIS.close(); } catch (Exception e) {}
                try { oFOS.close(); } catch (Exception e) {}
            }
            
            // now, try to read this file, non-strict
            ID3Tag.useStrict(false);
            MediaFile oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                ID3Tag[] aoID3Tag = oMediaFile.getTags();
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                TPE1TextInformationID3V2Frame oTPE1 = oID3V2_3_0Tag.getTPE1TextInformationFrame();
                if (oTPE1 != null)
                {
                    fail("In non-strict mode, we should not have been able to read a frame encrypted with a method that is not registered.");
                }
                EncryptedID3V2Frame[] aoEncrypted = oID3V2_3_0Tag.getEncryptedFrames();
                if (aoEncrypted.length != 1)
                {
                    fail("In non-strict mode, we should be able to access a frame still encrypted with a method that is not registered.");
                }
                if ( ! (new String(aoEncrypted[0].getEncryptedFrameId()).equals("TPE1")))
                {
                    fail("In non-strict mode, the encrypted frame we read should have been a TPE1 frame.");
                }
            }
            catch (Exception e)
            {
                fail("With non-strict setting, reading this file with an unsupported encrypted frame should not have generated an exception: " + ID3Exception.getStackTrace(e));
            }
            
            // now, try to read this file, strict
            ID3Tag.useStrict(true);
            oMediaFile = new MP3File(new File(sSourceFile));
            try
            {
                ID3Tag[] aoID3Tag = oMediaFile.getTags();
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[0];
                TPE1TextInformationID3V2Frame oTPE1 = oID3V2_3_0Tag.getTPE1TextInformationFrame();
                if (oTPE1 != null)
                {
                    fail("In strict mode, we should not have been able to read a frame encrypted with a method that is not registered.");
                }
                EncryptedID3V2Frame[] aoEncrypted = oID3V2_3_0Tag.getEncryptedFrames();
                if (aoEncrypted.length != 1)
                {
                    fail("In strict mode, we should be able to access a frame still encrypted with a method that is not registered.");
                }
                if ( ! (new String(aoEncrypted[0].getEncryptedFrameId()).equals("TPE1")))
                {
                    fail("In strict mode, the encrypted frame we read should have been a TPE1 frame.");
                }
            }
            catch (Exception e)
            {
                fail("With strict setting, reading this file with an unsupported encrypted frame should not have generated an exception: " + ID3Exception.getStackTrace(e));
            }
        }
        catch (Exception e)
        {
            fail("Unexpected " + e.getClass().getName() + " exception: " + ID3Exception.getStackTrace(e));
        }
    }
    
    public class ROTCryptoAgent implements ICryptoAgent
    {
        public byte[] decrypt(byte[] abyEncryptedData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            int iDiff = abyEncryptionData[0];
            byte[] abyDecrypted = new byte[abyEncryptedData.length];
            
            for (int i=0; i < abyEncryptedData.length; i++)
            {
                abyDecrypted[i] = (byte)(abyEncryptedData[i] - iDiff);
            }
            
            return abyDecrypted;
        }
        
        public byte[] encrypt(byte[] abyRawData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            int iDiff = abyEncryptionData[0];
            byte[] abyEncrypted = new byte[abyRawData.length];
            
            for (int i=0; i < abyRawData.length; i++)
            {
                abyEncrypted[i] = (byte)(abyRawData[i] + iDiff);
            }
            
            return abyEncrypted;
        }
        
        public String getOwnerIdentifier()
        {
            return "jid3-rot1";
        }
    }
    
    public class ExpandCryptoAgent implements ICryptoAgent
    {
        public byte[] decrypt(byte[] abyEncryptedData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            ByteArrayInputStream oBAIS = new ByteArrayInputStream(abyEncryptedData);
            ID3DataInputStream oIDIS = new ID3DataInputStream(oBAIS);
            ByteArrayOutputStream oBAOS = new ByteArrayOutputStream();

            try
            {
                while (oIDIS.available() > 0)
                {
                    oBAOS.write(oIDIS.readBE32());
                }
            }
            catch (IOException e) {}
            
            return oBAOS.toByteArray();
        }
        
        public byte[] encrypt(byte[] abyRawData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            ByteArrayOutputStream oBAOS = new ByteArrayOutputStream();
            ID3DataOutputStream oIDOS = new ID3DataOutputStream(oBAOS);
            
            try
            {
                for (int i=0; i < abyRawData.length; i++)
                {
                    oIDOS.writeBE32(abyRawData[i]);
                }
            }
            catch (IOException e) {}
            
            return oBAOS.toByteArray();
        }
        
        public String getOwnerIdentifier()
        {
            return "expand";
        }
    }
    
    public class DummyCryptoAgent implements ICryptoAgent
    {
        public byte[] decrypt(byte[] abyEncryptedData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            return abyEncryptedData;
        }
        
        public byte[] encrypt(byte[] abyRawData, byte[] abyEncryptionData)
            throws ID3CryptException
        {
            return abyRawData;
        }
        
        public String getOwnerIdentifier()
        {
            return "dummy";
        }
    }
}
