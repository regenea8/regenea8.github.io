import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RecvTestMain {

	public static void main(String[] args) throws IOException {

		System.out.println("서버 준비 중...");
		ServerSocket serverSocket = new ServerSocket(8340);
		System.out.println("서버가 준비 되었습니다.");

		System.out.println("클라이언트 접속 대기 중...");
		Socket socket = serverSocket.accept();
		System.out.println("클라이언트가 접속 하였습니다.");

		InputStream inputStream = socket.getInputStream();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

		int headerSize = 4 + 4 + 4 + 36;

		while (true) {
			int read 			= 0;			// 스트림에서 read했을 때 int 값
			int remain 			= headerSize;	// 헤더 사이즈에 맞게 // 남아있는
			int receive 		= 0;			// 받은 데이터
			int len 			= headerSize;	// 헤더 사이즈에 맞게 // 길이
			String strHead 		= null; 		// 전문 중 헤더 부분
			String strBody 		= null;			// 전문 중 바디 부분

			byte buffer[] 		= new byte[headerSize];	// 헤더 사이즈에 맞게 

			while(remain > 0) {
				read = inputStream.read(buffer, receive, remain);						// 스트림에서 데이터(int)를 가져온다.
				if(read < 0) throw new SocketException("heder read Exception");			// 해당 데이터가 0보다 작으면 Exception 발생
				receive += read;														// 받은 데이터를 더해주고
				remain = len - receive;													// 길이에서 받은 누적 데이터를 뺐을 때 0보다 크면 반복
			}
			
			System.out.println("RECV>> byte : " + byteArrayToHexString(buffer));
			byte[] type = Arrays.copyOf(buffer, 4);
			System.out.println("RECV>> type : " + byteArrayToHexString(type));
			byte[] size = Arrays.copyOfRange(buffer, 4, 8);
			System.out.println("RECV>> size : " + byteArrayToHexString(size));
			System.out.println("RECV>> size : " + Integer.parseInt(byteArrayToHexString(size), 16));
			byte[] error = Arrays.copyOfRange(buffer, 8, 12);
			System.out.println("RECV>> error : " + byteArrayToHexString(error));
			byte[] tid = Arrays.copyOfRange(buffer, 12, 48);
			System.out.println("RECV>> tid : " + byteArrayToHexString(tid));
			
			//==========================================================================// 헤더 끝
			
			int bodyLength = Integer.parseInt(byteArrayToHexString(size), 16);			// header에서 body size(길이)를 가져온다.
			byte[] cbody = new byte[bodyLength];										// body size 만큼 byte 배열을 생성한다.

			remain = bodyLength;
			len = bodyLength;
			receive = 0;
			read = 0;

			while(remain > 0) {
				read = inputStream.read(cbody, receive, remain);
				if(read < 0) throw new SocketException("body read Exception");
				receive += read;
				remain = len - receive;
			}
						
			strBody = new String(cbody, "EUC-KR");
			System.out.println("RECV>> cbody : " + byteArrayToHexString(cbody));
			System.out.println("RECV>> strBody : [" + strBody + "]");

			//==========================================================================// 바디 끝
		}

	}

	private static String stringToHexString(String str) {
		char[] chars = str.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++){
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	// String to 0x 자동
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	// 0x to String
	public static String byteArrayToHexString(byte[] bytes){ 

		StringBuilder sb = new StringBuilder(); 
		for(byte b : bytes){ 
			sb.append(String.format("%02X", b&0xff)); 
		} 
		return sb.toString(); 
	} 

	// num to 0x
	public static byte[] numToHexByteArr(int num) {
		return ByteBuffer.allocate(4).putInt(num).array();
	}
}
