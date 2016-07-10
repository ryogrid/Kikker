package jp.ryo.informationPump.server.persistent;
/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Ryo Kanbayashi, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */

import java.io.*;
import java.util.*;

//一定量以上になったらファイルへ書き出すLinkedList
//LinkedHashMapを継承はしているが全てのメソッドを実装はしていないので使用には注意を要す
//使用可のメソッドは以下
//containsKey(Object),put(Serializable,Object),remove(Object),get(Object),size(),iterator(),得たIteratorの各種イテレーション
public class PersistentLinkedHashMap implements Serializable,Cloneable{
    private static final long serialVersionUID = 1L;
    
    private final int MAX_WASTE_SIZE=10485760;      //ファイル中のゴミの処理を行うしきい値。とりあえず１０Ｍ
    private int nowAllWasteArea = 0;    //現在の無駄領域サイズ
    private int iMaxCacheSize; //キャッシュの最大成長サイズ
    private int iCacheSize; //現在のキャッシュサイズ

    /**
     * 
     * @uml.property name="cache"
     * @uml.associationEnd inverse="this$0:PersistentLinkedHashMap$CacheEntry" qualifier=
     * "e:PersistentLinkedHashMap$StubEntry PersistentLinkedHashMap$CacheEntry" multiplicity=
     * "(0 1)"
     */
    private Hashtable cache = new Hashtable();//キャッシュ

    /**
     * 
     * @uml.property name="rows"
     * @uml.associationEnd inverse="this$0:PersistentLinkedHashMap$StubEntry" qualifier=
     * "key:java.lang.Object PersistentLinkedHashMap$StubEntry" multiplicity="(0 1)"
     */
    private LinkedHashMap rows = new LinkedHashMap();//LinkedHashMapの写し、挿入する各エントリのStubEntryオブジェクトだけをストアする

    /**
     * 
     * @uml.property name="list"
     * @uml.associationEnd elementType="PersistentLinkedHashMap$CacheEntry" qualifier=
     * "key:java.lang.Object PersistentLinkedHashMap$StubEntry" multiplicity="(0 -1)"
     * ordering="ordered"
     */
    private LinkedList list = new LinkedList(); //頻度によるリスト

