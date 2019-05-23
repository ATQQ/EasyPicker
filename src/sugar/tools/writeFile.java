package sugar.tools;
/*
 *@auther suger
 *2019
 *19:42
 */

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class writeFile {


    /**
     * 输出未成功导入的名单
     * @param names 人员列表
     * @param filePath  保存文件的路径
     */
    public static void xls(List<String> names,String filePath) throws IOException {
        HSSFWorkbook workbook =new HSSFWorkbook();
        HSSFSheet sheet1 = workbook.createSheet("sheet1");
        HSSFRow row = sheet1.createRow(0);
//        设置表头
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("原因");
        for(int i=1;i<=names.size();i++){
            row=sheet1.createRow(i);
            row.createCell(0).setCellValue(names.get(i-1));
            row.createCell(1).setCellValue("名字已经存在");
        }
        File file=new File(filePath);
        workbook.write(file);
        workbook.close();
    }
}
