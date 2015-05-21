package com.javacard.cap.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.javacard.cap.Cap;
import com.javacard.cap.Formatter;
import com.watchdata.commons.lang.WDAssert;

public class MethodComponent extends Formatter {
	@Override
	public String format(String componentInfo) throws IOException {
		String headerFormat = read("MethodComponent");
		
		StringBuilder sb=new StringBuilder();
		StringReader sReader=new StringReader(componentInfo);
		
		if (WDAssert.isNotEmpty(headerFormat)) {
			sb.append(paddingExt(headerFormat, sReader));
		}
		System.out.println(sb.toString());
		while (true) {
			try {
				int flags=readU1Left(sReader,4);
				
				if (flags==Cap.ACC_EXTENDED) {
					sb.append("\textended_method_header_info {");
					sb.append("\r\n\t\tu1 bitfield {");
					sb.append("\r\n\t\t\tbit[4] flags:");
					sb.append(byteHex1(flags));
					sb.append("\r\n\t\t\tbit[4] padding:");
					sb.append(byteHex1(readU1Right(sReader)));
					sb.append("\r\n\t\t}");
					sb.append("\r\n\t\tu1 max_stack:"+readU1(sReader));
					sb.append("\r\n\t\tu1 nargs:"+readU1(sReader));
					sb.append("\r\n\t\tu1 max_locals:"+readU1(sReader));
					sb.append("\r\n\t}\r\n");
					break;
				}else {
					sb.append("\tmethod_header_info {\r\n\t\tu1 bitfield {\r\n\t\t\tbit[4] flags:");
					sb.append(byteHex1(flags)+"\r\n\t\t\t");
					sb.append("bit[4] max_stack:"+byteHex1(readU1Right(sReader)));
					sb.append("\r\n\t\t}");
					sb.append("\r\n\t\tu1 bitfield {");
					sb.append("\r\n\t\t\tbit[4] nargs:"+byteHex1(readU1Left(sReader,4)));
					sb.append("\r\n\t\t\tbit[4] max_locals:"+byteHex1(readU1Right(sReader)));
					sb.append("\r\n\t\t}");
					sb.append("\r\n\t}\r\n");
				}
			} catch (Exception e) {
				// TODO: handle exception
				break;
			}
		}
		
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> map = Cap.readCap("pboc1200.cap");
		System.out.println(map.get("Method.cap"));
		String a = new MethodComponent().format(map.get("Method.cap"));
		System.out.println(a);
	}
	//0798160A
//	027A804E02C80115
//	02F6024A05400114
//	02F6024A056D00D9
//	02F6824A058C0120
//	8DFB00998E940114
//	8DFB00998E9E00D9
//	8DFB80998EA80120
//	92E700E193C80114
//	92E700E193D200D9
//	92E780E193DC0120
//	0541
//	188C
//	0126
//	1803
//	880F1803
//	88021803
//	88311803
//	880B7B00
//	FC670C11023A058D008A7F00FC7B00499F009C05058D005E7F0
//	0491059058D008A7F00D411010C058D008A7F012805110080038D00E59400011E7F013508038D00677F00BF08038D
//	00677F012C08038D00677F00AF08038D00677F008908038D00677F012308038D00677F00B91014038D00627F007E10
//	14038D00627F010F04038D00C57F0116100C038D00677F006A100C038D00677F00F704110080038D00E59400011E7F
//	00DF0480008B18102E900B873318102E900B872C18102E900B873918102E900B873418102E900B873D18102E900B87
}
	