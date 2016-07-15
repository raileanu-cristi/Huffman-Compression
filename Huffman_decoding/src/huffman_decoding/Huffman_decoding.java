/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman_decoding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Cristi
 */



// ///////////////////////////////// Main class //////////////////////////////

public class Huffman_decoding {
    
    static void decode(String inputName) throws IOException
    {
        String outputName= inputName.substring(0, inputName.length()- 4);
        
        FileInputStream in= null;
        FileOutputStream out= null;
        
        HuffmanTree hufTree= new HuffmanTree();
        
        try             // encoding file
        {
            in= new FileInputStream(inputName);
            out= new FileOutputStream(outputName);
            
            int c1, c2, c3= 0;
            
            c1= in.read();
            c2= in.read();
            
            while ( (c3= in.read()) != -1 )
            {
                // prelucrez c1
                
                hufTree.decode(c1);
                
                while (hufTree.hasNext())
                    out.write( hufTree.next() );
                
                
                c1=c2;
                c2=c3;
            }
     
            hufTree.deleteLast(c2);
            hufTree.decode(c1);
            
            while (hufTree.hasNext())
                    out.write( hufTree.next() );
            
        } 
        finally
        {
            
            if (in != null)
                in.close();
            
            if (out!= null)
                out.close();
        }
        System.out.println(hufTree.mTest);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        // TODO code application logic here
        
        decode(args[0]);
        
        System.out.println("decoding complete !");
        
    }
    
}
