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

//���ʈȏ�ɂȂ�����t�@�C���֏����o��LinkedList
//LinkedHashMap���p���͂��Ă��邪�S�Ẵ��\�b�h�������͂��Ă��Ȃ��̂Ŏg�p�ɂ͒��ӂ�v��
//�g�p�̃��\�b�h�͈ȉ�
//containsKey(Object),put(Serializable,Object),remove(Object),get(Object),size(),iterator(),����Iterator�̊e��C�e���[�V����
public class PersistentLinkedHashMap implements Serializable,Cloneable{
    private static final long serialVersionUID = 1L;
    
    private final int MAX_WASTE_SIZE=10485760;      //�t�@�C�����̃S�~�̏������s���������l�B�Ƃ肠�����P�O�l
    private int nowAllWasteArea = 0;    //���݂̖��ʗ̈�T�C�Y
    private int iMaxCacheSize; //�L���b�V���̍ő听���T�C�Y
    private int iCacheSize; //���݂̃L���b�V���T�C�Y

    /**
     * 
     * @uml.property name="cache"
     * @uml.associationEnd inverse="this$0:PersistentLinkedHashMap$CacheEntry" qualifier=
     * "e:PersistentLinkedHashMap$StubEntry PersistentLinkedHashMap$CacheEntry" multiplicity=
     * "(0 1)"
     */
    private Hashtable cache = new Hashtable();//�L���b�V��

    /**
     * 
     * @uml.property name="rows"
     * @uml.associationEnd inverse="this$0:PersistentLinkedHashMap$StubEntry" qualifier=
     * "key:java.lang.Object PersistentLinkedHashMap$StubEntry" multiplicity="(0 1)"
     */
    private LinkedHashMap rows = new LinkedHashMap();//LinkedHashMap�̎ʂ��A�}������e�G���g����StubEntry�I�u�W�F�N�g�������X�g�A����

    /**
     * 
     * @uml.property name="list"
     * @uml.associationEnd elementType="PersistentLinkedHashMap$CacheEntry" qualifier=
     * "key:java.lang.Object PersistentLinkedHashMap$StubEntry" multiplicity="(0 -1)"
     * ordering="ordered"
     */
    private LinkedList list = new LinkedList(); //�p�x�ɂ�郊�X�g

