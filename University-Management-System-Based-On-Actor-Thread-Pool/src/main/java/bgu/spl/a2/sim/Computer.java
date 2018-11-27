package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;
import bgu.spl.a2.callback;
import bgu.spl.a2.Promise;

public class Computer {

	String computerType;
	long failSig;
	long successSig;
	SuspendingMutex mySuspendingMutex; //this has been added
	
	
	public Computer(String computerType) {
		this.computerType = computerType;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return 
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		boolean accept=true;
		for(int i=0; accept && i<courses.size(); i++){
			if(!coursesGrades.containsKey(courses.get(i))){
				accept=false;
			}
			else{
				if(coursesGrades.get(courses.get(i))<56){
					accept=false;
				}
			}
		}
		if(accept){
				return successSig;
		}
		else{
			return failSig;
		}
	}
	/*
	 * setter functions
	 */
	public void setFailSig(long value){
		this.failSig=value;
	}
	public void setSuccessSig(long value){
		this.successSig=value;
	}
	public long getFailSig(){
		return failSig;
	}
	public long getSuccessSig(){
		return successSig;
	}
	public String getType(){
		return this.computerType;
	}
	public SuspendingMutex getSuspendingMutex(){
		return mySuspendingMutex;
	}
	public void setSuspendingMutex(SuspendingMutex SuspendingMutex){
		this.mySuspendingMutex=SuspendingMutex;
	}
}