import java.io.*;
import java.util.*;

public class LogDataReaderWriter {
    private String filePath;
    public LogDataReaderWriter(String filePath) {
        this.filePath = filePath;
    }

    // 从文件读取测井数据
    public List<String> readLogData() throws FileNotFoundException {
        List<String> logData = new ArrayList<>();
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(filePath));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                // 检查行是否以数字开头，如果是，则添加到测井数据列表中
                if (line.matches("^\\d.*$")) {
                    logData.add(line);
                }
            }
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }
        return logData;
    }

    public void printLogData(List<String> logData) {
        for (String line : logData) {
            System.out.println(line);
        }
    }
    // 将测井数据写入文件
    public void writeLogData(List<String> logData) throws FileNotFoundException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String line : logData) {
                writer.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 对测井数据进行简单运算（加2.0）
    public void processLogData(List<String> logData) {
        for (int i = 0; i < logData.size(); i++) {
            String line = logData.get(i);
            String[] tokens = line.trim().split("\\s+");
            for (int j = 1; j < tokens.length; j++) { // 跳过深度列
                try {
                    float value = Float.parseFloat(tokens[j]);
                    value += 2.0f;
                    tokens[j] = String.format("%.4f", value);
                } catch (NumberFormatException e) {
                    // 如果数据格式不正确，则捕获异常并打印错误消息
                    System.err.println("无法解析为浮点数：" + line);
                }
            }
            logData.set(i, String.join(" ", tokens));
        }
    }

    // 测试
    public static void main(String[] args) {
        String filePath = "D:/well_logging_data.txt";
        LogDataReaderWriter readerWriter = new LogDataReaderWriter(filePath);
        try {
            List<String> logData = readerWriter.readLogData();
            readerWriter.processLogData(logData);
            readerWriter.printLogData(logData); // 打印测井数据到屏幕
            readerWriter.writeLogData(logData);
            System.out.println("文件处理完成");
        } catch (FileNotFoundException e) {
            System.err.println("找不到数据文件：" + filePath);
        }
    }
    
}
