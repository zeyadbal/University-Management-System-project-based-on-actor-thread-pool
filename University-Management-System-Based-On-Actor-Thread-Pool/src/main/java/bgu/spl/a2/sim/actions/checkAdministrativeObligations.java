package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class checkAdministrativeObligations extends Action<Boolean> {
	List<String> students;
	List<String> conditions;
	String computerType;
	Warehouse Warehouse;


	public checkAdministrativeObligations(List<String> students, List<String> conditions, String computerType, Warehouse Warehouse) {
		super.actionName="checkAdministrativeObligations";
		this.students=students;
		this.conditions=conditions;
		this.computerType=computerType;
		this.Warehouse=Warehouse;
		this.promise= new Promise<Boolean>();
	}


	@Override
	protected void start() {
		
		LinkedList<Action<Map<String, Integer>>> actions= new LinkedList<Action<Map<String, Integer>>>();

		for(int i=0; i< students.size(); i++){
			getStudentGrades getStudentGrades= new getStudentGrades();
			actions.add(getStudentGrades);
			sendMessage(getStudentGrades, students.get(i), new StudentPrivateState());
		}

		then(actions, ()->{
			for(int i=0; i<actions.size(); i++){
				int j=i; // effective final
				Warehouse.getComputer(computerType).getSuspendingMutex().down().subscribe( ()->{
					long signature= Warehouse.getComputer(computerType).checkAndSign(conditions, actions.get(j).getResult().get());
					Warehouse.getComputer(computerType).getSuspendingMutex().up();
					sendMessage(new setSigForStudent(signature), students.get(j),  new StudentPrivateState());
				});
			}
			complete(true);
		});
		
	}
}
