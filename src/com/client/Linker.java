package com.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
/**
 * 
 * @author NB111
 *@version 0.0.6
 */
public class Linker extends Thread{
	private InetAddress serverAdd;
	private InetAddress group;
	private String broadCastAdd;
	private String username;
	private int dstport;

	private int srcport;
	private int linktime = 0;
	public Linker(int srcport, int dstport, String username){
		
		this.dstport = dstport;
		this.srcport = srcport;
		this.username = username;
		broadCastAdd = "255.255.255.255";
		
		try {
			group = InetAddress.getByName(broadCastAdd);
			start();	
		} catch (UnknownHostException e) {
			MyOut.println(e.toString());
		} 
		
	}

	@Override
	public void run(){
		
		linkStart();
		
		while (true) {
			// 发送请求包
			try {
				DatagramSocket socketSender = new DatagramSocket(dstport);
				byte[] dataRequest = new String("001"+username).getBytes();
				DatagramPacket request = new DatagramPacket(dataRequest, dataRequest.length, group, dstport);
				socketSender.send(request);
				socketSender.close();
				MyOut.println("发送成功:"+new String(dataRequest));
			} catch (IOException e) {
				MyOut.println(e);
			}
			
			// 接收服务器IP，超时重新发送请求.
			DatagramSocket socketReciever = null ;
			try {
				socketReciever = new DatagramSocket(srcport); 
				byte[] dataResponce = new byte[1024];
				DatagramPacket reply = new DatagramPacket(dataResponce, dataResponce.length);
				socketReciever.setSoTimeout(500);
				socketReciever.receive(reply);
				socketReciever.close();
				String massage = byteToString(reply.getData());
				if(!isReply(massage.substring(0, 3))){
					serverAdd = reply.getAddress();
					linkSuccessed();
					MyOut.println("找到服务器，IP："+serverAdd.getHostAddress());
					break;
				}
				else{
					Thread.sleep(500);
				}


			} catch (IOException | InterruptedException e) {
				MyOut.println(e.toString()+"服务器查找超时，重试中...");
				linktime++;
				if(linktime == 20){
					socketReciever.close();
					linkTimeOut();
					break;
				}
				socketReciever.close();
			}

		}
	}

	public static String byteToString(byte[] bs)
	{
		String result = new String(bs);
		return result.trim();
	}

	public boolean isReply(String s){
		return s.equals("001");

	}

	public int getDstport() {
		return dstport;
	}

	public void setDstport(int dstport) {
		this.dstport = dstport;
	}

	public int getSrcport() {
		return srcport;
	}

	public void setSrcport(int srcport) {
		this.srcport = srcport;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public InetAddress getServerAdd() {
		return serverAdd;
	}

	protected void linkStart() {

	}

	protected void linkSuccessed() {

	}

	protected void linkTimeOut() {

	}

	public String getLocalBroadCastAdd(){
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface netInterface;
			netInterface = NetworkInterface.getByInetAddress(address);
			if (!netInterface.isLoopback() && netInterface.isUp()) {
				List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
				for (InterfaceAddress interfaceAddress : interfaceAddresses) {
					if (interfaceAddress.getBroadcast() != null) {
						MyOut.println(interfaceAddress.getBroadcast().getHostAddress());// 输出广播地址
						return interfaceAddress.getBroadcast().getHostAddress();
					}
				}
			}
		} catch (SocketException | UnknownHostException e) {
			return null;
		}
		return null;
	}


}
