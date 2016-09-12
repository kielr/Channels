package SyncCom;

import java.util.concurrent.locks.Condition;

class RecvEvent extends CommEvent {
	RecvEvent(Channel c)
	{
		m_chan = c;
		this.matched = false;
	}

	@Override
	public boolean poll() {
		boolean flag = false;
		try{
			//Need to lock
			GlobalLock.relock.lock();
			//We are receiveEvent, if the sendQueue is non empty, then we need to transfer the data
			if(!m_chan.sQueue.isEmpty())
			{
				//Pop from the sendQueue
				Data sendData = m_chan.sQueue.remove();
				//Save the object...
				this.m_obj = sendData.o;
				//Tell the data to unlock
				sendData.c.signal();
				
				//We matched
				this.matched = true;
				
				//We poll()'d correctly, set the flag
				flag = true;
				
			}
			
		} finally {
			GlobalLock.relock.unlock();
		}
		
		return flag;
	}

	@Override
	public void enqueue() {		
		//Lock
		GlobalLock.relock.lock();
		//Create a new condition, put it in a local variable because we need to tell it to wait
		Condition newCondition = GlobalLock.relock.newCondition();
		//Add to the receive queue
		m_chan.rQueue.add(new Data(this.m_obj, newCondition));
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
