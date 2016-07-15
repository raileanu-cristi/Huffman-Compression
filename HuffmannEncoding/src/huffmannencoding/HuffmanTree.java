/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmannencoding;

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

/**
 *
 * @author Cristi
 */

class HuffmanTree
{
    public int mTest= 0;
    
    TreeSet<Node> mTrSet= new TreeSet<Node>();
    
    private Node mRoot= null;
    
    private Node[] mLocation= new Node[256];  
    private BitSet mCode= new BitSet(512);
    private int mIndex= 0;
    
    private int     mState;
    
    private int mFreebits;
    
    private class Node extends Elem
    {
        private Node mLeft= null;
        private Node mRight= null;
        private Node mParent= null;
        private int  mBit= 0;
        
        public Node() {}
        public Node(int frecv, int val)
        {
            mFrecv= frecv;
            mVal= val;
        }
        
        boolean isLeaf() {return mLeft == null && mRight == null; }
    }       // class node ///////////////////////////////////////////////
    
    // ///////////////////////////////// Node Add //////////////////////////////
    
    public void add(Elem el)
    {
        Node newNode= new Node(el.mFrecv, el.mVal);
        mLocation[el.mVal]= newNode;
        
        mTrSet.add(newNode);
    }           
    
    public void assemble()
    {
        int uniqueKey= -1;
        
        while ( mTrSet.size() > 1 )
        {
            Node first= mTrSet.last();
            mTrSet.remove(mTrSet.last());
            
            Node second= mTrSet.last();
            mTrSet.remove(mTrSet.last());
            
            Node newRoot= new Node(first.mFrecv+second.mFrecv, --uniqueKey);
            
            newRoot.mLeft= first;
            newRoot.mRight= second;
            
            first.mBit= 0;
            second.mBit= 1;
            first.mParent= newRoot;
            second.mParent= newRoot;
            
            mTrSet.add(newRoot);
        }
        
        mRoot= mTrSet.last();
    }
    
    void reverseCode(int st, int dr)
    {    
        int n= (st+dr)/2;
        
        for (int i=st; i<= n; i++ )               // reverse the bitset
            if ( mCode.get(i) != mCode.get(dr-i+st) ) 
            {
                mCode.flip(i);
                mCode.flip(dr-i + st);
            }
    }
    
    public void treeCode()
    {
        mIndex= 6;
        mCode.set(4);   // 8 + 1 -bit leaf
        
        RSD(mRoot);
        
    }
    
    void RSD(Node nod)
    {
        if (  nod.isLeaf() )
        {
            mTest++;
            
            mCode.set(mIndex+1);
            
            for (int i=0; i<8; i++)                 
                if ( ( nod.mVal & (1<< i) ) > 0 )
                    mCode.set(mIndex+2+i);
            
            mIndex+= 8 + 1;
            return;
        }
        
        mIndex++;
        RSD(nod.mLeft);
        RSD(nod.mRight);
            
    }
    
    public void encode(int octet)  // bag un octet
    {            
        Node c= mLocation[octet];
        
        int st= mIndex+1;
        
        while ( c.mParent != null ) 
        {
            mIndex++;
            if ( c.mBit == 1)
                mCode.set(mIndex);
            
            c= c.mParent;
        }
        
        reverseCode(st, mIndex);  // fac la loc
        
    }
    
    
    public boolean hasNext()
    {
        return mIndex >= 8;
    }
    
    public boolean hasSomething()
    {
        return mIndex > 0;
    }
    
    public byte next()
    {
        if (!hasSomething())
            return 0;
        
        if (!hasNext())
            mFreebits= 8 - mIndex;
        
        byte out= 0;
        
        reverseCode(1, mIndex);
        
        for (int i= mIndex; i>= mIndex-7 && i > 0; i--)
            if ( mCode.get(i) )
            {
                out|= 1<< (mIndex - i) ;
                
                mCode.clear(i);
            }
        
        mIndex-= 8;
        if (mIndex < 0)         // correction
            mIndex= 0;
        
        reverseCode(1, mIndex); // facem la loc
       
        return out;
    }
    
    public byte get_free() { return (byte)mFreebits; }
    public int getState() {return mState; }
}

