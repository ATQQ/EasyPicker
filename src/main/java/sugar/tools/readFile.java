package sugar.tools;
/*
 *@auther suger
 *2019
 *13:50
 */

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class readFile {
    /**
     * 读取txt文件返回名称列表
     * @return
     */
    public static List<String> txt(String filePath){
        List<String> names=new ArrayList<String>();
//        System.out.println(filePath);
        try(FileInputStream fr = new FileInputStream(filePath);
            InputStreamReader is = new InputStreamReader(fr,"UTF-8");
            BufferedReader br=new BufferedReader(is);
        ){
            String line;
            while((line=br.readLine())!=null){
                names.add(line);
                System.out.println(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return names;
    }


    /**
     * 获取xls文件中的名单列表
     * @param filePath 文件路径
     * @return
     */
    public static List<String> xls(String filePath) throws IOException {
        FileInputStream is=new FileInputStream(filePath);
        HSSFWorkbook workbook=new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
        //最后一行的下标(从0开始的)
        int rowCount=sheet.getLastRowNum();
        List<String> names=new ArrayList<String>();
        for (int i=0;i<=rowCount;i++){
            names.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }

        workbook.close();
        is.close();
        return names;
    }


    /**
     * 获取XLSX文件中的名单列表
     * @param filePath
     * @return
     */
    public static List<String> xlsx(String filePath) throws IOException {
        FileInputStream is=new FileInputStream(filePath);
        XSSFWorkbook workbook= new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(0);
//        最后一行的下标(从0开始的)
        int rowCount=sheet.getLastRowNum();
        List<String> names=new ArrayList<String>();
        for (int i=0;i<=rowCount;i++){
            names.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }
        workbook.close();
        is.close();
        return names;
    }

    /**
     * 读取文件数据
     */
    public static List<String> read(String filePath) throws IOException {
        String fileType=filePath.substring(filePath.lastIndexOf("."));
        switch (fileType){
            case ".xls":
                return xls(filePath);
            case ".txt":
                return txt(filePath);
            case ".xlsx":
                return xlsx(filePath);
                default:
                    return null;
        }
    }
}
