package bgu.spl.a2;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.sim.actions.unRegister;

import java.util.Collection;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
	protected String actionName;
	protected callback callback;
	protected Promise<R> promise;
	protected ActorThreadPool pool;
	protected String actorId;
	protected PrivateState actorState;
	private  AtomicInteger num;
	protected int tries=3;
	/**
	 * start handling the action - note that this method is protected, a thread
	 * cannot call it directly.
	 */
	protected abstract void start();


	/**
	 *
	 * start/continue handling the action
	 *
	 * this method should be called in order to start this action
	 * or continue its execution in the case where it has been already started.
	 *
	 * IMPORTANT: this method is package protected, i.e., only classes inside
	 * the same package can access it - you should *not* change it to
	 * public/private/protected
	 *
	 */
	/*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
		if(callback==null){
			if(tries==3 &&//the action has not started yet
					!actionName.equals("addCourseToAStudent")
					& !actionName.equals("closeThisCourse") 
					& !actionName.equals("creationAcception")
					& !actionName.equals("doesTheStudentHasThePrequisites")
					& !actionName.equals("getStudentGrades")
					& !actionName.equals("removeCourseFromSheet")
					& !actionName.equals("setSigForStudent")){
				actorState.addRecord(this.getActionName());
			}
		}
		this.pool=pool;
		this.actorId=actorId;
		this.actorState=actorState;

		if(callback==null){
			this.start();
		}
		else{//resolved!
			callback.call();
		}
	}


	/**
	 * add a callback to be executed once *all* the given actions results are
	 * resolved
	 * 
	 * Implementors note: make sure that the callback is running only once when
	 * all the given actions completed.
	 *
	 * @param actions
	 * @param callback the callback to execute once all the results are resolved
	 */
	protected final void then(Collection<? extends Action<?>> actions, callback callback) {

		this.callback= callback;

		if(actions.size()==0){
			pool.submit(this, actorId, actorState); 
			return;
		}
		num = new AtomicInteger(actions.size());

		for(Action<?> entry : actions) {
			entry.getPromise().subscribe( ()-> {
				num.decrementAndGet();
				if(num.get()==0){
					pool.submit(this, actorId, actorState); 
				}
			});
		}
	}

	/**
	 * resolve the internal result - should be called by the action derivative
	 * once it is done.
	 *
	 * @param result - the action calculated result
	 */
	protected final void complete(R result) {
		promise.resolve(result);
	}

	/**
	 * @return action's promise (result)
	 */
	public final Promise<R> getResult() {
		return promise;
	}

	/**
	 * send an action to an other actor
	 * 
	 * @param action
	 * 				the action
	 * @param actorId
	 * 				actor's id
	 * @param actorState
	 * 				actor's private state (actor's information)
	 *    
	 * @return promise that will hold the result of the sent action
	 */
	public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
		pool.submit(action, actorId, actorState) ;
		return promise;
	}

	/**
	 * set action's name
	 * @param actionName
	 */
	public void setActionName(String actionName){
		this.actionName= actionName;
	}

	/**
	 * @return action's name
	 */
	public String getActionName(){
		return actionName;
	}
	public callback getCallback(){
		return callback;
	}
	public Promise<?> getPromise(){
		return promise;
	}
}