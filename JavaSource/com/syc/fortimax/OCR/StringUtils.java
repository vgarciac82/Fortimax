package com.syc.fortimax.OCR;

/**
 * This class provides static util methods for String manaipulation that
 * aren't part of the default JDK functionalities.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaél Luta</a>
 * @version $Id$
 */
public class StringUtils
{
    /**
     * Replaces all the occurences of a substring found
     * within a StringBuffer by a
     * replacement string
     *
     * @param buffer the StringBuffer in where the replace will take place
     * @param find the substring to find and replace
     * @param replacement the replacement string for all occurences of find
     * @return the original StringBuffer where all the
     * occurences of find are replaced by replacement
     */
    public static StringBuffer replaceAll(StringBuffer buffer, String find, String replacement)
    {
        int bufidx = buffer.length() - 1;
        int offset = find.length();
        while( bufidx > -1 ) {
            int findidx = offset -1;
            while( findidx > -1 ) {
                if( bufidx == -1 ) {
                    //Done
                    return buffer;
                }
                if( buffer.charAt( bufidx ) == find.charAt( findidx ) ) {
                    findidx--; //Look for next char
                    bufidx--;
                } else {
                    findidx = offset - 1; //Start looking again
                    bufidx--;
                    if( bufidx == -1 ) {
                        //Done
                        return buffer;
                    }
                    continue;
                }
            }
            //Found
            //System.out.println( "replacing from " + (bufidx + 1) +
            //                    " to " + (bufidx + 1 + offset ) +
            //                    " with '" + replacement + "'" );
            buffer.replace( bufidx+1,
                            bufidx+1+offset,
                            replacement);
            //start looking again
        }
        //No more matches
        return buffer;
    }
}