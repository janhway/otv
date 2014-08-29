package com.otv.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OtvUtils {

	static public String readHttpContent(HttpServletRequest req) {
		String ret = null;
		final int maxLen = 8192;
		byte[] readBuf = new byte[maxLen];

		try {
			InputStream is = req.getInputStream();
			int len = 0;
			int offset = 0;
			while (offset < maxLen
					&& (len = is.read(readBuf, offset, maxLen - offset)) > 0) {
				offset += len;
			}

			if (offset > 0) {
				ret = new String(readBuf, "utf-8");
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ret;
	}

	static public void writeHttpResponse(HttpServletResponse rsp, String content) {
		rsp.setContentType("text/plain");
		PrintWriter out;
		try {
			out = rsp.getWriter();
			out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
