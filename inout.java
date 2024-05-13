import java.util.*;
import java.io.*;

public class inout {
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        float number = scanner.nextFloat();
        System.out.println("输入的数字为：" + number);

        scanner.nextLine();

        //2输出到文件
        try {
            System.out.println("请输入：");
            String inputData = scanner.nextLine();
            File outputFile = new File("d:\\output.txt");
            FileWriter writer = new FileWriter(outputFile);
            writer.write(inputData);
            writer.close();
            System.out.println("文件写入成功");
        }catch(IOException e) {
            System.err.println("写入文件时发生错误"+ e.getMessage());
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        //3
        String filepath ="D:\\well_logging_data.txt";
        try{
            Scanner fileScanner = new Scanner(new File(filepath));

            while(fileScanner.hasNext())
            {
                String line = fileScanner.nextLine();
                System.out.println("原始信息：" + line);

                // 将读取的数据转换为浮点数，并执行简单的运算
                try {
                    float originalValue = Float.parseFloat(line);
                    float result = originalValue + 2.0f;
                    System.out.println("简单运算结果：" + result);
                }catch(NumberFormatException e){
                    System.err.println("无法解析为浮点数：" + line);
                }
            }fileScanner.close();
        }catch(FileNotFoundException e){
            System.err.println("找不到数据文件"+ filepath);
        }
    }
}