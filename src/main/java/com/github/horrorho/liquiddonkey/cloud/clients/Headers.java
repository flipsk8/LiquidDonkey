/* 
 * The MIT License
 *
 * Copyright 2015 Ahseya.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.horrorho.liquiddonkey.cloud.clients;

import com.github.horrorho.liquiddonkey.cloud.protobuf.ChunkServer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

/**
 * Headers factory.
 *
 * @author ahseya
 */
@Immutable
@ThreadSafe
public final class Headers {

    public static Headers create() {
        return instance;
    }

    private static final Headers instance = new Headers();

    private static final Header accept
            = new BasicHeader(
                    "Accept",
                    "application/vnd.com.apple.me.ubchunk+protobuf");
    private static final Header contentType
            = new BasicHeader(
                    "Content-Type",
                    "application/vnd.com.apple.me.ubchunk+protobuf");
    private static final Header mbsProtocolVersion
            = new BasicHeader(
                    "X-Apple-MBS-Protocol-Version",
                    "1.7");
    private static final Header mmcsProtocolVersion
            = new BasicHeader(
                    "x-apple-mmcs-proto-version",
                    "3.3");
    private static final Header mmcsDataClass
            = new BasicHeader(
                    "x-apple-mmcs-dataclass",
                    "com.apple.Dataclass.Backup");
    private static final Header mmeClientInfo
            = new BasicHeader(
                    "X-MMe-Client-Info",
                    "<iPhone2,1> <iPhone OS;5.1.1;9B206> <com.apple.AppleAccount/1.0 ((null)/(null))>");
    private static final Header mmeClientInfoBackup
            = new BasicHeader(
                    "X-MMe-Client-Info",
                    "<N88AP> <iPhone OS;5.1.1;9B206> <com.apple.icloud.content/211.1 (com.apple.MobileBackup/9B206)>");
    private static final Header userAgentBackupd
            = new BasicHeader(
                    HttpHeaders.USER_AGENT,
                    "backupd (unknown version) CFNetwork/548.1.4 Darwin/11.0.0");
    private static final Header userAgentMobileBackup
            = new BasicHeader(
                    HttpHeaders.USER_AGENT,
                    "MobileBackup/5.1.1 (9B206; iPhone3,1)");
    private static final Header userAgentUbd
            = new BasicHeader(
                    HttpHeaders.USER_AGENT,
                    "ubd (unknown version) CFNetwork/548.1.4 Darwin/11.0.0");

    List<Header> mobileBackupHeaders(String authMme) {
        return Arrays.asList(
                authorization(authMme),
                mmeClientInfo,
                userAgentMobileBackup,
                mbsProtocolVersion
        );
    }

    List<Header> contentHeaders(String dsPrsID) {
        return Arrays.asList(
                mmeDsid(dsPrsID),
                mmcsProtocolVersion,
                mmcsDataClass,
                userAgentBackupd,
                accept,
                contentType,
                mmeClientInfoBackup
        );
    }

    Header mmeClientInfo() {
        return mmeClientInfo;
    }

    Header mmeDsid(String dsPrsID) {
        return header("x-apple-mme-dsid", dsPrsID);
    }

    Header authorization(String token) {
        return header("Authorization", token);
    }

    Header mmcsAuth(String token) {
        return header("x-apple-mmcs-auth", token);
    }

    Header header(String name, String value) {
        return new BasicHeader(name, value);
    }

    List<Header> headers(List<ChunkServer.NameValuePair> headers) {
        return headers.stream()
                .map(header -> new BasicHeader(header.getName(), header.getValue()))
                .collect(Collectors.toList());
    }
}