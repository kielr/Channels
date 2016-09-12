package SyncCom;

import java.util.concurrent.locks.Condition;

class Data {
	public Object o;
	public Condition c;
	
	//Constructor
	Data(Object o, Condition c) {
		this.o = o;
		this.c = c;
	}
}