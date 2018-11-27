package bgu.spl.a2.sim.privateStates;

import java.io.Serializable;
import java.util.HashMap;

import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{// implements Serializable{

	private HashMap<String, Integer> grades;
	private long signature;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		this.grades= new HashMap<String, Integer>();
		this.signature=0;
	}

	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}
	public void addGrade(String courseName, Integer grade){
		grades.put(courseName, grade);
	}
	public void setSignature(long signature){
		this.signature=signature;
	}
}
