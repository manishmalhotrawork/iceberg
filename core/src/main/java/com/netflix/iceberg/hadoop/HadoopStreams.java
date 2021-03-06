/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package com.netflix.iceberg.hadoop;

import com.netflix.iceberg.io.DelegatingInputStream;
import com.netflix.iceberg.io.DelegatingOutputStream;
import com.netflix.iceberg.io.PositionOutputStream;
import com.netflix.iceberg.io.SeekableInputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Convenience methods to get Parquet abstractions for Hadoop data streams.
 *
 * This class is based on Parquet's HadoopStreams.
 */
class HadoopStreams {
  private static final Logger LOG = LoggerFactory.getLogger(HadoopStreams.class);

  /**
   * Wraps a {@link FSDataInputStream} in a {@link SeekableInputStream} implementation for readers.
   *
   * @param stream a Hadoop FSDataInputStream
   * @return a SeekableInputStream
   */
  static SeekableInputStream wrap(FSDataInputStream stream) {
    return new HadoopSeekableInputStream(stream);
  }

  /**
   * Wraps a {@link FSDataOutputStream} in a {@link PositionOutputStream} implementation for
   * writers.
   *
   * @param stream a Hadoop FSDataOutputStream
   * @return a PositionOutputStream
   */
  static PositionOutputStream wrap(FSDataOutputStream stream) {
    return new HadoopPositionOutputStream(stream);
  }

  /**
   * SeekableInputStream implementation for FSDataInputStream that implements ByteBufferReadable in
   * Hadoop 2.
   */
  private static class HadoopSeekableInputStream extends SeekableInputStream implements DelegatingInputStream {
    private final FSDataInputStream stream;

    HadoopSeekableInputStream(FSDataInputStream stream) {
      this.stream = stream;
    }

    @Override
    public InputStream getDelegate() {
      return stream;
    }

    @Override
    public void close() throws IOException {
      stream.close();
    }

    @Override
    public long getPos() throws IOException {
      return stream.getPos();
    }

    @Override
    public void seek(long newPos) throws IOException {
      stream.seek(newPos);
    }

    @Override
    public int read() throws IOException {
      return stream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      return stream.read(b, off, len);
    }

    public int read(ByteBuffer buf) throws IOException {
      return stream.read(buf);
    }
  }

  /**
   * PositionOutputStream implementation for FSDataOutputStream.
   */
  private static class HadoopPositionOutputStream extends PositionOutputStream implements DelegatingOutputStream {
    private final FSDataOutputStream stream;

    public HadoopPositionOutputStream(FSDataOutputStream stream) {
      this.stream = stream;
    }

    @Override
    public OutputStream getDelegate() {
      return stream;
    }

    @Override
    public long getPos() throws IOException {
      return stream.getPos();
    }

    @Override
    public void write(int b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      stream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      stream.flush();
    }

    @Override
    public void close() throws IOException {
      stream.close();
    }
  }
}
