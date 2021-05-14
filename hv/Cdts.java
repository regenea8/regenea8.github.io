
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Cdts {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8951), 0);
		server.createContext("/test", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			
			// Request 분석
			InputStream inputStream = httpExchange.getRequestBody();
			read(inputStream);
			
			// Response 처리
			byte[] message = getMessage();
			httpExchange.sendResponseHeaders(200, message.length);
			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(message);
			outputStream.close();
		}
		
		public void read(InputStream inputStream) throws IOException {
			
			System.out.println("============================== [ R E Q ] ==============================");
			
			int reqHeaderSize 	= 4 + 4 + 4 + 36;
			int read 			= 0;				// 스트림에서 read했을 때 int 값
			int remain 			= reqHeaderSize;	// 헤더 사이즈에 맞게 // 남아있는
			int receive 		= 0;				// 받은 데이터
			int len 			= reqHeaderSize;	// 헤더 사이즈에 맞게 // 길이
			byte[] buffer 		= new byte[reqHeaderSize];	// 헤더 사이즈에 맞게 
			
			while(remain > 0) {
				read = inputStream.read(buffer, receive, remain);						// 스트림에서 데이터(int)를 가져온다.
				if(read < 0) throw new SocketException("heder read Exception");			// 해당 데이터가 0보다 작으면 Exception 발생
				receive += read;														// 받은 데이터를 더해주고
				remain = len - receive;													// 길이에서 받은 누적 데이터를 뺐을 때 0보다 크면 반복
			}
			
			byte[] type  = Arrays.copyOf(buffer, 4);
			byte[] size  = Arrays.copyOfRange(buffer, 4, 8);
			byte[] error = Arrays.copyOfRange(buffer, 8, 12);
			byte[] tid   = Arrays.copyOfRange(buffer, 12, 48);
			
			System.out.println("RECV>>");
			System.out.println("[type :4H=" + byteArrayToHexString(type) + "]");
			System.out.println("[size :4H=" + byteArrayToHexString(size) + "]");
			System.out.println("[size :  =" + Integer.parseInt(byteArrayToHexString(size), 16));
			System.out.println("[error:4H=" + byteArrayToHexString(error) + "]");
			System.out.println("[tid  :4H=" + byteArrayToHexString(tid) + "]");
			
			//==========================================================================// 헤더 끝
			
			int bodyLength = Integer.parseInt(byteArrayToHexString(size), 16);			// header에서 body size(길이)를 가져온다.
			byte[] reqBody = new byte[bodyLength];										// body size 만큼 byte 배열을 생성한다.

			remain 	= bodyLength;
			len		= bodyLength;
			receive = 0;
			read 	= 0;

			while(remain > 0) {
				read = inputStream.read(reqBody, receive, remain);
				if(read < 0) throw new SocketException("body read Exception");
				receive += read;
				remain = len - receive;
			}
			
			System.out.println("\n\n");
			System.out.println(new String(reqBody));

			//==========================================================================// 바디 끝
		}
		
		public byte[] getMessage() {
			
			System.out.println("============================== [ R E S ] ==============================");

			byte[] headTypeBytes 	= {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02}; 	// [type:4H=00000002]
			byte[] headSizeBytes  	= numToHexByteArr(getResBody().length); 		 	// [size:4H=000000F5]
			byte[] headErrorBytes 	= {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00}; 	// [error:4H=00000000]
			byte[] headTidBytes	 	= "TEST00000000000000000000000000000000".getBytes();// [tid:36A=PVUPSMS00000000000000000000000002796]
			byte[] bodyBytes	 	= getResBody();
			byte[] message		 	= new byte[headTypeBytes.length + headSizeBytes.length + headErrorBytes.length + headTidBytes.length + bodyBytes.length];
			
			System.arraycopy(headTypeBytes,  0, message, 0, headTypeBytes.length);
			System.arraycopy(headSizeBytes,  0, message, headTypeBytes.length, headSizeBytes.length);
			System.arraycopy(headErrorBytes, 0, message, headTypeBytes.length + headSizeBytes.length, headErrorBytes.length);
			System.arraycopy(headTidBytes,   0, message, headTypeBytes.length + headSizeBytes.length + headErrorBytes.length, headTidBytes.length);
			System.arraycopy(bodyBytes,      0, message, headTypeBytes.length + headSizeBytes.length + headErrorBytes.length + headTidBytes.length, bodyBytes.length);
			
			System.out.println("SEND>> ");
			System.out.println("[type :4H=" + byteArrayToHexString(headTypeBytes) + "]");
			System.out.println("[size :4H=" + byteArrayToHexString(headSizeBytes) + "]");
			System.out.println("[error:4H=" + byteArrayToHexString(headErrorBytes) + "]");
			System.out.println("[tid  :4H=" + byteArrayToHexString(headTidBytes) + "]");
			System.out.println("\n\n ");
			System.out.println(new String(bodyBytes));
			
			return message;
		}
		
		public byte[] getResBody() {
			StringBuffer sb = new StringBuffer();
			sb.append("<response>\n");
			sb.append("    <head>\n");
			sb.append("        <svcode>UP2017</svcode>\n");
			sb.append("        <err_cd></err_cd>\n");
			sb.append("        <err_msg></err_msg>\n");
			sb.append("    </head>\n");
			sb.append("    <body>\n");
			sb.append("        <operation>\n");
			sb.append("            <opcode>MD01</opcode>\n");
			sb.append("        </operation>");
			sb.append("    </body>\n");
			sb.append("</response>\n");
			return sb.toString().getBytes();
		}
		
		private String stringToHexString(String str) {
			char[] chars = str.toCharArray();
			StringBuffer hex = new StringBuffer();
			for (int i = 0; i < chars.length; i++){
				hex.append(Integer.toHexString((int) chars[i]));
			}
			return hex.toString();
		}

		// String to 0x 자동
		public byte[] hexStringToByteArray(String s) {
			int len = s.length();
			byte[] data = new byte[len / 2];
			for (int i = 0; i < len; i += 2) {
				data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
						+ Character.digit(s.charAt(i+1), 16));
			}
			return data;
		}

		// 0x to String
		public String byteArrayToHexString(byte[] bytes){ 

			StringBuilder sb = new StringBuilder(); 
			for(byte b : bytes){ 
				sb.append(String.format("%02X", b&0xff)); 
			} 
			return sb.toString(); 
		} 

		// num to 0x
		public byte[] numToHexByteArr(int num) {
			return ByteBuffer.allocate(4).putInt(num).array();
		}
	}

}
