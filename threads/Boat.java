//idcnritnmhkh`ovdhytrihbi`ofmhkh`bido{itcnvdo
//PART OF THE NACHOS. DON'T CHANGE CODE OF THIS LINE
package nachos.threads;
import java.util.LinkedList;

import org.omg.CORBA.INITIALIZE;

import jdk.internal.dynalink.beans.StaticClass;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;

    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();

	System.out.println("\n ***Testing Boats with only 2 children***");
	begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
//  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
	bg = b;

	// Instantiate global variables here

	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.
	Person.init();
	LinkedList<KThread> personThreads=new LinkedList<KThread>();
	LinkedList<Person> people = new LinkedList<Person>();
	for(int i=0; i<children; i++){
		Child child=new Child("Child "+(i+1));
		people.add(child);
		KThread t=new KThread(child);
		t.setName("Child "+(i+1));
		personThreads.add(t);
		t.fork();
	}
	for(int i=0; i<adults; i++){
		Adult adult=new Adult("Adult "+(i+1));
		people.add(adult);
		KThread t=new KThread(adult);
		t.setName("Adult "+(i+1));
		personThreads.add(t);
		t.fork();
	}
	
	for (Person person : people) {
		person.countSemaphore.P();
	}
	//System.out.println("All counted.");
	Person.startLock.acquire();
	Person.isStart=true;
	Person.startCondition.wakeAll();
	Person.startLock.release();
	for(KThread personThread : personThreads)
		personThread.join();
	
	boolean correct=true;
	for(Person person: people)
		if(person.pos==0)
		{
			correct=false;
		}
	if(!correct)
	{
		System.out.println("main thread: Error!");
	}else {
		System.out.println("main thread: Correct!");
	}
	/*Runnable r = new Runnable() {
	    public void run() {
                //SampleItinerary();
	    	
            }
        };
        KThread t = new KThread(r);
        t.setName("Sample Boat Thread");
        t.fork();*/

    }

    static void AdultItinerary()
    {
	bg.initializeAdult(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE.

	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
    }

    static void ChildItinerary()
    {
	bg.initializeChild(); //Required for autograder interface. Must be the first thing called.
	//DO NOT PUT ANYTHING ABOVE THIS LINE.
    }

    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }
    
    private abstract static class Person implements Runnable{  	
    	public Person(String name){
    		this.name=name;
    		pos=0;
    		countSemaphore=new Semaphore(0);
    	}
    	protected void printDebugInfo(){
    		if(debugFlag)
    		{
    			System.out.println(name+": "+"Oahu Child:"+OahuChildNum+" Adult:"+OahuAdultNum);    			
    		}
    	}
    	private boolean debugFlag=false;
    	private String name;
    	//postion of the person
    	//pos=0 means the person is at Oahu; pos=1 means the person is at Molokai 
    	public int pos;
    	
    	//semaphore used to help count people at the beginning
    	public Semaphore countSemaphore;
    	
    	
    	public static void init()
    	{
    		OahuChildNum=0;
    		OahuAdultNum=0;
        	MolokaiChildNum=0;
        	MolokaiAdultNum=0;
        	numLock=new Lock();
        	boatLock=new Lock();
        	BoatConAtOahu=new Condition(boatLock);
        	BoatConAtMolokai=new Condition(boatLock);
        	boatPos=0;
        	isBoatEmpty=true;
        	startLock=new Lock();
        	startCondition=new Condition(startLock);
        	isStart=false;
        	isOver=false;
    	}
    	/*
    	 * shared variable
    	 */
    	//people number on two islands
    	static int OahuChildNum;
    	static int OahuAdultNum;
    	static int MolokaiChildNum;
    	static int MolokaiAdultNum;
    	//lock used to sync all the thread about the people number
    	static Lock numLock;    	
    	
    	/*
    	 * variables about the boat
    	 */
    	//lock used to give permission for people to get on boat
    	static Lock boatLock;
    	//condition variable used for people at Oahu to wait on
    	static Condition BoatConAtOahu;
    	//condition variable used for people at Molokai to wait on
    	static Condition BoatConAtMolokai;
    	//boat position
    	static int boatPos;
    	//whether the boat is empty
    	static boolean isBoatEmpty;
    	
    	/*
    	 * variables that indicate the status of the program
    	 */
    	static Lock startLock;
    	static Condition startCondition;
    	static boolean isStart;
    	static boolean isOver;
    }
    
    private static class Child extends Person{
    	public Child(String name) {
    		super(name);
			bg.initializeChild();
		}
    	
    	
    	private void rowToMolokai()
    	{
    		bg.ChildRowToMolokai();
			pos=1;
			OahuChildNum=OahuChildNum-1;
			MolokaiChildNum=MolokaiChildNum+1;
			printDebugInfo();
    	}
    	private void rideToMolokai()
    	{
    		bg.ChildRideToMolokai();
			pos=1;
			OahuChildNum=OahuChildNum-1;
			MolokaiChildNum=MolokaiChildNum+1;
			printDebugInfo();
    	}
    	private void rowToOahu()
    	{
    		bg.ChildRowToOahu();
			pos=0;
			OahuChildNum=OahuChildNum+1;
			MolokaiChildNum=MolokaiChildNum-1;
			printDebugInfo();
    	}
    	
    	public void run() {
    		//get the total number of people
    		numLock.acquire();
    		OahuChildNum=OahuChildNum+1;
    		numLock.release();
    		countSemaphore.V();
    		
    		//wait until the main thread to tell start
    		startLock.acquire();
    		while(!isStart)
    		{
    			startCondition.sleep();
    		}
    		startLock.release();
    		
    		//begin to take action
    		while(!isOver)
    		{
    			//child at Oahu
    			if(pos==0)
    			{
    				boatLock.acquire();
    				//wait for the boat to come to Oahu
    				while(boatPos!=pos)
    				{
    					BoatConAtOahu.sleep();;
    				}
    				if(isOver)
    				{
    					boatLock.release();
    					continue;
    				}
    				//If the boat already has a rower, just ride to Molokai as a passenger
    				if(!isBoatEmpty)
    				{
    					if(OahuChildNum==1&&OahuAdultNum==0)
    						isOver=true;
    					rideToMolokai();    					
						isBoatEmpty=true;
						boatPos=1;
						BoatConAtMolokai.wakeAll();
    				}else if(OahuChildNum>=2)
    				{
    					//If more than two children at Oahu, 2 children go to Oahu
    					isBoatEmpty=false;    						
						rowToMolokai();
    				}else if(OahuAdultNum==0)
    				{
    					//If only one children and no adult at Oahu, just go to Oahu by himeself
    					rowToMolokai();    					
    					boatPos=1;
    					isOver=true;
    				}else {
    					BoatConAtOahu.sleep();;
					}
    				boatLock.release();
    			}else{
    				//the child is at Molokai and there are still people at Oahu
    				//the child row back to Oahu
    				boatLock.acquire();
    				while(boatPos!=pos)
    				{
    					BoatConAtMolokai.sleep();;
    				}
    				if(isOver)
    				{
    					boatLock.release();
    					continue;
    				}
    					
    				rowToOahu();
    				boatPos=0;
    				BoatConAtOahu.wakeAll();
    				boatLock.release();
    			}
    		}
    	}
    }
    
    private static class Adult extends Person{
    	public Adult(String name){
    		super(name);
    		bg.initializeAdult();
    	}
    	private void rowToMolokai()
    	{
    		bg.AdultRowToMolokai();
    		pos=1;
    		OahuAdultNum--;
    		MolokaiAdultNum++;
    		printDebugInfo();
    	}
    	public void run()
    	{
    		numLock.acquire();
    		OahuAdultNum=OahuAdultNum+1;
    		numLock.release();
    		countSemaphore.V();
    		
    		//wait until the main thread to tell start
    		startLock.acquire();
    		while(!isStart)
    		{
    			startCondition.sleep();
    		}
    		startLock.release();
    		
    		//If the adult is at Oahu
    		//Only when less than 2 children is at Oahu that he row to Molokai
    		while(pos==0)
    		{
    			boatLock.acquire();
    			while(boatPos!=pos)
    			{
    				BoatConAtOahu.sleep();
    			}
    			if(isBoatEmpty&&OahuChildNum<2)
    			{
    				if(OahuAdultNum==1&&OahuChildNum==0)isOver=true;
    				rowToMolokai();
    				boatPos=1;
    				BoatConAtMolokai.wakeAll();
    			}else{
    				BoatConAtOahu.sleep();
    			}
    			boatLock.release();
    		}
    	}
    }
}
