package getUrlMp4Duration;


/**
 * 将获得的时长和文件名字批量的更新到数据库中去，现在写的比较傻逼，以后可以写成存储过程
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

public class DecodeAndInject {

	// public static int[] getArray() {
	// String filePath = "D:\\javaMp4Length";
	// File rootFile = new File(filePath);
	// if(rootFile.isDirectory()){
	// File files[] = rootFile.listFiles();
	// int[] pathArray = new int[files.length];
	// if(files != null && files.length >0 ){
	// for (int i = 0; i < files.length; i++) {
	// File fileName = files[i];
	// String AbsolutePath = filePath + "\\" + fileName.getName();
	// pathArray[i] = getMp4Length(AbsolutePath);
	// }
	// }
	//// System.out.println(pathArray);
	// return pathArray;
	// }
	// return null;
	// }

	public static Map<String, Integer> getArray() {
		String filePath = "D:\\javaMp4Length";
		File rootFile = new File(filePath);
		if (rootFile.isDirectory()) {
			File files[] = rootFile.listFiles();
			Map<String, Integer> map = new HashMap<String, Integer>();
			int[] pathArray = new int[files.length];
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					File fileName = files[i];
					String AbsolutePath = filePath + "\\" + fileName.getName();
					pathArray[i] = gtMp4Length(AbsolutePath);
					map.put(fileName.getName().substring(0, fileName.getName().indexOf(".")), pathArray[i]);
				}
			}
			// System.out.println(pathArray);
			return map;
		}
		return null;
	}

	public static int gtMp4Length(String filepath) {
		int length = 0;
		File file = new File(filepath);
		MultimediaInfo info = null;
		Encoder encoder = new Encoder();
		try {
			info = encoder.getInfo(file);
			long length1 = info.getDuration();

			float length2 = length1 / 1000;
			length = (int) Math.ceil(length2);
			// System.out.println( length);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return length;
	}
	
	
	public static void main(String[] args) throws SQLException {
		int plus = 0;
		Connection conn = null;
		String sql = "";
		String url = "jdbc:mysql://127.0.0.1:3307/student_test?" 
		+ "user=haha&password=haha&&useUnicode=true&characterEncoding=UTF8&useSSL=false";
		try {
//			Class.forName("com.mysql.jdbc.Driver");
			com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
			System.out.println("\n---------驱动成功加载-----------");
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			System.out.println("\n----------打印sql到本地文件-------------");
			File file = new File("javaMp4LengthSql.sql");
			if(!file.exists()){
				file.createNewFile();
			}
			Map<String, Integer>  lengthArray = getArray();
			FileWriter fileWriter = new FileWriter(file.getName());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			for(Map.Entry<String, Integer> entry:lengthArray.entrySet()){
				sql = "update stu_video set length=" + entry.getValue() + " where Id=" + "'" +entry.getKey() + "'";
				int result = stmt.executeUpdate(sql);
				if(result != -1 ){
//					System.out.println(sql);
//					System.out.println("更新成功");	
					plus++;
				}
//				System.out.println("key= " + entry.getKey() + "value= " + entry.getValue());
				bufferWriter.write(sql + ";" + "\n");
			}
			bufferWriter.close();
			System.out.println(plus);
//			sql = "update stu_video set length=123 where Id='18364';";			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			conn.close();
		}
		
	}

}
