/*
 * AllTests.java
 *
 * Created on 29-Dec-2003
 *
 * Copyright (C)2003-2005 Paul Grebenc
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
 * $Id: AllTests.java,v 1.8 2005/04/26 16:55:34 paul Exp $
 */

package org.blinkenlights.jid3.test;

import java.io.*;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;
import org.blinkenlights.jid3.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author paul
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AllTests extends TestCase
{
    // set root path for testing, so tests can find the test files
    public static String s_RootPath = "c:/work/jid3/test_data/";

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(AllTests.class);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.blinkenlights.id3.test");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(ID3V1Test.class));
        suite.addTest(new TestSuite(ID3V2Test.class));
        suite.addTest(new TestSuite(AllTests.class));
        //$JUnit-END$
        return suite;
    }
    
    public void testRemoveTags()
    {
        try
        {
            // get a copy of an unmodified file to edit
            ID3Util.copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v2_3_0_tagtest.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
            
            // write v1.1 tag to file
            ID3V1_1Tag oID3V1_1Tag = new ID3V1_1Tag();
            oID3V1_1Tag.setArtist("Artist");
            oID3V1_1Tag.setTitle("Song Title");
            oID3V1_1Tag.setAlbum("Album");
            oID3V1_1Tag.setYear("2004");
            oID3V1_1Tag.setAlbumTrack(3);
            oID3V1_1Tag.setGenre(ID3V1Tag.Genre.Blues);
            oMediaFile.setID3Tag(oID3V1_1Tag);
        
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
            
            // remove those tags
            oMediaFile.removeTags();
            
            // read file back
            oMediaFile = new MP3File(oSourceFile);
            
            // make sure the tags aren't there
            if (oMediaFile.getID3V1Tag() != null)
            {
                fail("The remove tag operation failed to remove v1 tag.");
            }
            if (oMediaFile.getID3V2Tag() != null)
            {
                fail("The remove tag operation failed to remove v2 tag.");
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }

    public void testVisitor()
    {
        try
        {
            File oSourceFile = new File(AllTests.s_RootPath + "v2_3_0tags.mp3");
            MP3File oMP3File = new MP3File(oSourceFile);
            
            ID3Tag[] aoID3Tag = oMP3File.getTags();

            AllTests.TestID3Visitor oTestID3Visitor = new AllTests.TestID3Visitor();
            
            // visit each tag (which also results in each v2 frame being visited)
            for (int i=0; i < aoID3Tag.length; i++)
            {
                aoID3Tag[i].accept(oTestID3Visitor);
            }
            
            // a 'visit list' was created by our visitor, recording which frames were visited, so we can compare
            if ( ! oTestID3Visitor.getVisitList().equals("3=DS+uw_PsKMr(VT$ICBUtvNyEzRL)W[QJO6*-"))
            {
                fail("Unexpected resulting visit list: " + oTestID3Visitor.getVisitList());
            }
        }
        catch (Exception e)
        {
            fail(ID3Exception.getStackTrace(e));
        }
    }
    
    public class TestID3Visitor extends ID3Visitor
    {
        private StringBuffer m_sbVisitList = null;
        
        public TestID3Visitor()
        {
            m_sbVisitList = new StringBuffer();
        }
        
        public String getVisitList()
        {
            return m_sbVisitList.toString();
        }
        
        // NOTE:  Normaly.. we would want to do something to each of the frames, or get some information out of them.
        //        But here, for this test, we just want to know that which visit methods were called in a particular case.

        public void visitID3V1_0Tag(ID3V1_0Tag oID3V1_0Tag) { m_sbVisitList.append("1"); };
        public void visitID3V1_1Tag(ID3V1_1Tag oID3V1_1Tag) { m_sbVisitList.append("2"); };

        public void visitID3V2_3_0Tag(ID3V2_3_0Tag oID3V2_3_0Tag) { m_sbVisitList.append("3"); };
        
        public void visitAENCID3V2Frame(AENCID3V2Frame oAENCID3V2Frame) { m_sbVisitList.append("4"); };
        public void visitAPICID3V2Frame(APICID3V2Frame oAPICID3V2Frame) { m_sbVisitList.append("5"); };
        public void visitCOMMID3V2Frame(COMMID3V2Frame oCOMMID3V2Frame) { m_sbVisitList.append("6"); };
        public void visitCOMRID3V2Frame(COMRID3V2Frame oCOMRID3V2Frame) { m_sbVisitList.append("7"); };
        public void visitENCRID3V2Frame(ENCRID3V2Frame oENCRID3V2Frame) { m_sbVisitList.append("8"); };
        public void visitEQUAID3V2Frame(EQUAID3V2Frame oEQUAID3V2Frame) { m_sbVisitList.append("9"); };
        public void visitETCOID3V2Frame(ETCOID3V2Frame oETCOID3V2Frame) { m_sbVisitList.append("a"); };
        public void visitGEOBID3V2Frame(GEOBID3V2Frame oGEOBID3V2Frame) { m_sbVisitList.append("b"); };
        public void visitGRIDID3V2Frame(GRIDID3V2Frame oGRIDID3V2Frame) { m_sbVisitList.append("c"); };
        public void visitIPLSID3V2Frame(IPLSID3V2Frame oIPLSID3V2Frame) { m_sbVisitList.append("d"); };
        public void visitLINKID3V2Frame(LINKID3V2Frame oLINKID3V2Frame) { m_sbVisitList.append("e"); };
        public void visitMCDIID3V2Frame(MCDIID3V2Frame oMCDIID3V2Frame) { m_sbVisitList.append("f"); };
        public void visitMLLTID3V2Frame(MLLTID3V2Frame oMLLTID3V2Frame) { m_sbVisitList.append("g"); };
        public void visitOWNEID3V2Frame(OWNEID3V2Frame oOWNEID3V2Frame) { m_sbVisitList.append("h"); };
        public void visitPCNTID3V2Frame(PCNTID3V2Frame oPCNTID3V2Frame) { m_sbVisitList.append("i"); };
        public void visitPOPMID3V2Frame(POPMID3V2Frame oPOPMID3V2Frame) { m_sbVisitList.append("j"); };
        public void visitPOSSID3V2Frame(POSSID3V2Frame oPOSSID3V2Frame) { m_sbVisitList.append("k"); };
        public void visitPRIVID3V2Frame(PRIVID3V2Frame oPRIVID3V2Frame) { m_sbVisitList.append("l"); };
        public void visitRBUFID3V2Frame(RBUFID3V2Frame oRBUFID3V2Frame) { m_sbVisitList.append("m"); };
        public void visitRVADID3V2Frame(RVADID3V2Frame oRVADID3V2Frame) { m_sbVisitList.append("n"); };
        public void visitRVRBID3V2Frame(RVRBID3V2Frame oRVRBID3V2Frame) { m_sbVisitList.append("o"); };
        public void visitSYLTID3V2Frame(SYLTID3V2Frame oSYLTID3V2Frame) { m_sbVisitList.append("p"); };
        public void visitSYTCID3V2Frame(SYTCID3V2Frame oSYTCID3V2Frame) { m_sbVisitList.append("q"); };
        public void visitTALBTextInformationID3V2Frame(TALBTextInformationID3V2Frame oTALBTextInformationID3V2Frame) { m_sbVisitList.append("r"); };
        public void visitTBPMTextInformationID3V2Frame(TBPMTextInformationID3V2Frame oTBPMTextInformationID3V2Frame) { m_sbVisitList.append("s"); };
        public void visitTCOMTextInformationID3V2Frame(TCOMTextInformationID3V2Frame oTCOMTextInformationID3V2Frame) { m_sbVisitList.append("t"); };
        public void visitTCONTextInformationID3V2Frame(TCONTextInformationID3V2Frame oTCONTextInformationID3V2Frame) { m_sbVisitList.append("u"); };
        public void visitTCOPTextInformationID3V2Frame(TCOPTextInformationID3V2Frame oTCOPTextInformationID3V2Frame) { m_sbVisitList.append("v"); };
        public void visitTDATTextInformationID3V2Frame(TDATTextInformationID3V2Frame oTDATTextInformationID3V2Frame) { m_sbVisitList.append("w"); };
        public void visitTDLYTextInformationID3V2Frame(TDLYTextInformationID3V2Frame oTDLYTextInformationID3V2Frame) { m_sbVisitList.append("x"); };
        public void visitTENCTextInformationID3V2Frame(TENCTextInformationID3V2Frame oTENCTextInformationID3V2Frame) { m_sbVisitList.append("y"); };
        public void visitTEXTTextInformationID3V2Frame(TEXTTextInformationID3V2Frame oTEXTTextInformationID3V2Frame) { m_sbVisitList.append("z"); };
        public void visitTFLTTextInformationID3V2Frame(TFLTTextInformationID3V2Frame oTFLTTextInformationID3V2Frame) { m_sbVisitList.append("A"); };
        public void visitTIMETextInformationID3V2Frame(TIMETextInformationID3V2Frame oTIMETextInformationID3V2Frame) { m_sbVisitList.append("B"); };
        public void visitTIT1TextInformationID3V2Frame(TIT1TextInformationID3V2Frame oTIT1TextInformationID3V2Frame) { m_sbVisitList.append("C"); };
        public void visitTIT2TextInformationID3V2Frame(TIT2TextInformationID3V2Frame oTIT2TextInformationID3V2Frame) { m_sbVisitList.append("D"); };
        public void visitTIT3TextInformationID3V2Frame(TIT3TextInformationID3V2Frame oTIT3TextInformationID3V2Frame) { m_sbVisitList.append("E"); };
        public void visitTKEYTextInformationID3V2Frame(TKEYTextInformationID3V2Frame oTKEYTextInformationID3V2Frame) { m_sbVisitList.append("F"); };
        public void visitTLANTextInformationID3V2Frame(TLANTextInformationID3V2Frame oTLANTextInformationID3V2Frame) { m_sbVisitList.append("G"); };
        public void visitTLENTextInformationID3V2Frame(TLENTextInformationID3V2Frame oTLENTextInformationID3V2Frame) { m_sbVisitList.append("H"); };
        public void visitTMEDTextInformationID3V2Frame(TMEDTextInformationID3V2Frame oTMEDTextInformationID3V2Frame) { m_sbVisitList.append("I"); };
        public void visitTOALTextInformationID3V2Frame(TOALTextInformationID3V2Frame oTOALTextInformationID3V2Frame) { m_sbVisitList.append("J"); };
        public void visitTOFNTextInformationID3V2Frame(TOFNTextInformationID3V2Frame oTOFNTextInformationID3V2Frame) { m_sbVisitList.append("K"); };
        public void visitTOLYTextInformationID3V2Frame(TOLYTextInformationID3V2Frame oTOLYTextInformationID3V2Frame) { m_sbVisitList.append("L"); };
        public void visitTOPETextInformationID3V2Frame(TOPETextInformationID3V2Frame oTOPETextInformationID3V2Frame) { m_sbVisitList.append("M"); };
        public void visitTORYTextInformationID3V2Frame(TORYTextInformationID3V2Frame oTORYTextInformationID3V2Frame) { m_sbVisitList.append("N"); };
        public void visitTOWNTextInformationID3V2Frame(TOWNTextInformationID3V2Frame oTOWNTextInformationID3V2Frame) { m_sbVisitList.append("O"); };
        public void visitTPE1TextInformationID3V2Frame(TPE1TextInformationID3V2Frame oTPE1TextInformationID3V2Frame) { m_sbVisitList.append("P"); };
        public void visitTPE2TextInformationID3V2Frame(TPE2TextInformationID3V2Frame oTPE2TextInformationID3V2Frame) { m_sbVisitList.append("Q"); };
        public void visitTPE3TextInformationID3V2Frame(TPE3TextInformationID3V2Frame oTPE3TextInformationID3V2Frame) { m_sbVisitList.append("R"); };
        public void visitTPE4TextInformationID3V2Frame(TPE4TextInformationID3V2Frame oTPE4TextInformationID3V2Frame) { m_sbVisitList.append("S"); };
        public void visitTPOSTextInformationID3V2Frame(TPOSTextInformationID3V2Frame oTPOSTextInformationID3V2Frame) { m_sbVisitList.append("T"); };
        public void visitTPUBTextInformationID3V2Frame(TPUBTextInformationID3V2Frame oTPUBTextInformationID3V2Frame) { m_sbVisitList.append("U"); };
        public void visitTRCKTextInformationID3V2Frame(TRCKTextInformationID3V2Frame oTRCKTextInformationID3V2Frame) { m_sbVisitList.append("V"); };
        public void visitTRDATextInformationID3V2Frame(TRDATextInformationID3V2Frame oTRDATextInformationID3V2Frame) { m_sbVisitList.append("W"); };
        public void visitTRSNTextInformationID3V2Frame(TRSNTextInformationID3V2Frame oTRSNTextInformationID3V2Frame) { m_sbVisitList.append("X"); };
        public void visitTRSOTextInformationID3V2Frame(TRSOTextInformationID3V2Frame oTRSOTextInformationID3V2Frame) { m_sbVisitList.append("Y"); };
        public void visitTSIZTextInformationID3V2Frame(TSIZTextInformationID3V2Frame oTSIZTextInformationID3V2Frame) { m_sbVisitList.append("Z"); };
        public void visitTSRCTextInformationID3V2Frame(TSRCTextInformationID3V2Frame oTSRCTextInformationID3V2Frame) { m_sbVisitList.append("!"); };
        public void visitTSSETextInformationID3V2Frame(TSSETextInformationID3V2Frame oTSSETextInformationID3V2Frame) { m_sbVisitList.append("@"); };
        public void visitTXXXTextInformationID3V2Frame(TXXXTextInformationID3V2Frame oTXXXTextInformationID3V2Frame) { m_sbVisitList.append("#"); };
        public void visitTYERTextInformationID3V2Frame(TYERTextInformationID3V2Frame oTYERTextInformationID3V2Frame) { m_sbVisitList.append("$"); };
        public void visitUFIDID3V2Frame(UFIDID3V2Frame oUFIDID3V2Frame) { m_sbVisitList.append("%"); };
        public void visitUSERID3V2Frame(USERID3V2Frame oUSERID3V2Frame) { m_sbVisitList.append("^"); };
        public void visitUSLTID3V2Frame(USLTID3V2Frame oUSLTID3V2Frame) { m_sbVisitList.append("&"); };
        public void visitWCOMUrlLinkID3V2Frame(WCOMUrlLinkID3V2Frame oWCOMUrlLinkID3V2Frame) { m_sbVisitList.append("*"); };
        public void visitWCOPUrlLinkID3V2Frame(WCOPUrlLinkID3V2Frame oWCOPUrlLinkID3V2Frame) { m_sbVisitList.append("("); };
        public void visitWOAFUrlLinkID3V2Frame(WOAFUrlLinkID3V2Frame oWOAFUrlLinkID3V2Frame) { m_sbVisitList.append(")"); };
        public void visitWOARUrlLinkID3V2Frame(WOARUrlLinkID3V2Frame oWOARUrlLinkID3V2Frame) { m_sbVisitList.append("-"); };
        public void visitWOASUrlLinkID3V2Frame(WOASUrlLinkID3V2Frame oWOASUrlLinkID3V2Frame) { m_sbVisitList.append("_"); };
        public void visitWORSUrlLinkID3V2Frame(WORSUrlLinkID3V2Frame oWORSUrlLinkID3V2Frame) { m_sbVisitList.append("="); };
        public void visitWPAYUrlLinkID3V2Frame(WPAYUrlLinkID3V2Frame oWPAYUrlLinkID3V2Frame) { m_sbVisitList.append("+"); };
        public void visitWPUBUrlLinkID3V2Frame(WPUBUrlLinkID3V2Frame oWPUBUrlLinkID3V2Frame) { m_sbVisitList.append("["); };
        public void visitWXXXUrlLinkID3V2Frame(WXXXUrlLinkID3V2Frame oWXXXUrlLinkID3V2Frame) { m_sbVisitList.append("]"); };
        public void visitEncryptedID3V2Frame(EncryptedID3V2Frame oEncryptedID3V2Frame) { m_sbVisitList.append(";"); };
        public void visitUnknownID3V2Frame(UnknownID3V2Frame oUnknownID3V2Frame) { m_sbVisitList.append(":"); };
        public void visitUnknownTextInformationID3V2Frame(UnknownTextInformationID3V2Frame oUnknownTextInformationID3V2Frame) { m_sbVisitList.append(","); };
        public void visitUnknownUrlLinkID3V2Frame(UnknownUrlLinkID3V2Frame oUnknownUrlLinkID3V2Frame) { m_sbVisitList.append("."); };
    }
}
