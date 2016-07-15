/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmannencoding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author Cristi
 */
public class HuffmannEncoding {
    
    //////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// ENCODE ////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    static void encode( String inputName ) throws IOException
    {
        String outputName= inputName +".huf";
        
        Map<Integer,Integer> map= new HashMap<Integer,Integer>();
        
        FileInputStream in= null;
        FileOutputStream out= null;
                
        try
        {
            in= new FileInputStream(inputName);
            int c= 0;
            
            while ( (c= in.read()) != -1 )
                if (!map.containsKey(c))
                    map.put(c,1);
                else
                {
                    Integer count= map.get(c);
                    map.put(c, ++count);
                }
            
        } 
        finally
        {
            if (in != null)
                in.close();
        }
        
        System.out.println(map);
        
        HuffmanTree hufTree= new HuffmanTree();
        
        // debug
        int test= 0;
        //debug
        
        for (Map.Entry<Integer, Integer> it: map.entrySet())
        {
            hufTree.add(new Elem(it.getValue(),it.getKey() ) );
            test++; // debug
        }
        
        System.out.println(test);
        
        hufTree.assemble();     // the tree is ready
        
        int c=0;
        byte octet=0;
        
        try             // encoding file
        {
            in= new FileInputStream(inputName);
            out= new FileOutputStream(outputName);
            
            hufTree.treeCode();
            
            while (hufTree.hasNext())               // writing the tree
                    out.write(hufTree.next());
            
            while ( (c= in.read()) != -1 )  // procesing the FILE
            {
                 hufTree.encode(c);      // throws exception - index out of bounds
                
                while (hufTree.hasNext())       // write the codification of the current byte
                    out.write(hufTree.next());
            }
            
            if ( hufTree.hasSomething())        // write the last byte
            {
                out.write(hufTree.next());
                
                byte free= hufTree.get_free();
                out.write(free);
            }
        } 
        finally
        {
            System.out.println(c+ " "+ octet);
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
    
        encode(args[0]);
    }
    
}
