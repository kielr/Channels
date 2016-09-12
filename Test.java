package SyncCom;

import SyncCom.SyncComm.RecvSelect;
import SyncCom.SyncComm.SenderStage3_1;
import SyncCom.SyncComm.SenderStage3_2;

class SyncComm {
	public Channel myChannel = new Channel();
	public Channel sepChannel1 = new Channel();
	public Channel sepChannel2 = new Channel();
	public SendEvent sevent = new SendEvent("Hello this is a sendevent", myChannel);
	public RecvEvent revent = new RecvEvent(myChannel);
	
	class SenderStage1 implements Runnable {
		public void run() {
			for(int i = 0; i < 100; i++)
			{
				try { 
					myChannel.Send("Hello this is PID " + Thread.currentThread().getId() 
							+ " with message #" + i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class ReceiverStage1 implements Runnable {
		public void run() {
			for(int i = 0; i < 200; i++)
			{
				try {
					String receivedString = (String) myChannel.Recv();
					
					System.out.println("Received: " + receivedString);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class SenderStage2 implements Runnable {
		public void run() {
			for(int i = 0; i < 100; i++)
			{
				String myString = (String) sevent.sync();
				System.out.println("Sent: " + myString);
			}
		}
	}
	
	class SenderStage3_1 implements Runnable {
		public void run() {
			SendEvent sepSendEvent = new SendEvent("Hello I am sending on my own channel", sepChannel1);
			System.out.println("Inside stage3");
			String myString = (String) sepSendEvent.sync();
			System.out.println("Test");
			System.out.println("Sent: " + myString);
		}
	}
	
	class SenderStage3_2 implements Runnable {
		public void run() {
			SendEvent sepSendEvent = new SendEvent("Hello I am sending on my own channel", sepChannel2);
			System.out.println("Inside stage3");
			String myString = (String) sepSendEvent.sync();
			System.out.println("Test");
			System.out.println("Sent: " + myString);
		}
	}
	
	class RecvSelect implements Runnable {
		public void run () {
			SelectionList myList = new SelectionList();
			RecvEvent revent1 = new RecvEvent(sepChannel1);
			RecvEvent revent2 = new RecvEvent(sepChannel2);
			
			myList.addEvent(revent1);
			myList.addEvent(revent2);
			
			CommEvent myString1 = (CommEvent) myList.select();
			CommEvent myString2 = (CommEvent) myList.select();
			
			System.out.println("Stage 3 Received: " + myString1.m_obj);
			System.out.println("Stage 3 Received: " +myString2.m_obj);

		}
	}

	class ReceiverStage2 implements Runnable {
		public void run() {
			for(int i = 0; i < 200; i++)
			{
				String myString = (String) revent.sync();
				
				System.out.println("Received: " + myString);;
			}
		}
	}
	
	
}

public class Test {
	static public void main(String[] args) throws InterruptedException
	{
		//This main function will just be used to test each stage...
	
		SyncComm project = new SyncComm();
		
		//STAGE 1 TESTING:
		/*
		SenderStage1 mySender1 = project.new SenderStage1();
		SenderStage1 mySender2 = project.new SenderStage1();
		
		ReceiverStage1 myReceiver = project.new ReceiverStage1();
		
		Thread S1 = new Thread(mySender1);
		Thread S2 = new Thread(mySender2);
		Thread R1 = new Thread(myReceiver);
		
		System.out.println("Starting Channel Test Stage 1");
		
		S1.start();
		S2.start();
		
		R1.start();
		
		S1.join();
		S2.join();
		R1.join();
		
		System.out.println("FINISHED STAGE 1 TESTING... Delaying before Stage 2...");
		Thread.sleep(3000);
		*/
		//END STAGE ONE TESTING....
		/*
		//STAGE 2 TESTING:
		SenderStage2 mySender3 = project.new SenderStage2();
		SenderStage2 mySender4 = project.new SenderStage2();
		
		ReceiverStage2 myReceiver2 = project.new ReceiverStage2();
		
		Thread S3 = new Thread(mySender3);
		Thread S4 = new Thread(mySender4);
		Thread R2 = new Thread(myReceiver2);
		
		System.out.println("Starting Channel Test Stage 2");
		
		S3.start();
		S4.start();
		
		R2.start();
		
		S3.join();
		S4.join();
		R2.join();
		
		System.out.println("FINISHED STAGE 2 TESTING... Delaying before Stage 3...");
		Thread.sleep(3000);
		
		//END STAGE 2 TESTING
		 */
		
		//STAGE 3 TESTING:
		
		System.out.println("Starting Channel Test Stage 3");
		
		SenderStage3_1 mySender5 = project.new SenderStage3_1();
		SenderStage3_2 mySender6 = project.new SenderStage3_2();
		
		RecvSelect myReceiver3 = project.new RecvSelect();
		
		Thread S5 = new Thread(mySender5);
		Thread S6 = new Thread(mySender6);
		Thread R3 = new Thread(myReceiver3);
		
		S5.start();
		S6.start();
		R3.start();
		
		S5.join();
		S6.join();
		R3.join();
		
		
	}
}
