import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Sender {

	public static void main(String[] args) throws UnknownHostException, IOException {

		String headTid = "TEST00000000000000000000000000000001";
		String bodyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + 
				"" + 
				"<request>" + 
				"  <head>" + 
				"    <svcode>dn2017</svcode>" + 
				"  </head>" + 
				"  <body>" + 
				"    <operation>" + 
				"      <opcode>UP10</opcode>" +
				"      <operatorId>15</operatorId>" + 
				
				up10() +
				
				"    </operation>" + 
				"  </body>" + 
				"</request>".trim();


		// 공백 생성기
		//byte[] blankBytes	 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

		// Head 생성
		byte[] headSizeBytes  	= numToHexByteArr(bodyXml.length()); 			 // 000001BB
		byte[] headErrorBytes 	= {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00}; // 00000000
		String headTidHexString = stringToHexString(headTid);
		byte[] headTidBytes	 	= hexStringToByteArray(headTidHexString);		 // 4946444E3030303030303030303030303030303030303030303030303030303030303031

		// Body 생성
		String bodyHexString 	= stringToHexString(bodyXml);
		byte[] bodyBytes	 	= hexStringToByteArray(bodyHexString);

		// Head(size + error + tid) + Body
		byte[] messageBytes 	= new byte[headSizeBytes.length + headErrorBytes.length + headTidBytes.length + bodyBytes.length];
		System.arraycopy(headSizeBytes, 0, messageBytes, 0, headSizeBytes.length);
		System.arraycopy(headErrorBytes, 0, messageBytes, headSizeBytes.length, headErrorBytes.length);
		System.arraycopy(headTidBytes, 0, messageBytes, headSizeBytes.length + headErrorBytes.length, headTidBytes.length);
		System.arraycopy(bodyBytes, 0, messageBytes, headSizeBytes.length + headErrorBytes.length + headTidBytes.length, bodyBytes.length);
		
		// 검증
		//System.out.println(byteArrayToHexString(headSizeBytes));
		//System.out.println(headSizeBytes.length);		// 4
		//System.out.println(byteArrayToHexString(headErrorBytes));
		//System.out.println(headErrorBytes.length);	// 4
		//System.out.println(byteArrayToHexString(headTidBytes));
		//System.out.println(headTidBytes.length);		// 36
		//System.out.println(bodyBytes.length);			// 469
		/*
		byte[] message = {

				 HEAD 
				// type 4byte
				(byte)0x00, (byte)0x00, (byte)0x01, (byte)0xBB,	// size 4byte
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // error 4byte 
				(byte)0x49, (byte)0x46, (byte)0x44, (byte)0x4E, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, // tid 36 byte
				(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30,
				(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30,
				(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30,
				(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x31,

				 BODY 

				(byte)0x3c, (byte)0x3f, (byte)0x78, (byte)0x6d, (byte)0x6c, (byte)0x20, (byte)0x76, (byte)0x65, (byte)0x72, (byte)0x73, 
				(byte)0x69, (byte)0x6f, (byte)0x6e, (byte)0x3d, (byte)0x22, (byte)0x31, (byte)0x2e, (byte)0x30, (byte)0x22, (byte)0x20, 
				(byte)0x65, (byte)0x6e, (byte)0x63, (byte)0x6f, (byte)0x64, (byte)0x69, (byte)0x6e, (byte)0x67, (byte)0x3d, (byte)0x22, 
				(byte)0x55, (byte)0x54, (byte)0x46, (byte)0x2d, (byte)0x38, (byte)0x22, (byte)0x20, (byte)0x73, (byte)0x74, (byte)0x61, 
				(byte)0x6e, (byte)0x64, (byte)0x61, (byte)0x6c, (byte)0x6f, (byte)0x6e, (byte)0x65, (byte)0x3d, (byte)0x22, (byte)0x79, 
				(byte)0x65, (byte)0x73, (byte)0x22, (byte)0x20, (byte)0x3f, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0xd, (byte)0xa, 
				(byte)0x3c, (byte)0x72, (byte)0x65, (byte)0x71, (byte)0x75, (byte)0x65, (byte)0x73, (byte)0x74, (byte)0x3e, (byte)0xd, 
				(byte)0xa, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x68, (byte)0x65, (byte)0x61, (byte)0x64, (byte)0x3e, (byte)0xd, 
				(byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x73, (byte)0x76, (byte)0x63, (byte)0x6f, 
				(byte)0x64, (byte)0x65, (byte)0x3e, (byte)0x64, (byte)0x6e, (byte)0x32, (byte)0x30, (byte)0x31, (byte)0x37, (byte)0x3c, 
				(byte)0x2f, (byte)0x73, (byte)0x76, (byte)0x63, (byte)0x6f, (byte)0x64, (byte)0x65, (byte)0x3e, (byte)0xd, (byte)0xa, 
				(byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x2f, (byte)0x68, (byte)0x65, (byte)0x61, (byte)0x64, (byte)0x3e, (byte)0xd, 
				(byte)0xa, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x62, (byte)0x6f, (byte)0x64, (byte)0x79, (byte)0x3e, (byte)0xd, 
				(byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x6f, (byte)0x70, (byte)0x65, (byte)0x72, 
				(byte)0x61, (byte)0x74, (byte)0x69, (byte)0x6f, (byte)0x6e, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x6f, (byte)0x70, (byte)0x63, (byte)0x6f, (byte)0x64, 
				(byte)0x65, (byte)0x3e, (byte)0x55, (byte)0x50, (byte)0x31, (byte)0x30, (byte)0x3c, (byte)0x2f, (byte)0x6f, (byte)0x70, 
				(byte)0x63, (byte)0x6f, (byte)0x64, (byte)0x65, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x70, (byte)0x65, (byte)0x65, (byte)0x72, (byte)0x3e, (byte)0x4c, 
				(byte)0x47, (byte)0x55, (byte)0x2b, (byte)0x3c, (byte)0x2f, (byte)0x70, (byte)0x65, (byte)0x65, (byte)0x72, (byte)0x3e, 
				(byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x72, 
				(byte)0x65, (byte)0x71, (byte)0x5f, (byte)0x6e, (byte)0x6f, (byte)0x3e, (byte)0x3c, (byte)0x2f, (byte)0x72, (byte)0x65, 
				(byte)0x71, (byte)0x5f, (byte)0x6e, (byte)0x6f, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x73, (byte)0x76, (byte)0x63, (byte)0x5f, (byte)0x6d, (byte)0x67, 
				(byte)0x6d, (byte)0x74, (byte)0x5f, (byte)0x6e, (byte)0x75, (byte)0x6d, (byte)0x3e, (byte)0x37, (byte)0x30, (byte)0x30, 
				(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x31, (byte)0x36, (byte)0x37, (byte)0x3c, (byte)0x2f, (byte)0x73, 
				(byte)0x76, (byte)0x63, (byte)0x5f, (byte)0x6d, (byte)0x67, (byte)0x6d, (byte)0x74, (byte)0x5f, (byte)0x6e, (byte)0x75, 
				(byte)0x6d, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, 
				(byte)0x3c, (byte)0x72, (byte)0x65, (byte)0x71, (byte)0x5f, (byte)0x73, (byte)0x74, (byte)0x3e, (byte)0x3c, (byte)0x2f, 
				(byte)0x72, (byte)0x65, (byte)0x71, (byte)0x5f, (byte)0x73, (byte)0x74, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x72, (byte)0x65, (byte)0x73, (byte)0x65, 
				(byte)0x72, (byte)0x76, (byte)0x65, (byte)0x64, (byte)0x31, (byte)0x3e, (byte)0x3c, (byte)0x2f, (byte)0x72, (byte)0x65, 
				(byte)0x73, (byte)0x65, (byte)0x72, (byte)0x76, (byte)0x65, (byte)0x64, (byte)0x31, (byte)0x3e, (byte)0xd, (byte)0xa, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x72, (byte)0x65, (byte)0x73, 
				(byte)0x65, (byte)0x72, (byte)0x76, (byte)0x65, (byte)0x64, (byte)0x32, (byte)0x3e, (byte)0x43, (byte)0x4e, (byte)0x43, 
				(byte)0x4c, (byte)0x5f, (byte)0x30, (byte)0x30, (byte)0x31, (byte)0x3c, (byte)0x2f, (byte)0x72, (byte)0x65, (byte)0x73, 
				(byte)0x65, (byte)0x72, (byte)0x76, (byte)0x65, (byte)0x64, (byte)0x32, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, 
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x62, (byte)0x69, (byte)0x72, (byte)0x74, 
				(byte)0x68, (byte)0x5f, (byte)0x64, (byte)0x74, (byte)0x3e, (byte)0x31, (byte)0x39, (byte)0x31, (byte)0x31, (byte)0x32, 
				(byte)0x39, (byte)0x3c, (byte)0x2f, (byte)0x62, (byte)0x69, (byte)0x72, (byte)0x74, (byte)0x68, (byte)0x5f, (byte)0x64, 
				(byte)0x74, (byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x2f, 
				(byte)0x6f, (byte)0x70, (byte)0x65, (byte)0x72, (byte)0x61, (byte)0x74, (byte)0x69, (byte)0x6f, (byte)0x6e, (byte)0x3e, 
				(byte)0xd, (byte)0xa, (byte)0x20, (byte)0x20, (byte)0x3c, (byte)0x2f, (byte)0x62, (byte)0x6f, (byte)0x64, (byte)0x79, 
				(byte)0x3e, (byte)0xd, (byte)0xa, (byte)0x3c, (byte)0x2f, (byte)0x72, (byte)0x65, (byte)0x71, (byte)0x75, (byte)0x65, 
				(byte)0x73, (byte)0x74, (byte)0x3e

		};
		*/
		Socket socket = new Socket("127.0.0.1", 8101);

		DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
		DataInputStream dIn = new DataInputStream(socket.getInputStream());

		//dOut.writeInt(message.length); // write length of the message
		//dOut.write(message);   
		dOut.writeInt(messageBytes.length); // write length of the message
		dOut.write(messageBytes);
		dOut.flush();
		System.out.println("SEND : " + bodyXml);
		System.out.println("SEND : " + byteArrayToHexString(messageBytes));

		byte[] buffer = new byte[10000];
		int read = 0;

		while ((read = dIn.read(buffer, 0, buffer.length)) != -1) {
			dIn.read(buffer);
			//System.out.println("Server says " + Arrays.toString(buffer));
			System.out.println("RECV : [" + new String(buffer, "UTF-8") + "]");
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
	
	public static String up10() {
		return "<soCode>soCode</soCode>" +
		"<custAcntNum>custAcntNum</custAcntNum>" +
		"<svcAcntNum>svcAcntNum</svcAcntNum>" +
		"<svcAcntEqpConsNum>svcAcntEqpConsNum</svcAcntEqpConsNum>" +
		"<rqesterId>rqesterId</rqesterId>" +
		"<roadNmMgmtNo>roadNmMgmtNo</roadNmMgmtNo>";

	}

}
