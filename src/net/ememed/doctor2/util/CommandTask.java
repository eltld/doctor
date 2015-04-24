package net.ememed.doctor2.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import net.ememed.doctor2.R;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

/**
 * 后台执行任务的类
 * @author chen
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class CommandTask<Params, Progress, Result> {

	 private static final String LOG_TAG = "UserTask"; 
	
	 private static final int CORE_POOL_SIZE = 5; 
     private static final int MAXIMUM_POOL_SIZE = 10; 
     private static final int KEEP_ALIVE = 3000; 
     
     /**
      * BlockingQueue,如果BlockQueue是空的,
      * 从BlockingQueue取东西的操作将会被阻断进入等待状态,
      * 直到BlockingQueue进了东西才会被唤醒.同样,如果BlockingQueue是满的,
      * 任何试图往里存东西的操作也会被阻断进入等待状态,
      * 直到BlockingQueue里有空间才会被唤醒继续操作.
      * 使用BlockingQueue的关键技术点如下:
    	1.BlockingQueue定义的常用方法如下:
	        1)add(anObject):把anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则抛出异常
	        2)offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.
	        3)put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
	        4)poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
	        5)take():取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到Blocking有新的对象被加入为止
    	2.BlockingQueue有四个具体的实现类,根据不同需求,选择不同的实现类
	        1)ArrayBlockingQueue:规定大小的BlockingQueue,其构造函数必须带一个int参数来指明其大小.其所含的对象是以FIFO(先入先出)顺序排序的.
	        2)LinkedBlockingQueue:大小不定的BlockingQueue,若其构造函数带一个规定大小的参数,生成的BlockingQueue有大小限制,若不带大小参数,
	        	所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定.其所含的对象是以FIFO(先入先出)顺序排序的
	        3)PriorityBlockingQueue:类似于LinkedBlockQueue,但其所含对象的排序不是FIFO,而是依据对象的自然排序顺序或者是构造函数的Comparator决定的顺序.
	        4)SynchronousQueue:特殊的BlockingQueue,对其的操作必须是放和取交替完成的.
    	3.LinkedBlockingQueue和ArrayBlockingQueue比较起来,它们背后所用的数据结构不一样,导致LinkedBlockingQueue的数据吞吐量要大于ArrayBlockingQueue,
    		但在线程数量很大时其性能的可预见性低于ArrayBlockingQueue. 
      */
     private static final BlockingQueue<Runnable> sWorkQueue = 
         new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE); 
     
     
     private static final ThreadFactory sThreadFactory = new ThreadFactory() { 
    	 /**
    	  * AtomicInteger，一个提供原子操作的Integer的类。
    	  * 在Java语言中，++i和i++操作并不是线程安全的，在使用的时候，
    	  * 不可避免的会用到synchronized关键字。
    	  * 而AtomicInteger则通过一种线程安全的加减操作接口。
    	  * 
    	  * 获取当前的值 public final int get()
    	  * 取当前的值，并设置新的值  public final int getAndSet(int newValue)
    	  * 获取当前的值，并自增 public final int getAndIncrement()
    	  * 获取当前的值，并自减 public final int getAndDecrement()
    	  * 获取当前的值，并加上预期的值 public final int getAndAdd(int delta)
    	  */
         private final AtomicInteger mCount = new AtomicInteger(1); 
  
         public Thread newThread(Runnable r) { 
             return new Thread(r, "UserTask #" + mCount.getAndIncrement()); 
         } 
     }; 
     
     /**
      * 线程池类为 java.util.concurrent.ThreadPoolExecutor，常用构造方法为：
      *	ThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
      *		long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
      *
      * corePoolSize： 线程池维护线程的最少数量
      *	maximumPoolSize：线程池维护线程的最大数量
      *	keepAliveTime： 线程池维护线程所允许的空闲时间
      *	unit： 线程池维护线程所允许的空闲时间的单位
      *	workQueue： 线程池所使用的缓冲队列
      *	handler： 线程池对拒绝任务的处理策略
      * 一个任务通过 execute(Runnable)方法被添加到线程池，任务就是一个 Runnable类型的对象，
      * 任务的执行方法就是 Runnable类型对象的run()方法。
      *	当一个任务通过execute(Runnable)方法欲添加到线程池时：
	  *	如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
	  *	如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
	  *	如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
	  *	如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。
	  *	也就是：处理任务的优先级为：
	  *	核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
	  *	当线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
	  *	unit可选的参数为java.util.concurrent.TimeUnit中的几个静态属性：
	  *	NANOSECONDS、MICROSECONDS、MILLISECONDS、SECONDS。
	  *	workQueue我常用的是：java.util.concurrent.ArrayBlockingQueue
	  *	handler有四个选择：
	  *	ThreadPoolExecutor.AbortPolicy()
	  *	抛出java.util.concurrent.RejectedExecutionException异常
	  *	ThreadPoolExecutor.CallerRunsPolicy()
	  *	重试添加当前的任务，他会自动重复调用execute()方法
	  *	ThreadPoolExecutor.DiscardOldestPolicy()
	  *	抛弃旧的任务
	  *	ThreadPoolExecutor.DiscardPolicy()
	  *	抛弃当前的任务
      *
      */
     private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, 
             MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory); 
     
     private static final int MESSAGE_POST_RESULT = 0x1; 
     private static final int MESSAGE_POST_PROGRESS = 0x2; 
     private static final int MESSAGE_POST_CANCEL = 0x3; 
  
     private static final InternalHandler sHandler = new InternalHandler(); 
  
     private final WorkerRunnable<Params, Result> mWorker; 
     
     /**
      * FutureTask类是Future 的一个实现，并实现了Runnable，所以可通过Excutor(线程池) 来执行。
      * 也可传递给Thread对象执行。如果在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，
      * 可以把这些作业交给Future对象在后台完成，当主线程将来需要时，
      * 就可以通过Future对象获得后台作业的计算结果或者执行状态。
      */
     private final FutureTask<Result> mFuture; 
  
     private volatile Status mStatus = Status.PENDING; 
  
    
     public enum Status { 
         /** 
          * 等待运行的线程. 
          */ 
         PENDING, 
         /** 
          * 运行中的线程. 
          */ 
         RUNNING, 
         /** 
          * 运行完毕的线程. 
          */ 
         FINISHED, 
     } 
     
     /** 
      * 创建一个新的线程. 被界面主线程调用. 
      */ 
     public CommandTask() { 
         mWorker = new WorkerRunnable<Params, Result>() { 
             public Result call() throws Exception { 
                 Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND); 
                 return doInBackground(mParams); 
             } 
         }; 
         mFuture = new FutureTask<Result>(mWorker) { 
             @SuppressWarnings("unchecked")
			@Override 
             protected void done() { 
                 Message message; 
                 Result result = null; 
  
                 try { 
                     result = get(); 
                 } catch (InterruptedException e) { 
                     android.util.Log.w(LOG_TAG, e); 
                 } catch (ExecutionException e) { 
                     throw new RuntimeException("An error occured while executing doInBackground()", 
                             e.getCause()); 
                 } catch (CancellationException e) { 
                     message = sHandler.obtainMessage(MESSAGE_POST_CANCEL, 
                             new UserTaskResult<Result>(CommandTask.this, (Result[]) null)); 
                     message.sendToTarget(); 
//                     System.out.println("cancel----------------------->");         
                     return; 
                 } catch (Throwable t) { 
                     throw new RuntimeException("An error occured while executing " 
                             + "doInBackground()", t); 
                 } 
  
                 message = sHandler.obtainMessage(MESSAGE_POST_RESULT,  new UserTaskResult<Result>(CommandTask.this, result)); 
                 message.sendToTarget(); 
             } 
         }; 
     } 
     
     /**
      * 返回当前线程的状态
      * @return
      */
     public final Status getStatus() { 
         return mStatus; 
     } 
     
     /**
      * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。在执行过程中可以调用.
      * 后台进程执行的具体计算在这里实现，doInBackground(Params...)是AsyncTask的关键，此方法必须重载。
      * 在这个方法内可以使用 publishProgress(Progress...)改变当前的进度值。
      * @param params
      * @return
      */
     public abstract Result doInBackground(Params... params); 
     
     /**
      * 执行预处理，它运行于UI线程，可以为后台任务做一些准备工作，比如绘制一个进度条控件。
      */
     public void onPreExecute() { } 
     
     
     /**
      * 此方法在主线程执行，任务执行的结果作为此方法的参数返回。
      * 运行于UI线程，可以对后台任务的结果做出处理，结果就是doInBackground(Params...)的返回值。
      * 此方法也要经常重载，如果Result为null表明后台任务没有完成(被取消或者出现异常)。
      * @param result
      */
     public void onPostExecute(Result result) { } 
     
     /** 
      * 运行于UI线程。如果在doInBackground(Params...)中使用了publishProgress(Progress...)，
      * 就会触发这个方法。在这里可以对进度条控件根据进度值做出具体的响应。
      */ 
     public void onProgressUpdate(Progress... values) { } 
     
     /** 
      * 在界面主线程并在cancel(boolean) 之后调用. 
      * @see #cancel(boolean) 
      * @see #isCancelled() 
      */ 
     public void onCancelled() { } 
  
     /** 
      * 如果当前任务在正常完成之前被取消返回 true 
      * @see #cancel(boolean) 
      */ 
     public final boolean isCancelled() { 
         return mFuture.isCancelled(); 
     } 
     /** 
      * 如果未开始运行则会被取消运行，如果已经运行则会跑出mayInterruptIfRunning错误并终止运行
      * @param mayInterruptIfRunning 如果为true  如果线程正在执行则会被中断，否则，会等待任务完成。 
      * @return false  如果线程无法取消，很可能是因为已经完成 
      *         true   则相反
      * 
      * @see #isCancelled() 
      * @see #onCancelled() 
      */ 
     public final boolean cancel(boolean mayInterruptIfRunning) { 
         return mFuture.cancel(mayInterruptIfRunning); 
     } 
     /** 
      * 返回结果
      */ 
     public final Result get() throws InterruptedException, ExecutionException { 
         return mFuture.get(); 
     } 
  
     /** 
      * 在制定时间内等待结果，超出时间则取消任务
      */ 
     public final Result get(long timeout, TimeUnit unit) throws InterruptedException, 
             ExecutionException, TimeoutException { 
         return mFuture.get(timeout, unit); 
     } 
     
     /** 
      * 自动调用的回调参数
      * @param params task的需要的参数. 
      * @return 返回一个task的实例. 
      * 
      */ 
     public final CommandTask<Params, Progress, Result> execute(Params... params) { 
         if (mStatus != Status.PENDING) { 
             switch (mStatus) { 
                 case RUNNING: 
                     throw new IllegalStateException("Cannot execute task:" 
                             + " the task is already running."); 
                 case FINISHED: 
                     throw new IllegalStateException("Cannot execute task:" 
                             + " the task has already been executed " 
                             + "(a task can be executed only once)"); 
             } 
         } 
         mStatus = Status.RUNNING; 
         onPreExecute(); 
         mWorker.mParams = params;
         sExecutor.execute(mFuture); 
         return this; 
     } 
     
     /** 
      * 此方法在doInBackground(Object[])里面当后台计算在运行时在UI线程调用
      * 每次调用都会触发通过onProgressUpdate（）方法在UI线程显示出来
      * @param values UI线程需要的参数
      * 
      * @see #onProgressUpdate (Object[]) 
      * @see #doInBackground(Object[]) 
      */ 
     protected final void publishProgress(Progress... values) { 
         sHandler.obtainMessage(MESSAGE_POST_PROGRESS, 
                 new UserTaskResult<Progress>(this, values)).sendToTarget(); 
     } 
  
     private void finish(Result result) { 
         onPostExecute(result); 
         mStatus = Status.FINISHED; 
     } 
     
	private static class InternalHandler extends Handler {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void handleMessage(Message msg) {
			UserTaskResult result = (UserTaskResult) msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				// 只有一个返回
				result.mTask.finish(result.mData[0]);
				break;
			case MESSAGE_POST_PROGRESS:
				result.mTask.onProgressUpdate(result.mData);
				break;
			case MESSAGE_POST_CANCEL:
				result.mTask.cancel(true);
				break;
			}
		}
	}
  
	private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
		Params[] mParams;
	}
  
	private static class UserTaskResult<Data> {
		@SuppressWarnings("rawtypes")
		final CommandTask mTask;
		final Data[] mData;

		@SuppressWarnings("rawtypes")
		UserTaskResult(CommandTask task, Data... data) {
			mTask = task;
			mData = data;
		}
	}
}
