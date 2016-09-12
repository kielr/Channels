package SyncCom;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//Recommended in the homework assignment to use global locks
class GlobalLock
{
	public static ReentrantLock relock = new ReentrantLock();
	public static Condition c = relock.newCondition();
}