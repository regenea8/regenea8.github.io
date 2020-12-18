import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SendTestMain {
	
	public static void main(String[] args) {
		
		long taskNo = 201204299954L;
		
		for (int i = 0; i < 3; i++) {
			Test1 test = new Test1(taskNo--);
			test.start();
		}
	}
}

class Test1 extends Thread {
	
	private long taskNo = 0L;
	
	public Test1(long taskNo) {
		this.taskNo = taskNo;
	}

	@Override
	public void run() {
		
		Socket socket = null;
		
		try {
					
			String headTid = "TEST00000000000000000000000000000001";
			String bodyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
					"<request>\n" + 
					"    <head>\n" + 
					"        <svcode>sv140</svcode>\n" + 
					"    </head>\n" + 
					"    <body>\n" + 
					"        <peer>NetMng</peer>\n" + 
					"        <operation>\n" + 
					"            <opcode>to401</opcode>\n" + 
					"            <so_cd>C40</so_cd>\n" + 
					"            <req_sys>NetMng</req_sys>\n" + 
					"            <task_no>" + taskNo + "</task_no>\n" + 
					"            <task_typ_cd>D</task_typ_cd>\n" + 
					"            <task_se_cd>040201002</task_se_cd>\n" + 
					"            <task_title>제목 테스트 " + taskNo + "</task_title>\n" + 
					"            <task_ctt></task_ctt>\n" + 
					"            <task_sta_dtm>" + getCurrentTime() + "</task_sta_dtm>\n" + 
					"            <ugnt_work_yn>N</ugnt_work_yn>\n" + 
					"            <cell_info>\n" + 
					"                <cell_num>109</cell_num>\n" + 
					"                <cell_cd>YC109</cell_cd>\n" + 
					"            </cell_info>\n" + 
					"            <cell_info>\n" + 
					"                <cell_num>103</cell_num>\n" + 
					"                <cell_cd>YC103</cell_cd>\n" + 
					"            </cell_info>\n" + 
					"            <oper_info>\n" + 
					"                <oper_id>CAMS</oper_id>\n" + 
					"                <oper_nm>CAMS</oper_nm>\n" + 
					"            </oper_info>\n" + 
					"        </operation>\n" + 
					"    </body>\n" + 
					"</request>".trim();
			
			// Head 생성
			byte[] typeBytes	 	= {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01}; 		// 00000001
			byte[] headSizeBytes  	= numToHexByteArr(bodyXml.getBytes("UTF-8").length);	// 000002D6
			byte[] headErrorBytes 	= {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00}; 		// 00000000
			String headTidHexString = stringToHexString(headTid);
			byte[] headTidBytes	 	= hexStringToByteArray(headTidHexString);		 		// 544553543030303030303030303030303030303030303030303030303030303030303031
			
			// Body 생성
			String bodyHexString 	= byteArrayToHexString(bodyXml.getBytes("UTF-8"));
			byte[] bodyBytes	 	= hexStringToByteArray(bodyHexString);			 		// 3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D227574662D38223F3E3C726571756573743E3C686561643E3C7376636F64653E6966313139363C2F7376636F64653E3C2F686561643E3C626F64793E3C6F7065726174696F6E3E3C706565723E4E65744D6E673C2F706565723E3C6F70636F64653E746F3330313C2F6F70636F64653E3C736F5F63643E4334303C2F736F5F63643E3C7461736B5F6E6F3E32302D30392D30313430343C2F7461736B5F6E6F3E3C7461736B5F7479705F63643E573C2F7461736B5F7479705F63643E3C7461736B5F73655F63643E3032303230313030313C2F7461736B5F73655F63643E3C7461736B5F7469746C653EC131B2A5202F20D488C9C8AC1CC120202F20484643202F2034B30020C9C0D45C20AC1CC1203C2F7461736B5F7469746C653E3C7461736B5F6374743EB0B4C6A93C2F7461736B5F6374743E3C7461736B5F7374615F64746D3E32303230303930313130323032303C2F7461736B5F7374615F64746D3E3C75676E745F776F726B5F796E3E593C2F75676E745F776F726B5F796E3E3C63656C6C5F696E666F3E202020203C63656C6C5F6E756D3E544130383C2F63656C6C5F6E756D3E3C2F63656C6C5F696E666F3E3C63656C6C5F696E666F3E202020203C63656C6C5F6E756D3E544133363C2F63656C6C5F6E756D3E3C2F63656C6C5F696E666F3E3C6F7065725F696E666F3E202020203C6F7065725F69643E5231323334353C2F6F7065725F69643E202020203C6F7065725F6E6D3ED64DAE38B3D93C2F6F7065725F6E6D3E3C2F6F7065725F696E666F3E3C6F7065725F696E666F3E202020203C6F7065725F69643E523132333435363C2F6F7065725F69643E202020203C6F7065725F6E6D3ED64DAE38C21C3C2F6F7065725F6E6D3E3C2F6F7065725F696E666F3E3C2F6F7065726174696F6E3E3C2F626F64793E3C2F726571756573743E
			
			byte[] messageBytes 	= new byte[typeBytes.length + headSizeBytes.length + headErrorBytes.length + headTidBytes.length + bodyBytes.length];
			System.arraycopy(typeBytes, 0, messageBytes, 0, typeBytes.length);
			System.arraycopy(headSizeBytes, 0, messageBytes, typeBytes.length, headSizeBytes.length);
			System.arraycopy(headErrorBytes, 0, messageBytes, typeBytes.length + headSizeBytes.length, headErrorBytes.length);
			System.arraycopy(headTidBytes, 0, messageBytes, typeBytes.length + headSizeBytes.length + headErrorBytes.length, headTidBytes.length);
			System.arraycopy(bodyBytes, 0, messageBytes, typeBytes.length + headSizeBytes.length + headErrorBytes.length + headTidBytes.length, bodyBytes.length);
						
			System.out.println("================================================================");
			System.out.println("요청 전문 :");
			System.out.println(byteArrayToHexString(messageBytes));		

			System.out.println("================================================================");
			System.out.println("잘못된 분석 :");
			System.out.println(new String(messageBytes, "UTF-8"));
			
			System.out.println("================================================================");
			System.out.println("정상 분석 :");
			byte[] type = Arrays.copyOf(messageBytes, 4);
			//System.out.println(byteArrayToHexString(type));
			byte[] size = Arrays.copyOfRange(messageBytes, 4, 8);
			//System.out.println(byteArrayToHexString(size));
			byte[] error = Arrays.copyOfRange(messageBytes, 8, 12);
			//System.out.println(byteArrayToHexString(error));
			byte[] tid = Arrays.copyOfRange(messageBytes, 12, 48);
			//System.out.println(byteArrayToHexString(tid));
			byte[] body = Arrays.copyOfRange(messageBytes, 48, 48 + Integer.parseInt(byteArrayToHexString(size), 16));
			//System.out.println(Integer.parseInt(byteArrayToHexString(type), 16));
			//System.out.println(Integer.parseInt(byteArrayToHexString(size), 16));
			//System.out.println(messageBytes.length - 48);
			//System.out.println(Integer.parseInt(byteArrayToHexString(error), 16));
			System.out.println(new String(body, "UTF-8"));
			System.out.println("================================================================");
			
			socket = new Socket("211.175.133.76", 9224);
			InputStream inputStream   = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			outputStream.write(messageBytes);
			outputStream.flush();
			System.out.println("SEND : " + bodyXml);
			System.out.println("SEND : " + byteArrayToHexString(messageBytes));

			byte[] buffer = new byte[5000];
			int read = 0;

			while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
				inputStream.read(buffer);
				System.out.println("RECV : [" + new String(buffer, "UTF-8") + "]");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getCurrentTime() {
		long curTime = System.currentTimeMillis();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return simpleDateFormat.format(new Date(curTime));
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


