package bgu.spl.a2.sim;

import java.util.HashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {
	private HashMap<Computer, SuspendingMutex> Computers;
	
	public Warehouse() {
		Computers= new HashMap<Computer, SuspendingMutex>();
	}
	
	public void addComputer(String type, long failSig, long successSig){
		Computer newComputer= new Computer(type);
		newComputer.setFailSig(failSig);
		newComputer.setSuccessSig(successSig);
		SuspendingMutex SuspendingMutex= new SuspendingMutex(newComputer);
		newComputer.setSuspendingMutex(SuspendingMutex);
		Computers.put(newComputer, SuspendingMutex);
	}
	
	public Computer getComputer(String type){
		for( Computer entry : Computers.keySet()){
			if(entry.getType().equals(type)){
				return entry;
			}
		}
		return null;
	}
	
}
