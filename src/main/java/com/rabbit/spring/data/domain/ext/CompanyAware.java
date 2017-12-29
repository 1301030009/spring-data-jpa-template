package com.rabbit.spring.data.domain.ext;

import java.io.Serializable;

public interface CompanyAware<T> {

    T getCurrentCompany();

}