    /**
     * 
     * @uml.property name="of"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ObjectFile of; //各オブジェクトがあるファイル

    private String sTmpName; //ファイル名
//    private CacheEntry firstCacheEntry;//最近アクセスされたエントリ
//    private CacheEntry lastCacheEntry; //最後のエントリ
    private String filePath = null;     //保存するファイルのパス
    private int elementCount = 0;        //size()メソッドで返すための要素数
    
//    private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
//    {
//        System.out.println("PersistentLinkedHashMapの書き込みを始めました");
//        s.defaultWriteObject();
//	}
//    
//    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException  {
//        s.defaultReadObject();
//    }
    
    public synchronized Object get(Object key) {
        
      StubEntry e = (StubEntry) rows.get(key);
      if(e==null){
          return null;
      }
          
      Object o = null;

      if (e.inCache) {
          CacheEntry ce = null;//エントリを取得
          ce = (CacheEntry) cache.get(e);

          if (ce == null) {
              throw new RuntimeException("Coudn't get CacheEntry from cache");
          }

          if ((ce != null && ce.o == null)) {
              throw new RuntimeException("Coudn't find Object in CacheEntry got from cache");
          }

          o = ce.o;

          list.remove(ce);
          list.addFirst(ce);

      } else {
          e.inCache = true; //キャッシュにある

          //取り出してキャッシュに移動する
          try {
              o = of.readObject(e.filePointer);
          } catch (ClassNotFoundException cnfe) {
              cnfe.printStackTrace();
          } catch (IOException e2) {
              e2.printStackTrace();
          }

          //キャッシュが限界かどうかチェック
          if (iCacheSize >= iMaxCacheSize) {
              CacheEntry leastUsed = (CacheEntry) list.removeLast(); //リストの終端を取り去る

              //取り出したオブジェクトをキャッシュに加える
              CacheEntry ce = new CacheEntry();
              ce.o = o;
              ce.key = key;
              Object before = cache.put(e, ce);
              if(before == null){
                  iCacheSize++;
              }else{
                  list.remove(before);
              }
              
              list.addFirst(ce);

              //削除のためのStubEntryを得る
              StubEntry outStubEntry = (StubEntry) rows.get(leastUsed.key);

              //オブジェクトをキャシュから得る
              CacheEntry outCacheEntry = (CacheEntry) cache
                      .remove(outStubEntry);
              iCacheSize--;
              
              if (outCacheEntry == null) {
                  throw new RuntimeException("Coudn't get CacheEntry from cache");
              }

              if (outCacheEntry != null && outCacheEntry.o == null) {
                  throw new RuntimeException("Coudn't find Object in CacheEntry got from cache");
              }

              Object outObject = outCacheEntry.o;
              outStubEntry.inCache = false;

              if (outStubEntry.filePointer == -1) { //キャッシュに行った
                  outStubEntry.filePointer = of
                          .writeObject((Serializable) outObject);
              } else {
                  //すでにファイルにある
                  
                  int iCurrentSize=0;
                  ByteArrayOutputStream baos = null;
                  ObjectOutputStream oos=null;
                  try{
	                    iCurrentSize = of
	                            .getObjectLength(outStubEntry.filePointer);
	
	                    baos = new ByteArrayOutputStream();
	                    oos = new ObjectOutputStream(baos);
	                    oos.writeObject((Serializable) outObject);
	                    oos.flush();
                  }catch(IOException err){
                      err.printStackTrace();
                  }
                  
                  int datalen = baos.size();

                  if (datalen <= iCurrentSize) {
                      of.reWriteObject(outStubEntry.filePointer, baos
                              .toByteArray());
                  } else {
                      outStubEntry.filePointer = of
                              .writeObject((Serializable) outObject);
                  }

                  baos = null;
                  oos = null;
                  outObject = null;
              }
          } else {
              CacheEntry ce = new CacheEntry();
              ce.o = o;
              ce.key = key;
              Object before = cache.put(e, ce);
              if(before==null){
                  iCacheSize++;                  
              }else{
                  list.remove(before);
              }

              list.addFirst(ce);
          }
      }
      return o;
    }
    
    public synchronized boolean containsKey(Object key) {
        return rows.containsKey(key);
    }
    
    public synchronized void put(Object key, Serializable o) throws IOException {
		StubEntry e = new StubEntry();

        Object contained = rows.put(key,e);
        if(contained == null){
            elementCount++;
        }else{       //上書きした場合は前のキャッシュを削除
            if(((StubEntry)contained).inCache==true){            
                Object before = cache.remove(contained);
                list.remove(before);
                iCacheSize--;
            }
        }
        
        if (iCacheSize < iMaxCacheSize) {
            e.inCache = true;
            CacheEntry ce = new CacheEntry();
            ce.o = o;
            ce.key = key;
            Object before = cache.put(e, ce);
            if(before == null){
                iCacheSize++;
            }else{
                list.remove(before);                
            }
            
            list.addFirst(ce);
        } else {
            if (of == null) {
                openTempFile();
            }
            e.filePointer = of.writeObject(o);
        }        
    }
    
    public synchronized Object put(Object key, Object value) {
        throw new RuntimeException("this Method isn't implemented!! illegal call!!");
    }
    
    public synchronized Object remove(Object key) {
        
	  StubEntry e = (StubEntry) rows.remove(key);
	  if(e==null){
	      return null;
	  }
	  
	  elementCount--;
	  
	  
	  
	  Object o = null;
	
	  if (e.inCache) {
	      CacheEntry ce = null;//エントリを取得
	      ce = (CacheEntry) cache.remove(e);
	      iCacheSize--;
	
	      if (ce == null) {
	          throw new RuntimeException("Couldn't get CacheEntry from cache");
	      }
	
	      if ((ce != null && ce.o == null)) {
	          throw new RuntimeException("Couldn't get Object from CacheEntry got from cache");
	      }
	
	      o = ce.o;
	      
	      list.remove(ce);
	      
	      if(e.filePointer!=-1){ //ファイル中にもあれば
	          int size=0;
	          try {
	              size = of.getObjectLength(e.filePointer);
	          } catch (IOException e1) {
	              e1.printStackTrace();
	          }
	          nowAllWasteArea+=size;
	      }
	      
	      
	  } else { //キャッシュには無い、つまりファイルの中に行っている
	      try {
	          o = of.readObject(e.filePointer);
	      } catch (IOException e1) {
	          e1.printStackTrace();
	      } catch (ClassNotFoundException e1) {
	          e1.printStackTrace();
	      }
	      
	      int size=0;
	      try {
	          size = of.getObjectLength(e.filePointer);
	      } catch (IOException e2) {
	          e2.printStackTrace();
	      }
	      nowAllWasteArea+=size;
	      //無駄な領域は後で一括して処理する
	  }
  
	  if(nowAllWasteArea>=MAX_WASTE_SIZE){ //ゴミが十分にたまってしまったら
	      doGerbageClean();
	      nowAllWasteArea=0; //ゴミリセット
	  }
	  return o;
    }
    
    //配列の最小エントリを表現する内部クラス
    class StubEntry implements Serializable {
        boolean inCache;//エントリがキャッシュにあるか
        long filePointer = -1;//エントリがディスクにあれば、以前はこの位置にあった
    }

    //キャッシュのエントリを表現する内部クラス
    class CacheEntry implements Serializable{
        Object key;
        Object o; //キャッシュに入れるObject
    }

    public PersistentLinkedHashMap(int iCacheSieze,String path) {
        this.iMaxCacheSize = iCacheSieze;
        this.filePath = path;
    }

    private synchronized final void openTempFile() throws IOException {
        boolean bInValid = true;

        if(filePath!=null){
            of = new ObjectFile(filePath);
        }else{
            throw new IOException("file path haven't been given!!");
        }
    }

    //終了処理を行う
    public synchronized void finalize()throws IOException{
        if(of!=null){
            of.close();
        }
    }
    
    //たまってしまった無効な領域をすべて取り除いたデータファイルを生成する。rowsの中の無効だった要素はnullの要素になるので注意
    private synchronized void doGerbageClean(){
        of.doGerbageClean(rows.values());
    }

    public synchronized int size(){
        return elementCount;
    }
    
    public synchronized Iterator iterator() {
        return new PersitentItr();
    }
    
    //オリジナルのItrator。内部のrowsから得たItratorをラッピング
    private class PersitentItr implements Iterator,Serializable {
        int cursor = 0;
        int lastRet = -1;
        Iterator innerItr=null;

        /**
         * 
         * @uml.property name="lastGot"
         * @uml.associationEnd multiplicity="(0 1)"
         */
        StubEntry lastGot = null; //next()の呼び出しで最後に得られたStubEntry

