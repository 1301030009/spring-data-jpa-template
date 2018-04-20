package com.huaan9527.spring.data;

import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;

public class TJpaQueryMethod extends JpaQueryMethod {
    private Method method;

    /**
     * Creates a {@link JpaQueryMethod}.
     *
     * @param method    must not be {@literal null}
     * @param metadata  must not be {@literal null}
     * @param factory
     * @param extractor must not be {@literal null}
     */
    public TJpaQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory, QueryExtractor extractor) {
        super(method, metadata, factory, extractor);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
