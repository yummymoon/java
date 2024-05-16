import java.io.*;
import java.util.*;

public class LogDataProcessor {
    private String filePath;

    public LogDataProcessor(String filePath) {
        this.filePath = filePath;
    }

    // 从文件读取测井数据，读取第15到30行有depth的数据
    public List<LogData> readLogData() throws FileNotFoundException {
        List<LogData> logDataList = new ArrayList<>();
        Scanner fileScanner = new Scanner(new File(filePath));
        int lineNumber = 0; // 用于跟踪文件中的行号

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine().trim();
            lineNumber++;
            if (line.matches("^\\d.*$")) { // 检查行是否以数字开头
                if (lineNumber >= 15 && lineNumber <= 30) {
                    String[] tokens = line.split("\\s+");
                    float depth = Float.parseFloat(tokens[0]);
                    float[] values = new float[tokens.length - 1];
                    for (int i = 1; i < tokens.length; i++) {
                        values[i - 1] = Float.parseFloat(tokens[i]);
                    }
                    logDataList.add(new LogData(depth, values));
                }
            }
        }
        fileScanner.close();
        return logDataList;
    }

    // 对测井数据进行简单运算（加2.0）
    public void processLogData(List<LogData> logDataList) {
        for (LogData logData : logDataList) {
            float[] values = logData.getValues();
            for (int i = 0; i < values.length; i++) {
                values[i] += 2.0f;
            }
            logData.setValues(values);
        }
    }

    // 打印测井数据
    public void printLogData(List<LogData> logDataList) {
        for (LogData logData : logDataList) {
            logData.print();
        }
    }

    // 主方法进行测试
    public static void main(String[] args) {
        String filePath = "D:\\well_logging_data.txt";
        LogDataProcessor processor = new LogDataProcessor(filePath);

        while (true) {
            System.out.println("菜单：");
            System.out.println("1. 查看原始测井数据和处理参数");
            System.out.println("2. 计算泥质含量、孔隙度、饱和度并输出");
            System.out.println("3. 退出");
            System.out.print("请选择一个选项：");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    try {
                        List<LogData> logDataList = processor.readLogData();
                        System.out.println("原始测井数据：");
                        processor.printLogData(logDataList);
                    } catch (FileNotFoundException e) {
                        System.err.println("找不到数据文件：" + filePath);
                    }
                    break;
                case 2:
                    try {
                        List<LogData> logDataList = processor.readLogData();
                        processor.processLogData(logDataList);
                        System.out.println("处理后的测井数据：");
                        processor.printLogData(logDataList);
                    } catch (FileNotFoundException e) {
                        System.err.println("找不到数据文件：" + filePath);
                    }
                    break;
                case 3:
                    System.out.println("退出程序");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("无效的选项，请重新选择");
                    break;
            }
        }
    }
}

class LogData {
    private float depth;
    private float[] values;

    public LogData(float depth, float[] values) {
        this.depth = depth;
        this.values = values;
    }

    public float getDepth() {
        return depth;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public void print() {
        System.out.printf("%.4f", depth);
        for (float value : values) {
            System.out.printf(" %.4f", value);
        }
        System.out.println();
    }
}
