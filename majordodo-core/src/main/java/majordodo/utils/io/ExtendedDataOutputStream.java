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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended version of DataInputStream. Backport from HerdDB.
 *
 * @author enrico.olivelli
 * @see ExtendedDataInputStream
 */
public final class ExtendedDataOutputStream extends DataOutputStream {

    public ExtendedDataOutputStream(OutputStream out) {
        super(out);
    }

    public final void writeVInt(int i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) i);
    }

    /**
     * Writes an long in a variable-length format. Writes between one and nine
     * bytes. Smaller values take fewer bytes. Negative numbers are not
     * supported.
     *
     */
    public final void writeVLong(long i) throws IOException {
        if (i < 0) {
            throw new IllegalArgumentException("cannot write negative vLong (got: " + i + ")");
        }
        writeSignedVLong(i);
    }

    // write a potentially negative vLong
    private void writeSignedVLong(long i) throws IOException {
        while ((i & ~0x7FL) != 0L) {
            writeByte((byte) ((i & 0x7FL) | 0x80L));
            i >>>= 7;
        }
        writeByte((byte) i);
    }
    
    public void writeArray(byte[] data) throws IOException {
        writeVInt(data.length);
        write(data);
    }

    public void writeArray(byte[] data, int offset, int len) throws IOException {
        writeVInt(len);
        write(data, offset ,len);
    }
    
}
