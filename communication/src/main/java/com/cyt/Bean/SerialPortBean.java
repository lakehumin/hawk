package com.cyt.Bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.*;

public class SerialPortBean implements Runnable, SerialPortEventListener {

	private String appName = "串口通讯测试";
	private int timeout = 2000;// open 端口时的等待时间
	private int threadTime = 0;
	// private SendMessage SM=new SendMessage();
	private CommPortIdentifier commPort;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private String rec_string = "";
	private byte[] rec_byte = null;

	public byte[] getRec_byte() {
		return rec_byte;
	}

	public void setRec_byte(byte[] rec_byte) {
		this.rec_byte = rec_byte;
	}

	public void setRec_string(String rec_string) {
		this.rec_string = rec_string;
	}

	public String getRec_string() {
		return rec_string;
	}

	// byte数组到16进制字符串
	public String byte2string(byte[] data) {
		if (data == null || data.length <= 1)
			return "0x1";
		if (data.length > 8000000)
			return "0x2";
		StringBuffer sb = new StringBuffer();
		int buf[] = new int[data.length];
		// byte数组转化成十进制
		for (int k = 0; k < data.length; k++) {
			buf[k] = data[k] < 0 ? (data[k] + 256) : (data[k]);
		}
		// 十进制转化成十六进制
		for (int k = 0; k < buf.length; k++) {
			if (buf[k] < 16)
				sb.append("0" + Integer.toHexString(buf[k]));
			else
				sb.append(Integer.toHexString(buf[k]));
		}
		return "" + sb.toString().toUpperCase();
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	// 合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	/**
	 * @方法名称 :listPort
	 * @功能描述 :列出所有可用的串口
	 * @返回值类型 :void
	 */
	// @SuppressWarnings("rawtypes")
	public void listPort() {
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		System.out.println("now to list all Port of this PC：" + en);
		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(cpid.getName() + ", "
						+ cpid.getCurrentOwner());
			}
		}
	}

	/**
	 * @方法名称 :selectPort
	 * @功能描述 :选择一个端口，比如：COM1
	 * @返回值类型 :void
	 * @param portName
	 */
	// @SuppressWarnings("rawtypes")
	public void selectPort(String portName) {

		this.commPort = null;
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL
					&& cpid.getName().equals(portName)) {
				this.commPort = cpid;
				break;
			}
		}

		openPort();
	}

	/**
	 * @方法名称 :openPort
	 * @功能描述 :打开SerialPort
	 * @返回值类型 :void
	 */
	private void openPort() {
		if (commPort == null) {
			log(String.format("无法找到名字为'%1$s'的串口！", commPort.getName()));
		} else {
			log("端口选择成功，当前端口：" + commPort.getName() + ",现在实例化 SerialPort:");

			try {
				serialPort = (SerialPort) commPort.open(appName, timeout);
				serialPort.setSerialPortParams(115200, // 波特率
						SerialPort.DATABITS_8, // 校验位
						SerialPort.STOPBITS_1, // 数据位
						SerialPort.PARITY_NONE); // 停止位
				log("实例 SerialPort 成功！");

			} catch (PortInUseException e) {

				throw new RuntimeException(String.format("端口'%1$s'正在使用中！",
						commPort.getName()));

			} catch (UnsupportedCommOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @方法名称 :checkPort
	 * @功能描述 :检查端口是否正确连接
	 * @返回值类型 :void
	 */
	private void checkPort() {

		if (commPort == null) {

			throw new RuntimeException("没有选择端口，请使用 "
					+ "selectPort(String portName) 方法选择端口");
		}
		if (serialPort == null) {

			throw new RuntimeException("SerialPort 对象无效！");
		}
	}

	/**
	 * @方法名称 :write
	 * @功能描述 :向端口发送数据，请在调用此方法前 先选择端口，并确定SerialPort正常打开！
	 * @返回值类型 :void
	 * @param message
	 */
	public void write(String message, String method) {

		checkPort();
		byte[] send = null;

		try {
			outputStream = new BufferedOutputStream(
					serialPort.getOutputStream());
		} catch (IOException e) {

			throw new RuntimeException("获取端口的OutputStream出错：" + e.getMessage());
		}

		try {
			if (method.equals("hex")) {
				send = hexStringToByte(message);
			} else if (method.equals("ascll")) {
				send = message.getBytes();
			}
			outputStream.write(send);
			log("信息发送成功！");

		} catch (IOException e) {

			throw new RuntimeException("向端口发送信息时出错：" + e.getMessage());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @方法名称 :startRead
	 * @功能描述 :开始监听从端口中接收的数据
	 * @返回值类型 :void
	 * @param time
	 *            监听程序的存活时间，单位为秒，0 则是一直监听
	 */
	public void startRead(int time) {
		checkPort();

		try {
			inputStream = new BufferedInputStream(serialPort.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("获取端口的InputStream出错：" + e.getMessage());
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e.getMessage());
		}

		serialPort.notifyOnDataAvailable(true);

		log(String.format("开始监听来自'%1$s'的数据--------------", commPort.getName()));
		if (time > 0) {
			this.threadTime = time * 1000;
			Thread t = new Thread(this);
			t.start();
			log(String.format("监听程序将在%1$d秒后关闭。。。。", threadTime));
		}
	}

	/**
	 * @方法名称 :close
	 * @功能描述 :关闭 SerialPort
	 * @返回值类型 :void
	 */
	public void close() {
		serialPort.close();
		serialPort = null;
		commPort = null;
	}

	private void log(String msg) {
		DateFormat format = new SimpleDateFormat("YY-MM-dd HH:mm:ss");
		String time = format.format(new java.util.Date());
		System.out.println(time + "--> " + msg);
	}

	@Override
	/**
	 * 数据接收的监听处理函数
	 */
	public void serialEvent(SerialPortEvent arg0) {

		switch (arg0.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
		case SerialPortEvent.OE:/* Overrun error，溢位错误 */
		case SerialPortEvent.FE:/* Framing error，传帧错误 */
		case SerialPortEvent.PE:/* Parity error，校验错误 */
		case SerialPortEvent.CD:/* Carrier detect，载波检测 */
		case SerialPortEvent.CTS:/* Clear to send，清除发送 */
		case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
		case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*
												 * Output buffer is
												 * empty，输出缓冲区清空
												 */
			break;
		case SerialPortEvent.DATA_AVAILABLE:/*
											 * Data available at the serial
											 * port，端口有可用数据。读到缓冲数组，输出到终端
											 */
			// byte[] readBuffer = new byte[1024];
			ArrayList<Byte> BUFFER = new ArrayList<Byte>(); // int i=0;
			// inputStream.
			try {

				int newData = 0;

				// rec_byte=new byte[1];
				while ((newData = inputStream.read()) != -1) {
					// log("准备接受");
					BUFFER.add((byte) newData);

				}
				SetData(BUFFER);
			} catch (IOException e) {
			}
		}
	}

	// 处理BUFFER中的list
	private void SetData(ArrayList<Byte> BUFFER) {
		rec_byte = new byte[BUFFER.size()];
		for (int i = 0; i < BUFFER.size(); i++) {
			rec_byte[i] = BUFFER.get(i);
			// System.out.println(rec_byte[i]+BUFFER.size());
		}
		rec_string = byte2string(rec_byte);
		// hasnew=true;
		// System.out.println("转换成功！");
		// System.out.println(rec_byte);
		log("rec_string= " + rec_string);
	}

	public void run() {
		try {

			Thread.sleep(threadTime);
			serialPort.close();
			log(String.format("端口''监听关闭了！", commPort.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
