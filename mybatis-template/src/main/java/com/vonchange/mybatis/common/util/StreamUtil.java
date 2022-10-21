// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package com.vonchange.mybatis.common.util;


import com.vonchange.mybatis.common.util.io.FastCharArrayWriter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


/**
 * Optimized byte and character stream utilities.
 */
public class StreamUtil {
	private static final int ZERO = 0;
	private static final int NEGATIVE_ONE = -1;
	private static final int ALL = -1;
	public static int ioBufferSizeX = 16384;
	/**
	 * Closes silently the closable object. If it is {@link Flushable}, it
	 * will be flushed first. No exception will be thrown if an I/O error occurs.
	 */
	public static void close(final Closeable closeable) {
		if (closeable != null) {
			if (closeable instanceof Flushable) {
				try {
					((Flushable) closeable).flush();
				} catch (IOException ignored) {
				}
			}

			try {
				closeable.close();
			} catch (IOException ignored) {
			}
		}
	}


	public static FastCharArrayWriter copy(final InputStream input, final String encoding) throws IOException {
		return copy(input, encoding, ALL);
	}
	public static FastCharArrayWriter copy(final InputStream input, final String encoding, final int count) throws IOException {
		try (FastCharArrayWriter output = createFastCharArrayWriter()) {
			copy(input, output, encoding, count);
			return output;
		}
	}
	private static FastCharArrayWriter createFastCharArrayWriter() {
		return new FastCharArrayWriter(bufferSize());
	}

	private static int bufferSize() {
		return ioBufferSizeX;
	}
	private static int bufferSize(final int count) {
		final int ioBufferSize = ioBufferSizeX;
		if (count < ioBufferSize) {
			return count;
		} else {
			return ioBufferSize;
		}
	}
	public static <T extends Writer> T copy(final InputStream input, final T output, final String encoding, final int count) throws IOException {
		copy(inputStreamReadeOf(input, encoding), output, count);
		return output;
	}
	public static InputStreamReader inputStreamReadeOf(final InputStream input, final String encoding) throws UnsupportedEncodingException {
		return new InputStreamReader(input, encoding);
	}
	public static int copy(final Reader input, final Writer output, final int count) throws IOException {
		if (count == ALL) {
			return copy(input, output);
		}

		int numToRead = count;
		char[] buffer = new char[numToRead];

		int totalRead = ZERO;
		int read;

		while (numToRead > ZERO) {
			read = input.read(buffer, ZERO, bufferSize(numToRead));
			if (read == NEGATIVE_ONE) {
				break;
			}
			output.write(buffer, ZERO, read);

			numToRead = numToRead - read;
			totalRead = totalRead + read;
		}

		output.flush();
		return totalRead;
	}
	public static int copy(final Reader input, final Writer output) throws IOException {
		int numToRead = bufferSize();
		char[] buffer = new char[numToRead];

		int totalRead = ZERO;
		int read;

		while ((read = input.read(buffer, ZERO, numToRead)) >= ZERO) {
			output.write(buffer, ZERO, read);
			totalRead = totalRead + read;
		}

		output.flush();
		return totalRead;
	}



}
