package net.ememed.doctor2.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.ememed.doctor2.R;
import net.ememed.doctor2.exception.MyException;

import android.os.Environment;

public class FileUtil {
	public static final String ROOT_DIRECTORY = "/emenddoctor";
	public static final String PATH = Environment.getExternalStorageDirectory() + ROOT_DIRECTORY;
	public static final String IMG_UPLOAD = "upload";
	public static void createFile(File file) {
		if (file.exists())
			return;
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createDir(File file) {
		if (file.exists())
			return;
		file.mkdirs();
	}

	public static void saveFile(InputStream in, String url) throws MyException {
		if (in == null) {
			throw new MyException("暂无文件可查看");
		}

		try {
			File file = new File(PATH, url.substring(url.lastIndexOf("/")));
			createFile(file);
			FileOutputStream fos = new FileOutputStream(file);
			int len = -1;
			byte[] bytes = new byte[1024];
			while ((len = in.read(bytes)) != -1) {
				fos.write(bytes, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /** 
     * 获得指定文件的byte数组 
     */  
    public static byte[] getBytes(File file){
        byte[] buffer = null;  
        try {  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }
	
}
