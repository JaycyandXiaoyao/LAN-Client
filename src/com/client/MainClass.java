package com.client;

import java.util.Scanner;

public class MainClass {
	public static void main(String arg[]) {
		 new Linker(65534,65535,"艹你大爷"){
			@Override
			protected void linkSuccessed() {
				MsgSender msgSender = new MsgSender(this, 60000){
					@Override
					protected void sendSuccessed() {
						
						MyOut.println("发送成功");
					}
					@Override
					protected void serverLost() {
						
						MyOut.println("主机已下线");
					}
				};
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				
				MyOut.println("开始输入：");
				while(scanner.hasNextLine())
				{
					
					MyOut.println("正在发送");
					msgSender.sendMSG(scanner.nextLine());
				}
			}
			@Override
			protected void linkTimeOut() {
				
				MyOut.println("连接超时");
			}
		};
	}
}