    /**
     * 
     * @uml.property name="of"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ObjectFile of; //�e�I�u�W�F�N�g������t�@�C��

    private String sTmpName; //�t�@�C����
//    private CacheEntry firstCacheEntry;//�ŋ߃A�N�Z�X���ꂽ�G���g��
//    private CacheEntry lastCacheEntry; //�Ō�̃G���g��
    private String filePath = null;     //�ۑ�����t�@�C���̃p�X
    private int elementCount = 0;        //size()���\�b�h�ŕԂ����߂̗v�f��
    
//    private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException
//    {
//        System.out.println("PersistentLinkedHashMap�̏������݂��n�߂܂���");
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
          CacheEntry ce = null;//�G���g�����擾
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
          e.inCache = true; //�L���b�V���ɂ���

          //���o���ăL���b�V���Ɉړ�����
          try {
              o = of.readObject(e.filePointer);
          } catch (ClassNotFoundException cnfe) {
              cnfe.printStackTrace();
          } catch (IOException e2) {
              e2.printStackTrace();
          }

          //�L���b�V�������E���ǂ����`�F�b�N
          if (iCacheSize >= iMaxCacheSize) {
              CacheEntry leastUsed = (CacheEntry) list.removeLast(); //���X�g�̏I�[����苎��

              //���o�����I�u�W�F�N�g���L���b�V���ɉ�����
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

              //�폜�̂��߂�StubEntry�𓾂�
              StubEntry outStubEntry = (StubEntry) rows.get(leastUsed.key);

              //�I�u�W�F�N�g���L���V�����瓾��
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

              if (outStubEntry.filePointer == -1) { //�L���b�V���ɍs����
                  outStubEntry.filePointer = of
                          .writeObject((Serializable) outObject);
              } else {
                  //���łɃt�@�C���ɂ���
                  
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
        }else{       //�㏑�������ꍇ�͑O�̃L���b�V�����폜
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
	      CacheEntry ce = null;//�G���g�����擾
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
	      
	      if(e.filePointer!=-1){ //�t�@�C�����ɂ������
	          int size=0;
	          try {
	              size = of.getObjectLength(e.filePointer);
	          } catch (IOException e1) {
	              e1.printStackTrace();
	          }
	          nowAllWasteArea+=size;
	      }
	      
	      
	  } else { //�L���b�V���ɂ͖����A�܂�t�@�C���̒��ɍs���Ă���
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
	      //���ʂȗ̈�͌�ňꊇ���ď�������
	  }
  
	  if(nowAllWasteArea>=MAX_WASTE_SIZE){ //�S�~���\���ɂ��܂��Ă��܂�����
	      doGerbageClean();
	      nowAllWasteArea=0; //�S�~���Z�b�g
	  }
	  return o;
    }
    
    //�z��̍ŏ��G���g����\����������N���X
    class StubEntry implements Serializable {
        boolean inCache;//�G���g�����L���b�V���ɂ��邩
        long filePointer = -1;//�G���g�����f�B�X�N�ɂ���΁A�ȑO�͂��̈ʒu�ɂ�����
    }

    //�L���b�V���̃G���g����\����������N���X
    class CacheEntry implements Serializable{
        Object key;
        Object o; //�L���b�V���ɓ����Object
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

    //�I���������s��
    public synchronized void finalize()throws IOException{
        if(of!=null){
            of.close();
        }
    }
    
    //���܂��Ă��܂��������ȗ̈�����ׂĎ�菜�����f�[�^�t�@�C���𐶐�����Brows�̒��̖����������v�f��null�̗v�f�ɂȂ�̂Œ���
    private synchronized void doGerbageClean(){
        of.doGerbageClean(rows.values());
    }

    public synchronized int size(){
        return elementCount;
    }
    
    public synchronized Iterator iterator() {
        return new PersitentItr();
    }
    
    //�I���W�i����Itrator�B������rows���瓾��Itrator�����b�s���O
    private class PersitentItr implements Iterator,Serializable {
        int cursor = 0;
        int lastRet = -1;
        Iterator innerItr=null;

        /**
         * 
         * @uml.property name="lastGot"
         * @uml.associationEnd multiplicity="(0 1)"
         */
        StubEntry lastGot = null; //next()�̌Ăяo���ōŌ�ɓ���ꂽStubEntry

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
                lastGot = e; //�Ō�ɓ������̂�remove()�̎��Ȃǂ̂��߂ɕێ����Ă���

                Object o = null;

                if (e.inCache) {
                    CacheEntry ce = null;//�G���g�����擾
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
                    e.inCache = true; //�L���b�V���ɂ���

                    //���o���ăL���b�V���Ɉړ�����
                    try {
                        o = of.readObject(e.filePointer);
                    } catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    //�L���b�V�������E���ǂ����`�F�b�N
                    if (iCacheSize >= iMaxCacheSize) {
                        CacheEntry leastUsed = (CacheEntry) list.removeLast(); //���X�g�̏I�[����苎��

                        //���o�����I�u�W�F�N�g���L���b�V���ɉ�����
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

                        //�폜�̂��߂�StubEntry�𓾂�
                        StubEntry outStubEntry = (StubEntry) rows.get(leastUsed.key);

                        //�I�u�W�F�N�g���L���V�����瓾��
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

                        if (outStubEntry.filePointer == -1) { //�L���b�V���ɍs����
                            outStubEntry.filePointer = of
                                    .writeObject((Serializable) outObject);
                        } else {
                            //���łɃt�@�C���ɂ���
                            
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
                    CacheEntry ce = null;//�G���g�����擾
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
                    
                    if(e.filePointer!=-1){ //�t�@�C�����ɂ������
                        int size=0;
                        try {
                            size = of.getObjectLength(e.filePointer);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        nowAllWasteArea+=size;
                    }
                    
                    
                } else { //�L���b�V���ɂ͖����A�܂�t�@�C���̒��ɍs���Ă���
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
                    //���ʂȗ̈�͌�ňꊇ���ď�������
                }
                
                if(nowAllWasteArea>=MAX_WASTE_SIZE){ //�S�~���\���ɂ��܂��Ă��܂�����
                    doGerbageClean();
                    nowAllWasteArea=0; //�S�~���Z�b�g
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
