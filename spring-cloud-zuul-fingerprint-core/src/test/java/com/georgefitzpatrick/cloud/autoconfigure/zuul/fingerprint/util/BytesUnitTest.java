/*
 * Copyright 2021 George Fitzpatrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georgefitzpatrick.cloud.autoconfigure.zuul.fingerprint.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author George Fitzpatrick
 */
public class BytesUnitTest {

    /*
     * input:
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     * | 128 |  64 |  32 |  16 |   8 |   4 |   2 |   1 |
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     * |   0 |   0 |   0 |   0 |   0 |   0 |   0 |   1 |
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     *
     * expected = 1
     */
    @Test
    void testToUint8() {
        byte input = 1;
        int expected = 1;

        int actual = Bytes.toUint8(input);

        assertEquals(expected, actual);
    }


    /*
     * input0:
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     * | 32768 | 16384 |  8192 |  4096 |  2048 |  1024 |   512 |   256 |
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     * |     0 |     0 |     0 |     0 |     0 |     0 |     0 |     1 |
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     *
     * input1:
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     * |   128 |    64 |    32 |    16 |     8 |     4 |     2 |     1 |
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     * |     0 |     0 |     0 |     0 |     0 |     0 |     0 |     1 |
     * +-------+-------+-------+-------+-------+-------+-------+-------+
     *
     * expected = 256 + 1 = 257
     */
    @Test
    void testToUint16() {
        byte input0 = 1, input1 = 1;
        int expected = 257;

        int actual = Bytes.toUint16(input0, input1);

        assertEquals(expected, actual);
    }

    /*
     * input0:
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * | 8388608 | 4194304 | 2097152 | 1048576 |  524288 |  262144 |  131072 |   65536 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * |       0 |       0 |       0 |       0 |       0 |       0 |       0 |       1 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     *
     * input1:
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * |   32768 |   16384 |    8192 |    4096 |    2048 |    1024 |     512 |     256 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * |       0 |       0 |       0 |       0 |       0 |       0 |       0 |       1 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     *
     * input2:
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * |     128 |      64 |      32 |      16 |       8 |       4 |       2 |       1 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     * |       0 |       0 |       0 |       0 |       0 |       0 |       0 |       1 |
     * +---------+---------+---------+---------+---------+---------+---------+---------+
     *
     * expected = 65536 + 256 + 1 = 65793
     */
    @Test
    void testToUint24() {
        byte input0 = 1, input1 = 1, input2 = 1;
        int expected = 65793;

        int actual = Bytes.toUint24(input0, input1, input2);

        assertEquals(expected, actual);
    }

    /*
       input:
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     * | 128 |  64 |  32 |  16 |   8 |   4 |   2 |   1 |
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     * |   0 |   0 |   1 |   0 |   0 |   1 |   0 |   0 |
     * +-----+-----+-----+-----+-----+-----+-----+-----+
     *
     * multiple of 16:               remainder:
     * +-----+-----+-----+-----+     +-----+-----+-----+-----+
     * | 128 |  64 |  32 |  16 |     |   8 |   4 |   2 |   1 |
     * +-----+-----+-----+-----+     +-----+-----+-----+-----+
     * |   0 |   0 |   1 |   0 |     |   0 |   1 |   0 |   0 |
     * +-----+-----+-----+-----+     +-----+-----+-----+-----+
     *
     * (0 + 0 + 32 + 0) / 16 = 2     0 + 4 + 0 + 0 = 4
     *
     * expected = 24
     */
    @Test
    void testToHexString_singleByte() {
        byte input = 36;
        String expected = "24";

        String actual = Bytes.toHexString(input);

        assertEquals(expected, actual);
    }

    @Test
    void testToHexString_multipleBytes() {
        byte[] input = {36, 28, 45, 23, 81, 1, 34, 6, 84, 54};
        String expected = "241C2D17510122065436";

        String actual = Bytes.toHexString(input);

        assertEquals(expected, actual);
    }

    @Test
    void testToString_singleByte() {
        byte input = 36;
        String expected = "24";

        String actual = Bytes.toString(input);

        assertEquals(expected, actual);
    }

    @Test
    void testToString_multipleBytes() {
        byte[] input = {36, 28, 45, 23, 81, 1, 34, 6, 84, 54};
        String expected = "24:1C:2D:17:51:01:22:06:54:36";

        String actual = Bytes.toString(input);

        assertEquals(expected, actual);
    }

}
