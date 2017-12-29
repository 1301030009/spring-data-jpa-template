package com.rabbit.spring.data.domain.ext;

import java.io.Serializable;

public interface CreateByCompany<T> {

    public void setCompanyId(T company);

    public T getCompanyId();

}
