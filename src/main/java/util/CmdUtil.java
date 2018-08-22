package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/12.
 */
public class CmdUtil {
    public static boolean exec(String cmd) {
        try {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd}).destroy();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static List<String> execWithOutput(String cmd) {
        List<String> list = null;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            list = new ArrayList<>();
            String result;
            while ((result = bs.readLine()) != null) {
                list.add(result);
            }
            in.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }
}
