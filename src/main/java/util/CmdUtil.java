package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VOYAGER on 2018/8/12.
 */
public class CmdUtil {
    public static boolean exec(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});//.destroy();
            p.waitFor();
            return true;
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
        return false;
    }

    public static boolean execInAdmin(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("su admin");
//            Runtime.getRuntime().exec(cmd+";th test.lua");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.flush();
            os.close();
            p.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
