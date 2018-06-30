package me.arowshot.mineconsole.util;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public class CompressionUtil {
    private final byte[] buffer = new byte[8192];
    private final Inflater inflater;
    private final Deflater deflater;
    private int threshold = 0;
    
    public CompressionUtil(int threshold) {
        this.threshold = threshold;
        this.inflater = new Inflater();
        this.deflater = new Deflater();
    }
    
    public void encode(List<Byte> in, List<Byte> out) throws Exception {
        int size = in.size();
        
        if(size<this.threshold) {
            out.addAll(in);
        } else {
            byte[] newBytes = new byte[size];
            int i = 0;
            for(byte b:in) {
                newBytes[i] = b;
                i++;
            }
            
            this.deflater.setInput(newBytes, 0, size);
            this.deflater.finish();
            
            while(!this.deflater.finished()) {
                int len = this.deflater.deflate(this.buffer);
                for(i=0;i<len;i++) {
                    out.add(this.buffer[i]);
                }
            }
            
            this.deflater.reset();
        }
    }
    
    public void decode(List<Byte> in, int size, List<Byte> out) throws DataFormatException {
        if(in.size()!=0) {
            if(size==0) {
                out.addAll(in);
            } else {
                if(size<this.threshold) {
                    throw new RuntimeException("Badly compressed packet - size of " + size + " is below server threshold of " + this.threshold);
                }
                
                if(size>2097152) {
                    throw new RuntimeException("Badly compressed packet - size of " + size + " is larger than protocol maximum of " + 2097152);
                }
                
                byte[] bytes = new byte[in.size()];
                int i = 0;
                for(byte b:in) {
                    bytes[i] = b;
                    i++;
                }
                this.inflater.setInput(bytes);
                byte[] newBytes = new byte[size];
                this.inflater.inflate(newBytes);
                for(byte b:newBytes) {
                    out.add(b);
                }
                this.inflater.reset();
            }
        }
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
