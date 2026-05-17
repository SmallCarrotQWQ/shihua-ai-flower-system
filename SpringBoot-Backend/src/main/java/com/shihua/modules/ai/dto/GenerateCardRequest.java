package com.shihua.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;

public class GenerateCardRequest {
    private Long flowerId;
    @NotBlank
    private String flowerName;
    @NotBlank
    private String relation;
    @NotBlank
    private String occasion;
    private String style = "浪漫";
    private Integer length = 50;

    public Long getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(Long flowerId) {
        this.flowerId = flowerId;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
