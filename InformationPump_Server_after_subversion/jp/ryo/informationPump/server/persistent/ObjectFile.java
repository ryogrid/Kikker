package jp.ryo.informationPump.server.persistent;

import java.io.*;
import java.util.*;


public class ObjectFile implements Serializable{
    private static final long serialVersionUID = 1L;
    
	private transient RandomAccessFile dataFile = null;
	private String sFileName;
	private String serchPath = null; //�N���X�̃��[�h���ɃN���X�t�@�C����T���ꏊ
	private String serchCandidate[] = null;   //�N���X���[�h���̕������ 
    private transient ClassLoader loader = null;        
	private final int MAX_WASTE_SIZE=10485760;      //�t�@�C�����̃S�~�̏������s���������l�B�Ƃ肠�����P�O�l
    private final int NOT_USE_SERCH = 0;    //�N���X���[�h���̌����͍s��Ȃ�
    private final int USE_SINGLE_SERCH = 1; //�ЂƂ�path���猟�����s��
    private final int USE_SEVERAL_SERCH = 2; //��������path���猟�����s��
    private int usePath = NOT_USE_SERCH;     //�N���X���[�h�̃p�X�������s����
    
	public ObjectFile(String sName,ClassLoader loader,String path) throws IOException{
		serchPath=path; 
		sFileName = sName;
		this.loader = loader;
		dataFile = new RandomAccessFile(sName,"rw");
		usePath = USE_SINGLE_SERCH;
	}
	
	public ObjectFile(String sName) throws IOException{
		sFileName = sName;
		dataFile = new RandomAccessFile(sName,"rw");
		usePath = NOT_USE_SERCH;
	}
	
	public ObjectFile(String sName,ClassLoader loader,String[] path) throws IOException{
	    serchCandidate = path; 
		sFileName = sName;
		this.loader = loader;
		dataFile = new RandomAccessFile(sName,"rw");
		usePath  = USE_SEVERAL_SERCH;
	}
	
	public void resetLoader(ClassLoader loader){
	    if((usePath == USE_SEVERAL_SERCH)||usePath == USE_SINGLE_SERCH){
	        this.loader = loader; 
	    }
	}
	
	//�I�u�W�F�N�g���������܂ꂽ�t�@�C���ʒu��Ԃ�
	public synchronized long writeObject(Serializable obj) {
	    checkIsInited();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		//���R�[�h�����o��
		long pos = 0L;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			
			int datalen = baos.size();
			
			pos = dataFile.length();
			dataFile.seek(pos);
			
			//�o�͂̒���������
			dataFile.writeInt(datalen);
			dataFile.write(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		baos = null;
		oos = null;
		
		return pos;
	}
	
	//�I�u�W�F�N�g�̒������擾
	public synchronized int getObjectLength(long lPos) throws IOException {
		checkIsInited();
	    dataFile.seek(lPos);
		return dataFile.readInt();
	}
	
	public synchronized Object readObject(long lPos) throws IOException,ClassNotFoundException{
		checkIsInited();
	    dataFile.seek(lPos);
		int datalen = dataFile.readInt();
		if(datalen > dataFile.length()){
			throw new IOException("Data file is corrupted. datalen: " + datalen);
		}
		
		byte[] data = new byte[datalen];
		dataFile.readFully(data);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
//	    ObjectInputStream ois = new ObjectInputStream(bais);
		ObjectInputStream ois = getSuitStream(bais);
		
		Object o = ois.readObject();
		
		bais = null;
		ois = null;
		data = null;
		
		return o;
	}
	
	public synchronized long length() throws IOException{
	    checkIsInited();
		return dataFile.length();
	}
	
	public void close() throws IOException{
		dataFile.close();
	}
	
	public synchronized void reWriteObject(long filePointer,byte[] object){
		try {
		    
		    checkIsInited();
			int datalen = object.length;
			dataFile.seek(filePointer);
			
			//�o�͂̒���������
			dataFile.writeInt(datalen);
			dataFile.write(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//�������܂�Ă����I�u�W�F�N�g�̃o�C�g���Ԃ�
	private synchronized byte[] getWritedByteArray(long filePointer){
	    
	    checkIsInited();
	    byte[] data=null;
        try {
            dataFile.seek(filePointer);
            int datalen = dataFile.readInt();
            if(datalen > dataFile.length()){
            	throw new IOException("Data file is corrupted. datalen: " + datalen);
            }
            
            data = new byte[datalen];
            dataFile.readFully(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return data;
	}
	
	//ArrayList���̖����ȗv�f��null�Ƃ���Ƌ��Ƀt�@�C���̒��̖��ʂȗ̈����|����B
	//����:�X�^�u���Ǘ����Ă���arrayList
	//�߂�l:���������v�f���A�܂葝������null�̃G�������g�̐�
	//�n����ArrayList�̒��g�͕ύX�����̂Œ���
	public synchronized void doGerbageClean(Collection stubs){
	    
	    checkIsInited();
	    File destFile = new File(sFileName + "_tmp");
        RandomAccessFile fout = null;
        try {
            fout = new RandomAccessFile(destFile,"rw");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
	    
	    int nullElements=0;
        Iterator itr = stubs.iterator();
        while(itr.hasNext()){
            PersistentVector.StubEntry tmp = (PersistentVector.StubEntry) itr.next();
            if((tmp!=null)&&(tmp.filePointer!=-1)){    //�L���ȗ̈����������R�s�[
                try {
                    long fileLength = fout.getFilePointer();
                    byte[] data =getWritedByteArray(tmp.filePointer);
                    fout.writeInt(getObjectLength(tmp.filePointer));
                    fout.write(data);
                    tmp.filePointer = fileLength;    //�ړ�������̏ꏊ�ōX�V����
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            fout.close();
            dataFile.close();
            dataFile = null;
            
            File delFile = new File(sFileName);
            boolean isDelSuccess = delFile.delete();       //�R�s�[���̃t�@�C�����폜����
            if(isDelSuccess == false){
                throw new IOException("have missed delete!!");
            }
            
            File copiedFile = new File(sFileName);
            boolean isRenameSuccess = destFile.renameTo(copiedFile);   //�R�s�[��̃t�@�C���̖��O���R�s�[���̃t�@�C�����Ƀ��l�[������
            if(isRenameSuccess==false){
                throw new IOException("have missed reaname");
            }

            dataFile = new RandomAccessFile(copiedFile,"rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }     //dataFile���ĂуZ�b�g�A�b�v
	}
	
	//File�������Ə���������Ă��邩�`�F�b�N���āA����������Ă��Ȃ��ꍇ����������
	//�V���A���C�Y��Ȃǂɏd�v
	private void checkIsInited(){
	    if(dataFile == null){
	        try {
                dataFile = new RandomAccessFile(sFileName,"rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
	    }
	}
	
	//���݂̏�ԂɓK���ɏ��������ꂽStream��Ԃ�
	private ObjectInputStream getSuitStream(InputStream in) throws IOException{
	        return new ObjectInputStream(in);
	}
}
