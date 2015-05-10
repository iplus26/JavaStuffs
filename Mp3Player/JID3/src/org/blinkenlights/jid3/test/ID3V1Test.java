/*
 * ID3V1Test.java
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
 * $Id: ID3V1Test.java,v 1.7 2005/04/26 16:56:39 paul Exp $
 */
package org.blinkenlights.jid3.test;

import java.io.*;
import junit.framework.TestCase;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.util.*;
import org.blinkenlights.jid3.v1.*;

/**
 * @author paul
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ID3V1Test extends TestCase
{

    /**
     * Constructor for ID3V1Test.
     * @param arg0
     */
    public ID3V1Test(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ID3V1Test.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /** Test reading v1.0 tag from known file. */
    public void testReadV1_0Tag()
    {
        try
        {
            File oSourceFile = new File(AllTests.s_RootPath + "v1_0tags.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
            
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            System.out.println("\n*** v1_0tags.mp3 tags:");
            ID3Util.printTags(aoID3Tag);
            
            if (aoID3Tag.length != 1)
            {
                fail("There should be exactly one set of tags in this file.");
            }
            if ( ! (aoID3Tag[0] instanceof ID3V1_0Tag) )
            {
                fail("Expected ID3V1_0Tag.");
            }
            ID3V1_0Tag oID3V1_0Tag = (ID3V1_0Tag)aoID3Tag[0];
            if ((!oID3V1_0Tag.getTitle().equals("Title")) ||
                (!oID3V1_0Tag.getArtist().equals("Artist")) ||
                (!oID3V1_0Tag.getAlbum().equals("Album")) ||
                (!oID3V1_0Tag.getYear().equals("1999")) ||
                (!oID3V1_0Tag.getComment().equals("Comment")) ||
                (!oID3V1_0Tag.getGenre().equals(ID3V1Tag.Genre.Nullsoft_BlackMetal))) 
            {
                fail("Unexpected tag value found.");
            }
        }
        catch (Exception e) 
        {
            fail(e.toString());
        }
    }

    /** Test writing v1.0 tag to bare file, then compare against expected result. */
    public void testWriteV1_0Tag()
    {
        try
        {
            // get a copy of an unmodified file to edit
            copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v1_0_testresult.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v1_0_testresult.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
    
            // write v1.0 tag to file
            ID3V1_0Tag oID3V1_0Tag = new ID3V1_0Tag();
            oID3V1_0Tag.setAlbum("Album");
            oID3V1_0Tag.setArtist("Artist");
            oID3V1_0Tag.setComment("Comment");
            oID3V1_0Tag.setGenre(ID3V1Tag.Genre.Nullsoft_BlackMetal);
            oID3V1_0Tag.setTitle("Title");
            oID3V1_0Tag.setYear("1999");
            
            System.out.println(oID3V1_0Tag.toString());
            
            oMediaFile.setID3Tag(oID3V1_0Tag);
            oMediaFile.sync();

            // check against expected result
            compare(AllTests.s_RootPath + "v1_0tags.mp3", AllTests.s_RootPath + "id3_v1_0_testresult.mp3");
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /** Test reading v1.1 tag from known file. */
    public void testReadV1_1Tag()
    {
        try
        {
            File oSourceFile = new File(AllTests.s_RootPath + "v1_1tags.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            ID3Tag[] aoID3Tag = oMediaFile.getTags();
            System.out.println("\n*** v1_1tags.mp3 tags:");
            ID3Util.printTags(aoID3Tag);

            if (aoID3Tag.length != 1)
            {
                fail("There should be exactly one set of tags in this file.");
            }
            if ( ! (aoID3Tag[0] instanceof ID3V1_1Tag) )
            {
                fail("Expected ID3V1_1Tag.");
            }
            ID3V1_1Tag oID3V1_1Tag = (ID3V1_1Tag)aoID3Tag[0];
            if ((!oID3V1_1Tag.getTitle().equals("Title")) ||
                (!oID3V1_1Tag.getArtist().equals("Artist")) ||
                (!oID3V1_1Tag.getAlbum().equals("Album")) ||
                (!oID3V1_1Tag.getYear().equals("1999")) ||
                (!oID3V1_1Tag.getComment().equals("Comment")) ||
                (!oID3V1_1Tag.getGenre().equals(ID3V1Tag.Genre.Nullsoft_BlackMetal)) ||
                (oID3V1_1Tag.getAlbumTrack() != 7) ||
                (oID3V1_1Tag.getGenre().getByteValue() != 138))
            {
                fail("Unexpected tag value found.");
            }
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    
    /** Test writing v1.0 tag to bare file, then compare against expected result. */
    public void testWriteV1_1Tag()
    {
        try
        {
            // get a copy of an unmodified file to edit
            copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v1_1_testresult.mp3");

            File oSourceFile = new File(AllTests.s_RootPath + "id3_v1_1_testresult.mp3");
            MediaFile oMediaFile = new MP3File(oSourceFile);
        
            // write v1.1 tag to file
            ID3V1_1Tag oID3V1_1Tag = new ID3V1_1Tag();
            oID3V1_1Tag.setAlbum("Album");
            oID3V1_1Tag.setArtist("Artist");
            oID3V1_1Tag.setComment("Comment");
            oID3V1_1Tag.setGenre(ID3V1Tag.Genre.Nullsoft_BlackMetal);
            oID3V1_1Tag.setTitle("Title");
            oID3V1_1Tag.setYear("1999");
            oID3V1_1Tag.setAlbumTrack(7);
        
            System.out.println(oID3V1_1Tag.toString());
        
            oMediaFile.setID3Tag(oID3V1_1Tag);
            oMediaFile.sync();
            
            // check against expected result
            compare(AllTests.s_RootPath + "v1_1tags.mp3", AllTests.s_RootPath + "id3_v1_1_testresult.mp3");
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    /** Copy a file.
     * 
     * @param sSource source filename
     * @param sDestination destination filename
     * @throws Exception
     */
    private static void copy(String sSource, String sDestination)
        throws Exception
    {
        File oInputFile = new File(sSource);
        File oOutputFile = new File(sDestination);

        FileInputStream oFIS = new FileInputStream(oInputFile);
        FileOutputStream oFOS = new FileOutputStream(oOutputFile);
        int c;

        while ((c = oFIS.read()) != -1)
           oFOS.write(c);

        oFIS.close();
        oFOS.close();
    }
    
    /** Compare two files.
     * 
     * @param sFileOne filename
     * @param sFileTwo filename
     * @return true if identical, false otherwise
     * @throws Exception
     */
    private static void compare(String sFileOne, String sFileTwo)
        throws Exception
    {
        File oOneFile = new File(sFileOne);
        File oTwoFile = new File(sFileTwo);

        // check that lengths are the same        
        if (oOneFile.length() != oTwoFile.length())
        {
            throw new Exception("File lengths differ.");
        }
        
        FileInputStream oFIS1 = new FileInputStream(oOneFile);
        FileInputStream oFIS2 = new FileInputStream(oTwoFile);
        int c;
        
        // lengths are equal, so check that contents are the same
        int i=0;
        while ((c = oFIS1.read()) != -1)
        {
            if (oFIS2.read() != c)
            {
                throw new Exception("File contents differ at position " + i + ".");
            }
            i++;
        }
        
        oFIS1.close();
        oFIS2.close();
    }
}
