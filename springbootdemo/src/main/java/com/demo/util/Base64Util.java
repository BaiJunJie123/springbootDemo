package com.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * @author zhuzhe
 * @date 2018/10/17 17:21
 * @email zhe.zhu1@outlook.com
 */
@Slf4j
public class Base64Util {

	public static MultipartFile base64ToMultipart(String base64) {
		try {
			String[] baseStr = base64.split(",");

			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = new byte[0];
			b = decoder.decodeBuffer(baseStr[1]);

			for(int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}

			return new BASE64DecodedMultipartFile(b, baseStr[0]);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}