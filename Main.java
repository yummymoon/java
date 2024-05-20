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
                case 3:
                case 4:
                case 5:
                    try {
                        List<LogData> logDataList = processor.readLogData();
                        processor.processLogData(logDataList);
                        if (choice == 2) {                           
                            processor.printLogData(logDataList);
                        }else if (choice == 3) { 
                            System.out.print("请输入要查看的深度点(0~15)：");
                            int depth_point = scanner.nextInt();
                            if (depth_point >= 0 && depth_point < 16) {
                                float depth = logDataList.get(depth_point).getDepth();
                                float[]depth_array = logDataList.get(depth_point).getValues();
                                System.out.print(depth);
                                for (int i = 0; i < depth_array.length; i++) {
                                    System.out.print(" " + depth_array[i]);
                                }System.out.println("");
                            }else{
                                System.out.println("深度点不合法");
                            }
                        }else if (choice == 4) {
                            processor.printStatistics(logDataList);
                            break;
                        }else if (choice == 5) {
                            processor.sortByPOR(logDataList);
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("找不到数据文件：" + logdata_Path);
                    }
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
    private double VSHmax = 0;
    private double VSHmin = 0;
    private double PORmax = 0;
    private double PORmin = 0;
    private double SOmax = 0;
    private double SOmin = 0;
    private double VSHall = 0;
    private double PORall = 0;
    private double SOall = 0;
    // private String filePath;

    // 读取参数
    public LogParameters parameters;

    public LogDataProcessor(String logdata_Path, String filePath) {
        // this.filePath = filePath;
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

    // 计算泥质含量、孔隙度、饱和度
    public void processLogData(List<LogData> logDataList) {
        float GRmin = parameters.getParameter("GRmin");
        float GRmax = parameters.getParameter("GRmax");
        float GCUR = parameters.getParameter("GCUR");
        float DTma = parameters.getParameter("DTma");
        float DTf = parameters.getParameter("DTf");
        float a = parameters.getParameter("a");
        float b = parameters.getParameter("b");
        float Rw = parameters.getParameter("Rw");
        float m = parameters.getParameter("m");
        float n = parameters.getParameter("n");
        for (LogData logData : logDataList) {
            float[] values = logData.getValues();

            // 计算泥质含量VSH
            float SH = (values[2]-GRmin) / (GRmax-GRmin);
            double VSH = (Math.pow(2, GCUR*SH) - 1) / (Math.pow(2, GCUR) - 1);
            if (VSH > 1) {
                VSH = 1;
            }else if (VSH < 0) {
                VSH = 0;
            }
            // 计算孔隙度
            double POR = (values[1] - DTma) / (DTf - DTma);
            if (POR > 0.4) {
                POR = 0.4;
            }else if (POR < 0) {    
                POR = 0.005;
            }
            // 计算饱和度
            double Sw = Math.pow(a*b*Rw / (Math.pow(POR,m)*values[3]) , 1/n);
            double SO = 1 - Sw;
            if (SO < 0) {
                SO = 0;
            }else if (SO > 1) {
                SO = 1;
            }
            // 创建一个新的float数组，并设置正确的值
            float[] newValues = new float[values.length + 3];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[values.length] = (float) VSH;
            newValues[values.length + 1] = (float) POR;
            newValues[values.length + 2] = (float) SO;

            logData.setValues(newValues);

            VSHall += VSH;
            PORall += POR;
            SOall += SO;
            if (VSH >= VSHmax) {
                VSHmax = VSH;
            }else {
                VSHmin = VSH;
            }
            if (POR >= PORmax) {
                PORmax = POR;
            }else {
                PORmin = POR;
            }
            if (SO >= SOmax) {
                SOmax = SO;
            }else {
                SOmin = SO;
            }
        }
    }

    // 打印测井数据
    public void printLogData(List<LogData> logDataList) {
        for (LogData logData : logDataList) {
            logData.print();
        }
    }
    
    // 打印极值、平均值
    public void printStatistics(List<LogData> logDataList){
        System.out.println("VSHmax = " + VSHmax);
        System.out.println("VSHmin = " + VSHmin);
        System.out.println("VSHavg = " + VSHall / logDataList.size());
        System.out.println("");
        System.out.println("PORmax = " + PORmax);
        System.out.println("PORmin = " + PORmin);
        System.out.println("PORavg = " + PORall / logDataList.size());
        System.out.println("");
        System.out.println("SOmax = " + SOmax);
        System.out.println("SOmin = " + SOmin);
        System.out.println("SOavg = " + SOall / logDataList.size());
        System.out.println("");
    }
    
    // 按饱和度排序
    public void sortByPOR(List<LogData> logDatalList){
        int[] array= new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        for (int i = 0 ; i < logDatalList.size() - 1 ; i++) {
            for (int j = i + 1 ; j < logDatalList.size(); j++) {
                if (logDatalList.get(i).getValues()[10] < logDatalList.get(j).getValues()[10]) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }for (int i = 0 ; i < logDatalList.size() ; i++) {
            System.out.print(array[i] + " ");
            System.out.println(logDatalList.get(array[i]).getValues()[10]);
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
        for (Map.Entry<String, Float> entry : parameters.entrySet())    {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
