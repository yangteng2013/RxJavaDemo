package com.noe.dex;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by huiyh on 2016/5/17.
 */
public class IOUtils {

    public static void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
