package bgu.spl.a2;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

	LinkedList<Thread> poolThreads;

	ConcurrentHashMap<String, PrivateState> actorsStatesMap;//this field must be private, i changed to public for tests
	ConcurrentHashMap<String, ConcurrentLinkedDeque<Action<?>>> actorsQsMap;
	ConcurrentHashMap<String, AtomicBoolean> actorsQsMapLocks;
	VersionMonitor vm= new VersionMonitor();
	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */

	public ActorThreadPool(int nthreads) {
		poolThreads= new LinkedList<Thread>();
		actorsStatesMap = new ConcurrentHashMap<String, PrivateState>();
		actorsQsMap = new ConcurrentHashMap<String, ConcurrentLinkedDeque<Action<?>>>();
		actorsQsMapLocks = new ConcurrentHashMap<String, AtomicBoolean>();
		for(int i=0; i< nthreads; i++){

			Thread newThread= new Thread( ()-> {
				while(!Thread.currentThread().isInterrupted()){
					int currVersion=vm.getVersion();
					for(String actorId : actorsQsMap.keySet()){
						if(Thread.currentThread().isInterrupted()){break;}
						ConcurrentLinkedDeque<Action<?>> q=actorsQsMap.get(actorId);
						if(!q.isEmpty()){
							if(actorsQsMapLocks.get(actorId).compareAndSet(false, true)){
								if(!q.isEmpty()){
									
										Action<?> action= q.pop();
										action.handle(this, actorId , actorsStatesMap.get(actorId));
									}
									actorsQsMapLocks.get(actorId).set(false);
								}
							}	
					}
					if(currVersion==vm.getVersion()){
						vm.await(currVersion);
					}
				}				
			});
			poolThreads.push(newThread);
		}
	}


	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for(int i=0; i<poolThreads.size(); i++){
			poolThreads.get(i).start();
		}
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {

		actorsStatesMap.putIfAbsent(actorId, actorState);
		actorsQsMapLocks.putIfAbsent(actorId, new AtomicBoolean(false));
		ConcurrentLinkedDeque<Action<?>> newQforTheNewActor= new ConcurrentLinkedDeque<Action<?>>();
		if(action!=null){newQforTheNewActor.add(action);}
		if(actorsQsMap.putIfAbsent(actorId, newQforTheNewActor)!=null){
			ConcurrentLinkedDeque<Action<?>> targetQ=actorsQsMap.get(actorId);
			targetQ.add(action);
		}
		vm.inc();

	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		while(!poolThreads.isEmpty()){
			poolThreads.pop().interrupt();
		}
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return actorsStatesMap;
	}

	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return actorsStatesMap.get(actorId);
	}
}
