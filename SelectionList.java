package SyncCom;

import java.util.ArrayList;
import java.util.List;


public class SelectionList {
	private List<CommEvent> selectList = new ArrayList<CommEvent>();
	
	//Our add event function will just add to the select list, but will first make sure there are no duplicate channels	
	void addEvent(CommEvent newEvent)
	{
		//For each commevent in the selection list
		for(CommEvent commEvent : selectList)
		{
			//There's no duplication of channels
			//Make sure there is no duplication of channels before adding
			if(newEvent.m_chan == commEvent.m_chan)
			{
				System.out.println("ERROR: Cannot add duplicating channels to the selection list!");
				return;
			}
		}
		//Otherwise, add the commevent to the list
		System.out.println("Adding to SelectList...");
		selectList.add(newEvent);
		return;
	}
	
	Object select()
	{
		//Within a list, there's no duplication of channels(assume they are all on different channels)
		//Each thread has their own list
		CommEvent returnComm = null;
		
		//Go through the list, call poll on the events
		for(CommEvent commEvent : selectList)
		{
			if(commEvent.poll() == true)
			{
				return commEvent;
			}
		}
		//If we get here, none of the poll()'s succeeded
		//If none of the polls succeeds, select() calls enqueue for each element of the SelectionList
		for(CommEvent commEvent : selectList)
		{
			commEvent.enqueue();
		}
		
		//After calling enqueue on all elements, select() waits
		try {
			GlobalLock.c.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//When awoken it figures out which event was matched
		for(CommEvent commEvent : selectList)
		{
			//Store the data from the queues as an array
			if(commEvent.matched == true)
			{
				returnComm = commEvent;
			}
		}
		
		//and removes all other events from their respective channel queues
		for(CommEvent commEvent : selectList)
		{
			//Store the data from the queues as an array
			Data[] dataArrayR = (Data[]) commEvent.m_chan.rQueue.toArray();
			Data[] dataArrayS = (Data[]) commEvent.m_chan.sQueue.toArray();
			
			//Greedily check each element of the array against our event data
			for(int i = 0; i < dataArrayR.length; i++)
			{
				//If we find a match..
				if(commEvent.m_obj == dataArrayR[i].o)
				{
					//Remove that object from the queue.
					commEvent.m_chan.rQueue.remove(dataArrayR[i]);
				}
			}
			//Greedily check each element of the array against our event data
			for(int i = 0; i < dataArrayS.length; i++)
			{
				//If we find a match..
				if(commEvent.m_obj == dataArrayS[i].o)
				{
					//Remove that object from the queue.
					commEvent.m_chan.sQueue.remove(dataArrayS[i]);
				}
			}
		}
		
		return returnComm;
	}
}
