package handler.edit;


import bean.ClientRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 修改http和https报文
 */
public class Editor {
    public static Object editRequest(ClientRequest clientRequest, Object msg) throws IOException {
        HttpRequest new_msg = (HttpRequest) msg;
        String host = clientRequest.getHost();
        if("wttr.in".equals(host)){
            new_msg.setUri("/Guangzhou?0");
            writeLog(new_msg.toString());
        }
        return new_msg;
    }

    private static void writeLog(String str) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("test.log"));
        bw.append(str);
        bw.newLine();
        bw.flush();
    }
}
