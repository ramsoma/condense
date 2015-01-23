/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newshog;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rtww
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
  if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            System.exit( 1 );
        }
        PrintWriter pw = null;
        try {
             pw = new PrintWriter(new FileWriter("runqueries.txt"));
            LineNumberReader lnr = new LineNumberReader(new FileReader("P:\\work\\glossary-terms.txt"));
            String arg = null;
            int i=0;
            while( (arg = lnr.readLine())!=null ) {
                try {
                    if(arg.trim().equals("")) continue;
                    String url = "http://onesearch.chevron.com/Pages/results.aspx?k=" + arg.trim().replaceAll(" ", "%20");
                    System.out.println(url);
                    pw.println(url);
                    java.net.URI uri = new java.net.URI( url);
                    desktop.browse( uri );
                }
                catch ( Exception e ) {

                    System.err.println( e.getMessage() );
                }
                if(++i %20==0)
                    System.in.read();
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        
        }finally{
            if(pw!=null)
                pw.close();
         }
    }

}
