    import java.io.*;
    import java.util.*;

    class InputOut{
        private String ipInputFilePath;
        private String ipOutputFilePath;
        public InputOut(String ipFilePath,String ipOutputFilePath){
            this.ipInputFilePath=ipFilePath;
            System.out.println("Input Path: " + ipInputFilePath);
            System.out.println("Output Path: " + ipOutputFilePath);
            
            /*try (
                    Scanner scanner = new Scanner(new FileInputStream(ipInputFilePath));
                    PrintWriter writer = new PrintWriter(new FileOutputStream(ipOutputFilePath))
                ) {
                    while (scanner.hasNextDouble()) {
                        double number = scanner.nextDouble();
                        double result = number + 2;
                        writer.println(result);
                        System.out.println(result);
                    }
                    System.out.println("数据处理完成，结果已存入文件 " + ipOutputFilePath);
                } catch (FileNotFoundException e) {
                    System.err.println("文件未找到: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("读取或写入文件时出错: " + e.getMessage());
                }*/
            
            try (BufferedReader reader = new BufferedReader(new FileReader(ipInputFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(ipOutputFilePath))){
                String line;
                while((line=reader.readLine())!=null){
                    System.out.println(line);
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("数据处理完成，结果已存入文件 " + ipOutputFilePath);
            }catch(IOException e){
                System.err.println("Error occurred while reading the file." + e.getMessage());
            }
        }
    }

    class Calculate extends InputOut{
        private Map<String, List<Double>> dataMap; // 将 dataMap 声明在类级别
        public Calculate(String ipFilePath, String ipOutputFilePath,String Parameterpath,int Start,int End) throws FileNotFoundException, IOException {
            super(ipFilePath, ipOutputFilePath);
            this.dataMap = new HashMap<>();
            
            String x = Start;
            String y = End;
            String parameterpath = Parameterpath;            
            System.out.println("Input Path: " + ipFilePath);
            System.out.println("Output Path: " + ipOutputFilePath);
            System.out.println("ParaMeter Path: " + Parameterpath);

            try (BufferedReader reader = new BufferedReader(new FileReader(parameterpath))){
                String line;
                while((line = reader.readLine())!=null){
                    //以制表符分割字符串
                    String[]parts = line.split("\\t");
                    //序号
                    int note = Integer.parseInt(parts[0]);
                    //名称
                    String name = parts[1];
                    //值
                    double value = Double.parseDouble(parts[2]);
                    dataMap.put(name, value);

                    System.out.println("编号：" + note + " 名称：" + name + " 值：" + value);
                }
                System.out.println("读取参数文件完成");
            } catch (IOException e) {
                System.err.println("Error occurred while reading the file." + e.getMessage());
            }

            Map parameters = readParameters(parameterFile);
            List<String> rows = getRows(ipFilePath, x, y);
            
            if (!rows.isEmpty()) {
                for (String row : rows) {
                    //计算VSH
                    double grValue = Double.parseDouble(row.split("\\s+")[2]);
                    double gcUR = parameters.getOrDefault("GCUR");
                    double grValueMax = parameters.getOrDefault("GRmax");
                    double grValueMin = parameters.getOrDefault("GRmin");
                    double SH = (grValue - grValueMin) / (grValueMax - grValueMin);
                    double aVSH = (Math.pow(2,gcUR*SH)-1)/(Math.pow(2,gcUR)-1);
                    double VSH = aVSH*100;
                    
                    if (VSH<0){
                    	VSH = 0;
                    }
                    else if (VSH>100){
                    	VSH = 100;
                    }
                    
                    dataMap.put("VSH", VSH);
                    System.out.println("第"+ row + "行的泥质含量为:" + VSH + "%");

                    //计算POR
                    double dtvalue = Double.parseDouble(row.split("\\s+")[1]);
                    double dtvalueMax = parameters.getOrDefault("DTf");
                    double dtvalueMin = parameters.getOrDefault("DTma");
                    double aPOR = (dtvalue - dtvalueMin) / (dtvalueMax - dtvalueMin);
                    double POR = aPOR * 100;
                    
                    if (POR<0){
                    	POR = 0.5;
                    }
                    else if (POR>40){
                    	POR = 40;
                    }
                    dataMap.put("POR", POR);
                    System.out.println("第"+ row + "行的孔隙度为:" + POR + "%");
                    
                    //计算So
                    double rValue = Double.parseDouble(row.split("\\s+")[3]);
                    double ra = parameters.getOrDefault("a");
                    double rb = parameters.getOrDefault("b");
                    double rm = parameters.getOrDefault("m");
                    double rn = parameters.getOrDefault("n");
                    double rRw = parameters.getOrDefault("Rw");
                    double x = Math.pow(POR, rm);
                    double aSw = Math.pow(((ra*rb*rRw)/(x*rValue)),1/rn);
                    double Sw = aSw * 100;
                    
                    if (Sw<0){
                    	Sw = 0;
                    }
                    else if (Sw>100){
                    	Sw = 100;
                    }
                    dataMap.put("Sw", Sw);
                    System.out.println("第"+ row + "行的含油饱和度为:" + Sw + "%");
                }
            } else {
                System.out.println("文件读取失败");
            }
        }


        private static Map<String, Double> readParameters(String filePath) {
            Map map = new HashMap<>();
            try {
                Files.lines(filePath)
                    .forEach(line -> {
                        if (!line.startsWith("#")) { //过注释行
                            String[] parts = line.split(" ");
                            map.put(parts[0], Double.parseDouble(parts[1]));
                        }
                    });
                    }
             catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }

        private List<String> getRows(String filePath, int start, int end) {
            List<String> rows = new ArrayList<>();
            try (BufferedReader reader = new FileReader(filePath)) {
                for (int lineCount = 0; (String line = reader.readLine(); line != null; lineCount++)) {
                    if (lineCount >= start && lineCount <= end) {
                        rows.add(line);
                    }
                    if (lineCount > end) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rows;
        }



        if (dataMap.containsKey("DTf")){
            List<Double> dtfList = dataMap.get("DTf");
            List<Double> dtfMax = dataMap.getOrDefault("DTma", Collections.emptyList());
            List<Double> dtfAveraged = dataMap.getOrDefault("DT", Collections.emptyList());


        }
}
    
    class Display extends InputOut{
        public Display(String ipFilePath,String ipOutputFilePath,String Parameterpath,int Start,int End){
            super(ipFilePath, ipOutputFilePath);
            
            String x = Start;
            String y = End;
            String parameterpath = Parameterpath;            
            System.out.println("Input Path: " + ipFilePath);
            System.out.println("Output Path: " + ipOutputFilePath);
            System.out.println("ParaMeter Path: " + Parameterpath);
            
            Calculate calculate=new Calculate(ipFilePath,ipOutputFilePath,"D:/parameter.txt",Start,End);
            
    }
    
public class CeJing {
    public static void main(String[] args) throws IOException{
            String inputFile="D:/well_logging_data.txt";
            String outputFile="D:/大数据22203_Results_02.txt";
            /*System.out.print("请输入一段数据:");
            Scanner reader=new Scanner(System.in);
            System.out.println(reader);
            try(
                Scanner scanner = new Scanner(System.in);
                PrintWriter writer = new PrintWriter(outputFile)){
                System.out.println("请输入数据（输入'exit'结束）：");
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if("exit".equalsIgnoreCase(line){
                        break;
                    }
                    writer.println(line);
                }
                System.out.println("数据已存入文件 " + outputFile);
            }catch(IOException e){
                System.err.println("写入文件时出错: " + e.getMessage());
            }*/
            System.out.println("--------菜单--------");
            System.out.println("1.读取并打印测井数据");
            System.out.println("2.计算测井数据并输出保存");
            System.out.println("3.查询深度点处理成果数据条");
            System.out.println("4.显示深度段泥质含量，孔隙度和含油饱和度党的最小值，最大值和平均值");
            System.out.println("5.输出按含油饱和度进行从大到小顺序排列后的处理后的测井数据");
            System.out.println("6.查询不同等级储层深度点的处理成果数据以及数目");
            System.out.println("7.退出程序");
            System.out.println("请选择你要进行的项目：");
        
            Scanner reader=new Scanner(System.in);
            int choice=reader.nextInt();

            switch(choice){
                case 1:
                    System.out.println("--------分界线--------");
                    System.out.println("读取并打印测井数据:");
                    InputOut inputOut=new InputOut(inputFile,outputFile);
                    break;
                case 2:
                    System.out.println("--------分界线--------");
                    System.out.println("计算测井数据并输出保存:");
                    Calculate calculate=new Calculate(inputFile,outputFile,"D:/parameter.txt",0,500);
                    break;
                case 3:
                    System.out.println("--------分界线--------");
                    System.out.println("查询深度点处理成果数据条:");
                    System.out.println("请输入你要查询的深度点的编号:");
                    Scanner reader1 = new Scanner(System.in);
                    int number = reader1.nextInt();
                    Display display = new Display(inputFile,outputFile,"D:/parameter.txt",number,number);
                    break;
                case 4:
                    System.out.println("--------分界线--------");
                    System.out.println("显示深度段泥质含量，孔隙度和含油饱和度党的最小值，最大值和平均值:");
                    break;
                case 5:
                    System.out.println("--------分界线--------");
                    System.out.println("输出按含油饱和度进行从大到小顺序排列后的处理后的测井数据:");
                    break;
                case 6:
                    System.out.println("--------分界线--------");
                    System.out.println("查询不同等级储层深度点的处理成果数据以及数目:");
                    break;
                case 7:
                    System.out.println("--------分界线--------");
                    System.out.println("退出程序。");
                    break;
                default:
                    System.out.println("--------分界线--------");
                    System.out.println("输入有误，请重新输入！");
                    break;
            }
        }
}