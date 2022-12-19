package de.jcm.discordgamesdk.impl.channel;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WindowsDiscordChannel implements DiscordChannel {
    private final FileChannel channel;
    private boolean blocking = true;

    public WindowsDiscordChannel() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("\\\\?\\pipe\\discord-ipc-0", "rw");
        channel = raf.getChannel();
    }

    public void close() throws IOException {
        channel.close();
    }

    public void configureBlocking(boolean block) throws IOException {
        blocking = block;
    }

    public int read(ByteBuffer dst) throws IOException {
        System.out.println("read1 start");
        if (!blocking && (channel.size() - channel.position()) < dst.remaining())
        {
            System.out.println("read1 done 0");
            return 0;
        }
        int res = channel.read(dst);
        System.out.println("read1 done " + res);
        return res;
    }

    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        System.out.println("read2 start");
        long remaining = 0;
        if (!blocking)
        {
            for (int i = offset; i < offset+length; i++)
            {
                remaining += dsts[i].remaining();
            }
            if ((channel.size() - channel.position()) < remaining)
            {
                System.out.println("read2 done 0");
                return 0;
            }
        }
        long res = channel.read(dsts, offset, length);
        System.out.println("read2 done " + res);
        return res;
    }

    public int write(ByteBuffer src) throws IOException {
        System.out.println("write start");
        int res = channel.write(src);
        channel.force(false); // ensure that data is actually written to file
        System.out.println("write done");
        return res;
    }
}
