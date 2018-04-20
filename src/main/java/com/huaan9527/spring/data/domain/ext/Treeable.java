/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.huaan9527.spring.data.domain.ext;

import java.io.Serializable;
import java.util.List;

/**
 * <p>实体实现该接口表示想要实现树结构
 */
public interface Treeable<ID extends Serializable, M extends Treeable> {

    public static final Long DEFAULT_ROOT_PARENT_ID = 0L;
    public static final String DEFAULT_ROOT_ICON_CLS = "";
    public static final String DEFAULT_BRANCH_ICON_CLS = "";
    public static final String DEFAULT_LEAF_ICON_CLS = "";
    public static final String DEFAULT_SEPARATOR = "/";

    public void setName(String name);

    public String getName();

    /**
     * 显示的图标 大小为16×16
     *
     * @return
     */
    public String getIcon();

    public void setIcon(String icon);

    /**
     * 是否展开该节点
     *
     * @return
     */
    public boolean isExpand();

    public void setExpand(boolean expand);

    /**
     * 设置Status
     *
     * @return
     */
    public Status getStatus();

    public void setStatus(Status status);

    /**
     * 父路径
     *
     * @return
     */
    public ID getParentId();

    public void setParentId(ID parentId);

    /**
     * 所有父路径 如1,2,3,
     *
     * @return
     */
    public String getParentIds();

    public void setParentIds(String parentIds);

    /**
     * 获取 parentIds 之间的分隔符
     *
     * @return
     */
    public String getSeparator();

    /**
     * 把自己构造出新的父节点路径
     *
     * @return
     */
    public String makeSelfAsNewParentIds();

    /**
     * 权重 用于排序 越小越排在前边
     *
     * @return
     */
    public Integer getWeight();

    public void setWeight(Integer weight);

    /**
     * 是否是根节点
     *
     * @return
     */
    public boolean isRoot();


    /**
     * 是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf();

    /**
     * 是否有孩子节点
     *
     * @return
     */
    public boolean isHasChildren();

    /**
     * 查询出所有的子类
     *
     * @return
     */
    public List<M> getChildren();

    public void addChild(M child);

    /**
     * 根节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getRootDefaultIcon();

    /**
     * 树枝节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getBranchDefaultIcon();

    /**
     * 树叶节点默认图标 如果没有默认 空即可  大小为16×16
     */
    public String getLeafDefaultIcon();


}
