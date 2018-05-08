package com.het.httpserver.core.observer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OHttpbservable {
    private static Set<IHttpObserver> obs = new HashSet<>();

    public synchronized void registerObserver(IHttpObserver o) {
        if (o != null) {
            if (!obs.contains(o)) {
                obs.add(o);
            }
        }
    }

    public synchronized void unregisterObserver(IHttpObserver o) {
        if (obs.contains(o)) {
            obs.remove(o);
        }
    }

    public synchronized void clear() {
        obs.clear();
    }

    public synchronized void notifyObservers(IHttpObserver self, Object obj) {
        if (obj == null)
            return;
        Iterator<IHttpObserver> it = obs.iterator();
        while (it.hasNext()) {
            IHttpObserver mgr = it.next();
            if (mgr.equals(self))
                continue;
            mgr.onHttpSession(obj);
        }
    }
}
