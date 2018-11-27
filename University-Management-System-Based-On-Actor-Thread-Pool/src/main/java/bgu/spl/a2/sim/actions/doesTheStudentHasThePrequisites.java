package bgu.spl.a2.sim.actions;



import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class doesTheStudentHasThePrequisites extends Action<Boolean>{
	private List<String> Prequisites;


	public doesTheStudentHasThePrequisites(List<String> Prequisites){
		super.actionName="doesTheStudentHasThePrequisites";
		this.Prequisites=Prequisites;
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start(){

			boolean accept=((StudentPrivateState)actorState).getGrades().size()>=Prequisites.size();
			for(int i=0;accept && i<Prequisites.size(); i++){
				if(!((StudentPrivateState)actorState).getGrades().containsKey(Prequisites.get(i))){
					accept=false;
					break;
				}
			}
			if(accept){
				complete(true);
			}
			else{
				complete(false);
			}
	}

}

