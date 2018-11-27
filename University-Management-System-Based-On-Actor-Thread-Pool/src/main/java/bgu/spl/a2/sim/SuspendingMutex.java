package bgu.spl.a2.sim;
import java.util.LinkedList;
import java.util.Queue;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private boolean flag;
	private Queue<Promise<Computer>> promises;
	Computer myComputer;

	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.myComputer=computer;
		this.flag = true;
		this.promises = new LinkedList<Promise<Computer>>();
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * 
	 * @return a promise for the requested computer
	 */
	public synchronized Promise<Computer> down(){
		if(flag){ //the computer is free
			try{
				flag = false;
				Promise<Computer> promise = new Promise<Computer>();
				promise.resolve(myComputer);
				return promise;
			}
			finally{
				up();
			}
		}
		else{ 
			Promise<Computer> promise = new Promise<Computer>();
			promises.add(promise);
			return promise;
		}
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public synchronized void up(){
		for(int i=0; i<promises.size(); i++){
			promises.remove().resolve(myComputer);
		}
		flag=true;
	}
	public boolean getFlag(){
		return flag;
	}
}
