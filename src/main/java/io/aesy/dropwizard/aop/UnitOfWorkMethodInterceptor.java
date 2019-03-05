package io.aesy.dropwizard.aop;

import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAspect;
import org.aopalliance.intercept.MethodInterceptor;
import org.hibernate.SessionFactory;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Singleton
@ContractsProvided(FilteredMethodInterceptor.class)
public class UnitOfWorkMethodInterceptor implements FilteredMethodInterceptor {
    private final UnitOfWorkAspect unitOfWorkAspect;

    @Inject
    public UnitOfWorkMethodInterceptor(
        SessionFactory sessionFactory
    ) {
        Map<String, SessionFactory> sessionFactoryMap = new HashMap<>();
        sessionFactoryMap.put(HibernateBundle.DEFAULT_NAME, sessionFactory);

        this.unitOfWorkAspect = new UnitOfWorkAspect(sessionFactoryMap);
    }

    @Override
    public boolean appliesTo(Method method) {
        if (method.isAnnotationPresent(Path.class)) {
            return false;
        }

        if (method.getDeclaringClass().isAnnotationPresent(Path.class)) {
            return false;
        }

        return method.isAnnotationPresent(UnitOfWork.class);
    }

    @Override
    public MethodInterceptor getInterceptor() {
        return invocation -> {
            UnitOfWork unitOfWork = invocation.getMethod().getAnnotation(UnitOfWork.class);

            try {
                unitOfWorkAspect.beforeStart(unitOfWork);
                Object result = invocation.proceed();
                unitOfWorkAspect.afterEnd();

                return result;
            } catch (Throwable throwable) {
                unitOfWorkAspect.onError();

                throw throwable;
            } finally {
                unitOfWorkAspect.onFinish();
            }
        };
    }
}
