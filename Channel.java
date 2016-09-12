package SyncCom;
import java.util.LinkedList;
import java.util.Queue;

public class Channel {
	//Set up Reentrant lock as recommended by Carl
		//public final ReentrantLock relock = new ReentrantLock();
	    //Set up a private field that lets us keep track of our conditions

	    //Set up our sendQueue and our receiveQueue.
	    final Queue<Data> sQueue = new LinkedList<Data>();
	    final Queue<Data> rQueue = new LinkedList<Data>();
	    
	    //Now we can start the algorithm as described in the assignment page.
	    
	    //First our Send() method
	    
	    Object Send(Object o) throws InterruptedException {
	    	//Lock
	    	GlobalLock.relock.lock();
	    	
	    	try{
	    		//if our receiveQueue is non-empty
	        	if(!rQueue.isEmpty())
	        	{
	        		//Get our first object
	        		Data recData = rQueue.remove();
	        		
	        		//Send new data to the object
	        		//recData is WAITING for the object
	        		recData.o = o;
	        		
	        		//Wake the object up using signal() because it has the new object
	        		recData.c.signal();
	        		
	        	} else { //Other wise receiveQueue is empty
	        		//We need to make a new entry in the sendQueue
	        		//Create the data object
	        		Data sendData = new Data(o, GlobalLock.relock.newCondition());
	        		//Now add that object to our sendQueue
	        		sQueue.add(sendData);
	        		
	        		//Now tell that object to wait for a signal from receiver
	        		sendData.c.await();
	        		
	        	}
	    	} finally {
	    		//Done, unlock
	    		GlobalLock.relock.unlock();
	    	}
	    	
	    	return o;
	    }
	    
	    //Second, our Recv() method
	    
	    Object Recv() throws InterruptedException {
	    	//Starting to receive, lock
	    	GlobalLock.relock.lock();
	    	//Declare a reference to the object that will get received and returned later
	    	Object o = null;
	    	
	    	try {
	    		//If the sendQueue is non-empty...
	    		if(!sQueue.isEmpty())
	    		{
	    			//Take value from first send in the sendQueue
	    			Data sentData = sQueue.remove();
	    			
	    			//Move data from sender to receiver
	    			o = sentData.o;
	    			
	    			//Awake the sender
	    			sentData.c.signal();
	    			
	    		} else { //Otherwise if the sendQueue IS empty...
	    			//Make new data
	    			Data newData = new Data(null, GlobalLock.relock.newCondition());
	    			
	    			//Add that data to the receiveQueue
	    			rQueue.add(newData);
	    			
	    			//Await data.
	    			newData.c.await();
	    			
	    			//After the await, we should have data.
	    			o = newData.o;
	    			
	    			//Done
	    		}
	    		
	    	} finally {
	    		//Done, unlock
	    		GlobalLock.relock.unlock();
	    	}
	    	
	    	return o;
	    }
}
