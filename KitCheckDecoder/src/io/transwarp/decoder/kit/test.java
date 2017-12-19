package io.transwarp.decoder.kit;

import java.io.File;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;

public class test {
	
    public static void main(String [] args) throws IOException{
	String encoding="UTF-8";
    File file=new File("g://test.txt");
    if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
        InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//���ǵ������ʽ
		BufferedReader bufferedReader = new BufferedReader(read);
		String text= new String();
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null){
            text=text+lineTxt;
        }
        
        //System.out.println(text);
        
        KitCheckDecoder kcd=new KitCheckDecoder(null);
        kcd.arrayFromBytes(text.getBytes());
        read.close();
    }
    }
}