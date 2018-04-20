package com.huaan9527.spring.data.domain.ext;

public interface CreateByCompany<T> {

    public void setCompanyId(T company);

    public T getCompanyId();

}
