/*
 * ID3Test.java
 *
 * Created on 8-Oct-2003
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
 * $Id: ID3Test.java,v 1.9 2005/02/06 18:11:27 paul Exp $
 */

package org.blinkenlights.jid3.test;

import java.io.*;

import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;

/**
 * @author paul
 *
 * Run all JUnit tests.
 */
public class ID3Test
{
    public static void main(String[] args)
        throws Exception
    {
        //testWriteSomething();
        if (false) { return; }
        
        testWriteV1_0Tag();
        
        testWriteV1_1Tag();
        
        testReadV1_0Tag();
        
        testReadV1_1Tag();
    }
    
    static void testWriteV1_0Tag()
        throws Exception
    {
        System.out.println("\n--- testWriteV1_0Tag ---");

        copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v1_0_testresult.mp3");
        File oSourceFile = new File(AllTests.s_RootPath + "id3_v1_0_testresult.mp3");
        MediaFile oMediaFile = new MP3File(oSourceFile);

        // test v1.0 tags
        ID3V1_0Tag oID3V1_0Tag = new ID3V1_0Tag();
        oID3V1_0Tag.setAlbum("Album");
        oID3V1_0Tag.setArtist("Artist");
        oID3V1_0Tag.setComment("Comment");
        oID3V1_0Tag.setGenre(ID3V1Tag.Genre.Dance);
        oID3V1_0Tag.setTitle("Title");
        oID3V1_0Tag.setYear("1999");
        
        System.out.println(oID3V1_0Tag.toString());
        
        oMediaFile.setID3Tag(oID3V1_0Tag);
        oMediaFile.sync();
    }
    
    static void testWriteV1_1Tag()
        throws Exception
    {
        System.out.println("\n--- testWriteV1_1Tag ---");

        copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_v1_1_testresult.mp3");
        File oSourceFile = new File(AllTests.s_RootPath + "id3_v1_1_testresult.mp3");
        MediaFile oMediaFile = new MP3File(oSourceFile);
        
        // test v1.1 tags
        ID3V1_1Tag oID3V1_1Tag = new ID3V1_1Tag();
        oID3V1_1Tag.setAlbum("Album");
        oID3V1_1Tag.setArtist("Artist");
        oID3V1_1Tag.setComment("Comment");
        oID3V1_1Tag.setGenre(ID3V1Tag.Genre.Dance);
        oID3V1_1Tag.setTitle("Title");
        oID3V1_1Tag.setYear("1999");
        oID3V1_1Tag.setAlbumTrack(7);
        
        System.out.println(oID3V1_1Tag.toString());
        
        oMediaFile.setID3Tag(oID3V1_1Tag);
        oMediaFile.sync();
    }
    
    private static void testReadV1_0Tag()
        throws Exception
    {
        System.out.println("\n--- testReadV1_0Tag ---");

        File oSourceFile = new File(AllTests.s_RootPath + "v1_0tags.mp3");
        MediaFile oMediaFile = new MP3File(oSourceFile);
        
        ID3Tag[] aoID3Tag = oMediaFile.getTags();
        printTags(aoID3Tag);
    }
    
    private static void testReadV1_1Tag()
        throws Exception
    {
        System.out.println("\n--- testReadV1_1Tag ---");
        
        File oSourceFile = new File(AllTests.s_RootPath + "v1_1tags.mp3");
        MediaFile oMediaFile = new MP3File(oSourceFile);
        
        ID3Tag[] aoID3Tag = oMediaFile.getTags();
        printTags(aoID3Tag);
    }
    
    private static void printTags(ID3Tag[] aoID3Tag)
        throws Exception
    {
        System.out.println("Number of tag sets: " + aoID3Tag.length);
        for (int i=0; i < aoID3Tag.length; i++)
        {
            if (aoID3Tag[i] instanceof ID3V1_0Tag)
            {
                System.out.println("ID3V1_0Tag:");
                System.out.println(aoID3Tag[i].toString());
            }
            else if (aoID3Tag[i] instanceof ID3V1_1Tag)
            {
                System.out.println("ID3V1_1Tag:");
                System.out.println(aoID3Tag[i].toString());
            }
            else if (aoID3Tag[i] instanceof ID3V2_3_0Tag)
            {
                System.out.println("ID3V2_3_0Tag:");
                System.out.println(aoID3Tag[i].toString());
            }
        }
    }
    
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
    
    /** Method used for random testing. */
/*    private static void testWriteSomething()
        throws Exception
    {
        copy(AllTests.s_RootPath + "notags.mp3", AllTests.s_RootPath + "id3_something.mp3");
        File oSourceFile = new File(AllTests.s_RootPath + "id3_something.mp3");
        MediaFile oMediaFile = new MP3File(oSourceFile);

        // test v1.0 tags
        ID3V1_0Tag oID3V1_0Tag = new ID3V1_0Tag();
        oID3V1_0Tag.setAlbum("Album");
        oID3V1_0Tag.setArtist("Artist");
        oID3V1_0Tag.setComment("Comment");
        oID3V1_0Tag.setGenre(ID3V1Tag.Genre.Dance);
        oID3V1_0Tag.setTitle("Title");
        oID3V1_0Tag.setYear("1999");
        
        System.out.println(oID3V1_0Tag.toString());
        
        oMediaFile.setID3Tag(oID3V1_0Tag);
        
        ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
        TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame("Lead Performer");
        oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);
        TRCKTextInformationID3V2Frame oTRCK = new TRCKTextInformationID3V2Frame(3, 9);
        oID3V2_3_0Tag.setTRCKTextInformationFrame(oTRCK);
        TIT1TextInformationID3V2Frame oTIT1 = new TIT1TextInformationID3V2Frame("Song Title");
        oID3V2_3_0Tag.setTIT1TextInformationFrame(oTIT1);
        
        oMediaFile.setID3Tag(oID3V2_3_0Tag);
        
        oMediaFile.sync();
        
        oSourceFile = new File(AllTests.s_RootPath + "id3_something.mp3");
        oMediaFile = new MP3File(oSourceFile);
        
        // any tags read from the file are returned, in an array, in an order which you should not assume
        ID3Tag[] aoID3Tag = oMediaFile.getTags();
        // let's loop through and see what we've got
        for (int i=0; i < aoID3Tag.length; i++)
        {
            // check to see if we read a v1.0 tag, or a v2.3.0 tag (just for example.. we could have other checks too)
            if (aoID3Tag[i] instanceof ID3V1_0Tag)
            {
                oID3V1_0Tag = (ID3V1_0Tag)aoID3Tag[i];
                // does this tag happen to contain a title?
                if (oID3V1_0Tag.getTitle() != null)
                {
                    System.out.println("Title = " + oID3V1_0Tag.getTitle());
                }
                // etc.
            }
            else if (aoID3Tag[i] instanceof ID3V2_3_0Tag)
            {
                oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[i];
                // check if this v2.3.0 frame contains a title, using the actual frame name
                if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null)
                {
                    System.out.println("Title = " + oID3V2_3_0Tag.getTIT2TextInformationFrame().getTitle());
                }
                // but check using the convenience method if it has a year set (either way works)
                try
                {
                    System.out.println("Year = " + oID3V2_3_0Tag.getYear());
                }
                catch (ID3Exception e)
                {
                    // error getting year.. most likely because one wasn't set
                    System.out.println("Could get read year from tag: " + e.toString());
                }
                // etc.
            }
        }
    }*/
}
