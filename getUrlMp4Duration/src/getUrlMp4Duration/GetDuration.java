package getUrlMp4Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

public class GetDuration {

	
	/**
	 * 获取excel中的url
	 * @param path
	 * @return
	 */
	public static List<String> getExcelUrl(String path){
		try {
			File file = new File(path);
			POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			System.out.print("总的行数：");
			System.out.println(hssfSheet.getLastRowNum());
			List<String> exList = new ArrayList<>();
//			for(int j=1;j<hssfSheet.getLastRowNum()+1;j++){
			for(int j=1;j<5+1;j++){
				HSSFRow hssfRow = hssfSheet.getRow(j);
				
//				for(int i=0;i<hssfRow.getLastCellNum();i++){
//					HSSFCell hssfCell2 = hssfRow.getCell(i);
//					System.out.print(i);
//					System.out.println(hssfCell2);
//				}
				
				HSSFCell hssfCell = hssfRow.getCell(12);
				exList.add(hssfCell.toString());
//				System.out.println(hssfCell);
//				return hssfCell.toString();
			}
			return exList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	/**
	 * 获取单个文件的duration
	 * @param path
	 * @return
	 */
	
	public static Long getDuration(String path){
		File file = new File(path);
		MultimediaInfo info = null;
		Encoder encoder = new Encoder();
		try {
			
			info = encoder.getInfo(file);
			long duration = info.getDuration();
//			System.out.println(duration);
			return duration;
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
/**
 * 得到一组视频url文件的时长
 * @param exList
 * @return
 */
	
	
	public static List<Long> getHeaderInfo(List<String> exList){
		List<Long> lengthList = new ArrayList<>();
		List<String> ExcelList = exList;
		for (Iterator<String> iterator = ExcelList.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String command = "cmd /c " + "curl -r 0-150000 " +string + " -o d:\\temp.mp4";
			try {
				Process process1 = Runtime.getRuntime().exec(command);
				process1.waitFor();
//				System.out.println(string);
				long duration = getDuration("D:\\temp.mp4");
				lengthList.add(duration);
				
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return lengthList;
		
	}
	
	
	/**
	 * 将得到的时长添加到excel中对应的位置去
	 * @param path
	 * @param Url
	 * @param Duratronhaha
	 */
	
	
	public static void InsertExcel(String path, List<String> Url, List<Long> Duratronhaha){
		try {
			List<String> urList =Url;
			List<Long> Duration = Duratronhaha;
			File file = new File(path);
			POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			for(int j=1;j<5+1;j++){
				HSSFRow hssfRow = hssfSheet.getRow(j);
				HSSFCell hssfCell = hssfRow.createCell(13);
				hssfCell = hssfRow.getCell(13);
				if(hssfRow.getCell(12).toString().equals(urList.get(j-1))){
					double value = Math.ceil(Duration.get(j-1)/1000);
					hssfCell.setCellValue(value);
				}
				System.out.println(hssfRow.getCell(13));
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			hssfWorkbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
	/**
	 * 执行一下
	 * @param args
	 */

	public static void main(String[] args) {

//		String filepath = "D:\\helloagain";
//		getDuration(filepath);
		String excelPath = "D:\\乐乐课堂.xls";
		List< String> ExcelList = getExcelUrl(excelPath);
		List<Long> LengthList = getHeaderInfo(ExcelList);
		InsertExcel(excelPath, ExcelList, LengthList);
	}
	
	
}
