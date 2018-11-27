package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.callback;
public class registerWithPreferences extends Action<Boolean> {
	private List<String> preferences;
	private List<Integer> grades;
	AtomicInteger i= new AtomicInteger(0);
	LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();


	public registerWithPreferences(List<String> preferences, List<Integer> grades) {
		super.actionName="registerWithPreferences";
		this.preferences=preferences;
		this.grades=grades;
		this.promise= new Promise<Boolean>();
	}

	@Override
	protected void start() {

		if(preferences.size()==0){
			complete(false);
			return;
		}
		callThen();

	}
	private void callThen(){

		Integer grade;
		if(grades.get(i.get()) instanceof Integer){// in case the grade is given/not
			grade= new Integer((Integer)grades.get(i.get()));
		}
		else{ 
			grade= new Integer(-1);
		}
		
	   List<String> studGrades= new ArrayList<String>();
	    studGrades.addAll(((StudentPrivateState)actorState).getGrades().keySet());
		tryToRegisterCourse tryToRegisterCourse= new tryToRegisterCourse(actorId, grade, studGrades);
		sendMessage(tryToRegisterCourse, preferences.get(i.get()), new CoursePrivateState());
		actions.add(tryToRegisterCourse);

		then(actions,()->{
			if(actions.get(0).getResult().get()==true){
				((StudentPrivateState)actorState).addGrade(preferences.get(i.get()), grade);
				complete(true);
				return;
			}
			else{
				actions.clear();
				i.set(i.get()+1);
				if(i.get()>=preferences.size()){
					complete(false);
					return;
				}
				else{
					callThen();
				}

			}
		});
		
	}
	
}

