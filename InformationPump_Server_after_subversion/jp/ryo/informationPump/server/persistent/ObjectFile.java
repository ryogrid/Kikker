package jp.ryo.informationPump.server.persistent;

import java.io.*;
import java.util.*;


public class ObjectFile implements Serializable{
    private static final long serialVersionUID = 1L;
    
	private transient RandomAccessFile dataFile = null;
	private String sFileName;
	private String serchPath = null; //クラスのロード時にクラスファイルを探す場所
	private String serchCandidate[] = null;   //クラスロード時の複数候補 
    private transient ClassLoader loader = null;        
	private final int MAX_WASTE_SIZE=10485760;      //ファイル中のゴミの処理を行うしきい値。とりあえず１０Ｍ
    private final int NOT_USE_SERCH = 0;    //クラスロード時の検索は行わない
    private final int USE_SINGLE_SERCH = 1; //ひとつのpathから検索を行う
    private final int USE_SEVERAL_SERCH = 2; //いくつかのpathから検索を行う
    private int usePath = NOT_USE_SERCH;     //クラスロードのパス検索を行うか
    
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
	
	//オブジェクトが書き込まれたファイル位置を返す
	public synchronized long writeObject(Serializable obj) {
	    checkIsInited();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		//レコードを取り出す
		long pos = 0L;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			
			int datalen = baos.size();
			
			pos = dataFile.length();
			dataFile.seek(pos);
			
			//出力の長さを書く
			dataFile.writeInt(datalen);
			dataFile.write(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		baos = null;
		oos = null;
		
		return pos;
	}
	
	//オブジェクトの長さを取得
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
			
			//出力の長さを書く
			dataFile.writeInt(datalen);
			dataFile.write(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//書き込まれていたオブジェクトのバイト列を返す
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
	
	//ArrayList中の無効な要素をnullとすると共にファイルの中の無駄な領域を一掃する。
	//引数:スタブを管理しているarrayList
	//戻り値:除去した要素数、つまり増加したnullのエレメントの数
	//渡したArrayListの中身は変更されるので注意
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
            if((tmp!=null)&&(tmp.filePointer!=-1)){    //有効な領域を見つけたらコピー
                try {
                    long fileLength = fout.getFilePointer();
                    byte[] data =getWritedByteArray(tmp.filePointer);
                    fout.writeInt(getObjectLength(tmp.filePointer));
                    fout.write(data);
                    tmp.filePointer = fileLength;    //移動した後の場所で更新する
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
            boolean isDelSuccess = delFile.delete();       //コピー元のファイルを削除する
            if(isDelSuccess == false){
                throw new IOException("have missed delete!!");
            }
            
            File copiedFile = new File(sFileName);
            boolean isRenameSuccess = destFile.renameTo(copiedFile);   //コピー先のファイルの名前をコピー元のファイル名にリネームする
            if(isRenameSuccess==false){
                throw new IOException("have missed reaname");
            }

            dataFile = new RandomAccessFile(copiedFile,"rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }     //dataFileを再びセットアップ
	}
	
	//Fileがちゃんと初期化されているかチェックして、初期化されていない場合初期化する
	//シリアライズ後などに重要
	private void checkIsInited(){
	    if(dataFile == null){
	        try {
                dataFile = new RandomAccessFile(sFileName,"rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
	    }
	}
	
	//現在の状態に適当に初期化されたStreamを返す
	private ObjectInputStream getSuitStream(InputStream in) throws IOException{
	        return new ObjectInputStream(in);
	}
}
