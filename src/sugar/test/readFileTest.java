package sugar.test;
/*
 *@auther suger
 *2019
 *13:27
 */

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import sugar.tools.readFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class readFileTest {

    /**
     * readTxt
     */
    @Test
    public void readTXT(){
        String path="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\reportsPicker_war_exploded\\../upload/admin/C#程序设计基础/实验2_peopleFile/test.txt";
        List<String> txt = readFile.txt(path);
        System.out.println(txt);
        //        try(FileReader reader=new FileReader(path);
//            BufferedReader br=new BufferedReader(reader);
//        ){
//            String line;
//            while((line=br.readLine())!=null){
//                System.out.println(line);
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    @Test
    public void readXlS() throws Exception {
        String path="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload\\admin\\C#程序设计基础\\实验2_peopleFile\\test.xls";
        FileInputStream is=new FileInputStream(path);
        HSSFWorkbook workbook=new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
//        最后一行的下标(从0开始的)
        int rowCount=sheet.getLastRowNum();
        List<String> names=new ArrayList<String>();
        for (int i=0;i<=rowCount;i++){
            names.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }
        for (String key :names
             ) {
            System.out.println(key);
        }
        workbook.close();
        is.close();
    }


    @Test
    public void readXlSX() throws Exception {
        String path="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload\\admin\\C#程序设计基础\\实验2_peopleFile\\test.xlsx";
        FileInputStream is=new FileInputStream(path);
        XSSFWorkbook workbook= new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(0);
//        最后一行的下标(从0开始的)
        int rowCount=sheet.getLastRowNum();
        List<String> names=new ArrayList<String>();
        for (int i=0;i<=rowCount;i++){
            names.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }
        for (String key :names
        ) {
            System.out.println(key);
        }
        workbook.close();
        is.close();
    }
}
