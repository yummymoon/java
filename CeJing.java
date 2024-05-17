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
                    System.out.println("���ݴ�����ɣ�����Ѵ����ļ� " + ipOutputFilePath);
                } catch (FileNotFoundException e) {
                    System.err.println("�ļ�δ�ҵ�: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("��ȡ��д���ļ�ʱ����: " + e.getMessage());
                }*/
            
            try (BufferedReader reader = new BufferedReader(new FileReader(ipInputFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(ipOutputFilePath))){
                String line;
                while((line=reader.readLine())!=null){
                    System.out.println(line);
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println("���ݴ�����ɣ�����Ѵ����ļ� " + ipOutputFilePath);
            }catch(IOException e){
                System.err.println("Error occurred while reading the file." + e.getMessage());
            }
        }
    }

    class Calculate extends InputOut{
        private Map<String, List<Double>> dataMap; // �� dataMap �������༶��
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
                    //���Ʊ���ָ��ַ���
                    String[]parts = line.split("\\t");
                    //���
                    int note = Integer.parseInt(parts[0]);
                    //����
                    String name = parts[1];
                    //ֵ
                    double value = Double.parseDouble(parts[2]);
                    dataMap.put(name, value);

                    System.out.println("��ţ�" + note + " ���ƣ�" + name + " ֵ��" + value);
                }
                System.out.println("��ȡ�����ļ����");
            } catch (IOException e) {
                System.err.println("Error occurred while reading the file." + e.getMessage());
            }

            Map parameters = readParameters(parameterFile);
            List<String> rows = getRows(ipFilePath, x, y);
            
            if (!rows.isEmpty()) {
                for (String row : rows) {
                    //����VSH
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
                    System.out.println("��"+ row + "�е����ʺ���Ϊ:" + VSH + "%");

                    //����POR
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
                    System.out.println("��"+ row + "�еĿ�϶��Ϊ:" + POR + "%");
                    
                    //����So
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
                    System.out.println("��"+ row + "�еĺ��ͱ��Ͷ�Ϊ:" + Sw + "%");
                }
            } else {
                System.out.println("�ļ���ȡʧ��");
            }
        }


        private static Map<String, Double> readParameters(String filePath) {
            Map map = new HashMap<>();
            try {
                Files.lines(filePath)
                    .forEach(line -> {
                        if (!line.startsWith("#")) { //��ע����
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
            String outputFile="D:/������22203_Results_02.txt";
            /*System.out.print("������һ������:");
            Scanner reader=new Scanner(System.in);
            System.out.println(reader);
            try(
                Scanner scanner = new Scanner(System.in);
                PrintWriter writer = new PrintWriter(outputFile)){
                System.out.println("���������ݣ�����'exit'��������");
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if("exit".equalsIgnoreCase(line){
                        break;
                    }
                    writer.println(line);
                }
                System.out.println("�����Ѵ����ļ� " + outputFile);
            }catch(IOException e){
                System.err.println("д���ļ�ʱ����: " + e.getMessage());
            }*/
            System.out.println("--------�˵�--------");
            System.out.println("1.��ȡ����ӡ�⾮����");
            System.out.println("2.����⾮���ݲ��������");
            System.out.println("3.��ѯ��ȵ㴦��ɹ�������");
            System.out.println("4.��ʾ��ȶ����ʺ�������϶�Ⱥͺ��ͱ��Ͷȵ�����Сֵ�����ֵ��ƽ��ֵ");
            System.out.println("5.��������ͱ��ͶȽ��дӴ�С˳�����к�Ĵ����Ĳ⾮����");
            System.out.println("6.��ѯ��ͬ�ȼ�������ȵ�Ĵ���ɹ������Լ���Ŀ");
            System.out.println("7.�˳�����");
            System.out.println("��ѡ����Ҫ���е���Ŀ��");
        
            Scanner reader=new Scanner(System.in);
            int choice=reader.nextInt();

            switch(choice){
                case 1:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("��ȡ����ӡ�⾮����:");
                    InputOut inputOut=new InputOut(inputFile,outputFile);
                    break;
                case 2:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("����⾮���ݲ��������:");
                    Calculate calculate=new Calculate(inputFile,outputFile,"D:/parameter.txt",0,500);
                    break;
                case 3:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("��ѯ��ȵ㴦��ɹ�������:");
                    System.out.println("��������Ҫ��ѯ����ȵ�ı��:");
                    Scanner reader1 = new Scanner(System.in);
                    int number = reader1.nextInt();
                    Display display = new Display(inputFile,outputFile,"D:/parameter.txt",number,number);
                    break;
                case 4:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("��ʾ��ȶ����ʺ�������϶�Ⱥͺ��ͱ��Ͷȵ�����Сֵ�����ֵ��ƽ��ֵ:");
                    break;
                case 5:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("��������ͱ��ͶȽ��дӴ�С˳�����к�Ĵ����Ĳ⾮����:");
                    break;
                case 6:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("��ѯ��ͬ�ȼ�������ȵ�Ĵ���ɹ������Լ���Ŀ:");
                    break;
                case 7:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("�˳�����");
                    break;
                default:
                    System.out.println("--------�ֽ���--------");
                    System.out.println("�����������������룡");
                    break;
            }
        }
}