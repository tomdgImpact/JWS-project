package fr.epita.assistant.jws.domain.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapProcessing {

    public static ArrayList<String> getOpenFile(String path) throws Exception {
        File fp = new File(path);
        FileReader fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        fr.close();
        return lines;
    }
     public static String openFile(String path) throws Exception {
        ArrayList<String> lines = getOpenFile(path);
        return String.join("", lines);
     }

    public static String RLEdecode(final String st) {
        final StringBuilder sb = new StringBuilder();

        final char[] chars = st.toCharArray();

        int i = 0;
        while (i < chars.length) {
            int repeat = 0;
            while ((i < chars.length) && Character.isDigit(chars[i])) {
                repeat = repeat * 10 + chars[i++] - '0';
            }
            final StringBuilder s = new StringBuilder();
            while ((i < chars.length) && !Character.isDigit(chars[i])) {
                s.append(chars[i++]);
            }

            if (repeat > 0) {
                for (int j = 0; j < repeat; j++) {
                    sb.append(s.toString());
                }
            } else {
                sb.append(s.toString());
            }
        }
        return sb.toString();
    }

    public static String RLEencode(String source) {
        StringBuffer dest = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            int runLength = 1;
            while (i+1 < source.length() && source.charAt(i) == source.charAt(i+1)) {
                runLength++;
                i++;
                if (runLength == 9){
                    break;
                }
            }
            dest.append(runLength);
            dest.append(source.charAt(i));
        }
        return dest.toString();
    }

    public static List<String> getArrayMap(String path) throws Exception {
        ArrayList<String> map = getOpenFile(path);
        List<String> newMap = (ArrayList<String>) map
                .stream()
                .map(s -> RLEdecode(s))
                .collect(Collectors.toList());
        return newMap;
    }

}
