package com.seroperson.mediator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

public class MediatorProxy implements InvocationHandler {

	private final Mediator mediator;
	
	public MediatorProxy(Mediator mediator) { 
		this.mediator = mediator;
	}
	
	public static ApplicationListener newInstance(Mediator mediator, Class<?>... classes) { 
	    return (ApplicationListener) Proxy.newProxyInstance(mediator.getClass().getClassLoader(), classes, new MediatorProxy(mediator));
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> declaringClass = method.getDeclaringClass();
        for (Class<?> interf : Game.class.getInterfaces()) {
            if (declaringClass.isAssignableFrom(interf)) {
                try {
                    return method.invoke(mediator, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }
        return null;
	}

}
