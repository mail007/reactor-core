/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.scheduler;

import reactor.core.Disposable;

/**
 * Executes tasks on the caller's thread immediately.
 * <p>
 * Use the ImmediateScheduler.instance() to get a shared, stateless instance of this scheduler.
 */
final class ImmediateScheduler implements Scheduler {

    private static final ImmediateScheduler INSTANCE = new ImmediateScheduler();
    
    public static Scheduler instance() {
        return INSTANCE;
    }
    
    private ImmediateScheduler() {
        
    }
    
    static final Disposable EMPTY = () -> { };
    
    @Override
    public Disposable schedule(Runnable task) {
        task.run();
        return EMPTY;
    }

    @Override
    public void shutdown() {
        dispose();
    }

    @Override
    public void dispose() {
        //NO-OP
    }

    @Override
    public Worker createWorker() {
        return new ImmediateSchedulerWorker();
    }
    
    static final class ImmediateSchedulerWorker implements Scheduler.Worker {
        
        volatile boolean shutdown;

        @Override
        public Disposable schedule(Runnable task) {
            if (shutdown) {
                return REJECTED;
            }
            task.run();
            return EMPTY;
        }

        @Override
        public void shutdown() {
            dispose();
        }

        @Override
        public void dispose() {
            shutdown = true;
        }

        @Override
        public boolean isDisposed() {
            return shutdown;
        }
    }

}
