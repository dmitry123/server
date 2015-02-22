import java.io.File;
import java.io.FileInputStream;

public class Main {

	public static void main(String[] arguments) throws Exception {

		new Server(new RequestListener() {
			@Override
			public Response process(Request request) throws Exception {

				File file = new File(System.getProperty("user.dir") + request.getPath());

				if (!file.exists()) {
					return new Response(ResponseCode.NOT_FOUND, "Can't find file \"" + file.getPath() + "\"");
				}

				FileInputStream stream = new FileInputStream(file);

				byte[] buffer = new byte[
						(int) file.length()
					];

				stream.read(buffer);
				stream.close();

				return new Response(ResponseCode.OK, ContentType.IMAGE_JPEG, buffer, null);
			}
		}, "server").run();
	}
}