        public PersitentItr(){
            innerItr = (rows.entrySet()).iterator();
        }
        
        public boolean hasNext() {
            return innerItr.hasNext();
        }

        public Object next() {
            
            try {
                Map.Entry entry = (Map.Entry) innerItr.next();
                StubEntry e = (StubEntry)(entry.getValue());
                lastGot = e; //最後に得たものをremove()の時などのために保持しておく

                Object o = null;

                if (e.inCache) {
                    CacheEntry ce = null;//エントリを取得
                    ce = (CacheEntry) cache.get(lastGot);

                    if (ce == null) {
                        throw new RuntimeException("Couldn't get CacheEntry from cache");
                    }

                    if ((ce != null && ce.o == null)) {
                        throw new RuntimeException("Couldn't find Object on CacheEntry got from cache!!");
                    }

                    o = ce.o;
                    
                    list.remove(ce);
                    list.addFirst(ce);

                } else {
                    e.inCache = true; //キャッシュにある

                    //取り出してキャッシュに移動する
                    try {
                        o = of.readObject(e.filePointer);
                    } catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    //キャッシュが限界かどうかチェック
                    if (iCacheSize >= iMaxCacheSize) {
                        CacheEntry leastUsed = (CacheEntry) list.removeLast(); //リストの終端を取り去る

                        //取り出したオブジェクトをキャッシュに加える
                        CacheEntry ce = new CacheEntry();
                        ce.o = o;
                        ce.key = entry.getKey();
                        Object before = cache.put(e, ce);
                        if(before==null){
                            iCacheSize++;
                        }else{
                            list.remove(before);                            
                        }
                        
                        list.addFirst(ce);

                        //削除のためのStubEntryを得る
                        StubEntry outStubEntry = (StubEntry) rows.get(leastUsed.key);

                        //オブジェクトをキャシュから得る
                        CacheEntry outCacheEntry = (CacheEntry) cache
                                .remove(outStubEntry);
                        iCacheSize--;
                        
                        if (outCacheEntry == null) {
                            throw new RuntimeException("Couldn't get Cache Entry from cache");
                        }

                        if (outCacheEntry != null && outCacheEntry.o == null) {
                            throw new RuntimeException("Couldn't find Object on Entry got form cache");
                        }

                        Object outObject = outCacheEntry.o;
                        outStubEntry.inCache = false;

                        if (outStubEntry.filePointer == -1) { //キャッシュに行った
                            outStubEntry.filePointer = of
                                    .writeObject((Serializable) outObject);
                        } else {
                            //すでにファイルにある
                            
                            int iCurrentSize=0;
                            ByteArrayOutputStream baos = null;
                            ObjectOutputStream oos=null;
                            try{
        	                    iCurrentSize = of
        	                            .getObjectLength(outStubEntry.filePointer);
        	
        	                    baos = new ByteArrayOutputStream();
        	                    oos = new ObjectOutputStream(baos);
        	                    oos.writeObject((Serializable) outObject);
        	                    oos.flush();
                            }catch(IOException err){
                                err.printStackTrace();
                            }
                            
                            int datalen = baos.size();

                            if (datalen <= iCurrentSize) {
                                of.reWriteObject(outStubEntry.filePointer, baos
                                        .toByteArray());
                            } else {
                                outStubEntry.filePointer = of
                                        .writeObject((Serializable) outObject);
                            }

                            baos = null;
                            oos = null;
                            outObject = null;
                        }
                    } else {
                        CacheEntry ce = new CacheEntry();
                        ce.o = o;
                        ce.key = entry.getKey();
                        Object before = cache.put(e, ce);
                        if(before == null){
                            iCacheSize++;                            
                        }else{
                            list.remove(before);
                        }

                        list.addFirst(ce);
                    }
                }
                
                lastRet = cursor++;
                return o;
            } catch(IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();

            try {
                innerItr.remove();
                StubEntry e = lastGot;
                elementCount--;
                
                Object o = null;

                if (e.inCache) {
                    CacheEntry ce = null;//エントリを取得
                    ce = (CacheEntry) cache.remove(lastGot);
                    iCacheSize--;

                    if (ce == null) {
                        throw new RuntimeException("Couldn't get Cache Entry from cache");
                    }

                    if ((ce != null && ce.o == null)) {
                        throw new RuntimeException("Couldn't find Object on Entry got form cache");
                    }

                    o = ce.o;
                    
                    list.remove(ce);
                    
                    if(e.filePointer!=-1){ //ファイル中にもあれば
                        int size=0;
                        try {
                            size = of.getObjectLength(e.filePointer);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        nowAllWasteArea+=size;
                    }
                    
                    
                } else { //キャッシュには無い、つまりファイルの中に行っている
                    try {
                        o = of.readObject(e.filePointer);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    
                    int size=0;
                    try {
                        size = of.getObjectLength(e.filePointer);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    nowAllWasteArea+=size;
                    //無駄な領域は後で一括して処理する
                }
                
                if(nowAllWasteArea>=MAX_WASTE_SIZE){ //ゴミが十分にたまってしまったら
                    doGerbageClean();
                    nowAllWasteArea=0; //ゴミリセット
                }
                
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                lastGot = null;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

}
