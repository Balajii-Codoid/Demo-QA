package SpringPoc.utilities;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
   
   public static String getCanonicalPath(){
      String strResult="";
      try{
         strResult=new File(".").getCanonicalPath();
      }
      catch(Exception e){
      }
      
      return strResult;
   }
   public static boolean verifyFileExist(String strFilePath){
      File objFile=new File(strFilePath);
      return objFile.exists();
   }

   public static String getAbsolutePath(String strFilePath){
      return new File(strFilePath).getAbsolutePath();
   }
   
   public static boolean createFolder(String strFolderPath){
      return new File(strFolderPath).mkdir();
   }
   
   public static boolean createFile(String strFilePath){
      boolean blResult = false;
      try {
         blResult= new File(strFilePath).createNewFile();
      } catch (IOException e) {
      }
      return blResult;
   }

   public static boolean createFileWithContent(String strFilePath,String strContent){
      boolean blResult = false;
      try {
         File file = new File(strFilePath);
         BufferedWriter output = new BufferedWriter(new FileWriter(file));
           output.write(strContent);
           output.close();
           blResult = true;
      } catch (Exception e) {
      }
      return blResult;
   }
   
   public static boolean renameFile(String strPath1,String strPath2){
      boolean blResult = false;
      try {
         File file1 = new File(strPath1);
         File file2 = new File(strPath2);
         
         file1.renameTo(file2);
         
           blResult = true;
      } catch (Exception e) {
      }
      return blResult;
   }
   
   public static boolean writeContent(String strFilePath,String strContent){
      boolean blResult = false;
      try {
         File file = new File(strFilePath);
         BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
           output.write(strContent);
           output.close();
           blResult = true;
      } catch (Exception e) {
      }
      return blResult;
   }
   
   public static boolean deleteFile(String strFilePath){
      boolean blResult=false;
      try{
         File file = new File(strFilePath);
         file.delete();
         blResult=true;
      }
      catch(Exception e){}
      return blResult;
   }

   public static String[] getFileNamesFromFolder(String strFolderLocation) {
      String[] strFileName = new String[10];
      File folder = new File(strFolderLocation);
      File[] listOfFiles = folder.listFiles();
      int i = 0;
      for (File file : listOfFiles) {
         if (file.isFile()) {
            strFileName[i]=file.getName();
            i++;
         }
      }
      return strFileName;
   }

   public static boolean deleteFiles(String strDirectoryPath) {
      boolean isDeleted = false;
      File f = new File(strDirectoryPath);
      f.mkdir();
      if (f.isDirectory()) {
         String fileList[] = f.list();
         int num = fileList.length;
         for (int i = 0; i < num; i++) {
            File f1 = new File(f.getAbsolutePath() + File.separator + fileList[i]);
            isDeleted = f1.delete();
         }
         f.mkdir();
      }
      return isDeleted;
   }

   public static boolean waitForFileToDownload(String strFolderPath, String strFileName){
      boolean isOpened = false;
      try {
         if (Desktop.isDesktopSupported()) {
            File downloadedFile = new File(new StringBuilder(strFolderPath).append(strFileName).toString());
            int inCount = 0;
            do {
               Thread.sleep(3000);
               inCount++;
            } while (downloadedFile.exists() == false && inCount <= 10);

            isOpened = true;
         }
      } catch (Exception e) {
         isOpened = false;
      }

      return isOpened;
   }

   public static void createFile(String strFilePath, String strFile){
      try {
          FileOutputStream f = new FileOutputStream(new File(strFilePath+"/"+strFile));
          XSSFWorkbook workbook = new XSSFWorkbook();
          workbook.write(f);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
   public static void writeLog(String strLogContent) {
      try {
         // Log to console
         System.out.println();
         LOGGER.info("\n" + strLogContent);

         // Log to file
         File file = new File(Constants.strLogFileLoc);
         BufferedWriter output = new BufferedWriter(new FileWriter(file, true));

         // Get the current timestamp
         String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

         // Add timestamp to the log content
         String logEntry = timestamp + " INFO - " + strLogContent;

         output.write(logEntry);
         output.newLine();
         output.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void writeLogError(String strLogContent) {
      try {
         // Log to console
         System.out.println();
         LOGGER.error("\n" + strLogContent);

         // Log to file
         File file = new File(Constants.strLogFileLoc);
         BufferedWriter output = new BufferedWriter(new FileWriter(file, true));

         // Get the current timestamp
         String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

         // Add timestamp to the log content
         String logEntry = timestamp + " ERROR - " + strLogContent;

         output.write(logEntry);
         output.newLine();
         output.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }



   public static void clearLogFile(String strFilePath) {
      try {
         // Create a FileWriter with the second parameter set to false (overwrite mode)
         BufferedWriter output = new BufferedWriter(new FileWriter(strFilePath, false));
         output.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static String getFileNameFromFolder(String strFolderLocation, String strType) {
      String strFilename = null;
      File folder = new File(strFolderLocation);
      File[] files = folder.listFiles((dir, name) -> name.endsWith(strType));
      for (File file : files) {
         System.out.println(file.getName());
         strFilename = file.getName();
      }
      return strFilename;
   }
}
