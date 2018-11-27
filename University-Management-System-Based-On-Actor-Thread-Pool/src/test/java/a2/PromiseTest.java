package a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Assert;

public class PromiseTest {
	Promise<Integer> p;
	@Before
	public void setUp() throws Exception {
		Promise<Integer> p = new Promise<>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		/*
		 * here, we are going to call get function in two cases:
		 * when the promise is resolved or not.
		 */
		try{
			try{
				p.get();
				Assert.fail();
			}
			/*
			 * if an IllegalStateException has been thrown when trying go get the promise while isn't resolved;
			 * in this case: the function works as expected.
			 */
			catch(IllegalStateException ex){
				assertEquals(false,p.isResolved());
			}
			try{
				p.resolve(7);
				p.get();
				Assert.fail();
			}
			/*
			 * if an IllegalStateException has been thrown although the promise is resolved;
			 * in this case: the function failed. since when the promise is resolved the get method should
			 * work as expected.
			 */
			catch(IllegalStateException ex){
				Assert.fail();
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}

	@Test
	public void testIsResolved() {
		/*
		 * going to resolve the created promise and check if the function has the right boolean answer
		 * about the promise status (resolved/not resolved)
		 */
		try{
			try{
				p.resolve(5);
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(p.isResolved(),true);
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}

	@Test
	public void testResolve() {
		try{
			p.resolve(5);
			try{
				p.resolve(6);
				Assert.fail();
			}
			catch(IllegalStateException ex){
				int x= p.get();
				assertEquals(x,5);
			}
			catch(Exception ex){
				Assert.fail();
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}


	@Test
	public void testSubscribe() {
		/*
		 * we are going to create 5 callback. the mission of each one is to increase the value of the 
		 * effectively final variable x by 1.
		 * the goal is, by resolving the promise; the callback's will be called and the value of x is going to be 5.
		 */
		try{
			AtomicInteger x= new AtomicInteger(0);

			callback clbk1= ()-> {x.set(x.get()+1);};
			callback clbk2= ()-> {x.set(x.get()+1);};
			callback clbk3= ()-> {x.set(x.get()+1);};
			callback clbk4= ()-> {x.set(x.get()+1);};
			callback clbk5= ()-> {x.set(x.get()+1);};

			p.subscribe(clbk1);
			p.subscribe(clbk2);
			p.subscribe(clbk3);
			p.subscribe(clbk4);
			p.subscribe(clbk5);

			try{
				/*
				 * if a callback has been called before the object is solved
				 */
				if(x.get()!=0){
					Assert.fail();
				}
				p.resolve(7);
				/*
				 * if after the object has been solved, it didn't called all the callback's
				 */
				if(x.get()!=5){
					Assert.fail();
				}
				/*
				 *  the object is already resolved, so the next callback should be called immediately
				 */
				callback clbk6= ()-> {x.set(x.get()+1);}; 
				p.subscribe(clbk6);
				if(x.get()!=6){
					Assert.fail();
				}
			}
			catch(Exception ex){
				Assert.fail();
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}
}
