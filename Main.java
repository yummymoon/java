import java.io.*;
import java.util.*;

class Main {
    public static void main(String[] args) {
        String filePath = "D:\\parameters.txt";
        String logdata_Path = "D:\\well_logging_data.txt";
        LogDataProcessor processor = new LogDataProcessor(logdata_Path, filePath);
        while (true) {
            System.out.println("---------菜单----------");
            System.out.println("0. 退出");
            System.out.println("1. 查看原始测井数据和处理参数");
            System.out.println("2. 计算泥质含量、孔隙度、饱和度并输出");
            System.out.println("3. 查看单深度点处理成果数据条");
            System.out.println("4. 统计查看储层参数的极值和平均值");
            System.out.println("5. 按饱和度对处理成果数据表排序");
            System.out.println("6. 统计显示不同等级储层信息");
            System.out.println("7. 统计查看好油层信息");
            System.out.print("请选择一个选项：");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            System.out.println("");

            switch (choice) {
                case 0:
                    System.out.println("退出程序");
                    scanner.close();
                    System.exit(0);
                    break;
                case 1:
                    try {
                        List<LogData> logDataList = processor.readLogData();
                        System.out.println("原始测井数据：");
                        processor.printLogData(logDataList);
                        System.out.println("");
                        System.out.println("参数信息：");
                        processor.parameters.printParameters();
                    } catch (FileNotFoundException e) {
                        System.err.println("找不到数据文件：" + logdata_Path);
                    }
                    break;
                case 2:
                    try {
                        List<LogData> logDataList = processor.readLogData();
                        processor.processLogData(logDataList);
                        System.out.println("处理后的测井数据：");
                        processor.printLogData(logDataList);
                    } catch (FileNotFoundException e) {
                        System.err.println("找不到数据文件：" + logdata_Path);
                    }
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                default:
                    System.out.println("无效的选项，请重新选择");
                    break;
            }
        }
    }
}

class LogDataProcessor {
    private String logdata_Path;
    private String filePath;

    public LogParameters parameters;

    public LogDataProcessor(String logdata_Path, String filePath) {
        this.filePath = filePath;
        this.logdata_Path = logdata_Path;
        this.parameters = new LogParameters();
        try {
            this.parameters.loadParametersFromFile(filePath);
        } catch (IOException e) {
            System.err.println("无法加载参数文件：" + e.getMessage());
        }
    }

    // 从文件读取测井数据，读取第15到30行有depth的数据
    public List<LogData> readLogData() throws FileNotFoundException {
        List<LogData> logDataList = new ArrayList<>();
        Scanner fileScanner = new Scanner(new File(logdata_Path));
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

class LogParameters {
    private Map<String, Float> parameters;

    public LogParameters() {
        parameters = new HashMap<>();
    }

    public void loadParametersFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        // 跳过文件头部
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 3) {
                String name = parts[1];
                float value = Float.parseFloat(parts[2]);
                parameters.put(name, value);
            }
        }
        reader.close();
    }

    public Float getParameter(String name) {
        return parameters.get(name);
    }

    public void printParameters() {
        for (Map.Entry<String, Float> entry : parameters.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
