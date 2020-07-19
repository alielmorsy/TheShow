package aie.amg.theshow.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class DecryptOutputStream extends OutputStream {
    private RandomAccessFile raf;

    public DecryptOutputStream(File file) throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, "rwd");
    }

    @Override
    public void write(int b) throws IOException {
        raf.write(b);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        raf.write(b, offset, length);
    }



    public void skip(long position) throws IOException {
        raf.seek(position);
    }
}
