package wl;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.crail.CrailBuffer;
import org.apache.crail.CrailBufferedInputStream;
import org.apache.crail.CrailBufferedOutputStream;
import org.apache.crail.CrailFile;
import org.apache.crail.CrailLocationClass;
import org.apache.crail.CrailNodeType;
import org.apache.crail.CrailOutputStream;
import org.apache.crail.CrailStorageClass;
import org.apache.crail.CrailStore;
import org.apache.crail.conf.CrailConfiguration;
import org.apache.crail.conf.CrailConstants;

import com.sun.net.httpserver.HttpExchange;

public class ObjectStorage {
    
    private CrailStore fs;

    public ObjectStorage() throws Exception {
        this.fs = null;
    }

    public void openSystem() throws Exception {
        if (fs == null)
        {
            CrailConfiguration conf = CrailConfiguration.createConfigurationFromFile();
            this.fs = CrailStore.newInstance(conf);
        }
            
    }

    public void closeSystem() throws Exception {
        if (fs != null) {
            fs.close();
            fs = null;
        }
    }
    
    
    public int Put(HttpExchange httpExchange) throws Exception {
        String filename = httpExchange.getRequestURI().toURL().getFile();
        InputStream body = httpExchange.getRequestBody();
        CrailFile file = fs.create(filename, CrailNodeType.DATAFILE,
            CrailStorageClass.get(0), CrailLocationClass.get(0), false).get().asFile();

        CrailBuffer buf = fs.allocateBuffer();
        body.reset();
        byte[] bytes = new byte[body.available()];
        DataInputStream datain = new DataInputStream(body);
        datain.readFully(bytes);
        buf.put(bytes);
        CrailBufferedOutputStream bufffStream = file.getBufferedOutputStream(CrailConstants.BUFFER_SIZE);
        bufffStream.write(buf.getByteBuffer());
        return 200;
    }

    public int Get(HttpExchange httpExchange) throws Exception {
        // 组装文件名
        String filename = httpExchange.getRequestURI().toURL().getFile();
        CrailFile file = fs.lookup(filename).get().asFile();
        CrailBuffer buf = fs.allocateBuffer();
        
        CrailBufferedInputStream bufferedStream = file.getBufferedInputStream(file.getCapacity());
        httpExchange.sendResponseHeaders(200, file.getCapacity());
        double ret = (double) bufferedStream.read(buf.getByteBuffer());
        if(ret < 0){
            System.err.printf("erro not read file ");
        }
        BufferedOutputStream out = new BufferedOutputStream(httpExchange.getResponseBody());
        buf.getByteBuffer().flip();
        int len =buf.getByteBuffer().limit() - buf.getByteBuffer().position();
        for(int i = 0; i < len; i++) {
            out.write(buf.getByteBuffer().get());
        }
        out.flush();
        out.close();


        return 200;
    }

}


