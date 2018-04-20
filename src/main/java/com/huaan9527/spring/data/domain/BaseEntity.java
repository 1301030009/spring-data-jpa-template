package com.huaan9527.spring.data.domain;

import com.huaan9527.spring.data.domain.ext.CreateByCompany;
import com.huaan9527.spring.data.idgenerate.SnowflakeGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<PK extends Serializable> implements Persistable<PK>, CreateByCompany<PK> {

    @Id
    @GenericGenerator(name = "snowflake", strategy = SnowflakeGenerator.TYPE)
    @GeneratedValue(generator = "snowflake")
    private PK id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createTime = new Date();

    @Version
    @Column(name = "version")
    protected long version;

    @LastModifiedDate
    protected Date lastModifiedDate;

    @CreatedBy
    protected Long createdBy;

    @LastModifiedBy
    protected Long lastModifiedBy;

    protected PK companyId;

    public PK getId() {
        return id;
    }

    public void setId(final PK id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public PK getCompanyId() {
        return companyId;
    }

    public void setCompanyId(PK company) {
        this.companyId = company;
    }

    @Transient
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ClassUtils.getUserClass(obj))) {
            return false;
        }

        Persistable<?> that = (Persistable<?>) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}
