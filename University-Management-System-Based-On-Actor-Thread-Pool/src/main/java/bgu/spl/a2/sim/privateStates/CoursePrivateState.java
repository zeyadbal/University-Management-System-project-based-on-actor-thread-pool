package bgu.spl.a2.sim.privateStates;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{// implements Serializable{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		availableSpots=0;
		registered=0;
		regStudents=new LinkedList<String>();
		prequisites=new LinkedList<String>();
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}
	public void setRegistered(Integer val){
		this.registered=val;
	}
	public void setRegStudents(List<String> val){
		this.regStudents=val;
	}
	public void setPrequisites(List<String> val){
		this.prequisites=val;
	}
	public void setAvailableSpots(Integer val){
		this.availableSpots=val;
	}
	public void increaseAvailableSports(){
		this.availableSpots++;
	}
	public void decreaseAvailableSports(){
		if(this.availableSpots>0){
			this.availableSpots--;
		}
	}
	public void increaseRegStudents(){
		this.registered++;
	}
	public void decreaseRegStudents(){
		if(this.registered>0){
			this.registered--;
		}
	}
	public boolean registerStudent(String newStudent){
		if(regStudents.contains(newStudent)){
			return false;
		}
		else{
			this.regStudents.add(newStudent);
			return true;
		}
	}
	public void unRegisterStudent(String toBeDeleted){
		this.regStudents.remove(toBeDeleted);
	}
	public void upDateAvailableSpots(Integer val){
		this.availableSpots+=val;
	}
}
