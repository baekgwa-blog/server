package baekgwa.blogserver.domain.ai.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * PackageName : baekgwa.blogserver.domain.ai.dto
 * FileName    : RetrievalResultDto
 * Author      : Baekgwa
 * Date        : 25. 11. 10.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 10.     Baekgwa               Initial creation
 */
@Getter
@Builder
public class RetrievalResultDto {
	private Double score; // 유사도 점수 (0.0 ~ 1.0)
	private String text; // 검색된 본문 내용 (청크)
	private Map<String, Object> metadata; // 메타데이터 (제목, 링크 등)

	public static RetrievalResultDto from(Double score, String text, Map<String, Object> metadata) {
		return RetrievalResultDto.builder()
			.score(score)
			.text(text)
			.metadata(metadata)
			.build();
	}
}