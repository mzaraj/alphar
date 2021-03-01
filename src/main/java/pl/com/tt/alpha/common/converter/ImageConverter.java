package pl.com.tt.alpha.common.converter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public final class ImageConverter {
	public static String encoder(MultipartFile multipartFile) throws IOException {
		return Base64.getEncoder().encodeToString(multipartFile.getBytes());
	}


	public static byte[] decoder(String base64OnString) {
		return Base64.getDecoder().decode(base64OnString);
	}
}
