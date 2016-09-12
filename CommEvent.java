package SyncCom;

public abstract class CommEvent {
	//All communication events will have channels. Let's make it part of 
	protected Channel m_chan;
	protected Object m_obj;
	protected boolean matched;
	
	
	
	//We have a return type of boolean to let sync() know if we successfully poll()'d
	protected abstract boolean poll();
	
	protected abstract void enqueue();
	
	//Public method sync
	public Object sync()
	{
		if(poll() == true)
		{
			//Return our sent object
			return this.m_obj;
		}
		else
		{
			enqueue();
			return this.m_obj;
		}
	}
}
