package SyncCom;

import java.util.concurrent.locks.Condition;

public class SendEvent extends CommEvent {
	//Constructor for our SendEvent
	SendEvent(Object o, Channel c)
	{
		m_obj = o;
		m_chan = c;
		this.matched = false;
	}
	@Override
	public boolean poll() {
		boolean flag = false; //This is what we'll use to know if we correctly poll()'d
		try{
			//We need to lock
			GlobalLock.relock.lock();
			// We are a SendEvent, if the receive queue is non-empty, transfer the data
			if(!m_chan.rQueue.isEmpty())
			{
				//We are transfering the data...
				//Take the first thing off of the queue
				Data recData = m_chan.rQueue.remove();
				//Put our data in the object
				recData.o = this.m_obj;
				
				//We matched
				this.matched = true;
				
				//Signal
				recData.c.signal();
				
				GlobalLock.c.signal();
				
				
				//We've successfully poll()'d
				flag = true;
				
			}
			//We don't need an else, we failed
		}
		finally{
			GlobalLock.relock.unlock();
		}
		
		return flag;
	}

	@Override
	//If we get here then we could not poll, add our object to the sendQueue
	public void enqueue() {
		//Lock
		GlobalLock.relock.lock();
		//Create a new condition, put it in a local variable because we need to tell it to wait
		Condition newCondition = GlobalLock.relock.newCondition();
		//Add to the send queue
		m_chan.sQueue.add(new Data(this.m_obj, newCondition));
		//Tell to wait
		try {
			newCondition.await();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			GlobalLock.c.signal();
			GlobalLock.relock.unlock();
		}
		
	}
	
}
