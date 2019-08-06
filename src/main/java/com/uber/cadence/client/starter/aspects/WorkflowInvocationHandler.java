package com.uber.cadence.client.starter.aspects;

import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

public class WorkflowInvocationHandler implements InvocationHandler {

  private final HelloWorkflowOne target;

  public WorkflowInvocationHandler(HelloWorkflowOne target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getAnnotation(WorkflowMethod.class) != null) {

      HelloWorkflowOne newProxy = proxying( target, HelloWorkflowOne.class);



    }
    return method.invoke(target, args);
  }

  @SuppressWarnings("unchecked")
  public static HelloWorkflowOne proxying(HelloWorkflowOne target, Class<HelloWorkflowOne> iface) {
    return (HelloWorkflowOne) Proxy.newProxyInstance(
        iface.getClassLoader(),
        new Class<?>[]{iface},
        new WorkflowInvocationHandler(target));
  }
}
