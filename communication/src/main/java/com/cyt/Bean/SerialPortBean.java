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

	private String appName = "����ͨѶ����";
	private int timeout = 2000;// open �˿�ʱ�ĵȴ�ʱ��
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

	// byte���鵽16�����ַ���
	public String byte2string(byte[] data) {
		if (data == null || data.length <= 1)
			return "0x1";
		if (data.length > 8000000)
			return "0x2";
		StringBuffer sb = new StringBuffer();
		int buf[] = new int[data.length];
		// byte����ת����ʮ����
		for (int k = 0; k < data.length; k++) {
			buf[k] = data[k] < 0 ? (data[k] + 256) : (data[k]);
		}
		// ʮ����ת����ʮ������
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

	// �ϲ�����byte����
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	/**
	 * @�������� :listPort
	 * @�������� :�г����п��õĴ���
	 * @����ֵ���� :void
	 */
	// @SuppressWarnings("rawtypes")
	public void listPort() {
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();
		System.out.println("now to list all Port of this PC��" + en);
		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(cpid.getName() + ", "
						+ cpid.getCurrentOwner());
			}
		}
	}

	/**
	 * @�������� :selectPort
	 * @�������� :ѡ��һ���˿ڣ����磺COM1
	 * @����ֵ���� :void
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
	 * @�������� :openPort
	 * @�������� :��SerialPort
	 * @����ֵ���� :void
	 */
	private void openPort() {
		if (commPort == null) {
			log(String.format("�޷��ҵ�����Ϊ'%1$s'�Ĵ��ڣ�", commPort.getName()));
		} else {
			log("�˿�ѡ��ɹ�����ǰ�˿ڣ�" + commPort.getName() + ",����ʵ���� SerialPort:");

			try {
				serialPort = (SerialPort) commPort.open(appName, timeout);
				serialPort.setSerialPortParams(115200, // ������
						SerialPort.DATABITS_8, // У��λ
						SerialPort.STOPBITS_1, // ����λ
						SerialPort.PARITY_NONE); // ֹͣλ
				log("ʵ�� SerialPort �ɹ���");

			} catch (PortInUseException e) {

				throw new RuntimeException(String.format("�˿�'%1$s'����ʹ���У�",
						commPort.getName()));

			} catch (UnsupportedCommOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @�������� :checkPort
	 * @�������� :���˿��Ƿ���ȷ����
	 * @����ֵ���� :void
	 */
	private void checkPort() {

		if (commPort == null) {

			throw new RuntimeException("û��ѡ��˿ڣ���ʹ�� "
					+ "selectPort(String portName) ����ѡ��˿�");
		}
		if (serialPort == null) {

			throw new RuntimeException("SerialPort ������Ч��");
		}
	}

	/**
	 * @�������� :write
	 * @�������� :��˿ڷ������ݣ����ڵ��ô˷���ǰ ��ѡ��˿ڣ���ȷ��SerialPort�����򿪣�
	 * @����ֵ���� :void
	 * @param message
	 */
	public void write(String message, String method) {

		checkPort();
		byte[] send = null;

		try {
			outputStream = new BufferedOutputStream(
					serialPort.getOutputStream());
		} catch (IOException e) {

			throw new RuntimeException("��ȡ�˿ڵ�OutputStream����" + e.getMessage());
		}

		try {
			if (method.equals("hex")) {
				send = hexStringToByte(message);
			} else if (method.equals("ascll")) {
				send = message.getBytes();
			}
			outputStream.write(send);
			log("��Ϣ���ͳɹ���");

		} catch (IOException e) {

			throw new RuntimeException("��˿ڷ�����Ϣʱ����" + e.getMessage());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @�������� :startRead
	 * @�������� :��ʼ�����Ӷ˿��н��յ�����
	 * @����ֵ���� :void
	 * @param time
	 *            ��������Ĵ��ʱ�䣬��λΪ�룬0 ����һֱ����
	 */
	public void startRead(int time) {
		checkPort();

		try {
			inputStream = new BufferedInputStream(serialPort.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("��ȡ�˿ڵ�InputStream����" + e.getMessage());
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e.getMessage());
		}

		serialPort.notifyOnDataAvailable(true);

		log(String.format("��ʼ��������'%1$s'������--------------", commPort.getName()));
		if (time > 0) {
			this.threadTime = time * 1000;
			Thread t = new Thread(this);
			t.start();
			log(String.format("����������%1$d���رա�������", threadTime));
		}
	}

	/**
	 * @�������� :close
	 * @�������� :�ر� SerialPort
	 * @����ֵ���� :void
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
	 * ���ݽ��յļ���������
	 */
	public void serialEvent(SerialPortEvent arg0) {

		switch (arg0.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,ͨѶ�ж� */
		case SerialPortEvent.OE:/* Overrun error����λ���� */
		case SerialPortEvent.FE:/* Framing error����֡���� */
		case SerialPortEvent.PE:/* Parity error��У����� */
		case SerialPortEvent.CD:/* Carrier detect���ز���� */
		case SerialPortEvent.CTS:/* Clear to send��������� */
		case SerialPortEvent.DSR:/* Data set ready�������豸���� */
		case SerialPortEvent.RI:/* Ring indicator������ָʾ */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*
												 * Output buffer is
												 * empty��������������
												 */
			break;
		case SerialPortEvent.DATA_AVAILABLE:/*
											 * Data available at the serial
											 * port���˿��п������ݡ������������飬������ն�
											 */
			// byte[] readBuffer = new byte[1024];
			ArrayList<Byte> BUFFER = new ArrayList<Byte>(); // int i=0;
			// inputStream.
			try {

				int newData = 0;

				// rec_byte=new byte[1];
				while ((newData = inputStream.read()) != -1) {
					// log("׼������");
					BUFFER.add((byte) newData);

				}
				SetData(BUFFER);
			} catch (IOException e) {
			}
		}
	}

	// ����BUFFER�е�list
	private void SetData(ArrayList<Byte> BUFFER) {
		rec_byte = new byte[BUFFER.size()];
		for (int i = 0; i < BUFFER.size(); i++) {
			rec_byte[i] = BUFFER.get(i);
			// System.out.println(rec_byte[i]+BUFFER.size());
		}
		rec_string = byte2string(rec_byte);
		// hasnew=true;
		// System.out.println("ת���ɹ���");
		// System.out.println(rec_byte);
		log("rec_string= " + rec_string);
	}

	public void run() {
		try {

			Thread.sleep(threadTime);
			serialPort.close();
			log(String.format("�˿�''�����ر��ˣ�", commPort.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
