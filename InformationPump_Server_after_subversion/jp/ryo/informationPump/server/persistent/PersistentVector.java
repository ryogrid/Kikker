package jp.ryo.informationPump.server.persistent;

import java.io.*;
import java.util.*;


//���ʈȏ�ɂȂ�����t�@�C���֏����o��vector
//Vector���p���͂��Ă��邪�S�Ẵ��\�b�h�������͂��Ă��Ȃ��̂Ŏg�p�ɂ͒��ӂ�v��
//�g�p�̃��\�b�h�͈ȉ�
//add(Serializable),get(int),remove(int),size(),iterator(),����Iterator�̊e��C�e���[�V����
public class PersistentVector implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int MAX_WASTE_SIZE=10485760;      //�t�@�C�����̃S�~�̏������s���������l�B�Ƃ肠�����P�O�l
    private final int NOT_USE_SERCH = 0;    //�N���X���[�h���̌����͍s��Ȃ�
    private final int USE_SINGLE_SERCH = 1; //�ЂƂ�path���猟�����s��
    private final int USE_SEVERAL_SERCH = 2; //��������path���猟�����s��
    private int usePath = NOT_USE_SERCH;     //�N���X���[�h�̃p�X�������s����
    private String serchPath = null; //�N���X�̃��[�h���ɃN���X�t�@�C����T���ꏊ
	private String serchCandidate[] = null;   //�N���X���[�h���̕������
    private transient ClassLoader loader = null;         //ObjectFile�̒��Ŏg�p����ClassLoader
	private int nowAllWasteArea = 0;    //���݂̖��ʗ̈�T�C�Y     
    private int iMaxCacheSize; //�L���b�V���̍ő听���T�C�Y
    private int iCacheSize; //���݂̃L���b�V���T�C�Y

    /**
     * 
     * @uml.property name="cache"
     * @uml.associationEnd inverse="this$0:PersistentVector$CacheEntry" qualifier="new:java.lang.Integer
     * PersistentVector$CacheEntry" multiplicity="(0 1)"
     */
    private Hashtable cache = new Hashtable();//�L���b�V��

    /**
     * 
     * @uml.property name="rows"
     * @uml.associationEnd inverse="this$0:PersistentVector$StubEntry" multiplicity="(0
     * -1)"
     */
    private Vector rows = new Vector();//vector�̎ʂ��A�}������e�G���g����StubEntry�I�u�W�F�N�g�������X�g�A����

    /**
     * 
     * @uml.property name="list"
     * @uml.associationEnd qualifier="key:java.lang.Integer PersistentVector$CacheEntry"
     * multiplicity="(0 1)"
     */
    private LinkedList list = new LinkedList(); //�p�x�ɂ�郊�X�g

    /**
     * 
     * @uml.property name="of"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ObjectFile of; //�e�I�u�W�F�N�g������t�@�C��

    private String sTmpName; //�t�@�C����
    private String filePath = null;     //�ۑ�����t�@�C���̃p�X
    private int elementCount = 0;        //size()���\�b�h�ŕԂ����߂̗v�f��
    
    //�z��̍ŏ��G���g����\����������N���X
    class StubEntry implements Serializable {
        boolean inCache;//�G���g�����L���b�V���ɂ��邩
//        boolean isArive=true;    //���̗v�f�͗L���ł��邩�B�����ł���΃t�@�C�����Ŗ��ʂȗ̈���g�p���Ă���\������B�����͌�ň�|����B
        long filePointer = -1;//�G���g�����f�B�X�N�ɂ���΁A�ȑO�͂��̈ʒu�ɂ�����
    }

    //�L���b�V���̃G���g����\����������N���X
    class CacheEntry implements Serializable {
        Integer key; //�����Ƀ}�b�s���O�A�z��̃C���f�b�N�X�ɑΉ�
        Object o; //�L���b�V���ɓ����Object
//        CacheEntry prev, next;//���X�g�\���̂��߂̎Q��
    }
    
    public PersistentVector(int iCacheSieze,String path) {
        this.iMaxCacheSize = iCacheSieze;
        this.filePath = path;
    }
    
    public PersistentVector(int iCacheSieze,String path,ClassLoader loader,String serPath){
        this(iCacheSieze,path);
        this.loader = loader;
        serchPath = serPath;
        usePath = USE_SINGLE_SERCH;
    }
    
    public PersistentVector(int iCacheSieze,String path,ClassLoader loader,String[] serPath){
        this(iCacheSieze,path);
        this.loader = loader;
        serchCandidate = serPath;
        usePath = USE_SEVERAL_SERCH;
    }
    
    public void resetLoader(ClassLoader loader){
        if(of!=null){
            of.resetLoader(loader);
        }
    }

    private synchronized final void openTempFile() throws IOException {
        boolean bInValid = true;

        if(filePath!=null){
            if(usePath == NOT_USE_SERCH){
                of = new ObjectFile(filePath);                
            }else if(usePath==USE_SINGLE_SERCH){
                of = new ObjectFile(filePath,loader,serchPath);
            }else if(usePath==USE_SEVERAL_SERCH){
                of = new ObjectFile(filePath,loader,serchCandidate);
            }
        }else{
            throw new IOException("file path haven't been given!!");
        }
    }
    
    public synchronized boolean add(Object o) {
        throw new RuntimeException("this Method isn't implemented!! illegal call!!");
    }
    
    public synchronized final void add(Serializable o) throws IOException {
        StubEntry e = new StubEntry();

        rows.add(e);
        elementCount++;


//if(iCacheSize > iMaxCacheSize){
//    throw new RuntimeException();
//}

        if (iCacheSize < iMaxCacheSize) {
            e.inCache = true;
            CacheEntry ce = new CacheEntry();
            ce.o = o;
            ce.key = new Integer(rows.size() - 1);
            Object before = cache.put(ce.key, ce);
            if(before==null){
                iCacheSize++;
            }else{
                list.remove(before);
        	}
            
            //�L���b�V���̃G���g�������X�g�ɉ�����
            list.addFirst(ce);

        } else {
            if (of == null) {
                openTempFile();
            }
            e.filePointer = of.writeObject(o);
        }
        
//        if(iCacheSize > iMaxCacheSize){
//            throw new RuntimeException();
//        }        
    }

    //�I�u�W�F�N�g���L���b�V���܂��̓t�@�C��������o��
    public synchronized final Object get(int idx) {
//        if(iCacheSize > iMaxCacheSize){
//            throw new RuntimeException();
//        }

        if (idx < 0 || idx >= rows.size()) {
            throw new IndexOutOfBoundsException("Index: " + idx
                    + "out of bounds.");
        }

        StubEntry e = (StubEntry) rows.get(idx);

        Object o = null;

        if (e.inCache) {
            CacheEntry ce = null;//�G���g�����擾
            ce = (CacheEntry) cache.get(new Integer(idx));

            if (ce == null) {
                System.out.println("Element at idx " + idx + "is NULL");
            }

            if ((ce != null && ce.o == null)) {
                System.out.println("Cache Element's object at idx" + idx
                        + " Not in cache!");
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
                CacheEntry leastUsed = (CacheEntry) list.getLast(); //���X�g�̏I�[����苎��

                list.remove(leastUsed);

                //���o�����I�u�W�F�N�g���L���b�V���ɉ�����
                CacheEntry ce = new CacheEntry();
                ce.o = o;
                ce.key = new Integer(idx);
                Object before = cache.put(ce.key, ce);
                if(before == null){
                    iCacheSize++;                    
                }else{
                    list.remove(before);
                }

                list.addFirst(ce);

                //�폜�̂��߂�StubEntry�𓾂�
                StubEntry outStubEntry = (StubEntry) rows.get(leastUsed.key
                        .intValue());
                
                //�I�u�W�F�N�g���L���V�����瓾��
                CacheEntry outCacheEntry = (CacheEntry) cache
                        .remove(leastUsed.key);
                
                if (outCacheEntry == null) {
                    throw new RuntimeException("Cache Entry at "
                            + leastUsed.key.intValue() + " is Null!");
                }

                if (outCacheEntry != null && outCacheEntry.o == null) {
                    throw new RuntimeException("Cache object at "
                            + leastUsed.key.intValue() + " is Null!");
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
                ce.key = new Integer(idx);
                Object before = cache.put(ce.key, ce);
                if(before==null){
                    iCacheSize++;
                }else{
                    list.remove(before);
                }

                list.addFirst(ce);
            }
        }
        
//        if(iCacheSize > iMaxCacheSize){
//            throw new RuntimeException();
//        }

        return o;
    }
    
    //�I���������s��
    public synchronized void finalize()throws IOException{
        if(of!=null){
            of.close();
        }
    }
    
    public synchronized Object remove(int idx){
//        if(iCacheSize > iMaxCacheSize){
//            throw new RuntimeException();
//        }

        if (idx < 0 || idx >= rows.size()) {
            throw new IndexOutOfBoundsException("Index: " + idx
                    + "out of bounds.");
        }

        StubEntry e = (StubEntry) rows.remove(idx);
        elementCount--;
        
        Object o = null;

        if (e.inCache) {
            CacheEntry ce = null;//�G���g�����擾
            ce = (CacheEntry) cache.remove(new Integer(idx));
            iCacheSize--;

            if (ce == null) {
                System.out.println("Element at idx " + idx + "is NULL");
            }

            if ((ce != null && ce.o == null)) {
                System.out.println("Cache Element's object at idx" + idx
                        + " Not in cache!");
            }

            o = ce.o;
            
            list.remove(ce);
            
            if(e.filePointer!=-1){  //�t�@�C�����ɂ������
                int size=0;
                try {
                    size = of.getObjectLength(e.filePointer);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                nowAllWasteArea+=size;
            }
            
            
        } else {    //�L���b�V���ɂ͖����A�܂�t�@�C���̒��ɍs���Ă���
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
        
        //remove�����v�f�ȍ~��stubEntry��CacheEntry��index�ɂ��}�b�s���O��␳����
        int size = rows.size()+1;     //+1�͂��ł�remove���Ă��܂���idx�Ԗڂ̗v�f�̕�
        for(int i=idx+1;i<size;i++){      //�L���b�V���ł̃L�[���P�Â��炷
            CacheEntry adjustTgt = (CacheEntry) cache.remove(new Integer(i));
            if(adjustTgt!=null){   //�l�������ƕԂ��Ă����B�܂�L���b�V���Ɋ܂܂�Ă����ꍇ
            	Integer newIndex =new Integer(i-1);
            	adjustTgt.key = newIndex;
                cache.put(newIndex,adjustTgt);
            }
        }
        
        if(nowAllWasteArea>=MAX_WASTE_SIZE){      //�S�~���\���ɂ��܂��Ă��܂�����
            doGerbageClean();
            nowAllWasteArea=0; //�S�~���Z�b�g
        }
        
//        if(iCacheSize > iMaxCacheSize){
//            throw new RuntimeException();
//        }

        return o;
    }
    
    
    public synchronized Iterator iterator() {
        return new OriginalItr();
    }
    
    //���܂��Ă��܂��������ȗ̈�����ׂĎ�菜�����f�[�^�t�@�C���𐶐�����Brows�̒��̖����������v�f��null�̗v�f�ɂȂ�̂Œ���
    private synchronized void doGerbageClean(){
        of.doGerbageClean(rows);
    }

    public synchronized int size(){
        return elementCount;
    }
    
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException  {
        s.defaultReadObject();
    }
    
    private class OriginalItr implements Iterator {
        int cursor = 0;
        int lastRet = -1;

        public boolean hasNext() {
            return cursor != PersistentVector.this.size();
        }

        public Object next() {
            try {
                Object next = PersistentVector.this.get(cursor);
                lastRet = cursor++;
                return next;
            } catch(IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();

            try {
                PersistentVector.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

    }
}