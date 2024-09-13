package com.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class PaperSimilarityChecker {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        try {
            System.out.print("请输入第一个文件的地址：");
            String file1 = scanner.nextLine();
            System.out.print("请输入第二个文件的地址：");
            String file2 = scanner.nextLine();
            Map<String, Integer> segments1 = segmentAndCount(file1);
            Map<String, Integer> segments2 = segmentAndCount(file2);
            System.out.println("对比文件位置如下：");
            System.out.println(file1);
            System.out.println(file2);

            double overallSimilarity = calculateOverallSimilarity(segments1, segments2);
            DecimalFormat df = new DecimalFormat("0.00");
            String rs = df.format(overallSimilarity*100);
            System.out.println("以首个文件为基准,计算可得：\n"+"论文整体重复率为：" + rs + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> segmentAndCount(String filePath) throws IOException {
        Map<String, Integer> segments = new HashMap<>();
        String content = readFile(filePath);

        //消除非文本，减少空格换行符等非必要因素的干扰
        content=  content.replaceAll("\\s*|\r|\n|\t","");

        // 使用正则表达式进行文本切分
        String[] splitContent = content.split("(?<=[，。；！？])");

        //对切分的文段进行处理，将>=13的文段分为一段，不足则补足，并对文段文本精简后计数，以便后续使用
        for (int i = 0; i < splitContent.length; i++) {
            if (splitContent[i].length() >= 13) {
                splitContent[i] = splitContent[i].replaceAll("[，。；！？”“：、]","");
                segments.put(splitContent[i], splitContent[i].length()-1);
            }
            else {
                splitContent[i] = splitContent[i].replaceAll("[，。；！？”“、：]","");
                splitContent[i+1] = splitContent[i+1].replaceAll("[，。；！？”“：、]","");
                segments.put(splitContent[i] +
                        splitContent[i+1], splitContent[i].length()+splitContent[i+1].length()-1);
                i++;
            }
        }

        return segments;
    }

    private static double calculateOverallSimilarity(Map<String, Integer> segments1, Map<String, Integer> segments2) {
        double totalSimilarity = 0;
        int totalSegments = segments1.size();
        //计算重复率
        for (Map.Entry<String, Integer> entry : segments1.entrySet()) {
            String segment = entry.getKey();
            int count1 = entry.getValue();
            //保存两文段的最大匹配数，以便合理统计重复率
            int countMax = 0,tem;

            // 这里简化处理，只考虑文段之间的最大重复度
            for (Map.Entry<String, Integer> entry2 : segments2.entrySet()) {
                tem = matchStringByHashSet(segment,entry2.getKey());
                if (tem > countMax) {countMax = tem;}
            }

            //计算每一文段的重复率，并求和得到全体重复率
            double segmentSimilarity = (double) countMax /count1;
            totalSimilarity += segmentSimilarity;
        }

        // 防止除以零
        if (totalSegments == 0) {
            return 0;
        }
        //计算论文重复度
        return totalSimilarity / totalSegments;
    }

    // 辅助函数，用于读取文件内容
    private static String readFile(String filePath) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        }
        return fileContents.toString();
    }
    //方法1、通过HashSet存储字符串，并逐个匹配另一个字符串
    private static int matchStringByHashSet( String parent,String child )
    {
        int count = 0;
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < parent.length(); i++) {
            set.add(parent.charAt(i));
        }
        for (int i = 0; i < child.length(); i++) {
            if (set.contains(child.charAt(i))) {
                count+=1;
            }
        }
        return count;						  //结果输出
    }

}
