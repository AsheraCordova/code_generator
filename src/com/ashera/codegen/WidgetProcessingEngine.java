package com.ashera.codegen;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ashera.codegen.pojo.CopyAttribute;

public class WidgetProcessingEngine {

    // Functional interface to abstract your processWidget business logic
    @FunctionalInterface
    public interface WidgetProcessorAction {
        void execute(com.ashera.codegen.pojo.Widget widget) throws Exception;
    }

    // Special structural token used to safely unblock and close dormant threads
    private static final com.ashera.codegen.pojo.Widget POISON_PILL = new com.ashera.codegen.pojo.Widget();

    private final BlockingQueue<com.ashera.codegen.pojo.Widget> readyQueue = new LinkedBlockingQueue<>();
    private final Map<String, com.ashera.codegen.pojo.Widget> globalWidgetMap;
    private final List<com.ashera.codegen.pojo.Widget> waitingList = new CopyOnWriteArrayList<>();
    
    private final AtomicInteger totalDiscoveredWidgets = new AtomicInteger(0);
    private final CountDownLatch completionLatch = new CountDownLatch(1);
    private final int maxThreads;

    public WidgetProcessingEngine(java.util.Map<String, com.ashera.codegen.pojo.Widget> globalWidgetMap, int maxThreads) {
        this.globalWidgetMap = globalWidgetMap;
        this.maxThreads = maxThreads;
    }

    /**
     * Feeds unmarshalled widgets from a file into the processing pipeline.
     */
    public void submitWidgets(com.ashera.codegen.pojo.Widget[] widgets) throws InterruptedException {
        if (widgets == null || widgets.length == 0) return;
        
        totalDiscoveredWidgets.addAndGet(widgets.length);
        for (com.ashera.codegen.pojo.Widget w : widgets) {
            readyQueue.put(w);
        }
    }

    /**
     * Executes the lifecycle using a thread pool. Blocks until all submitted widgets are completed.
     */
    public void executePipeline(WidgetProcessorAction action) throws Exception {
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
        
        // Spin up worker threads
        for (int i = 0; i < maxThreads; i++) {
            threadPool.submit(() -> workerLoop(action));
        }

        // Wait until all registered widgets hit the processed pool
        completionLatch.await(30, java.util.concurrent.TimeUnit.SECONDS);
        
        if (waitingList.size() > 0) {
        	throw new RuntimeException(waitingList.size() + " " + waitingList.get(0).getName() + waitingList.get(0).getOs() + " " + globalWidgetMap.keySet() + " " + areDependenciesReady(waitingList.get(0)));
        }
        System.out.println(waitingList.size() + " " + totalDiscoveredWidgets.get());

        // Broadcast poison pills to shut down blocking workers safely
        for (int i = 0; i < maxThreads; i++) {
            readyQueue.put(POISON_PILL);
        }

        threadPool.shutdown();
    }

    private void workerLoop(WidgetProcessorAction action) {
        try {
            while (true) {
                com.ashera.codegen.pojo.Widget current = readyQueue.take();

                if (current == POISON_PILL) {
                    break;
                }
if (current.getName().equals("AutoCompleteTextView") && current.getOs().equals("android")) {
	System.out.println("aaa");
}
                if (areDependenciesReady(current)) {
                    processAndRelease(current, action);
                } else {
                    // Not ready yet (parent or source missing) -> Hold in the waiting bay
                    waitingList.add(current);
                }

                // Verify pipeline completion threshold
                // Checks how many components have keys present in your main tracking map
                int completedCount = 0;
                synchronized (globalWidgetMap) {
                	completedCount = globalWidgetMap.keySet().size() / 3;
                }

                if (completedCount >= totalDiscoveredWidgets.get() && totalDiscoveredWidgets.get() > 0) {
                    completionLatch.countDown();
                }
            }
        } catch (Exception e) {
            System.err.println("Critical failure in widget execution thread: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean areDependenciesReady(com.ashera.codegen.pojo.Widget widget) {
        // Change these getters to match your actual com.ashera.codegen.pojo.Widget property layout
        String parentId = widget.getParentWidget() == null ? null : widget.getParentWidget() + widget.getOs(); 
        com.ashera.codegen.pojo.CopyAttribute[] copyAttributes = widget.getCopyAttribute(); // Assumes a list method or adjust accordingly
        List<com.ashera.codegen.pojo.CustomAttribute> customAttributes = widget.getCustomAttribute(); // Assumes a list method or adjust accordingly

        synchronized (globalWidgetMap) {
            if (parentId != null && !globalWidgetMap.containsKey(parentId)) {
            	System.out.println("1");
                return false;
            }
            if (copyAttributes != null) {
                for (CopyAttribute copyAttribute : copyAttributes) {
                    String coptAttrId = copyAttribute.getWidget()+(copyAttribute.getOs()==null?widget.getOs():copyAttribute.getOs());
					if (!globalWidgetMap.containsKey(coptAttrId)) {
						System.out.println("2");
						return false;
					}
                }
            }
            
            if (customAttributes != null) {
                for (com.ashera.codegen.pojo.CustomAttribute customAttribute : customAttributes) {
                	if (customAttribute.getCopyDef() != null) {
                		if (!globalWidgetMap.containsKey(customAttribute.getCopyDef())) {
                			System.out.println("3");
                			return false;	
                		}
                	}
                }
            }
        }
        return true;
    }

    private void processAndRelease(com.ashera.codegen.pojo.Widget current, WidgetProcessorAction action) throws Exception {
        long t1 = System.currentTimeMillis();
        if (CodeGenFromHtml.LOG_TIME) {
        	System.out.println("[" + Thread.currentThread().getName() + "] " + current.getName() + current.getOs() + " -> start " + new java.util.Date());
        }

        // Execute original code generation business logic
        action.execute(current);

        if (CodeGenFromHtml.LOG_TIME) {
        	System.out.println("[" + Thread.currentThread().getName() + "] " + current.getName() + current.getOs() + " -> end " + new java.util.Date() + " " + (System.currentTimeMillis() - t1));
        }

        // Register the updated instance into your global registry map to unlock child nodes
        synchronized (globalWidgetMap) {
            globalWidgetMap.put(current.getName() + current.getOs(), current);
            globalWidgetMap.put("jsoncache/" + current.getOs() + current.getName()  + ".json", current); 
            globalWidgetMap.put(current.getClassname() + current.getOs(), current);
            
        }

        // Scan the waiting list to see if this completion releases pending components
        for (com.ashera.codegen.pojo.Widget waitingWidget : waitingList) {
            if (areDependenciesReady(waitingWidget)) {
                if (waitingList.remove(waitingWidget)) {
                    readyQueue.put(waitingWidget);
                }
            }
        }
    }
}
