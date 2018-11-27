/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {


	public static ActorThreadPool pool;
	public static MyStaticClass input;

	public static void main(String [] args){

		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(args[0]);
		} catch (FileNotFoundException e) {}
		br = new BufferedReader(fr);


		Gson gson1= new Gson();

		MyStaticClass fromJson= gson1.fromJson(br, MyStaticClass.class);
		input=fromJson;

		attachActorThreadPool(new ActorThreadPool(input.getThreads()));

		start();

		try{
			FileOutputStream fous= new FileOutputStream("result.ser");
			ObjectOutputStream oos= new ObjectOutputStream(fous);
			oos.writeObject(end());
			fous.close();
			oos.close();        
		}
		catch(IOException ex){}

	}

	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		pool= myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String,PrivateState> end(){
		HashMap<String, PrivateState> copy = new HashMap<String, PrivateState>(pool.getActors());
		return copy;
	}
	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start(){

		Warehouse warehouse= new Warehouse();

		for(Computer entry : input.getComputers()){
			long SigFail= Long.valueOf(entry.getSigFail()).longValue();
			long SigSuccess= Long.valueOf(entry.getSigSuccess()).longValue();
			warehouse.addComputer(entry.getType(), SigFail, SigSuccess);
		}

		pool.start();

		int numOfActionsInPhase1= input.getPhase1().size();
		CountDownLatch l1=new CountDownLatch(numOfActionsInPhase1);

		String Department;
		String Course;
		Integer Space;
		Integer Number;
		String StudentName;
		List<Integer> Grades;
		List<String> Prerequisites;
		List<String> Preferences;
		List<String> Students;
		List<String> Conditions;
		String Computer;

		//////////////////////////////////PHASE1/////////////////////////////////////////

		for(Phase1 entry : input.getPhase1()){
			String actionName= entry.getAction();
			switch(actionName){

			case "Open Course" :
				Department= entry.getDepartment();
				Course= entry.getCourse();
				Space= Integer.parseInt(entry.getSpace());
				Prerequisites= entry.getPrerequisites();
				openANewCourse openANewCourse= new openANewCourse(Course,Space ,Prerequisites);
				pool.submit(openANewCourse, Department, new DepartmentPrivateState());
				openANewCourse.getResult().subscribe( ()-> {
					l1.countDown();
				});
				break;

			case "Add Student" :
				StudentName= entry.getStudent();
				Department= entry.getDepartment();
				addStudent addStudent=new addStudent(StudentName);
				pool.submit(addStudent, Department, new DepartmentPrivateState());
				addStudent.getResult().subscribe( ()-> {
					l1.countDown();
				});
				break;

			case "Add Spaces" :
				Course= entry.getCourse();
				Number= Integer.parseInt(entry.getNumber());
				openningNewPlacesInACourse openningNewPlacesInACourse
				= new openningNewPlacesInACourse(Number);
				pool.submit(openningNewPlacesInACourse, Course, new CoursePrivateState());
				openningNewPlacesInACourse.getResult().subscribe( ()-> {
					l1.countDown();
				});
				break;
			}
		}

		try {
			l1.await();
		} catch (InterruptedException e1) {}


		//////////////////////////////////PHASE2/////////////////////////////////////////


		int numOfActionsInPhase2= input.getPhase2().size();
		CountDownLatch l2=new CountDownLatch(numOfActionsInPhase2);

		for(Phase2 entry : input.getPhase2()){
			String actionName= entry.getAction();
			switch(actionName){

			case "Open Course" :
				Department= entry.getDepartment();
				Course= entry.getCourse();
				Space= Integer.parseInt(entry.getSpace());
				Prerequisites= entry.getPrerequisites();
				openANewCourse openANewCourse= new openANewCourse(Course,Space ,Prerequisites);
				pool.submit(openANewCourse, Department, new DepartmentPrivateState());
				openANewCourse.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Add Student" :
				StudentName= entry.getStudent();
				Department= entry.getDepartment();
				addStudent addStudent=new addStudent(StudentName);
				pool.submit(addStudent, Department, new DepartmentPrivateState());
				addStudent.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Participate In Course" :
				StudentName= entry.getStudent();
				Course= entry.getCourse();
				Grades= entry.getGrade();
				participatingInCourse participatingInCourse=new participatingInCourse(StudentName, Grades.get(0));
				pool.submit(participatingInCourse, Course, new CoursePrivateState());
				participatingInCourse.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Register With Preferences" :
				StudentName= entry.getStudent();
				Preferences= entry.getPreferences();
				Grades= entry.getGrade();
				registerWithPreferences registerStd1WithPreferences=new registerWithPreferences(Preferences,Grades);
				pool.submit(registerStd1WithPreferences, StudentName, new StudentPrivateState());
				registerStd1WithPreferences.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Unregister" :
				StudentName= entry.getStudent();
				Course= entry.getCourse();
				unRegister unRegister=new unRegister(StudentName);	
				pool.submit(unRegister, Course, new CoursePrivateState());
				unRegister.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Close Course" :
				Department= entry.getDepartment();
				Course= entry.getCourse();
				closeACourse closeACourse=new closeACourse(Course);	
				pool.submit(closeACourse, Department, new DepartmentPrivateState());
				closeACourse.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Administrative Check" :
				Department= entry.getDepartment();
				Students= entry.getStudents();
				Computer= entry.getComputer();
				Conditions= entry.getConditions();
				checkAdministrativeObligations checkAdministrativeObligations
				=new checkAdministrativeObligations(
						Students,
						Conditions,
						Computer,
						warehouse
						);
				pool.submit(checkAdministrativeObligations,Department, new DepartmentPrivateState());	
				checkAdministrativeObligations.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;

			case "Add Spaces" :
				Course= entry.getCourse();
				Number= Integer.parseInt(entry.getNumber());
				openningNewPlacesInACourse openningNewPlacesInACourse
				= new openningNewPlacesInACourse(Number);
				pool.submit(openningNewPlacesInACourse, Course, new CoursePrivateState());
				openningNewPlacesInACourse.getResult().subscribe( ()-> {
					l2.countDown();
				});
				break;
			}
		}


		try {
			l2.await();
		} catch (InterruptedException e1) {}


		//////////////////////////////////PHASE3/////////////////////////////////////////

		int numOfActionsInPhase3= input.getPhase3().size();
		CountDownLatch l3=new CountDownLatch(numOfActionsInPhase3);

		for(Phase3 entry : input.getPhase3()){
			String actionName= entry.getAction();
			switch(actionName){


			case "Open Course" :
				Department= entry.getDepartment();
				Course= entry.getCourse();
				Space= Integer.parseInt(entry.getSpace());
				Prerequisites= entry.getPrerequisites();
				openANewCourse openANewCourse= new openANewCourse(Course,Space ,Prerequisites);
				pool.submit(openANewCourse, Department, new DepartmentPrivateState());
				openANewCourse.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Add Student" :
				StudentName= entry.getStudent();
				Department= entry.getDepartment();
				addStudent addStudent=new addStudent(StudentName);
				pool.submit(addStudent, Department, new DepartmentPrivateState());
				addStudent.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Participate In Course" :
				StudentName= entry.getStudent();
				Course= entry.getCourse();
				Grades= entry.getGrade();
				participatingInCourse participatingInCourse=new participatingInCourse(StudentName, Grades.get(0));
				pool.submit(participatingInCourse, Course, new CoursePrivateState());
				participatingInCourse.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Register With Preferences" :
				StudentName= entry.getStudent();
				Preferences= entry.getPreferences();
				Grades= entry.getGrade();
				registerWithPreferences registerStd1WithPreferences=new registerWithPreferences(Preferences,Grades);
				pool.submit(registerStd1WithPreferences, StudentName, new StudentPrivateState());
				registerStd1WithPreferences.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Unregister" :
				StudentName= entry.getStudent();
				Course= entry.getCourse();
				unRegister unRegister=new unRegister(StudentName);	
				pool.submit(unRegister, Course, new CoursePrivateState());
				unRegister.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Close Course" :
				Department= entry.getDepartment();
				Course= entry.getCourse();
				closeACourse closeACourse=new closeACourse(Course);	
				pool.submit(closeACourse, Department, new DepartmentPrivateState());
				closeACourse.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Administrative Check" :
				Department= entry.getDepartment();
				Students= entry.getStudents();
				Computer= entry.getComputer();
				Conditions= entry.getConditions();
				checkAdministrativeObligations checkAdministrativeObligations
				=new checkAdministrativeObligations(
						Students,
						Conditions,
						Computer,
						warehouse
						);
				pool.submit(checkAdministrativeObligations,Department, new DepartmentPrivateState());	
				checkAdministrativeObligations.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			case "Add Spaces" :
				Course= entry.getCourse();
				Number= Integer.parseInt(entry.getNumber());
				openningNewPlacesInACourse openningNewPlacesInACourse
				= new openningNewPlacesInACourse(Number);
				pool.submit(openningNewPlacesInACourse, Course, new CoursePrivateState());
				openningNewPlacesInACourse.getResult().subscribe( ()-> {
					l3.countDown();
				});
				break;

			}
		}

		try {
			l3.await();
		} catch (InterruptedException e1) {}


		try {
			pool.shutdown();
		} catch (InterruptedException e) {}
	}





	public static class MyStaticClass {

		private MyStaticClass(){}

		@SerializedName("threads")
		@Expose
		private Integer threads;
		@SerializedName("Computers")
		@Expose
		private List<Computer> computers = null;
		@SerializedName("Phase 1")
		@Expose
		private List<Phase1> phase1 = null;
		@SerializedName("Phase 2")
		@Expose
		private List<Phase2> phase2 = null;
		@SerializedName("Phase 3")
		@Expose
		private List<Phase3> phase3 = null;

		public Integer getThreads() {
			return threads;
		}

		public void setThreads(Integer threads) {
			this.threads = threads;
		}

		public List<Computer> getComputers() {
			return computers;
		}

		public void setComputers(List<Computer> computers) {
			this.computers = computers;
		}

		public List<Phase1> getPhase1() {
			return phase1;
		}

		public void setPhase1(List<Phase1> phase1) {
			this.phase1 = phase1;
		}

		public List<Phase2> getPhase2() {
			return phase2;
		}

		public void setPhase2(List<Phase2> phase2) {
			this.phase2 = phase2;
		}

		public List<Phase3> getPhase3() {
			return phase3;
		}

		public void setPhase3(List<Phase3> phase3) {
			this.phase3 = phase3;
		}
	}
	public class Computer {

		private Computer(){}


		@SerializedName("Type")
		@Expose
		private String type;
		@SerializedName("Sig Success")
		@Expose
		private String sigSuccess;
		@SerializedName("Sig Fail")
		@Expose
		private String sigFail;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getSigSuccess() {
			return sigSuccess;
		}

		public void setSigSuccess(String sigSuccess) {
			this.sigSuccess = sigSuccess;
		}

		public String getSigFail() {
			return sigFail;
		}

		public void setSigFail(String sigFail) {
			this.sigFail = sigFail;
		}

	}
	public static class Phase1 {

		private Phase1(){}

		@SerializedName("Action")
		@Expose
		private String action;
		@SerializedName("Department")
		@Expose
		private String department;
		@SerializedName("Course")
		@Expose
		private String course;
		@SerializedName("Space")
		@Expose
		private String space;
		@SerializedName("Prerequisites")
		@Expose
		private List<String> prerequisites = null;
		@SerializedName("Student")
		@Expose
		private String student;
		@SerializedName("Number")
		@Expose
		private String number;

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getCourse() {
			return course;
		}

		public void setCourse(String course) {
			this.course = course;
		}

		public String getSpace() {
			return space;
		}

		public void setSpace(String space) {
			this.space = space;
		}

		public List<String> getPrerequisites() {
			return prerequisites;
		}

		public void setPrerequisites(List<String> prerequisites) {
			this.prerequisites = prerequisites;
		}

		public String getStudent() {
			return student;
		}

		public void setStudent(String student) {
			this.student = student;
		}
		public String getNumber() {
			return number;
		}

	}
	private static class Phase2 {

		private Phase2(){}

		@SerializedName("Action")
		@Expose
		private String action;
		@SerializedName("Department")
		@Expose
		private String department;
		@SerializedName("Student")
		@Expose
		private String student;
		@SerializedName("Course")
		@Expose
		private String course;
		@SerializedName("Grade")
		@Expose
		private List<String> grade = null;
		@SerializedName("Preferences")
		@Expose
		private List<String> preferences = null;
		@SerializedName("Students")
		@Expose
		private List<String> students = null;
		@SerializedName("Computer")
		@Expose
		private String computer;
		@SerializedName("Conditions")
		@Expose
		private List<String> conditions = null;
		@SerializedName("Space")
		@Expose
		private String space;
		@SerializedName("Prerequisites")
		@Expose
		private List<String> prerequisites = null;
		@SerializedName("Number")
		@Expose
		private String number;

		public String getAction() {
			return action;
		}
		public String getDepartment() {
			return department;
		}
		public String getStudent() {
			return student;
		}
		public String getCourse() {
			return course;
		}

		public List<Integer> getGrade() {
			List<Integer> IntegerGrade= new LinkedList<Integer>();
			for(int i=0; i<grade.size(); i++){
				if(grade.get(i).equals("-")){
					IntegerGrade.add(new Integer(-1));
				}
				else{
					IntegerGrade.add(Integer.parseInt(grade.get(i)));
				}
			}
			return IntegerGrade;
		}
		public List<String> getPreferences() {
			return preferences;
		}
		public List<String> getStudents() {
			return students;
		}
		public String getComputer() {
			return computer;
		}
		public List<String> getConditions() {
			return conditions;
		}
		public String getSpace() {
			return space;
		}
		public List<String> getPrerequisites() {
			return prerequisites;
		}
		public String getNumber() {
			return number;
		}
	}
	private static class Phase3 {

		private Phase3(){}

		@SerializedName("Action")
		@Expose
		private String action;
		@SerializedName("Student")
		@Expose
		private String student;
		@SerializedName("Course")
		@Expose
		private String course;
		@SerializedName("Grade")
		@Expose
		private List<String> grade = null;
		@SerializedName("Department")
		@Expose
		private String department;
		@SerializedName("Students")
		@Expose
		private List<String> students = null;
		@SerializedName("Computer")
		@Expose
		private String computer;
		@SerializedName("Conditions")
		@Expose
		private List<String> conditions = null;
		@SerializedName("Space")
		@Expose
		private String space;
		@SerializedName("Prerequisites")
		@Expose
		private List<String> prerequisites = null;
		@SerializedName("Preferences")
		@Expose
		private List<String> preferences = null;
		@SerializedName("Number")
		@Expose
		private String number;

		public String getAction() {
			return action;
		}
		public String getStudent() {
			return student;
		}
		public String getCourse() {
			return course;
		}
		public List<Integer> getGrade() {
			List<Integer> IntegerGrade= new LinkedList<Integer>();
			for(int i=0; i<grade.size(); i++){
				if(grade.get(i).equals("-")){
					IntegerGrade.add(new Integer(-1));
				}
				else{
					IntegerGrade.add(Integer.parseInt(grade.get(i)));
				}
			}
			return IntegerGrade;
		}
		public String getDepartment() {
			return department;
		}
		public List<String> getStudents() {
			return students;
		}
		public String getComputer() {
			return computer;
		}
		public List<String> getConditions() {
			return conditions;
		}
		public String getSpace() {
			return space;
		}
		public List<String> getPrerequisites() {
			return prerequisites;
		}
		public List<String> getPreferences() {
			return preferences;
		}
		public String getNumber() {
			return number;
		}
	}
}