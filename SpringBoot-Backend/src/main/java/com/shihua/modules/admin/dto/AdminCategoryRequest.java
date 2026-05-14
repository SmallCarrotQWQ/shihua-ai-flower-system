package com.shihua.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private Long parentId = 0L;
    private Integer sortOrder = 0;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

