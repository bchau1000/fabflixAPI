import java.io.*;

public class log_processor {
    public static Long getAvg(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        long total = 0;
        int count = 0;

        while ((line = br.readLine()) != null) {
            total += Long.parseLong(line);
            count++;
        }

        return total/count;
    }

    public static void main(String[] args) {
        File tsFile = new File("ts.txt");
        File tjFile = new File("tj.txt");

        try {
            System.out.println("Average Search: " + getAvg(tsFile));
            System.out.println("Average JDBC: " + getAvg(tjFile));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
