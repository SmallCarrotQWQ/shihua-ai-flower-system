package com.shihua.modules.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.modules.ai.dto.GenerateCardRequest;
import com.shihua.modules.ai.entity.AiCard;
import com.shihua.modules.ai.mapper.AiCardMapper;
import com.shihua.modules.ai.vo.AiCardVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AiCardService {
    private final AiServiceClient aiServiceClient;
    private final AiCardMapper aiCardMapper;

    public AiCardService(AiServiceClient aiServiceClient, AiCardMapper aiCardMapper) {
        this.aiServiceClient = aiServiceClient;
        this.aiCardMapper = aiCardMapper;
    }

    @Transactional
    public AiCardVO generate(Long userId, GenerateCardRequest request) {
        Map<String, Object> payload = Map.of(
            "flower_name", request.getFlowerName(),
            "relation", request.getRelation(),
            "occasion", request.getOccasion(),
            "style", request.getStyle() == null ? "浪漫" : request.getStyle(),
            "length", request.getLength() == null ? 50 : request.getLength()
        );
        Object content = "";
        var response = aiServiceClient.generateCard(payload);
        if (response != null && response.data() instanceof Map<?, ?> data) {
            Object generated = data.get("generated_content");
            content = generated == null ? "" : generated;
        }
        if (String.valueOf(content).isBlank()) {
            content = "愿这束" + request.getFlowerName() + "把温柔与祝福送到你身边。";
        }
        AiCard card = new AiCard();
        card.setUserId(userId);
        card.setFlowerId(request.getFlowerId());
        card.setRelation(request.getRelation());
        card.setOccasion(request.getOccasion());
        card.setPrompt(payload.toString());
        card.setGeneratedContent(String.valueOf(content));
        card.setIsUsed(0);
        aiCardMapper.insert(card);
        return toVO(card);
    }

    public List<AiCardVO> list(Long userId) {
        return aiCardMapper.selectList(new LambdaQueryWrapper<AiCard>()
                .eq(AiCard::getUserId, userId)
                .orderByDesc(AiCard::getCreateTime))
            .stream()
            .map(this::toVO)
            .toList();
    }

    private AiCardVO toVO(AiCard card) {
        return new AiCardVO(card.getCardId(), card.getFlowerId(), card.getRelation(), card.getOccasion(), card.getGeneratedContent(), card.getCreateTime());
    }
}
