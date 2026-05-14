from pathlib import Path

from langchain_core.prompts import PromptTemplate

from app.services.llm_service import get_llm


def generate_card(flower_name: str, relation: str, occasion: str, style: str = "浪漫", length: int = 50) -> dict:
    llm = get_llm()
    if llm is None:
        return {
            "generated_content": f"愿这束{flower_name}把温柔与祝福送到你身边，愿{occasion}的每一刻都明亮美好。",
            "model": "fallback-template",
            "tokens_used": 0,
        }

    template = Path("app/prompts/card_prompt.txt").read_text(encoding="utf-8")
    prompt = PromptTemplate.from_template(template)
    result = (prompt | llm).invoke(
        {
            "flower_name": flower_name,
            "relation": relation,
            "occasion": occasion,
            "style": style,
            "length": length,
        }
    )
    return {
        "generated_content": result.content,
        "model": "deepseek-chat",
        "tokens_used": 0,
    }

