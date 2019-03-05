package io.aesy.dropwizard.aop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.Rank;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class ImperativeMethodInterceptionService implements InterceptionService {
    private final LoadingCache<Method, List<MethodInterceptor>> interceptorCache = CacheBuilder.newBuilder()
        .build(
            new CacheLoader<Method, List<MethodInterceptor>>() {
                @Override
                public List<MethodInterceptor> load(Method method) {
                    return StreamSupport.stream(methodInterceptors.spliterator(), true)
                        .filter(x -> x.appliesTo(method))
                        .sorted(interceptorSorter)
                        .map(FilteredMethodInterceptor::getInterceptor)
                        .collect(Collectors.toList());
                }
        });

    @Inject
    private IterableProvider<FilteredMethodInterceptor> methodInterceptors;

    private final Comparator<FilteredMethodInterceptor> interceptorSorter = (x, y) -> {
        Optional<Integer> xRank = Optional.ofNullable(
            x.getClass().getAnnotation(Rank.class)).map(Rank::value);
        Optional<Integer> yRank = Optional.ofNullable(
            y.getClass().getAnnotation(Rank.class)).map(Rank::value);

        if (xRank.isPresent() && yRank.isPresent()) {
            return xRank.get() - yRank.get();
        } else if (xRank.isPresent()) {
            return -1;
        } else if (yRank.isPresent()) {
            return 1;
        } else {
            return 0;
        }
    };

    @Override
    public Filter getDescriptorFilter() {
        return x -> !x.getAdvertisedContracts()
                      .contains(FilteredMethodInterceptor.class.getName());
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        try {
            return interceptorCache.get(method);
        } catch (ExecutionException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}
