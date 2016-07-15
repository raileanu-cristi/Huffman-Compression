/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman_decoding;

/**
 *
 * @author Cristi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.BitSet;
import java.util.Stack;
import java.util.TreeSet;
import javafx.util.Pair;

/**
 *
 * @author Cristi
 */

class Elem implements Comparable<Elem>
{
    protected int mFrecv;
    protected int mVal;
    
    public Elem() {};
    public Elem(int frecv, int val)
    {
        mFrecv= frecv;
        mVal= val;
    }
    
    public int getFrecv() {return mFrecv; }
    public int getVal() {return mVal; }
    
    public int compareTo(Elem other)
    {
        if (mFrecv < other.mFrecv)
            return 1;
        if (mFrecv > other.mFrecv)
            return -1;
         
        if (mVal < other.mVal)
            return 1;
        if (mVal > other.mVal)
            return -1;
        
        return 0;
    }
    
    public String toString() { return "("+mFrecv+" "+mVal+")"; }
}

class HuffmanTree
{
    int mTest= 0;
          
    private Node mRoot= null;
  
    private BitSet mCode= new BitSet(512);
    private int mIndex= 0;
    private BitSet mOutPut= new BitSet();
    private int mOutPoz= 0;
    
    private int mLeafLength= 0;
    
    private Stack<Pair<Node, Integer> > mStack= new Stack<Pair<Node, Integer>>();
    private int     mState= 0;
    private Node mCurrentNode= null;
    
    
    private int mFreebits= 0;
    
    private class Node extends Elem
    {
        private Node mLeft= null;
        private Node mRight= null;
       
        public Node() {}
        public Node(int frecv, int val)
        {
            mFrecv= frecv;
            mVal= val;
        }
        
        boolean isLeaf() {return mLeft == null && mRight == null; }
    }       // class node ///////////////////////////////////////////////
    
    
    void reverseCode(BitSet code,int st, int dr)
    {    
        int n= (st+dr)/2;
        
        for (int i=st; i<= n; i++ )               // reverse the bitset
            if ( code.get(i) != code.get(dr-i+st) ) 
            {
                code.flip(i);
                code.flip(dr-i + st);
            }
    }
    
    public void decode(int octet)
    {
        int i;
 
        if ( mLeafLength == 0)
        {
            for (i=6; i<=7; i++)
                if ( (octet & (1<<i)) > 0 )
                {
                    mCode.set(++mIndex);
                    octet^= (1<<i);
                }
                else
                    mIndex++;
            
            mLeafLength= octet;
            
            return;
        }
        
        if (mIndex < 0)
            mIndex= 0;
        
        for (i=0; i<=7; i++)
            if ( (octet & (1<<i)) > 0 )
                mCode.set(++mIndex);
            else
                mCode.clear(++mIndex);
        
        mIndex-= mFreebits;
        
        if (mState < 2)
            generate_tree();
        else
            parse();
    }
    
    public void generate_tree()
    {
        reverseCode(mCode,1, mIndex);
        
        while ( mIndex > 0 )
        {

            if (mState == 0)
            {
                if ( !mCode.get(mIndex) )
                {
                    // create node
                    mState= 0;
                    Node newNode= new Node(0,0);

                    if ( mStack.empty())
                        mRoot= newNode;
                    else
                    {
                        Node parent= mStack.peek().getKey();
                        int d= mStack.peek().getValue();
                        
                        if (d == 1)
                            parent.mLeft= newNode;
                        else
                            parent.mRight= newNode;
                    }

                    mStack.add( new Pair(newNode, 1) );
                }
                else
                    mState= 1;  
                
                mCode.clear(mIndex--);
            }
            else    // mState == 1 - reading a leaf
            {
                if (mIndex < mLeafLength)
                    break;
                
                int b= 0, i;
                
                for (i=0; i <= mLeafLength-1; i++)
                    if (mCode.get(mIndex-i))
                    {
                        b|= (1<<i);
                        mCode.clear(mIndex-i);
                    }
                
                mIndex-= mLeafLength;
                
                if (mIndex < 0 )
                    mIndex= 0;
                
                
                    // debug
                System.out.println(b);
                mTest++;
                    // debug
                
                Node newNode= new Node(0, b);
                
                Pair<Node, Integer> p= mStack.pop();
                Pair<Node, Integer> p1;
                
                if (p.getValue() == 1)
                {
                    p.getKey().mLeft= newNode;
                    
                    p1= new Pair(p.getKey(), 2);
                    mStack.add(p1);
                }
                else
                { 
                    p.getKey().mRight= newNode;
                    
                        // pop internal completed nodes
                    while (!mStack.empty() && mStack.peek().getValue() == 2)
                        mStack.pop();
                    
                    if (!mStack.empty() && mStack.peek().getValue() == 1)
                    { 
                        p= mStack.pop();
                        p1= new Pair(p.getKey(), 2);
                        mStack.add(p1);
                    }
                }
                
                if (mStack.empty())
                    break;
                
                mState= 0;
            }           // mState == 1
        }       // while loop
        
            // assert
        int verify=0;
        if (mStack.size() == 6)
            verify= 1;          // <- target
            // assert
        
        if (mStack.empty())
            mState= 2;
        
        reverseCode(mCode,1, mIndex);
    } 
    
    // * method: parse
    // * purpose: decoding
    
    public void parse()
    {
        
        int  i;
        
        if (mCurrentNode == null)
            mCurrentNode= mRoot;
        
        reverseCode(mCode, 1, mIndex);
        
        while (mIndex > 0)
        {
            if ( !mCode.get(mIndex--) )
                mCurrentNode= mCurrentNode.mLeft;
            else
                mCurrentNode= mCurrentNode.mRight;
            
            if (mCurrentNode.isLeaf())
            {
                for (i=0; i< 8; i++)
                    if ( (mCurrentNode.mVal & (1<<i)) > 0 )
                        mOutPut.set(++mOutPoz);
                    else
                        mOutPut.clear(++mOutPoz);
                
                mCurrentNode= mRoot;
            }
        }
        
        reverseCode(mCode, 1, mIndex);
        
    }
    
    public void deleteLast(int n)
    {
        mFreebits= n;
    }
    
    public boolean hasNext()
    {
        return mOutPoz >= 8;
    }
    
    public boolean hasSomething()
    {
        return mOutPoz > 0;
    }
    
    public byte next()
    {
        if (!hasSomething())
            return 0;
        
        byte out= 0;
        
        reverseCode(mOutPut,1, mOutPoz);
        
        for (int i= mOutPoz; i>= mOutPoz-7 && i > 0; i--)
            if ( mOutPut.get(i) )
            {
                out|= 1<< (mOutPoz - i) ;
                
                mOutPut.clear(i);
            }
        
        mOutPoz-= 8;
        if (mOutPoz < 0)         // correction
            mOutPoz= 0;
        
        reverseCode(mOutPut,1, mOutPoz); // facem la loc
       
        return out;
    }
    
    public byte get_free() { return (byte)mFreebits; }
    public int getState() {return mState; }
}

