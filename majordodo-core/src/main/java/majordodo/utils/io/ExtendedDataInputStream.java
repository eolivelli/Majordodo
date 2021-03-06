/*
 Licensed to Diennea S.r.l. under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. Diennea S.r.l. licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

 */
package majordodo.utils.io;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extended version of DataInputStream. Backport from HerdDB.
 *
 * @author enrico.olivelli
 */
public class ExtendedDataInputStream extends DataInputStream {

    public ExtendedDataInputStream(InputStream in) {
        super(in);
    }

    /**
     * Reads an int stored in variable-length format. Reads between one and five bytes. Smaller values take fewer bytes.
     * Negative numbers are not supported.
     */
    public int readVInt() throws IOException {
        byte b = readByte();
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7F) << shift;
        }
        return i;
    }

    private boolean eof;

    /**
     * @see #readVIntNoEOFException()
     * @return
     */
    public boolean isEof() {
        return eof;
    }

    /**
     * Same as {@link  #readVInt() } but does not throw EOFException. Since throwing exceptions is very expensive for the
     * JVM this operation is preferred if you could hit and EOF
     *
     * @return
     * @throws IOException
     * @see #isEof()
     */
    public int readVIntNoEOFException() throws IOException {
        int ch = in.read();
        if (ch < 0) {
            eof = true;
            return -1;
        }

        byte b = (byte) (ch);
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7F) << shift;
        }
        return i;
    }

    /**
     * Reads a long stored in variable-length format. Reads between one and nine bytes. Smaller values take fewer bytes.
     * Negative numbers are not supported.
     */
    public long readVLong() throws IOException {
        return readVLong(false);
    }

    private long readVLong(boolean allowNegative) throws IOException {
        /* This is the original code of this method,
     * but a Hotspot bug (see LUCENE-2975) corrupts the for-loop if
     * readByte() is inlined. So the loop was unwinded!
    byte b = readByte();
    long i = b & 0x7F;
    for (int shift = 7; (b & 0x80) != 0; shift += 7) {
      b = readByte();
      i |= (b & 0x7FL) << shift;
    }
    return i;
         */
        byte b = readByte();
        if (b >= 0) {
            return b;
        }
        long i = b & 0x7FL;
        b = readByte();
        i |= (b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 56;
        if (b >= 0) {
            return i;
        }
        if (allowNegative) {
            b = readByte();
            i |= (b & 0x7FL) << 63;
            if (b == 0 || b == 1) {
                return i;
            }
            throw new IOException("Invalid vLong detected (more than 64 bits)");
        } else {
            throw new IOException("Invalid vLong detected (negative values disallowed)");
        }
    }

    private static final byte[] EMPTY_ARRAY = new byte[0];

    public byte[] readArray() throws IOException {
        int len = readVInt();
        if (len == 0) {
            return EMPTY_ARRAY;
        }
        byte[] res = new byte[len];
        readFully(res);
        return res;
    }

    @SuppressFBWarnings(value = "SR_NOT_CHECKED")
    public void skipArray() throws IOException {
        int len = readVInt();
        if (len == 0) {
            return;
        }
        skip(len);
    }

    @SuppressFBWarnings(value = "SR_NOT_CHECKED")
    public void skipInt() throws IOException {
        skip(4);
    }

    @SuppressFBWarnings(value = "SR_NOT_CHECKED")
    public void skipLong() throws IOException {
        skip(8);
    }

    @SuppressFBWarnings(value = "SR_NOT_CHECKED")
    public void skipDouble() throws IOException {
        skip(8);
    }

    @SuppressFBWarnings(value = "SR_NOT_CHECKED")
    public void skipBoolean() throws IOException {
        skip(1);
    }

}
